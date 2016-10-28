package gui;

import org.newdawn.slick.Color;

import geom.Vector2D;
import ui.UIHelper;

public class GUIText extends AGUIComponent {
	private String text;
	private Color col, drawCol;
	private GUIAlign align;
	
	public GUIText(String text) {
		this.text = text;
		width = UIHelper.getWidth(text);
		height = UIHelper.getHeight(text);
		col = Color.white;
		drawCol = col;
		align = GUIAlign.LEFT;
	}
	
	public void update(Vector2D mpos) {
//		if (inBounds(mpos.sub(pos)))
//			drawCol = Color.yellow;
//		else
//			drawCol = col;
	}
	
	public void draw() {
		switch (align) {
			case LEFT:
				UIHelper.drawString(pos.getXi(), pos.getYi(), text, drawCol);
				break;
			case CENTER:
				UIHelper.drawStringCentered(pos.getXi(), pos.getYi(), text, drawCol);;
				break;
		}
	}
	
	public boolean inBounds(Vector2D pos) {
		switch (align) {
			case LEFT:
				if (pos.getX() >= 0 && pos.getX() <= width && pos.getY() >= 0 && pos.getY() <= height)
					return true;
				break;
			case CENTER:
				if (pos.getX() >= -width/2 && pos.getX() <= width/2 && pos.getY() >= 0 && pos.getY() <= height)
					return true;
				break;
		}
		
		return false;
	}
	
	public void setColor(Color col) { 
		this.col = col;
		drawCol = col;
	}
	public void setAlignment(GUIAlign align) { this.align = align; }
}
