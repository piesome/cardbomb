package main;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;


public class Fruit
{
	private Sprite sprite;
	private Sprite boom, empty, spr, bomb;
	private int x, y, type;
	private boolean combo = false;
	private boolean delete = false;

	/**
	 * Public constructor for Fruit
	 * @param x x-coordinate in grid [0..GRID_WIDTH[
	 * @param y y-coordinate in grid [0..GRID_HEIGHT[
	 * @param image image number to load [0..IMAGE_AMOUNT[
	 */
	public Fruit(int x, int y, int image)
	{
		this.x = x;
		this.y = y;
		this.type = image;
		spr = createSprite(main.getImage(image, false));
		sprite = spr;
		boom = createSprite(main.getImage(main.IMG_EXPLOSION, true));
		empty= createSprite(main.getImage(main.IMG_EMPTY, true));
		bomb = createSprite(main.getImage(main.IMG_BOMB, true));
		sprite.setPosition(x*sprite.getWidth(),y*sprite.getHeight());
	}

	public Fruit(Fruit f) {
		int[] pos = f.getPosition();
		this.x = pos[0];
		this.y = pos[1];
		this.type = f.getType();
		spr = createSprite(main.getImage(type, false));
		sprite = spr;
		boom = createSprite(main.getImage(main.IMG_EXPLOSION, true));
		empty= createSprite(main.getImage(main.IMG_EMPTY, true));
		bomb = createSprite(main.getImage(main.IMG_BOMB, true));
		sprite.setPosition(x*sprite.getWidth(),y*sprite.getHeight());
	}
	
	/* creates a new sprite */
	private Sprite createSprite(Image image)
	{
		Sprite sprite = new Sprite(image, image.getWidth(), image.getHeight());
		return sprite;
	}

	/**
	 * Moves the sprite downwards by one tile
	 */
	public void moveDown()
	{
		y = Math.min(y + 1, main.HEIGHT-1);
		sprite.setPosition(x*sprite.getWidth(),y*sprite.getHeight());
	}
	/**
	 * Sets the sprite to an explosion
	 */
	public void boom() {
		boom.setPosition(sprite.getX(),sprite.getY());
		sprite = boom;
	}
	/**
	 * Sets the sprite to an empty tile
	 */
	public void empty() {
		empty.setPosition(sprite.getX(),sprite.getY());
		sprite = empty;
	}
	/**
	 * Returns the sprite back to normal
	 */
	public void sprite() {
		spr.setPosition(sprite.getX(),sprite.getY());
		sprite = spr;
	}
	/**
	 * Sets the sprite to a bomb
	 */
	public void bomb() {
		bomb.setPosition(sprite.getX(),sprite.getY());
		sprite = bomb;
	}

	/**
	 * Returns the type aka image number of the object
	 * @returns type of object [0..IMAGE_AMOUNT[
	 */
	public int getType() {
		return this.type;
	}

	/**
	 * Returns whether object should be deleted
	 * @returns whether object should be deleted [true, false]
	 */

	public boolean getDelete() {
		return delete;
	}
	/**
	 * Sets whether object should be deleted
	 * @param delete should the object be deleted [true, false]
	 */
	public void setDelete(boolean delete) {
		this.delete = delete;
	}

	/**
	 * Returns the type of the object, and returns -1 for any
	 * additional calls to the function
	 * @returns type of object or -1 if called multiple times [-1..IMAGE_AMOUNT[
	 */
	public int getComboType() {
		if(combo) {
			return -1;
		}
		combo = true;
		return this.type;
	}

	/**
	 * Allows getComboType() to return other than -1
	 */
	public void clearCombo() {
		combo = false;
	}
	
	/**
	 * Returns the Sprite from the Fruit
	 * @returns Sprite from the Fruit
	 */
	public Sprite getSprite() {
		return sprite;
	}
	
	/**
	 * Returns the coordinates of the Fruit
	 * @returns array of coordinates [x, y]
	 */
	public int[] getPosition() {
		int[] res = {x, y};
		return res;
	}
}
