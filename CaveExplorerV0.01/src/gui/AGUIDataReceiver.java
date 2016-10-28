package gui;

import java.util.HashMap;

public abstract class AGUIDataReceiver<ID> implements IGUIDataReceiver<ID> {	
	private HashMap<IGUIDataComponent, ID> map;
	
	public AGUIDataReceiver() {
		map = new HashMap<IGUIDataComponent, ID>();
	}
	
	public void registerDataInput(ID id, IGUIDataComponent c) {
		map.put(c, id);
		c.addReceiver(this);
	}
	
	public void notify(IGUIDataComponent c, Object arg) {
		update(map.get(c), arg);
	}
	
	public abstract void update(ID id, Object arg);
}
