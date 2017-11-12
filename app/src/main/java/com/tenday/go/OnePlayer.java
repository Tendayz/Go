package com.tenday.go;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class OnePlayer extends Activity implements View.OnClickListener {

    TextView Play;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.one_player);

        Play = (TextView) findViewById(R.id.Play);
        Play.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.Play:
                Intent intent = new Intent(this, Game.class);
                intent.putExtra("bot", true);
                startActivity(intent);
        }
    }
}
