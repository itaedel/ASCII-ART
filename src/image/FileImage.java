package image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A package-private class of the package image.
 * @author Dan Nirel
 */
class FileImage implements Image {
    private static final Color DEFAULT_COLOR = Color.WHITE;
    private static final int BASE = 2;
    private final int height;
    private final int width;
    private final Color[][] pixelArray;


    /**
     * constructor
     * @param filename the filename
     * @throws IOException in case of fail?
     */
    public FileImage(String filename) throws IOException {
        java.awt.image.BufferedImage im = ImageIO.read(new File(filename));
        int origWidth = im.getWidth(), origHeight = im.getHeight();
        //im.getRGB(x, y)); getter for access to a specific RGB rates
        this.width = (int) Math.pow(2, Math.floor(Math.log(origWidth) / Math.log(2)) + 1.0);
        this.height = (int) Math.pow(2, Math.floor(Math.log(origHeight) / Math.log(2)) + 1.0);
        int heightDiff = (height - origHeight) / 2, widthDiff = (width - origWidth) / 2;
        pixelArray = new Color[width][height];
        for(int i = 0; i < width; i++){
            for (int j = 0; j < height; j++) {
                if(i < origWidth + widthDiff && j < origHeight + heightDiff &&
                        i >= widthDiff && j >= heightDiff){ // for padding edges
                    pixelArray[i][j] = new Color(im.getRGB(i - widthDiff, j - heightDiff));
                } else{
                    pixelArray[i][j] = DEFAULT_COLOR;
                }
            }

        }
    }

    /**
     * getter function
     * @return returns width
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

    /**
     * iterator that goes over sub-images from in order of rows from first row to last while going over
     * each row from first column to last
     * @param sub_image_size the size of each given image - the n in the n x n
     * @return the special iterator
     */
    @Override
    public Iterable<Color> pixels(int sub_image_size) {
        return new IterSubImage<Color>(this, sub_image_size);
    }
}

/**
 * special iterator for going over the sub-images one by one in order of row.
 */
class IterSubImage<Color> implements Iterable<Color>{
    private final Image image;
    private final int sub_image_size; // the n in n X n
    private Color color;

    /**
     * constructor
     * @param image
     * @param sub_image_size
     */
    public IterSubImage(Image image, int sub_image_size){
        this.image = image;
        this.sub_image_size = sub_image_size;
    }

    /**
     * returns the iterator object that can be iterated to go over sub images
     * @return an Iterator<Color>
     */
    @Override
    public Iterator<Color> iterator() {
        return new Iterator<Color>() {
            int x = 0, y = 0;
            int sub_image_row = 0, sub_image_col = 0; // thinking of the array as a 2d array of sub-pictures

            /**
             * checks if there are more pixels to go over
             * @return true if there are, false otherwise
             */
            @Override
            public boolean hasNext() {
                return (sub_image_row * sub_image_size) < image.getHeight();
            }

            /**
             * returns the current pixel and advances to the next
             * @return a single pixel Color
             */
            @Override
            public Color next() {
                if(!hasNext()){
                    throw new NoSuchElementException();
                }
                var cur_pixel = image.getPixel(sub_image_col* sub_image_size + x,
                        sub_image_row * sub_image_size + y);
                x += 1;
                if(x >= sub_image_size){
                    x = 0;
                    y++;
                }
                if(y >= sub_image_size){
                    y = 0;
                    sub_image_col++;
                }
                if(sub_image_col * sub_image_size >= image.getWidth()){
                    sub_image_col = 0;
                    sub_image_row++;
                }
                return (Color) cur_pixel; // todo why is this bad?
            }
        };
    }

}
