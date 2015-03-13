package main;

/**
 *	GameModel class provides an interface for game
 *  objects to encapsulate it's game state. 
 */
public interface GameModel {

	/**
	 * Process the game logic on the object model.
	 *
	 * @param keys the state of the keys.
	 * @see javax.microedition.lcdui.GameCanvas
	 *
	 * @param width Width of the drawing area.
	 * @param height Height of the drawing area.
	 */
	public void Ai(int keys, int width, int height);

}
