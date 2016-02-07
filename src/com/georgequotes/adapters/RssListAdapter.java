package com.georgequotes.adapters;

import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.georgequotes.R;

public class RssListAdapter extends BaseAdapter
{
	private RSSFeed fRSSFeed;
	private Context fContext;
	
	public RssListAdapter(Context context, RSSFeed feed)
	{
		fRSSFeed = feed;
		fContext = context;
	}
	
	public int getCount() 
	{
		return fRSSFeed.getItems().size();
	}

	public Object getItem(int arg0) 
	{
		return  fRSSFeed.getItems().get(arg0);
	}

	public long getItemId(int arg0) 
	{
		return arg0;
	}

	public View getView(int position, View view, ViewGroup viewGroup)
	{
		ListAdapterCache cache;
		
        if (view == null) 
        {
            LayoutInflater inflater = (LayoutInflater) fContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.rss_listview_item, null);
            cache = new ListAdapterCache(view);
            view.setTag(cache);
        }
        else
        {
        	cache = (ListAdapterCache)view.getTag();
        }
        
        RSSItem item = (RSSItem)getItem(position);
        
        cache.getTextView(R.id.nameTextView).setText(item.getTitle());
        cache.getTextView(R.id.descrTextView).setText(item.getDescription());
                
        return view;
	}
}
