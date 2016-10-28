package gui;

public interface IGUIDataComponent extends IGUIComponent {
	
	/* Add a receiver to receive updates from this data component */
	public void addReceiver(IGUIDataReceiver r);
	
	/* Notify receivers about changes in this object */
	public void notifyReceivers(Object arg);
	
}
