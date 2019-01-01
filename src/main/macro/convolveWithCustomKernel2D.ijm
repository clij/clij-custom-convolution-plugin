psf_folder = "C:/structure/code/clij-custom-convolution-plugin/src/main/resources/"
run("Close All");

// get test data
newImage("spots", "32-bit black", 100, 100, 1);
makeRectangle(15, 35, 1, 1);
run("Add...", "value=255 slice");

makeRectangle(45, 35, 1, 1);
run("Add...", "value=255 slice");

makeRectangle(50, 35, 1, 1);
run("Add...", "value=255 slice");

makeRectangle(50, 70, 1, 1);
run("Add...", "value=255 slice");

// get custom convolution kernel
newImage("kernelImage", "32-bit black", 7, 7, 1);
makeRectangle(0, 3, 7, 1);
run("Add...", "value=0.2");

// convolve in GPU
run("CLIJ Macro Extensions", "cl_device=[Intel(R) UHD Graphics 620]");
Ext.CLIJ_clear();
Ext.CLIJ_push("spots");
Ext.CLIJ_push("kernelImage");

// normalize kernel
Ext.CLIJ_sumOfAllPixels("kernelImage");
sumPixels = getResult("Sum", nResults() - 1);
Ext.CLIJ_multiplyImageAndScalar("kernelImage", "normalizedKernel", 1.0 / sumPixels);

Ext.CLIJ_convolve("spots", "normalizedKernel", "convolved");
Ext.CLIJ_pull("convolved");




