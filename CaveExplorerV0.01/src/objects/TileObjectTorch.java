package objects;
import java.util.*;

import org.newdawn.slick.Color;

public class TileObjectTorch extends TileObject {
	int maxLight;
	
	public static Color lightColor = new Color(0, 0, 0);
	
	public TileObjectTorch (SpaceTile parent) {
		this.parent = parent;
		type = TileObjectType.TORCH;
		parent.setObject(this);
		maxLight = 10;
		
		for (int radius = 0; radius < maxLight; radius++) {
			List<Tile> tileRing = Tile.map.tileRing(parent, radius);
			
			System.out.format("At Radius %d we have %d Tiles in tileRing  (should have %d if not clipped)\n", radius, tileRing.size(), Math.max(1,radius*4));
			
			
			for (Tile tile : tileRing){
				//System.out.format("  tile at (%d,%d)\n", tile.locx, tile.locy);
				tile.addTorchLight(this, maxLight-radius);
			}
			
		}
		
		//parent.putTorch(this);
		
		
		//Tile.map.forceTileLight(parent, 15);
	}
	
	public void affectPlayer(PlatformPlayer player) {}
	
	public void destroy() {
		for (int radius = 0; radius < maxLight; radius++) {
			List<Tile> tileRing = Tile.map.tileRing(parent, radius);

			for (Tile tile : tileRing){
				tile.removeTorchLight(this);
			}		
		}
		
		parent.setObject(null);
	}
	
}
