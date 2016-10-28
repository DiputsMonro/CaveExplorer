package gui;

import geom.Vector2D;

public interface IGUIComponent {
	public void update(Vector2D mpos);
	
	public void draw();

	public boolean inBounds(Vector2D pos);

	public void setPos(Vector2D pos);
	
	public Vector2D getSize();
}
