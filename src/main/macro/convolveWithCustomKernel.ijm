run("Close All");

// get test data
open("C:/structure/data/blobs.gif");
// run("Blobs (25K)");
run("32-bit");

// get custom convolution kernel
newImage("kernelImage", "32-bit black", 256, 254, 1);
makeRectangle(0, 3, 7, 1);
run("Add...", "value=0.2");

// convolve in GPU
run("CLIJ Macro Extensions", "cl_device=[Intel(R) UHD Graphics 620]");
Ext.CLIJ_clear();
Ext.CLIJ_push("blobs.gif");
Ext.CLIJ_push("kernelImage");
Ext.CLIJ_convolveWithCustomKernel2D("blobs.gif", "kernelImage", "CLIJ_convolveWithCustomKernel_destination_blobs.gif", 7, 7);
Ext.CLIJ_pull("CLIJ_convolveWithCustomKernel_destination_blobs.gif");

