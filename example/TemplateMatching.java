import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_objdetect.*;
import static com.googlecode.javacv.cpp.opencv_features2d.*;
import static com.googlecode.javacv.cpp.opencv_video.*;
import static com.googlecode.javacv.cpp.opencv_videostab.*;

import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.cpp.opencv_core.CvContour;
import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import com.googlecode.javacv.cpp.opencv_core.CvSize;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class TemplateMatching {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		IplImage imagenOriginal = cvLoadImage("src/img/imgbush.bmp");
		IplImage objetoABuscar = cvLoadImage("src/img/imgbush-TM.bmp");
		int width = imagenOriginal.width()-objetoABuscar.width()+1;
		int height = imagenOriginal.height()-objetoABuscar.height()+1;
		IplImage resultado = IplImage.create(cvSize(width, height), 32, 1);
		//DO THE MATCHING OF THE TEMPLATE WITH THE IMAGE
		cvMatchTemplate(imagenOriginal, objetoABuscar, resultado, 1);
		cvNormalize(resultado, resultado, 4.0, 0.0, CV_MINMAX, null);
		//DISPLAY
		cvShowImage("Original", imagenOriginal);
		cvShowImage("Objeto a buscar", objetoABuscar);
		IplImage org = IplImage.create(imagenOriginal.cvSize(), 32, 1);
		cvResize(resultado, org);
		cvShowImage("Resultado", org);
		CvMat mat = org.asCvMat();
		
		for (int i = 0; i < mat.cols(); i++) {
			for (int j = 0; j < mat.rows(); j++) {
				
			}
		}
		cvWaitKey();
		/*
		CvSeq solv = new CvSeq(null);
		CvMemStorage storage = CvMemStorage.create();
		cvFindContours(box0, storage, solv, Loader.sizeof(CvContour.class), CV_RETR_LIST, CV_CHAIN_APPROX_SIMPLE);
		cvClearMemStorage(storage);
		IplImage box = imagenOriginal.clone();
        int total = solv.total();
        int SCALE = 1;
        for (int i = 0; i < total; i++) {
                CvRect r = new CvRect(cvGetSeqElem(solv, i));
                cvRectangle(box, cvPoint( r.x()*SCALE, r.y()*SCALE ),cvPoint( (r.x() + r.width())*SCALE,(r.y() + r.height())*SCALE ),CvScalar.GREEN, 2, CV_AA, 0);
        }
        */
		cvWaitKey();
	}

}
