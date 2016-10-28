package objects;
import geom.Vector2D;

public class CollisionData {
	public Vector2D correction;
	public boolean top, bottom, left, right;
	
	public CollisionData(Vector2D correction, boolean top, boolean bottom, boolean left, boolean right) {
		this.correction = correction;
		this.top        = top;
		this.bottom     = bottom;
		this.left       = left;
		this.right      = right;
	}
}