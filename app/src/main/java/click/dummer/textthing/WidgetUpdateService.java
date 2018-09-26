package click.dummer.textthing;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class WidgetUpdateService extends Service {
    private Uri data = null;
    private SharedPreferences mPreferences;
    private int themeNr;
    private boolean isMono;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        isMono = mPreferences.getBoolean(App.PREF_Mono, App.DEFAULT_Mono);
        themeNr = mPreferences.getInt(App.PREF_Theme, App.DEFAULT_Theme);

        RemoteViews views = null;
        switch (themeNr) {
            case 1:
                if(isMono) {
                    views = new RemoteViews(getPackageName(), R.layout.day_widget_m);
                } else {
                    views = new RemoteViews(getPackageName(), R.layout.day_widget);
                }
                break;
            case 2:
                if(isMono) {
                    views = new RemoteViews(getPackageName(), R.layout.night_widget_m);
                } else {
                    views = new RemoteViews(getPackageName(), R.layout.night_widget);
                }
                break;
            case 3:
                //views = new RemoteViews(getPackageName(), R.layout.c64_widget);
                views = new RemoteViews(getPackageName(), R.layout.c64_widget_test);
                break;
            case 4:
                //views = new RemoteViews(getPackageName(), R.layout.c64_widget);
                views = new RemoteViews(getPackageName(), R.layout.green_widget);
                break;
            default:
                if(isMono) {
                    views = new RemoteViews(getPackageName(), R.layout.retro_widget_m);
                } else {
                    views = new RemoteViews(getPackageName(), R.layout.retro_widget);
                }
                break;
        }



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
/*            if (themeNr == 3) {
                //AppWidgetManager.getInstance(this).getAppWidgetOptions(0).getBundle(AppWidgetManager.O)
                int width = intent.getIntExtra("width", 73);
                int height = intent.getIntExtra("height", 73);
                Bitmap myBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                Canvas myCanvas = new Canvas(myBitmap);
                Paint paint = new Paint();
                Typeface c64 = Typeface.createFromAsset(this.getAssets(), "fonts/c64pro_mono.ttf");
                paint.setAntiAlias(true);
                paint.setSubpixelText(true);
                paint.setTypeface(c64);
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(ContextCompat.getColor(this, R.color.LightRetro));
                paint.setTextSize(9);
                paint.setTextAlign(Paint.Align.LEFT);
                myCanvas.drawText(text, 0, 11, paint);
                views.setImageViewBitmap(R.id.wContent, myBitmap);
            } else {
*/                views.setTextViewText(R.id.wContent, text);
//            }

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