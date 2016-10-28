package gui;

import java.util.Observable;
import java.util.Observer;

import geom.Vector2D;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;

import ui.UIHelper;


public class GUIRect extends AGUIReceiverComponent<String> {
	private Color col;
	private boolean draw;
	
	public GUIRect(int width, int height, Color col) {
		this.width = width;
		this.height = height;
		this.col = col;
		this.draw = true;
	}
	
	public void update(Vector2D mpos) {
		
	}
	
	public void draw() {
		/* Do not draw if draw is set to false */
		if (!draw)
			return;
		
		/* Draw colored rectangle */
		col.bind();
		
		GL11.glDisable(GL11.GL_TEXTURE_2D); 
		int locx = pos.getXi(),
			oppx = pos.getXi() + width,
			locy = pos.getYi(),
			oppy = pos.getYi() + height;
				    
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex2f(locx, locy);
			GL11.glVertex2f(oppx, locy);
			GL11.glVertex2f(oppx, oppy);
			GL11.glVertex2f(locx, oppy);
		GL11.glEnd();
		
		GL11.glEnable(GL11.GL_TEXTURE_2D); 
	}

	public void update(String id, Object arg) {
		if (id.equals("Draw"))
			draw = (boolean) arg;
		else if (id.equals("SetColorR")) 
			col = new Color((int)arg / 255f, col.g, col.b, col.a);
		else if (id.equals("SetColorG"))
			col = new Color(col.r, (int)arg / 255f, col.b, col.a);
		else if (id.equals("SetColorB"))
			col = new Color(col.r, col.g, (int)arg / 255f, col.a);
		else if (id.equals("SetColorA"))
			col = new Color(col.r, col.g, col.b, (int)arg / 255f);
		else
			System.err.format("GUIRect received unrecognized update ID: \"%s\"\n", id);
	}

	public void setColor(Color col) { this.col = col; }
	public void setDraw(boolean draw) { this.draw = draw; }
}
