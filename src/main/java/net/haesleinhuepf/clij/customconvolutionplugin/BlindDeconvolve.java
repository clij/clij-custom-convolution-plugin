package net.haesleinhuepf.clij.customconvolutionplugin;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.NewImage;
import ij.gui.Roi;
import net.haesleinhuepf.clij.CLIJ;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.macro.AbstractCLIJPlugin;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clij.macro.CLIJOpenCLProcessor;
import net.haesleinhuepf.clij.macro.documentation.OffersDocumentation;
import org.scijava.plugin.Plugin;

/**
 * BlindDeconvolve
 *
 * G. R. Ayers and J. C. Dainty, Iterative blind deconvolution method and its applications. Optics Letters, (1988); 13:547-549.
 * http://www.deconvolve.net/bialith/Research/BAR_IBD.htm
 *
 * Author: @haesleinhuepf
 * January 2019
 */
@Plugin(type = CLIJMacroPlugin.class, name = "CLIJ_blindDeconvolve")
public class BlindDeconvolve extends AbstractCLIJPlugin implements CLIJMacroPlugin, CLIJOpenCLProcessor, OffersDocumentation {

    @Override
    public String getParameterHelpText() {
        return "Image source, Image psf, Image destination, Number iterations, Number mseThreshold";
    }

    @Override
    public boolean executeCL() {
        ClearCLBuffer source = (ClearCLBuffer)args[0];
        ClearCLBuffer destination = (ClearCLBuffer)args[1];
        ClearCLBuffer psfEstimation = (ClearCLBuffer)args[2];
        int numberOfIterations = asInteger(args[3]);
        float mseThreshold = asFloat(args[4]);

        blindDeconvolve(clij, source, destination, psfEstimation, (int)psfEstimation.getWidth(), (int)psfEstimation.getHeight(), (int)psfEstimation.getDepth(), numberOfIterations, mseThreshold);

        return true;
    }

    public static void blindDeconvolve(CLIJ clij, ClearCLBuffer source, ClearCLBuffer destination, ClearCLBuffer psfEstimate, int psfSizeX, int psfSizeY, int psfSizeZ, int numberOfIterations, float mseThreshold) {
        ImagePlus idealPSFImp = NewImage.createFloatImage("ideal", psfSizeX, psfSizeY, psfSizeZ, NewImage.FILL_BLACK);
        idealPSFImp.setRoi(new Roi(psfSizeX / 2, psfSizeY / 2,1,1));
        idealPSFImp.setZ(psfSizeZ / 2 + 1);
        IJ.run(idealPSFImp, "Add...", "value=1");

        ClearCLBuffer idealPSF = clij.push(idealPSFImp);


        // start determining PSF
        ClearCLBuffer result = clij.create(source);

        ClearCLBuffer difference = clij.create(result);
        ClearCLBuffer squared = clij.create(result);

        clij.op().copy(source, result);

        ClearCLBuffer formerPSFEstimate = clij.create(psfEstimate);
        for (int i = 0; i < 100; i++) {
            clij.op().copy(psfEstimate, formerPSFEstimate);

            Convolve.convolveWithCustomKernel(clij, idealPSF, result, psfEstimate);

            Deconvolve.deconvolveWithCustomKernel(clij, source, psfEstimate, result, 10);
            clij.show(psfEstimate, "psf estimate");
            clij.show(result, "temp");

            clij.op().addImagesWeighted(psfEstimate, formerPSFEstimate, difference, 1f, -1f);
            clij.op().power(difference, squared, 2f);

            double mse = clij.op().sumPixels(squared);
            System.out.println("Change: " + mse);
            if (mse < 1) {
                break;
            }
        }

    }

    @Override
    public String getDescription() {
        return "Experimental. \n" +
                "The psf can be empty before. It serves as descriptor for the deserved psf size (xyz).\n" +
                "The higher the mse threshold, the earlier the iterative deconvolution stops";
    }

    @Override
    public String getAvailableForDimensions() {
        return "2D, 3D";
    }
}
