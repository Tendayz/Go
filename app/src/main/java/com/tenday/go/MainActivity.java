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

    TextView onePlayer, twoPlayers;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        onePlayer = (TextView) findViewById(R.id.onePlayer);
        twoPlayers = (TextView) findViewById(R.id.twoPlayers);

        // присваиваем обработчик кнопкам
        onePlayer.setOnClickListener(this);
        twoPlayers.setOnClickListener(this);

        onePlayer.setText(R.string.one_player);
        twoPlayers.setText(R.string.two_players);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.onePlayer:
                intent = new Intent(this, OnePlayer.class);
                startActivity(intent);
                break;
            case R.id.twoPlayers:
                intent = new Intent(this, TwoPlayers.class);
                startActivity(intent);
                break;
        }
    }

}