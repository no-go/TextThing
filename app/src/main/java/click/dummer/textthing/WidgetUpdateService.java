package click.dummer.textthing;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WidgetUpdateService extends Service {
    private Uri data = null;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Date dNow = new Date();
        RemoteViews views = new RemoteViews(getPackageName(), R.layout.main_widget);

        File file = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            file = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                    App.PACKAGE_NAME
            );
        } else {
            file = new File(Environment.getExternalStorageDirectory() + "/Documents/"+App.PACKAGE_NAME);
        }

        String path = file.getPath() + App.NOTE_FILENAME;
        try {
            Log.d(App.PACKAGE_NAME, "mkdirs()");
            file.mkdirs();
            file = new File(path);
            if (!file.exists()) file.createNewFile();
            data = Uri.fromFile(file);

            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file))
            );
            String text = "";
            while (bufferedReader.ready()) {
                text += bufferedReader.readLine() + "\n";
            }
            views.setTextViewText(R.id.wContent, text);

        } catch (Exception e) {
            Toast.makeText(
                    getApplicationContext(),
                    getString(R.string.errAccess) + "\n" + data.getPath(),
                    Toast.LENGTH_LONG
            ).show();
        }

        // Push update for this widget to the home screen
        ComponentName thisWidget = new ComponentName(WidgetUpdateService.this, MyWidgetProvider.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(WidgetUpdateService.this);
        manager.updateAppWidget(thisWidget, views);

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}