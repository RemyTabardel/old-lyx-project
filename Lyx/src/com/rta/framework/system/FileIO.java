package com.rta.framework.system;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Environment;
import android.preference.PreferenceManager;

public class FileIO
{
	Context			context;
	AssetManager	assets;
	String			externalStoragePath;

	public FileIO(Context context)
	{
		this.context = context;
		this.assets = context.getAssets();
		this.externalStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;

	}

	public InputStream readAsset(String file) throws IOException
	{
		return assets.open(file);
	}

	public InputStream readFile(String file) throws IOException
	{
		return new FileInputStream(externalStoragePath + file);
	}

	public OutputStream writeFile(String file) throws IOException
	{
		return new FileOutputStream(externalStoragePath + file);
	}

	public SharedPreferences getSharedPref()
	{
		return PreferenceManager.getDefaultSharedPreferences(context);
	}
}