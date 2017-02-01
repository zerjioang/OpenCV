import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_features2d.*;
import static com.googlecode.javacv.cpp.opencv_nonfree.*;

import java.awt.Color;

public class SURFdetector {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Read input image
		IplImage image = cvLoadImage("src/img/imgbush.bmp");
	    // Detect SURF features.
	    KeyPoint keyPoints = new KeyPoint();
	    double hessianThreshold = 2500d;
	    int nOctaves = 4;
	    int nOctaveLayers = 2;
	    boolean extended = true;
	    boolean upright = false;
	    SURF surf = new SURF(hessianThreshold, nOctaves, nOctaveLayers, extended, upright);
	    surf.detect(image, null, keyPoints);
	    // Draw keyPoints
	    IplImage featureImage = IplImage.create(image.cvSize(), image.depth(), 3);
	    drawKeypoints(image, keyPoints, featureImage, CvScalar.WHITE, DrawMatchesFlags.DRAW_RICH_KEYPOINTS);
	    cvShowImage("SURF Features", featureImage);
	    cvWaitKey();
	}

}
