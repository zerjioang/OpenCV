import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class DisplayCam {

	public static void main(String[] args) {
		OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
		try {
			grabber.start();
			IplImage frame = grabber.grab();
			CanvasFrame canvasFrame = new CanvasFrame("Camara");
	        canvasFrame.setCanvasSize(frame.width(), frame.height());
	        canvasFrame.setLocationRelativeTo(null);
	        canvasFrame.setDefaultCloseOperation(CanvasFrame.DISPOSE_ON_CLOSE);
	        while((frame = grabber.grab()) != null && canvasFrame.isVisible()){
	        	canvasFrame.showImage(frame);
	        }
	        grabber.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
