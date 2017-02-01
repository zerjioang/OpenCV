import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.CvContour;
import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_imgproc.CvMoments;

public class VideoPause {
	
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
		//Declare FrameGrabber to import video from file
		FrameGrabber grabber = new OpenCVFrameGrabber("C:\\Users\\S01\\Downloads\\videos prueba\\carretera\\SAM_1038.avi");
		try {
			//Start grabber to capture video
			grabber.start();
			//Declare img as IplImage
			IplImage frame;
			//Create canvas frame for displaying video.
			CanvasFrame canvas = new CanvasFrame("VideoCam Player");
			canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
			canvas.setCanvasSize(grabber.getImageWidth(), grabber.getImageHeight());
			
			CanvasFrame canvasFrame2 = new CanvasFrame("Imagen Threshold");
			canvasFrame2.setCanvasSize(grabber.getImageWidth(), grabber.getImageHeight());
	        canvasFrame2.setDefaultCloseOperation(CanvasFrame.DISPOSE_ON_CLOSE);
			
	        Barras b = new Barras(canvas.isVisible(), hueLowerR, hueUpperR, satHigh, satLower, vHigh, vlow, smooth_level, erode_value, dilate_value);
	        b.start();
			
			while (true) {
				frame = grabber.grab();
					while(b.getParar() == 0){
						//procesar el frame actual que esta en pausa
						IplImage hsv = IplImage.create(frame.cvSize(), 8, 3);
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
				    	
				    	IplImage thresholdImage = hsvThreshold(hsv);
				        int[] datos = new int[3];
				        datos = getCoordinates(thresholdImage, datos);
				        //System.out.println("Dimension of original Image : " + thresholdImage.width() + " , " + thresholdImage.height());
				        //System.out.println("Position of red spot    : x : " + position.width + " , y : " + position.height);
				    	canvas.showImage(frame);
				    	canvasFrame2.showImage(thresholdImage);
					}
					while(b.getParar() == 1 && (frame = grabber.grab())!=null){
						//procesar el frame actual
						IplImage hsv = IplImage.create(frame.cvSize(), 8, 3);
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
				    	
				    	IplImage thresholdImage = hsvThreshold(hsv);
				        int[] datos = new int[3];
				        datos = getCoordinates(thresholdImage, datos);
				        //System.out.println("Dimension of original Image : " + thresholdImage.width() + " , " + thresholdImage.height());
				        //System.out.println("Position of red spot    : x : " + position.width + " , y : " + position.height);
				       
				        CvMemStorage storage = CvMemStorage.create();
				        CvSeq contour = new CvSeq(null);
				        cvFindContours(thresholdImage, storage, contour, Loader.sizeof(CvContour.class), CV_RETR_LIST, CV_CHAIN_APPROX_SIMPLE);
				        int x=2000,y=2000, x2=0, y2=0;
				        if(contour!=null && !contour.isNull()){
				        	for (int i = 0; i < contour.total(); i++) {
		                        CvRect r = new CvRect(cvGetSeqElem(contour, i));
		                        if(r.x()<x){
		                        	x = r.x();		                        	
		                        }
		                        if(r.y()<y){
		                        	y = r.y();		                        	
		                        }
		                        if(r.x()>x2){
		                        	x2 = r.x();		                        	
		                        }
		                        if(r.y()>y2){
		                        	y2 = r.y();		                        	
		                        }
							}
				        	System.out.println(x+" "+y+" "+x2+" "+y2+" w: "+(x2-x)+" h: "+(y2-y));
				        	cvRectangle(frame,
	                        		cvPoint(x, y),
	                        		cvPoint( (x + (x2-x)), (y + (y2-y)) ),
	                        		CvScalar.GREEN, 2, CV_AA, 0);
				        }
				        /*if(datos[0]!=0 && datos[1]!=0){
				        	marcar(frame, datos);
				        }*/
				    	canvas.showImage(frame);
				    	canvasFrame2.showImage(thresholdImage);
					}
				}
        //grabber.stop();
        //b.setEjecutandose(false);
		}
		catch (Exception e) {
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