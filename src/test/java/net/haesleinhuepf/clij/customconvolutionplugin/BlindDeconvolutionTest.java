package net.haesleinhuepf.clij.customconvolutionplugin;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.NewImage;
import ij.gui.Roi;
import net.haesleinhuepf.clij.CLIJ;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;

/**
 * BlindDeconvolutionTest
 * <p>
 * Author: @haesleinhuepf
 * January 2019
 */
public class BlindDeconvolutionTest {

    public static void main(String... args) {
        new ImageJ();
        CLIJ clij = CLIJ.getInstance("1070");

        int width = 15;
        int height = 15;
        int depth = 1;

        ImagePlus idealPSFImp = NewImage.createFloatImage("ideal", width, height, depth, NewImage.FILL_BLACK);
        idealPSFImp.setRoi(new Roi(width / 2, height / 2,1,1));
        idealPSFImp.setZ(depth / 2 + 1);
        IJ.run(idealPSFImp, "Add...", "value=1");

        ImagePlus sampleImp = IJ.openImage("src/test/resources/blobs.tif");

        ClearCLBuffer idealPSF = clij.push(idealPSFImp);
        ClearCLBuffer sample = clij.push(sampleImp);

        // create test data
        ClearCLBuffer psfGroundTruth = clij.create(idealPSF);
        ClearCLBuffer convolved = clij.create(sample);

        clij.op().blurFast(idealPSF, psfGroundTruth, 2f, 2f, 0);

        clij.show(psfGroundTruth, "psf");

        Convolve.convolveWithCustomKernel(clij, sample, psfGroundTruth, convolved);

        clij.show(convolved, "convolved");

        ClearCLBuffer psfEstimate = clij.create(psfGroundTruth);
        ClearCLBuffer result = clij.create(convolved);

        BlindDeconvolve.blindDeconvolve(clij, convolved, result, psfEstimate, 15, 15, 15, 100, 1.0f);





    }
}
