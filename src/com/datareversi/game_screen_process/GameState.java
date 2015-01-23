package com.datareversi.game_screen_process;

public class GameState {
	
	private static final String URL = "http://j11017.sangi01.net/data_reversi/post.php";
	//private static final String URL = "http://10.250.0.12/data_reversi/post.php";
	private static final int NoDisc = 0;
	private static final int BlackDisc = 1;
	private static final int WhiteDisc = 2;
	private static final int BlackTurn = 1;
	private static final int WhiteTurn = 2;
	private static final int FirstMove = 0;
	private static final int SecondMove = 1;
	
	private static final int SquareEvaluations[][] = {{30, -12, 0, -1, -1, 0, -12, 30},
		{-12, -15, -3, -3, -3, -3, -15, -12},
		{0, -3, 0, -1, -1, 0, -3, 0},
		{-1, -3, -1, -1, -1, -1, -3, -1},
		{-1, -3, -1, -1, -1, -1, -3, -1},
		{0, -3, 0, -1, -1, 0, -3, 0},
		{-12, -15, -3, -3, -3, -3, -15, -12},
		{30, -12, 0, -1, -1, 0, -12, 30}};

	public static String getUrl() {
		return URL;
	}

	public static int getNodisc() {
		return NoDisc;
	}

	public static int getBlackdisc() {
		return BlackDisc;
	}

	public static int getWhitedisc() {
		return WhiteDisc;
	}

	public static int getBlackturn() {
		return BlackTurn;
	}

	public static int getWhiteturn() {
		return WhiteTurn;
	}

	public static int getFirstmove() {
		return FirstMove;
	}

	public static int getSecondmove() {
		return SecondMove;
	}

	public static int[][] getSquareevaluations() {
		return SquareEvaluations;
	}
}
