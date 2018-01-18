package com.tenday.go;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;

import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.util.Log;

public class Game extends Activity implements View.OnClickListener {
    Bitmap bBitmap, wBitmap, bpBitmap, wpBitmap, bDot, wDot, wDotOnB, bDotOnW, bLast, wLast, wDotOnBLast, bDotOnWLast, none;
    ImageButton[][] ncArr;
    Button btnOk, btnCancel;
    private int[][] intArr, intArrTerritory;
    int ruleKo[];
    boolean m = false, passCheck = true, endGame, bot, botColor=false, testBot = false;
    int cord = 0, cordLast = 0;
    float komi;
    private static final String TAG = "myLogs";
    TextView status, textBlack, textWhite, pass, menu, title, text;
    Dialog dialog;
    final int DIALOG_EXIT = 1;
    final int DIALOG_END = 2;
    int n;

    public float getKomi(){
        return komi;
    }

    int kolvoHodov;


    private Board board;
    SoundPool sp;
    int soundIdClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        bot = intent.getBooleanExtra("bot", true);
        n = intent.getIntExtra("n", 0);
        komi = intent.getFloatExtra("wScore", 0);
        Log.d(TAG, "komi="+komi);
        Log.d(TAG, "n="+n);

        if (n==9)
            setContentView(R.layout.activity_game);
        else if (n==11)
            setContentView(R.layout.activity_game2);
        else
            setContentView(R.layout.activity_game3);

        board = new Board(n, komi);

        status = (TextView) findViewById(R.id.status);
        textBlack = (TextView) findViewById(R.id.black);
        textWhite = (TextView) findViewById(R.id.white);
        pass = (TextView) findViewById(R.id.pass);
        pass.setOnClickListener(this);
        menu = (TextView) findViewById(R.id.menu);
        menu.setOnClickListener(this);

        pass.setText(R.string.btn_pass);
        textBlack.setText(getResources().getString(R.string.score_black)+" 0");
        textWhite.setText(getResources().getString(R.string.score_white)+" 0");
        if(komi!=0)
            textWhite.setText(getResources().getString(R.string.score_white)+" "+komi);
        status.setText(R.string.move_black);
        menu.setText(R.string.btn_menu);

        ncArr = new ImageButton[n][n];

        for (int i = 0; i < n; i++){
            for (int j = 0; j < n; j++){
                if (j < 9 && i < 9)
                    ncArr[i][j] = (ImageButton) findViewById(getResources().getIdentifier("imageButton"+(i*9+j+1), "id", getPackageName()));
                else if (j < 11 && i < 9)
                    ncArr[i][j] = (ImageButton) findViewById(getResources().getIdentifier("imageButton"+(9*9+i*2+j%9+1), "id", getPackageName()));
                else if (j < 11 && i < 11)
                    ncArr[i][j] = (ImageButton) findViewById(getResources().getIdentifier("imageButton"+(i*11+j+1), "id", getPackageName()));
                else if (j < 13 && i < 11)
                    ncArr[i][j] = (ImageButton) findViewById(getResources().getIdentifier("imageButton"+(11*11+i*2+j%11+1), "id", getPackageName()));
                else if (j < 13 && i < 13)
                    ncArr[i][j] = (ImageButton) findViewById(getResources().getIdentifier("imageButton"+(i*13+j+1), "id", getPackageName()));
            }
        }

        ruleKo = new int[2];
        ruleKo[1] = 0;
        intArr = new int[n+2][n+2];
        intArrTerritory = new int[n+2][n+2];
        for (int i = 0; i < n+2; i++) {
            for (int j = 0; j < n+2; j++) {
                if (i == 0 || j == 0 || i==n+1 || j==n+1) {
                    intArr[i][j] = -1;
                    intArrTerritory[i][j] = -1;
                }
                else {
                    intArr[i][j] = 0;
                    intArrTerritory[i][j] = 0;
                }
            }
        }

        bBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b);
        wBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.w);
        bpBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bp);
        wpBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.wp);
        bDot = BitmapFactory.decodeResource(getResources(), R.drawable.bdot);
        wDot = BitmapFactory.decodeResource(getResources(), R.drawable.wdot);
        bDotOnW = BitmapFactory.decodeResource(getResources(), R.drawable.bdotonw);
        wDotOnB = BitmapFactory.decodeResource(getResources(), R.drawable.wdotonb);
        bLast = BitmapFactory.decodeResource(getResources(), R.drawable.blast);
        wLast = BitmapFactory.decodeResource(getResources(), R.drawable.wlast);
        bDotOnWLast = BitmapFactory.decodeResource(getResources(), R.drawable.bdotonwlast);
        wDotOnBLast = BitmapFactory.decodeResource(getResources(), R.drawable.wdotonblast);
        none = BitmapFactory.decodeResource(getResources(), R.drawable.none);

        for (int i = 0; i < n; i++) {
            for(int j = 0; j < n; j++) {
                ncArr[i][j].setOnClickListener(this);
            }
        }
    }

    public void move(int i, int j) {

        if (cord == i*100+j) {
            board.move(i, j);

            cord = 0;
            if (!m) {
                ncArr[i-1][j-1].setImageBitmap(bLast);
                cordLast = 10000 + i * 100 + j;
                m = true;
                status.setText(R.string.move_white);
            } else {
                ncArr[i-1][j-1].setImageBitmap(wLast);
                cordLast = 20000 + i * 100 + j;
                m = false;
                status.setText(R.string.move_black);
            }
            intArrTerritory = board.scoring();

            textBlack.setText(getResources().getString(R.string.score_black) + board.bTotalScore);
            if (komi!=0)
                textWhite.setText(getResources().getString(R.string.score_white) + (board.wTotalScore+komi));
            else
                textWhite.setText(getResources().getString(R.string.score_white) + board.wTotalScore);

            intArr = board.getBoard();
            for (int i1 = 1; i1 < n+1; i1++) {
                for (int j1 = 1; j1 < n+1; j1++) {
                    if (intArrTerritory[i1][j1] == 1 && intArr[i1][j1] == 0)
                        ncArr[i1-1][j1-1].setImageBitmap(bDot);
                    else if (20000 + i1 * 100 + j1 != cordLast && intArrTerritory[i1][j1] == 1 && intArr[i1][j1]/10000000 == 2)
                        ncArr[i1-1][j1-1].setImageBitmap(bDotOnW);
                    else if (20000 + i1 * 100 + j1 == cordLast && intArrTerritory[i1][j1] == 1 && intArr[i1][j1]/10000000 == 2)
                        ncArr[i1-1][j1-1].setImageBitmap(bDotOnWLast);
                    else if (intArrTerritory[i1][j1] == 2 && intArr[i1][j1] == 0)
                        ncArr[i1-1][j1-1].setImageBitmap(wDot);
                    else if (10000 + i1 * 100 + j1 != cordLast && intArrTerritory[i1][j1] == 2 && intArr[i1][j1]/10000000 == 1)
                        ncArr[i1-1][j1-1].setImageBitmap(wDotOnB);
                    else if (10000 + i1 * 100 + j1 == cordLast && intArrTerritory[i1][j1] == 2 && intArr[i1][j1]/10000000 == 1)
                        ncArr[i1-1][j1-1].setImageBitmap(wDotOnBLast);
                    else if (10000 + i1 * 100 + j1 != cordLast && intArr[i1][j1]/10000000 == 1)
                        ncArr[i1-1][j1-1].setImageBitmap(bBitmap);
                    else if (20000 + i1 * 100 + j1 != cordLast && intArr[i1][j1]/10000000 == 2)
                        ncArr[i1-1][j1-1].setImageBitmap(wBitmap);
                    else if (intArr[i1][j1] == 0 && intArrTerritory[i1][j1] == 0)
                        ncArr[i1-1][j1-1].setImageBitmap(none);

                }
            }

            pass.setText(getResources().getString(R.string.btn_pass));

        } else if (cord != i * 100 + j && board.checkMove(i,j)) {
            if (cord != 0) {
                ncArr[cord / 100 - 1][cord % 100 - 1].setImageBitmap(none);
            }
            cord = i * 100 + j;
            if (m)
                ncArr[i - 1][j - 1].setImageBitmap(wpBitmap);
            else
                ncArr[i - 1][j - 1].setImageBitmap(bpBitmap);
        } else if (cord != 0 && cord != i * 100 + j && !board.checkMove(i,j)) {
            ncArr[cord / 100 - 1][cord % 100 - 1].setImageBitmap(none);
        }
    }

    //}


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageButton1: move(1, 1); break;
            case R.id.imageButton2: move(1, 2); break;
            case R.id.imageButton3: move(1, 3); break;
            case R.id.imageButton4: move(1, 4); break;
            case R.id.imageButton5: move(1, 5); break;
            case R.id.imageButton6: move(1, 6); break;
            case R.id.imageButton7: move(1, 7); break;
            case R.id.imageButton8: move(1, 8); break;
            case R.id.imageButton9: move(1, 9); break;
            case R.id.imageButton82: move(1, 10); break;
            case R.id.imageButton83: move(1, 11); break;
            case R.id.imageButton122: move(1, 12); break;
            case R.id.imageButton123: move(1, 13); break;
            case R.id.imageButton10: move(2, 1); break;
            case R.id.imageButton11: move(2, 2); break;
            case R.id.imageButton12: move(2, 3); break;
            case R.id.imageButton13: move(2, 4); break;
            case R.id.imageButton14: move(2, 5); break;
            case R.id.imageButton15: move(2, 6); break;
            case R.id.imageButton16: move(2, 7); break;
            case R.id.imageButton17: move(2, 8); break;
            case R.id.imageButton18: move(2, 9); break;
            case R.id.imageButton84: move(2, 10); break;
            case R.id.imageButton85: move(2, 11); break;
            case R.id.imageButton124: move(2, 12); break;
            case R.id.imageButton125: move(2, 13); break;
            case R.id.imageButton19: move(3, 1); break;
            case R.id.imageButton20: move(3, 2); break;
            case R.id.imageButton21: move(3, 3); break;
            case R.id.imageButton22: move(3, 4); break;
            case R.id.imageButton23: move(3, 5); break;
            case R.id.imageButton24: move(3, 6); break;
            case R.id.imageButton25: move(3, 7); break;
            case R.id.imageButton26: move(3, 8); break;
            case R.id.imageButton27: move(3, 9); break;
            case R.id.imageButton86: move(3, 10); break;
            case R.id.imageButton87: move(3, 11); break;
            case R.id.imageButton126: move(3, 12); break;
            case R.id.imageButton127: move(3, 13); break;
            case R.id.imageButton28: move(4, 1); break;
            case R.id.imageButton29: move(4, 2); break;
            case R.id.imageButton30: move(4, 3); break;
            case R.id.imageButton31: move(4, 4); break;
            case R.id.imageButton32: move(4, 5); break;
            case R.id.imageButton33: move(4, 6); break;
            case R.id.imageButton34: move(4, 7); break;
            case R.id.imageButton35: move(4, 8); break;
            case R.id.imageButton36: move(4, 9); break;
            case R.id.imageButton88: move(4, 10); break;
            case R.id.imageButton89: move(4, 11); break;
            case R.id.imageButton128: move(4, 12); break;
            case R.id.imageButton129: move(4, 13); break;
            case R.id.imageButton37: move(5, 1); break;
            case R.id.imageButton38: move(5, 2); break;
            case R.id.imageButton39: move(5, 3); break;
            case R.id.imageButton40: move(5, 4); break;
            case R.id.imageButton41: move(5, 5); break;
            case R.id.imageButton42: move(5, 6); break;
            case R.id.imageButton43: move(5, 7); break;
            case R.id.imageButton44: move(5, 8); break;
            case R.id.imageButton45: move(5, 9); break;
            case R.id.imageButton90: move(5, 10);break;
            case R.id.imageButton91: move(5, 11); break;
            case R.id.imageButton130: move(5, 12); break;
            case R.id.imageButton131: move(5, 13); break;
            case R.id.imageButton46: move(6, 1); break;
            case R.id.imageButton47: move(6, 2); break;
            case R.id.imageButton48: move(6, 3); break;
            case R.id.imageButton49: move(6, 4); break;
            case R.id.imageButton50: move(6, 5); break;
            case R.id.imageButton51: move(6, 6); break;
            case R.id.imageButton52: move(6, 7); break;
            case R.id.imageButton53: move(6, 8); break;
            case R.id.imageButton54: move(6, 9); break;
            case R.id.imageButton92: move(6, 10); break;
            case R.id.imageButton93: move(6, 11); break;
            case R.id.imageButton132: move(6, 12); break;
            case R.id.imageButton133: move(6, 13); break;
            case R.id.imageButton55: move(7, 1); break;
            case R.id.imageButton56: move(7, 2); break;
            case R.id.imageButton57: move(7, 3); break;
            case R.id.imageButton58: move(7, 4); break;
            case R.id.imageButton59: move(7, 5); break;
            case R.id.imageButton60: move(7, 6); break;
            case R.id.imageButton61: move(7, 7); break;
            case R.id.imageButton62: move(7, 8); break;
            case R.id.imageButton63: move(7, 9); break;
            case R.id.imageButton94: move(7, 10); break;
            case R.id.imageButton95: move(7, 11); break;
            case R.id.imageButton134: move(7, 12); break;
            case R.id.imageButton135: move(7, 13); break;
            case R.id.imageButton64: move(8, 1); break;
            case R.id.imageButton65: move(8, 2); break;
            case R.id.imageButton66: move(8, 3); break;
            case R.id.imageButton67: move(8, 4); break;
            case R.id.imageButton68: move(8, 5); break;
            case R.id.imageButton69: move(8, 6); break;
            case R.id.imageButton70: move(8, 7); break;
            case R.id.imageButton71: move(8, 8); break;
            case R.id.imageButton72: move(8, 9); break;
            case R.id.imageButton96: move(8, 10); break;
            case R.id.imageButton97: move(8, 11); break;
            case R.id.imageButton136: move(8, 12); break;
            case R.id.imageButton137: move(8, 13); break;
            case R.id.imageButton73: move(9, 1); break;
            case R.id.imageButton74: move(9, 2); break;
            case R.id.imageButton75: move(9, 3); break;
            case R.id.imageButton76: move(9, 4); break;
            case R.id.imageButton77: move(9, 5);break;
            case R.id.imageButton78: move(9, 6); break;
            case R.id.imageButton79: move(9, 7); break;
            case R.id.imageButton80: move(9, 8); break;
            case R.id.imageButton81: move(9, 9); break;
            case R.id.imageButton98: move(9, 10); break;
            case R.id.imageButton99: move(9, 11); break;
            case R.id.imageButton138: move(9, 12); break;
            case R.id.imageButton139: move(9, 13); break;
            case R.id.imageButton100: move(10, 1); break;
            case R.id.imageButton101: move(10, 2); break;
            case R.id.imageButton102: move(10, 3); break;
            case R.id.imageButton103: move(10, 4); break;
            case R.id.imageButton104: move(10, 5); break;
            case R.id.imageButton105: move(10, 6); break;
            case R.id.imageButton106: move(10, 7); break;
            case R.id.imageButton107: move(10, 8); break;
            case R.id.imageButton108: move(10, 9); break;
            case R.id.imageButton109: move(10, 10);break;
            case R.id.imageButton110: move(10, 11); break;
            case R.id.imageButton140: move(10, 12); break;
            case R.id.imageButton141: move(10, 13); break;
            case R.id.imageButton111: move(11, 1); break;
            case R.id.imageButton112: move(11, 2); break;
            case R.id.imageButton113: move(11, 3); break;
            case R.id.imageButton114: move(11, 4); break;
            case R.id.imageButton115: move(11, 5); break;
            case R.id.imageButton116: move(11, 6); break;
            case R.id.imageButton117: move(11, 7); break;
            case R.id.imageButton118: move(11, 8); break;
            case R.id.imageButton119: move(11, 9); break;
            case R.id.imageButton120: move(11, 10); break;
            case R.id.imageButton121: move(11, 11); break;
            case R.id.imageButton142: move(11, 12); break;
            case R.id.imageButton143: move(11, 13); break;
            case R.id.imageButton144: move(12, 1); break;
            case R.id.imageButton145: move(12, 2); break;
            case R.id.imageButton146: move(12, 3); break;
            case R.id.imageButton147: move(12, 4); break;
            case R.id.imageButton148: move(12, 5); break;
            case R.id.imageButton149: move(12, 6); break;
            case R.id.imageButton150: move(12, 7); break;
            case R.id.imageButton151: move(12, 8); break;
            case R.id.imageButton152: move(12, 9); break;
            case R.id.imageButton153: move(12, 10); break;
            case R.id.imageButton154: move(12, 11); break;
            case R.id.imageButton155: move(12, 12); break;
            case R.id.imageButton156: move(12, 13); break;
            case R.id.imageButton157: move(13, 1); break;
            case R.id.imageButton158: move(13, 2); break;
            case R.id.imageButton159: move(13, 3); break;
            case R.id.imageButton160: move(13, 4); break;
            case R.id.imageButton161: move(13, 5); break;
            case R.id.imageButton162: move(13, 6); break;
            case R.id.imageButton163: move(13, 7); break;
            case R.id.imageButton164: move(13, 8); break;
            case R.id.imageButton165: move(13, 9); break;
            case R.id.imageButton166: move(13, 10); break;
            case R.id.imageButton167: move(13, 11); break;
            case R.id.imageButton168: move(13, 12); break;
            case R.id.imageButton169: move(13, 13); break;

            case R.id.pass:
                pass();
                break;

            case R.id.btnOk:
                btnOk();
                break;

            case R.id.btnCancel:
                if (endGame)
                    dismissDialog(DIALOG_END);
                else
                    dismissDialog(DIALOG_EXIT);
                break;
            case R.id.menu:
                endGame = false;
                showDialog(DIALOG_EXIT);
                break;
        }

    }

    public void btnOk(){
        if (endGame){
            dismissDialog(DIALOG_END);
            pass.setText(getResources().getString(R.string.btn_new));
            board.scoring();
            if (cordLast/10000 == 1 && intArrTerritory[(cordLast%10000)/100][cordLast%100] != 2)
                ncArr[(cordLast%10000)/100-1][cordLast%100-1].setImageBitmap(bBitmap);
            else if (cordLast/10000 == 1 && intArrTerritory[(cordLast%10000)/100][cordLast%100] == 2)
                ncArr[(cordLast%10000)/100-1][cordLast%100-1].setImageBitmap(wDotOnB);
            else if (cordLast/10000 == 2 && intArrTerritory[(cordLast%10000)/100][cordLast%100] != 1)
                ncArr[(cordLast%10000)/100-1][cordLast%100-1].setImageBitmap(wBitmap);
            else if (cordLast/10000 == 2 && intArrTerritory[(cordLast%10000)/100][cordLast%100] == 1)
                ncArr[(cordLast%10000)/100-1][cordLast%100-1].setImageBitmap(bDotOnW);

            if (board.bTotalScore > board.wTotalScore+komi)
                status.setText(R.string.victory_black);
            else if (board.bTotalScore < board.wTotalScore+komi)
                status.setText(R.string.victory_white);
            else
                status.setText(R.string.draw);
            if (!(bot && botColor == m || testBot))
                btnOk.setOnClickListener(this);
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++)
                    ncArr[i][j].setEnabled(false);
        }
        else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    public void pass(){
        if(pass.getText() == getResources().getString(R.string.btn_pass)) {
            if (cord>0 && intArrTerritory[cord/100][cord%100] == 0)
                ncArr[cord/100-1][cord%100-1].setImageBitmap(none);
            else if (cord>0 && intArrTerritory[cord/100][cord%100] == 1)
                ncArr[cord/100-1][cord%100-1].setImageBitmap(bDot);
            else if (cord>0 && intArrTerritory[cord/100][cord%100] == 2)
                ncArr[cord/100-1][cord%100-1].setImageBitmap(wDot);
            cord = 0;
            board.pass();
            pass.setText(getResources().getString(R.string.btn_end));
            passCheck = false;
            if (m == true) {
                m = false;
                status.setText(R.string.move_black);
            } else {
                m = true;
                status.setText(R.string.move_white);
            }

        }
        else if(pass.getText() == getResources().getString(R.string.btn_end)){
            endGame = true;
            showDialog(DIALOG_END);
        }
        else{
            board.newGame();
            status.setText(R.string.move_black);
            endGame = false;
            cordLast = 0;
            m = false;
            textBlack.setText(getResources().getString(R.string.score_black) + " 0");
            if (komi == 0)
                textWhite.setText(getResources().getString(R.string.score_white) +  " 0");
            else
                textWhite.setText(getResources().getString(R.string.score_white) +  " " + komi);
            for (int i=1; i < n+1; i++){
                for (int j=1; j < n+1; j++) {
                    ncArr[i - 1][j - 1].setImageBitmap(none);
                    ncArr[i - 1][j - 1].setEnabled(true);
                }
            }
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_menu, null);
        title = (TextView) view.findViewById(R.id.title);
        text = (TextView) view.findViewById(R.id.textMenu);
        btnOk = (Button) view.findViewById(R.id.btnOk);
        btnCancel = (Button) view.findViewById(R.id.btnCancel);
        title.setText(R.string.end_game);
        text.setText(R.string.end_text);
        btnOk.setText(R.string.btnOk);
        btnOk.setOnClickListener(this);
        btnCancel.setText(R.string.btnCancel);
        btnCancel.setOnClickListener(this);
        dialog = adb.create();
        if (id == DIALOG_EXIT) {
            title.setText(R.string.main_menu);
            text.setText(R.string.menu_text);
            adb.setView(view);
            return adb.create();
        }
        if (id == DIALOG_END){
            title.setText(R.string.end_game);
            text.setText(R.string.end_text);
            adb.setView(view);
            return adb.create();
        }
        return super.onCreateDialog(id);
    }

}