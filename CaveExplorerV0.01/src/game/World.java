package game;
import geom.Rectangle2D;
import geom.Vector2D;

import java.util.*;

import objects.ImageLayer;
import objects.PlatformPlayer;
import objects.Tile;
import objects.TileObjectTorch;

import org.lwjgl.opengl.GL11;

public class World {
	PlatformPlayer player;
	//ArrayList<WorldBlock> blocks;
	//ArrayList<Furniture> furniture;
	private Vector2D gravity;
	private Map map;
	
	public World() {
		setGravity(new Vector2D(0,600));	
	}
	
	public void loadMap(String mapname) {
		setMap(new Map(mapname));
	}
	
	public void update(double delta) {
		player.update(delta, this);
	}
	
	public void draw(Rectangle2D bounds, Vector2D offset, boolean drawLighting) {
		//---Draw Map---
		double origX = bounds.pos().getX();
		double origY = bounds.pos().getY();

		int blkX = (int) Math.floor(origX/getMap().scale());
		int blkY = (int) Math.floor(origY/getMap().scale());

		int tileIndex = (blkY * getMap().width()) + blkX;
		for (; tileIndex/getMap().width() < Math.min(Math.ceil(bounds.height()/getMap().scale()) + blkY+1, getMap().height()); tileIndex += getMap().width()){
			for (int cnt = 0; cnt < Math.min(Math.ceil(bounds.width()/getMap().scale()) + blkX+1, getMap().width() - blkX); cnt++ ) {
				//if (!map.tiles().get(tileIndex + cnt).tileType().equals(TileType.FORE))
				getMap().tiles().get(tileIndex + cnt).draw(offset, ImageLayer.BACK2);
				getMap().tiles().get(tileIndex + cnt).draw(offset, ImageLayer.MID);
			}
		}
		//------

		//---Draw Player Sprite---
		player.draw(offset, this);
		//------

		/*
		GL11.glColor4f( 1f, 1f, 1f, 1f);	
		//---Draw Foreground Tiles---
		tileIndex = (blkY * map.width()) + blkX;
		for (; tileIndex/map.width() < Math.min(Math.ceil(bounds.height()/map.scale()) + blkY+1, map.height()); tileIndex += map.width()){
			for (int cnt = 0; cnt < Math.min(Math.ceil(bounds.width()/map.scale()) + blkX+1, map.width() - blkX); cnt++ ) {
				if (map.tiles().get(tileIndex + cnt).tileType().equals(TileType.FORE))
					map.tiles().get(tileIndex + cnt).draw(offset);
			}
		}
		 */

		//---Draw Torch Lighting---
		if (drawLighting) {
			tileIndex = (blkY * getMap().width()) + blkX;
			int locx, locy;
			float val;
			GL11.glDisable(GL11.GL_TEXTURE_2D); 
			for (; tileIndex/getMap().width() < Math.min(Math.ceil(bounds.height()/getMap().scale()) + blkY+1, getMap().height()); tileIndex += getMap().width()){
				for (int cnt = 0; cnt < Math.min(Math.ceil(bounds.width()/getMap().scale()) + blkX+1, getMap().width() - blkX); cnt++ ) {
					//if (!map.tiles().get(tileIndex + cnt).tileType().equals(TileType.FORE))
					locx = getMap().tiles().get(tileIndex + cnt).getLocx() * Tile.getScale() + offset.getXi();
					locy = getMap().tiles().get(tileIndex + cnt).getLocy() * Tile.getScale() + offset.getYi();
					val = ( getMap().tiles().get(tileIndex + cnt).getLightLevel() ) * (1f/15);           //range 0-1, 1: clear, 0: opaque 

					//alpha value 1: opaque; 0: clear
					//GL11.glColor4f(0 + .1f*val, 0 + .2f*val, 0 + .3f*val, 1 - val);
					GL11.glColor4f(0 + TileObjectTorch.lightColor.r*val, 
								   0 + TileObjectTorch.lightColor.g*val, 
								   0 + TileObjectTorch.lightColor.b*val, 
								   1 - val);

					GL11.glBegin(GL11.GL_QUADS);
						GL11.glVertex2f(locx, locy);
						GL11.glVertex2f(locx + Tile.getScale(), locy);
						GL11.glVertex2f(locx + Tile.getScale(), locy + Tile.getScale());
						GL11.glVertex2f(locx, locy + Tile.getScale());
					GL11.glEnd();
				}
			}
			GL11.glEnable(GL11.GL_TEXTURE_2D); 
		}
		//------
	}

	public Map getMap() {
		return map;
	}

	public void setMap(Map map) {
		this.map = map;
	}

	public Vector2D getGravity() {
		return gravity;
	}

	public void setGravity(Vector2D gravity) {
		this.gravity = gravity;
	}
}
