package geom;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

import java.util.*;

public class Rectangle2D {
	private Vector2D pos;
	ArrayList<Vector2D> points;
	ArrayList<Line2D> lines;
	private double width;
	private double height;
	double rotation;
	double radius;
	
	public Rectangle2D(double posX, double posY, double width, double height, double rotation) {
		setPos(new Vector2D(posX, posY));
		this.setWidth(width);
		this.setHeight(height);
		this.rotation = rotation;
		calcPoints();
	}
	
	public Rectangle2D(Vector2D pos, double width, double height, double rotation) {
		this.setPos(pos);
		this.setWidth(width);
		this.setHeight(height);
		this.rotation = rotation;
		calcPoints();
	}
	
	void calcPoints() {
		radius = sqrt( pow(this.getWidth(),2) + pow(this.getHeight(),2) ) * .5;
		points = new ArrayList<Vector2D>();
		
		double normOffset = atan2(-getHeight(), -getWidth());
		double angle = normOffset + rotation;
		points.add ( new Vector2D ( (cos(angle) * radius + mid().getX()), 
		                            (sin(angle) * radius + mid().getY()) ) );
		
		normOffset = atan2(-getHeight(), getWidth());
		angle = normOffset + rotation;
		points.add ( new Vector2D ( (cos(angle) * radius + mid().getX()), 
		                            (sin(angle) * radius + mid().getY()) ) );
		
		normOffset = atan2(getHeight(), getWidth());
		angle = normOffset + rotation;
		points.add ( new Vector2D ( (cos(angle) * radius + mid().getX()), 
		                            (sin(angle) * radius + mid().getY()) ) );
		
		normOffset = atan2(getHeight(), -getWidth());
		angle = normOffset + rotation;
		points.add ( new Vector2D ( (cos(angle) * radius + mid().getX()), 
		                            (sin(angle) * radius + mid().getY()) ) );	
		
		lines = new ArrayList<Line2D>();
		for (int x = 0; x < 4; x++) {
			int next = (x < 3 ? x+1 : 0);
			lines.add(new Line2D(points.get(x), points.get(next) ) );
		}
	}
	
	public Vector2D move(Vector2D movt) { 
		setPos(getPos().add(movt));
		return getPos();
	}
	
	public void setPos(Vector2D newPos) {
		pos = newPos;
	}

	public boolean encloses(Vector2D point) {
		Vector2D diff = mid().sub(point);
		
		if ( Math.abs(diff.getX()) <= getWidth()/2 &&
			 Math.abs(diff.getY()) <= getHeight()/2 ) {
			return true;
		}
		
		return false;
	}
	
	public boolean collision(Rectangle2D other) {
		Vector2D diff = mid().sub( other.mid() );
		
		if ( Math.abs(diff.getX()) <= (getWidth() + other.width())/2 &&
			 Math.abs(diff.getY()) <= (getHeight() + other.height())/2 ) {
			return true;
		}
		
		return false;
	}
	
	public Vector2D pos() { return getPos(); }
	public Vector2D mid() { return getPos().add((new Vector2D(getWidth(), getHeight())).div(2)); }
	public Vector2D opp() { return getPos().add( new Vector2D(getWidth(), getHeight())); }
	
	public double getX() { return pos().getX(); }
	public double getY() { return pos().getY(); }
	public int getXi() { return (int) getX(); }
	public int getYi() { return (int) getY(); }
	
	public double getOppX() { return opp().getX(); }
	public double getOppY() { return opp().getY(); }
	public int getOppXi() { return (int) getOppX(); }
	public int getOppYi() { return (int) getOppY(); }
	
	public Vector2D getPoint(int n) { return points.get(n); }
	public ArrayList<Vector2D> points() { return points; }
	public Line2D getLine(int n) { return lines.get(n); }
	public ArrayList<Line2D> lines() { return lines; }
	
	public double width() { return getWidth(); }
	public double height() { return getHeight(); }
	public double rotation() { return rotation; }
	
	
	public Rectangle2D copy() {
		return new Rectangle2D(getPos(), getWidth(), getHeight(), rotation);
	}

	public Vector2D getPos() {
		return pos;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}
	
	
}
