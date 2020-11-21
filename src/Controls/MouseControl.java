package Controls;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;

public class MouseControl {

	private Robot r;
	private Dimension screen;
	private volatile boolean mouseInversion = false;
	
	public MouseControl() {
		try {
			r = new Robot();
			screen = Toolkit.getDefaultToolkit().getScreenSize();
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
	
	public void moveMouse(int x, int y){
		r.mouseMove(x, y);
	}
	
	private void leftMouseClick(){
		r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		r.delay(200);
		r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
	}
	
	private void doubleMouseClick(){
		leftMouseClick();
		r.delay(200);
		leftMouseClick();
	}
	
	public void mouseClick(int x, int y){
		moveMouse(x, y);
		leftMouseClick();
	}
	
	public void doubleMouseClick(int x, int y){
		moveMouse(x, y);
		doubleMouseClick();
	}
	
	public void buttonClick(int button){
		System.out.println(button);
		r.keyPress(button);
		r.delay(200);
		r.keyRelease(button);
	}
	
	public void buttonPress(int button){
		r.keyPress(button);
	}
	
	public void buttonRelease(int button){
		r.keyRelease(button);
	}
	
	public void stopMouseInversion(){
		mouseInversion = false;
	}
	
	public boolean getMouseInversion(){
		return mouseInversion;
	}
	
	public void invertMouse(){	
		mouseInversion = true;
		
		new Thread(new Runnable() {
			
			public void run() {
				Point start = MouseInfo.getPointerInfo().getLocation();
				
				while(mouseInversion){
					Point current =  MouseInfo.getPointerInfo().getLocation();
					
					if(current.getX() == 0 || current.getX() == screen.getWidth() - 1
							|| current.getY() == 0 || current.getY() == screen.getHeight() - 1){
						moveMouse((int)screen.getWidth()/2, (int)screen.getHeight()/2);
						start = new Point((int)screen.getWidth()/2, (int)screen.getHeight()/2);
						continue;
					}
					
					double distanceX = current.getX() - start.getX();
					double distanceY = current.getY() - start.getY();
					moveMouse((int)(start.getX() - distanceX), (int)(start.getY() - distanceY));
					start = new Point((int)(start.getX() - distanceX), (int)(start.getY() - distanceY));
				}
			}
		}).start();
	}
}
