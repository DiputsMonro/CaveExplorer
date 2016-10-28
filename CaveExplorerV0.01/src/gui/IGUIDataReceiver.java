package gui;

public interface IGUIDataReceiver<ID>{
	
	/* Registers an IGUIDataComponent to give input to this receiver with the ID id */
	public void registerDataInput(ID id, IGUIDataComponent c);
	
	/* Notify this receiver about updates from a DataComponent */
	public void notify(IGUIDataComponent c, Object arg);
	
	/* Update this receiver using the argument given by the DataComponent associated with the ID id */
	public void update(ID id, Object arg);
	
}
