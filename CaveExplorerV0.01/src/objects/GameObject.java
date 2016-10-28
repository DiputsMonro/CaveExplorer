package objects;
import game.World;
import geom.Rectangle2D;
import geom.Vector2D;

import java.io.IOException;
import java.util.*;

import drawing.Animation;

public class GameObject {
	Rectangle2D boundingBox;
	Vector2D spawnPos;
	
	HashMap<String, Animation> animations;
	Animation curAnim;
	
	public GameObject(Vector2D loc, int width, int height){
		boundingBox = new Rectangle2D(loc, width, height, 0);
		spawnPos = loc.copy();
	}
	
	void initValues() {
		
	}
	
	public void loadResources() {
		/*
		try {
			
		} catch (IOException e) {
			System.err.format("Problem loading Player resources!\n");
			e.printStackTrace();
		}
		*/
		
	}
	
	
	
	public void update(double delta, World world) {

	}
	
	public Vector2D move(double delta, Vector2D movt) {
		return boundingBox.move( movt.mult(delta) );
	}
	
	public Vector2D move(Vector2D movt) {
		return boundingBox.move( movt );
	}

	public void setAnim(String animName) {
		curAnim = animations.get(animName);
		curAnim.restart();
	}
	
	public void draw(Vector2D offset, World world) {
		
	}
	
	public Vector2D pos() { return boundingBox.pos(); }
	public Vector2D mid() { return boundingBox.mid(); }
	public Vector2D opp() { return boundingBox.opp(); }
	
	public double getX() { return pos().getX(); }
	public double getY() { return pos().getY(); }
	public int getXi() { return (int) getX(); }
	public int getYi() { return (int) getY(); }
	
	public double getOppX() { return opp().getX(); }
	public double getOppY() { return opp().getY(); }
	public int getOppXi() { return (int) getOppX(); }
	public int getOppYi() { return (int) getOppY(); }

}



