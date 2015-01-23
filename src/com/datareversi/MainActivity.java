package com.datareversi;

import com.datareversi.LoadingScreen;
import com.datareversi.framework.Screen;
import com.datareversi.framework.impl.AndroidGame;
public class MainActivity extends AndroidGame{

	@Override
	public Screen getStartScreen(){
		return new LoadingScreen(this);
	}
}
