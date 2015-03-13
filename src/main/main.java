package main;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;
import java.io.InputStream;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import java.util.Random;
import javax.microedition.media.*;
import com.nokia.mid.ui.DirectUtils;

public class main extends MIDlet implements CommandListener {
	public static Image[]		images;			//array of objects' images
	public static Image[]		miscImages;		//array of misc images
	public static MenuItem[]	menu;			//main menu items
	public static Fruit[]		fruits;			//array of objects
	public static int[]			score = {0, 0},	//both players' score
								bombs = {5, 5};	//both players' bombs
	public static long[] 		time = {0, 0};	//both players' time
	public static int			turn = 0;		//whose turn it is
	public static final int		WIDTH = 6,		//width of playing field
								HEIGHT = 6,		//height of playing field
								IMAGES = 6,		//amount of different objects
								SOUNDS = 3,		//am. of blowing fruit sounds
								MISCIMG = 4,	//amount of misc. images
								MISCSND = 1,	//amount of misc. sounds
								SND_BOOM = 3,	//index of explosion sound
								IMG_EXPLOSION=0,//index of explosion image
								IMG_BOMB=1,		//index of bomb image
								IMG_EMPTY=2,	//index of empty image
								IMG_INFO=3;		//index of info image
	private static boolean		booming=false;	//is blow() being called
	private static boolean		generating=true;//are we generating a grid
	private static long			curtime;		//current time
	public static Player[] 		sounds;			//array of sounds
	public static Player[]		miscSounds;		//array of misc. sounds
	public static String 		status;			//current status of app
	public static Sprite		infoscreen; 	//info screen sprite
	private static Random		random;			//random generator
	private Touch				t;				//touch event handler
	private static boolean		aiState = true;	//playing against AI
	private static Fruit[] 		tmpfruits;		//tmp fruit array for ai
	private final String[]		imgPaths		//array of images' paths
						= {
							"/tile_apple.png",
							"/tile_banana.png",
							"/tile_grapes.png",
							"/tile_orange.png",
							"/tile_pear.png",
							"/tile_watermelon.png"
						};
	private final String[]		miscIPaths		//array of misc. images' paths
						= {
							"/explosion.png",
							"/bomb.png",
							"/empty.png",
							"/info.png"
						};
	private final String[]		sndPaths		//array of sounds' paths
						= {
							"/plim_02.wav",
							"/plim_03.wav",
							"/plim_04.wav",
							"/explosion_01.wav"
						};
	/* random stuff */
	private GameEngine			engine;
	private Display				display;
	private MyView				view;
	private Command				exit;

	/**
	 * Main function
	 */
	public main() {
		status = 		"menu";
		images = 		new Image[IMAGES];
		miscImages =	new Image[MISCIMG];
		sounds = 		new Player[SOUNDS + 1];
		random = 		new Random(System.currentTimeMillis());
		tmpfruits = new Fruit[WIDTH*HEIGHT];
		t = 			new Touch(false);
		menu = 			createMenu();
		for(int i = 0; i < IMAGES; i++) {
			images[i] = loadImage(imgPaths[i]);
		}
		for(int i = 0; i < MISCIMG; i++) {
			miscImages[i] = loadImage(miscIPaths[i]);
		}
		for(int i = 0; i < SOUNDS + 1; i++) {
			sounds[i] = loadSound(sndPaths[i]);
		}
		sounds[SND_BOOM] = loadSound(sndPaths[SND_BOOM]);
		infoscreen = 	new Sprite(miscImages[IMG_INFO], 
				miscImages[IMG_INFO].getWidth(), miscImages[IMG_INFO].getHeight());
		fruits = 		createFruits(WIDTH, HEIGHT);
		
		this.display = 				Display.getDisplay(this);
		MyModel model = 			new MyModel(0,0,234,320);
		this.view = 				new MyView(model, 505050);
		this.engine = 				new GameEngine(model, 10);
		Displayable drawingArea = 	this.engine.getDrawingArea();
		this.exit = 				new Command("Exit", Command.EXIT, 1);
		drawingArea.setCommandListener(this);
		this.engine.attach(view);
		drawingArea.addCommand(this.exit);
		booming = true;
		timer();
		blow(0, 0);
		booming = false;
		generating = false;
		score[0] = 0;
		score[1] = 0;
		time[0] = 0;
		time[1] = 0;
		bombs[0] = 5;
		bombs[1] = 5;
	}

	/**
	 * Returns the specified image
	 * @param image index of image
	 * @param misc return from misc. images
	 */
	public static Image getImage(int image, boolean misc) {
		if(misc) {
			return miscImages[image];
		}
		else {
			return images[image];
		}
	}

	//timer updater
	private void timer() {
		if(!booming && status == "game") {
			main.time[turn] += System.currentTimeMillis() - curtime;
		}
		curtime = System.currentTimeMillis();
	}

	protected void startApp() throws MIDletStateChangeException {
		this.display.setCurrent(this.engine.getDrawingArea());
		this.engine.start();
	}

	public void commandAction(Command c, Displayable d) {
		if (c == this.exit) {
			this.notifyDestroyed();
		}
	}

	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {}

	protected void pauseApp() {}

	private class MyView implements GameView {

		private MyModel model;
		private int 	color;

		public MyView(MyModel model, int color) {
			this.model = model;
			this.color = color;
		}

		public void render(Graphics g) {
			g.drawImage(loadImage("/bg.png"), 0, 0, 0);
			g.setColor(this.color);
			timer();
			if(status == "game") {
				if(generating) {
					for(int i = 0; i < WIDTH*HEIGHT; i++) {
						if(fruits[i] == null) {
							return;
						}
						fruits[i].getSprite().paint(g);
					}
					return;
				}
				else {
					for(int i = 0; i < fruits.length; i++) {
						fruits[i].getSprite().paint(g);
					}
				}
				g.setColor(0x808080);
				g.setFont(DirectUtils.getFont(Font.FACE_SYSTEM,
								Font.STYLE_PLAIN,Font.SIZE_MEDIUM));

				g.drawImage(loadImage("/status.png"), 0, 228, 0);
				g.drawString("" + bombs[0], 	34, 	228+15, 0);
				g.drawString("" + bombs[1], 	134,	228+15, 0);

				g.drawString("" + time[0]/1000,	34,		228+35, 0);
				g.drawString("" + time[1]/1000,	134,	228+35, 0);

				g.drawString("" + score[0], 	34, 	228+55, 0);
				g.drawString("" + score[1], 	134,	228+55, 0);

			}
			if(status == "menu") {
				int i = 0;
				for(i = 0; i < menu.length; i++) {
					menu[i].getSprite().paint(g);
				}
			}
			if(status == "pause") {
				g.setColor(0);
				g.fillRect(0, 0, 240, 430);
				g.setColor(0xFFFFFF);
				g.setFont(Font.getFont(Font.FACE_SYSTEM,
								Font.STYLE_PLAIN, Font.SIZE_LARGE));
				g.drawString("Next player " + + (turn+1),	50, 30, 0);
				g.drawString("Player 1 score: " + score[0],	50, 80, 0);
				g.drawString("Player 2 score: " + score[1],	50, 130, 0);
				g.drawString("Touch the screen",			50, 200, 0);
				g.drawString("to continue...", 				50, 216, 0);
			}
			if(status == "gameover") {
				g.setColor(0);
				g.fillRect(0, 0, 240, 430);
				g.setColor(0xFFFFFF);
				g.setFont(Font.getFont(Font.FACE_SYSTEM,
								Font.STYLE_PLAIN, Font.SIZE_LARGE));;
				g.drawString("Player 1 score: " + score[0],	50, 30, 0);
				g.drawString("Player 2 score: " + score[1],	50, 80, 0);
				if(score[0] != score[1]) {
					g.drawString("Player " + (score[0] > score[1] ? 1: 2) +
								" wins!",					50, 130, 0);
				}
				else {
    				g.drawString("Draw!", 					0, 130, 0);
    			}
				g.drawString("Touch the screen", 			50, 200, 0);
				g.drawString(" to continue...", 			50, 216, 0);
			}
			if(status == "info") {
				infoscreen.paint(g);
			}
		}
	}

	private class MyModel implements GameModel {
		int x,y,w,h;

		public MyModel(int x, int y, int w, int h) {
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
		}

		public void Ai(int keys, int width, int height)
		{

		}
	}

	//loads an image from a path
	private Image loadImage(String path) {
		Image image;
		try {
			InputStream in = getClass().getResourceAsStream(path);
			image = Image.createImage(in);
		}
		catch (Exception e) {
			throw new RuntimeException("ImageLoader failed to load image:" + path + " " + e.getMessage());
		}
		return image;
	}

	//loads a sound from a path
	private static Player loadSound(String path)
	{
		InputStream is;
		Player p;
		try {
		   	is = main.class.getResourceAsStream(path);
		   	p = Manager.createPlayer(is, "audio/X-wav");
		   	p.prefetch();
		}
		catch (Exception e) {
			//throw new RuntimeException("loadSound failed to load sound:" + path + " " + e.getMessage());
			return null;
		}
		return p;
	}

	//plays a sound
	private static void playSound(int i) {
		Player p = sounds[i];
		if(p != null) {
			try {
				p.stop();
				p.setMediaTime(0);
				p.prefetch();
				p.realize();
				p.start();
			} catch (MediaException e) {
				e.printStackTrace();
			}
		}
	}

	//initiates an array of objects
	private Fruit[] createFruits(int width, int height) {
		Fruit[] fruits = new Fruit[width*height];
		int x = 0;
		int y = 0;
		for(int i = 0; i < width*height; i++)
		{
			int state = random.nextInt(IMAGES);
			fruits[i] = new Fruit(x, y, state);
			x++;
			if(x >= width)
			{
				y++;
				x = 0;
			}
		}
		return fruits;
	}

	//sleep for i milliseconds
	public static void sleep(int i) {
		try {
			Thread.sleep(i);
		} catch(Exception e) {}
	}

	/**
	 * Create an explosion at (x, y)
	 * @param x x-coordinate of explosion [0..WIDTH[
	 * @param y y-coordinate of explosion [0..HEIGHT[
	 */
	public static boolean blow(int x, int y) {
		booming = true;
		int combo = 0;
		fruits[x + y*WIDTH].bomb();
		if(!generating) {
			sleep(1000);
		}
		playSound(SND_BOOM);

		if(y>0)
			fruits[x + (y-1)*WIDTH].boom();
		if(x>0)
			fruits[x-1 + y*WIDTH].boom();
		fruits[x + y*WIDTH].boom();
		if(x<WIDTH-1)
			fruits[x+1 + y*WIDTH].boom();
		if(y<HEIGHT-1)
			fruits[x + (y+1)*WIDTH].boom();
		sleep(1000);
		if(y>0)
			drop(x,y-1);
		if(x>0)
			drop(x-1,y);
		drop(x,y);
		if(x<WIDTH-1)
			drop(x+1,y);
		if(y<HEIGHT-1)
			drop(x,y+1);
		boolean cont = true;
		while(cont) {
			combo++;
			cont = false;
			for(int i = 0; i < WIDTH; i++) {
				for(int h = 0; h < HEIGHT; h++) {
					if(checkCombo(i, h)) {
						if(!cont && !generating) {
							sleep(1000);
						}
						cont = true;
						int tmp = blowCombo(i, h,
								fruits[i + h*WIDTH].getType());
						if(!generating) {
							score[turn] += tmp * combo;
						}
					}
				}
			}
			if(!cont) continue;
			if(!generating) {
				sleep(1000);
			}
			for(int a = 0; a < WIDTH; a++) {
				for(int b = 0; b < WIDTH; b++) {
					if(fruits[a + b*WIDTH].getDelete()) {
						drop(a, b);
					}
				}
			}
		}
		if(!generating) {
			if(turn == 0) turn = 1;
			else turn = 0;
		}
		booming = false;
		return combo>1;
	}

	//converts series of objects to explosions
	private static int blowCombo(int x, int y, int c) {
		int val = 0;
		try {
			if(x == WIDTH || x == -1) return 0;
			int tmp = fruits[x + y*WIDTH].getComboType();
			if(c != tmp) {
				return 0;
			}
			if(fruits[x + y*WIDTH].getDelete()) return 0;
			fruits[x + y*WIDTH].setDelete(true);
			val += 1;
			if(!generating) {
				fruits[x + y*WIDTH].boom();
				playSound(random.nextInt(SOUNDS));
				sleep(50);
			}
		}

		catch(ArrayIndexOutOfBoundsException e) {
			return 0;
		}

		val += blowCombo(x-1, y, c);
		val += blowCombo(x+1, y, c);
		val += blowCombo(x, y-1, c);
		val += blowCombo(x, y+1, c);
		return val;
	}

	//drops objects downwards
	private static void drop(int x, int y) {
			for(int i = y-1; i >= 0; i--) {
				fruits[x+i*WIDTH].moveDown();
				fruits[x+i*WIDTH].empty();
				if(!generating) {
					sleep(100);
				}
				fruits[x+(i+1)*WIDTH] = fruits[x+i*WIDTH];
				fruits[x+(i+1)*WIDTH].sprite();
			}
			fruits[x] = new Fruit(x,0, random.nextInt(IMAGES));
	}

	//check for an available combo at (x, y)
	private static boolean checkCombo(int x, int y) {
		int type = fruits[x + y*WIDTH].getType();
		boolean val = false;
		int asd = countCombo(x, y, type);
		if(asd >= 3)
			val = true;
		for(int i = 0; i < fruits.length; i++) {
			fruits[i].clearCombo();
		}
		return val;
	}

	//recursively (flood fill) count amount of adjacent objects
	private static int countCombo(int x, int y, int c) {
		try {
			if(x == WIDTH || x == -1) return 0;
			int asd = fruits[x + y*WIDTH].getComboType();
			if(c != asd) {
				return 0;
			}
		}

		catch(Exception e) {
			return 0;
		}

		int val = 1;
		val += countCombo(x-1, y, c);
		val += countCombo(x+1, y, c);
		val += countCombo(x, y-1, c);
		val += countCombo(x, y+1, c);

		return val;
	}

	//initialize menu
	private MenuItem[] createMenu() {
		int x, y;
		x = 75;
		y = 96;
		MenuItem[] menu = new MenuItem[2];
		Image start = loadImage("/button_start.png");
		Image info = loadImage("/button_info.png");
		menu[0] = new MenuItem(x,y,start);
		menu[1] = new MenuItem(x,y+info.getHeight(),info);
		return menu;
	}

	/**
	 * Change program's status to pause
	 */
	public static void pause() {
		main.status = "pause";
	}
	/**
	 * Change program's status to the main game
	 */
	public static void cont() {
		main.status = "game";
	}
	/**
	 * Change program's status to game over screen
	 */
	public static void end() {
		main.status = "gameover";
	}
	
	/**
	 * Does the next AI move
	 */
	public static void nextAI() {
		for(int i = 0; i < WIDTH*HEIGHT; i++) {
			tmpfruits[i] = new Fruit(fruits[i]);
		}
		generating = true;
		do {
			int i = random.nextInt(WIDTH);
			int h = random.nextInt(HEIGHT);
			boolean val = blow(i, h);
			for(int j = 0; j < WIDTH*HEIGHT; j++) {
				fruits[i] = new Fruit(tmpfruits[i]);
			}
			if(val) {
				generating = false;
				blow(i, h);
				return;
			}
		} while(true);
	}
	
	/**
	 * Returns true if playing against AI
	 * @returns true if playing against AI
	 */
	public static boolean getAI() {
		return aiState;
	}
}
