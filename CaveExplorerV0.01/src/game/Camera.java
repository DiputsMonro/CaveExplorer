package game;

import geom.Rectangle2D;
import geom.Vector2D;
import inputHelpers.KeyboardHelper;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;


@SuppressWarnings("deprecation")

public class Camera {
	Rectangle2D bounds;
	Rectangle2D playerBox;
	TrueTypeFont font;
	
	private static boolean drawLighting = false;
	
	public Camera(double locx, double locy, int width, int height, TrueTypeFont font) {
		this.bounds = new Rectangle2D(locx, locy, width, height, 0);
		this.font = font;
		
		playerBox = new Rectangle2D(locx + width * .4, locy + height * .4, width * .20, height * .20, 0);
	}
	
	public void update(double delta, World world) {
		/* Manual Keyboard Controls
		if (Keyboard.isKeyDown(Keyboard.KEY_L))
			move(new Vector2D(-1,0));
		if (Keyboard.isKeyDown(Keyboard.KEY_APOSTROPHE))
			move(new Vector2D(1,0));
		if (Keyboard.isKeyDown(Keyboard.KEY_P))
			move(new Vector2D(0,-1));
		if (Keyboard.isKeyDown(Keyboard.KEY_SEMICOLON))
			move(new Vector2D(0,1));
		*/
		
		/* Move screen based on player position, such that it tracks the player */
		Vector2D diff = playerBox.mid().sub(world.player.mid());
		double movtX = world.player.getMovtCorrection().getX();
		double movtY = world.player.getMovtCorrection().getY();
		if (diff.getX() < -playerBox.getWidth()/2 && movtX > 0) 
			move( new Vector2D( movtX, 0) );
		if (diff.getX() > playerBox.getWidth()/2 && movtX < 0) 
			move( new Vector2D( movtX, 0) );
		if (diff.getY() < -playerBox.getHeight()/2 && movtY > 0) 
			move( new Vector2D(0, movtY) );
		if (diff.getY() > playerBox.getHeight()/2 && movtY < 0) 
			move( new Vector2D(0, movtY) );

		/* Ensure that the screen does not pan past the map boundaries */
		if (bounds.pos().getX() < 0) {
			move(new Vector2D( bounds.getX()*-1, 0));
		}
		if (bounds.pos().getY() < 0) {
			move(new Vector2D( 0, bounds.getY()*-1));
		}
	}
	
	public void move(Vector2D mvt) {
		bounds.move(mvt);
		playerBox.move(mvt);
	}
	
	public Vector2D offset() { return bounds.pos().mult(-1); }
	
	public void draw(World world) {
		Vector2D offset = bounds.pos().mult(-1);
		
		world.draw(bounds, offset, drawLighting);
	}
	
	public static void setLighting(boolean state) { drawLighting = state; }
}
