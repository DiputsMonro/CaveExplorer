package ui;

import java.awt.Font;

import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;

public class UIHelper {
	@SuppressWarnings("deprecation")
	static TrueTypeFont font;
	
	@SuppressWarnings("deprecation")
	public static void init() {
		Font awtFont = new Font("Times New Roman", Font.BOLD, 15);
		font = new TrueTypeFont(awtFont, true);	
	}
	
	@SuppressWarnings("deprecation")
	public static void drawString(float x, float y, String str, Color col) {
		font.drawString(x, y, str, col);
	}
	
	public static void drawString(float x, float y, String str) {
		font.drawString(x, y, str, Color.white);
	}
	
	public static void drawStringCentered(float x, float y, String str, Color col) {
		int width = font.getWidth(str);
		
		font.drawString(x - (width/2), y, str, col);
		
	}
	
	public static int getWidth(String text) { return font.getWidth(text); }
	public static int getHeight(String text) { return font.getHeight(text); }
}
