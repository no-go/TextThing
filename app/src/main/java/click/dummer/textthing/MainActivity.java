package click.dummer.textthing;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
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
import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {
    private static final String PROJECT_LINK = "http://no-go.github.io/TextThing/";
    private static final String FLATTR_ID = "o6wo7q";
    private String FLATTR_LINK;

    private SharedPreferences mPreferences;
    private boolean isMono;
    private boolean autoSave;
    private int themeNr;
    private Uri data = null;
    private TextView contentView;
    private Button btn;
    private PopupMenu popup;
    private ViewGroup mainView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPreferences = getPreferences(MODE_PRIVATE);

        try {
            FLATTR_LINK = "https://flattr.com/submit/auto?fid="+FLATTR_ID+"&url="+
                    java.net.URLEncoder.encode(PROJECT_LINK, "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        contentView = (TextView) findViewById(R.id.editText);
        mainView = (ViewGroup) findViewById(R.id.mainView);
        btn = (Button) findViewById(R.id.optnBtn);

        float fontSize = mPreferences.getFloat(App.PREF_Size, App.DEFAULT_Size);
        isMono = mPreferences.getBoolean(App.PREF_Mono, App.DEFAULT_Mono);
        autoSave = mPreferences.getBoolean(App.PREF_AutoSave, App.DEFAULT_AutoSave);
        themeNr = mPreferences.getInt(App.PREF_Theme, App.DEFAULT_Theme);
        contentView.setTextSize(fontSize);
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
                        fontSize = fontSize * 0.8f;
                        break;
                    case R.id.biggerSize:
                        fontSize = fontSize * 1.2f;
                        break;
                    case R.id.saveNow:
                        saveNow();
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

                    case R.id.theme_day:
                        item.setChecked(true);
                        themeDay();
                        break;

                    case R.id.theme_night:
                        item.setChecked(true);
                        themeNight();
                        break;

                    case R.id.action_flattr:
                        Intent intentFlattr = new Intent(Intent.ACTION_VIEW, Uri.parse(FLATTR_LINK));
                        startActivity(intentFlattr);
                        break;
                    case R.id.action_project:
                        Intent intentProj = new Intent(Intent.ACTION_VIEW, Uri.parse(PROJECT_LINK));
                        startActivity(intentProj);
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
                editor.apply();
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
                if (themeNr == 0) mi0.setChecked(true);
                if (themeNr == 1) mi1.setChecked(true);
                if (themeNr == 2) mi2.setChecked(true);
                mi.setChecked(isMono);
                mi3.setChecked(autoSave);
                popup.show();
            }
        });

        Intent intent = getIntent();
        data = intent.getData();
        if (data != null) {
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
                file.mkdirs();
                file = new File(path);
                if (!file.exists()) file.createNewFile();
                data = Uri.fromFile(file);
                loadNow();
            } catch (Exception e) {
                Toast.makeText(
                        getApplicationContext(),
                        getString(R.string.errAccess) + "\n" + data.getPath(),
                        Toast.LENGTH_LONG
                ).show();
            }
        }
    }

    void themeRetro() {
        themeNr = 0;
        mainView.setBackgroundColor(
                ContextCompat.getColor(getApplicationContext(), R.color.LightRetro)
        );
        btn.setTextColor(Color.BLACK);
        contentView.setBackgroundColor(
                ContextCompat.getColor(getApplicationContext(), R.color.DarkRetro)
        );
        contentView.setTextColor(
                ContextCompat.getColor(getApplicationContext(), R.color.LightRetro)
        );
    }

    void themeDay() {
        themeNr = 1;
        mainView.setBackgroundColor(
                ContextCompat.getColor(getApplicationContext(), R.color.MiddleDay)
        );
        btn.setTextColor(Color.BLACK);
        contentView.setBackgroundColor(
                ContextCompat.getColor(getApplicationContext(), R.color.DarkDay)
        );
        contentView.setTextColor(
                ContextCompat.getColor(getApplicationContext(), R.color.LightDay)
        );
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
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (data != null && autoSave) {
            try {
                File file = new File(data.getPath());
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(contentView.getText().toString().getBytes());
                fos.flush();
                fos.close();
            } catch (Exception e) {
                Toast.makeText(
                        getApplicationContext(),
                        getString(R.string.errWrite) + "\n" + data.getPath(),
                        Toast.LENGTH_LONG
                ).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (data != null && autoSave) {
            try {
                File file = new File(data.getPath());

                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(new FileInputStream(file))
                );
                String text = "";
                while (bufferedReader.ready()) {
                    text += bufferedReader.readLine() + "\n";
                }
                contentView.setText(text);

            } catch (Exception e) {
                Toast.makeText(
                        getApplicationContext(),
                        getString(R.string.errRead) + "\n" + data.getPath(),
                        Toast.LENGTH_LONG
                ).show();
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

    void saveNow() {
        if (data != null) {
            try {
                File file = new File(data.getPath());
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
                Toast.makeText(
                        getApplicationContext(),
                        getString(R.string.errWrite) + "\n" + data.getPath(),
                        Toast.LENGTH_LONG
                ).show();
            }
        }
    }

    void loadNow() {
        try {
            File file = new File(data.getPath());

            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file))
            );
            String text = "";
            while (bufferedReader.ready()) {
                text += bufferedReader.readLine() + "\n";
            }
            contentView.setText(text);

        } catch (Exception e) {
            Toast.makeText(
                    getApplicationContext(),
                    getString(R.string.errRead) + "\n" + data.getPath(),
                    Toast.LENGTH_LONG
            ).show();
        }
    }
}
