package gui;

import geom.Vector2D;

import inputHelpers.MouseHelper;

import java.util.ArrayList;
import java.util.List;

import objects.Tile;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;

public class GUIWindow extends AGUIReceiverComponent<String> {
	private List<IGUIComponent> components;
	private Vector2D pos;
	private int width, height;
	private boolean active;
	
	public GUIWindow(Vector2D pos, int width, int height) {
		this.pos = pos;
		this.height = height;
		this.width = width;
		components = new ArrayList<IGUIComponent>();
	}
	
	public void update() {
		Vector2D mpos = MouseHelper.pos();
	
		for (IGUIComponent c : components) {
			c.update(mpos);
		}
	}
	
	public void draw() {		
		/* Draw all of the children components first */
		for (IGUIComponent c : components) {
			c.draw();
		}
		
		/* Draw window border */
		Color col = new Color(255,255,255,255);
		col.bind();
		
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
		
		GL11.glEnable(GL11.GL_TEXTURE_2D); 
	}
	
	public void addComponent(IGUIComponent toAdd, Vector2D off) {
		toAdd.setPos(pos.add(off));
		components.add(toAdd);
	}

	public void update(String id, Object arg) {
		if (id.equals("CloseWindow")) {
			active = false;
		}
		else {
			System.err.format("GUIWindow received unrecognized update ID: %s\n", id);
		}
	}

	public void setActive(boolean state) { active = state; }
	
	public Vector2D getSize() { return new Vector2D(width, height); }
	public boolean isActive() { return active; }
	
	public void update(Vector2D mpos) {}
}
