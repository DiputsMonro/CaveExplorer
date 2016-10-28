package menus;
import java.util.ArrayList;


public abstract class AMenu {
	String title;
	String[]  items;
	
	AMenu(String title, String[]  menuItems) {
		this.items = menuItems;
		this.title = title;
	}
	
	public String select(int selection) {
		return items[selection];
	}

	public abstract void render();
	public abstract String update();
}
