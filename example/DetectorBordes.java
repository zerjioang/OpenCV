import com.googlecode.javacv.CanvasFrame;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class DetectorBordes {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
		try {
			grabber.start();
			IplImage frame = grabber.grab();
			CanvasFrame canvasFrame = new CanvasFrame("Deteccion de bordes - Fuente: Webcam");
	        canvasFrame.setCanvasSize(frame.width(), frame.height());
	        canvasFrame.setLocationRelativeTo(null);
	        canvasFrame.setDefaultCloseOperation(CanvasFrame.DISPOSE_ON_CLOSE);
	        
	        while((frame = grabber.grab()) != null && canvasFrame.isVisible()){
	        	//convertir imagen a escala de grises
	        	cvFlip(frame, frame, 1);
	        	IplImage gris = IplImage.create(frame.cvSize(), IPL_DEPTH_8U, 1);
	        	cvCvtColor(frame, gris, CV_RGB2GRAY);
	        	cvEqualizeHist(gris, gris);
	        	IplImage out = IplImage.create(frame.cvSize(), IPL_DEPTH_8U, 1);
	        	cvCanny(gris, out, 70,80, 3);
	        	canvasFrame.showImage(out);
	        }
	        grabber.stop();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
