package com.datareversi.screen;

import java.util.List;

import com.datareversi.Assets;
import com.datareversi.framework.Game;
import com.datareversi.framework.Graphics;
import com.datareversi.framework.Input.TouchEvent;
import com.datareversi.framework.Screen;

public class TitleScreen extends Screen {
	
	private static final int ScreenWidth = 480;
	private static final int ScreenHeight = 800;

	public TitleScreen(Game game) {
		super(game);
	}

	@Override
	public void update(float deltaTime) {
		List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
		game.getInput().getKeyEvents();
		int len = touchEvents.size();

		for (int i = 0; i < len; i++) {
			TouchEvent event = touchEvents.get(i);
			if (event.type == TouchEvent.TOUCH_UP)
				if (inBounds(event, 0, 0, ScreenWidth, ScreenHeight)) {
					game.setScreen(new ModeSelectScreen(game));
					return;
				}
		}
		
	}

	@Override
	public void present(float deltaTime) {
		Graphics g = game.getGraphics();
		g.drawPixmap(Assets.titlePixmap, 0, 0);
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {}

	@Override
	public void dispose() {}
	
	private boolean inBounds(TouchEvent event, int x, int y, int width, int height) {
		return (event.x > x && event.x < x + width - 1 && event.y > y && event.y < y + height - 1);
	}

}
