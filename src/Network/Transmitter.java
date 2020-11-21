package Network;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import Controls.MouseControl;
import LiveView.Capture;
import LiveView.ScreenCapture;
import LiveView.WebcamCapture;

public class Transmitter extends Thread {

	private String address;
	private int port;

	private Socket server;
	private BufferedReader br;
	private Java2DFrameConverter converter;

	private MouseControl mc;

	// Frame Transfer State
	private volatile boolean running = false;

	public Transmitter(String address, int port) {

		this.address = address;
		this.port = port;
		converter = new Java2DFrameConverter();
		mc = new MouseControl();

		System.out.println("Connecting to " + address + " at port " + port + " ...");
	}

	@Override
	public void run() {
		while (!connect())
			;

		while (listen())
			;
	}

	private boolean connect() {
		try {
			server = new Socket(address, port);
			br = new BufferedReader(new InputStreamReader(server.getInputStream()));

		} catch (IOException e) {
			return false;
		}
		return true;
	}

	private boolean listen() {

		try {
			String command = br.readLine();

			if (command.equals("w0"))
				new Thread(new Runnable() {

					public void run() {
						startFrameTransfer(CommuniationType.WEBCAM, 0);
					}
				}).start();

			else if (command.equals("w1"))
				new Thread(new Runnable() {

					public void run() {
						startFrameTransfer(CommuniationType.WEBCAM, 1);
					}
				}).start();

			else if (command.equals("d"))
				new Thread(new Runnable() {

					public void run() {
						startFrameTransfer(CommuniationType.DESKTOP, 0);
					}
				}).start();

			else if (command.equals("s0")) {
				singleFrameTransfer(0);
			}

			else if (command.equals("s1")) {
				singleFrameTransfer(1);
			}

			else if (command.equals("i")) {
				if (!mc.getMouseInversion())
					mc.invertMouse();
			}

			else if (command.equals("stop")) {
				stopFrameTransfer();
				mc.stopMouseInversion();
			}

			else if (command.equals("exit")) {
				stopFrameTransfer();
				mc.stopMouseInversion();
				return false;
			}
			else if(command.startsWith("KEY:"))
				mc.buttonClick(Integer.parseInt(command.substring(4, command.length())));
			else {
				String[] point = command.split(Pattern.quote("|"));
				if (point.length <= 1)
					return true;
				float sourceWidth = Float.parseFloat(point[2]);
				float sourceHeight = Float.parseFloat(point[3]); 
				Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
				int x = (int)((Integer.parseInt(point[0]) / sourceWidth) * screen.getWidth());
				int y = (int)((Integer.parseInt(point[1]) / sourceHeight) * screen.getHeight());
				mc.mouseClick(x, y);
			}

		} catch (IOException e) {
			return false;
		}
		return true;
	}

	private void singleFrameTransfer(int captureDevice) {
		try {
			DataOutputStream out = new DataOutputStream(new BufferedOutputStream(server.getOutputStream()));
			Capture capture = new WebcamCapture(captureDevice);
			transmitFrame(capture.getSingleFrame(), out);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void startFrameTransfer(CommuniationType captureDevice, int device) {

		Capture capture;
		if (captureDevice.equals(CommuniationType.WEBCAM))
			capture = new WebcamCapture(device);
		else if (captureDevice.equals(CommuniationType.DESKTOP))
			capture = new ScreenCapture();
		else
			return;

		running = capture.startGrabber();

		try {
			DataOutputStream out = new DataOutputStream(new BufferedOutputStream(server.getOutputStream()));

			if(running == false){
				out.writeInt(-1);
				out.flush();
			}
			while (running)
				transmitFrame(capture.getFrame(), out);

			capture.stopGrabber();
		} catch (IOException e) {
			e.printStackTrace();
			running = false;
		}
	}

	private void transmitFrame(Frame frame, DataOutputStream out) throws IOException {

		BufferedImage img = converter.convert(frame);

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ImageIO.write(img, "png", bos);
		byte imgBytes[] = bos.toByteArray();
		out.writeInt(imgBytes.length);
		out.write(imgBytes);
		bos.close();

		img = null;
		imgBytes = null;
	}

	private void stopFrameTransfer() {
		running = false;
	}

	public static void main(String[] args) {
		String address = "localhost";
		if (args.length > 0)
			address = args[0];
		Transmitter t = new Transmitter(address, 1234);
		t.start();
	}
}
