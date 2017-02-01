import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class ThresholdImage {

	private static int erode_value = 0;
	private static int dilate_value = 0;
	private static int smooth_level = 0;
	private static double satLower = 0;
	private static double vlow = 0;
	private static double satHigh = 0;
	private static double vHigh = 0;
	private static double hueLowerR = 0;
	private static double hueUpperR = 0;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Creador de imagen Threshold");
		OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
		try {
			grabber.start();
			IplImage frame = grabber.grab();
			CanvasFrame canvasFrame = new CanvasFrame("Imagen de Webcam");
	        canvasFrame.setCanvasSize(frame.width(), frame.height());
	        canvasFrame.setLocationRelativeTo(null);
	        canvasFrame.setDefaultCloseOperation(CanvasFrame.DISPOSE_ON_CLOSE);
	        Barras b = new Barras(canvasFrame.isVisible(), hueLowerR, hueUpperR, satHigh, satLower, vHigh, vlow, smooth_level, erode_value, dilate_value);
	        b.start();
	        while((frame = grabber.grab()) != null && canvasFrame.isVisible()){
	        	cvFlip(frame, frame, 1);
	        	hueLowerR = b.getLow();
	        	hueUpperR = b.getUp();
	        	satHigh = b.getSatHigh();
	        	satLower = b.getSatLower();
	        	vHigh = b.getvHigh();
	        	vlow = b.getVlow();
	        	smooth_level = (b.getSmoothLevel()%2==1) ? smooth_level = b.getSmoothLevel() : b.getSmoothLevel()+1;
	        	erode_value = b.getErode_value();
	        	dilate_value = b.getDilate_value();
	        	frame = hsvThreshold(frame);
	        	canvasFrame.showImage(frame);
	        }
	        grabber.stop();
	        b.setEjecutandose(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static IplImage hsvThreshold(IplImage orgImg) {
        // 8-bit, 3- color =(RGB)
        IplImage imgHSV = IplImage.create(orgImg.cvSize(), 8, 3);
        //System.out.println(cvGetSize(orgImg));
        cvCvtColor(orgImg, imgHSV, CV_BGR2HSV);
        // 8-bit 1- color = monochrome
        IplImage imgThreshold = IplImage.create(orgImg.cvSize(), 8, 1);
        // cvScalar : ( H , S , V, A)
        cvInRangeS(imgHSV, cvScalar(hueLowerR, satLower, vlow, 0), cvScalar(hueUpperR, satHigh, vHigh, 0), imgThreshold);
        imgHSV.release();
        if(erode_value>=0)
        	cvErode(imgThreshold, imgThreshold, null, erode_value);
        if(dilate_value>=0)
        	cvDilate(imgThreshold, imgThreshold, null, dilate_value);
        cvSmooth(imgThreshold, imgThreshold, CV_MEDIAN, smooth_level);
        // save
        return imgThreshold;
    }
}
