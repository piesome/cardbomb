package main;

import com.nokia.mid.ui.multipointtouch.*;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.lcdui.game.Sprite;

public class Touch extends GameCanvas implements MultipointTouchListener {
	protected Touch(boolean suppressKeyEvents) {
		super(suppressKeyEvents);
		touch();
	}

	MultipointTouch multipointTouch;

	public void touch () {
		multipointTouch = MultipointTouch.getInstance();
		multipointTouch.addMultipointTouchListener(this);
	}
	//is (x, y) touching Sprite a
	private boolean isInside(Sprite a, int x, int y) {
		return(a.getX() + a.getWidth() > x &&
				a.getX() < x &&
				a.getY() + a.getHeight() > y &&
				a.getY() < y);
	}

	/*
	The MultipointTouchListener interface defines the pointersChanged method,
	which is called every time the platform triggers a multipoint touch event.
	 */
	public void pointersChanged(int[] pointerIds) {
		int pointerId;
		int x;
		int y;
		int state;
		for(int i=0; i < pointerIds.length; i++) {
			pointerId = pointerIds[i];
			state = MultipointTouch.getState(pointerId);
			x = MultipointTouch.getX(pointerId);
			y = MultipointTouch.getY(pointerId);
			switch(state) {
			case MultipointTouch.POINTER_PRESSED:
				if(main.status == "game") {
					if(main.getAI() && main.turn == 1) {
						main.nextAI();
						return;
					}
					for(int h = 0; h < main.HEIGHT; h++) {
						for(int j = 0; j < main.WIDTH; j++) {
							Fruit a = main.fruits[j + h * main.WIDTH];
							if(isInside(a.getSprite(), x, y)) {
								main.bombs[main.turn]--;
								main.blow(j, h);
								main.sleep(500);
								if(main.turn == 0 && main.bombs[main.turn] == 0) {
									main.end();
									return;
								}
								main.pause();
								return;
							}
    					}
					}
					break;
				}
				if(main.status == "menu") {
					MenuItem a = main.menu[0];
					if(isInside(a.getSprite(), x, y)) {
						main.status = "pause";
					}
					a = main.menu[1];
					if(isInside(a.getSprite(), x, y)) {
						main.status = "info";
					}
					break;
				}
				if(main.status == "pause") {
					main.cont();
					break;
				}
				if(main.status == "info") {
					main.status = "menu";
					break;
				}
				if(main.status == "gameover") {
					main.status = "menu";
					break;
				}
				break;
			}
		}
	}
}

