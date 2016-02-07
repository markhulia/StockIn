package com.georgequotes.adapters;

import java.util.TreeMap;

import android.view.View;
import android.widget.TextView;

public class ListAdapterCache 
{
	private final View fBaseView;
	private final TreeMap<Integer,View> fMap = new  TreeMap<Integer,View>();
	
	public ListAdapterCache(View baseView)
	{
		fBaseView = baseView;
	}
	
	public TextView getTextView(int id)
	{
		return (TextView)getView(id);
	}
	
	public View getView(int id)
	{
		View result = fMap.get(id);
		
		if(null == result)
		{
			result = (View)fBaseView.findViewById(id);
			fMap.put(id, result);
		}
		
		return result;
	}
}