package click.dummer.textthing;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
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

public class MainActivity extends Activity {
    private SharedPreferences mPreferences;
    private boolean isMono;
    private Uri data = null;
    private TextView contentView;
    private Button btn;
    private PopupMenu popup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPreferences = getPreferences(MODE_PRIVATE);

        contentView = (TextView) findViewById(R.id.editText);
        btn = (Button) findViewById(R.id.optnBtn);
        popup = new PopupMenu(MainActivity.this, btn);

        float fontSize = mPreferences.getFloat(App.PREF_Size, App.DEFAULT_Size);
        isMono = mPreferences.getBoolean(App.PREF_Mono, App.DEFAULT_Mono);
        contentView.setTextSize(fontSize);
        if (isMono == true) {
            contentView.setTypeface(Typeface.MONOSPACE);
        } else {
            contentView.setTypeface(Typeface.SANS_SERIF);
        }

        popup.dismiss();
        popup.getMenuInflater().inflate(R.menu.fontsel, popup.getMenu());
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
                    default:
                        return false;
                }
                contentView.setTextSize(fontSize);
                SharedPreferences.Editor editor = mPreferences.edit();
                editor.putFloat(App.PREF_Size, fontSize);
                editor.putBoolean(App.PREF_Mono, isMono);
                editor.apply();
                return true;
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MenuItem mi = popup.getMenu().findItem(R.id.monoStyle);
                mi.setChecked(isMono);
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
            } catch (Exception e) {
                Toast.makeText(
                        getApplicationContext(),
                        getString(R.string.errAccess) + "\n" + data.getPath(),
                        Toast.LENGTH_LONG
                ).show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (data != null) {
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
        if (data != null) {
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
}
