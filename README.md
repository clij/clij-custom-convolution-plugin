This repository contains a clij plugin for convolving images with custom OpenCL kernels.


In order to deploy a plugin to your Fiji installation, enter the correct path of your Fiji to the pom file:

```xml
<imagej.app.directory>C:/programs/fiji-win64/Fiji.app/</imagej.app.directory>
```

Afterwards, run

```
mvn install
```

Restart Fiji and check using this macro if your plugin was installed successfully.
