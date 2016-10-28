package objects;
import game.World;
import geom.Rectangle2D;
import geom.Vector2D;

import java.util.ArrayList;



public class CollisionHandler {

	public static CollisionData checkWorldCollision(Rectangle2D boundingBox, Vector2D movt, double delta, World world) {
		movt = movt.mult(delta);
		
		Vector2D curPos = boundingBox.pos();
		Rectangle2D nextBox = boundingBox.copy();
		nextBox.move(movt);
		
		boolean top, bottom, left, right;
		top = bottom = left = right = false;
		
		double movtx = movt.getX();  
		double movty = movt.getY();
		int posx = curPos.getXi();
		int posy = curPos.getYi();	
		
		Vector2D correction = new Vector2D(movtx, movty);
		
		/* Find the rows and columns of tiles that contain the player */	
		// Rows...
		int firsty = (int) (posy/world.getMap().scale());
		int lasty  = (int) ((posy+boundingBox.height()-1)/world.getMap().scale());
		
		int height = (lasty-firsty) + 1;
		
		int[] rows = new int[height];
		
		rows[0] = firsty;
		for( int x = 1; x < height; x++) {
			rows[x] = rows[x-1] + 1;
		}
		
		//IntersectionRows = rows;
		
		//Columns...
		int firstx = (int) (posx/world.getMap().scale());
		int lastx  = (int) ((posx+boundingBox.width()-1)/world.getMap().scale());
		
		int width  = (lastx-firstx) + 1;	
		
		int[] columns = new int[width];
		
		columns[0] = firstx;
		for( int x = 1; x < width; x++) {
			columns[x] = columns[x-1] + 1;
		}
		
		//IntersectionColumns = columns;
		
		/* ----Determine player collision along the X-axis---- 
		 * For each row, we select the blocks on the player's leading edge, and scan across the tiles in those rows
		 * until we find a solid block, which we add to the list of collisionCandidates, the list of blocks that the
		 * player could collide with.  We then look at all those blocks and compare the distances between the player's
		 * leading edge and the closest edge of those blocks.  We then select the minimum of those distances and the
		 * amount by which the player wished to move, and move the player by that amount. 
		 */
		
		if (movtx < 0) {
			ArrayList<Tile> collisionCandidates = new ArrayList<Tile>();
			
			for (int r = 0; r < height; r++) {
				int edge = (rows[r] * world.getMap().width()) + columns[0];
				for(int x = edge; x >= rows[r] * world.getMap().width(); x--) {
					Tile tile = world.getMap().tiles().get(x);
					if ( tile instanceof BlockTile ) {
						collisionCandidates.add(tile);
						break;
					}
				}
			}
			
			double corx = movtx;
			
			for (Tile tile : collisionCandidates) {
				int dist = (tile.getLocx()*tile.getScale()+tile.getScale()) - posx;
				corx = Math.max(corx, dist);
			}
			
			if(corx != movtx)
				left = true;
			correction = new Vector2D( corx, correction.getY());
		}
		if (movtx > 0) {
			ArrayList<Tile> collisionCandidates = new ArrayList<Tile>();
			
			for (int r = 0; r < height; r++) {
				int edge = (rows[r] * world.getMap().width()) + columns[width-1];
				for(int x = edge; x <= (rows[r]+1) * world.getMap().width() -1; x++) {
					Tile tile = world.getMap().tiles().get(x);
					if (tile instanceof BlockTile) {
						collisionCandidates.add(tile);
						break;
					}
				}
			}
			
			double corx = movtx;
			
			for (Tile tile : collisionCandidates) {
				int dist = (int) (tile.getLocx()*tile.getScale() - (posx + boundingBox.width()));
				corx = Math.min(corx, dist);
			}
			
			if(corx != movtx)
				right = true;
			correction = new Vector2D(corx, correction.getY());
		}
		
		/* ----Determine player collision along the Y-axis---- 
		 * Here we use a similar algorithm as above for the Y-axis.
		 */
		if (movty < 0) {
			ArrayList<Tile> collisionCandidates = new ArrayList<Tile>();
			
			for (int c = 0; c < width; c++) {
				int edge = (rows[0] * world.getMap().width()) + columns[c];
				for(int y = edge; y >= columns[c]; y -= world.getMap().width()) {
					Tile tile = world.getMap().tiles().get(y);
					if (tile instanceof BlockTile) {
						collisionCandidates.add(tile);
						break;
					}
				}
			}
			
			double cory = movty;
			
			for (Tile tile : collisionCandidates) {
				int dist = (tile.getLocy()*tile.getScale() +tile.getScale()) - posy;
				cory = Math.max(cory, dist);
			}
			
			if(cory != movty)
				top = true;
			correction = new Vector2D( correction.getX(), cory);
		}
		if (movty > 0) {
			ArrayList<Tile> collisionCandidates = new ArrayList<Tile>();
			
			for (int c = 0; c < width; c++) {
				int edge = (rows[height-1] * world.getMap().width()) + columns[c];
				for(int y = edge; y <= (world.getMap().height()-1)*world.getMap().width() + columns[c]; y += world.getMap().width()) {
					Tile tile = world.getMap().tiles().get(y);
					if (tile instanceof BlockTile) {
						collisionCandidates.add(tile);
						break;
					}
				}
			}
			
			double cory = movty;
			
			for (Tile tile : collisionCandidates) {
				int dist = (int) ((tile.getLocy()*tile.getScale()) - (posy+boundingBox.height()));
				cory = Math.min(cory, dist);
			}
			
			if(cory != movty)
				bottom = true;
			correction = new Vector2D( correction.getX(), cory);
		}

		return new CollisionData(correction, top, bottom, left, right);
	}
	
}

