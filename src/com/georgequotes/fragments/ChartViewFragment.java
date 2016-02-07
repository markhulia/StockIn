package com.georgequotes.fragments;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.mcsoxford.rss.RSSItem;
import org.stockchart.StockChartView;
import org.stockchart.core.Area;
import org.stockchart.core.Axis;
import org.stockchart.core.Axis.ILabelFormatProvider;
import org.stockchart.core.Axis.Side;
import org.stockchart.core.AxisRange;
import org.stockchart.misc.DateTimeScaleValuesProvider;
import org.stockchart.series.BarSeries;
import org.stockchart.series.LinearSeries;
import org.stockchart.series.StockSeries;
import org.stockchart.series.StockSeries.ViewType;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.georgequotes.App;
import com.georgequotes.R;
import com.georgequotes.adapters.RssListAdapter;
import com.georgequotes.adapters.SimplePagerAdapter;
import com.georgequotes.objects.Point;

@SuppressLint("SimpleDateFormat")
public class ChartViewFragment extends CustomViewFragment {
	public static final String ARG_SYMBOL = "Symbol";

	public static final int STEP_DAILY = 0;
	public static final int STEP_WEEKLY = 1000;
	public static final int STEP_MONTHLY = 2000;
	public static final int STEP_YEARLY = 3000;

	private static final SimpleDateFormat DF = new SimpleDateFormat();

	private final ArrayList<Point> fPreparedPoints = new ArrayList<Point>();

	private ListView fListView;
	private ViewPager fViewPager;

	private StockChartView[] fViews;

	private StockSeries fBarSeries;
	private StockSeries fCandleSeries;
	private LinearSeries fLineSeries;
	private BarSeries fVolumeSeries;

	private int fStep = STEP_DAILY;

	public ChartViewFragment() {
		super(R.layout.chart_view);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.chart_view_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int i = item.getItemId();

		int step = fStep;
		switch (i) {
		case R.id.dailyItem:
			step = STEP_DAILY;
			break;
		case R.id.weeklyItem:
			step = STEP_WEEKLY;
			break;
		case R.id.monthlyItem:
			step = STEP_MONTHLY;
			break;
		case R.id.yearlyItem:
			step = STEP_YEARLY;
			break;
		default:
			return super.onOptionsItemSelected(item);
		}

		if (step != fStep && null != App.one.points) {
			fStep = step;
			this.reloadAllStuff();
		}

		return true;
	}

	@Override
	public void setupView(LayoutInflater inflater, View v) {
		this.setHasOptionsMenu(true);

		fListView = (ListView) v.findViewById(R.id.rssListView);
		fListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				RSSItem item = App.one.feed.getItems().get(position);

				Intent browserIntent = new Intent(Intent.ACTION_VIEW, item
						.getLink());
				startActivity(browserIntent);
			}
		});

		if (null != App.one.feed)
			fListView
					.setAdapter(new RssListAdapter(getActivity(), App.one.feed));
		// setup pager

		fViewPager = (ViewPager) v.findViewById(R.id.pager);
		fViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub

			}

		});

		ArrayList<View> pages = new ArrayList<View>();

		fViews = new StockChartView[] { new StockChartView(getActivity()),
				new StockChartView(getActivity()),
				new StockChartView(getActivity()) };

		for (StockChartView view : fViews) {
			pages.add(view);
		}

		SimplePagerAdapter pagerAdapter = new SimplePagerAdapter(pages);

		fViewPager.setAdapter(pagerAdapter);

		if (null != App.one.points) {
			reloadAllStuff();
		}
	}

	private static boolean checkMerge(Point p1, Point p2, int step, Calendar c) {

		switch (step) {
		case STEP_DAILY: {
			c.setTime(p1.dt);
			int d1 = c.get(Calendar.DAY_OF_YEAR);
			int y1 = c.get(Calendar.YEAR);
			c.setTime(p2.dt);
			int d2 = c.get(Calendar.DAY_OF_YEAR);
			int y2 = c.get(Calendar.YEAR);

			return d1 == d2 && y1 == y2;
		}
		case STEP_WEEKLY: {
			c.setTime(p1.dt);

			int w1 = c.get(Calendar.WEEK_OF_YEAR);
			int y1 = c.get(Calendar.YEAR);

			c.setTime(p2.dt);
			int w2 = c.get(Calendar.WEEK_OF_YEAR);
			int y2 = c.get(Calendar.YEAR);

			return w1 == w2 && y1 == y2;
		}
		case STEP_MONTHLY: {
			c.setTime(p1.dt);

			int m1 = c.get(Calendar.MONTH);
			int y1 = c.get(Calendar.YEAR);
			c.setTime(p2.dt);
			int m2 = c.get(Calendar.MONTH);
			int y2 = c.get(Calendar.YEAR);

			return m1 == m2 && y1 == y2;
		}
		case STEP_YEARLY: {
			c.setTime(p1.dt);

			int y1 = c.get(Calendar.YEAR);
			c.setTime(p2.dt);
			int y2 = c.get(Calendar.YEAR);

			return y1 == y2;
		}
		}

		return false;
	}

	private void preparePoints() {
		Calendar c = Calendar.getInstance();

		fPreparedPoints.clear();
		fPreparedPoints.add(new Point(App.one.points.get(0)));

		for (int i = 1; i < App.one.points.size(); i++) {
			Point lp = fPreparedPoints.get(fPreparedPoints.size() - 1);

			Point p = App.one.points.get(i);

			if (checkMerge(p, lp, fStep, c)) {
				lp.c = p.c;

				if (p.h > lp.h)
					lp.h = p.h;

				if (p.l < lp.l)
					lp.l = p.l;

				lp.v += p.v;
			} else {
				fPreparedPoints.add(new Point(p));
			}
		}
	}

	private void reloadAllStuff() {
		preparePoints();
		prepareCharts();
		reloadPoints();
		invalidateAll();
	}

	private void setupStockChart(StockChartView v, int configuration) {
		v.reset();

		Area primaryArea = v.addArea();
		Area secondaryArea = v.addArea();

		primaryArea.setAxesVisible(false, false, true, false);
		secondaryArea.setAxesVisible(false, false, true, true);

		primaryArea.getRightAxis().setLinesCount(2);

		secondaryArea.setAutoHeight(false);
		secondaryArea.setHeightInPercents(0.2f);
		secondaryArea.getRightAxis().setLinesCount(0);

		secondaryArea.getBottomAxis().setLabelFormatProvider(
				new ILabelFormatProvider() {
					@Override
					public String getAxisLabel(Axis sender, double value) {
						int index = fVolumeSeries.convertToArrayIndex(value);
						if (index < 0)
							index = 0;

						if (index >= 0 && fVolumeSeries.getPointCount() > 0) {
							if (index >= fVolumeSeries.getPointCount())
								index = fVolumeSeries.getPointCount() - 1;

							Date date = (Date) fVolumeSeries.getPoints()
									.get(index).getID();

							switch (fStep) {
							case STEP_DAILY: {
								DF.applyPattern("d/MM");
							}
								break;
							case STEP_WEEKLY: {
								DF.applyPattern("d/MM");
							}
								break;
							case STEP_MONTHLY: {
								DF.applyPattern("MMMM");
							}
								break;
							case STEP_YEARLY: {
								DF.applyPattern("y");
							}
								break;
							}

							return DF.format(date);
						}

						return null;
					}
				});

		AxisRange ar = new AxisRange();
		ar.setZoomable(true);
		ar.setMovable(true);
		v.enableGlobalAxisRange(Side.BOTTOM, ar);

		switch (configuration) {
		case 0: {
			fCandleSeries = new StockSeries();
			fCandleSeries.setViewType(ViewType.CANDLESTICK);
			primaryArea.getSeries().add(fCandleSeries);
		}
			break;
		case 1: {
			fBarSeries = new StockSeries();
			fBarSeries.setViewType(ViewType.BAR);
			primaryArea.getSeries().add(fBarSeries);
		}
			break;
		case 2: {
			fLineSeries = new LinearSeries();
			primaryArea.getSeries().add(fLineSeries);
		}
			break;
		}

		if (null == fVolumeSeries) {
			fVolumeSeries = new BarSeries();
		}

		secondaryArea.getSeries().add(fVolumeSeries);

		DateTimeScaleValuesProvider svp = new DateTimeScaleValuesProvider(
				fVolumeSeries);

		primaryArea.getBottomAxis().setScaleValuesProvider(svp);
		secondaryArea.getBottomAxis().setScaleValuesProvider(svp);
	}

	private void invalidateAll() {
		for (View v : fViews)
			v.invalidate();
	}

	private void prepareCharts() {
		for (int i = 0; i < fViews.length; i++) {
			setupStockChart(fViews[i], i);
		}
	}

	private void reloadPoints() {
		fBarSeries.getPoints().clear();
		fCandleSeries.getPoints().clear();
		fLineSeries.getPoints().clear();
		fVolumeSeries.getPoints().clear();

		for (Point p : fPreparedPoints) {
			fBarSeries.addPoint(p.o, p.h, p.l, p.c).setID(p.dt);
			fCandleSeries.addPoint(p.o, p.h, p.l, p.c).setID(p.dt);
			fLineSeries.addPoint(p.c).setID(p.dt);
			fVolumeSeries.addPoint(0.0, p.v).setID(p.dt);
		}
	}

}
