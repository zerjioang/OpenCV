//imports
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import java.awt.Dimension;

import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.*;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_imgproc.CvMoments;

public class ColorTreshold {

    public static void main(String[] args) {

		OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
		try {
			grabber.start();
			IplImage frame = grabber.grab();
			CanvasFrame canvasFrame = new CanvasFrame("Imagen de Webcam");
	        canvasFrame.setCanvasSize(frame.width(), frame.height());
	        canvasFrame.setLocationRelativeTo(null);
	        canvasFrame.setDefaultCloseOperation(CanvasFrame.DISPOSE_ON_CLOSE);
	        
			CanvasFrame canvasFrame2 = new CanvasFrame("Imagen Threshold");
	        canvasFrame2.setCanvasSize(frame.width(), frame.height());
	        canvasFrame2.setLocationRelativeTo(null);
	        canvasFrame2.setDefaultCloseOperation(CanvasFrame.DISPOSE_ON_CLOSE);
	        
	        while((frame = grabber.grab()) != null && canvasFrame.isVisible()){
	        	cvFlip(frame, frame, 1);
	        	IplImage thresholdImage = hsvThreshold(frame);
	            int[] datos = new int[3];
	            datos = getCoordinates(thresholdImage, datos);
	            //System.out.println("Dimension of original Image : " + thresholdImage.width() + " , " + thresholdImage.height());
	            //System.out.println("Position of red spot    : x : " + position.width + " , y : " + position.height);
	            if(datos[0]!=0 && datos[1]!=0){
	            	marcar(frame, datos);
	            }
	        	canvasFrame.showImage(frame);
	        	canvasFrame2.showImage(thresholdImage);
	        }
	        grabber.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    private static IplImage marcar(IplImage frame, int[] datos) {
    	final int SCALE = 1;
    	final int TAM = (int) Math.sqrt(datos[2]);
    	CvRect r = new CvRect(datos[0], datos[1], TAM, TAM);
    	cvRectangle(frame, cvPoint( r.x()*SCALE, r.y()*SCALE ),cvPoint( (r.x() + r.width())*SCALE,(r.y() + r.height())*SCALE ),CvScalar.GREEN, 2, CV_AA, 0);
		return frame;
    }

	private static int[] getCoordinates(IplImage thresholdImage, int[] datos) {
        CvMoments moments = new CvMoments();
        cvMoments(thresholdImage, moments, 1);
        // cv Spatial moment : Mji=sumx,y(I(x,y)•xj•yi)
        // where I(x,y) is the intensity of the pixel (x, y).
        double momX10 = cvGetSpatialMoment(moments, 1, 0); // (x,y)
        double momY01 = cvGetSpatialMoment(moments, 0, 1);// (x,y)
        double area = cvGetCentralMoment(moments, 0, 0);
        datos[0] = (int) (momX10 / area); //coordenada X
        datos[1] = (int) (momY01 / area); // coordenada Y
        datos[2] = (int) area;
        return datos;
    }

    private static IplImage hsvThreshold(IplImage orgImg) {
        // 8-bit, 3- color =(RGB)
        IplImage imgHSV = IplImage.create(orgImg.cvSize(), 8, 3);
        //System.out.println(cvGetSize(orgImg));
        cvCvtColor(orgImg, imgHSV, CV_BGR2HSV);
        // 8-bit 1- color = monochrome
        IplImage imgThreshold = IplImage.create(orgImg.cvSize(), 8, 1);
        // cvScalar : ( H , S , V, A)
        cvInRangeS(imgHSV, cvScalar(0,0, 77, 0), cvScalar(256, 256, 256, 0), imgThreshold);
        /*cvDilate(imgThreshold, imgThreshold, null, 5);
        cvErode(imgThreshold, imgThreshold, null, 5);*/
        cvSmooth(imgThreshold, imgThreshold, CV_MEDIAN, 1);
        imgHSV.release();
        return imgThreshold;
    }
}
