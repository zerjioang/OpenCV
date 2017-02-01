//imports
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import java.awt.Dimension;

import javax.swing.JOptionPane;

import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.*;
import com.googlecode.javacv.cpp.opencv_imgproc.CvMoments;

public class CustomColorTreshold {
	
	private static int erode_value = 0;
	private static int dilate_value = 0;
	private static int smooth_level = 0;
	private static double satLower = 0;
	private static double vlow = 0;
	private static double satHigh = 0;
	private static double vHigh = 0;
	private static double hueLowerR = 0;
	private static double hueUpperR = 0;

    public static void main(String[] args) {

		OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
		try {
			grabber.start();
			IplImage frame = grabber.grab();
			CanvasFrame canvasFrame = new CanvasFrame("Imagen de Webcam");
			canvasFrame.setCanvasSize(frame.width()/2, frame.height()/2);
	        canvasFrame.setDefaultCloseOperation(CanvasFrame.DISPOSE_ON_CLOSE);
	        
			CanvasFrame canvasFrame2 = new CanvasFrame("Imagen Threshold");
			canvasFrame2.setCanvasSize(frame.width()/2, frame.height()/2);
	        canvasFrame2.setDefaultCloseOperation(CanvasFrame.DISPOSE_ON_CLOSE);
	        
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
	        	
	        	IplImage thresholdImage = hsvThreshold(frame);
	            int[] datos = new int[3];
	            datos = getCoordinates(thresholdImage, datos);
	            //System.out.println("Dimension of original Image : " + thresholdImage.width() + " , " + thresholdImage.height());
	            //System.out.println("Position of red spot    : x : " + position.width + " , y : " + position.height);
	            if(datos[2]>100){
	            	marcar(frame, datos);
	            }
	        	canvasFrame.showImage(frame);
	        	canvasFrame2.showImage(thresholdImage);
	        }
	        grabber.stop();
	        b.setEjecutandose(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

	private static IplImage marcar(IplImage frame, int[] datos) {
    	final int SCALE = 1;
    	final int TAM = (int) Math.sqrt(datos[2]); //cambia el tamaño del rectangulo dependiendo del objeto fuente
    	CvRect r = new CvRect(datos[0], datos[1], TAM, TAM);
    	cvRectangle(frame, cvPoint( (r.x()-TAM/2)*SCALE, (r.y()-TAM/2)*SCALE ),cvPoint( (r.x() + r.width())*SCALE,(r.y() + r.height())*SCALE ),CvScalar.GREEN, 2, CV_AA, 0);
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
