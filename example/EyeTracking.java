//imports
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_objdetect.*;

import java.rmi.server.RemoteObjectInvocationHandler;

import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.*;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_core.IplROI;
import com.googlecode.javacv.cpp.opencv_imgproc.CvMoments;
import com.googlecode.javacv.cpp.opencv_objdetect.CvHaarClassifierCascade;

public class EyeTracking {

    private static final int SCALE = 1;
    private static CvRect recorte;
    private static IplImage rec;
    
	public static void main(String[] args) {
		OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
		try {
			grabber.start();
			IplImage frame = grabber.grab();
			CanvasFrame canvasFrame = new CanvasFrame("Imagen de Webcam");
			canvasFrame.setCanvasSize(frame.width()/2, frame.height()/2);
	        canvasFrame.setLocationRelativeTo(null);
	        canvasFrame.setDefaultCloseOperation(CanvasFrame.DISPOSE_ON_CLOSE);
	        
	        while((frame = grabber.grab()) != null && canvasFrame.isVisible()){
	        	cvFlip(frame, frame, 1);
	        	IplImage c = detectarCaras(frame);
	        	canvasFrame.showImage(c);
	        }
	        grabber.stop();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	private static IplImage detectarCaras(IplImage origImg) {
    	String CASCADE_FILE ="src/img/haarcascade_frontalface_alt2.xml";

        // convert to grayscale
        IplImage grayImg = IplImage.create(origImg.width(),origImg.height(), IPL_DEPTH_8U, 1);
        cvCvtColor(origImg, grayImg, CV_BGR2GRAY);
           
        // scale the grayscale (to speed up face detection)
        IplImage smallImg = IplImage.create(grayImg.width()/SCALE,grayImg.height()/SCALE, IPL_DEPTH_8U, 1);
        cvResize(grayImg, smallImg, CV_INTER_LINEAR);

        // equalize the small grayscale
        IplImage equImg = IplImage.create(smallImg.width(),smallImg.height(), IPL_DEPTH_8U, 1);
        cvEqualizeHist(smallImg, equImg);

        // create temp storage, used during object detection
        CvMemStorage storage = CvMemStorage.create();

        // instantiate a classifier cascade for face detection

        CvHaarClassifierCascade cascade = new CvHaarClassifierCascade(cvLoad(CASCADE_FILE));
        CvSeq faces;
        faces = cvHaarDetectObjects(equImg, cascade, storage, 1.1, 3, CV_HAAR_FIND_BIGGEST_OBJECT);
            //faces = cvHaarDetectObjects(equImg, cascade, storage, 1.1, 3, CV_HAAR_DO_CANNY_PRUNING);
            //TIPO: CvSeq faces = cvHaarDetectObjects(image, cascade, storage, scale_factor, min_neighbors, flags);
            cvClearMemStorage(storage);
            
            // draw thick yellow rectangles around all the faces
            int total = faces.total();
            for (int i = 0; i < total; i++) {
            		CvRect r = new CvRect(cvGetSeqElem(faces, i));
                    cvRectangle(origImg, cvPoint( r.x()*SCALE, r.y()*SCALE ),cvPoint( (r.x() + r.width())*SCALE,(r.y() + r.height())*SCALE ),CvScalar.GREEN, 2, CV_AA, 0);
            }
            grayImg.release();
            smallImg.release();
			return origImg;
    }
}
