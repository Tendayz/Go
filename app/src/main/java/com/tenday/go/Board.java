package com.tenday.go;

import android.provider.Settings;

public class Board {

    int n, lastMove = -1, cur_player;
    int[][] intArr, intArrTerritory;
    boolean passCheck, /*m=false,*/ endGame = false, bot=false;
    int zone=0, zone2=1, bScore=0, wScore=0, bTotalScore=0, wTotalScore=0, checkRuleKo=0, kolvoHodov=0;
    float komi;



    public Board(int n, float komi){
        cur_player = 1;
        this.n = n;
        this.komi = komi;

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
    }

    public int[][] getBoard(){
        int[][] intBoard = new int[n+2][n+2];
        for (int i = 1; i < n+1; i++){
            for (int j = 1; j < n+1; j++){
                intBoard[i][j] = intArr[i][j]/10000000;
            }
        }
        return intArr;
    }

    public void newGame(){
        endGame = false;
        cur_player = 1;
        zone = 0;
        zone2 = 1;
        bScore = 0;
        wScore = 0;
        checkRuleKo = 0;
        lastMove = -1;

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
    }

    public void move(int i, int j) {
        if (!endGame /*&& checkMove(i, j)*/) {
            passCheck = true;

            intArr[i][j] = cur_player*10000000;
            zone++;
            int scoreBefore;
            if (cur_player == 1)
                scoreBefore = bScore;
            else
                scoreBefore = wScore;

            intArr[i][j] += zone /*+ zone2*1000*/;
            //Проверки на стоящие рядом камни противника
            move1(i - 1, j, i, j);
            move1(i, j + 1, i, j);
            move1(i + 1, j, i, j);
            move1(i, j - 1, i, j);
            //Проверки на стоящие рядом камни того же цвета
            move2(i - 1, j, i, j);
            move2(i, j + 1, i, j);
            move2(i + 1, j, i, j);
            move2(i, j - 1, i, j);

            //Правило Ко
            if ((cur_player == 1 ? bScore-scoreBefore == 1 : wScore-scoreBefore == 1) && (intArr[i-1][j]/10000000 == 3-cur_player || i==1) && (intArr[i][j+1]/10000000 == 3-cur_player || j==n) &&
                    (intArr[i+1][j]/10000000 == 3-cur_player || i==n)) {
                checkRuleKo = (3 - cur_player) * 10000 + i * 100 + j - 1;
            }
            else if ((cur_player == 1 ? bScore-scoreBefore == 1 : wScore-scoreBefore == 1) && (intArr[i][j+1]/10000000 == 3-cur_player || j==n) && (intArr[i+1][j]/10000000 == 3-cur_player || i==n) &&
                    (intArr[i][j-1]/10000000 == 3-cur_player || j==1))
                checkRuleKo = (3-cur_player)*10000+(i-1)*100+j;
            else if ((cur_player == 1 ? bScore-scoreBefore == 1 : wScore-scoreBefore == 1) && (intArr[i-1][j]/10000000 == 3-cur_player || i==1) && (intArr[i][j-1]/10000000 == 3-cur_player || j==1) &&
                    (intArr[i+1][j]/10000000 == 3-cur_player || i==n))
                checkRuleKo = (3 - cur_player) * 10000 + i * 100 + j + 1;
            else if ((cur_player == 1 ? bScore-scoreBefore == 1 : wScore-scoreBefore == 1) && (intArr[i-1][j]/10000000 == 3-cur_player || i==1) && (intArr[i][j-1]/10000000 == 3-cur_player || j==1) &&
                    (intArr[i][j+1]/10000000 == 3-cur_player || j==n))
                checkRuleKo = (3-cur_player)*10000+(i+1)*100+j;
            else
                checkRuleKo = 0;

            lastMove = i*100+j;
            cur_player = 3-cur_player;
        }
    }

    //Версия уже без учета zone
    public void fillBreath(int i, int j, int breath){

        intArr[i][j] = intArr[i][j] - intArr[i][j]%100 + breath;

        if (intArr[i-1][j]/10000000 == intArr[i][j]/10000000 && intArr[i-1][j]%100 != breath){
            fillBreath(i-1,j,breath);
        }
        if (intArr[i][j+1]/10000000 == intArr[i][j]/10000000 && intArr[i][j+1]%100 != breath){
            fillBreath(i,j+1,breath);
        }
        if (intArr[i+1][j]/10000000 == intArr[i][j]/10000000 && intArr[i+1][j]%100 != breath){
            fillBreath(i+1,j,breath);
        }
        if (intArr[i][j-1]/10000000 == intArr[i][j]/10000000 && intArr[i][j-1]%100 != breath){
            fillBreath(i,j-1,breath);
        }

    }

    public int breath(int i, int j){

        intArr[i][j] += 100;

        int breath = 0;
        if (intArr[i-1][j]==0){
            intArr[i-1][j] = 1;
            breath++;
        }
        if (intArr[i][j+1]==0){
            intArr[i][j+1] = 1;
            breath++;
        }
        if (intArr[i+1][j]==0){
            intArr[i+1][j] = 1;
            breath++;
        }
        if (intArr[i][j-1]==0){
            intArr[i][j-1] = 1;
            breath++;
        }

        int b1=0, b2=0, b3=0, b4=0;

        if (intArr[i-1][j]/10000000 == intArr[i][j]/10000000 && (intArr[i-1][j]%1000)/100 == 0){
            b1 = breath(i-1,j);
        }
        if (intArr[i][j+1]/10000000 == intArr[i][j]/10000000 && (intArr[i][j+1]%1000)/100 == 0){
            b2 = breath(i,j+1);
        }
        if (intArr[i+1][j]/10000000 == intArr[i][j]/10000000 && (intArr[i+1][j]%1000)/100 == 0){
            b3 = breath(i+1,j);
        }
        if (intArr[i][j-1]/10000000 == intArr[i][j]/10000000 && (intArr[i][j-1]%1000)/100 == 0){
            b4 = breath(i,j-1);
        }

        return b1+b2+b3+b4+breath;
    }

    //Съедение вражеских камней
    private void move1(int i, int j, int ii, int jj) {
        if (intArr[i][j] / 10000000 != intArr[ii][jj] / 10000000 && intArr[i][j] / 10000000 != 0 && checkMove1(i,j,ii,jj)) {

            int oldzone = intArr[i][j] % 1000, score = 0;

            for (int i1 = 1; i1 < n+1; i1++) {
                for (int j1 = 1; j1 < n + 1; j1++) {
                    if (intArr[i1][j1] % 1000 == oldzone) {
                        intArr[i1][j1] = 0;
                        score++;
                    }
                }
            }

            if (intArr[ii][jj] / 10000000 == 1)
                bScore += score;
            else
                wScore += score;
        }
    }

    //Присоединение к союзным камням
    private void move2(int i, int j, int ii, int jj) {
        if (intArr[i][j] / 10000000 == intArr[ii][jj] / 10000000) {
            int
                    oldzone = intArr[i][j] % 1000,
                    zone = intArr[ii][jj] % 1000;

            if ((intArr[i][j]%10000000)/1000000 == 1 && (intArr[ii][jj]%10000000)/1000000 != 1)
                intArr[ii][jj] += 1000000;
            for (int i1 = 1; i1 < n + 1; i1++) {
                for (int j1 = 1; j1 < n + 1; j1++) {
                    if (intArr[i1][j1] % 1000 == oldzone)
                        intArr[i1][j1] += zone - oldzone;
                }
            }

        }
    }

    //Проверка допустимости хода
    public boolean checkMove(int i, int j){

        if (intArr[i][j] == 0) {

            int zone = this.zone + 1/*, zone2 = this.zone2 + 1*/;

            intArr[i][j] = cur_player * 10000000;

            intArr[i][j] += zone /*+ zone2 * 1000*/;

            if (!(checkRuleKo/10000 == cur_player && (checkRuleKo%10000)/100 == i && checkRuleKo%100 == j) &&
                    (checkMove1(i - 1, j, i, j) || checkMove1(i, j + 1, i, j) || checkMove1(i + 1, j, i, j) || checkMove1(i, j - 1, i, j) ||
                            checkMove2(i - 1, j, i, j) || checkMove2(i, j + 1, i, j) || checkMove2(i + 1, j, i, j) || checkMove2(i, j - 1, i, j))) {
                intArr[i][j] = 0;
                return true;
            } else {
                //checkRuleKo = 0;
                intArr[i][j] = 0;
                return false;
            }


        }
        else
            return false;
    }

    //Наличие свободного дыхания рядом после поставки камня и заодно правило Ко
    private boolean checkMove1(int i, int j, int ii, int jj) {
        if (intArr[i][j] / 10000000 != intArr[ii][jj] / 10000000 && intArr[i][j] / 10000000 != 0) {

            int oldzone = intArr[i][j] % 1000;

            for (int i1 = 1; i1 < n + 1; i1++) {
                for (int j1 = 1; j1 < n + 1; j1++) {
                    if (intArr[i1][j1] % 1000 == oldzone) {
                        if (intArr[i1 + 1][j1] == 0 || intArr[i1 - 1][j1] == 0 || intArr[i1][j1 + 1] == 0 || intArr[i1][j1 - 1] == 0) {
                            return false;
                        }
                    }
                }
            }

            return true;
        }
        else if (intArr[i][j] != 0)
            return false;
        else
            return true;
    }

    //Наличие надежного союзного камня рядом
    private boolean checkMove2(int i, int j, int ii, int jj) {
        if (intArr[i][j] / 10000000 == intArr[ii][jj] / 10000000) {
            int
                    oldzone = intArr[i][j] % 1000,
                    zone = intArr[ii][jj] % 1000;

            intArr[ii][jj] += oldzone;

            for (int i1 = 1; i1 < n+1; i1++) {
                for (int j1 = 1; j1 < n+1; j1++) {
                    if (intArr[i1][j1] % 1000 == oldzone || intArr[i1][j1] % 1000 == zone) {
                        if (intArr[i1+1][j1] == 0 || intArr[i1-1][j1] == 0 || intArr[i1][j1+1] == 0 || intArr[i1][j1-1] == 0) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }
        else
            return false;
    }

    //Пропуск хода
    public void pass(){
        if (passCheck) {
            passCheck = false;
            cur_player = 3-cur_player;
        }
        else if (!endGame){
            endGame = true;
            cur_player = 3-cur_player;
        }
    }

    public int getScoringWinner(){
        scoring();
        if (bTotalScore > wTotalScore+komi)
            return 1;
        else if (bTotalScore < wTotalScore+komi)
            return 2;
        else
            return 0;
    }

    public void zone2(int i,int j){
        intArr[i][j] += zone2*1000;
        if (intArr[i+1][j+1] / 10000000 == intArr[i][j] / 10000000 && (intArr[i+1][j+1]%1000000)/1000 == 0){
            zone2(i+1,j+1);
        }
        if (intArr[i+1][j] / 10000000 == intArr[i][j] / 10000000 && (intArr[i+1][j]%1000000)/1000 == 0){
            zone2(i+1,j);
        }
        if (intArr[i+1][j-1] / 10000000 == intArr[i][j] / 10000000 && (intArr[i+1][j-1]%1000000)/1000 == 0){
            zone2(i+1,j-1);
        }
        if (intArr[i][j-1] / 10000000 == intArr[i][j] / 10000000 && (intArr[i][j-1]%1000000)/1000 == 0){
            zone2(i,j-1);
        }
        if (intArr[i-1][j-1] / 10000000 == intArr[i][j] / 10000000 && (intArr[i-1][j-1]%1000000)/1000 == 0){
            zone2(i-1,j-1);
        }
        if (intArr[i-1][j] / 10000000 == intArr[i][j] / 10000000 && (intArr[i-1][j]%1000000)/1000 == 0){
            zone2(i-1,j);
        }
        if (intArr[i-1][j+1] / 10000000 == intArr[i][j] / 10000000 && (intArr[i-1][j+1]%1000000)/1000 == 0){
            zone2(i-1,j+1);
        }
        if (intArr[i][j+1] / 10000000 == intArr[i][j] / 10000000 && (intArr[i][j+1]%1000000)/1000 == 0){
            zone2(i,j+1);
        }

        //Надо будет обнулить zone2 у intArr потом и сам zone2
    }

    public void helpScoring(int i, int j){
        if (!( intArr[i][j]/10000000 == intArr[i-1][j]/10000000  && (intArr[i-1][j]%10000000)/1000000 == 1 &&
                (intArr[i][j]/10000000 != intArr[i-1][j+1]/10000000 && (intArr[i][j]/10000000 == intArr[i][j+1]/10000000 && (intArr[i][j+1]%10000000)/1000000 == 1 || j == n) ||
                        (intArr[i][j]/10000000 == intArr[i+1][j+1]/10000000 && (intArr[i+1][j+1]%10000000)/1000000 == 1 || i == n || j == n) ||
                        (intArr[i][j]/10000000 == intArr[i+1][j]/10000000 && (intArr[i+1][j]%10000000)/1000000 == 1 || i == n) ||
                        (intArr[i][j]/10000000 == intArr[i+1][j-1]/10000000 && (intArr[i+1][j-1]%10000000)/1000000 == 1 || i == n || j == 1 ) ||
                        intArr[i][j]/10000000 != intArr[i-1][j-1]/10000000 && intArr[i][j]/10000000 == intArr[i][j-1]/10000000 && (intArr[i][j-1]%10000000)/1000000 == 1) ||

                intArr[i][j]/10000000 == intArr[i-1][j+1]/10000000 && (intArr[i-1][j+1]%10000000)/1000000 == 1  &&
                        (intArr[i][j]/10000000 != intArr[i][j+1]/10000000 && (intArr[i][j]/10000000 == intArr[i+1][j+1]/10000000 && (intArr[i+1][j+1]%10000000)/1000000 == 1|| i == n || j == n) ||
                                (intArr[i][j]/10000000 == intArr[i+1][j]/10000000 && (intArr[i+1][j]%10000000)/1000000 == 1 || i == n) ||
                                (intArr[i][j]/10000000 == intArr[i+1][j-1]/10000000 && (intArr[i+1][j-1]%10000000)/1000000 == 1 || i == n || j == 1) ||
                                (intArr[i][j]/10000000 == intArr[i][j-1]/10000000 && (intArr[i][j-1]%10000000)/1000000 == 1 || j == 1) ||
                                intArr[i][j]/10000000 != intArr[i][j-1]/10000000 && (intArr[i][j]/10000000 == intArr[i-1][j-1]/10000000 && (intArr[i-1][j-1]%10000000)/1000000 == 1 || i == 1 || j == 1)) ||

                intArr[i][j]/10000000 == intArr[i][j+1]/10000000 && (intArr[i][j+1]%10000000)/1000000 == 1 &&
                        (intArr[i][j]/10000000 != intArr[i+1][j+1]/10000000 && intArr[i][j]/10000000 == intArr[i+1][j]/10000000 && (intArr[i+1][j]%10000000)/1000000 == 1 ||
                                (intArr[i][j]/10000000 == intArr[i+1][j-1]/10000000 && (intArr[i+1][j-1]%10000000)/1000000 == 1 || i == n || j == 1) ||
                                (intArr[i][j]/10000000 == intArr[i][j-1]/10000000 && (intArr[i][j-1]%10000000)/1000000 == 1 || j == 1) ||
                                (intArr[i][j]/10000000 == intArr[i-1][j-1]/10000000 && (intArr[i-1][j-1]%10000000)/1000000 == 1 || i == 1 || j == 1)
                                || i == 1) ||

                intArr[i][j]/10000000 == intArr[i+1][j+1]/10000000 && (intArr[i+1][j+1]%10000000)/1000000 == 1 &&
                        (intArr[i][j]/10000000 != intArr[i+1][j]/10000000 && (intArr[i][j]/10000000 == intArr[i+1][j-1]/10000000 && (intArr[i+1][j-1]%10000000)/1000000 == 1 || i == n || j == 1) ||
                                (intArr[i][j]/10000000 == intArr[i][j-1]/10000000 && (intArr[i][j-1]%10000000)/1000000 == 1 || j == 1) ||
                                (intArr[i][j]/10000000 == intArr[i-1][j-1]/10000000 && (intArr[i-1][j-1]%10000000)/1000000 == 1 || i == 1 || j == 1)
                                || i == 1 || j == 1) ||

                intArr[i][j]/10000000 == intArr[i+1][j]/10000000 && (intArr[i+1][j]%10000000)/1000000 == 1 &&
                        (intArr[i][j]/10000000 != intArr[i+1][j-1]/10000000 && intArr[i][j]/10000000 == intArr[i][j-1]/10000000 && (intArr[i][j-1]%10000000)/1000000 == 1 ||
                                (intArr[i][j]/10000000 == intArr[i-1][j-1]/10000000 && (intArr[i-1][j-1]%10000000)/1000000 == 1) ||
                                i == 1) ||

                intArr[i][j]/10000000 == intArr[i+1][j-1]/10000000 && (intArr[i+1][j-1]%10000000)/1000000 == 1 &&
                        (intArr[i][j]/10000000 != intArr[i][j-1]/10000000 && (intArr[i][j]/10000000 == intArr[i-1][j-1]/10000000 && (intArr[i-1][j-1]%10000000)/1000000 == 1 || i == 1 || j == 1) ||
                                i == 1 || j == n ) ||

                intArr[i][j]/10000000 == intArr[i-1][j-1]/10000000 && (intArr[i-1][j-1]%10000000)/1000000 == 1 && (i == n || j == n) ||

                intArr[i][j]/10000000 == intArr[i][j-1]/10000000 && (intArr[i][j-1]%10000000)/1000000 == 1 && j == n ) ){
            //System.out.println("i = "+i+"; j = "+j+" helper");
            intArr[i][j] -= 1000000;
            //Значит это не территориальные камни, обнуляем далее
            if (intArr[i-1][j]/10000000 == intArr[i][j]/10000000 && (intArr[i-1][j]%10000000)/1000000 == 1)
                helpScoring(i-1,j);
            if (intArr[i-1][j+1]/10000000 == intArr[i][j]/10000000 && (intArr[i-1][j+1]%10000000)/1000000 == 1)
                helpScoring(i-1,j+1);
            if (intArr[i][j+1]/10000000 == intArr[i][j]/10000000 && (intArr[i][j+1]%10000000)/1000000 == 1)
                helpScoring(i,j+1);
            if (intArr[i+1][j+1]/10000000 == intArr[i][j]/10000000 && (intArr[i+1][j+1]%10000000)/1000000 == 1)
                helpScoring(i+1,j+1);
            if (intArr[i+1][j]/10000000 == intArr[i][j]/10000000 && (intArr[i+1][j]%10000000)/1000000 == 1)
                helpScoring(i+1,j);
            if (intArr[i+1][j-1]/10000000 == intArr[i][j]/10000000 && (intArr[i+1][j-1]%10000000)/1000000 == 1)
                helpScoring(i+1,j-1);
            if (intArr[i][j-1]/10000000 == intArr[i][j]/10000000 && (intArr[i][j-1]%10000000)/1000000 == 1)
                helpScoring(i,j-1);
            if (intArr[i-1][j-1]/10000000 == intArr[i][j]/10000000 && (intArr[i-1][j-1]%10000000)/1000000 == 1)
                helpScoring(i-1,j-1);
        }
    }

    public void territory(int i, int j){

    }

    //Подсчет очков и территорий
    public int[][] scoring(){

        for (int i = 1; i < n+1; i++) {
            for (int j = 1; j < n + 1; j++) {
                if (intArr[i][j] > 0 && (intArr[i][j]%1000000)/1000 == 0){
                    zone2(i, j);
                    zone2++;
                }
            }
        }

        //TEST NEW  ALGHORYTM
        for (int i = 1; i < n+1; i++) {
            for (int j = 1; j < n + 1; j++) {
                if (intArr[i][j] > 1)
                    intArr[i][j] += 1000000;
            }
        }

        for (int i = 1; i < n+1; i++) {
            for (int j = 1; j < n + 1; j++) {
                if ((intArr[i][j]%10000000)/1000000 == 1)
                    helpScoring(i,j);
            }
        }
        //

        System.out.println("new Scroing...");

        zone2=1;

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
                if ((intArr1[i][j] % 1000000) / 1000 > 0 && (intArr1[i][j]%10000000)/1000000 == 1){
                    int oldzone2 = (intArr1[i][j] % 1000000) / 1000;

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

                    //Проверка являются ли территория смешанной

                    for (int i1 = 1; i1 < n+1; i1++) {
                        for (int j1 = 1; j1 < n+1; j1++) {
                            if (intArr2[i1][j1] == 4 && intArr1[i1][j1]/10000000 != intArr1[i][j]/10000000 && (intArr1[i1][j1]%10000000)/1000000 == 1) {
                                for (int i2 = 1; i2 < n+1; i2++) {
                                    for (int j2 = 1; j2 < n+1; j2++) {
                                        if ((intArr1[i2][j2]%1000000)/1000 == oldzone2 && intArrTerritory[i2][j2] != intArr1[i2][j2]/10000000 && intArrTerritory[i2][j2] > 0 &&
                                                (intArr1[i2][j2]%10000000)/1000000 == 1) {
                                            intArr2[i1][j1] = 1;
                                            intArrTerritory[i2][j2] = 0;
                                        }
                                    }
                                }
                            }
                        }
                    }

                    oldzone2 = (intArr1[i][j]%1000000)/1000;
                    for (int k = 0; k < 5; k++) {
                        for (int i1 = 1; i1 < n+1; i1++) {
                            for (int j1 = 1; j1 < n+1; j1++) {
                                if (intArr2[i1][j1] == 4 && intArr2[i1][j1 - 1] > 0 && intArr2[i1][j1 - 1] < 4 && ((intArr1[i1][j1]%10000000)/1000000 != 1 ||
                                        intArr1[i1][j1]/10000000 != 0 && intArr1[i1][j1]/10000000 != intArr1[i][j]/10000000) ||
                                        (intArr1[i1][j1]%10000000)/1000000 >= 1 && intArr2[i1][j1] == 4 && intArr1[i1][j1]/10000000 != intArr1[i][j]/10000000) {
                                    intArr2[i1][j1] = 1;
                                }
                            }
                        }

                        for (int j1 = 1; j1 < n+1; j1++) {
                            for (int i1 = 1; i1 < n+1; i1++) {
                                if (intArr2[i1][j1] == 4 && intArr2[i1 - 1][j1] > 0 && intArr2[i1 - 1][j1] < 4 && ((intArr1[i1][j1]%10000000)/1000000 != 1 ||
                                        intArr1[i1][j1]/10000000 != 0 && intArr1[i1][j1]/10000000 != intArr1[i][j]/10000000) ||
                                        (intArr1[i1][j1]%10000000)/1000000 >= 1  && intArr2[i1][j1] == 4 && intArr1[i1][j1]/10000000 != intArr1[i][j]/10000000) {
                                    intArr2[i1][j1] = 1;
                                }
                            }
                        }

                        for (int i1 = 1; i1 < n+1; i1++) {
                            for (int j1 = n; j1 > 0; j1--) {
                                if (intArr2[i1][j1] == 4 && intArr2[i1][j1+1] > 0 && intArr2[i1][j1+1] < 4 && ((intArr1[i1][j1]%10000000)/1000000 != 1 ||
                                        intArr1[i1][j1]/10000000 != 0 && intArr1[i1][j1]/10000000 != intArr1[i][j]/10000000) ||
                                        (intArr1[i1][j1]%10000000)/1000000 >= 1 && intArr2[i1][j1] == 4 && intArr1[i1][j1]/10000000 != intArr1[i][j]/10000000) {
                                    intArr2[i1][j1] = 1;
                                }
                            }
                        }

                        for (int j1 = 1; j1 < n+1; j1++) {
                            for (int i1 = n; i1 > 0; i1--) {
                                if (intArr2[i1][j1] == 4 && intArr2[i1 + 1][j1] > 0 && intArr2[i1 + 1][j1] < 4 && ((intArr1[i1][j1]%10000000)/1000000 != 1 ||
                                        intArr1[i1][j1]/10000000 != 0 && intArr1[i1][j1]/10000000 != intArr1[i][j]/10000000) ||
                                        (intArr1[i1][j1]%10000000)/1000000 >= 1 && intArr2[i1][j1] == 4 && intArr1[i1][j1]/10000000 != intArr1[i][j]/10000000) {
                                    intArr2[i1][j1] = 1;
                                }
                            }
                        }
                    }


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
                    //Распределение получившихся территорий
                    for (int i1 = 1; i1 < n + 1; i1++) {
                        for (int j1 = 1; j1 < n + 1; j1++) {
                            if (intArr2[i1][j1] == 4)
                                intArrTerritory[i1][j1] = intArr1[i][j] / 10000000;
                            /*if ((intArr1[i1][j1]%10000000)/1000000==1 && intArr2[i1][j1]==4)
                                intArr1[i1][j1] -= 1000000;*/
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

        for (int i = 1; i < n+1; i++) {
            for (int j = 1; j < n + 1; j++) {
                intArr[i][j] -= (intArr[i][j]%1000000-intArr[i][j]%1000);
                intArr[i][j] -= (intArr[i][j]%10000000-intArr[i][j]%1000000);
            }
        }

        // Присвоение очков //
        for (int i1=1; i1 < n+1; i1++){
            for (int j1=1; j1 < n+1; j1++) {
                if (intArrTerritory[i1][j1] == 1) {
                    //Добавление очков черным
                    if (intArr1[i1][j1]==0)
                        bStrategicScore++;
                    else if (intArr1[i1][j1]/10000000==2)
                        bStrategicScore += 2;
                }
                else if (intArrTerritory[i1][j1] == 2) {
                    //Добавление очков белым
                    if (intArr1[i1][j1]==0)
                        wStrategicScore++;
                    else if (intArr1[i1][j1]/10000000==1)
                        wStrategicScore += 2;
                }
            }
        }

        wTotalScore = wScore+wStrategicScore;
        bTotalScore = bScore+bStrategicScore;
        return intArrTerritory;

    }

    public boolean isEye(int i, int j){
        //Проверяем 9 вариантов глаза, начиная с середины, а потом сверху
        if (intArr[i+1][j]/10000000 == cur_player && intArr[i+1][j] > 0 && intArr[i+1][j]%1000 ==  intArr[i-1][j]%1000 && intArr[i+1][j]%1000 ==  intArr[i][j+1]%1000 && intArr[i+1][j]%1000 ==  intArr[i][j-1]%1000
                || intArr[i-1][j] == -1 && intArr[i][j-1]%1000 == intArr[i][j+1]%1000 && intArr[i][j-1]%1000 == intArr[i+1][j]%1000 && intArr[i][j-1]/10000000 == cur_player
                || intArr[i-1][j] == -1 && intArr[i][j+1] == -1 && intArr[i][j-1]%1000 == intArr[i+1][j]%1000 && intArr[i][j-1]/10000000 == cur_player
                || intArr[i][j+1] == -1 && intArr[i-1][j]%1000 == intArr[i+1][j]%1000 && intArr[i-1][j]%1000 == intArr[i][j-1]%1000 && intArr[i-1][j]/10000000 == cur_player
                || intArr[i][j+1] == -1 && intArr[i+1][j] == -1 && intArr[i][j-1]%1000 == intArr[i-1][j]%1000 && intArr[i][j-1]/10000000 == cur_player
                || intArr[i+1][j] == -1 && intArr[i][j-1]%1000 == intArr[i][j+1]%1000 && intArr[i][j-1]%1000 == intArr[i-1][j]%1000 && intArr[i][j-1]/10000000 == cur_player
                || intArr[i+1][j] == -1 && intArr[i][j-1] == -1 && intArr[i-1][j]%1000 == intArr[i][j+1]%1000 && intArr[i-1][j]/10000000 == cur_player
                || intArr[i][j-1] == -1 && intArr[i-1][j]%1000 == intArr[i+1][j]%1000 && intArr[i-1][j]%1000 == intArr[i][j+1]%1000 && intArr[i-1][j]/10000000 == cur_player
                || intArr[i-1][j] == -1 && intArr[i][j-1] == -1 && intArr[i][j+1]%1000 == intArr[i+1][j]%1000 && intArr[i][j+1]/10000000 == cur_player
                )
            return true;
        else
            return false;
    }
}

