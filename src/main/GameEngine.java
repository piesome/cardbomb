package main;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.GameCanvas;

/**
 * GameEngine class provides a basic game engine.
 */
public class GameEngine implements Runnable {

	private GameCanvasProxy canvas;
	private Graphics graphics;

	private GameModel models;
	private Vector 	  views;
	private int		  fps;

	private boolean paused;
	private boolean stopped;

	/**
	 * Creates a Game Engine with the specified
	 * Game model attached. The game runs at the
	 * specified fps.
	 *
	 * @param model game model.
	 * @param fps frames per second.
	 */
	protected GameEngine(GameModel model, int fps) {
		this.canvas = new GameCanvasProxy();
		this.views  = new Vector();

		this.graphics = this.canvas.getGraphics();
		this.models   = model;
		this.stopped	  = true;
		this.fps	  = fps;
	}

	/**
	 * Gets the drawing area.
	 *
	 * @return drawing area.
	 */
	public Displayable getDrawingArea() {
		return this.canvas;
	}

	/**
	 * Starts the Game.
	 */
	public void start() {
		if (stopped) {
			Thread t = new Thread(this);
			t.start();
		}
	}

	/**
	 * Stops the Game.
	 */
	public void stop() {
		if (!stopped) {
			this.stopped = true;
		}
	}

	/**
	 * Resumes the Game.
	 */
	public void resume() {
		synchronized (this) {
			if (paused) {
				this.paused = false;
			}
			this.notifyAll();
		}
	}

	/**
	 * Pauses the Game.
	 */
	public void pause() {
		synchronized (this) {
			if (!paused) {
				this.paused = true;
			}
		}
	}

	/**
	 * Attaches the specified view to the
	 * engine.
	 *
	 * @param view some view.
	 */
	public void attach(GameView view) {
		if (!this.views.contains(view)) {
			this.views.addElement(view);
		}
	}

	/**
	 * Detaches the specified view from the
	 * engine.
	 *
	 * @param view some view.
	 */
	public void detach(GameView view) {
		this.views.removeElement(view);
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.game.GameCanvas#paint(javax.microedition.lcdui.Graphics)
	 */
	public void paint(Graphics g) {

		int keys = this.canvas.getKeyStates();
		this.models.Ai(keys, this.canvas.getWidth(), this.canvas.getHeight());

		Enumeration enumeration = this.views.elements();
		while (enumeration.hasMoreElements()) {
			GameView view = (GameView) enumeration.nextElement();
			view.render(g);
		}
	}

	/**
	 * Game Loop
	 */
	public void run() {
		this.stopped = false;
		while (!stopped) {

			try {
				synchronized (this) {
					if (paused) {
						this.wait();
					}
				}

				long interval = System.currentTimeMillis();
				this.paint(this.graphics);
				this.canvas.flushGraphics();
				interval = System.currentTimeMillis() - interval;
				interval = ((1000 / fps) - interval);
				Thread.sleep(interval > 0 ? interval : 0x00);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.stopped = true;
	}

	private class GameCanvasProxy extends GameCanvas {

		protected GameCanvasProxy() {
			super(true);
		}

		/* (non-Javadoc)
		 * @see javax.microedition.lcdui.game.GameCanvas#getGraphics()
		 */
		public Graphics getGraphics() {
			return super.getGraphics();
		}
	}

}
