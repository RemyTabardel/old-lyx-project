package com.rta.framework.graphics;

import java.io.IOException;
import java.io.InputStream;

import com.rta.framework.physics.BoundingAABB;
import com.rta.framework.physics.BoundingCircle;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;

public class Graphics
{
	AssetManager	assets;
	Bitmap			frameBuffer;
	Canvas			canvas;
	Paint			paint;
	Rect			srcRect	= new Rect();
	Rect			dstRect	= new Rect();

	public static enum ImageFormat
	{
		ARGB8888, ARGB4444, RGB565
	}

	public Graphics(AssetManager assets, Bitmap frameBuffer)
	{
		this.assets = assets;
		this.frameBuffer = frameBuffer;
		this.canvas = new Canvas(frameBuffer);
		this.paint = new Paint();
	}

	public boolean checkAsset(String fileName)
	{
		InputStream in = null;
		boolean value = true;
		
		try
		{
			in = assets.open(fileName);
		}
		catch (IOException e)
		{
			value = false;
		}
		finally
		{
			if(in != null)
			{
				try
				{
					in.close();
				}
				catch (IOException e)
				{
					
				}
			}
		}
		
		return value;
	}
	
	public Image newImage(String fileName, ImageFormat format)
	{
		Config config = null;
		if (format == ImageFormat.RGB565)
			config = Config.RGB_565;
		else if (format == ImageFormat.ARGB4444)
			config = Config.ARGB_4444;
		else
			config = Config.ARGB_8888;

		Options options = new Options();
		options.inPreferredConfig = config;

		InputStream in = null;
		Bitmap bitmap = null;
		try
		{
			in = assets.open(fileName);
			bitmap = BitmapFactory.decodeStream(in, null, options);
			if (bitmap == null)
				throw new RuntimeException("Couldn't load bitmap from asset '" + fileName + "'");
		}
		catch (IOException e)
		{
			//throw new RuntimeException("Couldn't load bitmap from asset '" + fileName + "'");
			
			return null;
		}
		finally
		{
			if (in != null)
			{
				try
				{
					in.close();
				}
				catch (IOException e)
				{
				}
			}
		}

		if (bitmap.getConfig() == Config.RGB_565)
			format = ImageFormat.RGB565;
		else if (bitmap.getConfig() == Config.ARGB_4444)
			format = ImageFormat.ARGB4444;
		else
			format = ImageFormat.ARGB8888;

		return new Image(bitmap, format);
	}

	public void clearScreen(int color)
	{
		canvas.drawRGB((color & 0xff0000) >> 16, (color & 0xff00) >> 8, (color & 0xff));
	}

	public void drawLine(int x, int y, int x2, int y2, int color)
	{
		paint.setColor(color);
		canvas.drawLine(x, y, x2, y2, paint);
	}

	public void drawBoundingShape(BoundingAABB boundingAABB, int color)
	{
		drawRect((int) boundingAABB.getPointTopLeft().x, (int) boundingAABB.getPointTopLeft().y, boundingAABB.getWidth(), boundingAABB.getHeight(), color);
	}

	public void drawBoundingShape(BoundingCircle boundingCircle, int color)
	{
		drawCircle((int)boundingCircle.getPosition().x, (int)boundingCircle.getPosition().y, boundingCircle.getRadius(), color);
	}

	public void drawRect(int x, int y, int width, int height, int color)
	{
		paint.setColor(color);
		paint.setStyle(Style.FILL);
		canvas.drawRect(x, y, x + width - 1, y + height - 1, paint);
	}

	public void drawCircle(int x, int y, int radius, int color)
	{
		paint.setColor(color);
		paint.setStyle(Style.FILL);

		canvas.drawCircle(x, y, radius, paint);
	}

	public void drawARGB(int a, int r, int g, int b)
	{
		paint.setStyle(Style.FILL);
		canvas.drawARGB(a, r, g, b);
	}

	public void drawString(String text, int x, int y, Paint paint)
	{
		canvas.drawText(text, x, y, paint);

	}

	public void drawImage(Image Image, int x, int y, int srcX, int srcY, int srcWidth, int srcHeight)
	{
		srcRect.left = srcX;
		srcRect.top = srcY;
		srcRect.right = srcX + srcWidth;
		srcRect.bottom = srcY + srcHeight;

		dstRect.left = x;
		dstRect.top = y;
		dstRect.right = x + srcWidth;
		dstRect.bottom = y + srcHeight;

		canvas.drawBitmap(((Image) Image).bitmap, srcRect, dstRect, null);
	}

	public void drawImage(Image Image, int x, int y)
	{
		canvas.drawBitmap(((Image) Image).bitmap, x, y, null);
	}

	public void drawScaledImage(Image Image, int x, int y, int width, int height, int srcX, int srcY, int srcWidth, int srcHeight)
	{

		srcRect.left = srcX;
		srcRect.top = srcY;
		srcRect.right = srcX + srcWidth;
		srcRect.bottom = srcY + srcHeight;

		dstRect.left = x;
		dstRect.top = y;
		dstRect.right = x + width;
		dstRect.bottom = y + height;

		canvas.drawBitmap(((Image) Image).bitmap, srcRect, dstRect, null);

	}

	public int getWidth()
	{
		return frameBuffer.getWidth();
	}

	public int getHeight()
	{
		return frameBuffer.getHeight();
	}
}
