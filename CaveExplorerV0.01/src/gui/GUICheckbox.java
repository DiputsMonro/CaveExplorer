package gui;

import inputHelpers.MouseHelper;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;

import drawing.GLHelper;

import ui.UIHelper;

import geom.Vector2D;

public class GUICheckbox extends AGUIDataComponent {
	private boolean checked;
	String label;
	Color col, drawCol;
	
	public GUICheckbox(String label, boolean state) { 
		setup();
		this.label = label;
		this.checked = state;
	}
	
	public GUICheckbox() { 
		setup();
	}
	
	private void setup() {
		this.label = "";
		setColor(Color.white);
		width = 12;
		height = 12;
		checked = false;
	}
	
	@Override
	public void update(Vector2D mpos) {
		if (inBounds(mpos.sub(pos))) {
			drawCol = Color.yellow;
			if (MouseHelper.click(0)) {
				checked = !checked;
				notifyReceivers(checked);
			}
		}
		else
			drawCol = col;
	}

	@Override
	public void draw() {
		// TODO Auto-generated method stub
		/* Draw box */
		drawCol.bind();
		
		GL11.glDisable(GL11.GL_TEXTURE_2D); 
		int locx = pos.getXi(),
			oppx = pos.getXi() + width,
			locy = pos.getYi(),
			oppy = pos.getYi() + height;
				    
		GL11.glBegin(GL11.GL_LINE_STRIP);
			GL11.glVertex2f(locx, locy);
			GL11.glVertex2f(oppx, locy);
			GL11.glVertex2f(oppx, oppy);
			GL11.glVertex2f(locx, oppy);
			GL11.glVertex2f(locx, locy);
		GL11.glEnd();
		
		if (checked) {
			GLHelper.rectfill(locx + 2, locy + 1, oppx - 1, oppy -2, drawCol);
		}
		
		GL11.glEnable(GL11.GL_TEXTURE_2D); 
		
		UIHelper.drawString(pos.getXi() + width + 7, pos.getYi() - 3, label, drawCol);
	}
	
	public void setColor(Color newcol) { 
		this.col = newcol;  
		drawCol = newcol;
	}
	
	public void setLabel(String label) { this.label = label; }
}
