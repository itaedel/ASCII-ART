package image;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Facade for the image module and an interface representing an image.
 * @author Dan Nirel
 */
public interface Image {
    Color getPixel(int x, int y);
    int getWidth();
    int getHeight();

    /**
     * Open an image from file. Each dimensions of the returned image is guaranteed
     * to be a power of 2, but the dimensions may be different.
     * @param filename a path to an image file on disk
     * @return an object implementing Image if the operation was successful,
     * null otherwise
     */
    static Image fromFile(String filename) {
        try {
            return new FileImage(filename);
        } catch(IOException ioe) {
            return null;
        }
    }

    /**
     * Allows iterating the pixels' colors by order (first row, second row and so on).
     * @return an Iterable<Color> that can be traversed with a foreach loop
     */
    default Iterable<Color> pixels() {
        return new ImageIterableProperty<>(
                this, this::getPixel);
    }

    /**
     * default method for getting an arrayList of subImages.
     * divides an image into sub images according to the wanted size and returns an
     * ArrayList of sub images objects
     * @param subImageSize the size of each sub image
     * @return an arrayList of subImages
     */
    default ArrayList<Image> divToSubImages(int subImageSize){
        ArrayList<Image> arrayList = new ArrayList<>();
        int x = 0, y = 0, subImageRow = 0, subImageCol = 0, count = 0;
        Color [][] temp = new Color[subImageSize][subImageSize];
        while ((subImageRow * subImageSize) < this.getHeight()){
            if(count >= subImageSize * subImageSize){
                count = 0;
                arrayList.add(new SubImage(temp, subImageSize, subImageSize));
                temp = new Color[subImageSize][subImageSize];
            }
            count++;
            temp[x][y] = this.getPixel(subImageCol * subImageSize + x,
                    subImageRow * subImageSize + y);
            x += 1;
            if(x >= subImageSize){
                x = 0;
                y++;
            } if(y >= subImageSize){
                y = 0;
                subImageCol++;
            } if(subImageCol * subImageSize >= this.getWidth()){
                subImageCol = 0;
                subImageRow++;
            }
        }
        arrayList.add(new SubImage(temp, subImageSize, subImageSize)); //adds the last one
        return arrayList;
    }
}
