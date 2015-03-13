package main;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

public class MenuItem
{
	private  Sprite sprite;

	/**
	 * Constructor for MenuItem
	 * @param x x-coordinate of the menu item
	 * @param y y-coordinate of the menu item
	 * @param image image of the menu item
	 */
	public MenuItem(int x, int y, Image image)
	{
		Sprite sprite = new Sprite(image);
		sprite.setPosition(x, y);
		this.sprite = sprite;
	}

	/**
	 * Get the sprite of this MenuItem
	 * @returns sprite of MenuItem
	 */
	public Sprite getSprite() {
		return sprite;
	}
}
