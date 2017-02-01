import java.io.File;
import com.googlecode.javacv.*;
import com.googlecode.javacv.cpp.opencv_core.*;

public class GuardarVideo {

	public static void main(String[] args) {
		OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
		File file = new File("C:\\Documents and Settings\\Administrador\\Escritorio\\video.avi");
		int imageWidth=640;
		int imageHeight=480;
		OpenCVFrameRecorder recorder = new OpenCVFrameRecorder(file, imageWidth, imageHeight);
		recorder.setFrameRate(20);
		recorder.setVideoBitrate(1000);
		recorder.setVideoCodec(0);
		try {
			grabber.start();
			recorder.start();
			IplImage frame = grabber.grab();
			imageWidth = frame.width();
			imageHeight = frame.height();
			CanvasFrame canvasFrame = new CanvasFrame("Capturando video de webcam...");
	        canvasFrame.setCanvasSize(frame.width(), frame.height());
	        canvasFrame.setLocationRelativeTo(null);
	        canvasFrame.setDefaultCloseOperation(CanvasFrame.DISPOSE_ON_CLOSE);
	        while((frame = grabber.grab()) != null && canvasFrame.isVisible()){
	        	canvasFrame.showImage(frame);
	        	recorder.record(frame);
	        }
	        grabber.stop();
	        recorder.stop();
	        canvasFrame.dispose();
		} catch (Exception e) {
			System.out.println("EXCEPTION - "+e.getMessage());
		}
	}

}
