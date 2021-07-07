package com.example.human_detection;

import android.graphics.Bitmap;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;


public class HumanDetect {

    CascadeClassifier humanBodyClassifier;

    public HumanDetect(String classifierPath) {

        humanBodyClassifier = new CascadeClassifier(classifierPath);
    }

    public Bitmap detect(Bitmap bmp, boolean basedCanny) {

        // IMAGE CROPPING
        Mat image = new Mat();

        Utils.bitmapToMat(bmp, image);

        MatOfRect body = new MatOfRect();

        humanBodyClassifier.detectMultiScale(image, body);

        Rect rect = body.toArray()[0];

        bmp = Bitmap.createBitmap(bmp, rect.x, rect.y, rect.width, rect.height);


        if (basedCanny) {
            return edgeDetectionWithCandy(bmp);
        } else {
            return edgeDetectionWithCustomFunction(bmp);
        }

    }

    public Bitmap edgeDetectionWithCandy(Bitmap bmp) {

        Mat cropped = new Mat();

        Utils.bitmapToMat(bmp, cropped);

        Mat img_canny = new Mat();

        Imgproc.Canny(cropped, img_canny, 80, 90);

        Bitmap output = Bitmap.createBitmap(img_canny.width(), img_canny.height(), Bitmap.Config.ARGB_8888);

        Utils.matToBitmap(img_canny, output);

        return output;

    }


    public static Bitmap edgeDetectionWithCustomFunction(Bitmap image) {

        int x = image.getWidth();
        int y = image.getHeight();
        int[][] colorsOfEdges = new int[x][y];
        int maxGr = -1;


        for (int i = 1; i < x - 1; i++) {
            for (int j = 1; j < y - 1; j++) {


                int v = grayScale(image.getPixel(i - 1, j - 1));
                int v1 = grayScale(image.getPixel(i - 1, j));
                int v2 = grayScale(image.getPixel(i - 1, j + 1));
                int v3 = grayScale(image.getPixel(i, j - 1));

                int v4 = grayScale(image.getPixel(i, j + 1));
                int v5 = grayScale(image.getPixel(i + 1, j - 1));
                int v6 = grayScale(image.getPixel(i + 1, j));
                int v7 = grayScale(image.getPixel(i + 1, j + 1));

                int gx = (v2 + v4 + v7) - (v + v3 + v5);

                int gy = (v5 + 2 * v6 + v7) - (v + 2 * v1 + v2);


                int g = (int) Math.sqrt((gx * gx) + (gy * gy));

                if (maxGr < g)
                    maxGr = g;

                colorsOfEdges[i][j] = g;
            }
        }

        double scale = 255.0 / maxGr;

        for (int i = 1; i < x - 1; i++) {
            for (int j = 1; j < y - 1; j++) {

                int color = colorsOfEdges[i][j];
                color = (int) (color * scale);
                color = 0xff000000 | (color << 16) | (color << 8) | color;
                image.setPixel(i, j, color);
            }
        }


        return image;
    }

    private static int grayScale(int rgb) {
        int R = (rgb >> 16) & 0xff;
        int G = (rgb >> 8) & 0xff;
        int B = (rgb) & 0xff;

        // grayscale calculation
        return (int) (0.2126 * R + 0.7152 * G + 0.0722 * B);
    }


}






