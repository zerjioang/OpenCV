import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import com.googlecode.javacv.*;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class HSV {
	
	public static void main(String[] args) {
		original();
	}
	private static void original() {
		//Declare FrameGrabber to import video from file
		OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
		try {
			//Start grabber to capture video
			grabber.start();
			//Declare img as IplImage
			IplImage img;
			//Create canvas frame for displaying video.
			CanvasFrame canvas = new CanvasFrame("Camara Portatil");
			canvas.setVisible(false);
			//Set Canvas frame to close on exit
			canvas.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
			canvas.setCanvasSize(grabber.getImageWidth(), grabber.getImageHeight());
			canvas.setLocationRelativeTo(null);
			canvas.setVisible(true);
			
			while ((img = grabber.grab())!=null && canvas.isVisible()) {
				IplImage hsv = IplImage.create(img.width(), img.height(), 8, 3);
				cvCvtColor(img, hsv, CV_BGR2HSV);
				//1 convertir imagen a GRIS
				IplImage gray = IplImage.create(img.width(), img.height(), 8, 1);
				cvCvtColor(img, gray, CV_BGR2GRAY);
				//Equalizar imagen
				IplImage equImg = IplImage.create(img.width(), img.height(), 8, 1);
				cvEqualizeHist(gray, equImg);
				//Convertir imagen a binary threshold
				double threshold=20;
				double max_value=255;
				cvThreshold(equImg, equImg, threshold, max_value, CV_THRESH_BINARY);
				cvMorphologyEx(equImg, equImg, null, null, CV_MOP_OPEN, 1);
				canvas.showImage(equImg);
			}
			grabber.stop();
			}
		catch (Exception e) 
		{
			System.out.println(e.getMessage());
		}
	}
}
