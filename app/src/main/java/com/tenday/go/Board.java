package com.tenday.go;

import android.util.Log;

public class Board {
    int n;
    int ruleKo[];
    int[][] intArr, intArrTerritory;
    boolean passCheck, m=false, endGame = false;
    int zone=0, zone2=0, bScore=0, wScore=0, checkRuleKo=0;

    private static final String TAG = "myLogs";

    public Board(int n){
        this.n = n;

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

    public void move(int i, int j) {
        if (!endGame && checkMove(i, j)) {
            if (!m) {
                m = true;
                passCheck = true;
                intArr[i][j] = 10000000;
            } else if (m) {
                m = false;
                passCheck = true;
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

            for (int i1 = 1; i1 < n+1; i1++) {
                Log.d(TAG, intArr[i1][1] + "\t" + intArr[i1][2] + "\t" + intArr[i1][3] + "\t" + intArr[i1][4] + "\t" + intArr[i1][5] + "\t" + intArr[i1][6] + "\t" + intArr[i1][7] + "\t" + intArr[i1][8] + "\t" + intArr[i1][9] + "\t" + "\n");
            }
            for (int i1 = 1; i1 < n+1; i1++) {
                Log.d(TAG, intArrTerritory[i1][1] + "\t" + intArrTerritory[i1][2] + "\t" + intArrTerritory[i1][3] + "\t" + intArrTerritory[i1][4] + "\t" + intArrTerritory[i1][5] + "\t" + intArrTerritory[i1][6] + "\t" + intArrTerritory[i1][7] + "\t" + intArrTerritory[i1][8] + "\t" + intArrTerritory[i1][9] + "\t" + "\n");
            }
            Log.d(TAG, "wScore = "+wScore);
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
        if (intArr[i][j] / 10000000 != intArr[ii][jj] / 10000000 && intArr[i][j] / 10000000 != 0 && intArr[i][j] != -1) {

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
        else if (intArr[i][j] == -1)
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

}
