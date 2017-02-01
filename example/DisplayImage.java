import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;

public class DisplayImage {

	public static void main(String[] args) {
		String filename ="src/img/imgbush.bmp";
		verImagen(filename);
	}

	public static void verImagen(String filename) {
		IplImage image = cvLoadImage(filename);
        if (image != null) {
            cvShowImage("Image", image);
            cvWaitKey();
            cvReleaseImage(image);
            cvDestroyWindow("Image");
        }
	}
	
	public static void guardarImagen(String filename) {
		IplImage image = cvLoadImage(filename);
        if (image != null) {
            cvSaveImage(filename, image);
        }
	}

}
