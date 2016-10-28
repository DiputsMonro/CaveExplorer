package game;

import geom.Vector2D;
import gui.AGUIDataReceiver;
import gui.GUIAlign;
import gui.GUIButton;
import gui.GUICheckbox;
import gui.GUIRect;
import gui.GUISlider;
import gui.GUIText;
import gui.GUIWindow;
import inputHelpers.GamepadHelper;
import inputHelpers.KeyboardHelper;
import inputHelpers.MouseHelper;
import menus.AMenu;
import menus.SelectionMenu;

import java.awt.Font;
import java.io.IOException;
import java.util.*;

import objects.BlockTile;
import objects.PlatformPlayer;
import objects.SpaceTile;
import objects.Tile;
import objects.TileObjectTorch;
import objects.TileObjectType;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Controllers;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.opengl.*;
import org.newdawn.slick.util.ResourceLoader;

import controllers.ControllerInterface;
import controllers.Controls;
import controllers.KeyboardController;

import drawing.GLHelper;
import drawing.Sprite;

import ui.UIHelper;


enum Gamestate {TITLE, MAIN, MENU, DEAD, PAUSED};

@SuppressWarnings("deprecation")
public class Game {
	private static int scr_x = 640;
	static int scr_y = 480;
	
	static float version = 0.01f;
	
	static double timescale = 1;
	
	static World world;
	static Camera camera;
	
	static Sprite tileBorder;
	static Tile selectedTile;
	
	static GUIWindow pauseWin;
	
	static Gamestate gamestate, prevgamestate;
	
	static long lastFrame;
	
	ControllerInterface controller;
	
	static ArrayList<GUIWindow> mainWindows, pauseWindows;
	
	//static AMenu currentMenu = null;
	
	static AMenu optionsMenu;
	
	TrueTypeFont font;
	
	public void start() {
		initOpenGL(getScr_x(), scr_y);
		init();
		
		getDelta(); // call once before loop to initialise lastFrame
		
		while (true) {
			// convert milliseconds to seconds
			// double delta measured in seconds
			double delta = ((double) getDelta()) / 1000;
			
			logic(world, delta);
			
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			render();
			
			Display.update();
			Display.sync(60);
			
			if (Display.isCloseRequested()) {
				Display.destroy();
				System.exit(0);
			}
		}
	}
	
	public void initOpenGL(int width, int height) {
		try {
			Display.setDisplayMode(new DisplayMode(width,height));
			Display.create();
			Display.setTitle("Cave Explorer v." + version);
			Display.setVSyncEnabled(true);
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(0);
		}

		
		GL11.glEnable(GL11.GL_TEXTURE_2D);               
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);          
        
        	// enable alpha blending
        	GL11.glEnable(GL11.GL_BLEND);
        	GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        
        	GL11.glViewport(0,0,width,height);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);

		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, width, height, 0, 1, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);

	
	}
	
	/*
	 * Initialize resources
	 */
	public void init() {
		gamestate = Gamestate.TITLE;
		
		//Setup Gamepad helper
		GamepadHelper.init();
		
		//Init controller
		controller = new KeyboardController();
		
		//Tile.loadTileSprites();
		world = new World();
		world.loadMap("res/Map2.mp1");

		Texture texture = null;
		Texture blockT = null;
		Texture blinky = null;
		try {
			// load texture from PNG file
			blockT = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/image.png"));
			blinky = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/AniStone.png"));
			tileBorder = new Sprite(TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/TileBorder.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//create Sprite from texture
		world.player = new PlatformPlayer(controller, world.getMap().playerLoc, 6, 10 );
		world.player.loadResources();

		Sprite blockS = new Sprite(blockT);
		//world.blocks = new ArrayList<WorldBlock>();
		//world.blocks.add( new WorldBlock( new Vector2D(300, 300), BlockType.STONE, blockS));
		
		/* Init UIHelper */
		UIHelper.init();
			
		/* Setup Camera */
		camera = new Camera(0, 0, getScr_x(), scr_y, font);
		
//		/* Setup Options menu */
//		optionsMenu = new SelectionMenu("Options", "Dummy 1", "Dummy 2", "Exit");
		
		/* Setup windows active during the main gamestate */
		mainWindows = new ArrayList<GUIWindow>();
		
		/* Setup windows active during the pause gamestate */
		pauseWindows = new ArrayList<GUIWindow>();
		
		pauseWin = new GUIWindow(new Vector2D(100,100), 200, 300);
		
		GUIRect pauseBG = new GUIRect(pauseWin.getSize().getXi(), pauseWin.getSize().getYi(), new Color(0, 0, 0, 230));
		pauseWin.addComponent(pauseBG, new Vector2D(0, 0));
		
		GUIText pauseText = new GUIText("PAUSE");
		pauseText.setAlignment(GUIAlign.CENTER);
		pauseWin.addComponent(pauseText, new Vector2D(pauseWin.getSize().getXi()/2, 0));
		
		AGUIDataReceiver<String> torchlightRcvr = new AGUIDataReceiver<String>() {

			@Override
			public void update(String id, Object arg) {
				Color col = TileObjectTorch.lightColor;
				
				if (id.equals("Draw"))
					Camera.setLighting((boolean) arg);
				else if (id.equals("SetColorR")) 
					TileObjectTorch.lightColor = new Color((int)arg / 255f, col.g, col.b, col.a);
				else if (id.equals("SetColorG"))
					TileObjectTorch.lightColor = new Color(col.r, (int)arg / 255f, col.b, col.a);
				else if (id.equals("SetColorB"))
					TileObjectTorch.lightColor = new Color(col.r, col.g, (int)arg / 255f, col.a);
				else if (id.equals("SetColorA"))
					TileObjectTorch.lightColor = new Color(col.r, col.g, col.b, (int)arg / 255f);
				else
					System.err.format("torchlightRcvr received unrecognized update ID: \"%s\"\n", id);
			}
			
		};
		
		GUICheckbox pauseCB = new GUICheckbox("Draw lighting", false);
		torchlightRcvr.registerDataInput("Draw", pauseCB);
		pauseWin.addComponent(pauseCB, new Vector2D(10, 50));
		
		GUISlider pauseSliderR = new GUISlider(0, 255, 0);
		pauseSliderR.setFillColor(Color.red);
		torchlightRcvr.registerDataInput("SetColorR", pauseSliderR);
		pauseWin.addComponent(pauseSliderR, new Vector2D(10, 100));
		
		GUISlider pauseSliderG = new GUISlider(0, 255, 0);
		pauseSliderG.setFillColor(Color.green);
		torchlightRcvr.registerDataInput("SetColorG", pauseSliderG);
		pauseWin.addComponent(pauseSliderG, new Vector2D(10, 120));
		
		GUISlider pauseSliderB = new GUISlider(0, 255, 0);
		pauseSliderB.setFillColor(Color.blue);
		torchlightRcvr.registerDataInput("SetColorB", pauseSliderB);
		pauseWin.addComponent(pauseSliderB, new Vector2D(10, 140));
		
//		GUISlider pauseSliderA = new GUISlider(0, 255, 0);
//		pauseSliderA.setFillColor(Color.black);
//		pauseBG.registerDataInput("SetColorA", pauseSliderA);
//		pauseWin.addComponent(pauseSliderA, new Vector2D(10, 160));
		
		GUIButton pauseButton = new GUIButton("button");
		pauseWin.addComponent(pauseButton, new Vector2D(30, 200));
		
		pauseWin.setActive(true);
		pauseWindows.add(pauseWin);
		//pauseWin.addComponent(pauseText, new Vector2D(15,15));
	}
	
	/** 
	 * Calculate how many milliseconds have passed 
	 * since last frame.
	 * 
	 * @return milliseconds passed since last frame 
	 */
	public static int getDelta() {
	    long time = getTime();
	    int delta = (int) (time - lastFrame);
	    lastFrame = time;
	 
	    return delta;
	}
	
	/**
	 * Get the accurate system time
	 * 
	 * @return The system time in milliseconds
	 */
	public static long getTime() {
	    return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}
	
	//Game Logic
	public void logic(World world, double delta) {
		KeyboardHelper.refresh();
		MouseHelper.refresh();
			
		
		if (gamestate == Gamestate.PAUSED) {
			
			for (GUIWindow win : pauseWindows) {
				if (win.isActive())
					win.update();
			}
			
			if (controller.pressed(Controls.PAUSE)) {
				changeGamestate(prevgamestate);
				controller.consume(Controls.PAUSE);
			}
		
		}
		
		if (gamestate == Gamestate.TITLE) {
			if(KeyboardHelper.pressed(Keyboard.KEY_RETURN))
				changeGamestate(Gamestate.MAIN);
			
			/*-- Options menu --*/
			if(KeyboardHelper.pressed(Keyboard.KEY_SPACE)) {
				String result = executeMenu(new SelectionMenu("Options", "Change Controls", "Exit"));				
				System.out.format("Chose %s\n", result);
				
//				if (result == "Change Controls") {
//					String control = null;
//					while (true) {
//						Controls controls[] = Controls.values();
//						String menuData[] = new String [controls.length+1];
//						for (int i = 0; i < controls.length; i++) {
//							menuData[i] = controls[i].name();
//						}
//						menuData[controls.length] = "Exit";
//						control = executeMenu(new SelectionMenu("Controls", menuData));	
//						System.out.format("Chose: %s\n", control);
//						
//						if (control == "Exit")
//							break;
//						
//						int response = Message
//						
//						//controller.remap(Controls.valueOf(control)
//					}
//						
//				}
			}
		}
		
		if (gamestate == Gamestate.MAIN || gamestate == Gamestate.DEAD) {
			/*
			if(KeyboardHelper.pressed(Keyboard.KEY_NUMPAD8))
				timescale += .1;
			if(KeyboardHelper.pressed(Keyboard.KEY_NUMPAD2))
				timescale -= .1;
			if(KeyboardHelper.pressed(Keyboard.KEY_NUMPAD5))
				timescale = 1;

			delta = delta * timescale;
			 */
			
			if (controller.pressed(Controls.PAUSE)) {
				changeGamestate(Gamestate.PAUSED);
			}
				
			
			int mx = Mouse.getX() - camera.offset().getXi();
			int my = scr_y - Mouse.getY() - camera.offset().getYi();
			int mw = Mouse.getDWheel() / 120;

			if (mx > 0 && mx < world.getMap().width() * world.getMap().scale() &&
					my > 0 && mx < world.getMap().height() * world.getMap().scale() ) {

				selectedTile = world.getMap().tileAt(mx, my);

			}

			if (selectedTile != null) {

				if (MouseHelper.click(0)) {
					if( selectedTile instanceof SpaceTile ) {
						SpaceTile selected = (SpaceTile)selectedTile;
						if (selected.getObject() == null) {
							new TileObjectTorch(selected);
						}
					}
					if( selectedTile instanceof BlockTile ) {
						BlockTile selected = (BlockTile)selectedTile;
						selected.destroy();
					}
				}

				if (MouseHelper.click(1)) {
					if( selectedTile instanceof SpaceTile ) {
						SpaceTile selected = (SpaceTile)selectedTile;
						if (selected.getObject() != null && selected.getObject().getType() == TileObjectType.TORCH) {
							TileObjectTorch torch = (TileObjectTorch) selected.getObject();

							torch.destroy();
						}

					}
				}

			}

			world.update(delta);
			camera.update(delta, world);
		}
		
		if (gamestate == Gamestate.DEAD) {
			if(KeyboardHelper.pressed(Keyboard.KEY_SPACE)) {
				changeGamestate(Gamestate.MAIN);
				world.player.resurrect();
			}
		}
	}
	
	public static void render() {
		Color txtCol = new Color(130,0,130);
		GL11.glColor4f( 1f, 1f, 1f, 1f);
		
		if (gamestate == Gamestate.TITLE) {
			UIHelper.drawStringCentered(scr_x/2, 200, "Press [Enter] to begin", Color.red);
			UIHelper.drawStringCentered(scr_x/2, 220, "Press [Space] for Options", Color.red);
			UIHelper.drawStringCentered(scr_x/2, 250, "v0.01", Color.red);
		}
		
		if (gamestate == Gamestate.MAIN || gamestate == Gamestate.PAUSED) {
			/* Draw the world */
			camera.draw(world);
			
			/* Draw debug info */
			Color.white.bind(); 
			UIHelper.drawString(400, 10, "Map: " + world.getMap().name(), txtCol);
			UIHelper.drawString(400, 25, world.getMap().width() + "x" + world.getMap().height() + "  scale: " + world.getMap().scale(),
					                 txtCol);
			
			UIHelper.drawString(400, 55, "Player Health: " + world.player.getHp(), txtCol);
			UIHelper.drawString(400, 70, "Player State: " + world.player.getState().toString(), txtCol);
			//UIHelper.drawString(400, 70, "Player speedFactor: " + world.player.speedFactor, txtCol);
			UIHelper.drawString(400, 85, "Player velY: " + (world.player.getVel().getY() < 500 ? world.player.getVel().getY() : "MAX!"), txtCol);
			
			UIHelper.drawString(400, 115, "Screen X: " + camera.bounds.getPos().getX() + ", Y: " + camera.bounds.getPos().getY(), txtCol);
			
			UIHelper.drawString(400, 145, "timescale: " + timescale, txtCol);
			//------
			
			//Draw Tile Border
			if( selectedTile != null) {
				tileBorder.draw(new Vector2D(selectedTile.getLocx() * Tile.getScale(), selectedTile.getLocy() * Tile.getScale()).add(camera.offset()), new Color(0,255,0) );
				
				UIHelper.drawString(400, 170, "Selected Tile Information: ", txtCol);
				UIHelper.drawString(420, 185, "Location: " + selectedTile.getLocx() + ", " + selectedTile.getLocy(), txtCol);
				UIHelper.drawString(420, 200, "Class: " + selectedTile.getClass().getName(), txtCol);
				
				if( selectedTile instanceof SpaceTile ) {
					SpaceTile tile = (SpaceTile)selectedTile;
					//if( tile.object != null )
						UIHelper.drawString(420, 215, "Contains: " + (tile.getObject() == null ? "Null" : tile.getObject().getClass().getName()), txtCol);
				}
				
				
				UIHelper.drawString(420, 230, "lightLevel: " + selectedTile.getLightLevel(), txtCol);
			}
		}
		
		if (gamestate == Gamestate.DEAD) {
			/* Draw the world */
			camera.draw(world);
			
			UIHelper.drawStringCentered(scr_x/2, 200, "You have died. Press [Space] to continue", Color.red);
		}
		
		if (gamestate == Gamestate.PAUSED) {
			//GLHelper.rectfill(0, 0, scr_x, scr_y, new Color(0,0,0,200));
			
			for (GUIWindow win : pauseWindows) {
				if (win.isActive())
					win.draw();
			}
		}
		
	}
	
	public static void main(String[] argv){
		Game game = new Game();
		game.start();
		
		return;
	}
	
	public static void playerDead() {
		changeGamestate(Gamestate.DEAD);
	}
	
	public static String executeMenu(AMenu menu) {
		String response = null;
			
		while (response == null) {
			KeyboardHelper.refresh();
			response = menu.update();
			
			double delta = ((double) getDelta()) / 1000;
			
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			render();
			menu.render();
			
			Display.update();
			Display.sync(60);
			
			if (Display.isCloseRequested()) {
				Display.destroy();
				System.exit(0);
			}
		}
		
		return response;
	}

	public static int getScr_x() {
		return scr_x;
	}

	public static void setScr_xREMOVE(int scr_x) {
		Game.scr_x = scr_x;
	}
	
	public static void changeGamestate(Gamestate newstate) {
		prevgamestate = gamestate;
		gamestate = newstate;
	}
}
