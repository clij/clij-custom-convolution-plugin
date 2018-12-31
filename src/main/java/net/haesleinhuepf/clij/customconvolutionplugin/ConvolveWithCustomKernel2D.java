package net.haesleinhuepf.clij.customconvolutionplugin;

import clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.macro.AbstractCLIJPlugin;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clij.macro.CLIJOpenCLProcessor;
import net.haesleinhuepf.clij.macro.documentation.OffersDocumentation;
import org.scijava.plugin.Plugin;

import java.util.HashMap;

/**
 *
 *
 * Author: @haesleinhuepf
 * 12 2018
 */
@Plugin(type = CLIJMacroPlugin.class, name = "CLIJ_convolveWithCustomKernel2D")
public class ConvolveWithCustomKernel2D extends AbstractCLIJPlugin implements CLIJMacroPlugin, CLIJOpenCLProcessor, OffersDocumentation {

    @Override
    public boolean executeCL() {
        Object[] args = openCLBufferArgs();
        boolean result = convolveWithCustomKernel((ClearCLBuffer)( args[0]), (ClearCLBuffer)(args[1]), (ClearCLBuffer)(args[2]), asInteger(args[3]), asInteger(args[4]));
        releaseBuffers(args);
        return result;
    }

    private boolean convolveWithCustomKernel(ClearCLBuffer src, ClearCLBuffer kernel, ClearCLBuffer dst, int kernelWidth, int kernelHeight) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("src", src);
        parameters.put("kernelImage", kernel);
        parameters.put("dst", dst);
        parameters.put("kernelWidth", kernelWidth);
        parameters.put("kernelHeight", kernelHeight);
        //if (src.getDimension() > 2) {
        //    parameters.put("kernelDepth", kernel.getDepth());
        //}
        return clij.execute(ConvolveWithCustomKernel2D.class,
                "customConvolution.cl",
                "custom_convolution_" + src.getDimension() + "d",
                parameters);
    }

    @Override
    public String getParameterHelpText() {
        return "Image source, Image convolution_kernel, Image destination, Number kernelWidth, Number kernelHeight";
    }

    @Override
    public String getDescription() {
        return "Convolve the image with a given kernel image. Kernel image and source image should have the same\n" +
                "bit-type. Furthermore, it is recommended that the kernel image has an odd size in X, Y and Z.";
    }

    @Override
    public String getAvailableForDimensions() {
        return "2D, 3D";
    }
}