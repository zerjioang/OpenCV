import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;

import com.googlecode.javacv.cpp.opencv_core.IplImage;


public class SuavizarImagen {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String filename ="src/img/imgbush.bmp";
		IplImage original = cvLoadImage(filename);
		IplImage procesada;
		cvShowImage("Imagen original", original);
		procesada = IplImage.create(original.cvSize(), IPL_DEPTH_8U, 3);
		cvSmooth(original, procesada, 2, 7);
		cvShowImage("Imagen procesada", procesada);
		cvReleaseImage(procesada);
		cvReleaseImage(original);
		cvWaitKey();
        cvDestroyWindow("Imagen original");
        cvDestroyWindow("Imagen procesada");
	}

}
