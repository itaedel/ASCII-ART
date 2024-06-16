package ascii_art.img_to_char;

import image.Image;
//import image.SubImage;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


/**
 * a class for matching the char with its brightness
 */
public class BrightnessImgCharMatcher {
    private final Image image;
    private final String fontName;
    private static final int RES_NORMAL = 16;
    private static final int MAX_RGB = 255;
    private HashMap<Integer, double[][]> subImagesMap; // key is the numCharsInRow

    /**
     * constructor
     * @param img an image
     * @param font the font of the chars
     */
    public BrightnessImgCharMatcher(Image img, String font){
        this.image = img;
        this.fontName = font;
        this.subImagesMap = new HashMap<>();
    }

    /**
     * convert the whole image into chars
     * @param numCharsInRow the number of chars in a row
     * @param charSet the chars
     * @return the image in characters
     */
    public char[][] chooseChars(int numCharsInRow, Character[] charSet){
        int subImageSize = image.getWidth() / numCharsInRow; // assuming this is a whole number
        int numCharsInCol = image.getHeight() / subImageSize; // assuming this is a whole number
        double [][] subImageAvg = null;
        if(subImagesMap.containsKey(numCharsInRow)){
            subImageAvg = subImagesMap.get(numCharsInRow);
        } else{
            subImageAvg = getImageAvg(subImageSize, numCharsInRow, numCharsInCol);
            subImagesMap.put(numCharsInRow, subImageAvg);
        }
        HashMap<Character, Double> charBrightnessMap = new HashMap<>();
        CalcCharBrightness(charBrightnessMap, charSet); // does phase 1-3
        return fillFinalPicture(numCharsInRow, numCharsInCol, subImageAvg, charBrightnessMap);
    }

    /**
     * helper function to fill the final picture that will be returned
     * @param numCharsInRow number of chars in a row
     * @param numCharsInCol number of chars in column
     * @param subImageAvg the Sub Image average brightness
     * @param charBrightnessMap a map with the brightness of all chars
     * @return the final char [][] array that will represent the picture in chars
     */
    private char[][] fillFinalPicture(int numCharsInRow, int numCharsInCol, double[][] subImageAvg,
                                      HashMap<Character, Double> charBrightnessMap){
        char [][] finalPicture = new char[numCharsInCol][numCharsInRow];
        for (int i = 0; i < numCharsInCol; i++) {
            for (int j = 0; j < numCharsInRow; j++) {
                double bestDiff = 2, curDiff = 0;
                for(Character key: charBrightnessMap.keySet()){
                    curDiff = Math.abs(charBrightnessMap.get(key) - subImageAvg[i][j]);
                    if(curDiff < bestDiff){
                        bestDiff = curDiff;
                        finalPicture[i][j] = key;
                    }
                }
            }
        }
        return finalPicture;
    }

    /**
     * calculate the brightness of each character - if only one char than don't stretch it
     * @param charBrightnessMap the map of character and their values
     * @param chars the chars
     */
    private void CalcCharBrightness(HashMap<Character, Double> charBrightnessMap, Character[] chars) {
        double maxBright = 0;
        double minBright = 1;
        for(Character c: chars){
            charBrightnessMap.put(c, countWhitesAndDiv(CharRenderer.getImg(c, RES_NORMAL, fontName)));
            if(charBrightnessMap.get(c) < minBright){
                minBright = charBrightnessMap.get(c);
            }
            if(charBrightnessMap.get(c) > maxBright){
                maxBright = charBrightnessMap.get(c);
            }
        }
        if(chars.length == 1){
            return;
        }
        for(Character key: charBrightnessMap.keySet()){
            double p = newCharBrightness(charBrightnessMap.get(key),
                    minBright, maxBright);
            charBrightnessMap.put(key, p);
        }
    }

    /**
     * This calls the image method to divide into sub images
     * @param subImageSize the sub image size
     * @param numCharsInRow the num of chars in a row
     * @param numCharsInCol the num of chars in a column
     * @return an array representing the average brightness of each sub image
     */
    private double[][] getImageAvg(int subImageSize, int numCharsInRow, int numCharsInCol) {
        double [][] array = new double[numCharsInCol][numCharsInRow];
        ArrayList<Image> arrayList = image.divToSubImages(subImageSize);
        for (int k = 0; k < arrayList.size(); k++) {
            double sum = 0;
            for (Color color : arrayList.get(k).pixels()) {
                sum += getGrayValue(color);
            }
            array[k / numCharsInRow][k % numCharsInRow] =
                    sum / ((subImageSize * subImageSize) * MAX_RGB);
        }
        return array;
    }

    /**
     * this does the first phase for each sub-image by counting how many white pixels are there
     * and divides by 16 - the constant
     * @param chars the array of boolean
     * @return the number of white pixels = number of true
     */
    private double countWhitesAndDiv(boolean[][] chars){
        double count_whites = 0;
        for (boolean[] aChar : chars) {
            for (boolean b : aChar) {
                if (b) {
                    count_whites += 1;
                }
            }
        }
        return count_whites / ( RES_NORMAL * RES_NORMAL);
    }

    /**
     * return new sub image brightness according to phase 3
     * @param char_brightness the char brightness
     * @param min_brightness the least bright char
     * @param max_brightness the most bright char
     * @return the new brightness
     */
    private double newCharBrightness(double char_brightness, double min_brightness,
                                     double max_brightness){
        return (char_brightness - min_brightness) / (max_brightness - min_brightness);
    }

    /**
     * gets color and returns its gray value
     * @param color the pixel color
     * @return gray pixel
     */
    private double getGrayValue(Color color){
        return color.getRed() * 0.2126 + color.getGreen() * 0.7152 + color.getBlue() * 0.0722;
    }
}
