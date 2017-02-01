import static com.googlecode.javacv.cpp.opencv_highgui.*;
import com.googlecode.javacv.cpp.opencv_highgui.*;

public class Barras extends Thread{
		
	private boolean ejecutandose;
	private double hueUp;
	private double hueLow;
	private double satLower;
	private double vlow;
	private double satHigh;
	private double vHigh;
	private int smooth_level;
	private int erode_value;
	private int dilate_value;
	private final String WINDOW_NAME = "Threshold Image Control";
	private int parar;
	
	public Barras(boolean end, double hueUp, double hueLow, double satLower,
			double vlow, double satHigh, double vHigh, int smooth_level,
			int erode_value, int dilate_value) {
		this.ejecutandose = end;
		this.hueUp = hueUp;
		this.hueLow = hueLow;
		this.satLower = satLower;
		this.vlow = vlow;
		this.satHigh = satHigh;
		this.vHigh = vHigh;
		this.smooth_level = smooth_level;
		this.erode_value = erode_value;
		this.dilate_value = dilate_value;
	}
	
	public boolean isEjecutandose() {
		return ejecutandose;
	}

	public void setEjecutandose(boolean end) {
		this.ejecutandose = end;
	}

	public int getErode_value() {
		return erode_value;
	}

	public int getDilate_value() {
		return dilate_value;
	}
	
	public double getUp() {
		return hueUp;
	}

	public double getLow() {
		return hueLow;
	}
	public double getSatLower() {
		return satLower;
	}

	public double getVlow() {
		return vlow;
	}

	public double getSatHigh() {
		return satHigh;
	}

	public double getvHigh() {
		return vHigh;
	}
	
	public int getSmoothLevel(){
		return smooth_level;
	}

	public int getParar() {
		return parar;
	}

	public void setParar(int parar) {
		this.parar = parar;
	}

	public void run(){
		
		cvNamedWindow(WINDOW_NAME, CV_WINDOW_NORMAL);
		
		cvCreateTrackbar("Min hue", WINDOW_NAME, null, 256, null);
		cvCreateTrackbar("Max hue", WINDOW_NAME, null, 256, null);
		cvSetTrackbarPos("Max hue", WINDOW_NAME,256);
		cvCreateTrackbar("Min sat", WINDOW_NAME, null, 256, null);
		cvCreateTrackbar("Max sat", WINDOW_NAME, null, 256, null);
		cvSetTrackbarPos("Max sat", WINDOW_NAME,256);
		cvCreateTrackbar("Min v", WINDOW_NAME, null, 256, null);
		cvCreateTrackbar("Max v", WINDOW_NAME, null, 256, null);
		cvSetTrackbarPos("Max v", WINDOW_NAME,256);
		cvCreateTrackbar("Smooth", WINDOW_NAME, null, 100, null);
		cvSetTrackbarPos("Smooth", WINDOW_NAME,0);
		
		cvCreateTrackbar("Erode", WINDOW_NAME, null, 100, null);
		cvCreateTrackbar("Dilate", WINDOW_NAME, null, 100, null);
		
		cvCreateTrackbar("Parar", WINDOW_NAME, null, 2, null);
		
		while(ejecutandose){
			hueLow = cvGetTrackbarPos("Min hue", WINDOW_NAME);
			hueUp = cvGetTrackbarPos("Max hue", WINDOW_NAME);
			satLower = cvGetTrackbarPos("Min sat", WINDOW_NAME);
			satHigh = cvGetTrackbarPos("Max sat", WINDOW_NAME);
			vlow = cvGetTrackbarPos("Min v", WINDOW_NAME);
			vHigh = cvGetTrackbarPos("Max v", WINDOW_NAME);
			smooth_level = cvGetTrackbarPos("Smooth", WINDOW_NAME);
			erode_value = cvGetTrackbarPos("Erode", WINDOW_NAME);
			dilate_value = cvGetTrackbarPos("Dilate", WINDOW_NAME);
			setParar(cvGetTrackbarPos("Parar", WINDOW_NAME));
			cvWaitKey(33);
		}
	}
}
