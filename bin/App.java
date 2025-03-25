import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;

public class App {

    final static Integer[] xAdjust = { 1, 1, 0, -1, -1, -1, 0, 1, 0 };
    final static Integer[] yAdjust = { 0, 1, 1, 1, 0, -1, -1, -1, 0 };
    final static Integer[] BlurKernel = { 2, 1, 2, 1, 2, 1, 2, 1, 4 };

    static String FilePath = "src\\RawImage.png";
    static String ReturnPath = "src\\ProcessedImage.png";

    public static void main(String[] args) throws Exception {

        Scanner scan = new Scanner(System.in);
        String process = "0", intensity = "0", inPath, outPath, outName, check;

        while (true){
            System.out.println(
                        "Choose Program: " +
                        "\nType 1 to make Image grayscale" +
                        "\nType 2 to blur Image" +
                        "\nType 3 blur and grayscale Image" +
                        "\nPress Enter to Cancel");

            process = scan.next();
            if (process.equals("")) {
                scan.close();
                return;
            } else if (process.equals("2") || process.equals("3")) {
                System.out.println("Type Intensity of blur between 1 and 15\nPress Enter to Exit");
                intensity = scan.next();
                if (intensity.equals("")) {
                    scan.close();
                    return;
                }
            }

            System.out.println("Paste Raw Image File Path\nPress Enter to Exit");
            inPath = scan.next();
            if (inPath.equals("")) {
                scan.close();
                return;
            }

            System.out.println("Paste Output Image Folder File Path\nPress Enter to Exit");
            outPath = scan.next();
            if(outPath.equals("")){
                scan.close();
                return;    
            }

            System.out.println("Enter Output Image File Name\nPress Enter to Exit");
            outName = scan.next();
            if (outName.equals("")) {
                scan.close();
                return;
            } else if (outPath.charAt(outPath.length() - 1) == '\\') {
                outPath += outName + ".png";
            } else {
                outPath += "\\" + outName + ".png";
            }

            try {
                File sourcePath = new File(inPath);
                File returnPath = new File(outPath);
                BufferedImage sourceImage = ImageIO.read(sourcePath);
                BufferedImage returnImage = ProcessImage(sourceImage, Integer.parseInt(process), Integer.parseInt(intensity));
                ImageIO.write(returnImage, "png", returnPath);
            } catch (IOException e) {
                System.out.println(e);
                e.printStackTrace();
                System.out.println("Type R to restart, Press Enter to exit");
                check = scan.next();
                if(check.equals("")){
                    scan.close();
                    return;
                }
            }
        }   
    }

    static BufferedImage ProcessImage(BufferedImage sourceImage, int process, int Intensity) {
        BufferedImage returnImage = sourceImage;
        if (process == 1) {
            returnImage = makeGrayScale(returnImage);
        } else if (process == 2) {
            for (int i = 0; i < Intensity; i++) {
                returnImage = ThreeConvolute(returnImage, BlurKernel);
            }
        } else if (process == 3) {
            for (int i = 0; i < Intensity; i++) {
                returnImage = ThreeConvolute(returnImage, BlurKernel);
            }
            returnImage = makeGrayScale(returnImage);
        }
        return returnImage;
    }

    static BufferedImage ThreeConvolute(BufferedImage sourceImage, Integer[] Kernel) {
        BufferedImage returnImage = sourceImage;
        Integer sourceKernel[][] = new Integer[9][3];
        Integer returnPixel[] = { 0, 0, 0 };
        int sum = 0;

        for (int i = 0; i < sourceImage.getWidth(); i++) {
            for (int j = 0; j < sourceImage.getHeight(); j++) {
                for (int k = 0; k < 9; k++) {

                    if (i + xAdjust[k] >= 0 && i + xAdjust[k] < sourceImage.getWidth()) {
                        if (j + yAdjust[k] >= 0 && j + yAdjust[k] < sourceImage.getHeight()) {
                            sourceKernel[k] = extractRGB(i + xAdjust[k], j + yAdjust[k], sourceImage);
                            for (int m = 0; m < 3; m++) {
                                returnPixel[m] += sourceKernel[k][m] * Kernel[k];
                            }
                            sum = Kernel[k] + sum;
                        }
                    }
                }

                if (sum != 0) {
                    for (int m = 0; m < 3; m++) {
                        returnPixel[m] /= sum;
                    }
                }
                returnImage.setRGB(i, j, encodeRGB(returnPixel));
                sourceKernel = new Integer[9][3];
                returnPixel = new Integer[] { 0, 0, 0 };
                sum = 0;
            }
        }

        return returnImage;
    }

    static BufferedImage makeGrayScale(BufferedImage sourceImage) { // Functional
        BufferedImage returnImage = sourceImage;
        Integer[] sourcePixel = { 0, 0, 0 };
        Integer[] returnPixel = { 0, 0, 0 };
        int grayScalePixel = 0;

        for (int i = 0; i < sourceImage.getWidth() - 1; i++) {
            for (int j = 0; j < sourceImage.getHeight() - 1; j++) {
                sourcePixel = extractRGB(i, j, sourceImage);
                for (int k = 0; k < 3; k++) {
                    grayScalePixel += sourcePixel[k];
                }
                grayScalePixel /= 3;
                for (int k = 0; k < 3; k++) {
                    returnPixel[k] = grayScalePixel;
                }
                returnImage.setRGB(i, j, encodeRGB(returnPixel));
                sourcePixel = new Integer[] { 0, 0, 0 };
                returnPixel = new Integer[] { 0, 0, 0 };
                grayScalePixel = 0;
            }
        }
        return returnImage;
    }

    static Integer[] extractRGB(int x, int y, BufferedImage sourceImage) { // Functional
        int sourceRGB = sourceImage.getRGB(x, y);
        Integer[] returnRGB = {
                (sourceRGB >> 16) & 0xFF,
                (sourceRGB >> 8) & 0xFF,
                (sourceRGB) & 0xFF
        };
        return returnRGB;
    }

    static int encodeRGB(Integer[] sourceRGB) { // Functional;
        int returnRGB = sourceRGB[0];
        returnRGB = (returnRGB << 8) + sourceRGB[1];
        returnRGB = (returnRGB << 8) + sourceRGB[2];
        return returnRGB;
    }

}