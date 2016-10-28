package inputHelpers;
import geom.Vector2D;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

public class MouseHelper {
	private static int numButtons = 3;
	private static boolean[] downLastFrame = new boolean[numButtons];
	private static boolean[] downThisFrame = new boolean[numButtons];
	private static Vector2D pos;
	
	/*
	 * Simply returns a boolean that indicates whether the
	 * specified Mouse button has been clicked since the
	 * last time MouseHelper.refresh() was called.
	 */
	public static boolean click(int button) {
		//downThisFrame[button] = Mouse.isButtonDown(button);
		
		if (!downLastFrame[button] && downThisFrame[button])
			return true;
		
		return false;
	}
	
	/*
	 * Like MouseHelper.click(), except that it returns a 
	 * Vector2D indicating the position of the mouse when 
	 * the specified button was clicked, or null if it wasn't.
	 */
	public static Vector2D clickLoc(int button) {
		//downThisFrame[button] = Mouse.isButtonDown(button);
		
		if (!downLastFrame[button] && downThisFrame[button])
			return pos;
		
		return null;
	}
	
	public static Vector2D pos() {
		return pos;
	}
	
	/*
	 * Refreshes MouseHelper, should be called once at the end
	 * (or beginning) of each logic loop.
	 */
	public static void refresh() {
		for (int button = 0; button < numButtons; button++) {
			downLastFrame[button] = downThisFrame[button];
			downThisFrame[button] = Mouse.isButtonDown(button);
		}
		
		pos = new Vector2D(Mouse.getX(), Display.getHeight() - Mouse.getY());
	}
	
}
