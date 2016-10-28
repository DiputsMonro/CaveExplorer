package inputHelpers;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;
import org.lwjgl.util.input.ControllerAdapter;


public class GamepadHelper {
	public static Controller gamepad;
	
	public static void init() {
		try {
			Controllers.create();
			gamepad = Controllers.getController(0);
		} catch (Exception e) {
			System.err.format("Unable to initialize gamepads!\n");
			e.printStackTrace();
		}
	}
	
	
}
