package com.datareversi.framework;

import android.view.KeyEvent;

public interface Game {
	public Input getInput();

	public FileIO getFileIO();

	public Graphics getGraphics();

	public Audio getAudio();

	public void setScreen(Screen screen);

	public Screen getCurrentScreen();

	public Screen getStartScreen();
	
	public boolean onKeyUp(int keyCode, KeyEvent event);
	
	public void exit();
}
