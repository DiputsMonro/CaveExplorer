package game;
import geom.Vector2D;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

import objects.BackgroundType;
import objects.BlockTile;
import objects.BlockType;
import objects.ImageLayer;
import objects.SpaceTile;
import objects.Tile;
import objects.TileObjectTorch;

public class Map {
	private int width;  // Width and Height in number of tiles        
	private int height; 
	private int scale;  // Number of pixels wide/high of each square tile
	private ArrayList<Tile> tiles;  // Array of tiles that constitute the map
	private String name;
	private String tileset;
	Vector2D playerLoc;
	
	Queue<Tile> toUpdate = new ArrayDeque<Tile>();
	Set<Tile> haveBeenUpdated = new HashSet<Tile>();
	
	enum MapfileSection {NONE, INFO, MAP};
	
	public Map() {
		name = "<DefaultEmptyMap>";
		scale = 16;
		tileset = "<EmptyTileset>";
		width = 10;
		height = 10;
		
		tiles = new ArrayList<Tile>();
		int x, y;
		for (x = 0; x < width; x++) {
			for (y = 0; y < height; y++) {
				tiles.add( new SpaceTile (x, y, ImageLayer.BACK2, BackgroundType.BACK1));
			}
		}
		
		Tile.setMap(this);
	}
	
	public Map(String filename) {
		BufferedReader in = null;
			
		try{
			in = new BufferedReader( new FileReader(filename) );
			String line;
			MapfileSection section = null;
			
			//System.out.format("filename: %s\n", filename);
			
			/* Get the file extension of the input file */
			String[] splitname = filename.split("\\.");
			String extension = splitname[splitname.length-1];
			
			//System.out.format("type: %s\n", filetype);
			
			/* Read the mapfile according to the file extension */
			if(extension.equals("mp1")){
				int x = 0, y = 0;
				
				for (line = in.readLine(); line != null; line = in.readLine()){
					
					if (line.equals("--[Info]--")) {
						section = MapfileSection.INFO;
						continue;
					}
					
					if (line.equals("--[Map]--")) {
						section = MapfileSection.MAP;
						tiles = new ArrayList<Tile>();
						continue;
					}
					
					if (line.equals("--[/Map]--")) {
						section = MapfileSection.NONE;
						height = y;
					}
					
					if (section.equals(MapfileSection.INFO)) {
						line = line.replace(" ", "");
						String[] values = line.split(":");
						String field = values[0];
						
						if (field.equals("Name")) {
							name = values[1];
						}
						if (field.equals("Tileset")) {
							tileset = values[1];
							BlockTile.loadTileset(tileset);
							SpaceTile.loadTileset(tileset);
						}
						if (field.equals("Scale")) {
							scale = Integer.parseInt(values[1]);
							Tile.setScale(scale);
						}
						
					}
					
					if (section.equals(MapfileSection.MAP)) {
						char[] tileLine = line.toCharArray();
						width = tileLine.length;
						
						for (x = 0; x < width; x++) {
							if (tileLine[x] == '*') {
								tiles.add( new BlockTile (x, y, ImageLayer.MID, BlockType.DIRT));
							}
							if (tileLine[x] == '#') {
								tiles.add( new BlockTile (x, y, ImageLayer.MID, BlockType.STONE));
							}
							/*
							if (tileLine[x] == 'G') {
								tiles.add( new BlockTile (x*scale, y*scale, scale, TileType.SOLID, BlockType.MID2));
							}
							if (tileLine[x] == 'T') {
								tiles.add( new BlockTile (x*scale, y*scale, scale, TileType.SOLID, BlockType.MID3));
							}
							if (tileLine[x] == 'B') {
								tiles.add( new BlockTile (x*scale, y*scale, scale, TileType.SOLID, BlockType.MID4));
							}
							*/
							if (tileLine[x] == '.') {
								tiles.add( new SpaceTile (x, y, ImageLayer.BACK2, BackgroundType.BACK1));
							}
							if (tileLine[x] == ' ') {
								tiles.add( new SpaceTile (x, y, ImageLayer.BACK2, BackgroundType.BACK1));
							}
							/*
							if (tileLine[x] == 'f') {
								tiles.add( new Tile (x*scale, y*scale, scale, TileType.FORE, BlockType.MID1));
							}
							*/
							if (tileLine[x] == 'P') {
								tiles.add( new SpaceTile (x, y, ImageLayer.BACK2, BackgroundType.BACK1));
								playerLoc = new Vector2D(x*scale, y*scale);
							}
							
						}
						
						y++;
					}
					
					
					/*
					String[] input = line.split(", ");
					
					String name = input[0];
					int posx = Integer.parseInt(input[1]);
					int posy = Integer.parseInt(input[2]);
					int len = Integer.parseInt(input[3]);
					int wid = Integer.parseInt(input[4]);
					float pos = Float.parseFloat(input[5]); 
					*/
					
				}
			}
		}
		catch(Exception e) {
			System.out.format("Exception!!!\n");
			e.printStackTrace();
			return;
		}
		
		Tile.setMap(this);
		
		System.out.format("Map----\n");
		System.out.format("width: %d, height: %d\n", width, height);
		System.out.format("scale: %d\n", scale);
		
	}
	/*
	public void forceTileLight(Tile tile, int light) {
		tile.lightLevel = light;
		
		for (Tile neighbor : neighborsOf(tile)) {
			if( !toUpdate.contains(neighbor) && !haveBeenUpdated.contains(neighbor)) {
				toUpdate.add(neighbor);
			}
		}
		
		haveBeenUpdated.add(tile);
		
		updateLight();
	}
	
	public void forceTileLightNoUpdate(Tile tile, int light) {
		tile.lightLevel = light;	
	}
	
	public void updateLight() {
		while(!toUpdate.isEmpty()) {
			Tile tile = toUpdate.remove();
			int maxLight = tile.lightLevel;
			
			for (Tile neighbor : neighborsOf(tile)) {
				maxLight = Math.max(maxLight, neighbor.lightLevel-1);
			}
			
			int lightSuggestion = Math.max(Math.min(maxLight, 15), 0);
			
			if (lightSuggestion != tile.lightLevel && lightSuggestion != 0 && tile instanceof SpaceTile) {
				for (Tile neighbor : neighborsOf(tile)) {
					if( !toUpdate.contains(neighbor) && !haveBeenUpdated.contains(neighbor)) {
						toUpdate.add(neighbor);
					}
				}
			}
			
			haveBeenUpdated.add(tile);
			tile.lightLevel = tile instanceof SpaceTile ? lightSuggestion : lightSuggestion/30;
		}
		
		haveBeenUpdated.clear();
		
	}
	
	public ArrayList<Tile> neighborsOf(Tile tile) {
		ArrayList<Tile> neighbors = new ArrayList<Tile>();
			
		neighbors.add(tileAt((tile.locx - 1)*scale, tile.locy*scale));
		neighbors.add(tileAt(tile.locx*scale, (tile.locy - 1)*scale));
		neighbors.add(tileAt((tile.locx + 1)*scale, tile.locy*scale));
		neighbors.add(tileAt(tile.locx*scale, (tile.locy + 1)*scale));
		
		neighbors.removeAll(Collections.singleton(null));
		
		return neighbors;
	}
	*/
	/* returns the tile in the map at the given x-y coordinate location */
	public Tile tileAt(int locx, int locy) {
		int x = locx/scale;
		int y = locy/scale;
		
		int tileIndex = (y * width) + x;
		
		if (x < width && y < height && x >= 0 && y >= 0) 
			return tiles.get(tileIndex);
		else
			return null;
	}
	
	public List<Tile> tileRing(Tile center, int radius) {
		List<Tile> ring = new ArrayList<Tile>();
		
		if (radius == 0) {
			ring.add(center);
			return ring;
		}
		else {
			int locx = center.getLocx(),
				locy = center.getLocy();
			
			locx -= radius;
			
			for (int t = 0; t < 4*radius; t++) {
				ring.add( tileAt(locx*scale, locy*scale) );
				
				if (t < radius) {   //Up-Right
					locx++;
					locy--;
				}
				if (t >= radius && t < radius*2) { //Down-Right
					locx++;
					locy++;
				}
				if (t >= radius*2 && t < radius*3) { //Down-Left
					locx--;
					locy++;
				}
				if (t >= radius*3 && t < radius*4) { //Up-Left
					locx--;
					locy--;
				}
			}
			
			ring.removeAll(Collections.singleton(null));

			return ring;
		}
	}
	
	public void RemoveTile(Tile toRemove) {
		int i = tiles.indexOf(toRemove);
		Tile newTile = new SpaceTile (toRemove.getLocx(), toRemove.getLocy(), ImageLayer.BACK2, BackgroundType.BACK1);
		
		for (TileObjectTorch t : toRemove.getTorchLights().keySet() ) {
			newTile.addTorchLight(t, toRemove.getTorchLight(t));
		}
		
		tiles.add(i,  newTile);
		tiles.remove(toRemove);
	}
	
	public int width()  { return width;  }
	public int height() { return height; }
	public int scale()  { return scale;  }
	public String name() { return name; }
	public ArrayList<Tile> tiles() { return tiles; }
}
