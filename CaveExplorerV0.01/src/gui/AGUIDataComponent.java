package gui;

import java.util.ArrayList;

import geom.Vector2D;

public abstract class AGUIDataComponent extends AGUIComponent implements IGUIDataComponent {
	private ArrayList<IGUIDataReceiver> receivers;
	
	public AGUIDataComponent() {
		receivers = new ArrayList<IGUIDataReceiver>();
	}
	
	public void addReceiver(IGUIDataReceiver r) {
		receivers.add(r);
	}

	public void notifyReceivers(Object arg) {
		for (IGUIDataReceiver r : receivers)
			r.notify(this, arg);
	}

	public abstract void update(Vector2D mpos);
	public abstract void draw();
}
