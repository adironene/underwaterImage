import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

public class OptimizedWaterEnhance {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {

        String imgPath = "src/main/resources/underwater_images/scene.jpg";
        Mat image = Imgcodecs.imread(imgPath, 5);
        int height = image.rows();
        int width = image.cols();
        showImage(image, "before", width, height);
        long start = System.nanoTime();


        Mat test = ColorBalance(image, 5);
        test.convertTo(test, CvType.CV_8UC1);
        long time = System.nanoTime() - start;
        System.out.println("Took: " + (time) / 1000000 + " ms");
        showImage(test, "after", width, height);

    }

    private static void showImage(Mat img, String title, int width, int height) {
        JFrame window = new JFrame(title);
        ImageIcon image = new ImageIcon();
        JLabel label = new JLabel();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        label.setIcon(image);
        window.getContentPane().add(label);
        window.setResizable(true);
        //Imgproc.resize(img, img, new Size(width, height));
        Imgproc.resize(img, img, new Size(640, 480));

        try {
            MatOfByte mob = new MatOfByte();
            Imgcodecs.imencode(".jpg", img, mob);
            byte ba[] = mob.toArray();
            BufferedImage bufImage = ImageIO.read(new ByteArrayInputStream(ba));
            image.setImage(bufImage);
            window.pack();
            label.updateUI();
            window.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Mat ColorBalance(Mat img, int percent) {

        Imgproc.resize(img, img, new Size(300, 169));
        //img.convertTo(img, CvType.CV_32F);
        //showImage(img, "after", img.width(), img.height());
        int ro = img.rows();
        System.out.println("row is ");
        int co = img.cols();
        int chan = img.channels();
        System.out.println("ROWS = " + ro + " COLS = " + co + " CHANNELS = " + chan);
        double thirdPercent = percent / 25.0;
        List<Mat> channels = new ArrayList<>();
        if (chan == 3) Core.split(img, channels);
        else channels.add(img);
        List<Mat> results = new ArrayList<>();
        for (int i = 0; i < chan; i++) {
            // find the low and high precentile values (based on the input percentile)
            Mat flat = new Mat();
            channels.get(i).reshape(1, 1).copyTo(flat);
            Core.sort(flat, flat, Core.SORT_ASCENDING);
            double lowVal = flat.get(0, (int) Math.floor(flat.cols() * thirdPercent))[0];
            double topVal = flat.get(0, (int) Math.ceil(flat.cols() * (1.0 - thirdPercent)))[0];
            // saturate below the low percentile and above the high percentile
            Mat channel = channels.get(i);
            for (int m = 0; m < ro; m++) {
                for (int n = 0; n < co; n++) {
                    if (channel.get(m, n)[0] < lowVal)
                        channel.put(m, n, lowVal);
                    if (channel.get(m, n)[0] > topVal)
                        channel.put(m, n, topVal);
                }
            }

            Core.normalize(channel, channel, 0.0, 255.0 / 2, Core.NORM_MINMAX);
//            channel.convertTo(channel, CvType.C_32F);
            results.add(channel);
        }

        Mat outval = new Mat();
        Core.merge(results, outval);
        return outval;
    }
}
