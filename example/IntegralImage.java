import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_objdetect.*;
import static com.googlecode.javacv.cpp.opencv_features2d.*;
import static com.googlecode.javacv.cpp.opencv_video.*;
import static com.googlecode.javacv.cpp.opencv_videostab.*;

import com.googlecode.javacv.cpp.opencv_core.CvSize;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class IntegralImage {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		IplImage[] data = new IplImage[2];
		data[0] = cvLoadImage("src/img/imgbush.bmp");
		data[1] = IplImage.create(new CvSize(data[0].width()+1, data[0].height()+1),32,3);
		cvIntegral(data[0], data[1], null, null);
		cvShowImage("Imagen Original", data[0]);
		cvShowImage("Imagen Integral", data[1]);
		cvWaitKey();
	}

}
