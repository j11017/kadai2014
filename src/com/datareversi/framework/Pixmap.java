package com.datareversi.framework;

import android.graphics.Bitmap;

import com.datareversi.framework.Graphics.PixmapFormat;


public interface Pixmap {
	public int getWidth();
	public int getHeight();
	public PixmapFormat getFormat();
	public void dispose();
	public Bitmap getBitmap();

}
