package objects;
public class TileObjectSpikes extends TileObject{
	
	
	public TileObjectSpikes (SpaceTile parent) {
		this.parent = parent;
		type = TileObjectType.SPIKES;
		parent.setObject(this);
	}
	
	public void affectPlayer(PlatformPlayer player) {
		
		if ( ((parent.getLocy()+1)*Tile.getScale()) - player.opp().getY() < Tile.getScale()/2 && player.getVel().getY() > 130) {
			player.damage(1);
		}
		
	}
	
}
