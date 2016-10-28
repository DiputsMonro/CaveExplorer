package gui;

import geom.Vector2D;

public abstract class AGUIComponent implements IGUIComponent {
	Vector2D pos;
	int width, height;

	public boolean inBounds(Vector2D pos) {
		if (pos.getX() >= 0 && pos.getX() <= width && pos.getY() >= 0 && pos.getY() <= height)
			return true;
		
		return false;
	}

	public void setPos(Vector2D pos) {
		this.pos = pos;
	}
	
	public Vector2D getSize() { return new Vector2D(width, height); }
	
	public abstract void update(Vector2D mpos);
	public abstract void draw();
}
