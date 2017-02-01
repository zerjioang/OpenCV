import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import com.googlecode.javacv.*;
import com.googlecode.javacv.cpp.opencv_core.*;
import com.googlecode.javacv.cpp.opencv_features2d.KeyPoint;
import static com.googlecode.javacv.cpp.opencv_features2d.*;
import static com.googlecode.javacv.cpp.opencv_core.*;

public class VideoGOOD {
	
	public static void main(String[] args) {
		OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
		try {
			grabber.start();
			IplImage frame = grabber.grab();
			CanvasFrame canvasFrame = new CanvasFrame("Imagen de Webcam - Good Features to Track");
	        canvasFrame.setCanvasSize(frame.width(), frame.height());
	        canvasFrame.setLocationRelativeTo(null);
	        canvasFrame.setDefaultCloseOperation(CanvasFrame.DISPOSE_ON_CLOSE);
	        
	        while((frame = grabber.grab()) != null && canvasFrame.isVisible()){
	        	//0 invertir verticamente
	        	//1 invertir horizontalmente
	        	//-1 invertir horizontalmente y verticalmente
	        	cvFlip(frame, frame, 1);
	        	frame = GFTTData(frame);
	        	canvasFrame.showImage(frame);
	        }
	        grabber.stop();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	private static IplImage GFTTData(IplImage image) {
		
		FeatureDetector  gftt = new GFTTDetector(
		        5000 /* maximum number of corners to be returned */ ,
		        0.01 /* quality level*/ ,
		        10.0 /* minimum allowed distance between points*/ ,
		        5 /* block size*/ ,
		        false /* use Harris detector*/ ,
		        0.04 /* Harris parameter */
		    );
		KeyPoint keyPoints = new KeyPoint();
		gftt.detect(image, keyPoints, null);
	    // Draw keyPoints
		return escribirCirculos(image, keyPoints);
	}
	private static IplImage escribirCirculos(IplImage image, KeyPoint kp) {
		// OpenCV drawing seems to crash a lot, so use Java2D
        int minR = 2;
        BufferedImage bi = image.getBufferedImage();
        Graphics g2d = bi.getGraphics();
        g2d.setColor(Color.WHITE);
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
