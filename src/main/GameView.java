package main;
import javax.microedition.lcdui.Graphics;

/**
 * GameView class provides an interface
 * for the game objects views.
 */
public interface GameView {

	/**
	 * Renders the object's view.
	 *
	 * @param g Graphics instance.
	 */
	public void render(Graphics g);

}
