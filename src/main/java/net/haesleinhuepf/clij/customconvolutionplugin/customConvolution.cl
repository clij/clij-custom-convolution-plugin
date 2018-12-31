__constant sampler_t sampler = CLK_NORMALIZED_COORDS_FALSE | CLK_ADDRESS_CLAMP_TO_EDGE | CLK_FILTER_NEAREST;


__kernel void custom_convolution_3d(
    DTYPE_IMAGE_IN_3D src,
    DTYPE_IMAGE_IN_3D kernelImage,
    DTYPE_IMAGE_OUT_3D dst,
    int kernelWidth,
    int kernelHeight,
    int kernelDepth
) {
  const sampler_t sampler = CLK_NORMALIZED_COORDS_FALSE | CLK_ADDRESS_CLAMP_TO_EDGE | CLK_FILTER_NEAREST;

  const int i = get_global_id(0);
  const int j = get_global_id(1);
  const int k = get_global_id(2);

  int4 coord = (int4){i, j, k, 0};

  int4 c = (int4){kernelWidth / 2, kernelHeight / 2, kernelDepth / 2, 0};

  float sum = 0;
  for (int x = -c.x; x <= c.x; x++) {
    for (int y = -c.y; y <= c.y; y++) {
      for (int z = -c.z; z <= c.z; z++) {
        sum = sum + READ_IMAGE_3D(kernelImage,sampler,(int4)(x,y,z,0)).x
                  * READ_IMAGE_3D(src,sampler,coord+(int4)(x,y,z,0)).x;
      }
    }
  }

  WRITE_IMAGE_3D(dst,(int4)(i,j,k,0),(DTYPE_OUT)sum);
}

__kernel void custom_convolution_2d(
    DTYPE_IMAGE_IN_2D src,
    DTYPE_IMAGE_IN_2D kernelImage,
    DTYPE_IMAGE_OUT_2D dst,
    int kernelWidth,
    int kernelHeight
) {
  const sampler_t sampler = CLK_NORMALIZED_COORDS_FALSE | CLK_ADDRESS_CLAMP_TO_EDGE | CLK_FILTER_NEAREST;

  const int i = get_global_id(0);
  const int j = get_global_id(1);

  int2 coord = (int2){i, j};

  int2 c = (int2){kernelWidth / 2, kernelHeight / 2};

  float sum = 0;
  for (int x = -c.x; x <= c.x; x++) {
    for (int y = -c.y; y <= c.y; y++) {
        sum = sum + ((float)READ_IMAGE_2D(kernelImage,sampler,(int2)(x,y)).x
                  * (float)READ_IMAGE_2D(src,sampler,coord+(int2)(x,y)).x);
    }
  }
  WRITE_IMAGE_2D(dst,(int2)(i,j),(DTYPE_OUT)sum);
}


