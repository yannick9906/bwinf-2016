package tk.yannickfelix.bwinf2016.task2;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * <b>Aufgabe 2 Bundeswettbewerb Informatik 2016/17</b>
 * This class is responsible for processing the image.
 * It can load, save and process images.
 *
 * @author Yannick Félix
 * @since 04.09.2016
 * @version 1.0
 */
public class ImageProcessor {

    private BufferedImage imgIn = null, imgOut = null;
    private boolean processing = false;
    private boolean loaded = false;

    public ImageProcessor() {

    }

    /**
     * Loads an file as a image
     *
     * @param file File to load
     * @throws IOException
     */
    public void loadImage(File file) throws IOException {
        System.out.println("Opening file: "+file.getAbsolutePath());
        imgIn = ImageIO.read(file);
        imgOut = ImageIO.read(file);
        loaded = true;
    }

    /**
     * Save a image to a file
     *
     * @param file Destination to save to
     * @throws IOException
     */
    public void saveImage(File file) throws IOException {
        ImageIO.write(imgOut, getExtension(file), file);
    }

    /**
     * Extracts the extension of a file
     *
     * @param file
     * @return <code>file</code>'s extension
     */
    private String getExtension(File file) {
        String extension = "";
        String fileName = file.getName();
        int i = fileName.lastIndexOf('.');
        int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));

        if (i > p) {
            extension = fileName.substring(i+1);
        }
        return extension;
    }

    /**
     * Processes a Image that has been loaded with <code>loadImage</code>
     *
     * @param smoothing Smoothing factor (positive)
     * @param jProgressBar JProgressBar to update
     * @param jStatus JLabel to update
     */
    public void processImage(int smoothing, JProgressBar jProgressBar, JLabel jStatus) {
        if(!processing && loaded) {
            processing = true;
            jProgressBar.setMinimum(0);
            jProgressBar.setMaximum(100);
            jProgressBar.setValue(getProgressValue(0, 2, 0, 1, 0, 1));
            jStatus.setText("Bildaufbereitung Schritt 1/2");
            //Find 'big' pixels
            findLowRes(jProgressBar);

            jStatus.setText("Bildaufbereitung Schritt 2/2");
            //Smooth result smoothing times
            for (int i = 0; i < smoothing; i++) {
                jProgressBar.setValue(getProgressValue(1, 2, i, smoothing, 0, 1));
                imgOut = doSmoothing(imgIn, imgOut, jProgressBar, i, smoothing);
            }

            jStatus.setText("Bildaufbereitung abgeschlossen");
            processing = false;
        }
    }

    /**
     * Smooths out the image
     *
     * @param original original image
     * @param processed previous processed image
     * @param jProgressBar JProgressBar to update
     * @param substep current substep (needed for progressbar calc)
     * @param maxsubstep max of substeps (needed for progressbar calc)
     * @return
     */
    private BufferedImage doSmoothing(BufferedImage original, BufferedImage processed, JProgressBar jProgressBar, int substep, int maxsubstep) {
        int height = original.getHeight();
        int width = original.getWidth();
        int rgbTop;
        int rgbTopLeft;
        int rgbTopRight;
        int rgbBottom;
        int rgbBottomLeft;
        int rgbBottomRight;
        int rgbLeft;
        int rgbRight;

        //Iterate over the complete picture
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                jProgressBar.setValue(getProgressValue(1,2,substep,maxsubstep,h*width+w,height*width));

                //Get the pixels values
                int rgbThis = processed.getRGB(w, h);
                try { rgbTop         = processed.getRGB(w, h-1);   } catch (ArrayIndexOutOfBoundsException e) { rgbTop         = 0; }
                try { rgbTopLeft     = processed.getRGB(w-1, h-1); } catch (ArrayIndexOutOfBoundsException e) { rgbTopLeft     = 0; }
                try { rgbTopRight    = processed.getRGB(w+1, h-1); } catch (ArrayIndexOutOfBoundsException e) { rgbTopRight    = 0; }
                try { rgbBottom      = processed.getRGB(w, h+1);   } catch (ArrayIndexOutOfBoundsException e) { rgbBottom      = 0; }
                try { rgbBottomLeft  = processed.getRGB(w-1, h+1); } catch (ArrayIndexOutOfBoundsException e) { rgbBottomLeft  = 0; }
                try { rgbBottomRight = processed.getRGB(w+1, h+1); } catch (ArrayIndexOutOfBoundsException e) { rgbBottomRight = 0; }
                try { rgbLeft        = processed.getRGB(w-1, h);   } catch (ArrayIndexOutOfBoundsException e) { rgbLeft        = 0; }
                try { rgbRight       = processed.getRGB(w+1, h);   } catch (ArrayIndexOutOfBoundsException e) { rgbRight       = 0; }

                //Bitshift to get r g b seperate
                int value = 0;
                int red = (rgbThis >> 16) & 0xff;
                int green = (rgbThis >> 8) & 0xff;
                int blue = (rgbThis) & 0xff;

                //if a pixel is white, check how many pixels around are white too
                if(red == 255 && green == 255 && blue == 255) {
                    if (rgbThis == rgbTop) value++;
                    if (rgbThis == rgbTopLeft) value++;
                    if (rgbThis == rgbTopRight) value++;
                    if (rgbThis == rgbBottom) value++;
                    if (rgbThis == rgbBottomLeft) value++;
                    if (rgbThis == rgbBottomRight) value++;
                    if (rgbThis == rgbLeft) value++;
                    if (rgbThis == rgbRight) value++;
                }

                //Only if 8 pixels are white too set him to white again
                if(value == 8) original.setRGB(w,h,0xFFFFFFF);
            }
        }
        return original;
    }

    /**
     * Finds all 'big'-pixels in the loaded image
     *
     * @param jProgressBar JProgressBar to update
     */
    private void findLowRes(JProgressBar jProgressBar) {
        int height = imgIn.getHeight();
        int width = imgIn.getWidth();
        int rgbTop;
        int rgbTopLeft;
        int rgbTopRight;
        int rgbBottom;
        int rgbBottomLeft;
        int rgbBottomRight;
        int rgbLeft;
        int rgbRight;

        //Iterate over the complete picture
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                jProgressBar.setValue(getProgressValue(0,2,h*width+w,height*width,1,1));

                //Get the pixels values
                int rgbThis = imgIn.getRGB(w, h);
                try { rgbTop         = imgIn.getRGB(w, h-1);   } catch (ArrayIndexOutOfBoundsException e) { rgbTop         = 0; }
                try { rgbTopLeft     = imgIn.getRGB(w-1, h-1); } catch (ArrayIndexOutOfBoundsException e) { rgbTopLeft     = 0; }
                try { rgbTopRight    = imgIn.getRGB(w+1, h-1); } catch (ArrayIndexOutOfBoundsException e) { rgbTopRight    = 0; }
                try { rgbBottom      = imgIn.getRGB(w, h+1);   } catch (ArrayIndexOutOfBoundsException e) { rgbBottom      = 0; }
                try { rgbBottomLeft  = imgIn.getRGB(w-1, h+1); } catch (ArrayIndexOutOfBoundsException e) { rgbBottomLeft  = 0; }
                try { rgbBottomRight = imgIn.getRGB(w+1, h+1); } catch (ArrayIndexOutOfBoundsException e) { rgbBottomRight = 0; }
                try { rgbLeft        = imgIn.getRGB(w-1, h);   } catch (ArrayIndexOutOfBoundsException e) { rgbLeft        = 0; }
                try { rgbRight       = imgIn.getRGB(w+1, h);   } catch (ArrayIndexOutOfBoundsException e) { rgbRight       = 0; }

                //Check for 'big' pixel and set those to white
                if(rgbThis == rgbRight && rgbThis == rgbTopRight && rgbThis == rgbTop) {
                    imgOut.setRGB(w,h, 0xFFFFFFF);
                    imgOut.setRGB(w,h-1, 0xFFFFFFF);
                    imgOut.setRGB(w+1,h-1, 0xFFFFFFF);
                    imgOut.setRGB(w+1,h, 0xFFFFFFF);
                } else if(rgbThis == rgbRight && rgbThis == rgbBottomRight && rgbThis == rgbBottom) {
                    imgOut.setRGB(w,h, 0xFFFFFFF);
                    imgOut.setRGB(w+1,h, 0xFFFFFFF);
                    imgOut.setRGB(w+1,h+1, 0xFFFFFFF);
                    imgOut.setRGB(w,h+1, 0xFFFFFFF);
                } else if(rgbThis == rgbLeft && rgbThis == rgbTopLeft && rgbThis == rgbTop) {
                    imgOut.setRGB(w,h, 0xFFFFFFF);
                    imgOut.setRGB(w,h-1, 0xFFFFFFF);
                    imgOut.setRGB(w-1,h-1, 0xFFFFFFF);
                    imgOut.setRGB(w-1,h, 0xFFFFFFF);
                } else if(rgbThis == rgbLeft && rgbThis == rgbBottomLeft && rgbThis == rgbBottom) {
                    imgOut.setRGB(w, h, 0xFFFFFFF);
                    imgOut.setRGB(w, h + 1, 0xFFFFFFF);
                    imgOut.setRGB(w - 1, h + 1, 0xFFFFFFF);
                    imgOut.setRGB(w - 1, h, 0xFFFFFFF);
                }
            }
        }
    }

    /**
     * Calcualtes the correct value for a progressbar
     *
     * @param step current step
     * @param maxstep max step
     * @param substep current substep
     * @param maxsubstep max substep
     * @param subsubstep current subsubstep
     * @param maxsubsubstep max subsubstep
     * @return value between 0 and 100
     */
    private int getProgressValue(int step, int maxstep, int substep, int maxsubstep, int subsubstep, int maxsubsubstep) {
        float completedFirstPart = ((float) step) / ((float) maxstep);
        float completedSubPart = ((float) substep) / ((float) maxsubstep);
        float completedSubSubPart = ((float) subsubstep) / ((float) maxsubsubstep);
        float subPartInFirstPart = completedSubPart*(1.0f / maxstep);
        float subSubPartInFirstPart = (completedSubSubPart*(1.0f / maxsubstep))*(1.0f / maxstep);
        return (int) (completedFirstPart*100+subPartInFirstPart*100+subSubPartInFirstPart*100+0.5f);
    }

    /**
     * @return the processed image
     */
    public BufferedImage getProcessedImg() {
        return imgOut;
    }

    /**
     * @return true if a image has been loaded
     */
    public boolean isLoaded() {
        return loaded;
    }
}
