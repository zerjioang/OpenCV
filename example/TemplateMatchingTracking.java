import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import java.awt.Frame;
import java.util.Map;
import java.util.Properties;

import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_core.*;


public class TemplateMatchingTracking {

	public static void main(String[] args) {
		
		OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
		try {
			grabber.start();
			IplImage frame = grabber.grab();
			IplImage aBuscar = null;
			CanvasFrame[] ventana = new CanvasFrame[4];
			String[] titulo = {"Imagen Original","Objeto a buscar","Seleccion","Resultado"};
			
			for (int i = 0; i < ventana.length; i++) {
				ventana[i] = new CanvasFrame(titulo[i]);
		        ventana[i].setCanvasSize(frame.width()/2, frame.height()/2);
		        ventana[i].setLocationRelativeTo(null);
		        ventana[i].setDefaultCloseOperation(CanvasFrame.DISPOSE_ON_CLOSE);
			}
			ventana[0].showImage(frame);
			boolean estado = true;
			IplImage muestra = frame.clone();
			Controls c = new Controls("Controles", muestra, estado);
			c.start();
			while(c.isMantenerActivo()){
				ventana[2].showImage(c.getImg());
				if(c.getOpcion()==1){
					int ancho = Math.abs(c.getW2()-c.getW());
					int alto = Math.abs(c.getH2()-c.getH());

					int m, h2 = c.getH2(), h = c.getH(), w2 = c.getW2(), w = c.getW();
					if(h2>h){
						m = h;
						h = h2;
						h2 = m;
					}
					if(w2>w){
						m = w;
						w = w2;
						w2 = m;
					}
					CvMat mat = CvMat.create(alto, ancho, 8, 3);
					cvZero(mat);
					final CvMat orig = frame.asCvMat();
					int x = w2;
					int y = h2;
					int recX = w, recY=h;
					int posx=0,posy=0;
					while(y<recY){
						while(x<recX){
							try {
								if(x<orig.cols() && y<orig.rows()){
									for (int i = 0; i < 3; i++) {
										mat.put(posy, posx, i, orig.get(y, x, i));
									}
								}
							} catch (Exception e) {
								System.out.println(e.getMessage());
							}
							x++;
							if(posx<ancho-1){
								posx++;
							}
							else{
								posx=0;
							}
						}
						x=w2;
						y++;
						if(posy<alto-1){
							posy++;
						}
						else{
							posy=0;
						}
					}
					aBuscar=mat.asIplImage();
				}
				if(aBuscar!=null){
					ventana[1].showImage(aBuscar);
				}
				if(c.getOpcion()==2){
					int width = frame.width()-aBuscar.width()+1;
					int height = frame.height()-aBuscar.height()+1;
					IplImage resultado = IplImage.create(cvSize(width, height), 32, 1);
					//DO THE MATCHING OF THE TEMPLATE WITH THE IMAGE
					cvMatchTemplate(frame, aBuscar, resultado, 1);
					cvNormalize(resultado, resultado, 4.0, 0.0, CV_MINMAX, null);
					//DISPLAY
					IplImage org = IplImage.create(frame.width()/2, frame.height()/2, 32, 1);
					cvResize(resultado, org);
					cvShowImage("Resultado", org);
					//ventana[3].showImage(gray);
				}
			}
	        /*while((frame = grabber.grab()) != null && ventana[0].isVisible()){
	        	cvFlip(frame, frame, 1);
	        	//frame = procesarFrame(frame);
	        	ventana[0].showImage(frame);
	        }*/
	        grabber.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
class Controls extends Thread{
	private IplImage img;
	private IplImage bkp;
	private String WINDOW_NAME;
	private boolean mantenerActivo;
	private int h, h2, w, w2;
	private int opcion;
	
	public Controls(String nombre, IplImage im, boolean estado){
		WINDOW_NAME = nombre;
		img = im;
		bkp = img.clone();
		mantenerActivo = estado;
	}
	
	public void run(){
		cvNamedWindow(WINDOW_NAME, CV_WINDOW_NORMAL);
		cvCreateTrackbar("HORIZONTAL 1", WINDOW_NAME, null, img.height(), null);
		cvCreateTrackbar("HORIZONTAL 2", WINDOW_NAME, null, img.height(), null);
		cvCreateTrackbar("VERTICAL 1", WINDOW_NAME, null, img.width(), null);
		cvCreateTrackbar("VERTICAL 2", WINDOW_NAME, null, img.width(), null);
		cvCreateTrackbar("Opcion", WINDOW_NAME, null, 3, null);
		
		while(mantenerActivo){
			h = cvGetTrackbarPos("HORIZONTAL 1", WINDOW_NAME);
			h2 = cvGetTrackbarPos("HORIZONTAL 2", WINDOW_NAME);
			w = cvGetTrackbarPos("VERTICAL 1", WINDOW_NAME);
			w2 = cvGetTrackbarPos("VERTICAL 2", WINDOW_NAME);
			setOpcion(cvGetTrackbarPos("Opcion", WINDOW_NAME));
			//cvLine(img, cvPoint(0, 0), cvPoint(img.height(), h), CvScalar.GREEN, 5, 0, 0);
			cvLine(img, cvPoint(0, h), cvPoint(img.width(), h), CvScalar.GREEN, 2, 0, 0); //horizontal superior
			cvLine(img, cvPoint(0, h2), cvPoint(img.width(), h2), CvScalar.BLUE, 2, 0, 0); //horizontal inferior
			cvLine(img, cvPoint(w2, 0), cvPoint(w2, img.height()), CvScalar.MAGENTA, 2, 0, 0); //vertical superior
			cvLine(img, cvPoint(w, 0), cvPoint(w, img.height()), CvScalar.YELLOW, 2, 0, 0); //vertical inferior
			//System.out.println(h+" "+h2+" "+w+" "+w2);
			cvWaitKey(33);
			img = bkp.clone();
		}
	}
	

	public IplImage getImg() {
		return img;
	}

	public void setImg(IplImage img) {
		this.img = img;
	}

	public boolean isMantenerActivo() {
		return mantenerActivo;
	}

	public void setMantenerActivo(boolean mantenerActivo) {
		this.mantenerActivo = mantenerActivo;
	}

	public int getH() {
		return h;
	}

	public void setH(int h) {
		this.h = h;
	}

	public int getW() {
		return w;
	}

	public void setW(int w) {
		this.w = w;
	}

	public int getH2() {
		return h2;
	}

	public void setH2(int h2) {
		this.h2 = h2;
	}

	public int getW2() {
		return w2;
	}

	public void setW2(int w2) {
		this.w2 = w2;
	}

	public int getOpcion() {
		return opcion;
	}

	public void setOpcion(int opcion) {
		this.opcion = opcion;
	}
	
}
