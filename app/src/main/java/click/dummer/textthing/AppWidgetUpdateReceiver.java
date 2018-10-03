package click.dummer.textthing;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.JobIntentService;

public class AppWidgetUpdateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        JobIntentService.enqueueWork(context, WidgetUpdateService.class, 1, intent);
    }
}