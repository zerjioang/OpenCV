import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_features2d.DrawMatchesFlags;
import com.googlecode.javacv.cpp.opencv_features2d.KeyPoint;
import com.googlecode.javacv.cpp.opencv_nonfree.SURF;

import static com.googlecode.javacv.cpp.opencv_features2d.drawKeypoints;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvShowImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvWaitKey;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_core.*;

public class VideoSURF {
	
	public static void main(String[] args) {
		OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
		try {
			grabber.start();
			IplImage frame = grabber.grab();
			CanvasFrame canvasFrame = new CanvasFrame("Imagen de Webcam - SURF");
	        canvasFrame.setCanvasSize(frame.width(), frame.height());
	        canvasFrame.setLocationRelativeTo(null);
	        canvasFrame.setDefaultCloseOperation(CanvasFrame.DISPOSE_ON_CLOSE);
	        
	        while((frame = grabber.grab()) != null && canvasFrame.isVisible()){
	        	//0 invertir verticamente
	        	//1 invertir horizontalmente
	        	//-1 invertir horizontalmente y verticalmente
	        	cvFlip(frame, frame, 1);
	        	frame = surfData(frame);
	        	canvasFrame.showImage(frame);
	        }
	        grabber.stop();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	private static IplImage surfData(IplImage image) {
	    // Detect SURF features.
	    KeyPoint keyPoints = new KeyPoint();
	    double hessianThreshold = 1000d; //2500d
	    int nOctaves = 12; //4
	    int nOctaveLayers = 2;  //2
	    boolean extended = true;
	    boolean upright = false;
	    SURF surf = new SURF(hessianThreshold, nOctaves, nOctaveLayers, extended, upright);
	    surf.detect(image, null, keyPoints);
	    // Draw keyPoints
	    IplImage featureImage = IplImage.create(image.cvSize(), image.depth(), 3);
	    drawKeypoints(image, keyPoints, featureImage, CvScalar.WHITE, DrawMatchesFlags.DEFAULT);
	    return featureImage;
	
	}
}
