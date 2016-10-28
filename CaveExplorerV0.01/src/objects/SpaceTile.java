package objects;

import geom.Vector2D;

import java.io.IOException;
import java.util.*;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import drawing.Sprite;



public class SpaceTile extends Tile {
	static HashMap<BackgroundType, Sprite> tileset;
	BackgroundType type;
	private TileObject object;
	
	public SpaceTile(int locx, int locy, ImageLayer layer, BackgroundType type) {
		super(locx, locy, layer);
		
		//lightLevel = 0;
		pic = tileset.get(type);

	}
	
	public static void loadTileset(String tilesetName) {
		String path = "res/Tilesets/" + tilesetName + "/";
		tileset = new HashMap<BackgroundType, Sprite>();
		
		try {
			Sprite back1 = new Sprite( TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(path + "Back1.png")) ); 
			tileset.put(BackgroundType.BACK1, back1);
			/*
			Sprite stone = new Sprite( TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(path + "Stone.png")) ); 
			tileset.put(BlockType.STONE, stone);
			*/

		} catch (IOException e) {
			System.err.format("Problem loading BlockTile resources!\n");
			e.printStackTrace();
		}
		
	}
	
	public void affectPlayer(PlatformPlayer player) {
		if(getObject() != null)
			getObject().affectPlayer(player);
	}
	
	public List<String> description() {
		List<String> description = new ArrayList<String>();
		
		description.add("Class: " + this.getClass().getName());
		description.add("Light Level: " + getLightLevel());
		description.add("Contained Object Type: " + getObject().getClass().getName());
		
		return description;
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
			
			if (getObject() != null)
				getObject().draw(offset);
		}
	}

	public TileObject getObject() {
		return object;
	}

	public void setObject(TileObject object) {
		this.object = object;
	}
}