//imports
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_features2d.*;
import static com.googlecode.javacv.cpp.opencv_photo.*;
import static com.googlecode.javacv.cpp.opencv_objdetect.*;
import com.googlecode.javacv.*;
import com.googlecode.javacv.cpp.opencv_core.*;
import com.googlecode.javacv.cpp.opencv_imgproc.*;


public class Histograma {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		IplImage image = cvLoadImage("src/img/imgbush.bmp");
		// Compute the HSV image and decompose it into separate planes.
		IplImage hsv = IplImage.create( image.cvSize(), 8, 3);
		cvCvtColor( image, hsv, CV_BGR2HSV);
		
		IplImage h_plane = IplImage.create( image.cvSize(), 8, 1);
		IplImage s_plane = IplImage.create( image.cvSize(), 8, 1);
		IplImage v_plane = IplImage.create( image.cvSize(), 8, 1);
		IplImage planes[] = { h_plane, s_plane };
		
		// Build the histogram and compute its contents.
		//
		int dims = 2;
		int[] sizes = {30, 32};
		int type = CV_HIST_ARRAY;
		float[] h_ranges = { 0, 180 };
		float[] s_ranges = { 0, 255 };
		float[][] ranges = { h_ranges, s_ranges };
		int uniform = 1;
		
		int h_bins = 30, s_bins = 32;
		
		CvHistogram hist = CvHistogram.create(dims, sizes, type, ranges, uniform);
		cvCalcHist(planes, hist, 0, null); //Compute histogram -- cvCalcHist(arr, hist, accumulate, mask);
		cvNormalizeHist(hist, 1.0); //Normalize it
		// Create an image to use to visualize our histogram.
		//
		int scale = 10;
		IplImage hist_img = cvCreateImage(image.cvSize(), 8, 3);
		cvZero(hist_img);
		// populate our visualization with little gray squares.
		//
		float max_value = 0;
		float[] arg1 = null;
		float[] arg2 = null;
		int[] arg3 = null;
		int[] arg4 = null;
		
		cvGetMinMaxHistValue(hist, arg1, arg2, arg3, arg4);
		
	}

}
