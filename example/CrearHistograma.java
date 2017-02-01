import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_objdetect.*;
import static com.googlecode.javacv.cpp.opencv_features2d.*;
import static com.googlecode.javacv.cpp.opencv_video.*;
import static com.googlecode.javacv.cpp.opencv_videostab.*;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class CrearHistograma {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		crearHistogramaNormal();
	}

	private static void crearHistogramaNormal() {
		//cargar imagen
		IplImage image = cvLoadImage("src/img/imgbush.bmp");
		//cargar valores del histograma
		int numberOfBins = 256;
		float _minRange = 0.0f;
		float _maxRange = 255.0f;
		float[] minMax = {_minRange, _maxRange};
		//es necesario que la img!=null & que n channels == 3
		int dims = 3;
		int[] sizes = {numberOfBins, numberOfBins, numberOfBins};
		int histType = CV_HIST_SPARSE;
		float[][] ranges = {minMax,minMax,minMax};
		int uniform = 1;
		 // Split bands, as required by `cvCalcHist`
		IplImage  channel0 = cvCreateImage(cvGetSize(image), image.depth(), 1);
		IplImage channel1 = cvCreateImage(cvGetSize(image), image.depth(), 1);
		IplImage channel2 = cvCreateImage(cvGetSize(image), image.depth(), 1);
		cvSplit(image, channel0, channel1, channel2, null);
		// Compute histogram
        CvHistogram hist = cvCreateHist(dims, sizes, histType, ranges, uniform);
	}

}
