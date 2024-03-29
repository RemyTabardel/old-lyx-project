package com.rta.framework.audio;

import java.io.IOException;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;

public class Music implements OnCompletionListener, OnSeekCompleteListener, OnPreparedListener, OnVideoSizeChangedListener
{
	MediaPlayer	mediaPlayer;
	boolean		isPrepared	= false;

	public Music(AssetFileDescriptor assetDescriptor)
	{
		mediaPlayer = new MediaPlayer();
		try
		{
			mediaPlayer.setDataSource(assetDescriptor.getFileDescriptor(), assetDescriptor.getStartOffset(), assetDescriptor.getLength());
			mediaPlayer.prepare();
			isPrepared = true;
			mediaPlayer.setOnCompletionListener(this);
			mediaPlayer.setOnSeekCompleteListener(this);
			mediaPlayer.setOnPreparedListener(this);
			mediaPlayer.setOnVideoSizeChangedListener(this);

		}
		catch (Exception e)
		{
			throw new RuntimeException("Couldn't load music");
		}
	}

	public void dispose()
	{

		if (this.mediaPlayer.isPlaying())
		{
			this.mediaPlayer.stop();
		}
		this.mediaPlayer.release();
	}

	public boolean isLooping()
	{
		return mediaPlayer.isLooping();
	}

	public boolean isPlaying()
	{
		return this.mediaPlayer.isPlaying();
	}

	public boolean isStopped()
	{
		return !isPrepared;
	}

	public void pause()
	{
		if (this.mediaPlayer.isPlaying())
			mediaPlayer.pause();
	}

	public void play()
	{
		if (this.mediaPlayer.isPlaying())
			return;

		try
		{
			synchronized (this)
			{
				if (!isPrepared)
					mediaPlayer.prepare();
				mediaPlayer.start();
			}
		}
		catch (IllegalStateException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void setLooping(boolean isLooping)
	{
		mediaPlayer.setLooping(isLooping);
	}

	public void setVolume(float volume)
	{
		mediaPlayer.setVolume(volume, volume);
	}

	public void stop()
	{
		if (this.mediaPlayer.isPlaying() == true)
		{
			this.mediaPlayer.stop();

			synchronized (this)
			{
				isPrepared = false;
			}
		}
	}

	public void onCompletion(MediaPlayer player)
	{
		synchronized (this)
		{
			isPrepared = false;
		}
	}

	public void seekBegin()
	{
		mediaPlayer.seekTo(0);

	}

	@Override
	public void onPrepared(MediaPlayer player)
	{
		// TODO Auto-generated method stub
		synchronized (this)
		{
			isPrepared = true;
		}

	}

	@Override
	public void onSeekComplete(MediaPlayer player)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onVideoSizeChanged(MediaPlayer player, int width, int height)
	{
		// TODO Auto-generated method stub

	}
}