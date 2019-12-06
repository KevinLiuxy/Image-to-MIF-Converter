import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.util.ArrayList;

public class Main extends JProcessing.Processing{
    public static void main(String[] args){ new Main(); }
    private ArrayList<String> images=new ArrayList<>(16);
    private byte bits=1;                                                    //Bits per channel
    private byte moved=0;
    private byte mode=0;
    private byte selected=0;
    private boolean converted=false;
    private float sliderPosition=width*0.618f+height*0.0125f;

    protected void setup(){                                                 //Define GUI Parameters
        frameRate(30);
        setTitle(" Image to MIF Converter");
        PFont font=createFont("Calibri", 32);
        textFont(font);
    }

    protected void draw(){
        images.clear();                                                     //Detect Image Files in the App Folder
        File[] allFiles=new File(sketchPath()).listFiles();
        for (File file:allFiles){
            if (file.getPath().contains(".jpg") || file.getPath().contains(".bmp") || file.getPath().contains(".png") ||
                file.getPath().contains(".gif") || file.getPath().contains(".JPG") || file.getPath().contains(".BMP") ||
                file.getPath().contains(".PNG") || file.getPath().contains(".GIF") || file.getPath().contains(".jpeg"))
                images.add(file.getPath());
        }
                                                                            //List All Images Files
        background(239);
        fill(63);
        textSize(height/30f);
        for (int index=0; index<images.size() && index<26; index++)
            text(images.get(index),width/72f,index*height/29f+height/10.1f);
        if (images.size()==0) text(sketchPath("\\"),width/72f,height/10.1f);

        noStroke();                                                         //Display Status Bar
        fill(224);
        rect(0, height*0.955f, width, height*0.05f);
        fill(0);
        textSize(height/32f);
        text("> "+images.size()+" images in total",width/128f, height*0.96f, width*0.27, height*0.05f);
        if (converted)
            text("conversion completed",width*0.31f, height*0.96f, width*0.3f, height*0.05f);
        else if (selected==2 || selected==4)
            text("format: 1+"+bits*(selected-1)+" bits transparent",width*0.275f, height*0.96f, width*0.35f, height*0.05f);
        else if (selected==1 || selected==3)
            text("format: "+bits*selected+" bits opaque",width*0.275f, height*0.96f, width*0.35f, height*0.05f);
        text("Output folder  [ "+sketchPath("\\MIF ]"),width*0.618f, height*0.96f, width, height*0.05f);

        textSize(height/20f);                                               //Display Headings
        if (images.size()>0)
            text("Images Found:",width/72f,height/19f);
        else
            text("No Image in Folder:",width/72f,height/19f);
        text("Select a Profile:",width*0.618f,height/18.5f);

        stroke(31);
        for (byte index=0; index<4; index++) {                              //Display Buttons
            if (mode == index + 1)
                fill(255,255,255);
            else if (moved==index+1)
                fill(215,215,215);
            else
                fill(191,200,196);
            strokeWeight(2);
            rect(width*0.618f, height*0.087f+index*height*0.15f, width/3f, height/8.2f);
        }
        stroke(159);
        line(width*0.618f,height*0.75f,width*0.618f+width/3f,height*0.75f);
        stroke(31);
        if (mode==0 || images.size()==0)
            fill(159,159,159);
        else if (selected==5)
            fill(255,240,235);
        else if (moved==5)
            fill(215,196,191);
        else
            fill(200,193,191);
        rect(width*0.618f,height*0.79f,width/3f,height/7.5f);   //Draw Text on Buttons
        textAlign(CENTER,CENTER);
        fill(0);
        text("(1)        GRAY    ",width*0.605f,height*0.082f,width/3f,height/7.5f);
        text("   (2)  Alpha+GRAY",width*0.605f,height*0.232f,width/3f,height/7.5f);
        text("(3)        RGB      ",width*0.605f,height*0.382f,width/3f,height/7.5f);
        text("(4) Alpha+RGB",width*0.605f,height*0.532f,width/3f,height/7.5f);
        text("Convert",width*0.618f,height*0.79f,width/3f,height/7.5f);
        textSize(height/24f);
        text("Bits per Channel:  "+bits,width*0.618f,height*0.63f,width/3f,height/7.5f);
        textAlign(BASELINE,BASELINE);
                                                                            //Draw Slider
        fill(255);
        ellipse(sliderPosition,height*0.75f,height*0.03f,height*0.03f);
        if (mouseX>=width*0.618f+height*0.0125f && mouseX<=width*0.618f+width/3f-height*0.0125f &&
                mouseY<=height*0.78f && mouseY>=height*0.72f && mousePressed) {
            sliderPosition=mouseX;
            bits=(byte)floor(map(sliderPosition,width*0.618f,width*0.618f+width/3f,0,5)+1);
        }

        if (selected==5 && mode!=0 && images.size()>0 && mousePressed){     //Image to MIF Conversion
            for (String name:images){
                PImage image=loadImage(name);
                image.loadPixels();
                String line[]=new String[6+image.height];

                line[0]="DEPTH = "+image.width*image.height+";";            //Write MIF Parameters
                line[1]="WIDTH = "+((mode+mode%2-1)*bits+1-mode%2)+";";
                line[2]="ADDRESS_RADIX = HEX;";
                line[3]="DATA_RADIX = BIN;";
                line[4]="CONTENT BEGIN";
                line[line.length-1]="END;";

                for (short y=0; y<image.height; y++){
                    line[y+5]=hex(y*image.width);
                    if (line[y+5].length()==1)
                        line[y+5]+="   ";
                    else if (line[y+5].length()==2)
                        line[y+5]+="  ";
                    else if (line[y+5].length()==3)
                        line[y+5]+=" ";
                    line[y+5]+=":";

                    for (short x=0; x<image.width; x++){                    //Convert Pixel Data
                        int index=y*image.width+x;
                        if (mode==1)
                            line[y+5]+=" "+addZero(binary(round(brightness(image.pixels[index])/255f*(pow(2,bits)-1))));
                        else if (mode==2)
                            line[y+5]+=" "+Integer.toString(image.pixels[index].getAlpha()/128)+
                                    addZero(binary(round(brightness(image.pixels[index])/255f*(pow(2,bits)-1))));
                        else if (mode==3)
                            line[y+5]+=" "+addZero(binary(round(image.pixels[index].getRed()/255f*(pow(2,bits)-1))))+
                                    addZero(binary(round(image.pixels[index].getGreen()/255f*(pow(2,bits)-1))))+
                                    addZero(binary(round(image.pixels[index].getBlue()/255f*(pow(2,bits)-1))));
                        else
                            line[y+5]+=" "+Integer.toString(image.pixels[index].getAlpha()/128)+
                                    addZero(binary(round(image.pixels[index].getRed()/255f*(pow(2,bits)-1))))+
                                    addZero(binary(round(image.pixels[index].getGreen()/255f*(pow(2,bits)-1))))+
                                    addZero(binary(round(image.pixels[index].getBlue()/255f*(pow(2,bits)-1))));
                    }
                    line[y+5]+=";";
                }
                String fileName=name.substring(sketchPath().length());      //Generate File Name
                if (fileName.substring(fileName.length()-4).equals("jpeg"))
                    fileName="MIF/"+fileName.substring(0,fileName.length()-4)+"mif";
                else
                    fileName="MIF/"+fileName.substring(0,fileName.length()-3)+"mif";
                saveStrings(fileName,line,"UTF-8");
            }
            converted=true;
        }
    }

    protected void mouseMoved(){
        boolean selecting=false;                                            //Slider and Button Visual Effects

        for (byte index=0; index<4; index++){
            if (mouseX>=width*0.618f && mouseX<=width*0.618f+width/3f &&
                mouseY>=height/11.5f+index*height*0.15f && mouseY<=height/11.5f+index*height*0.15f+height/8.2f){
                moved=(byte)(index+1);
                selecting=true;
            }
        }
        if (mouseX>=width*0.618f && mouseX<=width*0.618f+width/3f &&
                mouseY>=height*0.79f && mouseY<=height*0.79f+height/7.5f){
            moved=5;
            selecting=true;
        }
        if (!selecting) moved=selected;

        if (mouseX>=width*0.618f+height*0.0125f && mouseX<=width*0.618f+width/3f-height*0.0125f
                 && mouseY<=height*0.78f && mouseY>=height*0.72f)
            cursor(HAND);
        else
            cursor(ARROW);
    }

    protected void mousePressed(){
        selected=moved;
        if (moved!=5) mode=moved;
        converted=false;
    }
    protected void mouseReleased(){ if (selected==5) selected=mode; }

    private String addZero(@NotNull String number){                         //Add Zeros before Converted Numbers
        String newNumber=number;
        for (short index=0; index<(bits-number.length()); index++) newNumber="0".concat(newNumber);
        return newNumber;
    }
}