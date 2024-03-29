package com.rta.framework.scene;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

import com.rta.framework.audio.Audio;
import com.rta.framework.graphics.FastRenderView;
import com.rta.framework.graphics.Graphics;
import com.rta.framework.input.Controller;
import com.rta.framework.input.Events;
import com.rta.framework.input.Input;
import com.rta.framework.input.Input.TouchEvent;
import com.rta.framework.system.FileIO;

public abstract class Game extends Activity implements Screen
{
	public static final int	SCREEN_WIDTH	= 1280;
	public static final int	SCREEN_HEIGHT	= 800;

	private FastUpdateLogic	updateLogic;
	private FastRenderView	renderView;
	private Graphics		graphics;
	private Audio			audio;
	private Input			input;
	private FileIO			fileIO;
	private WakeLock		wakeLock;
	private Controller		controller;
	private FPS				fpsLogic;
	private FPS				fpsRender;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		boolean isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
		int frameBufferWidth = isPortrait ? SCREEN_HEIGHT : SCREEN_WIDTH;
		int frameBufferHeight = isPortrait ? SCREEN_WIDTH : SCREEN_HEIGHT;
		Bitmap frameBuffer = Bitmap.createBitmap(frameBufferWidth, frameBufferHeight, Config.RGB_565);

		// ////////////////////////////////////////////////////////////////////
		// float scaleX = (float) frameBufferWidth / getWindowManager().getDefaultDisplay().getWidth();
		// float scaleY = (float) frameBufferHeight / getWindowManager().getDefaultDisplay().getHeight();

		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		float scaleX = (float) frameBufferWidth / metrics.widthPixels;
		float scaleY = (float) frameBufferHeight / metrics.heightPixels;
		// /////////////////////////////////////////////////////////////////////

		updateLogic = new FastUpdateLogic(this);
		renderView = new FastRenderView(this, frameBuffer);
		graphics = new Graphics(getAssets(), frameBuffer);
		fileIO = new FileIO(this);
		audio = new Audio(this);
		input = new Input(this, renderView, scaleX, scaleY);
		controller = new Controller();
		fpsLogic = new FPS(graphics, "FPS (Thread logic)", 40);
		fpsRender = new FPS(graphics, "FPS (Thread render)", 80);
		
		setContentView(renderView);

		PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);

		wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "MyGame");
		// wakeLock = powerManager.newWakeLock(LayoutParams.FLAG_KEEP_SCREEN_ON, "MyGame");//MODIF
	}

	@Override
	public void update(float deltaTime)
	{
		List<TouchEvent> touchEvents = input.getTouchEvents();
		Events events = controller.getEvents(touchEvents);

		fpsLogic.update();
		
		update(deltaTime, events);
	}

	@Override
	public void paint(float deltaTime)
	{
		graphics.clearScreen(Color.BLACK);
		
		fpsRender.update();
		fpsRender.paint(graphics);
		fpsLogic.paint(graphics);
		
		controller.paint(getGraphics());
		
		paint();
	}

	public abstract void update(float deltaTime, Events events);

	public abstract void paint();

	@Override
	public void onResume()
	{
		super.onResume();
		wakeLock.acquire();
		this.resume();
		updateLogic.resume();
		renderView.resume();
	}

	@Override
	public void onPause()
	{
		super.onPause();
		wakeLock.release();
		updateLogic.pause();
		renderView.pause();
		this.pause();

		if (isFinishing())
			this.dispose();
	}

	public Input getInput()
	{
		return input;
	}

	public FileIO getFileIO()
	{
		return fileIO;
	}

	public Graphics getGraphics()
	{
		return graphics;
	}

	public Audio getAudio()
	{
		return audio;
	}
}