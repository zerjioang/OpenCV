import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_legacy.CvFaceTracker;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.cvWaitKey;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_objdetect.*;

public class VideoFaceDetectionHaar {

        private static final int SCALE = 2;
        // scaling factor to reduce size of input image
        // cascade definition for face detection

        public static void main(String[] args){
    		OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
    		try {
    			grabber.start();
    			IplImage frame = grabber.grab();
    			CanvasFrame canvasFrame = new CanvasFrame("Camara");
    	        canvasFrame.setCanvasSize(frame.width(), frame.height());
    	        canvasFrame.setLocationRelativeTo(null);
    	        canvasFrame.setDefaultCloseOperation(CanvasFrame.DISPOSE_ON_CLOSE);
    	        while((frame = grabber.grab()) != null && canvasFrame.isVisible()){
    	        	cvFlip(frame, frame, 1);
    	        	canvasFrame.showImage(detectarCaras(frame));
    	        }
    	        grabber.stop();
    	        grabber.release();
    		} catch (Exception e) {
    			e.printStackTrace();
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
            faces = cvHaarDetectObjects(equImg, cascade, storage, 1.1, 3, CV_HAAR_DO_CANNY_PRUNING);
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
                //equImg.release();
				return origImg; 
        }	
}
