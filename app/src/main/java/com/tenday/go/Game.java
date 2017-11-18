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

import java.util.HashMap;
import java.util.Map;


public class Game extends Activity implements View.OnClickListener {
    Bitmap bBitmap, wBitmap, bpBitmap, wpBitmap, bDot, wDot, wDotOnB, bDotOnW, bLast, wLast, wDotOnBLast, bDotOnWLast, none;
    ImageButton[][] ncArr;
    Button btnOk, btnCancel;
    int[][] intArr, intArrTerritory;
    int ruleKo[];
    boolean m = false, passCheck = true, move = false, checkMove = false, checkMove2 = false, endGame, bot;
    int zone = 0, zone2 = 0, oldzone = 0, oldzone2 = 0, Cord = 0, bScore = 0, wScore, bTotalScore = 0, wTotalScore = 0;
    float komi;
    private static final String TAG = "myLogs";
    TextView status, textBlack, textWhite, pass, menu, title, text;
    Dialog dialog;
    final int DIALOG_EXIT = 1;
    final int DIALOG_END = 2;
    int n;

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

        sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        //sp.setOnLoadCompleteListener(this);
        soundIdClick = sp.load(this, R.raw.click, 1);

        if (n==9)
            setContentView(R.layout.activity_game);
        else if (n==11)
            setContentView(R.layout.activity_game2);
        else
            setContentView(R.layout.activity_game3);

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
            case R.id.imageButton43:move(5, 7); break;
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

            case R.id.btnOk:
                if (endGame){
                    dismissDialog(DIALOG_END);
                    pass.setText(getResources().getString(R.string.btn_new));
                    scoring();
                    if (bTotalScore > wTotalScore+komi)
                        status.setText(R.string.victory_black);
                    else if (bTotalScore < wTotalScore+komi)
                        status.setText(R.string.victory_white);
                    else
                        status.setText(R.string.draw);
                    btnOk.setOnClickListener(this);
                    for (int i = 0; i < n; i++)
                        for (int j = 0; j < n; j++)
                            ncArr[i][j].setEnabled(false);
                }
                else {
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                }
                break;

            case R.id.btnCancel:
                //dialog.dismiss();
                if (endGame)
                    dismissDialog(DIALOG_END);
                else
                    dismissDialog(DIALOG_EXIT);
                break;

            case R.id.pass:
                if(pass.getText() == getResources().getString(R.string.btn_pass)) {
                    if (Cord != 0) {
                        intArr[Cord / 100][Cord % 100] = 0;
                        if (intArrTerritory[Cord / 100][Cord % 100] == 0)
                            ncArr[Cord / 100 - 1][Cord % 100 - 1].setImageBitmap(none);
                        else if (intArrTerritory[Cord / 100][Cord % 100] == 1)
                            ncArr[Cord / 100 - 1][Cord % 100 - 1].setImageBitmap(bDot);
                        else
                            ncArr[Cord / 100 - 1][Cord % 100 - 1].setImageBitmap(wDot);
                    }
                    Cord = 0;
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
                    status.setText(R.string.move_black);
                    m = false;
                    zone = 0;
                    zone2 = 0;
                    wScore = 0;
                    bScore = 0;
                    ruleKo[0] = 0;
                    ruleKo[1] = 0;
                    textBlack.setText(getResources().getString(R.string.score_black) + " 0");
                    textWhite.setText(getResources().getString(R.string.score_white) + " 0");
                    for (int i=1; i < n+1; i++){
                        for (int j=1; j < n+1; j++) {
                            intArr[i][j] = 0;
                            ncArr[i - 1][j - 1].setImageBitmap(none);
                            ncArr[i - 1][j - 1].setEnabled(true);
                        }
                    }
                    for (int i = 0; i < n+2; i++){
                        for (int j = 0; j < n+2; j++){
                            if (i==0 || j==0 || i==n+1 || j==n+1)
                                intArrTerritory[i][j] = -1;
                            else
                                intArrTerritory[i][j] = 0;
                        }
                    }
                }
                break;
            case R.id.menu:
                endGame = false;
                showDialog(DIALOG_EXIT);
                break;
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

    //Формат номера элемента на доске - X X XXX XXX :
    // цвет;
    // наличие или отсутствие территории у группы;
    // номер группы, окружайщей территорию;
    // номер объединенной группы;
    public void move(int i, int j) {
        if (intArr[i][j] == 0 || Cord/100 == i && Cord%100 == j) {
            if (!m) {
                if (Cord/100 != i || Cord%100 != j){
                    if (Cord != 0) {
                        if (intArrTerritory[Cord / 100][Cord % 100] == 0)
                            ncArr[Cord / 100 - 1][Cord % 100 - 1].setImageBitmap(none);
                        else if (intArrTerritory[Cord / 100][Cord % 100] == 1)
                            ncArr[Cord / 100 - 1][Cord % 100 - 1].setImageBitmap(bDot);
                        else
                            ncArr[Cord / 100 - 1][Cord % 100 - 1].setImageBitmap(wDot);
                        intArr[Cord / 100][Cord % 100] = 0;
                    }
                    ncArr[i - 1][j - 1].setImageBitmap(bpBitmap);
                    Cord = i*100+j;
                }
                else {
                    //sp.play(soundIdClick, 1, 1, 0, 0, 1);
                    Cord = 0;
                    ncArr[i - 1][j - 1].setImageBitmap(bBitmap);
                    m = true;
                    status.setText(R.string.move_white);
                    pass.setText(getResources().getString(R.string.btn_pass));
                    passCheck = true;
                    move = true;
                }
                intArr[i][j] = 10000000;
            } else if (m) {
                if (Cord/100 != i || Cord%100 != j){
                    if (Cord != 0) {
                        if (intArrTerritory[Cord / 100][Cord % 100] == 0)
                            ncArr[Cord / 100 - 1][Cord % 100 - 1].setImageBitmap(none);
                        else if (intArrTerritory[Cord / 100][Cord % 100] == 1)
                            ncArr[Cord / 100 - 1][Cord % 100 - 1].setImageBitmap(bDot);
                        else
                            ncArr[Cord / 100 - 1][Cord % 100 - 1].setImageBitmap(wDot);
                        intArr[Cord / 100][Cord % 100] = 0;
                    }
                    ncArr[i - 1][j - 1].setImageBitmap(wpBitmap);
                    Cord = i*100+j;
                }
                else {
                    //sp.play(soundIdClick, 1, 1, 0, 0, 1);
                    move = true;
                    Cord = 0;
                    ncArr[i - 1][j - 1].setImageBitmap(wBitmap);
                    m = false;
                    status.setText(R.string.move_black);
                    pass.setText(getResources().getString(R.string.btn_pass));
                    passCheck = true;
                }
                intArr[i][j] = 20000000;
            }

            int checkRuleKo = ruleKo[1];
            zone++;
            zone2++;

            intArr[i][j] += zone + zone2*1000;
            //Проверки на стоящие рядом камни противника
            checkMove(i - 1, j, i, j);
            checkMove(i, j + 1, i, j);
            checkMove(i + 1, j, i, j);
            checkMove(i, j - 1, i, j);
            //Проверки на стоящие рядом камни того же цвета
            checkMove2(i - 1, j, i, j);
            checkMove2(i, j + 1, i, j);
            checkMove2(i + 1, j, i, j);
            checkMove2(i, j - 1, i, j);

            if (checkRuleKo == ruleKo[1] && !move && ((ruleKo[1]%100000000)/1000000 != i || (ruleKo[1]%1000000)/10000 != j || ruleKo[1]/100000000 != intArr[i][j]/10000000))
                ruleKo[1] = 0;

            if (move) {
                if (intArr[i + 1][j + 1] / 10000000 == intArr[i][j] / 10000000 && intArr[i][j] != 0 && intArr[i + 1][j + 1] != 0) {
                    oldzone2 = (intArr[i + 1][j + 1] % 1000000) / 1000;
                    intArr[i][j] += oldzone2 * 1000 - zone2 * 1000;
                    for (int i1 = 1; i1 < n + 1; i1++) {
                        for (int j1 = 1; j1 < n + 1; j1++) {
                            if ((intArr[i1][j1] % 1000000) / 1000 == oldzone2)
                                intArr[i1][j1] += zone2 * 1000 - oldzone2 * 1000;
                        }
                    }
                }
                if (intArr[i + 1][j - 1] / 10000000 == intArr[i][j] / 10000000 && intArr[i][j] != 0 && intArr[i + 1][j - 1] != 0) {
                    oldzone2 = (intArr[i + 1][j - 1] % 1000000) / 1000;
                    intArr[i][j] += oldzone2 * 1000 - zone2 * 1000;
                    for (int i1 = 1; i1 < n + 1; i1++) {
                        for (int j1 = 1; j1 < n + 1; j1++) {
                            if ((intArr[i1][j1] % 1000000) / 1000 == oldzone2)
                                intArr[i1][j1] += zone2 * 1000 - oldzone2 * 1000;
                        }
                    }
                }
                if (intArr[i - 1][j + 1] / 10000000 == intArr[i][j] / 10000000 && intArr[i][j] != 0 && intArr[i - 1][j + 1] != 0) {
                    oldzone2 = (intArr[i - 1][j + 1] % 1000000) / 1000;
                    intArr[i][j] += oldzone2 * 1000 - zone2 * 1000;
                    for (int i1 = 1; i1 < n + 1; i1++) {
                        for (int j1 = 1; j1 < n + 1; j1++) {
                            if ((intArr[i1][j1] % 1000000) / 1000 == oldzone2)
                                intArr[i1][j1] += zone2 * 1000 - oldzone2 * 1000;
                        }
                    }
                }
                if (intArr[i - 1][j - 1] / 10000000 == intArr[i][j] / 10000000 && intArr[i][j] != 0 && intArr[i - 1][j - 1] != 0) {
                    oldzone2 = (intArr[i - 1][j - 1] % 1000000) / 1000;
                    intArr[i][j] += oldzone2 * 1000 - zone2 * 1000;
                    for (int i1 = 1; i1 < n + 1; i1++) {
                        for (int j1 = 1; j1 < n + 1; j1++) {
                            if ((intArr[i1][j1] % 1000000) / 1000 == oldzone2)
                                intArr[i1][j1] += zone2 * 1000 - oldzone2 * 1000;
                        }
                    }
                }
            }

            //Если камень окружен с четырех сторон камнями противника и правило Ко
            if (intArr[i][j] != 0 && !checkMove &&
                    (intArr[i+1][j]/10000000 != intArr[i][j]/10000000 && intArr[i+1][j] != 0) &&
                    (intArr[i-1][j]/10000000 != intArr[i][j]/10000000 && intArr[i-1][j] != 0) &&
                    (intArr[i][j+1]/10000000 != intArr[i][j]/10000000 && intArr[i][j+1] != 0) &&
                    (intArr[i][j-1]/10000000 != intArr[i][j]/10000000 && intArr[i][j-1] != 0) ||
                    ruleKo[0] / 100000000 != intArr[i][j]/10000000 && (ruleKo[0] % 100000000)/1000000 == (ruleKo[1] % 10000)/100 && (ruleKo[0] % 1000000)/10000 == ruleKo[1] % 100
                            && (ruleKo[1] % 100000000)/1000000 == (ruleKo[0] % 10000)/100 && (ruleKo[1] % 1000000)/10000 == ruleKo[0] % 100 && ruleKo[0] != 0 && ruleKo[1] != 0){
                intArr[i][j] = 0;
                if (intArrTerritory[i][j] == 0)
                    ncArr[i - 1][j - 1].setImageBitmap(none);
                else if (intArrTerritory[i][j] == 1)
                    ncArr[i - 1][j - 1].setImageBitmap(bDot);
                else
                    ncArr[i - 1][j - 1].setImageBitmap(wDot);
                Cord = 0;
            }

            if(move) {
                ruleKo[0] = ruleKo[1];
                scoring();
                for (int i1 = 1; i1 < n + 1; i1++) {
                    for (int j1 = 1; j1 < n + 1; j1++) {
                        if (intArr[i1][j1] != 0 && intArrTerritory[i1][j1] == 0) {
                            if (intArr[i1][j1] / 10000000 == 1)
                                ncArr[i1 - 1][j1 - 1].setImageBitmap(bBitmap);
                            else
                                ncArr[i1 - 1][j1 - 1].setImageBitmap(wBitmap);
                        }
                    }
                }
            }

            if (m && move && intArrTerritory[i][j] != 2)
                ncArr[i - 1][j - 1].setImageBitmap(bLast);
            else if (m && move && intArrTerritory[i][j] == 2)
                ncArr[i - 1][j - 1].setImageBitmap(wDotOnBLast);
            else if (!m && move && intArrTerritory[i][j] != 1)
                ncArr[i - 1][j - 1].setImageBitmap(wLast);
            else if (!m && move && intArrTerritory[i][j] == 1)
                ncArr[i - 1][j - 1].setImageBitmap(bDotOnWLast);

            move = false;
            checkMove = false;
            checkMove2 = false;
            for (int i1 = 1; i1 < n+1; i1++) {
                Log.d(TAG, intArr[i1][1] + "\t" + intArr[i1][2] + "\t" + intArr[i1][3] + "\t" + intArr[i1][4] + "\t" + intArr[i1][5] + "\t" + intArr[i1][6] + "\t" + intArr[i1][7] + "\t" + intArr[i1][8] + "\t" + intArr[i1][9] + "\t" + "\n");
            }
            for (int i1 = 1; i1 < n+1; i1++) {
                Log.d(TAG, intArrTerritory[i1][1] + "\t" + intArrTerritory[i1][2] + "\t" + intArrTerritory[i1][3] + "\t" + intArrTerritory[i1][4] + "\t" + intArrTerritory[i1][5] + "\t" + intArrTerritory[i1][6] + "\t" + intArrTerritory[i1][7] + "\t" + intArrTerritory[i1][8] + "\t" + intArrTerritory[i1][9] + "\t" + "\n");
            }
            Log.d(TAG, "wScore = "+wScore);

            if(bot){
                m = false;
                //bot(intArr, 0);
            }
        }
    }

    //Функция, в случае стоящего рядом камня другого цвета
    public void checkMove(int i, int j, int ii, int jj) {
        boolean a=false;
        byte rule = 0;
        if (intArr[i][j] / 10000000 != intArr[ii][jj] / 10000000 && intArr[i][j] / 10000000 != 0) {

            //////////////////////********************
            oldzone = intArr[i][j] % 1000;
            for (int i1 = 1; i1 < n+1; i1++) {
                for (int j1 = 1; j1 < n+1; j1++) {
                    if (intArr[i1][j1] % 1000 == oldzone){
                        if (intArr[i1+1][j1] == 0 || intArr[i1-1][j1] == 0 || intArr[i1][j1+1] == 0 || intArr[i1][j1-1] == 0){
                            a = true;
                            break;
                        }
                    }
                }
                if (a)
                    break;
            }
            if (!a && move) {
                if (intArr[ii][jj] / 10000000 == 1) {
                    for (int i1 = 1; i1 < n+1; i1++) {
                        for (int j1 = 1; j1 < n+1; j1++) {
                            if (intArr[i1][j1] % 1000 == oldzone) {
                                intArr[i1][j1] = 0;
                                ncArr[i1 - 1][j1 - 1].setImageBitmap(none);
                                bScore++;
                            }
                        }
                    }
                } else {
                    for (int i1 = 1; i1 < n+1; i1++) {
                        for (int j1 = 1; j1 < n+1; j1++) {
                            if (intArr[i1][j1] % 1000 == oldzone) {
                                intArr[i1][j1] = 0;
                                ncArr[i1 - 1][j1 - 1].setImageBitmap(none);
                                wScore++;
                            }
                        }
                    }
                }
                a = false;
            }
            else if (!a && !move) {
                checkMove = true;
                for (int i1 = 1; i1 < n+1; i1++) {
                    for (int j1 = 1; j1 < n+1; j1++) {
                        if (intArr[i1][j1] % 1000 == oldzone)
                            rule++;
                    }
                }
                if (rule == 1)
                    ruleKo[1] = (intArr[ii][jj] / 10000000) * 100000000 + ii * 1000000 + jj * 10000 + i * 100 + j;
            }
        }
    }

    //Функция, в случае стоящего рядом камня того же цвета
    public void checkMove2(int i, int j, int ii, int jj) {
        boolean a=false;
        if (intArr[i][j] / 10000000 == intArr[ii][jj] / 10000000 && intArr[i][j] != -1) {
            oldzone = intArr[i][j] % 1000;
            oldzone2 = (intArr[i][j]%1000000)/1000;
            intArr[ii][jj] += oldzone - zone + oldzone2*1000 - zone2*1000;
            for (int i1 = 1; i1 < n+1; i1++) {
                for (int j1 = 1; j1 < n+1; j1++) {
                    if (intArr[i1][j1] % 1000 == oldzone || intArr[i1][j1] % 1000 == zone) {
                        if (intArr[i1+1][j1] == 0 || intArr[i1-1][j1] == 0 || intArr[i1][j1+1] == 0 || intArr[i1][j1-1] == 0) {
                            a = true;
                            checkMove2 = true;
                            break;
                        }
                    }
                }
                if(a) break;
            }
            if ( !a && !checkMove && !checkMove2 && (i == ii - 1 && intArr[ii][jj+1] / 10000000 != intArr[ii][jj] / 10000000
                    && intArr[ii+1][jj] / 10000000 != intArr[ii][jj] / 10000000
                    && intArr[ii][jj-1] / 10000000 != intArr[ii][jj] / 10000000
                    || j == jj + 1 && intArr[ii+1][jj] / 10000000 != intArr[ii][jj] / 10000000
                    && intArr[ii][jj-1] / 10000000 != intArr[ii][jj] / 10000000
                    || i == ii + 1 && intArr[ii][jj-1] / 10000000 != intArr[ii][jj] / 10000000
                    || j == jj - 1)){
                intArr[ii][jj] = 0;
                Cord = 0;
                if (intArrTerritory[ii][jj] == 0)
                    ncArr[ii - 1][jj - 1].setImageBitmap(none);
                else if (intArrTerritory[ii][jj] == 1)
                    ncArr[ii - 1][jj - 1].setImageBitmap(bDot);
                else
                    ncArr[ii - 1][jj - 1].setImageBitmap(wDot);
            }
            else if (move){
                if ((intArr[i][j]%10000000)/1000000 == 1 && (intArr[ii][jj]%10000000)/1000000 != 1)
                    intArr[ii][jj] += 1000000;
                for (int i1 = 1; i1 < n + 1; i1++) {
                    for (int j1 = 1; j1 < n + 1; j1++) {
                        if (intArr[i1][j1] % 1000 == oldzone)
                            intArr[i1][j1] += zone - oldzone;
                        if ((intArr[i1][j1] % 1000000) / 1000 == oldzone2 && intArr[i1][j1] != 0)
                            intArr[i1][j1] += zone2 * 1000 - oldzone2 * 1000;
                    }
                }
            }
            else if (!move)
                intArr[ii][jj] += zone - oldzone + zone2*1000 - oldzone2*1000;
        }
    }

    //Подсчет очков и территорий
    public void scoring(){
        int[][] intArr1 = new int[n+2][n+2];
        int[][] intArr2 = new int[n+2][n+2];
        int wStrategicScore = 0, bStrategicScore = 0;

        for (int i = 0; i < n+2; i++){
            for (int j = 0; j < n+2; j++){
                intArr1[i][j] = intArr[i][j];
                if (i==0 || j==0 || i==n+1 || j==n+1) {
                    intArr2[i][j] = -1;
                    intArrTerritory[i][j] = -1;
                }
                else {
                    intArr2[i][j] = 0;
                    intArrTerritory[i][j] = 0;
                }
            }
        }

        for (int i = 1; i < n+1; i++){
            for (int j = 1; j < n+1; j++) {
                if ((intArr1[i][j] % 1000000) / 1000 > 0){
                    oldzone2 = (intArr1[i][j] % 1000000) / 1000;
                    oldzone = 0;

                    //Анализ территорий
                    for (int i1=1; i1 < n+1; i1++){
                        for (int j1=1; j1 < n+1; j1++){
                            if ((intArr1[i1][j1]%1000000)/1000 == oldzone2){
                                for (int j2=j1+1; j2 < n+1; j2++) {
                                    if ((intArr1[i1][j2] % 1000000)/1000 != oldzone2 ) {
                                        intArr2[i1][j2]++;
                                    }
                                }
                                break;
                            }
                        }
                    }

                    for (int j1=1; j1 < n+1; j1++){
                        for (int i1=1; i1 < n+1; i1++) {
                            if ((intArr1[i1][j1]%1000000)/1000 == oldzone2){
                                for (int i2=i1+1; i2 < n+1; i2++) {
                                    if ((intArr1[i2][j1] % 1000000)/1000 != oldzone2) {
                                        intArr2[i2][j1]++;
                                    }
                                }
                                break;
                            }
                        }
                    }

                    for (int i1=1; i1 < n+1; i1++){
                        for (int j1=n; j1 > 0; j1--){
                            if ((intArr1[i1][j1]%1000000)/1000 == oldzone2){
                                for (int j2=j1-1; j2 > 0; j2--) {
                                    if ((intArr1[i1][j2] % 1000000)/1000 != oldzone2 ) {
                                        intArr2[i1][j2]++;
                                    }
                                }
                                break;
                            }
                        }
                    }

                    for (int j1=1; j1 < n+1; j1++){
                        for (int i1=n; i1 > 0; i1--){
                            if ((intArr1[i1][j1]%1000000)/1000 == oldzone2){
                                for (int i2=i1-1; i2 > 0; i2--){
                                    if ((intArr1[i2][j1] % 1000000)/1000 != oldzone2) {
                                        intArr2[i2][j1]++;
                                    }
                                }
                                break;
                            }
                        }
                    }
                    //

                    //Наличие территории из трех стен
                    for (int i1 = 1; i1 < n+1; i1++){
                        for (int j1 = 1; j1 < n+1; j1++){
                            if ((i1 == 1 || i1 == n || j1 == 1 || j1 == n) && intArr2[i1][j1] == 3){
                                for (int i2 = 1; i2 < n+1; i2++){
                                    for (int j2 = 1; j2 < n+1; j2++){
                                        if (intArr2[i2][j2] == 3)
                                            intArr2[i2][j2] = 4;
                                    }
                                }
                            }
                        }
                    }
                    //

                    //Наличие территории из двух стен

                    if (intArr2[1][1]==2 || intArr2[1][n]==2 || intArr2[n][n]==2 || intArr2[n][1]==2){
                        for (int i1=1; i1 < n+1; i1++){
                            for (int j1 = 1; j1 < n+1; j1++) {
                                if (intArr2[i1][j1] >= 2) {
                                    intArr2[i1][j1] = 4;
                                }
                            }
                        }
                    }
                    //

                    //Наличие территории из одной стены
                    boolean oneWall = false;
                    i1: for (int i1 = 1; i1 < n+1; i1++){
                        if ((intArr1[i1][1]%1000000)/1000 == oldzone2) {
                            for (int i2 = 1; i2 < n+1; i2++){
                                if ((intArr1[i2][n]%1000000)/1000 == oldzone2) {
                                    oneWall = true;
                                    break i1;
                                }
                            }
                        }
                    }

                    j1: for (int j1 = 1; j1 < n+1; j1++){
                        if ((intArr1[1][j1]%1000000)/1000 == oldzone2) {
                            for (int j2 = 1; j2 < n+1; j2++){
                                if ((intArr1[n][j2]%1000000)/1000 == oldzone2) {
                                    oneWall = true;
                                    break j1;
                                }
                            }
                        }
                    }

                    if (oneWall){
                        for (int i1 = 1; i1 < n+1; i1++){
                            for (int j1 = 1; j1 < n+1; j1++){
                                if (intArr2[i1][j1] >= 1)
                                    intArr2[i1][j1] = 4;
                            }
                        }
                    }

                    // Устранение фиктивных территорий

                    for (int k = 0; k < 5; k++) {
                        for (int i1 = 1; i1 < n+1; i1++) {
                            for (int j1 = 1; j1 < n+1; j1++) {
                                if (intArr2[i1][j1] == 4 && intArr2[i1][j1 - 1] > 0 && intArr2[i1][j1 - 1] < 4) {
                                    intArr2[i1][j1] = 1;
                                }
                            }
                        }

                        for (int j1 = 1; j1 < n+1; j1++) {
                            for (int i1 = 1; i1 < n+1; i1++) {
                                if (intArr2[i1][j1] == 4 && intArr2[i1 - 1][j1] > 0 && intArr2[i1 - 1][j1] < 4) {
                                    intArr2[i1][j1] = 1;
                                }
                            }
                        }

                        for (int i1 = 1; i1 < n+1; i1++) {
                            for (int j1 = n; j1 > 0; j1--) {
                                if (intArr2[i1][j1] == 4 && intArr2[i1][j1+1] > 0 && intArr2[i1][j1+1] < 4) {
                                    intArr2[i1][j1] = 1;
                                }
                            }
                        }

                        for (int j1 = 1; j1 < n+1; j1++) {
                            for (int i1 = n; i1 > 0; i1--) {
                                if (intArr2[i1][j1] == 4 && intArr2[i1 + 1][j1] > 0 && intArr2[i1 + 1][j1] < 4) {
                                    intArr2[i1][j1] = 1;
                                }
                            }
                        }
                    }

                    i1 : for (int i1 = 1; i1 < n + 1; i1++) {
                        for (int j1 = 1; j1 < n + 1; j1++) {
                            if (intArr2[i1][j1] == 4) {
                                for (int i2 = 1; i2 < n + 1; i2++) {
                                    for (int j2 = 1; j2 < n + 1; j2++) {
                                        if ((intArr1[i2][j2] % 1000000) / 1000 == oldzone2)
                                            intArr1[i2][j2] += 1000000;
                                    }
                                }
                                break i1;
                            }
                        }
                    }

                    //Проверка являются ли территория смешанной

                    boolean mix = false;

                    for (int i1 = 1; i1 < n+1; i1++) {
                        for (int j1 = 1; j1 < n+1; j1++) {
                            if (intArr2[i1][j1] == 4 && intArr1[i1][j1]/10000000 != intArr1[i][j]/10000000 && (intArr1[i1][j1]%10000000)/1000000 == 1 &&
                                    (i1 == 1 || j1 == 1 || i1 == n || j1 == n)) {
                                for (int i2 = 1; i2 < n+1; i2++) {
                                    for (int j2 = 1; j2 < n+1; j2++) {
                                        if ((intArr1[i2][j2]%1000000)/1000 == oldzone2 && intArrTerritory[i2][j2] != intArr1[i2][j2]/10000000 && intArrTerritory[i2][j2] > 0 &&
                                                (i2 == 1 || j2 == 1 || i2 == n || j2 == n)) {
                                            intArr2[i1][j1] = 1;
                                            intArrTerritory[i2][j2] = 0;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    /////
                    for (int i1 = 1; i1 < n+1; i1++) {
                        Log.d(TAG, "Перед первой: "+intArr2[i1][1] + "\t" + intArr2[i1][2] + "\t" + intArr2[i1][3] + "\t" + intArr2[i1][4] + "\t" + intArr2[i1][5] + "\t" + intArr2[i1][6] + "\t" + intArr2[i1][7] + "\t" + intArr2[i1][8] + "\t" + intArr2[i1][9] + "\t" + "\n\n");
                    }
                    Log.d(TAG, "///");

                    oldzone2 = (intArr1[i][j]%1000000)/1000;
                    for (int k = 0; k < 5; k++) {
                        for (int i1 = 1; i1 < n+1; i1++) {
                            for (int j1 = 1; j1 < n+1; j1++) {
                                if (intArr2[i1][j1] == 4 && intArr2[i1][j1 - 1] > 0 && intArr2[i1][j1 - 1] < 4 && ((intArr1[i1][j1]%10000000)/1000000 != 1 ||
                                        intArr1[i1][j1]/10000000 != 0 && intArr1[i1][j1]/10000000 != intArr1[i][j]/10000000)/* ||
                                        intArr2[i1][j1] == 4 && intArrTerritory[i1][j1 - 1] != intArr1[i][j]/10000000 && intArrTerritory[i1][j1 - 1] > 0 &&
                                                intArrTerritory[i1][j1] == 0 && intArr1[i1][j1]/10000000 > 0 */||
                                        (intArr1[i1][j1]%10000000)/1000000 >= 1 && intArr2[i1][j1] == 4 && intArr1[i1][j1]/10000000 != intArr1[i][j]/10000000 /*&& mix*/ &&
                                                (i1 == 1 || j1 == 1 || i1 == n || j1 == n)) {
                                    intArr2[i1][j1] = 1;
                                }
                            }
                        }

                        for (int j1 = 1; j1 < n+1; j1++) {
                            for (int i1 = 1; i1 < n+1; i1++) {
                                if (intArr2[i1][j1] == 4 && intArr2[i1 - 1][j1] > 0 && intArr2[i1 - 1][j1] < 4 && ((intArr1[i1][j1]%10000000)/1000000 != 1 ||
                                        intArr1[i1][j1]/10000000 != 0 && intArr1[i1][j1]/10000000 != intArr1[i][j]/10000000) /*||
                                        intArr2[i1][j1] == 4 && intArrTerritory[i1 - 1][j1] != intArr1[i][j]/10000000 && intArrTerritory[i1 - 1][j1] > 0 &&
                                                intArrTerritory[i1][j1] == 0 && intArr1[i1][j1]/10000000 > 0*/ ||
                                        (intArr1[i1][j1]%10000000)/1000000 >= 1  && intArr2[i1][j1] == 4 && intArr1[i1][j1]/10000000 != intArr1[i][j]/10000000 /*&& mix*/ &&
                                                (i1 == 1 || j1 == 1 || i1 == n || j1 == n)) {
                                    intArr2[i1][j1] = 1;
                                }
                            }
                        }

                        for (int i1 = 1; i1 < n+1; i1++) {
                            for (int j1 = n; j1 > 0; j1--) {
                                if (intArr2[i1][j1] == 4 && intArr2[i1][j1+1] > 0 && intArr2[i1][j1+1] < 4 && ((intArr1[i1][j1]%10000000)/1000000 != 1 ||
                                        intArr1[i1][j1]/10000000 != 0 && intArr1[i1][j1]/10000000 != intArr1[i][j]/10000000) /*||
                                        intArr2[i1][j1] == 4 && intArrTerritory[i1][j1 + 1] != intArr1[i][j]/10000000 && intArrTerritory[i1][j1 + 1] > 0 &&
                                                intArrTerritory[i1][j1] == 0 && intArr1[i1][j1]/10000000 > 0*/ ||
                                        (intArr1[i1][j1]%10000000)/1000000 >= 1 && intArr2[i1][j1] == 4 && intArr1[i1][j1]/10000000 != intArr1[i][j]/10000000 /*&& mix*/ &&
                                                (i1 == 1 || j1 == 1 || i1 == n || j1 == n)) {
                                    intArr2[i1][j1] = 1;
                                }
                            }
                        }

                        for (int j1 = 1; j1 < n+1; j1++) {
                            for (int i1 = n; i1 > 0; i1--) {
                                if (intArr2[i1][j1] == 4 && intArr2[i1 + 1][j1] > 0 && intArr2[i1 + 1][j1] < 4 && ((intArr1[i1][j1]%10000000)/1000000 != 1 ||
                                        intArr1[i1][j1]/10000000 != 0 && intArr1[i1][j1]/10000000 != intArr1[i][j]/10000000) /*||
                                        intArr2[i1][j1] == 4 && intArrTerritory[i1 + 1][j1] != intArr1[i][j]/10000000 && intArrTerritory[i1 + 1][j1] > 0 &&
                                                intArrTerritory[i1][j1] == 0 && intArr1[i1][j1]/10000000 > 0 */||
                                        (intArr1[i1][j1]%10000000)/1000000 >= 1 && intArr2[i1][j1] == 4 && intArr1[i1][j1]/10000000 != intArr1[i][j]/10000000 /*&& mix*/ &&
                                                (i1 == 1 || j1 == 1 || i1 == n || j1 == n)) {
                                    intArr2[i1][j1] = 1;
                                }
                            }
                        }
                    }

                    for (int i1 = 1; i1 < n+1; i1++) {
                        Log.d(TAG, "После первой: "+intArr2[i1][1] + "\t" + intArr2[i1][2] + "\t" + intArr2[i1][3] + "\t" + intArr2[i1][4] + "\t" + intArr2[i1][5] + "\t" + intArr2[i1][6] + "\t" + intArr2[i1][7] + "\t" + intArr2[i1][8] + "\t" + intArr2[i1][9] + "\t" + "\n\n");
                    }
                    Log.d(TAG, "///");

                    for (int k = 0; k < 5; k++) {
                        for (int i1 = 1; i1 < n+1; i1++) {
                            for (int j1 = 1; j1 < n+1; j1++) {
                                if (intArr2[i1][j1 - 1] == 4 && intArr1[i1][j1 - 1]/10000000 != intArrTerritory[i1][j1] && intArrTerritory[i1][j1] > 0 ||
                                        intArrTerritory[i1][j1 - 1] == 0 && intArr1[i1][j1 - 1]/10000000 != intArrTerritory[i1][j1] ) {
                                    intArrTerritory[i1][j1] = 0;
                                }
                            }
                        }

                        for (int j1 = 1; j1 < n+1; j1++) {
                            for (int i1 = 1; i1 < n+1; i1++) {
                                if (intArr2[i1 - 1][j1] == 4 && intArr1[i1 - 1][j1]/10000000 != intArrTerritory[i1][j1] && intArrTerritory[i1][j1] > 0 ||
                                        intArrTerritory[i1 - 1][j1] == 0 && intArr1[i1 - 1][j1]/10000000 != intArrTerritory[i1][j1]) {
                                    intArrTerritory[i1][j1] = 0;
                                }
                            }
                        }

                        for (int i1 = 1; i1 < n+1; i1++) {
                            for (int j1 = n; j1 > 0; j1--) {
                                if (intArr2[i1][j1 + 1] == 4 && intArr1[i1][j1 + 1]/10000000 != intArrTerritory[i1][j1] && intArrTerritory[i1][j1] > 0 ||
                                        intArrTerritory[i1][j1 + 1] == 0 && intArr1[i1][j1 + 1]/10000000 != intArrTerritory[i1][j1]) {
                                    intArrTerritory[i1][j1] = 0;
                                }
                            }
                        }

                        for (int j1 = 1; j1 < n+1; j1++) {
                            for (int i1 = n; i1 > 0; i1--) {
                                if (intArr2[i1 + 1][j1] == 4 && intArr1[i1 + 1][j1]/10000000 != intArrTerritory[i1][j1] && intArrTerritory[i1][j1] > 0 ||
                                        intArrTerritory[i1 + 1][j1] == 0 && intArr1[i1 + 1][j1]/10000000 != intArrTerritory[i1][j1]) {
                                    intArrTerritory[i1][j1] = 0;
                                }
                            }
                        }
                    }
                    ///

                    for (int i1 = 1; i1 < n+1; i1++) {
                        Log.d(TAG, "После второй: "+intArrTerritory[i1][1] + "\t" + intArrTerritory[i1][2] + "\t" + intArrTerritory[i1][3] + "\t" + intArrTerritory[i1][4] + "\t" + intArrTerritory[i1][5] + "\t" + intArrTerritory[i1][6] + "\t" + intArrTerritory[i1][7] + "\t" + intArrTerritory[i1][8] + "\t" + intArrTerritory[i1][9] + "\t" + "\n\n");
                    }
                    Log.d(TAG, "///");
                    //Распределение получившихся территорий
                    for (int i1 = 1; i1 < n + 1; i1++) {
                        for (int j1 = 1; j1 < n + 1; j1++) {
                            if (intArr2[i1][j1] == 4)
                                intArrTerritory[i1][j1] = intArr1[i][j] / 10000000;
                            if ((intArr1[i1][j1]%10000000)/1000000==1 && intArr2[i1][j1]==4)
                                intArr1[i1][j1] -= 1000000;
                        }
                    }
                    ////

                    for (int i1=1; i1 < n+1; i1++){
                        for (int j1=1; j1 < n+1; j1++) {
                            intArr2[i1][j1] = 0;
                        }
                    }

                    for (int i1=1; i1 < n+1; i1++){
                        for (int j1=1; j1 < n+1; j1++) {
                            if ((intArr1[i1][j1]%1000000)/1000 == oldzone2) {
                                intArr1[i1][j1] -= oldzone2*1000;
                            }
                        }
                    }

                }
            }
        }
        // Присвоение очков //
        for (int i1=1; i1 < n+1; i1++){
            for (int j1=1; j1 < n+1; j1++) {
                if (intArrTerritory[i1][j1] == 1) {
                    if (intArr1[i1][j1]==0)
                        ncArr[i1 - 1][j1 - 1].setImageBitmap(bDot);
                    else if (intArr1[i1][j1]/10000000==2)
                        ncArr[i1 - 1][j1 - 1].setImageBitmap(bDotOnW);
                    else if (intArr1[i1][j1]/10000000==1)
                        ncArr[i1 - 1][j1 - 1].setImageBitmap(bBitmap);
                    //Добавление очков черным
                    if (intArr1[i1][j1]==0)
                        bStrategicScore++;
                    else if (intArr1[i1][j1]/10000000==2)
                        bStrategicScore += 2;
                }
                else if (intArrTerritory[i1][j1] == 2) {
                    if (intArr1[i1][j1]==0)
                        ncArr[i1 - 1][j1 - 1].setImageBitmap(wDot);
                    else if (intArr1[i1][j1]/10000000==1)
                        ncArr[i1 - 1][j1 - 1].setImageBitmap(wDotOnB);
                    else if (intArr1[i1][j1]/10000000==2)
                        ncArr[i1 - 1][j1 - 1].setImageBitmap(wBitmap);
                    //Добавление очков белым
                    if (intArr1[i1][j1]==0)
                        wStrategicScore++;
                    else if (intArr1[i1][j1]/10000000==1)
                        wStrategicScore += 2;
                }
                else if (intArr[i1][j1]/10000000 == 0){
                    ncArr[i1 - 1][j1 - 1].setImageBitmap(none);
                }
            }
        }

        wTotalScore = wScore+wStrategicScore;
        bTotalScore = bScore+bStrategicScore;
        if (komi != 0)
            textWhite.setText(getResources().getString(R.string.score_white) + " " + (wTotalScore+komi));
        else
            textWhite.setText(getResources().getString(R.string.score_white) + " " + wTotalScore);
        textBlack.setText(getResources().getString(R.string.score_black) + " " +bTotalScore);

    }

}