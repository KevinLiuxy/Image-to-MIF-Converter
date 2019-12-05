# Image-to-MIF-Converter
Image to MIF File Graphical Converter for FPGA Projects (supports pixel transparency)

<h5>This project is an image to memory initialization file converter:</h5>
<p>
->It has graphical user interface<br>
->Support converting multiple image files at a time (with a single click)<br>
->Support converting transparent images and keeps the alpha data in one bit<br>
->Support .bmp .jpg .png .gif format<br>
->No image size constraints<br>

<h5>How it works:</h5>
<p>
Drag images into the folder containing the program, select a profile, and then press the convert button. All image files in the folder will be converted at the same time, and output to "./MIF" folder after conversion completes.<br>
You can choose number of bits per color channel using the slider, support 1~5 bits per channel.<br>
The size of converted image will be equivalent to the original image.<br>

<h5>Profile Explained:</h5>
<p>
-> GRAY:  store image using grayscale data with width specified by bits-per-channel (non-transparent)<br>
->A+GRAY: store alpha data of image in the first bit, followed by grayscale data with width specified by bits-per-channel<br>
-> RGB:   store image using R-G-B components with width specified by bits-per-channel (non-transparent)<br>
->A+RGB:  store alpha data of image in the first bit, followed by R-G-B components with width specified by bits-per-channel<br>

<h5>System Requirement:</h5>
<p>
Runs on computers with Java Runtime Environment (since it's a Java application).<br>
If " IMG to MIF.jar " program cannot be executed, please install Java first.<br>

<h6>I hope it will be helpful for your project. Good luck!</h6>
