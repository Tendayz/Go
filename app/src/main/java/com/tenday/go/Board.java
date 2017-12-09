package com.tenday.go;

import android.provider.Settings;

public class Board {
    int n;
    int ruleKo[];
    int[][] intArr, intArrTerritory;
    boolean passCheck, m=false, endGame = false;
    int zone=0, zone2=0, bScore=0, wScore=0, bTotalScore=0, wTotalScore=0, checkRuleKo=0;
    float komi;

    public Board(int n, float komi){
        this.n = n;
        this.komi = komi;

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
    }

    public int getSize() { return n; }

    public int getBScore (){
        return bTotalScore;
    }

    public float getWScore (){
        if (komi!=0)
            return wTotalScore+komi;
        else
            return wTotalScore;
    }

    public int getMovesColor(){
        if (m)
            return 2;
        else
            return 1;
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
        m=false;
        zone = 0;
        zone2 = 0;

        ruleKo[1] = 0;
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
            if (!m) {
                m = true;
                intArr[i][j] = 10000000;
            } else if (m) {
                m = false;
                intArr[i][j] = 20000000;
            }

            int checkRuleKo = ruleKo[1];
            zone++;
            zone2++;

            intArr[i][j] += zone + zone2*1000;
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

            if (checkRuleKo == ruleKo[1] && ((ruleKo[1]%100000000)/1000000 != i || (ruleKo[1]%1000000)/10000 != j || ruleKo[1]/100000000 != intArr[i][j]/10000000))
                ruleKo[1] = 0;

            int oldzone2;
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

            ruleKo[0] = ruleKo[1];
        }
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

            if (score == 1)
                ruleKo[1] = (intArr[ii][jj] / 10000000) * 100000000 + ii * 1000000 + jj * 10000 + i * 100 + j;
        }
    }

    //Присоединение к союзным камням
    private void move2(int i, int j, int ii, int jj) {
        if (intArr[i][j] / 10000000 == intArr[ii][jj] / 10000000) {
            int
                    oldzone = intArr[i][j] % 1000,
                    oldzone2 = (intArr[i][j]%1000000)/1000,
                    zone = intArr[ii][jj] % 1000,
                    zone2 = (intArr[ii][jj] % 1000000)/1000;

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
    }

    //Проверка допустимости хода
    public boolean checkMove(int i, int j){

        if (intArr[i][j] == 0) {

            int ruleKoBefore = ruleKo[1];
            int zone = this.zone + 1, zone2 = this.zone2 + 1;

            if (!m)
                intArr[i][j] = 10000000;
            else
                intArr[i][j] = 20000000;

            intArr[i][j] += zone + zone2 * 1000;


            if (checkMove1(i - 1, j, i, j) || checkMove1(i, j + 1, i, j) || checkMove1(i + 1, j, i, j) || checkMove1(i, j - 1, i, j) ||
                    checkMove2(i - 1, j, i, j) || checkMove2(i, j + 1, i, j) || checkMove2(i + 1, j, i, j) || checkMove2(i, j - 1, i, j)) {
                if (ruleKoBefore == checkRuleKo && ((checkRuleKo % 100000000) / 1000000 != i || (checkRuleKo % 1000000) / 10000 != j || checkRuleKo / 100000000 != intArr[i][j] / 10000000))
                    checkRuleKo = 0;
                if (ruleKo[0] / 100000000 != intArr[i][j] / 10000000 && (ruleKo[0] % 100000000) / 1000000 == (checkRuleKo % 10000) / 100 && (ruleKo[0] % 1000000) / 10000 == checkRuleKo % 100
                        && (checkRuleKo % 100000000) / 1000000 == (ruleKo[0] % 10000) / 100 && (checkRuleKo % 1000000) / 10000 == ruleKo[0] % 100 && ruleKo[0] != 0 && checkRuleKo != 0) {
                    checkRuleKo = 0;
                    intArr[i][j] = 0;
                    return false;
                } else {
                    checkRuleKo = 0;
                    intArr[i][j] = 0;
                    return true;
                }
            } else {
                checkRuleKo = 0;
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

            //Правило Ко
            byte rule = 0;
            for (int i1 = 1; i1 < n+1; i1++) {
                for (int j1 = 1; j1 < n+1; j1++) {
                    if (intArr[i1][j1] % 1000 == oldzone)
                        rule++;
                }
            }
            if (rule == 1)
                checkRuleKo = (intArr[ii][jj] / 10000000) * 100000000 + ii * 1000000 + jj * 10000 + i * 100 + j;

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
                    oldzone2 = (intArr[i][j]%1000000)/1000,
                    zone = intArr[ii][jj] % 1000;

            intArr[ii][jj] = (intArr[ii][jj]/1000000)*1000000 + oldzone + oldzone2*1000;

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
            if (m)
                m = false;
            else
                m = true;
        }
        else{
            endGame = true;
        }
    }

    //Подсчет очков и территорий
    public int[][] scoring(){
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

                    oldzone2 = (intArr1[i][j]%1000000)/1000;
                    for (int k = 0; k < 5; k++) {
                        for (int i1 = 1; i1 < n+1; i1++) {
                            for (int j1 = 1; j1 < n+1; j1++) {
                                if (intArr2[i1][j1] == 4 && intArr2[i1][j1 - 1] > 0 && intArr2[i1][j1 - 1] < 4 && ((intArr1[i1][j1]%10000000)/1000000 != 1 ||
                                        intArr1[i1][j1]/10000000 != 0 && intArr1[i1][j1]/10000000 != intArr1[i][j]/10000000) ||
                                        (intArr1[i1][j1]%10000000)/1000000 >= 1 && intArr2[i1][j1] == 4 && intArr1[i1][j1]/10000000 != intArr1[i][j]/10000000 /*&& mix*/ &&
                                                (i1 == 1 || j1 == 1 || i1 == n || j1 == n)) {
                                    intArr2[i1][j1] = 1;
                                }
                            }
                        }

                        for (int j1 = 1; j1 < n+1; j1++) {
                            for (int i1 = 1; i1 < n+1; i1++) {
                                if (intArr2[i1][j1] == 4 && intArr2[i1 - 1][j1] > 0 && intArr2[i1 - 1][j1] < 4 && ((intArr1[i1][j1]%10000000)/1000000 != 1 ||
                                        intArr1[i1][j1]/10000000 != 0 && intArr1[i1][j1]/10000000 != intArr1[i][j]/10000000) ||
                                        (intArr1[i1][j1]%10000000)/1000000 >= 1  && intArr2[i1][j1] == 4 && intArr1[i1][j1]/10000000 != intArr1[i][j]/10000000 /*&& mix*/ &&
                                                (i1 == 1 || j1 == 1 || i1 == n || j1 == n)) {
                                    intArr2[i1][j1] = 1;
                                }
                            }
                        }

                        for (int i1 = 1; i1 < n+1; i1++) {
                            for (int j1 = n; j1 > 0; j1--) {
                                if (intArr2[i1][j1] == 4 && intArr2[i1][j1+1] > 0 && intArr2[i1][j1+1] < 4 && ((intArr1[i1][j1]%10000000)/1000000 != 1 ||
                                        intArr1[i1][j1]/10000000 != 0 && intArr1[i1][j1]/10000000 != intArr1[i][j]/10000000) ||
                                        (intArr1[i1][j1]%10000000)/1000000 >= 1 && intArr2[i1][j1] == 4 && intArr1[i1][j1]/10000000 != intArr1[i][j]/10000000 /*&& mix*/ &&
                                                (i1 == 1 || j1 == 1 || i1 == n || j1 == n)) {
                                    intArr2[i1][j1] = 1;
                                }
                            }
                        }

                        for (int j1 = 1; j1 < n+1; j1++) {
                            for (int i1 = n; i1 > 0; i1--) {
                                if (intArr2[i1][j1] == 4 && intArr2[i1 + 1][j1] > 0 && intArr2[i1 + 1][j1] < 4 && ((intArr1[i1][j1]%10000000)/1000000 != 1 ||
                                        intArr1[i1][j1]/10000000 != 0 && intArr1[i1][j1]/10000000 != intArr1[i][j]/10000000) ||
                                        (intArr1[i1][j1]%10000000)/1000000 >= 1 && intArr2[i1][j1] == 4 && intArr1[i1][j1]/10000000 != intArr1[i][j]/10000000 /*&& mix*/ &&
                                                (i1 == 1 || j1 == 1 || i1 == n || j1 == n)) {
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
        /*if (komi != 0)
            textWhite.setText(getResources().getString(R.string.score_white) + " " + (wTotalScore+komi));
        else
            textWhite.setText(getResources().getString(R.string.score_white) + " " + wTotalScore);
        textBlack.setText(getResources().getString(R.string.score_black) + " " +bTotalScore);*/
        return intArrTerritory;

    }

    public boolean checkEye(int i, int j){
        //Проверяем 9 вариантов глаза, начиная с середины, а потом сверху
        if (intArr[i+1][j] > 0 && intArr[i+1][j]%1000 ==  intArr[i-1][j]%1000 && intArr[i+1][j]%1000 ==  intArr[i][j+1]%1000 && intArr[i+1][j]%1000 ==  intArr[i][j-1]%1000
                || intArr[i-1][j] == -1 && intArr[i][j-1]%1000 == intArr[i][j+1]%1000 && intArr[i][j-1]%1000 == intArr[i+1][j]%1000 && intArr[i][j-1] > 0
                || intArr[i-1][j] == -1 && intArr[i][j+1] == -1 && intArr[i][j-1]%1000 == intArr[i+1][j]%1000 && intArr[i][j-1] > 0
                || intArr[i][j+1] == -1 && intArr[i-1][j]%1000 == intArr[i+1][j]%1000 && intArr[i-1][j]%1000 == intArr[i][j-1]%1000 && intArr[i-1][j] > 0
                || intArr[i][j+1] == -1 && intArr[i+1][j] == -1 && intArr[i][j-1]%1000 == intArr[i-1][j]%1000 && intArr[i][j-1] > 0
                || intArr[i+1][j] == -1 && intArr[i][j-1]%1000 == intArr[i][j+1]%1000 && intArr[i][j-1]%1000 == intArr[i-1][j]%1000 && intArr[i][j-1] > 0
                || intArr[i+1][j] == -1 && intArr[i][j-1] == -1 && intArr[i-1][j]%1000 == intArr[i][j+1]%1000 && intArr[i-1][j]%1000 > 0
                || intArr[i][j-1] == -1 && intArr[i-1][j]%1000 == intArr[i+1][j]%1000 && intArr[i-1][j]%1000 == intArr[i][j+1]%1000 && intArr[i-1][j] > 0
                || intArr[i-1][j] == -1 && intArr[i][j-1] == -1 && intArr[i][j+1]%1000 == intArr[i+1][j]%1000 && intArr[i][j+1] > 0
                )
            return true;
        else
            return false;
    }

    public void botScoring() {
        int wStrategicScore = 0, bStrategicScore = 0;
        for (int i = 1; i < n + 1; i++) {
            for (int j = 1; j < n + 1; j++) {
                if (intArr[i][j] == 0 && (intArr[i - 1][j] / 10000000 == 1 || intArr[i + 1][j] / 10000000 == 1 || intArr[i][j + 1] / 10000000 == 1 || intArr[i][j - 1] / 10000000 == 1))
                    bStrategicScore++;
                else if (intArr[i][j] == 0 && (intArr[i - 1][j] / 10000000 == 2 || intArr[i + 1][j] / 10000000 == 2 || intArr[i][j + 1] / 10000000 == 2 || intArr[i][j - 1] / 10000000 == 2))
                    wStrategicScore++;
            }
        }
        wTotalScore = wScore+wStrategicScore;
        bTotalScore = bScore+bStrategicScore;
    }
}
