package com.nish;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

public class CallReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
			if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
				Intent i = new Intent(context, MainActivity.class);
				i.putExtras(intent);
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				context.startActivity(i);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
