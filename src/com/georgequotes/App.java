package com.georgequotes;

import org.mcsoxford.rss.RSSFeed;

import android.app.Application;

import com.georgequotes.objects.Points;

public class App extends Application  
{
	public static App one;
	
	public void onCreate()
	{
		one = this;
	}
	
	public RSSFeed feed;
	public String symbol;
	public Points points;
}
