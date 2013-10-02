package com.nish;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.nish.model.FriendRow;
import com.nish.model.ListPendingAdapter;
import com.nish.model.Utility;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.ListView;
import android.widget.RemoteViews;

public class WidgetActivity extends AppWidgetProvider {
	private static final String LOG_TAG = "ExampleWidget";

	private static final DateFormat df = new SimpleDateFormat("hh:mm:ss");

	/**
	 * Custom Intent name that is used by the AlarmManager to tell us to update
	 * the clock once per second.
	 */
	public static String CLOCK_WIDGET_UPDATE = "com.eightbitcloud.example.widget.8BITCLOCK_WIDGET_UPDATE";
	public static String FORCE_WIDGET_UPDATE = "com.paad.mywidget.FORCE_WIDGET_UPDATE";

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		try {
			Parse.initialize(context,
					"PhPbACGcB2vstnumIcUX1D1WrOzabqFPognqfufu",
					"K5fWoItwvDbFXXPyYL11J3t4thAW3YQw3oUyeS7P");
		} catch (Exception e) {
		}
		try {
			Log.d(LOG_TAG, "Clock update");
			if (CLOCK_WIDGET_UPDATE.equals(intent.getAction())
					|| FORCE_WIDGET_UPDATE.equals(intent.getAction())) {

				// Get the widget manager and ids for this widget provider, then
				// call the shared
				// clock update method.
				ComponentName thisAppWidget = new ComponentName(
						context.getPackageName(), getClass().getName());
				AppWidgetManager appWidgetManager = AppWidgetManager
						.getInstance(context);
				int ids[] = appWidgetManager.getAppWidgetIds(thisAppWidget);
				for (int appWidgetID : ids) {
					updateAppWidget(context, appWidgetManager, appWidgetID);

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private PendingIntent createClockTickIntent(Context context) {
		try {
			Parse.initialize(context,
					"PhPbACGcB2vstnumIcUX1D1WrOzabqFPognqfufu",
					"K5fWoItwvDbFXXPyYL11J3t4thAW3YQw3oUyeS7P");
		} catch (Exception e) {
		}
		Intent intent = new Intent(CLOCK_WIDGET_UPDATE);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);

		return pendingIntent;
	}

	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
		try {
			Parse.initialize(context,
					"PhPbACGcB2vstnumIcUX1D1WrOzabqFPognqfufu",
					"K5fWoItwvDbFXXPyYL11J3t4thAW3YQw3oUyeS7P");
		} catch (Exception e) {
		}
		try {
			AlarmManager alarmManager = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);
			alarmManager.cancel(createClockTickIntent(context));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		try {
			Parse.initialize(context,
					"PhPbACGcB2vstnumIcUX1D1WrOzabqFPognqfufu",
					"K5fWoItwvDbFXXPyYL11J3t4thAW3YQw3oUyeS7P");
		} catch (Exception e) {
		}
		try {
			AlarmManager alarmManager = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);

			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(System.currentTimeMillis());
			calendar.add(Calendar.SECOND, 60);
			alarmManager.setRepeating(AlarmManager.RTC,
					calendar.getTimeInMillis(), 60000,
					createClockTickIntent(context));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		try {
			Parse.initialize(context,
					"PhPbACGcB2vstnumIcUX1D1WrOzabqFPognqfufu",
					"K5fWoItwvDbFXXPyYL11J3t4thAW3YQw3oUyeS7P");
		} catch (Exception e) {
		}
		try {
			final int N = appWidgetIds.length;

			Log.d(LOG_TAG, "Updating Example Widgets.");

			// Perform this loop procedure for each App Widget that belongs to
			// this
			// provider
			for (int i = 0; i < N; i++) {
				int appWidgetId = appWidgetIds[i];

				// Create an Intent to launch ExampleActivity
				Intent intent = new Intent(context, MainActivity.class);
				PendingIntent pendingIntent = PendingIntent.getActivity(
						context, 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);

				// Get the layout for the App Widget and attach an on-click
				// listener
				// to the button
				RemoteViews views = new RemoteViews(context.getPackageName(),
						R.layout.widget1);
				views.setOnClickPendingIntent(R.id.button, pendingIntent);
				// Tell the AppWidgetManager to perform an update on the current
				// app
				// widget
				appWidgetManager.updateAppWidget(appWidgetId, views);

				// Update The clock label using a shared method
				updateAppWidget(context, appWidgetManager, appWidgetId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void updateAppWidget(Context context,
			AppWidgetManager appWidgetManager, int appWidgetId) {
		boolean network= Utility.isOnline(context);
		try {
			Parse.initialize(context,
					"PhPbACGcB2vstnumIcUX1D1WrOzabqFPognqfufu",
					"K5fWoItwvDbFXXPyYL11J3t4thAW3YQw3oUyeS7P");
		} catch (Exception e) {
		}
		try {
			if(!network){
				RemoteViews updateViews = new RemoteViews(
						context.getPackageName(), R.layout.widget1);
				updateViews
						.setTextViewText(R.id.widget1label, "Not have connection");
				updateViews.setTextViewText(R.id.widget2label, "");
				appWidgetManager.updateAppWidget(appWidgetId, updateViews);
			}
			ParseUser currUser = ParseUser.getCurrentUser();
			if (currUser != null) {
				final Context c = context;
				final AppWidgetManager app = appWidgetManager;
				final int id = appWidgetId;
				ParseQuery query = new ParseQuery("Pending");
				query.include("user1");
				query.include("user2");
				query.whereEqualTo("user1", currUser);
				query.findInBackground(new FindCallback() {

					@Override
					public void done(List<ParseObject> pendings,
							ParseException e) {
						if (e == null) {
							RemoteViews updateViews = new RemoteViews(c
									.getPackageName(), R.layout.widget1);
							updateViews.setTextViewText(R.id.widget1label,
									pendings.size() + " friend pending(s)");
							app.updateAppWidget(id, updateViews);
						} else {
							e.printStackTrace();
						}
					}
				});

				final ParseUser pu = ParseUser.getCurrentUser();

				ParseQuery query1 = new ParseQuery("Image");
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.MINUTE, -30);
				Date date = cal.getTime();
				query1.whereGreaterThanOrEqualTo("createdAt", date);

				query1.countInBackground(new CountCallback() {

					@Override
					public void done(int total, ParseException e) {
						RemoteViews updateViews = new RemoteViews(c
								.getPackageName(), R.layout.widget1);
						updateViews.setTextViewText(R.id.widget2label, total
								+ " new post(s) in last 30 minutes");
						app.updateAppWidget(id, updateViews);
					}
				});

			} else {
				RemoteViews updateViews = new RemoteViews(
						context.getPackageName(), R.layout.widget1);
				updateViews
						.setTextViewText(R.id.widget1label, "Not log in yet");
				updateViews.setTextViewText(R.id.widget2label, "");
				appWidgetManager.updateAppWidget(appWidgetId, updateViews);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
