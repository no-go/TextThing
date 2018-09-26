package click.dummer.textthing;

import android.app.Application;

public class App extends Application {
    public static String PACKAGE_NAME;

    public static final float   DEFAULT_Size = 20f;
    public static final boolean DEFAULT_Mono = true;
    public static final boolean DEFAULT_AutoSave = true;
    public static final int     DEFAULT_Theme = 0;
    public static final String PREF_Size     = "size";
    public static final String PREF_Mono     = "mono";
    public static final String PREF_AutoSave = "autosave";
    public static final String PREF_Theme    = "theme";

    public static final String NOTE_FILENAME = "/notes.txt";

    public static final int READ_PERMISSION_REQ = 23;
    public static final int WRITE_PERMISSION_REQ = 42;

    @Override
    public void onCreate() {
        super.onCreate();
        PACKAGE_NAME = getApplicationContext().getPackageName();
    }
}
