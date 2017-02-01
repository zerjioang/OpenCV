import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import static com.googlecode.javacv.cpp.opencv_core.cvAbsDiff;
import static com.googlecode.javacv.cpp.opencv_core.cvClearMemStorage;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_CHAIN_APPROX_SIMPLE;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_GAUSSIAN;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_RETR_LIST;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_RGB2GRAY;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_THRESH_BINARY;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvFindContours;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvSmooth;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvThreshold;

import java.awt.Point;

import javax.swing.JFrame;
import javax.swing.JLabel;

import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.CvContour;
import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class runCam extends Thread{

	private CanvasFrame cv;
	private JFrame frame;
	private JLabel lbl;
	public runCam(CanvasFrame canvas, JFrame frmSensorDeMovimiento, JLabel l) {
		this.cv = canvas;
		cv.setCanvasSize(380, 270);
        cv.setLocationRelativeTo(null);
        cv.setAlwaysOnTop(true);
        cv.setResizable(false);
        cv.setEnabled(false);
        cv.setLocation(110, 138);
        cv.setDefaultCloseOperation(CanvasFrame.DISPOSE_ON_CLOSE);
		frame = frmSensorDeMovimiento;
		lbl = l;
	}
	public void run(){
		init();
	}
	public void init(){
		OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
		CanvasFrame cv2 = new CanvasFrame("Movimiento");
		cv2.setCanvasSize(380, 270);
        cv2.setLocationRelativeTo(null);
        cv2.setAlwaysOnTop(true);
        cv2.setResizable(false);
        cv2.setEnabled(false);
        cv.setDefaultCloseOperation(CanvasFrame.DISPOSE_ON_CLOSE);
        cv2.setCanvasSize(230,180);
        
		try {
			grabber.start();
			IplImage frame = grabber.grab();
	        IplImage image = null;
	        IplImage prevImage = null;
	        IplImage diff = null;
	        
	        CvMemStorage storage = CvMemStorage.create();

	        while (cv.isVisible() && (frame = grabber.grab()) != null) {
	        	
	        	Point p = cv.getLocation();
	        	cv.setLocation(this.frame.getX()+10, this.frame.getY()+38);
	        	cv2.setLocation(this.frame.getX()+410, this.frame.getY()+38);

	        	IplImage temp = frame.clone();
	            cvSmooth(frame, frame, CV_GAUSSIAN, 9, 9, 2, 2);
	            
	            if (image == null) {
	                image = IplImage.create(frame.width(), frame.height(), IPL_DEPTH_8U, 1);
	                cvCvtColor(frame, image, CV_RGB2GRAY);
	            } else {
	                prevImage = IplImage.create(frame.width(), frame.height(), IPL_DEPTH_8U, 1);
	                prevImage = image;
	                image = IplImage.create(frame.width(), frame.height(), IPL_DEPTH_8U, 1);
	                cvCvtColor(frame, image, CV_RGB2GRAY);
	            }

	            if (diff == null) {
	                diff = IplImage.create(frame.width(), frame.height(), IPL_DEPTH_8U, 1);
	            }

	            if (prevImage != null) {
	                // perform ABS difference
	                cvAbsDiff(image, prevImage, diff);
	                // do some threshold for wipe away useless details
	                //cvThreshold(diff, diff, 64, 255, CV_THRESH_BINARY);
	                cvThreshold(diff, diff, 32, 255, CV_THRESH_BINARY);
	                cv.showImage(temp);
	                cv2.showImage(diff);
	                CvSeq contour = new CvSeq(null);
	                cvFindContours(diff, storage, contour, Loader.sizeof(CvContour.class), CV_RETR_LIST, CV_CHAIN_APPROX_SIMPLE);
	                cvClearMemStorage(storage);
	                
	                if(contour!=null && !contour.isNull()){
	                //Movimiento detectado
	                	lbl.setText("MOVIMIENTO DETECTADO");
	                }
	                else{
	                	lbl.setText("SIN MOVIMIENTO");
	                }
	            }
	        }
	        grabber.stop();
	        cv.dispose();
	        this.frame.dispose();
	        grabber.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
