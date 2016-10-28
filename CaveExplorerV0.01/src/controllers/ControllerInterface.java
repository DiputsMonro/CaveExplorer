package controllers;

/* 
 * An Interface to abstract functionality of the input device, such that any input device
 * can be used
 */
public interface ControllerInterface {
	public boolean pressed(Controls action);
	public boolean held(Controls action);
	public void remap(Controls newControl, Integer key);
	public void consume(Controls pause);
}
