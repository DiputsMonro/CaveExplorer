package geom;
import static java.lang.Math.*;

public class Line2D {
	Vector2D p1, p2;
	double dx, dy;
	double length;
	double slope;
	
	public Line2D(Vector2D p1, Vector2D p2) {
		this.p1 = p1;
		this.p2 = p2;
		dx = abs(p1.getX() - p2.getX());
		dy = abs(p1.getY() - p2.getY());
		
		slope = (dx != 0 ? dy/dx : Double.POSITIVE_INFINITY);
		
		length = sqrt( pow(dx,2) + pow(dy,2) );
	}
	
	public boolean intersects(Line2D other) {
		return java.awt.geom.Line2D.linesIntersect( p1.getX(), p1.getY(), 
													p2.getX(), p2.getY(),
													other.p1.getX(), other.p1.getY(), 
													other.p2.getX(), other.p2.getY() );
		
	}
	
	public boolean horizontal() { return (dy == 0); }
	public boolean vertical() { return (dx == 0); }
	
	public double length() { return length; }
	
}
