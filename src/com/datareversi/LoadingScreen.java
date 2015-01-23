package com.datareversi;

import com.datareversi.framework.Game;
import com.datareversi.framework.Graphics;
import com.datareversi.framework.Pixmap;
import com.datareversi.framework.Screen;
import com.datareversi.framework.Graphics.PixmapFormat;
import com.datareversi.screen.TitleScreen;

public class LoadingScreen extends Screen {

	public LoadingScreen(Game game) {
		super(game);
	}

	@Override
	public void update(float deltaTime) {
	
		try {
			fileLoad();
		} catch (OutOfMemoryError e) {
			java.lang.System.gc();
			
			try {
				fileLoad();
			} catch (OutOfMemoryError e2) {
				System.err.println("insufficient memory was available to complete the operation.");
				fileDispose();
				game.exit();
				return;
			}
		}

		game.setScreen(new TitleScreen(game));
		return;
	}

	@Override
	public void present(float deltaTime) {}
	
	@Override
	public void pause() {}

	@Override
	public void resume() {}

	@Override
	public void dispose() {}

	private void fileLoad() {
		Graphics g = game.getGraphics();
		Assets.titlePixmap = g.newPixmap("Title.png", PixmapFormat.RGB565);
		Assets.modeSelectPixmap = g.newPixmap("modeSelect.png", PixmapFormat.RGB565);
		Assets.boardPixmap = g.newPixmap("board.png", PixmapFormat.RGB565);
		Assets.noDiscPixmap = g.newPixmap("noDisc.png", PixmapFormat.RGB565);
		Assets.blackDiscPixmap = g.newPixmap("blackDisc.png", PixmapFormat.RGB565);
		Assets.whiteDiscPixmap = g.newPixmap("whiteDisc.png", PixmapFormat.RGB565);
		Assets.whitePixmap = g.newPixmap("white.png", PixmapFormat.RGB565);
		Assets.blackPixmap = g.newPixmap("black.png", PixmapFormat.RGB565);
		Assets.turnPixmap = g.newPixmap("turn.png", PixmapFormat.RGB565);
		Assets.colonPixmap = g.newPixmap("colon.png", PixmapFormat.RGB565);
		Assets.winnerPixmap = g.newPixmap("winner.png", PixmapFormat.RGB565);
		Assets.drawPixmap = g.newPixmap("draw.png", PixmapFormat.RGB565);
		Assets.endButtonPixmap = g.newPixmap("endButton.png", PixmapFormat.RGB565);
		Assets.endCheckDialogPixmap = g.newPixmap("endCheckDialog.png", PixmapFormat.RGB565);
		
		Assets.numberPixmap = new Pixmap[10];
		for(int i = 0; i < 10; i++) {
			String fileName = "" + i + ".png";
			Assets.numberPixmap[i] = g.newPixmap(fileName, PixmapFormat.RGB565);
		}

		//Assets.pushButton = game.getAudio().newSound("pushButton.wav");

		/*Assets.Prologue_BGM = game.getAudio().newMusic("Prologue_BGM.mp3");
		Assets.Prologue_BGM.setLooping(true);
		Assets.Prologue_BGM.setVolume(0.5f);
		Assets.Prologue_BGM.seekToStart();*/
	}

	private void fileDispose() {

		Assets.titlePixmap.dispose();

		//Assets.CuckooClock.dispose();
	}

}
