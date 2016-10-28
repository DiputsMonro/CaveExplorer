package drawing;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;

public class GLHelper {
	
	public static void rect(int x1, int y1, int x2, int y2, Color col) {
		col.bind();
		
		GL11.glDisable(GL11.GL_TEXTURE_2D); 
				    
		GL11.glBegin(GL11.GL_LINE_STRIP);
			GL11.glVertex2f(x1, y1);
			GL11.glVertex2f(x2, y1);
			GL11.glVertex2f(x2, y2);
			GL11.glVertex2f(x1, y2);
			GL11.glVertex2f(x1, y1);
		GL11.glEnd();
		
		GL11.glEnable(GL11.GL_TEXTURE_2D); 
	}
	
	public static void rectfill(int x1, int y1, int x2, int y2, Color col) {
		col.bind();
		
		GL11.glDisable(GL11.GL_TEXTURE_2D); 
				    
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex2f(x1, y1);
			GL11.glVertex2f(x2, y1);
			GL11.glVertex2f(x2, y2);
			GL11.glVertex2f(x1, y2);
		GL11.glEnd();
		
		GL11.glEnable(GL11.GL_TEXTURE_2D); 
	}
}
