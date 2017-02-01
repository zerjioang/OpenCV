import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.*;
import com.googlecode.javacv.OpenCVFrameGrabber;;

public class DisplayVideo {
	
	static boolean pulse = true;
	
	public static void main(String[] args) {
		//Declare FrameGrabber to import video from file
		FrameGrabber grabber = new OpenCVFrameGrabber("src/img/video1.avi");
		try {
			//Start grabber to capture video
			grabber.start();
			//Declare img as IplImage
			IplImage img;
			//Create canvas frame for displaying video.
			CanvasFrame canvas = new CanvasFrame("VideoCam Player");
			canvas.setVisible(false);
			//Set Canvas frame to close on exit
			canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
			canvas.setCanvasSize(grabber.getImageWidth(), grabber.getImageHeight());
			canvas.setLocationRelativeTo(null);
			canvas.setVisible(true);
			
			canvas.addKeyListener(new KeyListener() {
				public void keyTyped(KeyEvent arg0) {}
				public void keyReleased(KeyEvent arg0) {}
				
				@Override
				public void keyPressed(KeyEvent arg0) {
					//System.out.println(arg0.getKeyCode());
					if(arg0.getKeyCode() == 32 ){
						pulse = !pulse;
					}
				}
			});
			
			while (true) {
				while(pulse){
					//inser grabed video fram to IplImage img
					img = grabber.grab();
					//Set canvas size as per dimentions of video frame.
					if (img != null) {
						//Show video frame in canvas
						canvas.showImage(img);
					}
				}
			}
			}
		catch (Exception e) 
		{
		}
	}
}