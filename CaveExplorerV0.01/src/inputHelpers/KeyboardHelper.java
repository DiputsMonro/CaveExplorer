package inputHelpers;
import org.lwjgl.input.Keyboard;

public class KeyboardHelper {
	private static int numKeys = Keyboard.getKeyCount();
	private static boolean[] downLastFrame = new boolean[numKeys];
	private static boolean[] downThisFrame = new boolean[numKeys];
	
	/*
	 * Simply returns a boolean that indicates whether the
	 * specified Keyboard button has been clicked since the
	 * last time KeyboardHelper.refresh() was called.
	 */
	public static boolean pressed(int button) {
		//downThisFrame[button] = Keyboard.isButtonDown(button);
		int index = Keyboard.getKeyIndex(Keyboard.getKeyName(button));
		
		/*
		System.out.format("button: %d,  name: %s,  index: %d,  max: %d\n", 
				button, Keyboard.getKeyName(button), index, numKeys);
		*/
		if (!downLastFrame[index] && downThisFrame[index])
			return true;
		
		return false;
	}
	
	/*
	 * Refreshes KeyboardHelper, should be called once at the end
	 * (or beginning) of each logic loop.
	 */
	public static void refresh() {
		for (int button = 0; button < numKeys; button++) {
			downLastFrame[button] = downThisFrame[button];
			downThisFrame[button] = Keyboard.isKeyDown(button);
		}
	}

	public static void consume(Integer button) {
		int index = Keyboard.getKeyIndex(Keyboard.getKeyName(button));
		
		downThisFrame[index] = true;
		downLastFrame[index] = true;
	}
	
	
}

