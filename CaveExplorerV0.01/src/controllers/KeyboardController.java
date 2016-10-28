package controllers;
import inputHelpers.KeyboardHelper;

import java.util.HashMap;
import org.lwjgl.input.Keyboard;


import java.util.*;


public class KeyboardController implements ControllerInterface {
	HashMap<Controls, Integer> keyMap;
	
	public KeyboardController() {
		keyMap = new HashMap<Controls, Integer>();
		
		keyMap.put(Controls.LEFT, Keyboard.KEY_A);
		keyMap.put(Controls.RIGHT, Keyboard.KEY_D);
		keyMap.put(Controls.UP, Keyboard.KEY_W);
		keyMap.put(Controls.DOWN, Keyboard.KEY_S);
		keyMap.put(Controls.JUMP, Keyboard.KEY_J);
		keyMap.put(Controls.SPRINT, Keyboard.KEY_SEMICOLON);
		keyMap.put(Controls.CLIMB,  Keyboard.KEY_K);
		keyMap.put(Controls.USE_ITEM, Keyboard.KEY_I);
		keyMap.put(Controls.SELECT, Keyboard.KEY_RETURN);
		keyMap.put(Controls.PAUSE, Keyboard.KEY_ESCAPE);
		keyMap.put(Controls.DIG, Keyboard.KEY_L);
	}
	
	public boolean pressed(Controls action) {
		return KeyboardHelper.pressed( keyMap.get(action) );
	}
	
	public boolean held(Controls action) {
		return Keyboard.isKeyDown( keyMap.get(action) );
	}
	
	public void remap(Controls newControl, Integer key) {
		keyMap.put(newControl, key);
	}

	public void consume(Controls action) {
		KeyboardHelper.consume( keyMap.get(action) );
	}
}
