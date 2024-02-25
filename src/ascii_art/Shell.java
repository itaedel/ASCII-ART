package ascii_art;

import ascii_art.img_to_char.BrightnessImgCharMatcher;
import ascii_output.ConsoleAsciiOutput;
import ascii_output.HtmlAsciiOutput;
import image.Image;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

/**
 * A class for getting user input
 */
public class Shell {
    private static final int DEF_48 = 48;
    private static final int NUM_OF_CHARS = 255;
    private static final String STOP_WORD = "exit";
    private static final String NOT_REMOVE_ERR = "Did not remove due to incorrect format";
    private static final String NOT_EXECUTED_ERR = "Did not executed due to incorrect command";
    private static final String NOT_ADD_ERR = "Did not add due to incorrect format";
//    private static final String [] ACCEPTED_WORDS = {"exit","add","remove","res","console",
//            "render","chars"};
    private static final int INITIAL_CHARS_IN_ROW = 64;
    private static final int MIN_ASCI = 32;
    private static final int MAX_ASCI = 126;
    private static final String NO_CHARS_FOR_RENDER = "There are no chars for the rendering";
    private Set<Character> ourChars = new HashSet<>();
    private final Image image;
    private Scanner scanner;
    private boolean isConsole;
    private static final int MIN_PIXELS_PER_CHAR = 2;
    private static final String ARROWS = ">>> ";
    private final int minCharsInRow;
    private final int maxCharsInRow;
    private int charsInRow;
    private final BrightnessImgCharMatcher brightnessImgCharMatcher;
    private static final String FONT = "Courier New";
    private static final String FILENAME = "out.html";

    /**
     * constructor
     * @param img the image
     */
    public Shell(Image img){
        this.image = img;
        this.scanner = new Scanner(System.in);
        for (int i = DEF_48; i <= DEF_48 + 9; i++) { // default 0-9
            ourChars.add((char)i);
        }
        this.minCharsInRow = Math.max(1, img.getWidth()/img.getHeight());
        this.maxCharsInRow = img.getWidth() / MIN_PIXELS_PER_CHAR;
        this.charsInRow = Math.max(Math.min(INITIAL_CHARS_IN_ROW, maxCharsInRow), minCharsInRow);
        this.isConsole = false;
        this.brightnessImgCharMatcher = new BrightnessImgCharMatcher(img, FONT);
    }

    /**
     * a method that gets input from user
     * if a single Enter - goes down a line and tries again and if another enter then prints error
     */
    public void run(){
        String input = "";
        while(!input.equals(STOP_WORD)){
            System.out.print(ARROWS);
            input = scanner.nextLine();
            String [] words = input.split(" ");
            if(input.isEmpty()){
                input = scanner.nextLine();
                if(input.isEmpty()){
                    System.out.println(NOT_EXECUTED_ERR);
                    continue;
                }
                words = input.split(" ");
            }
            if (input.charAt(0) == ' '){
                System.out.println(NOT_EXECUTED_ERR);
            } else {
                inputCheck(words);
            }
        }
    }

    /**
     * helper function for checking the user input
     * @param words the input divided into an array
     */
    private void inputCheck(String [] words){
        if(words[0].equals("exit")){
            scanner.close();
        } else if (words[0].equals("add")){
            checkAddConditions(words);
        } else if (words[0].equals("remove")){
            checkRemoveConditions(words);
        } else if (words[0].equals("res")) {
            checkResConditions(words);
        } else if (words[0].equals("console")) {
            setConsole();
        } else if (words[0].equals("render")) {
            renderImage();
        } else if (words[0].equals("chars")) {
            printChars();
        } else {
            System.out.println(NOT_EXECUTED_ERR);
        }
    }

    /**
     * checks remove conditions
     * @param words the args received by the user in the terminal
     */
    private void checkRemoveConditions(String[] words) {
        if(words.length != 2){
            System.out.println(NOT_REMOVE_ERR);
        } else {
            remove(words[1]);
        }
    }

    /**
     * checks add conditions
     * @param words the args received by the user in the terminal
     */
    private void checkAddConditions(String[] words) {
        if (words.length != 2){
            System.out.println(NOT_ADD_ERR);
        } else{
            add(words[1]);
        }
    }

    /**
     * checks res conditions
     * @param words the args received by the user in the terminal
     */
    private void checkResConditions(String[] words) {
        if (words.length == 2){
            changeRes(words[1]);
        } else{
            System.out.println(NOT_EXECUTED_ERR);
        }
    }

    /**
     * renders image if there are enough chars > 1
     */
    private void renderImage() {
        if(ourChars.size() == 0){
            System.out.println(NO_CHARS_FOR_RENDER);
            return;
        }
        Character[] arr = new Character[ourChars.size()];
        ourChars.toArray(arr);
        char [][] finalImage = brightnessImgCharMatcher.chooseChars(charsInRow, arr);
        if (isConsole){
            new ConsoleAsciiOutput().output(finalImage);
        } else {
            new HtmlAsciiOutput(FILENAME, FONT).output(finalImage);
        }
    }

    /**
     * sets the console to work
     */
    private void setConsole() {
        this.isConsole = true;
    }

    /**
     * change the resolution of the image - meaning changing the number of sub images
     * @param word the user input - should be "res up" or "res down"
     */
    private void changeRes(String word) {
        if(word.equals("up")){
            if(charsInRow * 2 <= maxCharsInRow){
                charsInRow *= 2;
                System.out.println("Width set to " + charsInRow);
            } else {
                System.out.println("Did not change due to exceeding boundaries");
            }
        } else if (word.equals("down")) {
            if(charsInRow / 2 >= minCharsInRow){
                charsInRow /= 2;
                System.out.println("Width set to " + charsInRow);
            } else {
                System.out.println("Did not change due to exceeding boundaries");
            }
        } else {
            System.out.println(NOT_EXECUTED_ERR);
        }
    }

    /**
     * prints all the chars in the set
     */
    private void printChars(){
        for (Character c: ourChars){
            System.out.print(c + " ");
        }
        System.out.println();
    }

    /**
     * adds the chars in range and checking the conditions
     * @param range the range of chars to add in format x-y, if not prints error
     */
    private void add(String range){
        if(range.equals("all")){
            addRange((char) MIN_ASCI, (char) MAX_ASCI);
        } else if (range.equals("space")) {
            ourChars.add(' ');
        } else if (range.length() == 1) {
            ourChars.add(range.charAt(0));
        } else if (range.length() == 3 && range.charAt(1) == '-') {
            if((int)range.charAt(0) < (int)range.charAt(2)){
                addRange(range.charAt(0), range.charAt(2));
            } else {
                addRange(range.charAt(2), range.charAt(0));
            }
        } else {
            System.out.println(NOT_ADD_ERR);
        }
    }

    /**
     * adds the chars in range
     * @param a the first char in range
     * @param b the last char in range
     */
    private void addRange(char a, char b){
        for (int i = a; i <= (int)b; i++) {
            ourChars.add((char)i);
        }
    }

    /**
     * tries to remove the chars in range and checks conditions
     * @param range the word describing the range
     */
    private void remove(String range){
        if(range.equals("all")){
            removeRange((char) 0,(char) NUM_OF_CHARS);
        } else if (range.equals("space")) { // removes space
            ourChars.remove(' ');
        } else if (range.length() == 1) { // one char
            ourChars.remove(range.charAt(0));
        } else if (range.length() == 3 && range.charAt(1) == '-') {
            if((int)range.charAt(0) < (int)range.charAt(2)){
                removeRange(range.charAt(0), range.charAt(2));
            } else {
                removeRange(range.charAt(2), range.charAt(0));
            }
        } else { // prints error
            System.out.println(NOT_REMOVE_ERR);
        }
    }

    /**
     * removes the chars in range
     * @param a the first char in range
     * @param b the last char in range
     */
    private void removeRange(char a, char b){
        for (int i = a; i <= (int)b; i++) {
            ourChars.remove((char)i);
        }
    }
}
