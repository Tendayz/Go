package com.tenday.go;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends Activity implements View.OnClickListener {

    public static final String APP_PREFERENCES = "mysettings";
    public static final String N = "size";
    public static final String K = "komi";
    int test12,test223,qwe,d123e,e42,sdrfssdfsdf;
    private SharedPreferences mSettings;

    TextView btnPlay, size, komi, sizeString, komiString;
    ImageButton sizeLess, sizeMore, komiLess, komiMore;
    int n;
    float wScore;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        setContentView(R.layout.activity_main);

        test223=3;
        n = 9;
        wScore = 0;

        // найдем View-элементы
        sizeString = (TextView) findViewById(R.id.sizeString);
        komiString = (TextView) findViewById(R.id.komiString);
        btnPlay = (TextView) findViewById(R.id.Play);
        size = (TextView) findViewById(R.id.size);
        komi = (TextView) findViewById(R.id.komi);
        sizeLess = (ImageButton) findViewById(R.id.sizeLess);
        sizeMore = (ImageButton) findViewById(R.id.sizeMore);
        komiLess = (ImageButton) findViewById(R.id.komiLess);
        komiMore = (ImageButton) findViewById(R.id.komiMore);

        size.setText(n+"x"+n);
        komi.setText(R.string.none);
        sizeString.setText(R.string.size);
        komiString.setText(R.string.komi);
        btnPlay.setText(R.string.play);

        if (mSettings.contains(N)) {
            // Получаем число из настроек
            n = mSettings.getInt(N, 0);
            // Выводим на экран данные из настроек
            size.setText(n+"x"+n);
        }
        if (mSettings.contains(N)) {
            // Получаем число из настроек
            wScore = mSettings.getFloat(K, 0);
            // Выводим на экран данные из настроек
            if (wScore!=0)
                komi.setText(""+wScore);
            else
                komi.setText(R.string.none);
        }


        // присваиваем обработчик кнопкам
        btnPlay.setOnClickListener(this);
        sizeLess.setOnClickListener(this);
        sizeMore.setOnClickListener(this);
        komiLess.setOnClickListener(this);
        komiMore.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        SharedPreferences.Editor editor = mSettings.edit();
        switch (v.getId()) {
            case R.id.sizeLess:
                if (n!=9) {
                    n -= 2;
                    size.setText(n+"x"+n);
                    editor.putInt(N, n);
                    editor.apply();
                }
                else {
                    n = 13;
                    size.setText(n+"x"+n);
                    editor.putInt(N, n);
                    editor.apply();
                }
                break;
            case R.id.sizeMore:
                if (n!=13) {
                    n += 2;
                    size.setText(n+"x"+n);
                    editor.putInt(N, n);
                    editor.apply();
                }
                else {
                    n = 9;
                    size.setText(n+"x"+n);
                    editor.putInt(N, n);
                    editor.apply();
                }
                break;
            case R.id.komiLess:
                if(wScore != 0 && wScore != 0.5) {
                    wScore--;
                    komi.setText(""+wScore);
                    editor.putFloat(K, wScore);
                    editor.apply();
                }
                else if (wScore == 0.5){
                    wScore = 0;
                    komi.setText(R.string.none);
                    editor.putFloat(K, wScore);
                    editor.apply();
                }
                else{
                    wScore = 9.5f;
                    komi.setText(""+wScore);
                    editor.putFloat(K, wScore);
                    editor.apply();
                }
                break;
            case R.id.komiMore:
                if(wScore != 9.5 && wScore != 0) {
                    wScore++;
                    komi.setText(""+wScore);
                    editor.putFloat(K, wScore);
                    editor.apply();
                }
                else if (wScore == 0){
                    wScore = 0.5f;
                    komi.setText(""+wScore);
                    editor.putFloat(K, wScore);
                    editor.apply();
                }
                else{
                    wScore = 0;
                    komi.setText(R.string.none);
                    editor.putFloat(K, wScore);
                    editor.apply();
                }
                break;
            case R.id.Play:
                // кнопка ОК
                Intent intent = new Intent(this, Game.class);
                intent.putExtra("n", n);
                intent.putExtra("wScore", wScore);
                startActivity(intent);
                break;
        }
    }

}