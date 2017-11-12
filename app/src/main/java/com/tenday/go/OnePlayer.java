package com.tenday.go;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class OnePlayer extends Activity implements View.OnClickListener {

    TextView btnPlay;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.one_player);

        btnPlay = (TextView) findViewById(R.id.Play);
        btnPlay.setOnClickListener(this);

        btnPlay.setText(R.string.play);
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.Play:
                Intent intent = new Intent(this, Game.class);
                intent.putExtra("bot", true);
                intent.putExtra("n", 9);
                intent.putExtra("wScore", 0);
                startActivity(intent);
        }
    }
}
