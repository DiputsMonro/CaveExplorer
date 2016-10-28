package objects;
import game.World;
import geom.Vector2D;


public class Solid extends GameObject {
	Vector2D vel, 
			 acl, 
			 movt, 
			 movtCorrection, 
			 pushImpulse;
	
	public Solid(Vector2D loc, int width, int height) {
		super(loc, width, height);
		
		
	}
	
	public void update(double delta, World world) {
		vel = vel.add(acl.mult(delta));
		
		
	}
	
}
