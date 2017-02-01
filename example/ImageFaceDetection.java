import java.io.File;
//import javax.swing.JFrame;
//import com.googlecode.javacv.*;
//import com.googlecode.javacv.cpp.*;
//import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_objdetect.*;

public class ImageFaceDetection {

        private static final int SCALE = 2;
        // scaling factor to reduce size of input image
        // cascade definition for face detection

        public static void main(String[] args){
        	File[] lista = new File("C:\\Documents and Settings\\Administrador\\Escritorio\\testImages").listFiles();
        	//System.out.println("Starting OpenCV...");
        	for (int i = 0; i < lista.length; i++) {
        		String ruta = lista[i].getAbsolutePath();
        		boolean val=(cvLoadImage(ruta))!=null;
        		if(val){
        			detectarCaras(ruta);
        		}
			}
        }

		private static void detectarCaras(String filename) {
        	String name = nombreFoto(filename);

        	String CASCADE_FILE ="src/img/haarcascade_frontalface_alt2.xml";
        	//CASCADE_FILE ="src/img/haarcascade_frontalface_alt_tree.xml";
        	//CASCADE_FILE ="src/img/Custom_haarcascade_frontalface.xml";
        	//CASCADE_FILE ="src/img/cascade.xml";
            String OUT_FILE = name+"_faceMarked.jpg";
            //preload the opencv_objdetect module to work around a known bug
            //Loader.load(opencv_objdetect.class);
               
            // load an image
            //System.out.println("Loading image from " + args[0]);
               
            IplImage origImg = cvLoadImage(filename, 1);
            //IplImage origImg = cvLoadImage(args[0]);
               
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

            CvHaarClassifierCascade cascade =new CvHaarClassifierCascade(cvLoad(CASCADE_FILE));
            System.out.println("Detecting faces...");
            CvSeq faces;
            //double scale = calcScale(origImg);
            //int min = CalcMin(origImg);
            //System.out.println(scale+" "+min);
            faces = cvHaarDetectObjects(equImg, cascade, storage, 1.1, 3, CV_HAAR_DO_ROUGH_SEARCH);
                //faces = cvHaarDetectObjects(equImg, cascade, storage, 1.1, 3, CV_HAAR_DO_CANNY_PRUNING);
                //TIPO: CvSeq faces = cvHaarDetectObjects(image, cascade, storage, scale_factor, min_neighbors, flags);
                cvClearMemStorage(storage);

                // draw thick yellow rectangles around all the faces
                int total = faces.total();
                if(total==0){
                	System.out.println("No faces detected");
                }
                else if(total==1){
                	System.out.println("One face detected");
                }
                else{
                	System.out.println("Total faces detected: "+total);
                }
                
                for (int i = 0; i < total; i++) {

                        CvRect r = new CvRect(cvGetSeqElem(faces, i));
                        cvRectangle(origImg, cvPoint( r.x()*SCALE, r.y()*SCALE ),cvPoint( (r.x() + r.width())*SCALE,(r.y() + r.height())*SCALE ),CvScalar.GREEN, 6, CV_AA, 0);
 
                        //String strRect = String.format("CvRect(%d,%d,%d,%d)", r.x(), r.y(), r.width(), r.height());
                        //System.out.println(strRect);
                        //undo image scaling when calculating rect coordinates
                }
               
                if (total > 0) {
                        System.out.println("Saving marked-faces version in " + OUT_FILE);
                        cvSaveImage(OUT_FILE, origImg);
                }  
        }

		private static int CalcMin(IplImage img) {
			int d = (img.width()+img.height())/800;
			if(img.width()>1500){
				return d-1;
			}
			else{
				return d;
			}
		}

		private static double calcScale(IplImage img) {
			int alto = img.height();
			int ancho = img.width();
			int div=500;
			if(alto>ancho){
				if(alto<=div){
					return 1.1;
				}
				else{
					return alto/div+0.1;
				}
			}
			else{
				if(alto<=div){
					return 1.1;
				}
				else{
					return ancho/div+0.1;
				}
			}
		}

		private static String nombreFoto(String s) {
			int point = s.indexOf(".");
			int sep = s.lastIndexOf("/");
			if(sep==-1){
				sep = s.lastIndexOf("\\");
			}
			if(point!=-1){
				return s.substring(sep+1, point);
			}
			else{
				return "";
			}
		}
		
}
