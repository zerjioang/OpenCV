import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_core.*;

public class DisplayCamAndFlip {
	
	public static void main(String[] args) {
		OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
		try {
			grabber.start();
			IplImage frame = grabber.grab();
			CanvasFrame canvasFrame = new CanvasFrame("Imagen de Webcam");
	        canvasFrame.setCanvasSize(frame.width(), frame.height());
	        canvasFrame.setLocationRelativeTo(null);
	        canvasFrame.setDefaultCloseOperation(CanvasFrame.DISPOSE_ON_CLOSE);
	        
	        while((frame = grabber.grab()) != null && canvasFrame.isVisible()){
	        	//0 invertir verticamente
	        	//1 invertir horizontalmente
	        	//-1 invertir horizontalmente y verticalmente
	        	cvFlip(frame, frame, 1);
	        	IplImage hsv = IplImage.create(frame.width(),frame.height(), IPL_DEPTH_8U, 3);
	        	cvCvtColor(frame, hsv, CV_RGB2HLS);
	        	canvasFrame.showImage(hsv);
	        }
	        grabber.stop();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
