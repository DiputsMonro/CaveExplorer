package gui;

import inputHelpers.MouseHelper;

import org.lwjgl.input.Mouse;
import org.newdawn.slick.Color;

import ui.UIHelper;

import drawing.GLHelper;

import geom.Vector2D;

public class GUIButton extends AGUIDataComponent {
	Color hoverCol, pressedCol, textCol, col;
	String label;
	boolean hover, pressed;
	
	public GUIButton(String label, int width, int height) {
		setup();
		this.label = label;
		this.width = Math.min(width, UIHelper.getWidth(label) + 6);
		this.height = height;
	}
	
	public GUIButton(String label) {
		setup();
		this.label = label;
		this.width = UIHelper.getWidth(label) + 6;
		this.height = 16;
	}
	
	public void setup() {
		label = "";
		hoverCol = new Color(150,150,150);
		pressedCol = new Color(80, 80, 80);
		textCol = Color.black;
		col = new Color(180,180,180);
		pressed = false;
		hover = false;
	}
	
	@Override
	public void update(Vector2D mpos) {
		if (inBounds(mpos.sub(pos))) {
			hover = true;
			if (MouseHelper.click(0)) {
				pressed = true;
			}
			if (pressed && !Mouse.isButtonDown(0)) {
				/* Mouse has been 'clicked', notify receivers */
				pressed = false;
				notifyReceivers(true);
			}
		}
		else {
			hover = false;
		}
		
		if (!Mouse.isButtonDown(0))
			pressed = false;

	}

	@Override
	public void draw() {
		Color toUse = col;
		if (hover && !pressed)
			toUse = hoverCol;
		if (hover && pressed)
			toUse = pressedCol;
		
		toUse.bind();
		
		int locx = pos.getXi();
		int locy = pos.getYi();
		
		GLHelper.rectfill(locx, locy, locx + width, locy + height, toUse);
		UIHelper.drawStringCentered(locx + width / 2, locy + height/2 - 9, label, textCol);
		
	}
	
	public void setHoverColor(Color col) { hoverCol = col; }
	public void setPressedColor(Color col) { pressedCol = col; }
	public void setColor(Color col) { this.col = col; }
	public void setTextColor(Color col) { textCol = col; }
}
