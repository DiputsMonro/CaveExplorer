package objects;

import java.io.IOException;
import java.util.*;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import drawing.Sprite;


public class BlockTile extends Tile {
	static HashMap<BlockType, Sprite> tileset;
	
	BlockType type;
	
	public BlockTile(int locx, int locy, ImageLayer layer, BlockType type) {
		super(locx, locy, layer);
		
		this.type = type;
		pic = tileset.get(type);
	}
	
	public static void loadTileset(String tilesetName) {
		String path = "res/Tilesets/" + tilesetName + "/";
		tileset = new HashMap<BlockType, Sprite>();
		
		try {
			Sprite dirt = new Sprite( TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(path + "Dirt.png")) ); 
			tileset.put(BlockType.DIRT, dirt);
			
			Sprite stone = new Sprite( TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(path + "Stone.png")) ); 
			tileset.put(BlockType.STONE, stone);

		} catch (IOException e) {
			System.err.format("Problem loading BlockTile resources!\n");
			e.printStackTrace();
		}
		
	}
	
	public BlockType getType() { return type; }
	
}
