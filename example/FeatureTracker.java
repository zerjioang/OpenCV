import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_features2d.*;
import static com.googlecode.javacv.cpp.opencv_objdetect.*;
import static com.googlecode.javacv.cpp.opencv_video.*;

import java.util.ArrayList;

import javax.swing.text.DefaultEditorKit.CopyAction;

import com.googlecode.javacpp.Pointer;
import com.googlecode.javacv.*;
import com.googlecode.javacv.cpp.opencv_core.CvArr;
import com.googlecode.javacv.cpp.opencv_core.CvPoint2D32f;
import com.googlecode.javacv.cpp.opencv_core.CvSize;
import com.googlecode.javacv.cpp.opencv_core.CvTermCriteria;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_core.*;

public class FeatureTracker {
	/** Detect and track moving features in a series of images.
	  *
	  * Described in section "Tracking feature points in video", chapter 10.
	  *
	  * @param maxCount 	maximum number of features to detect
	  * @param qLevel   quality level for feature detection
	  * @param minDist minimum distance between two feature points
	  */
	
	private static int MAX_COUNT=500;
	private static double Q_LEVEL=0.01;
	private static double MIN_DIST=10;
	private static int minNumberOfTrackedPoints = 10;
	
	/** initial position of tracked points */
	ArrayList<CvPoint2D32f> initialPositions = new ArrayList<CvPoint2D32f>();

    /** tracked features from 0->1 */
	ArrayList<CvPoint2D32f> trackedPoints = new ArrayList<CvPoint2D32f>();
    
    /** previous gray-level image */
    private IplImage grayPrevious = null;
	
    public FeatureTracker(){
	}
	public IplImage process(IplImage frame){
		IplImage gray = IplImage.create(frame.cvSize(), frame.depth(), 1);
		// convert to gray-level image
		cvCvtColor(frame, gray, CV_RGB2GRAY);
		// 1. Check if additional new feature points should be added
		if (shouldAddNewPoints()) {
            // detect feature points
            CvPoint2D32f features = detectFeaturePoints(gray);
            // add the detected features to the currently tracked features
            initialPositions.add(features);
            trackedPoints.add(features);
		}
		//System.out.println(grayPrevious+" "+gray+" "+(grayPrevious == null));
		// for first image of the sequence
        if (grayPrevious == null) {
            grayPrevious = IplImage.create(gray.cvSize(), gray.depth(), gray.nChannels());
            cvCopy(gray, grayPrevious, null);
        }
        // 2. track features
        ArrayList<CvPoint2D32f> trackedPointsNewUnfilteredOCV = new ArrayList<CvPoint2D32f>();
        //CvPoint2D32f trackedPointsNewUnfilteredOCV = new CvPoint2D32f(trackedPoints.size());
        //val trackingStatus = new Array[Byte](trackedPoints.length)
        byte[] trackingStatus = new byte[trackedPoints.size()];
        
        CvPoint2D32f input = toNativeVector(toArray(trackedPoints));
        CvPoint2D32f output = toNativeVector(toArray(trackedPointsNewUnfilteredOCV));
		cvCalcOpticalFlowPyrLK(
	            grayPrevious, gray, // 2 consecutive images
	            null, null, // Unused
	            input, // input point position in previous image
	            output, // output point position in the current image
	            trackedPoints.size(),
	            new CvSize(21, 21), 3, // Defaults
	            trackingStatus , // tracking success
	            new float[trackedPoints.size()], // Not used
	            new CvTermCriteria(CV_TERMCRIT_ITER + CV_TERMCRIT_EPS, 30, 0.01), 0 // defaults
	        );
        // 2. loop over the tracked points to reject the undesirables
        ArrayList<CvPoint2D32f> trackedPointsNewUnfiltered = trackedPointsNewUnfilteredOCV;
        System.out.println(trackedPointsNewUnfiltered.size());
		ArrayList<CvPoint2D32f> initialPositionsNew = new ArrayList<CvPoint2D32f>();
		ArrayList<CvPoint2D32f> trackedPointsNew = new ArrayList<CvPoint2D32f>();
        for (int i=0; i< trackedPointsNewUnfiltered.size(); i++) {
            if (acceptTrackedPoint(trackingStatus[i], trackedPoints.get(i), trackedPointsNewUnfiltered.get(i))) {
                initialPositionsNew.add(initialPositions.get(i));
                trackedPointsNew.add(trackedPointsNewUnfiltered.get(i));
            }
        }

        // Prepare output
        IplImage outputImage = IplImage.create(frame.cvSize(), frame.depth(), frame.nChannels());
        cvCopy(frame, outputImage, null);

        // 3. handle the accepted tracked points
        visualizeTrackedPoints(initialPositionsNew, trackedPointsNew, frame, outputImage);

        // 4. current points and image become previous ones
        trackedPoints = trackedPointsNew;
        initialPositions = initialPositionsNew;
        grayPrevious = gray.clone();
        return outputImage;
	}
	/** Feature point detection. */
	private CvPoint2D32f detectFeaturePoints(IplImage grayImg){
		CvPoint2D32f featurePoints = new CvPoint2D32f(MAX_COUNT);
         int[] featureCount = new int[MAX_COUNT];
        // detect the features
        cvGoodFeaturesToTrack(grayImg, // the image
            null, null, // ignored parameters
            featurePoints, // the output detected features
            featureCount,
            Q_LEVEL, // quality level
            MIN_DIST, // min distance between two features
            null, 3, 0, 0.04 // Default parameters
        );
        // Select only detected features, end of the vector do not have valid entries
        return featurePoints;		
	}
	/** Determine if new points should be added. */
	private  boolean shouldAddNewPoints(){
		return trackedPoints.size() < minNumberOfTrackedPoints;
	}
	/** Determine if a tracked point should be accepted. */
	private boolean acceptTrackedPoint(int status, CvPoint2D32f point0, CvPoint2D32f point1){
		return (status!=0 && // if point has moved
                (Math.abs(point0.x() - point1.x()) + Math.abs(point0.y() - point1.y())) > 2);
	}
	/** display the currently tracked points */
	private void visualizeTrackedPoints(ArrayList<CvPoint2D32f> startPoints, ArrayList<CvPoint2D32f> endPoints, IplImage frame, IplImage output){
		 // for all tracked points
        for (int i=0; i< startPoints.size(); i++) {
        	CvPoint startPoint = cvPointFrom32f(startPoints.get(i));
        	CvPoint endPoint = cvPointFrom32f(endPoints.get(i));
            // Mark tracked point movement with aline
            cvLine(output, startPoint, endPoint, CvScalar.WHITE, 3, CV_AA, 0);
            // Mark starting point with circle
            cvCircle(output, startPoint, 3, CvScalar.WHITE, 3, CV_AA, 0);
        }
	}

	private CvPoint2D32f[] toArray(CvPoint2D32f points){
		int oldPosition = points.position();
		CvPoint2D32f[] dest = new CvPoint2D32f[points.capacity()];
		// Convert points to sequence
		//val dest = for (i <- Array.range(0, points.capacity)) yield new CvPoint2D32f(points.position(i))
		for (int i = 0; i < points.capacity(); i++) {
			dest [i] = new CvPoint2D32f(points.position(i));
		}
		// Reset position explicitly to avoid issues from other uses of this position-based container.
		points.position(oldPosition);
		return dest;
	}
	
	private CvPoint2D32f[] toArray(ArrayList<CvPoint2D32f> p) {
		CvPoint2D32f[] dest = new CvPoint2D32f[p.size()];
		for (int i = 0; i < dest.length; i++) {
			dest[i] = p.get(i);
		}
		return dest;
	}
	
	private CvPoint2D32f toNativeVector(CvPoint2D32f[] src){
		CvPoint2D32f dest = new CvPoint2D32f(src.length);
        for (int i = 0; i<src.length; i++){
            // Since there is no way to `put` objects into a vector CvPoint2D32f,
            // We have to reassign all values individually, and hope that API will not any new ones.
            copy(src[i], dest.position(i));
        }
        // Set position to 0 explicitly to avoid issues from other uses of this position-based container.
        dest.position(0);
        return dest;
	}
	
	private void copy(Pointer src, Pointer dest) {
		dest.put(src.limit(src.position()+1));
	}
}
