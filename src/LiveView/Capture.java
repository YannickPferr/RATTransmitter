package LiveView;

import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameGrabber.Exception;

public abstract class Capture {
	
	protected FrameGrabber grabber;	
	
	
	public Capture(int captureDevice) {
		initGrabber(captureDevice);
	}
	
	protected abstract void initGrabber(int captureDevice);
	
	
	public boolean startGrabber(){
		try {
			grabber.start();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public Frame getFrame(){
		
		Frame frame = null;
		try {
			 frame = grabber.grab();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return frame;
	}
	
	public Frame getSingleFrame(){
		startGrabber();
		Frame f = getFrame();
		stopGrabber();
		
		return f;
	}
	
	public void stopGrabber(){
		try {
			grabber.stop();
			grabber.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
