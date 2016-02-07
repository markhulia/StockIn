package com.georgequotes.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class CustomViewFragment extends Fragment {
	private int fResId;
	private IFragmentEventListener fListener;

	public interface IFragmentEventListener {
		public void OnFragmentEvent(int eventId, Bundle params);
	}

	public CustomViewFragment() {
	}

	public CustomViewFragment(int resId) {
		fResId = resId;
	}

	public int getResId() {
		return fResId;
	}

	public IFragmentEventListener getFragmentEventListener() {
		return fListener;
	}

	public void setFragmentEventListener(IFragmentEventListener value) {
		fListener = value;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(fResId, container, false);

		setupView(inflater, v);

		return v;
	}

	public void setupView(LayoutInflater inflater, View v) {
		// do nothing here
	}

	protected void OnFragmentEvent(int eventId, Bundle params) {
		if (null != fListener)
			fListener.OnFragmentEvent(eventId, params);
	}
}
