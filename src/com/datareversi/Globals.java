package com.datareversi;

import java.util.Date;

import android.text.format.DateFormat;

public class Globals {
	public static final int FirstMove = 0;
	public static final int SecondMove = 1;
	
	public static boolean isPlayer[] = new boolean[2];
	
	public static String mode;
	public static String startTime, endTime;
	public static String turn[] = new String[60];
	public static String position[] = new String[60];
	public static String evaluationValue[] = new String[60];
	public static String moveTime[] = new String[60];
	public static String datePattern = "yyyy-MM-dd kk:mm:ss";
	
	private Globals() {
	}
	
	public static void initGrobals() {
		isPlayer[FirstMove] = false; // 先手は人間か
		isPlayer[SecondMove] = true; // 後手は人間か
		mode = String.valueOf(1);
		Date date = new Date();
		startTime = String.valueOf(DateFormat.format(datePattern, date)); 
		System.out.println(startTime);
	}

}
