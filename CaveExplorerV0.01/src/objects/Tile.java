package objects;

import game.Map;
import geom.Vector2D;

import java.io.IOException;
import java.util.*;

import org.newdawn.slick.Color;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import drawing.Sprite;

/*
enum TileType {SOLID, FORE, BACK, EMPTY};
enum BlockType {MID1, MID2, MID3, MID4, MID5, LWALL, RWALL, TOP, BOT};
*/

/* new 
 * enum TileType {BLOCK, FORE, BACK, TRANS, VOID, TRIGGER};
 * enum BlockType {SOLID, SPIKE, WATER, LADDER};
 */

public class Tile {
	private int locx;     //x-y location of tile, in terms of map coordinates;
	private int locy;
	private static int scale;   //width and height of the tile in pixels.
	private int lightLevel;     //15 = Full Bright, 0 = Full Dark
	Color hue;          //The color of the light in the tile 
	HashMap<TileObjectTorch, Integer> torchLights;
	ImageLayer layer;
	Sprite pic;
	
	static Map map;
	
	public Tile(int locx, int locy, ImageLayer layer) {
		this.setLocx(locx);
		this.setLocy(locy);
		this.layer = layer;
		this.setLightLevel(0);
		this.hue = new Color(0,0,0);
		torchLights = new HashMap<TileObjectTorch, Integer>();
	}
	
	
	
//	public void forceLightNoUpdate(int newLightLevel) {
//		lightLevel = Math.max(Math.min(newLightLevel, 15), 0);
//	}
//	
//	public void putTorch(TileObjectTorch t) {
//		lightLevel = 15;
//		
//		for (Tile tile : neighbors()) {
//			tile.updateLight(t);
//		}
//	}
//	
//	public void updateLight(TileObjectTorch t) { 
//		//System.out.format("Updating Light in tile at <%d, %d>\n", locx, locy);
//		
//		int maxLight = getTorchLight(t);
//		
//		for (Tile tile : neighbors()) {
//			maxLight = (int) Math.max(maxLight,  tile.getTorchLight(t));
//		}
//		
//
//		if (getTorchLight(t) != maxLight - 1) {
//			addTorchLight(t, Math.min(0, maxLight -1));
//			updateLight();
//			
//			for (Tile tile : neighbors()) {
//				tile.updateLight(t);
//			}
//		}
//	}
	
	
	public void addTorchLight(TileObjectTorch torch, int light) {
		torchLights.put(torch, light);
		updateLight();
	}
	
	public void removeTorchLight(TileObjectTorch torch) {
		if (torchLights.containsKey(torch))
			torchLights.remove(torch);
		updateLight();
	}
	
	// sets lightLevel to the maximum value in the TorchLight map
	public void updateLight() {
		Collection<Integer> lights = torchLights.values();
		
		setLightLevel(0);
		//hue = new Color(0, 0, 0);
		
		/* Find the maximum light level among affecting torches */
		for (Integer light : lights) {
			setLightLevel(Math.max(getLightLevel(), light));
			//hue.r = hue.r + 
		}
	}
	
	public List<Tile> neighbors() {
		return map.tileRing(this, 1);
	}
	
	public static void setScale(int newScale) { scale = newScale; }
	public static void setMap(Map newMap) { map = newMap; }
	
	public void draw(Vector2D offset, ImageLayer drawLayer) {
		Color col = new Color(255,255,255,255);
		//col = col.darker(  100 - (lightLevel * (100f/15)) );
		//int val = (int) (lightLevel * (255f / 15));
		//col = new Color(val,val,val);
		col.bind();
		
		drawHelper(offset, drawLayer);
	}
	
	public void draw(Vector2D offset, ImageLayer drawLayer, Color col) {
		//int val = (int) (lightLevel * (255f / 15));
		//col = new Color(val,val,val);
		col.bind();
		
		drawHelper(offset, drawLayer);
	}
	
	void drawHelper(Vector2D offset, ImageLayer drawLayer) {
		if (layer.equals(drawLayer)) {
			pic.getTexture().bind();
			int offX = offset.getXi();
			int offY = offset.getYi();
			
			float sclX = pic.sclX();
			float sclY = pic.sclY();
			GL11.glBegin(GL11.GL_QUADS);
				GL11.glTexCoord2f(0,0);
				GL11.glVertex2f(getLocx()*getScale() + offX, getLocy()*getScale() + offY);
				GL11.glTexCoord2f(sclX,0);
				GL11.glVertex2f(getLocx()*getScale() + getScale() + offX, getLocy()*getScale() + offY);
				GL11.glTexCoord2f(sclX, sclY);
				GL11.glVertex2f(getLocx()*getScale() + getScale() + offX, getLocy()*getScale() + getScale() + offY);
				GL11.glTexCoord2f(0,sclY);
				GL11.glVertex2f(getLocx()*getScale() + offX, getLocy()*getScale() + getScale() + offY);
			GL11.glEnd();
		}
	}

	public List<String> description() {
		List<String> description = new ArrayList<String>();
		
		description.add("Class: " + this.getClass().getName());
		description.add("Light Level: " + getLightLevel());
		
		return description;
	}

	public int getLocx() {
		return locx;
	}

	public void setLocx(int locx) {
		this.locx = locx;
	}

	public static int getScale() {
		return scale;
	}

	public int getLocy() {
		return locy;
	}

	public void setLocy(int locy) {
		this.locy = locy;
	}

	public int getLightLevel() {
		return lightLevel;
	}

	public void setLightLevel(int lightLevel) {
		this.lightLevel = lightLevel;
	}
	
	public int getTorchLight(TileObjectTorch t) { 
		if (torchLights.containsKey(t))
			return torchLights.get(t);
		else 
			return 0;
	}
	
	public HashMap<TileObjectTorch, Integer> getTorchLights() {
		return torchLights;
	}
	
	public void destroy() {
		map.RemoveTile(this);
	}
}
