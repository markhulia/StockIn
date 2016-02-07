package com.georgequotes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSReader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.georgequotes.fragments.ChartViewFragment;
import com.georgequotes.fragments.CustomViewFragment.IFragmentEventListener;
import com.georgequotes.fragments.RssViewFragment;
import com.georgequotes.fragments.SearchViewFragment;
import com.georgequotes.objects.Point;
import com.georgequotes.objects.Points;
import com.georgequotes.utils.CouchDb;

@SuppressLint("UseSparseArrays")
public class MainActivity extends Activity implements IFragmentEventListener {

	private class ChartDataParams {
		public String url;
		public String db;
		public String document;
		public String login;
		public String password;
		public int timeout;
	}

	private class ChartDataAsyncTask extends
			AsyncTask<ChartDataParams, Void, Object> {
		@Override
		protected Object doInBackground(ChartDataParams... params) {
			ChartDataParams cdp = params[0];

			try {
				CouchDb db = new CouchDb(cdp.url, cdp.db, cdp.login,
						cdp.password, cdp.timeout);
				return db.document(cdp.document);
			} catch (Exception ex) {
				return ex;
			}

		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);

			OnTaskComplete(this, result);
		}
	}

	private class RssAsyncTask extends AsyncTask<String, Void, Object> {
		@Override
		protected Object doInBackground(String... urls) {
			String url = urls[0];

			Object result = null;

			RSSReader reader = new RSSReader();

			try {
				result = reader.load(url);
			} catch (Exception ex) {
				result = ex;
			} finally {
				reader.close();
			}

			return result;
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);

			OnTaskComplete(this, result);
		}
	}

	private static final int MAP_OPEN = 1;
	private static final int MAP_CLOSE = 2;
	private static final int MAP_HIGH = 3;
	private static final int MAP_LOW = 4;
	private static final int MAP_VOLUME = 5;
	private static final int MAP_DATE = 6;

	static final Map<Integer, String> JSON_MAP;

	private ArrayList<Object> fTasks = new ArrayList<Object>();

	private ProgressDialog fProgressDialog;

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	private CharSequence mDrawerTitle;
	private CharSequence mTitle;

	private String[] mDrawerItems;

	static {
		JSON_MAP = new HashMap<Integer, String>();
		JSON_MAP.put(MAP_OPEN, "Open");
		JSON_MAP.put(MAP_CLOSE, "Close");
		JSON_MAP.put(MAP_HIGH, "High");
		JSON_MAP.put(MAP_LOW, "Low");
		JSON_MAP.put(MAP_VOLUME, "Volume");
		JSON_MAP.put(MAP_DATE, "Time");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);

		mTitle = mDrawerTitle = getTitle();
		mDrawerItems = getResources().getStringArray(R.array.drawer_array);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		// set a custom shadow that overlays the main content when the drawer
		// opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		// // set up the drawer's list view with items and click listener
		mDrawerList.setAdapter(new ArrayAdapter<String>(this,
				R.layout.drawer_list_item, mDrawerItems));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		// enable ActionBar app icon to behave as action to toggle nav drawer
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description for accessibility */
		R.string.drawer_close /* "close drawer" description for accessibility */
		) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}
		};

		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			setSearchView();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action buttons
		switch (item.getItemId()) {
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			switch (position) {
			case 0:
				setSearchView();
				break;
			case 1:
				setChartView();
				break;
			case 2:
				setRssView();
				break;
			}

			mDrawerList.setItemChecked(position, true);
			mDrawerLayout.closeDrawer(mDrawerList);
		}
	}

	private void setPoints(Points points) {
		App.one.points = points;

		if (App.one.points != null && App.one.feed != null)
			this.setChartView();
	}

	private void setRss(RSSFeed feed) {
		App.one.feed = feed;

		if (App.one.points != null && App.one.feed != null)
			this.setChartView();

	}

	private void setSymbol(String symbol) {
		if (App.one.symbol != symbol) {
			App.one.feed = null;
			App.one.points = null;

			App.one.symbol = symbol;

			refreshData();
		}
	}

	private void refreshData() {
		String rss = String.format(Globals.RSS_URL_PLACEHOLDER, App.one.symbol);

		fTasks.clear();

		RssAsyncTask rssTask = new RssAsyncTask();
		ChartDataAsyncTask cdTask = new ChartDataAsyncTask();

		fTasks.add(rssTask);
		fTasks.add(cdTask);

		ChartDataParams params = new ChartDataParams();
		params.url = Globals.COUCH_HOST;
		params.db = Globals.COUCH_DB;
		params.document = App.one.symbol;
		params.login = Globals.COUCH_USER;
		params.password = Globals.COUCH_PASSWORD;
		params.timeout = Globals.HTTP_TIMEOUT;

		cdTask.execute(params);
		rssTask.execute(rss);

		this.showProgressDialog("Loading...");

	}

	private void setChartView() {
		ChartViewFragment f = new ChartViewFragment();
		f.setFragmentEventListener(this);
		setFragment(f);
		setTitle(App.one.symbol);
	}

	private void setRssView() {
		RssViewFragment f = new RssViewFragment();
		f.setFragmentEventListener(this);
		setFragment(f);
	}

	private void setSearchView() {
		SearchViewFragment f = new SearchViewFragment();
		f.setFragmentEventListener(this);
		setFragment(f);
	}

	private void setFragment(Fragment fragment) {

		FragmentManager fragmentManager = getFragmentManager();

		FragmentTransaction tr = fragmentManager.beginTransaction();

		tr.replace(R.id.content_frame, fragment).commit();
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void OnFragmentEvent(int eventId, Bundle params) {
		switch (eventId) {
		case SearchViewFragment.EVENT_GO_BUTTON_PRESSED: {
			String symbol = params.getString(SearchViewFragment.KEY_SYMBOL);
			this.setSymbol(symbol);

		}
			break;
		}

	}

	protected void OnTaskComplete(Object task, Object result) {
		if (fTasks.size() == 0)
			return;

		Exception ex = null;
		if (result instanceof Exception) {
			ex = (Exception) result;
		} else {
			try {
				handleResult(result);
			} catch (Exception e) {
				ex = e;
			}
		}

		if (ex != null) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("Error")
					.setMessage("No symbol found")
					.setCancelable(false)
					.setNeutralButton("OK",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									switch (which) {
									case DialogInterface.BUTTON_NEUTRAL: {
										dialog.dismiss();
									}
										break;
									}
								}
							});

			b.show();
		} else {

		}

		fTasks.remove(task);

		if (fTasks.size() == 0) {
			hideProgressDialog();
		}
	}

	private void handleResult(Object result) throws JSONException,
			ParseException {
		if (result instanceof RSSFeed) {
			RSSFeed feed = (RSSFeed) result;

			this.setRss(feed);
		} else if (result instanceof JSONObject) {
			JSONObject json = (JSONObject) result;

			Points points = parseJsonDocument(json);

			this.setPoints(points);
		}
	}

	private void showProgressDialog(String text) {
		hideProgressDialog();

		fProgressDialog = ProgressDialog.show(this, "Please wait", text);
	}

	private void hideProgressDialog() {
		if (null != fProgressDialog) {
			fProgressDialog.dismiss();
			fProgressDialog = null;
		}
	}

	@SuppressLint("SimpleDateFormat")
	private Points parseJsonDocument(JSONObject json) throws JSONException,
			ParseException {

		String openKey = JSON_MAP.get(MAP_OPEN);
		String closeKey = JSON_MAP.get(MAP_CLOSE);
		String highKey = JSON_MAP.get(MAP_HIGH);
		String lowKey = JSON_MAP.get(MAP_LOW);
		String volumeKey = JSON_MAP.get(MAP_VOLUME);
		String dateKey = JSON_MAP.get(MAP_DATE);

		JSONObject object = json.getJSONObject("Stock");
		JSONArray array = object.getJSONArray("Data");
		Points result = new Points();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		for (int i = 0; i < array.length(); i++) {
			JSONObject obj = array.getJSONObject(i);

			Point cp = new Point();
			cp.c = obj.getDouble(closeKey);
			cp.h = obj.getDouble(highKey);
			cp.l = obj.getDouble(lowKey);
			cp.o = obj.getDouble(openKey);
			cp.v = obj.getDouble(volumeKey);

			String date = obj.getString(dateKey); // get date as a UNIX string

			String longModtime = df.format(new Date(Long.parseLong(date)));

			Date dateObj = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.parse(longModtime); // format Date string to Date format

			cp.dt = dateObj; // assign date to variable

			result.add(cp);
		}
		return result;
	}
}