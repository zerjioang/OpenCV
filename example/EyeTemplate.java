//imports
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import java.awt.Dimension;

import javax.swing.JOptionPane;

import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.*;
import com.googlecode.javacv.cpp.opencv_imgproc.CvMoments;

public class EyeTemplate {

    public static void main(String[] args) {

		OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
		IplImage objetoABuscar = cvLoadImage("src/img/cara.png");
		try {
			grabber.start();
			IplImage frame = grabber.grab();
	        //(frame = grabber.grab()) != null
	        while(true){
	        	IplImage imagenOriginal = frame.clone();
	    		int width = imagenOriginal.width()-objetoABuscar.width()+1;
	    		int height = imagenOriginal.height()-objetoABuscar.height()+1;
	    		IplImage resultado = IplImage.create(cvSize(width, height), 32, 1);
	    		//DO THE MATCHING OF THE TEMPLATE WITH THE IMAGE
	    		cvMatchTemplate(imagenOriginal, objetoABuscar, resultado, 0);
	    		cvNormalize(resultado, resultado, 1.0, 0.0, CV_MINMAX, null);
	    		//DISPLAY
	    		cvShowImage("Original", imagenOriginal);
	    		cvShowImage("Objeto a buscar", objetoABuscar);
	    		IplImage org = IplImage.create(imagenOriginal.cvSize(), 32, 1);
	    		cvResize(resultado, org);
	    		cvShowImage("Resultado", org);
	    		cvWaitKey(33);
	    		frame = grabber.grab();
	        }
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
}
