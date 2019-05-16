package click.dummer.textthing;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import static android.Manifest.*;

public class MainActivity extends AppCompatActivity {
    private static final String PROJECT_LINK = "http://no-go.github.io/TextThing/";
    private static final String PROJECT2_LINK = "http://style64.org/c64-truetype";
    public static final int FILEREQCODE = 1234;

    private SharedPreferences mPreferences;
    private boolean isMono;
    private boolean autoSave;
    private boolean fromExtern;
    private int themeNr;
    private Uri data = null;
    private TextView contentView;
    private Button btn;
    private PopupMenu popup;
    private ViewGroup mainView;

    private Typeface c64Font;
    private Typeface intuitiveFont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        fromExtern = false;

        contentView = (TextView) findViewById(R.id.editText);
        mainView = (ViewGroup) findViewById(R.id.mainView);
        btn = (Button) findViewById(R.id.optnBtn);

        float fontSize = mPreferences.getFloat(App.PREF_Size, App.DEFAULT_Size);
        isMono = mPreferences.getBoolean(App.PREF_Mono, App.DEFAULT_Mono);
        autoSave = mPreferences.getBoolean(App.PREF_AutoSave, App.DEFAULT_AutoSave);
        themeNr = mPreferences.getInt(App.PREF_Theme, App.DEFAULT_Theme);
        contentView.setTextSize(fontSize);

        c64Font = Typeface.createFromAsset(getAssets(), "fonts/c64pro_mono.ttf");
        intuitiveFont = Typeface.createFromAsset(getAssets(), "fonts/intuitive.ttf");

        if (isMono == true) {
            contentView.setTypeface(Typeface.MONOSPACE);
        } else {
            contentView.setTypeface(Typeface.SANS_SERIF);
        }
        switch (themeNr) {
            case 1:
                themeDay();
                break;
            case 2:
                themeNight();
                break;
            case 3:
                themeC64();
                break;
            case 4:
                themeGreen();
                break;
            case 5:
                themePanther();
                break;
            default:
                themeRetro();
                break;
        }
        popup = new PopupMenu(MainActivity.this, btn);
        popup.dismiss();
        popup.getMenuInflater().inflate(R.menu.pref, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                float fontSize = mPreferences.getFloat(App.PREF_Size, App.DEFAULT_Size);
                switch (item.getItemId()) {
                    case R.id.smallerSize:
                        fontSize = fontSize * 0.9f;
                        break;
                    case R.id.biggerSize:
                        fontSize = fontSize * 1.1f;
                        break;
                    case R.id.monoStyle:
                        if (item.isChecked() == false) {
                            item.setChecked(true);
                            isMono = true;
                            contentView.setTypeface(Typeface.MONOSPACE);
                        } else {
                            item.setChecked(false);
                            isMono = false;
                            contentView.setTypeface(Typeface.SANS_SERIF);
                        }
                        break;

                    case R.id.autoSave:
                        if (item.isChecked() == false) {
                            item.setChecked(true);
                            autoSave = true;
                        } else {
                            item.setChecked(false);
                            autoSave = false;
                        }
                        break;

                    case R.id.theme_retro:
                        item.setChecked(true);
                        themeRetro();
                        break;

                    case R.id.theme_c64:
                        item.setChecked(true);
                        themeC64();
                        break;

                    case R.id.theme_day:
                        item.setChecked(true);
                        themeDay();
                        break;

                    case R.id.theme_night:
                        item.setChecked(true);
                        themeNight();
                        break;

                    case R.id.theme_green:
                        item.setChecked(true);
                        themeGreen();
                        break;

                    case R.id.theme_panther:
                        item.setChecked(true);
                        themePanther();
                        break;

                    case R.id.fileSelect:
                        Intent intentFileChooser = new Intent()
                                .setType(Intent.normalizeMimeType("text/*"))
                                .setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intentFileChooser, getString(R.string.open)), FILEREQCODE);
                        break;
                    case R.id.action_project:
                        Intent intentProj = new Intent(Intent.ACTION_VIEW, Uri.parse(PROJECT_LINK));
                        startActivity(intentProj);
                        break;
                    case R.id.action_project2:
                        Intent intentProj2 = new Intent(Intent.ACTION_VIEW, Uri.parse(PROJECT2_LINK));
                        startActivity(intentProj2);
                        break;
                    default:
                        return false;
                }
                contentView.setTextSize(fontSize);
                SharedPreferences.Editor editor = mPreferences.edit();
                editor.putFloat(App.PREF_Size, fontSize);
                editor.putBoolean(App.PREF_Mono, isMono);
                editor.putBoolean(App.PREF_AutoSave, autoSave);
                editor.putInt(App.PREF_Theme, themeNr);
                editor.commit();
                Intent i = new Intent("click.dummer.textthing.widget.UPDATE");
                sendBroadcast(i);
                return true;
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MenuItem mi = popup.getMenu().findItem(R.id.monoStyle);
                MenuItem mi0 = popup.getMenu().findItem(R.id.theme_retro);
                MenuItem mi1 = popup.getMenu().findItem(R.id.theme_day);
                MenuItem mi2 = popup.getMenu().findItem(R.id.theme_night);
                MenuItem mi3 = popup.getMenu().findItem(R.id.autoSave);
                MenuItem mi4 = popup.getMenu().findItem(R.id.theme_c64);
                MenuItem mi5 = popup.getMenu().findItem(R.id.theme_green);
                MenuItem mi6 = popup.getMenu().findItem(R.id.theme_panther);
                if (themeNr == 0) mi0.setChecked(true);
                if (themeNr == 1) mi1.setChecked(true);
                if (themeNr == 2) mi2.setChecked(true);
                if (themeNr == 3) mi4.setChecked(true);
                if (themeNr == 4) mi5.setChecked(true);
                if (themeNr == 5) mi6.setChecked(true);
                mi.setChecked(isMono);
                mi3.setChecked(autoSave);
                popup.show();
            }
        });

        Intent intent = getIntent();
        data = intent.getData();
        if (data != null) {
            fromExtern = true;

            Log.d(App.PACKAGE_NAME, "intent.getData() is not Null - Use App via filemanager?");
            try {
                InputStream input = getContentResolver().openInputStream(data);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input));

                String text = "";
                while (bufferedReader.ready()) {
                    text += bufferedReader.readLine() + "\n";
                }
                contentView.setText(text);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // opening App without a filemanager and use the default file
            fromExtern = false;

            Log.d(App.PACKAGE_NAME, "intent.getData() is Null - use App with default File");
            if (PermissionUtils.readGranted(this)) {
                loadNow();
            } else {
                String[] permissions = new String[]{permission.READ_EXTERNAL_STORAGE, permission.WRITE_EXTERNAL_STORAGE};
                PermissionUtils.requestPermissions(this, App.READ_PERMISSION_REQ, permissions);
            }
        }
    }

    void themeRetro() {
        themeNr = 0;
        mainView.setBackgroundColor(
                ContextCompat.getColor(getApplicationContext(), R.color.LightRetro)
        );
        btn.setTextColor(Color.WHITE);
        contentView.setBackgroundColor(
                ContextCompat.getColor(getApplicationContext(), R.color.DarkRetro)
        );
        contentView.setTextColor(
                ContextCompat.getColor(getApplicationContext(), R.color.LightRetro)
        );
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(
                    ContextCompat.getColor(getApplicationContext(), R.color.LightRetro)
            );
        }
        if (isMono) {
            contentView.setTypeface(Typeface.MONOSPACE);
            btn.setTypeface(Typeface.MONOSPACE);
        } else {
            contentView.setTypeface(Typeface.SANS_SERIF);
            btn.setTypeface(Typeface.SANS_SERIF);
        }
    }

    void themeDay() {
        themeNr = 1;
        mainView.setBackgroundColor(
                ContextCompat.getColor(getApplicationContext(), R.color.MiddleDay)
        );
        btn.setTextColor(Color.WHITE);
        contentView.setBackgroundColor(
                ContextCompat.getColor(getApplicationContext(), R.color.DarkDay)
        );
        contentView.setTextColor(
                ContextCompat.getColor(getApplicationContext(), R.color.LightDay)
        );
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(
                    ContextCompat.getColor(getApplicationContext(), R.color.LightDay)
            );
        }
        if (isMono) {
            contentView.setTypeface(Typeface.MONOSPACE);
            btn.setTypeface(Typeface.MONOSPACE);
        } else {
            contentView.setTypeface(Typeface.SANS_SERIF);
            btn.setTypeface(Typeface.SANS_SERIF);
        }
    }

    void themeNight() {
        themeNr = 2;
        mainView.setBackgroundColor(
                ContextCompat.getColor(getApplicationContext(), R.color.MiddleNight)
        );
        btn.setTextColor(Color.GRAY);
        contentView.setBackgroundColor(
                ContextCompat.getColor(getApplicationContext(), R.color.DarkNight)
        );
        contentView.setTextColor(
                ContextCompat.getColor(getApplicationContext(), R.color.LightNight)
        );
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(
                    ContextCompat.getColor(getApplicationContext(), R.color.LightNight)
            );
        }
        if (isMono) {
            contentView.setTypeface(Typeface.MONOSPACE);
            btn.setTypeface(Typeface.MONOSPACE);
        } else {
            contentView.setTypeface(Typeface.SANS_SERIF);
            btn.setTypeface(Typeface.SANS_SERIF);
        }
    }

    void themeC64() {
        themeNr = 3;
        isMono = true;
        contentView.setTypeface(Typeface.MONOSPACE);
        mainView.setBackgroundColor(
                ContextCompat.getColor(getApplicationContext(), R.color.LightRetro)
        );
        btn.setTextColor(Color.WHITE);
        contentView.setBackgroundColor(
                ContextCompat.getColor(getApplicationContext(), R.color.DarkRetro)
        );
        contentView.setTextColor(
                ContextCompat.getColor(getApplicationContext(), R.color.LightRetro)
        );
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(
                    ContextCompat.getColor(getApplicationContext(), R.color.LightRetro)
            );
        }
        contentView.setTypeface(c64Font);
        btn.setTypeface(c64Font);
    }

    void themeGreen() {
        themeNr = 4;
        isMono = true;
        contentView.setTypeface(Typeface.MONOSPACE);
        mainView.setBackgroundColor(
                ContextCompat.getColor(getApplicationContext(), R.color.LightGreen)
        );
        btn.setTextColor(Color.WHITE);
        contentView.setBackgroundColor(
                ContextCompat.getColor(getApplicationContext(), R.color.DarkGreen)
        );
        contentView.setTextColor(
                ContextCompat.getColor(getApplicationContext(), R.color.LightGreen)
        );
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(
                    ContextCompat.getColor(getApplicationContext(), R.color.LightGreen)
            );
        }
        contentView.setTypeface(c64Font);
        btn.setTypeface(c64Font);
    }

    void themePanther() {
        themeNr = 5;
        isMono = true;
        contentView.setTypeface(Typeface.MONOSPACE);
        mainView.setBackgroundColor(
                ContextCompat.getColor(getApplicationContext(), R.color.LightPanther)
        );
        btn.setTextColor(Color.BLACK);
        contentView.setBackgroundColor(
                ContextCompat.getColor(getApplicationContext(), R.color.DarkPanther)
        );
        contentView.setTextColor(
                ContextCompat.getColor(getApplicationContext(), R.color.LightPanther)
        );
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(
                    ContextCompat.getColor(getApplicationContext(), R.color.MiddlePanther)
            );
        }
        contentView.setTypeface(intuitiveFont);
        btn.setTypeface(intuitiveFont);
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.d(App.PACKAGE_NAME, "onPause()");

        if (data != null && autoSave && !fromExtern && PermissionUtils.writeGranted(this)) {
            String path = data.getPath();
            if (path != null) {
                try {
                    path = PathUtil.getPath(getApplicationContext(), data);
                    File file = new File(path);
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(contentView.getText().toString().getBytes());
                    fos.flush();
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(
                            getApplicationContext(),
                            getString(R.string.errWrite) + "\n" + path,
                            Toast.LENGTH_LONG
                    ).show();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(App.PACKAGE_NAME, "onResume()");

        if (data != null && autoSave && !fromExtern && PermissionUtils.readGranted(this)) {
            String path = data.getPath();
            if (path != null) {
                try {
                    path = PathUtil.getPath(getApplicationContext(), data);
                    File file = new File(path);

                    BufferedReader bufferedReader = new BufferedReader(
                            new InputStreamReader(new FileInputStream(file))
                    );
                    String text = "";
                    while (bufferedReader.ready()) {
                        text += bufferedReader.readLine() + "\n";
                    }
                    contentView.setText(text);

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(
                            getApplicationContext(),
                            getString(R.string.errRead) + "\n" + path,
                            Toast.LENGTH_LONG
                    ).show();
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        final TextView textBox = (TextView) findViewById(R.id.editText);
        CharSequence userText = textBox.getText();
        outState.putCharSequence("savedText", userText);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        final TextView textBox = (TextView) findViewById(R.id.editText);
        CharSequence userText = savedInstanceState.getCharSequence("savedText");
        textBox.setText(userText);
    }

    public void clickSaveButton(View v) {
        if (PermissionUtils.writeGranted(this)) {
            saveNow();
        } else {
            String[] permissions = new String[]{permission.WRITE_EXTERNAL_STORAGE, permission.READ_EXTERNAL_STORAGE};
            PermissionUtils.requestPermissions(this, App.WRITE_PERMISSION_REQ, permissions);
        }
    }

    void saveNow() {
        if (data != null) {
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
                if (!file.exists()) {
                    Log.d(App.PACKAGE_NAME, "on save mkdirs()");
                    file.mkdirs();
                }
                file = new File(path);
                if (!file.exists()) file.createNewFile();
            } catch (Exception e) {
                Toast.makeText(
                        getApplicationContext(),
                        getString(R.string.errAccess) + "\n" + data.getPath(),
                        Toast.LENGTH_LONG
                ).show();
            }

            path = data.getPath();
            if (path != null) {
                try {
                    Log.d(App.PACKAGE_NAME, "saveNow()");
                    path = PathUtil.getPath(getApplicationContext(), data);
                    file = new File(path);
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(contentView.getText().toString().getBytes());
                    fos.flush();
                    fos.close();
                    Toast.makeText(
                            getApplicationContext(),
                            getString(R.string.ok) + "\n" + data.getPath(),
                            Toast.LENGTH_SHORT
                    ).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(
                            getApplicationContext(),
                            getString(R.string.errWrite) + "\n" + path,
                            Toast.LENGTH_LONG
                    ).show();
                }
            }
            Intent i = new Intent("click.dummer.textthing.widget.UPDATE");
            sendBroadcast(i);
        }
    }

    void loadNow() {
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
            if (!file.exists()) {
                Log.d(App.PACKAGE_NAME, "on load mkdirs()");
                file.mkdirs();
            }
            file = new File(path);
            if (!file.exists()) file.createNewFile();
            data = Uri.fromFile(file);

            path = data.getPath();
            if (path != null) {
                try {
                    Log.d(App.PACKAGE_NAME, "loadNow()");
                    path = PathUtil.getPath(getApplicationContext(), data);
                    file = new File(path);

                    BufferedReader bufferedReader = new BufferedReader(
                            new InputStreamReader(new FileInputStream(file))
                    );
                    String text = "";
                    while (bufferedReader.ready()) {
                        text += bufferedReader.readLine() + "\n";
                    }
                    contentView.setText(text);

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(
                            getApplicationContext(),
                            getString(R.string.errRead) + "\n" + path,
                            Toast.LENGTH_LONG
                    ).show();
                }
            }

        } catch (Exception e) {
            if (data != null) {
                Toast.makeText(
                        getApplicationContext(),
                        getString(R.string.errAccess) + "\n" + data.getPath(),
                        Toast.LENGTH_LONG
                ).show();
            } else {
                Toast.makeText(
                        getApplicationContext(),
                        getString(R.string.errAccess),
                        Toast.LENGTH_LONG
                ).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean granted = false;
        switch (requestCode) {
            case App.READ_PERMISSION_REQ:
                granted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (granted) {
                    loadNow();
                } else {
                    //nobody knows what to do
                }
                break;
            case App.WRITE_PERMISSION_REQ:
                granted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (granted) {
                    saveNow();
                } else {
                    //nobody knows what to do
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(requestCode == FILEREQCODE && resultCode == RESULT_OK) {

            if (intent != null) {
                data = intent.getData();
                fromExtern = true;

                Log.d(App.PACKAGE_NAME, "intent.getData() is not Null - Use App via filemanager?");
                try {
                    InputStream input = getContentResolver().openInputStream(data);
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input));

                    String text = "";
                    while (bufferedReader.ready()) {
                        text += bufferedReader.readLine() + "\n";
                    }
                    contentView.setText(text);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
