package LiveView;

import org.bytedeco.javacv.OpenCVFrameGrabber;

public class WebcamCapture extends Capture{

	
	public WebcamCapture(int captureDevice) {
		super(captureDevice);
	}

	@Override
	public void initGrabber(int inputDevice) {
		grabber =  new OpenCVFrameGrabber(inputDevice); 
	}
}
