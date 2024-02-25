package image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A package-private class of the package image.
 * @author Dan Nirel
 */
class FileImage implements Image {
    private static final Color DEFAULT_COLOR = Color.WHITE;
    private static final int BASE_2 = 2;
    private final int height;
    private final int width;
    private final Color[][] pixelArray;
//    private final BufferedImage bufferedImage;
//    private final int origWidth;
//    private final int origHeight;
//    private final int heightDiff;
//    private final int widthDiff;


    /**
     * constructor
     * @param filename the filename
     * @throws IOException in case of fail
     */
    public FileImage(String filename) throws IOException {
        java.awt.image.BufferedImage im = ImageIO.read(new File(filename));
        int origWidth = im.getWidth(), origHeight = im.getHeight();
        //im.getRGB(x, y)); getter for access to a specific RGB rates
        if(logBaseChange(origWidth, BASE_2) == Math.floor(logBaseChange(origWidth,BASE_2))){
            this.width = origWidth;
        } else {
            this.width = (int)Math.pow(BASE_2, Math.floor(logBaseChange(origWidth, BASE_2) + 1));
        }
        if(logBaseChange(origHeight, BASE_2) == Math.floor(logBaseChange(origHeight, BASE_2))){
            this.height = origHeight;
        } else {
            this.height = (int)Math.pow(BASE_2, Math.floor(logBaseChange(origHeight, BASE_2) + 1));
        }
        pixelArray = new Color[width][height];
        fillPixelArray(origWidth, origHeight, im); // fill array
    }

    /**
     * fills the pixels array and adds padding
     * @param origWidth the image original width
     * @param origHeight the image original height
     * @param im the image itself
     */
    private void fillPixelArray(int origWidth, int origHeight,
                                BufferedImage im){
        int heightDiff = (height - origHeight) / 2;
        int widthDiff = (width - origWidth) / 2;
        for(int i = 0; i < width; i++){
            for (int j = 0; j < height; j++) {
                if(i < origWidth + widthDiff && j < origHeight + heightDiff &&
                        i >= widthDiff && j >= heightDiff){ // for padding edges
                    pixelArray[i][j] = new Color(im.getRGB(i - widthDiff, j - heightDiff));
                } else{
                    pixelArray[i][j] = DEFAULT_COLOR; // pad edges
                }
            }
        }
    }

    /**
     * transform base of log
     * @param a the result
     * @param b new log base
     * @return log_a^b
     */
    private double logBaseChange(double a, double b){
        return Math.log(a) / Math.log(b);
    }

    /**
     * getter function
     * @return returns image width
     */
    @Override
    public int getWidth() {
        return width;
    }

    /**
     * getter function
     * @return returns height
     */
    @Override
    public int getHeight() {
        return height;
    }

    /**
     * getter for pixel
     * @param x the width
     * @param y the height
     * @return the Color of the pixel
     */
    @Override
    public Color getPixel(int x, int y) {
        if(x < getWidth() && y < getHeight() && x >= 0 && y >= 0){
            return pixelArray[x][y];
        }
        return null;
    }
}


/**
 * reperesnt a sub image as an image object
 */
class SubImage implements Image{
    private final Color[][] image;
    private final int width;
    private final int height;


    /**
     * constructor
     * @param image the pixels of the image
     * @param width the width
     * @param height the height
     */
    public SubImage(Color[][] image, int width, int height) {
        this.image = image;
        this.width = width;
        this.height = height;
    }

    /**
     * get pixel at x,y
     * @param x
     * @param y
     * @return the pixel
     */
    @Override
    public Color getPixel(int x, int y) {
        return image[x][y];
    }

    /**
     * get width of an image
     * @return
     */
    @Override
    public int getWidth() {
        return width;
    }

    /**
     * get height of an image
     * @return
     */
    @Override
    public int getHeight() {
        return height;
    }
}


