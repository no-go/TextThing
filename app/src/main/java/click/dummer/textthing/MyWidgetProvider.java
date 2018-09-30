package click.dummer.textthing;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.RemoteViews;

import java.util.Calendar;

public class MyWidgetProvider extends AppWidgetProvider {
    public static final int LONG_UPDATE  = 1 * 60 * 1000; // 1 minute
    public static int width = 90;
    public static int height = 70;

    private PendingIntent service = null;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int count = appWidgetIds.length;
        final AlarmManager m = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        final Calendar TIME = Calendar.getInstance();

        for (int i = 0; i < count; i++) {

            TIME.set(Calendar.MINUTE, 0);
            TIME.set(Calendar.SECOND, 0);
            TIME.set(Calendar.MILLISECOND, 0);
            Intent in = new Intent(context, WidgetUpdateService.class);

            if (service == null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    service = PendingIntent.getForegroundService(context, 0, in, PendingIntent.FLAG_CANCEL_CURRENT);
                } else {
                    service = PendingIntent.getService(context, 0, in, PendingIntent.FLAG_CANCEL_CURRENT);
                }
            }

            m.setRepeating(AlarmManager.RTC, TIME.getTime().getTime(), LONG_UPDATE, service);

            // ---------
            int widgetId = appWidgetIds[i];

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.retro_widget);

//          if you want widget update on click !!!
/*
            Intent intentU = new Intent(context, MyWidgetProvider.class);
            intentU.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intentU.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            intentU.putExtra("width", width);
            intentU.putExtra("height", height);
            PendingIntent pendingIntentU = PendingIntent.getBroadcast(
                    context, widgetId, intentU, PendingIntent.FLAG_UPDATE_CURRENT
            );
*/
            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
            );

            remoteViews.setOnClickPendingIntent(R.id.wContent, pendingIntent);
            //remoteViews.setOnClickPendingIntent(R.id.wTextApp, pendingIntent);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {

        width = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        height = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);

        int[] appWidgetIds = new int[1];
        appWidgetIds[0] = appWidgetId;
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.retro_widget);
        Intent intent = new Intent(context, MyWidgetProvider.class);

        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        intent.putExtra("width", width);
        intent.putExtra("height", height);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT
        );
        remoteViews.setOnClickPendingIntent(R.id.wContentLayout, pendingIntent);
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    @Override
    public void onDisabled(Context context) {
        final AlarmManager m = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (m != null) m.cancel(service);
    }
}
