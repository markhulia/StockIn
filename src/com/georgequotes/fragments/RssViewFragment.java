package com.georgequotes.fragments;

import org.mcsoxford.rss.RSSFeed;

import org.mcsoxford.rss.RSSItem;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.georgequotes.App;
import com.georgequotes.R;
import com.georgequotes.adapters.RssListAdapter;

@SuppressLint("ValidFragment")
public class RssViewFragment extends CustomViewFragment {
	private RSSFeed fRSSFeed;

	public RssViewFragment() {
		this(null);
	}

	public RssViewFragment(Object object) {
		super(R.layout.rss_view);

		fRSSFeed = App.one.feed;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setupView(LayoutInflater inflater, View v) {
		this.setHasOptionsMenu(false);

		ListView listView = (ListView) v.findViewById(R.id.rssListView2);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				RSSItem item = fRSSFeed.getItems().get(position);

				Intent browserIntent = new Intent(Intent.ACTION_VIEW, item
						.getLink());
				startActivity(browserIntent);
			}
		});
		if (null != fRSSFeed)
			listView.setAdapter(new RssListAdapter(getActivity(), fRSSFeed));
	}
}
