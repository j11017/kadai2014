package com.datareversi.screen;

import java.util.Date;
import java.util.List;

import android.text.format.DateFormat;

import com.datareversi.Assets;
import com.datareversi.Globals;
import com.datareversi.framework.Game;
import com.datareversi.framework.Graphics;
import com.datareversi.framework.Input.TouchEvent;
import com.datareversi.framework.Screen;

public class ModeSelectScreen extends Screen {
	
	private static final int humanVsCpuStartX = 23;
	private static final int humanVsCpuStartY = 160;
	private static final int humanVsCpuEndX = 437;
	private static final int humanVsCpuEndY = 272;
	private static final int humanVsCpuWidth = humanVsCpuEndX - humanVsCpuStartX;
	private static final int humanVsCpuHeight = humanVsCpuEndY - humanVsCpuStartY;
	private static final int cpuVsHumanStartX = 22;
	private static final int cpuVsHumanStartY = 297;
	private static final int cpuVsHumanEndX = 436;
	private static final int cpuVsHumanEndY = 410;
	private static final int cpuVsHumanWidth = cpuVsHumanEndX - cpuVsHumanStartX;
	private static final int cpuVsHumanHeight = cpuVsHumanEndY - cpuVsHumanStartY;
	private static final int humanVsHumanStartX = 23;
	private static final int humanVsHumanStartY = 436;
	private static final int humanVsHumanEndX = 437;
	private static final int humanVsHumanEndY = 549;
	private static final int humanVsHumanWidth = humanVsHumanEndX - humanVsHumanStartX;
	private static final int humanVsHumanHeight = humanVsHumanEndY - humanVsHumanStartY;
	private static final int cpuVsCpuStartX = 22;
	private static final int cpuVsCpuStartY = 574;
	private static final int cpuVsCpuEndX = 436;
	private static final int cpuVsCpuEndY = 687;
	private static final int cpuVsCpuWidth = cpuVsCpuEndX - cpuVsCpuStartX;
	private static final int cpuVsCpuHeight = cpuVsCpuEndY - cpuVsCpuStartY;
	

	public ModeSelectScreen(Game game) {
		super(game);
		Date date = new Date();
		Globals.startTime = String.valueOf(DateFormat.format(Globals.datePattern, date));
	}

	@Override
	public void update(float deltaTime) {
		List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
		game.getInput().getKeyEvents();
		int len = touchEvents.size();

		for (int i = 0; i < len; i++) {
			TouchEvent event = touchEvents.get(i);
			if (event.type == TouchEvent.TOUCH_UP) {
				if (inBounds(event, humanVsCpuStartX, humanVsCpuStartY,
						humanVsCpuWidth, humanVsCpuHeight)) {
					Globals.mode = String.valueOf(0);
					Globals.isPlayer[Globals.FirstMove] = true;
					Globals.isPlayer[Globals.SecondMove] = false;
					game.setScreen(new GameScreen(game));
					return;
				}
				if (inBounds(event, cpuVsHumanStartX, cpuVsHumanStartY,
						cpuVsHumanWidth, cpuVsHumanHeight)) {
					Globals.mode = String.valueOf(1);
					Globals.isPlayer[Globals.FirstMove] = false;
					Globals.isPlayer[Globals.SecondMove] = true;
					game.setScreen(new GameScreen(game));
					return;
				}
				if (inBounds(event, humanVsHumanStartX, humanVsHumanStartY,
						humanVsHumanWidth, humanVsHumanHeight)) {
					Globals.mode = String.valueOf(2);
					Globals.isPlayer[Globals.FirstMove] = true;
					Globals.isPlayer[Globals.SecondMove] = true;
					game.setScreen(new GameScreen(game));
					return;
				}
				if (inBounds(event, cpuVsCpuStartX, cpuVsCpuStartY,
						cpuVsCpuWidth, cpuVsCpuHeight)) {
					Globals.mode = String.valueOf(3);
					Globals.isPlayer[Globals.FirstMove] = false;
					Globals.isPlayer[Globals.SecondMove] = false;
					game.setScreen(new GameScreen(game));
					return;
				}
			}
		}
		
	}

	@Override
	public void present(float deltaTime) {
		Graphics g = game.getGraphics();
		g.drawPixmap(Assets.modeSelectPixmap, 0, 0);
		
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
