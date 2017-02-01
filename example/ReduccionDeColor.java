import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import static com.googlecode.javacv.cpp.opencv_core.cvFlip;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvShowImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvWaitKey;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_RGB2HLS;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;

import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.googlecode.javacv.cpp.opencv_core.IplImage;


public class ReduccionDeColor {

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
	        	cvFlip(frame, frame, 1);
	        	frame = reducirColor(frame);
	        	canvasFrame.showImage(frame);
	        }
	        grabber.stop();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	private static IplImage reducirColor(IplImage src) {
		CvMat mat = src.asCvMat();
        // Total number of elements, combining components from each channel
		int div = 64;
		int nbElements = mat.rows() * mat.cols() * mat.channels();
        for (int i = 0; i<nbElements;i++) {
            // Convert to integer
            int v = (int) mat.get(i);
            // Use integer division to reduce number of values
            double newV = v / div * div + div / 2;
            // Put back into the image
            mat.put(i, newV);
        }
		//cvShowImage("Imagen ColorReduced", mat.asIplImage());
		//cvWaitKey();
		return mat.asIplImage();
	}

}
