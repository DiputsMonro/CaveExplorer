package objects;

import geom.Vector2D;

import java.util.HashMap;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;

import drawing.Sprite;


public abstract class TileObject {
	SpaceTile parent;
	protected TileObjectType type;
	//Sprite pic;
	
	
	//static HashMap<TileObjectType, Sprite> pics;
	
	public void affectPlayer(PlatformPlayer player) {} 
	
	public void draw(Vector2D offset){
		Color col = new Color(255,255,255,255);
		col.bind();
		
		GL11.glDisable(GL11.GL_TEXTURE_2D); 
		int locx = Tile.getScale()*parent.getLocx() + Tile.getScale()/4     + offset.getXi(),
			oppx = Tile.getScale()*parent.getLocx() + 3*(Tile.getScale()/4) + offset.getXi(),
			locy = Tile.getScale()*parent.getLocy() + Tile.getScale()/4     + offset.getYi(),
			oppy = Tile.getScale()*parent.getLocy() + 3*(Tile.getScale()/4) + offset.getYi();
				    
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex2f(locx, locy);
			GL11.glVertex2f(oppx, locy);
			GL11.glVertex2f(oppx, oppy);
			GL11.glVertex2f(locx, oppy);
		GL11.glEnd();
		
		GL11.glEnable(GL11.GL_TEXTURE_2D); 
	}

	public TileObjectType getType() {
		return type;
	}
	
}
