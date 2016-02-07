package com.georgequotes.fragments;


import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.georgequotes.R;

public class SearchViewFragment extends CustomViewFragment 
{					
	
	private EditText fSymbolEditText;
	
	public static final int EVENT_GO_BUTTON_PRESSED = 100;
	public static final String KEY_SYMBOL = "Symbol";
	
	public SearchViewFragment()
	{
		super(R.layout.search_view);
	}
	
	public EditText getSymbolEditText() { return fSymbolEditText; }
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setHasOptionsMenu(false);
		
	}
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
	}
	@Override
	public void setupView(LayoutInflater inflater, View v)
	{
		
		
		fSymbolEditText = (EditText)v.findViewById(R.id.symbolEditText);
    	Button go = (Button)v.findViewById(R.id.goButton);
    	
    	go.setOnClickListener(new OnClickListener() 
    	{
			@Override
			public void onClick(View v) 
			{					
				 Bundle args = new Bundle();
				 
				 args.putString(KEY_SYMBOL, fSymbolEditText.getText().toString());			 
				 
				 OnFragmentEvent(EVENT_GO_BUTTON_PRESSED,args);
				 
			}	    		
    	});
	}
}