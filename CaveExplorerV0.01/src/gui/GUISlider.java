package gui;

import inputHelpers.MouseHelper;

import org.lwjgl.input.Mouse;
import org.newdawn.slick.Color;

import ui.UIHelper;

import drawing.GLHelper;

import geom.Vector2D;

public class GUISlider extends AGUIDataComponent {
	int min, max;
	int val;
	Color fillCol, emptyCol, sliderCol;
	
	int diffx;
	
	public GUISlider(int min, int max, int defaultValue) {
		setup();
		this.min = min;
		this.max = max;
		this.val = defaultValue;
		
		if (defaultValue < min || defaultValue > max)
			val = min;
	}
	
	public void setup() {
		width = 100;
		height = 10;
		fillCol = new Color(50,150,200);
		emptyCol = new Color(230,230,240);
		sliderCol = new Color(200,200,210);
	}
	
	@Override
	public void update(Vector2D mpos) {
		if (inBounds(mpos.sub(pos))) {
			if (Mouse.isButtonDown(0)) {
				diffx = mpos.getXi() - pos.getXi();
				val = Math.round((max - min) * (float) diffx / width) + min;
				notifyReceivers(new Integer(val));
			}
		}
		
	}

	public void draw() {
		int locx = pos.getXi();
		int locy = pos.getYi();
		
		int sliderpos = width * (val - min) / (max - min);
		
		GLHelper.rectfill(locx, locy + 2, locx + sliderpos, locy + 7, fillCol);
		GLHelper.rectfill(locx + sliderpos, locy + 2, locx + width, locy + 7, emptyCol);
		GLHelper.rectfill(locx + sliderpos - 2, locy, locx + sliderpos + 2, locy + 9, sliderCol);
		
		UIHelper.drawString(locx + width + 10, locy - 5, "" + val);	
	}

	
	public void setFillColor(Color col) { fillCol = col; }
	public void setEmptyColor(Color col) { emptyCol = col; }
	public void setSliderColor(Color col) { sliderCol = col; }
}
