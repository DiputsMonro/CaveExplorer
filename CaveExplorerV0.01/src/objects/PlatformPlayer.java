package objects;
import game.Game;
import game.World;
import geom.Rectangle2D;
import geom.Vector2D;

import inputHelpers.KeyboardHelper;

import java.awt.Font;
import java.io.IOException;
import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import controllers.ControllerInterface;
import controllers.Controls;
import controllers.KeyboardController;
import drawing.Animation;
import drawing.Sprite;

enum Direction {LEFT, RIGHT};

public class PlatformPlayer {
	Rectangle2D boundingBox;
	private Vector2D vel;
	Vector2D movt;
	private Vector2D movtCorrection;
	Vector2D spawnPos;
	double maxVel;
	double baseSpeed;
	double baseClimbSpeed;
	double speedFactor;
	boolean ableToJump;
	Vector2D jumpImpulse;
	
	private int hp;
	int maxhp;
	boolean stunned;
	double stunCooldown;
	
	Vector2D pushImpulse;
	
	Sprite jumpAsc, jumpPeak, jumpDesc;
	Animation runAnim, idleAnim, deathAnim, wclimbAnim;
	private PlayerState state;
	Direction direction;
	
	ControllerInterface controller;
	
	int[] IntersectionRows, IntersectionColumns;
	
	public PlatformPlayer(ControllerInterface controller, Vector2D loc, int width, int height){
		spawnPos = loc;
		boundingBox = new Rectangle2D(loc, width, height, 0);
		this.controller = controller;
		setBaseStats();
		initValues();
	}
	
	void setBaseStats() {
		baseSpeed = 60;
		baseClimbSpeed = 20;
		maxVel = 500;
		maxhp = 3;
	}
	
	void initValues() {
		speedFactor = 1;
		direction = Direction.RIGHT;
		
		setVel(new Vector2D(0,0));
		jumpImpulse = new Vector2D(0,-210);
		pushImpulse = new Vector2D(0,0);
		
		setHp(maxhp);
		stunned = false;
		stunCooldown = 0;
		
		setState(PlayerState.AIRBORNE);
	}
	
	public void resurrect() {
		boundingBox.setPos(spawnPos);
		initValues();
	}
	
	public void loadResources() {
		try {
			// load texture from PNG file
			Texture standT = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/player.png"));
			//pic = new Sprite(standT, 16, 32);

			Texture runT = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/LemWalk.png"));
			Sprite runSheet = new Sprite(runT);
			runAnim = new Animation(runSheet, 6, 8, .6, true);
			
			Texture idleT = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/LemStand.png"));
			Sprite idleSheet = new Sprite(idleT);
			idleAnim = new Animation(idleSheet, 6, new double[] {1}, true);
			
			/*
			Texture deathT = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/EricDeath.png"));
			Sprite deathSheet = new Sprite(deathT);
			deathAnim = new Animation(deathSheet, 32, 8, 0.8, false);
			*/
			Texture deathT = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/LemFallDeath.png"));
			Sprite deathSheet = new Sprite(deathT);
			deathAnim = new Animation(deathSheet, 16, 16, 0.8, false);
			
			Texture wclimbT = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/LemWallClimb.png"));
			Sprite wclimbSheet = new Sprite(wclimbT);
			wclimbAnim = new Animation(wclimbSheet, 10, 9, 1.2, true);
			
			jumpAsc = new Sprite( TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/LemAsc.png")) ); 
			jumpPeak = new Sprite( TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/LemPeak.png")) ); 
			jumpDesc = new Sprite( TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/LemDesc.png")) ); 

		} catch (IOException e) {
			System.err.format("Problem loading Player resources!\n");
			e.printStackTrace();
		}
		
	}
	
	void changeState(PlayerState newState) {
		if (getState() != newState && getState() != PlayerState.DEAD) {
			setState(newState);
			
			switch(newState) {
				case RUNNING:
					runAnim.restart();
					break;
				case STANDING:
					idleAnim.restart();
					break;
				case DEAD:
					setHp(0);
					deathAnim.restart();
					break;
				case WALL_CLIMBING:
					wclimbAnim.restart();
					break;
			}
		}
	}
	
	public void update(double delta, World world) {
		ableToJump = true;
		movt = new Vector2D(0,0); 
		
		if (delta > .5)
			System.out.format("delta: %f\n", delta);
		
		if (getState() == PlayerState.STANDING || getState() == PlayerState.RUNNING) {	
			setVel(new Vector2D(0,0));
			
			/*-- Sprinting Control --*/
			if (controller.held(Controls.SPRINT)) {
				speedFactor = 1.5;
				runAnim.setSpeed(1.5);
			}
			else{
				speedFactor = 1;
				runAnim.setSpeed(1);
			}
			
			/*-- Basic Movement --*/
			if (controller.held(Controls.LEFT)){
				movt = movt.add( new Vector2D(-1,0).mult(baseSpeed * speedFactor) );
				direction = Direction.LEFT;
			}
			if (controller.held(Controls.RIGHT)){
				movt = movt.add( new Vector2D(1,0).mult(baseSpeed * speedFactor) );
				direction = Direction.RIGHT;
			}
			
			/*-- If player hasn't moved, then he is standing still --*/
			if (movt.getX() != 0)
				changeState(PlayerState.RUNNING);
			else
				changeState(PlayerState.STANDING);
			
			/*-- Check if ground is empty beneath player, if so, change state to AIRBORNE so that he falls --*/
			int firstx = (int) (boundingBox.getPos().getX()/world.getMap().scale());
			int lastx  = (int) ((boundingBox.getPos().getX()+boundingBox.width()-1)/world.getMap().scale());
			
			int width  = (lastx-firstx) + 1;	
			
			int[] columns = new int[width];
			
			columns[0] = firstx;
			for( int x = 1; x < width; x++) {
				columns[x] = columns[x-1] + 1;
			}
			
			int numEmptyUnderneath = 0;
			for (int c = 0; c < width; c++) {
				/* If ALL of the tiles under the player are not solid, then the player falls, becoming airborne */
				Tile tileUnder = world.getMap().tileAt(columns[c]*world.getMap().scale(), boundingBox.getOppYi());
				if (! (tileUnder instanceof BlockTile) ) 
					numEmptyUnderneath++;
				
				/* If ANY of the tiles directly above the player's head are solid, he's unable to jump */
				Tile tileAbove = world.getMap().tileAt(columns[c]*world.getMap().scale(), boundingBox.getYi()-1);
				if (tileAbove instanceof BlockTile) 
					ableToJump = false;
			}
			
			if (numEmptyUnderneath == width)
				changeState(PlayerState.AIRBORNE);
			
			/*-- Try to jump if the JUMP button is pressed --*/
			if (controller.pressed(Controls.JUMP) && ableToJump) {
				setVel(getVel().add( jumpImpulse ));
				changeState(PlayerState.AIRBORNE);
			}
			
			/* If the player presses the DIG button and holds DOWN while standing on a dirt tile, delete the tile under them */
			if (controller.pressed(Controls.DIG) && controller.held(Controls.DOWN)) {
				Tile tileUnder = world.getMap().tileAt(columns[width/2]*world.getMap().scale(), boundingBox.getOppYi());
				if (tileUnder instanceof BlockTile && ((BlockTile)tileUnder).getType() == BlockType.DIRT)
					tileUnder.destroy();
			}
			
		}
		
		if (getState() == PlayerState.AIRBORNE) {	
			
			/*-- Basic Movement in the air --*/
			if (controller.held(Controls.LEFT)){
				movt = movt.add( new Vector2D(-1,0).mult(baseSpeed * speedFactor) );
				direction = Direction.LEFT;
			}
			if (controller.held(Controls.RIGHT)){
				movt = movt.add( new Vector2D(1,0).mult(baseSpeed * speedFactor) );
				direction = Direction.RIGHT;
			}

			
			/*-- Let gravity act on the player --*/
			setVel(getVel().add((world.getGravity()).mult(delta)));
			if (getVel().getY() > maxVel)
				setVel(new Vector2D( getVel().getX(), maxVel));
			
			movt = movt.add( getVel() );
		}
		
		if (getState() == PlayerState.WALL_CLIMBING) {	
			int dir = (direction == Direction.RIGHT ? 1 : -1);
			Tile graspedTile = world.getMap().tileAt(mid().getXi() + (Tile.getScale()*dir), mid().getYi());
			
			/*-- Sprinting Control --*/
			if (controller.held(Controls.SPRINT)) {
				speedFactor = 1.5;
				wclimbAnim.setSpeed(1.5);
			}
			else{
				speedFactor = 1;
				wclimbAnim.setSpeed(1);
			}
			
			if ( graspedTile instanceof BlockTile ) {
				/*-- Basic Movement while climbing --*/
				if (controller.held(Controls.UP)){
						movt = movt.add( new Vector2D(0,-1).mult(baseClimbSpeed * speedFactor) );
						wclimbAnim.setPlayback(-1);
						wclimbAnim.update(delta);
				}
				if (controller.held(Controls.DOWN)){
						movt = movt.add( new Vector2D(0,1).mult(baseClimbSpeed * speedFactor) );
						wclimbAnim.setPlayback(1);
						wclimbAnim.update(delta);
				}
			} else {
				/* There is no tile to hold onto */
				if (controller.held(Controls.UP))
					push(new Vector2D(0,-130));
				else
					push(new Vector2D(0,0));
			}

			/*-- Try to jump if the JUMP button is pressed --*/
			if (controller.pressed(Controls.JUMP) && ableToJump) {
				if (controller.held(Controls.DOWN)){
					push(new Vector2D(0,0));
				} else {
					push(new Vector2D(0,-150));
				}
			}
			
			/*-- Let go of the wall if the CLIMB button is released --*/
			if (!controller.held(Controls.CLIMB)) {
				push(new Vector2D(0,0));
			}
			
		}
		
		if (getState() == PlayerState.DEAD) {	
			/*-- Let gravity act on the player --*/
			setVel(getVel().add((world.getGravity()).mult(delta)));
			if (getVel().getY() > maxVel)
				setVel(new Vector2D( getVel().getX(), maxVel));
			
			movt = movt.add( getVel() );
		}
		
		if (stunned) {
			stunCooldown -= delta;
			
			if (stunCooldown <= 0) {
				stunCooldown = 0;
				stunned = false;
			}
		}
		
		/*-- Check collision and move player appropriately --*/
		CollisionData colData = CollisionHandler.checkWorldCollision(boundingBox, movt, delta, world);
		setMovtCorrection(colData.correction);

		move(getMovtCorrection());
		
		/*-- Take actions based on type of collisions --*/
		if (colData.top)
			setVel(new Vector2D(0, 0));
		if (colData.bottom){
			changeState(PlayerState.STANDING);
			setVel(new Vector2D(0, 0));
			if (movt.getY() > 210)
				System.out.format("Y-Velocity upon impact: %f (%d)\n", movt.getY(), 260);
			/* Dealing Fall Damage */
			if (movt.getY() > 260)
				damage((int)Math.ceil((movt.getY()-260)/70)/*, new Vector2D(10, movt.getY()/2)*/);
		}
		if (colData.right || colData.left) {
			int dir = (direction == Direction.RIGHT ? 1 : -1); 
			Tile touchedTile = world.getMap().tileAt(mid().getXi() + (Tile.getScale()*dir), mid().getYi());
			/* Try to destroy the wall if the DIG button and LEFT/RIGHT is pressed */
			if (getState() == PlayerState.RUNNING && controller.held(Controls.DIG) ) {
				if (touchedTile instanceof BlockTile && ((BlockTile)touchedTile).getType() == BlockType.DIRT) {
					touchedTile.destroy();
				}
			}
			/* Try to cling onto wall */
			if (getState() == PlayerState.AIRBORNE && controller.held(Controls.CLIMB) ) {
				if (touchedTile instanceof BlockTile) {
					setVel(new Vector2D(0, 0));
					changeState(PlayerState.WALL_CLIMBING);
				}
			}
		}
		
		/*-- Actions that can be taken at any time --*/
		if (controller.pressed(Controls.USE_ITEM)) {
			SpaceTile tile = inTile(world);
			
			if (tile.getObject() == null) {
				new TileObjectTorch(tile);
			}
			else if (tile.getObject().getType() == TileObjectType.TORCH){
				((TileObjectTorch)tile.getObject()).destroy();
			}
		}
		
		/*-- Special Rules for Tiles the player is in --*/
		inTile(world).affectPlayer(this);
		
		
		
		/*-- DEBUG TOOLS TODO: remove--*/
		if (Keyboard.isKeyDown(Keyboard.KEY_1))
			kill();

		if (Keyboard.isKeyDown(Keyboard.KEY_2))
			damage(0);
		
		if (Keyboard.isKeyDown(Keyboard.KEY_3) && getState().equals(PlayerState.DEAD))
			resurrect();
		/*------------*/
		
		/*-- Update Animations --*/
		runAnim.update(delta);
		idleAnim.update(delta);
		deathAnim.update(delta);
		
		
	}
	
	public Vector2D move(double delta, Vector2D movt) {
		return boundingBox.move( movt );
	}
	
	public Vector2D move(Vector2D movt) {
		return boundingBox.move( movt );
	}
	
	public void push(Vector2D push) {
		setVel(getVel().add( push ));
		changeState(PlayerState.AIRBORNE);
	}
	

	public void damage(int damage, Vector2D knockback) {
		if (!stunned) {
			setVel(new Vector2D(0,0));
			setHp(getHp() - damage);
			
			System.out.format("Player took damage of: %d\n", damage);
			
			if (getHp() <= 0){
				/* If player has died */
				kill();
			} 

			stunned = true;
			stunCooldown = 1;

			knockback = new Vector2D( knockback.getX() * (direction.equals(Direction.RIGHT) ? -1 : 1), knockback.getY() * -1);
			push( knockback );
		}
	}
	
	public void damage(int damage) {
		//int pushX = 10 * (direction.equals(Direction.RIGHT) ? -1 : 1);
		Vector2D knockback = new Vector2D(40, 100) ;
		damage(damage, knockback);	
	}
	
	public void kill() {
		changeState(PlayerState.DEAD);
		Game.playerDead();
		System.out.format("Player has died.\n");
	}
	
	public SpaceTile inTile(World world) {
		SpaceTile containedIn;
		
		if( world.getMap().tileAt(mid().getXi(), mid().getYi()) instanceof SpaceTile ) {
			containedIn = (SpaceTile)world.getMap().tileAt(mid().getXi(), mid().getYi());
			return containedIn;
		}
		
		return null;
	}
	
	
	public void draw(Vector2D offset, World world) {
		/* draw intersection tiles */
		/*
		int scale = world.map.scale();
		int width = IntersectionColumns.length;
		int height = IntersectionRows.length;
		
		GL11.glColor4f( .8f, 0, 0, 1f);	
		for (Tile tile : world.map.tiles()) {
			if ( (tile.locx >= IntersectionColumns[0]*scale && tile.locx <= IntersectionColumns[width-1]*scale) ||
				 (tile.locy >= IntersectionRows[0]*scale && tile.locy <= IntersectionRows[height-1]*scale)	     )
				tile.draw(offset);
		}
		*/         
		boolean flipped = direction.equals(Direction.LEFT);
		
		Color col = stunned ? new Color(255,100,100,100) : new Color(255,255,255,255);
		
		
		if (getState().equals(PlayerState.STANDING))
			idleAnim.draw( pos().add(offset).add(new Vector2D(0, 0)), col, flipped);
		
		if (getState() == PlayerState.WALL_CLIMBING)
			wclimbAnim.draw( pos().add(offset).add(new Vector2D(-2, 0)), col, flipped);
		
		if (getState().equals(PlayerState.AIRBORNE)) {
			if (movt.getY() <= -100)
				jumpAsc.draw( pos().add(offset).add(new Vector2D(0, 0)), col, flipped);
			if (movt.getY() > -100 && movt.getY() < 100)
				jumpPeak.draw( pos().add(offset).add(new Vector2D(0, 0)), col, flipped);
			if (movt.getY() >= 100)
				jumpDesc.draw( pos().add(offset).add(new Vector2D(0, 0)), col, flipped);	
		}
		
		if (getState().equals(PlayerState.RUNNING)) 
			runAnim.draw( pos().add(offset).add(new Vector2D(0, 0)), col, flipped);
		
		/*
		if (state.equals(PlayerState.DEAD)) 
			deathAnim.draw( pos().add(offset).add(new Vector2D(-15, -22)), col, flipped);
		*/
		if (getState().equals(PlayerState.DEAD)) 
			deathAnim.draw( pos().add(offset).add(new Vector2D(-5, -1)), col, flipped);
		
		/*
		GL11.glColor4f( 1f, 1f, 1f, 1f);	
		pic.getTexture().bind();
		int offX = offset.getXi();
		int offY = offset.getYi();
		
		float sclX = pic.sclX();
		float sclY = pic.sclY();
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(0,0);
			GL11.glVertex2f(getXi() + offX, getYi() + offY);
			GL11.glTexCoord2f(sclX,0);
			GL11.glVertex2f(getOppXi() + offX, getYi() + offY);
			GL11.glTexCoord2f(sclX, sclY);
			GL11.glVertex2f(getOppXi() + offX, getOppYi() + offY);
			GL11.glTexCoord2f(0,sclY);
			GL11.glVertex2f(getXi() + offX, getOppYi() + offY);
		GL11.glEnd();
		*/
	}
	
	public Vector2D pos() { return boundingBox.pos(); }
	public Vector2D mid() { return boundingBox.mid(); }
	public Vector2D opp() { return boundingBox.opp(); }
	
	public double getX() { return pos().getX(); }
	public double getY() { return pos().getY(); }
	public int getXi() { return (int) getX(); }
	public int getYi() { return (int) getY(); }
	
	public double getOppX() { return opp().getX(); }
	public double getOppY() { return opp().getY(); }
	public int getOppXi() { return (int) getOppX(); }
	public int getOppYi() { return (int) getOppY(); }

	public Vector2D getMovtCorrection() { return movtCorrection; }
	private void setMovtCorrection(Vector2D movtCorrection) { this.movtCorrection = movtCorrection; }

	public int getHp() { return hp; }
	private void setHp(int hp) { this.hp = hp; }

	public PlayerState getState() { return state; }
	private void setState(PlayerState state) { this.state = state; }

	public Vector2D getVel() { return vel; }
	private void setVel(Vector2D vel) { this.vel = vel; }
	
	//public Sprite sprite() { return pic; }
}


