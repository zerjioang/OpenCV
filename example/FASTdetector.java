import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_features2d.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.googlecode.javacpp.*;
import com.googlecode.javacv.cpp.opencv_features2d.KeyPoint;
import com.googlecode.javacv.cpp.opencv_core.*;
import com.googlecode.javacv.cpp.opencv_features2d.*;
import com.googlecode.javacv.cpp.opencv_video.*;

public class FASTdetector {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Read input image
	    IplImage image = cvLoadImage("src/img/imgbush.bmp");

	    // Detect FAST features
	    FeatureDetector ffd = new FastFeatureDetector(40, true);
	    KeyPoint kp = new KeyPoint();
	    ffd.detect(image, kp, null);
	    image = escribirCirculos(image, kp);
	    cvShowImage("Imagen FAST", image);
	    cvWaitKey();
	}

	private static IplImage escribirCirculos(IplImage image, KeyPoint kp) {
		// OpenCV drawing seems to crash a lot, so use Java2D
        int minR = 2;
        BufferedImage bi = image.getBufferedImage();
        Graphics g2d = bi.getGraphics();
        g2d.setColor(Color.CYAN);
        for (int i = 0; i < kp.sizeof(); i++) {
        	kp = kp.position(i);
			int radius = (int) (kp.size()/2);
			int r;
			if(radius == Float.NaN || radius < minR)
				r = minR;
			else
				r = radius;
			CvPoint2D32f pt = kp.pt();
			g2d.drawOval((int)pt.x() - r, (int)pt.y() - r, r * 2, r * 2);
		}
		return IplImage.createFrom(bi);

	}


}
