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

public class CustomColorTracking {
	
	private static int lastX = 0;
	private static int lastY = 0;
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
	        
			CanvasFrame canvasFrame3 = new CanvasFrame("Movimiento del Objeto");
	        canvasFrame3.setCanvasSize(frame.width()/2, frame.height()/2);
	        canvasFrame3.setDefaultCloseOperation(CanvasFrame.DISPOSE_ON_CLOSE);
	        
	        Barras b = new Barras(canvasFrame.isVisible(), hueLowerR, hueUpperR, satHigh, satLower, vHigh, vlow, smooth_level, erode_value, dilate_value);
	        b.start();
	        
	        IplImage mov = IplImage.create(frame.cvSize(), 8, 3);
	        
	        while((frame = grabber.grab()) != null && canvasFrame.isVisible()){
	        	IplImage hsv = IplImage.create(frame.cvSize(), 8, 3);
	        	cvFlip(frame, frame, 1);
	        	frame = reducirColor(frame);
	        	cvCvtColor(frame, hsv, CV_BGR2HSV);
	        	hueLowerR = b.getLow();
	        	hueUpperR = b.getUp();
	        	satHigh = b.getSatHigh();
	        	satLower = b.getSatLower();
	        	vHigh = b.getvHigh();
	        	vlow = b.getVlow();
	        	smooth_level = (b.getSmoothLevel()%2==1) ? smooth_level = b.getSmoothLevel() : b.getSmoothLevel()+1;
	        	erode_value = b.getErode_value();
	        	dilate_value = b.getDilate_value();
	        	String vals = "HueUp "+hueUpperR+" HueLow "+hueLowerR+" SatUp "+satHigh+" SatLow "+satLower+" VLow "+vlow+" VUp "+vHigh;
	        	
	        	if(b.getParar()==2){
	        		JOptionPane.showMessageDialog(null, vals, "Valores HSV", JOptionPane.INFORMATION_MESSAGE);
	        	}
	        	IplImage thresholdImage = hsvThreshold(hsv);
	            int[] datos = new int[3];
	            datos = getCoordinates(thresholdImage, datos);
	            //System.out.println("Dimension of original Image : " + thresholdImage.width() + " , " + thresholdImage.height());
	            //System.out.println("Position of red spot    : x : " + position.width + " , y : " + position.height);
	            if(datos[2]>100 && b.getParar()==1){
	            	marcar(frame, datos);
	            }
	        	canvasFrame.showImage(frame);
	        	canvasFrame2.showImage(thresholdImage);
	        	if(b.getParar()==1){
	        		canvasFrame3.showImage(trackedImage(mov, datos));
	        	}
	        }
	        grabber.stop();
	        b.setEjecutandose(false);
	        canvasFrame.dispose();
	        canvasFrame2.dispose();
	        canvasFrame3.dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

	private static IplImage reducirColor(IplImage src) {
		CvMat mat = src.asCvMat();
        // Total number of elements, combining components from each channel
		int div = 100;//64
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

	private static IplImage trackedImage(IplImage mov, int[] datos) {
    	//obtener el centro del objeto. coordenadas x e y (datos 0 y datos 1)
    	int x = datos[0];
    	int y = datos[1];
    	final int grosor = 4;
    	//System.out.println(cvPoint(x, y)+" "+cvPoint(lastX, lastY));
		if(lastX>0 && lastY>0 && x>0 && y>0)
        {
            // Draw a yellow line from the previous point to the current point
            //cvLine(frame, cvPoint(x, y), cvPoint(lastX, lastY), CvScalar.BLUE, 4);
			cvLine(mov, cvPoint(x, y), cvPoint(lastX, lastY), CvScalar.GREEN, grosor, 0, 0);
        }

        lastX = x;
        lastY = y;

		return mov;
    }

	private static IplImage marcar(IplImage frame, int[] datos) {
    	final int SCALE = 1;
    	final int TAM = (int) Math.sqrt(datos[2]); //cambia el tamaño del rectangulo dependiendo del objeto fuente
    	CvRect r = new CvRect(datos[0], datos[1], TAM, TAM);
    	//cvRectangle(frame,  cvPoint( (r.x() + r.width())*SCALE,(r.y() + r.height())*SCALE ), cvPoint( (r.x()-TAM/2)*SCALE, (r.y()-TAM/2)*SCALE), CvScalar.GREEN, 2, CV_AA, 0);
		cvDrawCircle(frame, cvPoint( (r.x()+TAM/2)*SCALE, (r.y()+TAM/2)*SCALE ), TAM, CvScalar.GREEN, 2, CV_AA, 0);
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
        //cvInRangeS(imgHSV, cvScalar(0, 91, 236, 0), cvScalar(69, 179, 255, 0), imgThreshold);
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
