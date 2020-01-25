
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class contains class (static) methods
 * that will help you test the Picture class
 * methods.  Uncomment the methods and the code
 * in the main to test.  This is a great lesson for learning
 * about 2D arrays and the Color class.
 *
 * @author Barbara Ericson
 */
public class PictureTester
{
    
    /** Main method for testing.  Every class can have a main
     * method in Java */
    public static void main(String[] args)
    {
        long start = System.nanoTime();
            testFixUnderwater();
        long time = System.nanoTime() - start;
        System.out.println("Took: " + (time)/1000000 + " ms");
    }
    

    /** This method is an effort to make a Picture taken underwater look
    * more like it would be if the water were drained
    */
    private static void testFixUnderwater() {
        Picture water = new Picture("skinny.jpg");
        //Picture water = new Picture("BeforeAndAfter_large.jpg");
        //Picture water = new Picture("AquariumDiver.jpg");
        //Picture water = new Picture("barracuda.jpg");
        
        water.explore(); //before
        
        
        //water.grayScale(); //makes the photo grayscale
        /* find top 10 brightest pixels.
         * Then, average the hues
         * Then adjust the hues of all the pixels
         */
                
        
        Pixel[][] pixels = water.getPixels2D();
        List<float[]> brightnessArray = new ArrayList<>();

        for (Pixel[] pixelArray : pixels) { // white balances the photo
            for(Pixel pixel : pixelArray) {
                Color currColor = pixel.getColor();
                int currRed = currColor.getRed();
                int currBlue = currColor.getBlue();
                int currGreen = currColor.getGreen();
                
                float[] hsbvals = new float[3];
                
                Color.RGBtoHSB(currRed, currGreen, currBlue, hsbvals);
                
                //float currVal = hsbvals[2];
                
                brightnessArray.add(hsbvals);
            }
            float bestValSoFar = 0;
            float worstValSoFar = 255;
            int indexOfWorstValSoFar = -1;
//            float bestValSoFar = 255;
            int indexOfBestValSoFar = -1;
            for (float[] currPix : brightnessArray) {
                float currVal = currPix[2];
                if (currVal > bestValSoFar) {
//                if (currVal < bestValSoFar) {
                    indexOfBestValSoFar = brightnessArray.indexOf(currPix);
                    bestValSoFar = currVal;
                }
                if (currVal < worstValSoFar) {
                    indexOfWorstValSoFar = brightnessArray.indexOf(currPix);
                    worstValSoFar = currVal;
                }
            }
            
            
            float[] bestArray = brightnessArray.get(indexOfBestValSoFar);
            float[] worstArray = brightnessArray.get(indexOfWorstValSoFar);
            
            float avgHue = bestArray[0];
            
            //Collections.sort(brightnessArray);
            //float avgHue = brightnessArray.get(brightnessArray.size()-1);
            
            for (Pixel pixel11 : pixelArray) {
                Color currColor = pixel11.getColor();
                int currRed = currColor.getRed();
                int currBlue = currColor.getBlue();
                int currGreen = currColor.getGreen();
                
                float[] hsbvals = new float[3];
                
                Color.RGBtoHSB(currRed, currGreen, currBlue, hsbvals);
                
                float currSat = hsbvals[1];
                float currVal = hsbvals[2];
                
                Color newColor = currColor.getHSBColor(avgHue, currSat*15/12, currVal);
                pixel11.setColor(newColor);
            }
    
        }
//        Pixel[][] pixels0 = water.getPixels2D();
//        for (Pixel[] pixelArray : pixels0) {
//            for (Pixel pixel : pixelArray) {
//                Color currColor = pixel.getColor();
//                int currRed = currColor.getRed();
//                int currBlue = currColor.getBlue();
//                int currGreen = currColor.getGreen();
//
//                pixel.setRed(currRed*14/13);
                //pixel.setBlue(currBlue*24/28);
                //pixel.setGreen(currGreen*27/28);
                

//                if (pixel.getBlue() > 40) {
//                    pixel.setBlue(currBlue - 40);
//                } else {
//                    pixel.setBlue(currBlue*10/13);
//                }
//                if (pixel.getGreen() > 40) {
//                    pixel.setGreen(currGreen - 40);
//                } else {
//                    pixel.setBlue(currGreen*22/23);
//                }
//                if (pixel.getRed() < (255-30)) {
//                    pixel.setRed(currRed + 30);
//                } else {
//                    pixel.setRed(currRed*24/23);
//
//                }
//            }
//        }
        
        water.explore();
    }

    

}
