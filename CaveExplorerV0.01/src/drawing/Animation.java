package drawing;
import geom.Vector2D;

import java.util.HashMap;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;


public class Animation {
	int numFrames;
	int offset;
	double[] dur;
	boolean loop;
	double speed;
	int playback;  //-1: Play backward; 0: Stopped; 1: Play forward (default)
	
	int curFrame;
	double curTime;
	
	Sprite spriteSheet;
	
	public Animation(Sprite spriteSheet, int offset, double[] dur, boolean loop) {
		numFrames = dur.length;
		this.dur = dur;
		this.offset = offset;
		this.spriteSheet = spriteSheet;
		this.loop = loop;
		
		speed = 1.0;
		playback = 1;
		curFrame = 0;
		curTime = 0;
	}
	
	public Animation(Sprite spriteSheet, int offset, int numFrames, double totalTime, boolean loop) {
		this.numFrames = numFrames;
		this.offset = offset;
		this.spriteSheet = spriteSheet;
		this.loop = loop;
		
		dur = new double[numFrames];
		for (int i = 0; i < numFrames; i++) 
			dur[i] = totalTime/numFrames;
		
		speed = 1.0;
		playback = 1;
		curFrame = 0;
		curTime = 0;
	}
	
	public void restart() {
		curFrame = 0;
		curTime = 0;
	}
	
	public void update(double delta) {
		curTime += (delta * speed) * playback;
		
		if (curTime > dur[curFrame] || curTime < 0) {    //This frame has been shown long enough, go to next frame;
			curTime -= dur[curFrame] * playback;
			
			curFrame += playback;
			
			if (curFrame >= numFrames && playback == 1)
				curFrame = (loop) ?  0 : numFrames-1;
			if (curFrame < 0 && playback == -1)
				curFrame = (loop) ?  numFrames-1 : 0;
		}
		
	}
	
	public void setSpeed(double newSpeed) { speed = newSpeed; }
	public void setPlayback(int newPlayback) { playback = newPlayback; }
	
	public void draw(Vector2D pos, Color col, boolean flipped){
		drawHelper(pos, this.offset, spriteSheet.height, 1, 0, col, flipped);
	}
	
	void drawHelper(Vector2D pos, int width, int height, float scale, double rotation, Color col, boolean flipped){
		col.bind();
		spriteSheet.getTexture().bind();
		//int offX = offset.getXi();
		//int offY = offset.getYi();
		
		float sclX = spriteSheet.sclX()/numFrames;
		float sclY = spriteSheet.sclY();
		
		int x     = pos.getXi();
		int y     = pos.getYi();
		int oppx  = x + width;
		int oppy  = y + height;
		
		if (!flipped) {
			GL11.glBegin(GL11.GL_QUADS);
				GL11.glTexCoord2f((sclX*curFrame),0);
				GL11.glVertex2f(x, y);
				GL11.glTexCoord2f(sclX + (sclX*curFrame),0);
				GL11.glVertex2f(oppx, y);
				GL11.glTexCoord2f(sclX + (sclX*curFrame), sclY);
				GL11.glVertex2f(oppx, oppy);
				GL11.glTexCoord2f((sclX*curFrame),sclY);
				GL11.glVertex2f(x, oppy);
			GL11.glEnd();
		}
		else {
			GL11.glBegin(GL11.GL_QUADS);
				GL11.glTexCoord2f(sclX + (sclX*curFrame),0);
				GL11.glVertex2f(x, y);
				GL11.glTexCoord2f((sclX*curFrame),0);
				GL11.glVertex2f(oppx, y);
				GL11.glTexCoord2f((sclX*curFrame), sclY);
				GL11.glVertex2f(oppx, oppy);
				GL11.glTexCoord2f(sclX + (sclX*curFrame),sclY);
				GL11.glVertex2f(x, oppy);
			GL11.glEnd();
		}
		
	}
}
