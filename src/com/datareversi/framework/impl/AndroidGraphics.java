package com.datareversi.framework.impl;

import java.io.IOException;
import java.io.InputStream;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;

import com.datareversi.framework.Graphics;
import com.datareversi.framework.Pixmap;

public class AndroidGraphics implements Graphics {
	AssetManager assets;
	Bitmap frameBuffer;
	Canvas canvas;
	Paint paint;
	Rect srcRect = new Rect();
	Rect dstRect = new Rect();

	Matrix m = new Matrix();

	public AndroidGraphics(AssetManager assets, Bitmap frameBuffer) {
		this.assets = assets;
		this.frameBuffer = frameBuffer;
		this.canvas = new Canvas(frameBuffer);
		this.paint = new Paint();
	}

	@Override
	public Pixmap newPixmap(String fileName, PixmapFormat format) {
		Config config = null;
		if (format == PixmapFormat.RGB565)
			config = Config.RGB_565;
		else if (format == PixmapFormat.ARGB4444)
			config = Config.ARGB_4444;
		else
			config = Config.ARGB_8888;

		Options options = new Options();
		options.inPreferredConfig = config;

		InputStream in = null;
		Bitmap bitmap = null;
		try {
			in = assets.open(fileName);
			bitmap = BitmapFactory.decodeStream(in);
			if (bitmap == null)
				throw new RuntimeException("Couldn't load bitmap from asset '" + fileName + "'");
		} catch (IOException e) {
			throw new RuntimeException("Couldn't load bitmap from asset'" + fileName + "'");
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {

				}
			}
		}

		if (bitmap.getConfig() == Config.RGB_565)
			format = PixmapFormat.RGB565;
		else if (bitmap.getConfig() == Config.ARGB_4444)
			format = PixmapFormat.ARGB4444;
		else
			format = PixmapFormat.ARGB8888;

		return new AndroidPixmap(bitmap, format);
	}

	@Override
	public void clear(int color) {
		canvas.drawRGB((color & 0xff0000) >> 16, (color & 0xff00) >> 8, (color & 0xff));
	}

	@Override
	public void drawPixel(int x, int y, int color) {
		paint.setColor(color);
		canvas.drawPoint(x, y, paint);
	}

	@Override
	public void drawLine(int x, int y, int x2, int y2, int color) {
		paint.setColor(color);
		canvas.drawLine(x, y, x2, y2, paint);
	}

	@Override
	public void drawRect(int x, int y, int width, int height, int color) {
		paint.setColor(color);
		paint.setStyle(Style.FILL);
		canvas.drawRect(x, y, x + width - 1, y + height - 1, paint);
	}

	@Override
	public void drawPixmap(Pixmap pixmap, int x, int y, int srcX, int srcY,
			int srcWidth, int srcHeight) {
		srcRect.left = srcX;
		srcRect.top = srcY;
		srcRect.right = srcX + srcWidth - 1;
		srcRect.bottom = srcY + srcHeight - 1;

		dstRect.left = x;
		dstRect.top = y;
		dstRect.right = x + srcWidth - 1;
		dstRect.bottom = y + srcHeight - 1;

		canvas.drawBitmap(((AndroidPixmap) pixmap).bitmap, srcRect, dstRect, null);
	}

	@Override
	public void drawPixmap(Pixmap pixmap, int x, int y) {
		canvas.drawBitmap(((AndroidPixmap)pixmap).bitmap, x, y, null);
	}

	public void drawBitmap(Bitmap bitmap, int x, int y){
		canvas.drawBitmap(bitmap, x, y, null);
	}

	@Override
	public void drawScalePixmap(Pixmap pixmap, int x, int y, float scaleX, float scaleY) {
		if(scaleX != 0 && scaleY != 0){
			m.postScale(scaleX, scaleY);
			Bitmap bitmap = Bitmap.createBitmap(((AndroidPixmap)pixmap).bitmap, 0, 0, pixmap.getWidth(), pixmap.getHeight(), m, true);
			int xPos = (pixmap.getWidth() - bitmap.getWidth()) / 2;
			int yPos = (pixmap.getHeight() - bitmap.getHeight()) / 2;
			canvas.drawBitmap(bitmap, x + xPos, y + yPos, null);
			m.postScale(1 / scaleX, 1 / scaleY);
		}
	}

	@Override
	public void drawRotatePixmap(Pixmap pixmap, int x, int y, float degrees) {
		m.postRotate(degrees, x / 2, y / 2);
		Bitmap bitmap2 = Bitmap.createBitmap(((AndroidPixmap)pixmap).bitmap, 0, 0, pixmap.getWidth(), pixmap.getHeight(), m, true);
		int xPos = (pixmap.getWidth() - bitmap2.getWidth()) / 2;
		int yPos = (pixmap.getHeight() - bitmap2.getHeight()) / 2;
		canvas.drawBitmap(bitmap2, x + xPos, y + yPos, null);
		m.postRotate(-degrees, x / 2, y / 2);
	}

	public void drawRotateBitmap(Bitmap bitmap, int x, int y, float degrees) {
		m.postRotate(degrees, x / 2, y / 2);
		Bitmap bitmap2 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
		int xPos = (bitmap.getWidth() - bitmap2.getWidth()) / 2;
		int yPos = (bitmap.getHeight() - bitmap2.getHeight()) / 2;
		canvas.drawBitmap(bitmap2, x + xPos, y + yPos, null);
		m.postRotate(-degrees, x / 2, y / 2);
	}

	@Override
	public int getWidth() {
		return frameBuffer.getWidth();
	}

	@Override
	public int getHeight() {
		return frameBuffer.getHeight();
	}

}
