package ascii_art.img_to_char;

import image.Image;

import java.awt.*;
import java.util.HashMap;
import java.util.Iterator;

//TODO: this class

/**
 *
 */
public class BrightnessImgCharMatcher {
    private final Image image;
    private final String fontName;
    private static final int RES_NORMAL = 16;
    private static final int MAX_RGB = 255;

    /**
     * constructor
     * @param img
     * @param font
     */
    public BrightnessImgCharMatcher(Image img, String font){
        this.image = img;
        this.fontName = font;
    }

    /**
     * convert the whole image into chars
     * @param numCharsInRow
     * @param charSet
     * @return
     */
    public char[][] chooseChars(int numCharsInRow, Character[] charSet){
        int sub_picture_size = image.getWidth() / numCharsInRow; // assuming this is a whole number
        int numCharsInCol = image.getHeight() / sub_picture_size; // assuming this is a whole number??
        // image.getHeight() / sub_picture_size = amount of sub pictures in a column
        char [][] final_picture = new char[numCharsInCol][numCharsInRow];
        double [][] subImagesAvg = getAvgForImage(sub_picture_size, numCharsInRow, numCharsInCol);
        HashMap<Character, Double> charBrightnessMap = new HashMap<Character, Double>();
        CalcCharBrightness(charBrightnessMap, sub_picture_size, charSet); // does phase 1-3
        for (int i = 0; i < numCharsInCol; i++) {
            for (int j = 0; j < numCharsInRow; j++) {
                char best_char = ' '; // todo change
                double bestDiff = 2;
                for(Character key: charBrightnessMap.keySet()){
                    double curDiff = Math.abs(charBrightnessMap.get(key) - subImagesAvg[i][j]);
                    if(curDiff < bestDiff){
                        bestDiff = curDiff;
                        best_char = key;
                    }
                }
                final_picture[i][j] = best_char;
            }
        }
        return final_picture;
    }

    private void CalcCharBrightness(HashMap<Character, Double> charBrightnessMap, int subPictureSize,
                                    Character[] chars) {
        for(Character c: chars){
            boolean [][] charset = CharRenderer.getImg(c, subPictureSize, fontName);
            charBrightnessMap.put(c, countWhitesAndDiv(charset));
        }
        double maxBright = 0;
        double minBright = 1;
        for (double d: charBrightnessMap.values()){
            if(d < minBright){
                minBright = d;
            }
            if (d > maxBright){
                maxBright = d;
            }
        }
        for(Character key: charBrightnessMap.keySet()){
            charBrightnessMap.put(key, newCharBrightness(charBrightnessMap.get(key),
                    minBright, maxBright));
        }
    }

    /**
     * calculates each sub image avg brightness and returns an array that holds these values
     * @param subImageSize the width of a sub message
     * @param numCharsInRow the number of columns
     * @param numCharsInCol the number of rows
     * @return 2d array of double
     */
    private double[][] getAvgForImage(int subImageSize, int numCharsInRow, int numCharsInCol) {
        double [][] array = new double[numCharsInCol][numCharsInRow];
        Iterator<Color> iter = image.pixels(subImageSize).iterator();
        for (int i = 0; i < numCharsInCol; i++) {
            for (int j = 0; j < numCharsInRow; j++) {
                double sum = 0;
                int count = 0; // Cant do more than size of subImageSize ^2
                while (iter.hasNext()){
                    Color pixel = iter.next();
                    sum += getGrayValue(pixel);
                    count++;
                    if(count >= subImageSize * subImageSize){
                        break;
                    }
                }
                array[i][j] = sum / ((subImageSize * subImageSize) * MAX_RGB);
            }
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
        for (int i = 0; i < chars.length; i++) {
            for (int j = 0; j < chars[i].length; j++) {
                if(chars[i][j]){
                    count_whites += 1;
                }
            }
        }
        return count_whites / RES_NORMAL;
    }

//    /**
//     * calculates the norm of whites according to 16 pixels
//     * @param count_whites the num of white pixels
//     * @return the norm
//     */
//    private double normWhites(double count_whites){
//        return count_whites / RES_NORMAL;
//    }

    //TODO TODO: figure out how to get all the values
    /**
     * return new sub image brightness according to phase 3
     * @param char_brightness
     * @param min_brightness
     * @param max_brightness
     * @return
     */
    private double newCharBrightness(double char_brightness, double min_brightness, double max_brightness){
        return (char_brightness - min_brightness) / (max_brightness - min_brightness);
    }


//    /**
//     * calculates the Avg brightness of a given subImage
//     * @param subImage A 2D array of color representing a sub image
//     * @return Avg brightness
//     */
//    private double subImageAvg(Color [][] subImage){
//        double sum = 0;
//        for (int i = 0; i < subImage.length; i++) {
//            for (int j = 0; j < subImage[i].length; j++) {
//                sum += getGrayValue(subImage[i][j]);
//            }
//        }
//        return sum / ((subImage.length * subImage.length) * MAX_RGB);
//    }

    /**
     * gets color and returns its gray value
     * @param color the pixel color
     * @return gray pixel
     */
    private double getGrayValue(Color color){
        return color.getRed() * 0.2126 + color.getGreen() * 0.7152 + color.getBlue() * 0.0722;
    }
}
