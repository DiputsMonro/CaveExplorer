package geom;

public class Vector2D {
	private final double x, y;
	private final double length;
	
	public Vector2D (double x, double y){
		this.x = x;
		this.y = y;
		length = Math.sqrt( Math.pow(x, 2) + Math.pow(y, 2));
	}
	
	public Vector2D add(Vector2D other) {	
		return new Vector2D(x + other.getX(), y + other.getY());
	}
	public Vector2D sub(Vector2D other) {
		return new Vector2D(x - other.getX(), y - other.getY());
	}
	
	public Vector2D mult(double factor) {
		return new Vector2D(x * factor, y * factor);
	}	
	public Vector2D div(double factor) {
		return new Vector2D(x / factor, y / factor);
	}
	
	public Vector2D abs() {
		return new Vector2D( Math.abs(x), Math.abs(y));
	}
	
	public Vector2D copy() {
		return new Vector2D( x, y );
	}
	
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
	
	public double getX() { return x; }
	public double getY() { return y; }
	public int getXi() { return (int) x; }
	public int getYi() { return (int) y; }
	
	public double length() { return length; }
	
}
