package LiveView;

import java.awt.Dimension;
import java.awt.Toolkit;
import org.bytedeco.javacv.FFmpegFrameGrabber;

public class ScreenCapture extends Capture {

	

	public ScreenCapture() {
		super(-1);
	}

	@Override
	public void initGrabber(int captureDevice) {
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

		grabber = new FFmpegFrameGrabber("desktop");
		grabber.setFormat("gdigrab");
		grabber.setImageWidth(screen.width);
		grabber.setImageHeight(screen.height);
	}
}
