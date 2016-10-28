package drawing;
import geom.Vector2D;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.Color;

public class Sprite {
	private Texture texture;
	int width, height;
	private int baseWidth, baseHeight;
	private float sclX, sclY;
	
	public Sprite (Texture texture, int width, int height){
		this.texture = texture;
		this.baseWidth = this.width = width;
		this.baseHeight = this.height = height;
		sclX = (float) texture.getImageWidth() / texture.getTextureWidth();
		sclY = (float) texture.getImageHeight() / texture.getTextureHeight();
	}
	
	public Sprite (Texture texture){
		this.texture = texture;
		this.baseWidth = this.width = texture.getImageWidth();
		this.baseHeight = this.height = texture.getImageHeight();
		sclX = (float) texture.getImageWidth() / texture.getTextureWidth();
		sclY = (float) texture.getImageHeight() / texture.getTextureHeight();
	}
	
	public void draw(Vector2D pos, int width, int height, Color col) {
		col.bind();	
		texture.bind();
		
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(0,0);
			GL11.glVertex2f(pos.getXi(), pos.getYi());
			GL11.glTexCoord2f(sclX,0);
			GL11.glVertex2f(pos.getXi() + width, pos.getYi());
			GL11.glTexCoord2f(sclX, sclY);
			GL11.glVertex2f(pos.getXi() + width, pos.getYi() + height);
			GL11.glTexCoord2f(0,sclY);
			GL11.glVertex2f(pos.getXi(), pos.getYi() + height);
		GL11.glEnd();
	}
	
	
	public void draw(Vector2D pos, Color col, boolean flipped) {
		if (flipped)
			drawFlipped(pos, col);
		else
			draw(pos, col);
	}
	
	public void draw(Vector2D pos, Color col) {
		col.bind();	
		texture.bind();
		
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(0,0);
			GL11.glVertex2f(pos.getXi(), pos.getYi());
			GL11.glTexCoord2f(sclX,0);
			GL11.glVertex2f(pos.getXi() + width, pos.getYi());
			GL11.glTexCoord2f(sclX, sclY);
			GL11.glVertex2f(pos.getXi() + width, pos.getYi() + height);
			GL11.glTexCoord2f(0,sclY);
			GL11.glVertex2f(pos.getXi(), pos.getYi() + height);
		GL11.glEnd();
	}

	public void drawFlipped(Vector2D pos, Color col) {
		col.bind();	
		texture.bind();
		
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(sclX,0);
			GL11.glVertex2f(pos.getXi(), pos.getYi());
			GL11.glTexCoord2f(0,0);
			GL11.glVertex2f(pos.getXi() + width, pos.getYi());
			GL11.glTexCoord2f(0, sclY);
			GL11.glVertex2f(pos.getXi() + width, pos.getYi() + height);
			GL11.glTexCoord2f(sclX,sclY);
			GL11.glVertex2f(pos.getXi(), pos.getYi() + height);
		GL11.glEnd();
	}
	
	public Texture getTexture() { return texture;    }
	public int getBaseHeight()  { return baseHeight; }
	public int getBaseWidth()   { return baseWidth;  }
	
	public float sclX() { return sclX; }
	public float sclY() { return sclY; }
	
}