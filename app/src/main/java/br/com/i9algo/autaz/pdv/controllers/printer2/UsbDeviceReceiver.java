package br.com.i9algo.autaz.pdv.controllers.printer2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.widget.Toast;

public class UsbDeviceReceiver extends WakefulBroadcastReceiver {
	   
	@SuppressLint("ShowToast")
	@Override
	public void onReceive(Context context, Intent i) {
		Log.i("UsbDeviceReceiver", "Impressora");
	 	Toast.makeText(context, "A impressora foi desconectada.", Toast.LENGTH_LONG);
	}
}