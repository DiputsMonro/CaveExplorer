package menus;

import game.Game;
import inputHelpers.KeyboardHelper;

import objects.Tile;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;

import controllers.ControllerInterface;
import controllers.Controls;
import controllers.KeyboardController;

import ui.UIHelper;

public class SelectionMenu extends AMenu {
	int numItems;
	int cursor;
	ControllerInterface controller;
	
	public SelectionMenu(String title, String... menuItems) {
		super(title, menuItems);
		numItems = menuItems.length;
		cursor = 0;
		controller = new KeyboardController();
	}
	
	public void render() {
		Color txtCol = new Color(130,0,130);
		int baseX = Game.getScr_x()/2 - 50;
		int baseY = 200;
		
		GL11.glColor4f( 1f, 1f, 1f, 1f);
		
		/* Draw black box around menu options */
		GL11.glDisable(GL11.GL_TEXTURE_2D); 
		int locx = 50,
			oppx = Game.getScr_x() - 50,
			locy = baseY - 50,
			oppy = baseY + (numItems) * 20 + 50;
				    
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex2f(locx, locy);
			GL11.glVertex2f(oppx, locy);
			GL11.glVertex2f(oppx, oppy);
			GL11.glVertex2f(locx, oppy);
		GL11.glEnd();
		
		GL11.glEnable(GL11.GL_TEXTURE_2D); 
		
		UIHelper.drawStringCentered(Game.getScr_x()/2, baseY - 40, title.toUpperCase(), Color.red);
		
		for(int i = 0; i < numItems; i++ ) {
			UIHelper.drawString(baseX, baseY + i*20, items[i], Color.red);
		}
		
		UIHelper.drawString(baseX - 20, baseY + cursor*20, ">", Color.red);
	}

	public String update() {
		if (controller.pressed(Controls.UP) || controller.pressed(Controls.LEFT))
			cursor--;
		
		if (controller.pressed(Controls.DOWN) || controller.pressed(Controls.RIGHT))
			cursor++;
		
		if (cursor < 0)
			cursor = numItems-1;
		
		if (cursor >= numItems)
			cursor = 0;
		
		if (controller.pressed(Controls.SELECT))
			return items[cursor];
		
		return null;
	}

}
