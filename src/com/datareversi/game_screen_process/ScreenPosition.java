package com.datareversi.game_screen_process;

import com.datareversi.Assets;
import com.datareversi.framework.Graphics;
import com.datareversi.framework.Pixmap;

public class ScreenPosition {
	
	public ScreenPosition(int endButtonWidth, int endButtonHeight) {
		super();
		this.endButtonWidth = endButtonWidth;
		this.endButtonHeight = endButtonHeight;
		this.setEndButtonSquare(new int[]{endButtonStartX, endButtonStartY,
				endButtonWidth, endButtonHeight});
	}

	private static final int BoardWidth = 8;
	private static final int BoardHeight = 8;
	private static final int BoardLeftMargin = 12;
	private static final int BoardTopMargin = 16;
	private static final int GridLeftMargin = 4;
	private static final int GridTopMargin = 4;
	private static final int StartDiscX = BoardLeftMargin + GridLeftMargin;
	private static final int StartDiscY = BoardTopMargin + GridTopMargin;
	private static final double GridWidth = 56.75;
	private static final double GridHeight = 56.75;
	private static final int GridStartX[] = {15, 69, 125, 182, 239, 296, 353, 409};
	private static final int GridWidthX[] = {50, 53, 53, 53, 53, 53, 53, 53};
	private static final int GridStartY[] = {19, 73, 129, 186, 243, 300, 357, 413};
	private static final int GridWidthY[] = {50, 53, 53, 53, 53, 53, 53, 53};
	private static final int DisplayTurn1PosX = 0;
	private static final int DisplayTurn2PosX = 100;
	private static final int DisplayTurnPosY = 485;
	private static final int No_BlackDiscY = DisplayTurnPosY + 100;
	private static final int No_WhiteDiscY = No_BlackDiscY + 100;
	private static final int No_DiscX = 0;
	private static final int No_DiscColonX = No_DiscX + 100;
	private static final int NumberWidth = 55;
	private static final int ScreenWidth = 480;
	private static final int ScreenHeight = 800;
	private static final int No_Grid = BoardWidth * BoardHeight;
	private static final int endButtonStartX = 351;
	private static final int endButtonStartY = 730;
	private static final int endCheckDialogStartX = 12;
	private static final int endCheckDialogStartY = 190;
	private static final int endCheckYesRelativeStartX = 32;
	private static final int endCheckNoRelativeStartX = 262;
	private static final int endCheckButtonRelativeStartY = 162;
	private static final int endCheckYesRelativeEndX = 180;
	private static final int endCheckNoRelativeEndX = 410;
	private static final int endCheckButtonRelativeEndY = 251;
	private static final int endCheckYesStartX = endCheckDialogStartX + endCheckYesRelativeStartX;
	private static final int endCheckNoStartX = endCheckDialogStartX + endCheckNoRelativeStartX;
	private static final int endCheckButtonStartY = endCheckDialogStartY + endCheckButtonRelativeStartY;
	private static final int endCheckYesWidth = endCheckYesRelativeEndX - endCheckYesRelativeStartX;
	private static final int endCheckNoWidth = endCheckNoRelativeEndX - endCheckNoRelativeStartX;
	private static final int endCheckButtonHeight = endCheckButtonRelativeEndY -
			endCheckButtonRelativeStartY;
	
	private static final int[] endCheckYesButtonSquare = {endCheckYesStartX, endCheckButtonStartY,
		endCheckYesWidth, endCheckButtonHeight};
	private static final int[] endCheckNoButtonSquare = {endCheckNoStartX, endCheckButtonStartY,
		endCheckNoWidth, endCheckButtonHeight};
	private static final int[] screenSquare = {0, 0, ScreenWidth, ScreenHeight};
	
	private int endButtonWidth, endButtonHeight;
	
	private int[] endButtonSquare;
	
	public static int getBoardwidth() {
		return BoardWidth;
	}
	public static int getBoardheight() {
		return BoardHeight;
	}
	public static int getBoardleftmargin() {
		return BoardLeftMargin;
	}
	public static int getBoardtopmargin() {
		return BoardTopMargin;
	}
	public static int getGridleftmargin() {
		return GridLeftMargin;
	}
	public static int getGridtopmargin() {
		return GridTopMargin;
	}
	public static int getStartdiscx() {
		return StartDiscX;
	}
	public static int getStartdiscy() {
		return StartDiscY;
	}
	public static double getGridwidth() {
		return GridWidth;
	}
	public static double getGridheight() {
		return GridHeight;
	}
	public static int[] getGridstartx() {
		return GridStartX;
	}
	public static int[] getGridwidthx() {
		return GridWidthX;
	}
	public static int[] getGridstarty() {
		return GridStartY;
	}
	public static int[] getGridwidthy() {
		return GridWidthY;
	}
	public static int getDisplayturn1posx() {
		return DisplayTurn1PosX;
	}
	public static int getDisplayturn2posx() {
		return DisplayTurn2PosX;
	}
	public static int getDisplayturnposy() {
		return DisplayTurnPosY;
	}
	public static int getNoBlackdiscy() {
		return No_BlackDiscY;
	}
	public static int getNoWhitediscy() {
		return No_WhiteDiscY;
	}
	public static int getNoDiscx() {
		return No_DiscX;
	}
	public static int getNoDisccolonx() {
		return No_DiscColonX;
	}
	public static int getNumberwidth() {
		return NumberWidth;
	}
	public static int getScreenwidth() {
		return ScreenWidth;
	}
	public static int getScreenheight() {
		return ScreenHeight;
	}
	public static int getNoGrid() {
		return No_Grid;
	}
	public static int getEndbuttonstartx() {
		return endButtonStartX;
	}
	public static int getEndbuttonstarty() {
		return endButtonStartY;
	}
	public static int getEndcheckdialogstartx() {
		return endCheckDialogStartX;
	}
	public static int getEndcheckdialogstarty() {
		return endCheckDialogStartY;
	}
	public static int getEndcheckyesrelativestartx() {
		return endCheckYesRelativeStartX;
	}
	public static int getEndchecknorelativestartx() {
		return endCheckNoRelativeStartX;
	}
	public static int getEndcheckbuttonrelativestarty() {
		return endCheckButtonRelativeStartY;
	}
	public static int getEndcheckyesrelativeendx() {
		return endCheckYesRelativeEndX;
	}
	public static int getEndchecknorelativeendx() {
		return endCheckNoRelativeEndX;
	}
	public static int getEndcheckbuttonrelativeendy() {
		return endCheckButtonRelativeEndY;
	}
	public static int getEndcheckyesstartx() {
		return endCheckYesStartX;
	}
	public static int getEndchecknostartx() {
		return endCheckNoStartX;
	}
	public static int getEndcheckbuttonstarty() {
		return endCheckButtonStartY;
	}
	public static int getEndcheckyeswidth() {
		return endCheckYesWidth;
	}
	public static int getEndchecknowidth() {
		return endCheckNoWidth;
	}
	public static int getEndcheckbuttonheight() {
		return endCheckButtonHeight;
	}
	
	public void drawBoard(Graphics g, int boardOnDiscs[][]) {
		for(int x = 0; x < ScreenPosition.getBoardwidth(); x++) {
			for(int y = 0; y < ScreenPosition.getBoardheight(); y++) {
				Pixmap disc = (boardOnDiscs[x][y] == GameState.getBlackdisc()) 
						? Assets.blackDiscPixmap
						: (boardOnDiscs[x][y] == GameState.getWhitedisc())
							? Assets.whiteDiscPixmap
							: Assets.noDiscPixmap;
				g.drawPixmap(disc, ScreenPosition.getStartdiscx() 
						+ (int)(ScreenPosition.getGridwidth() * x), ScreenPosition.getStartdiscy() 
						+ (int)(ScreenPosition.getGridheight() * y));
			}
		}
	}
	
	public void displayTurn(Graphics g, int turn) {
		Pixmap tPixmap = (turn == GameState.getBlackturn())
				? Assets.blackPixmap
				: Assets.whitePixmap;
		g.drawPixmap(tPixmap, ScreenPosition.getDisplayturn1posx(), ScreenPosition.getDisplayturnposy());
		
		g.drawPixmap(Assets.turnPixmap, ScreenPosition.getDisplayturn2posx(),
				ScreenPosition.getDisplayturnposy());
	}
	
	public void displayNo_Disc(Graphics g, int blackNo_Disc[], int whiteNo_Disc[]) {
		g.drawPixmap(Assets.blackPixmap, ScreenPosition.getNoDiscx(), ScreenPosition.getNoBlackdiscy());
		g.drawPixmap(Assets.whitePixmap, ScreenPosition.getNoDiscx(), ScreenPosition.getNoWhitediscy());
		g.drawPixmap(Assets.colonPixmap, ScreenPosition.getNoDisccolonx(),
				ScreenPosition.getNoBlackdiscy());
		g.drawPixmap(Assets.colonPixmap, ScreenPosition.getNoDisccolonx(),
				ScreenPosition.getNoWhitediscy());
		g.drawPixmap(Assets.numberPixmap[blackNo_Disc[0]], ScreenPosition.getNoDisccolonx()
				+ ScreenPosition.getNumberwidth(), ScreenPosition.getNoBlackdiscy());
		g.drawPixmap(Assets.numberPixmap[blackNo_Disc[1]], ScreenPosition.getNoDisccolonx()
				+ (ScreenPosition.getNumberwidth() * 2), ScreenPosition.getNoBlackdiscy());
		g.drawPixmap(Assets.numberPixmap[whiteNo_Disc[0]], ScreenPosition.getNoDisccolonx() 
				+ ScreenPosition.getNumberwidth(), ScreenPosition.getNoWhitediscy());
		g.drawPixmap(Assets.numberPixmap[whiteNo_Disc[1]], ScreenPosition.getNoDisccolonx()
				+ (ScreenPosition.getNumberwidth() * 2), ScreenPosition.getNoWhitediscy());
	}
	
	public void displayResult(Graphics g, int no_Disc[]){
		if(no_Disc[GameState.getBlackdisc()] == no_Disc[GameState.getWhitedisc()]) {
			g.drawPixmap(Assets.drawPixmap, ScreenPosition.getDisplayturn2posx(),
					ScreenPosition.getDisplayturnposy());
		} else {
			g.drawPixmap(Assets.winnerPixmap, ScreenPosition.getDisplayturn2posx(),
					ScreenPosition.getDisplayturnposy());
			
			Pixmap tPixmap = (no_Disc[GameState.getBlackdisc()] > no_Disc[GameState.getWhitedisc()])
					? Assets.blackPixmap
					: Assets.whitePixmap;
			g.drawPixmap(tPixmap, ScreenPosition.getDisplayturn1posx(), 
					ScreenPosition.getDisplayturnposy());
		}
	}
	public int getEndButtonWidth() {
		return endButtonWidth;
	}
	public void setEndButtonWidth(int endButtonWidth) {
		this.endButtonWidth = endButtonWidth;
	}
	public int getEndButtonHeight() {
		return endButtonHeight;
	}
	public void setEndButtonHeight(int endButtonHeight) {
		this.endButtonHeight = endButtonHeight;
	}
	public static int[] getEndcheckyesbuttonsquare() {
		return endCheckYesButtonSquare;
	}
	public static int[] getEndchecknobuttonsquare() {
		return endCheckNoButtonSquare;
	}
	public int[] getEndButtonSquare() {
		return endButtonSquare;
	}
	public void setEndButtonSquare(int[] endButtonSquare) {
		this.endButtonSquare = endButtonSquare;
	}
	public static int[] getScreensquare() {
		return screenSquare;
	}
	public int[] getGridSquare(int x, int y) {
		return new int[]{GridStartX[x], GridStartY[x], GridWidthX[x], GridWidthY[y]};
	}

}
