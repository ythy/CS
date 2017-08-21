package com.mx.cs.receiver;

import com.mx.cs.service.FxService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ActionReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
	 
		Intent smsIntent = new Intent(context,
				FxService.class);
		context.startService(smsIntent);
	}

}

