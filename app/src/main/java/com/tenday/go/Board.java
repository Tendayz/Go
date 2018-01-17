package com.tenday.go;

import android.provider.Settings;

import java.util.Random;

import static com.tenday.go.Game.patterns;

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

    double worst_prior=1;

    // child with highest number of visits is used (not: best winrate)
    public Node getBestChild(Node root) {
        Node child = root.child;
        Node best_child = null;
        int  best_visits= -1;
        while (child!=null) { // for all children
            if (child.visits>best_visits) {
                best_child=child;
                best_visits=child.visits;
            }
            if ((double)child.wins/child.visits < worst_prior && child.x != 0)
                worst_prior = (double)child.wins/child.visits;
            System.out.println(child.wins+"/"+child.visits+" "+(double)child.wins/child.visits);
            child = child.sibling;
        }
        return best_child;
    }

    public Node root=null;
    public static final double UCTK = 0.44; // 0.44 = sqrt(1/5)
    // Larger values give uniform search
    // Smaller values give very selective search
    public Node UCTSelect(Node node) {
        Node res=null;
        Node next = node.child;
        double best_uct=0;

        while (next!=null) { // for all children
            double uctvalue;
            if (next.visits > 0) {
                double winrate = next.getWinRate();
                double uct = UCTK * Math.sqrt(Math.log(node.visits) / next.visits);
                uctvalue = winrate + uct;
            } else {
                // Always play a random unexplored move first
                uctvalue = 10000 + 1000 * Math.random();
            }
            if (uctvalue > best_uct) { // get max uctvalue of all children
                best_uct = uctvalue;
                res = next;
            }
            next = next.sibling;
        }
        return res;
    }
    // return 0=lose 1=win for current player to move
    int playSimulation(Node n) {
        int randomresult=0;
        if (!endGame && n.child==null && n.visits < 5) { // 10 simulations until chilren are expanded (saves memory)
            randomresult = playRandomGame();
        }
        else if (!endGame) {
            if (n.child == null)
                createChildren(n);
            Node next = UCTSelect(n); // select a move
            if (next == null) { /* ERROR */ // Не ERROR, а отсутствие доступного хода... А значит pass()
                System.out.println("ERROR ERROR ERROR ERROR ERROR ERROR ERROR ERROR ERROR ERROR ERROR ERROR ERROR ERROR ERROR ERROR ERROR ERROR ERROR ERROR ");
                //pass();
            }

            // if (next.x != 0 || next.y != 0) {
            move(next.x, next.y);
            //System.out.println("Not PASS()");
            //}
            //else {
            //System.out.println("Test Bot PASS()");
            //  pass();
            // }

            /*int res = 0;
            if (!endGame)*/
            int res = playSimulation(next);
            randomresult = 1-res;
        } /*else
            res = getScoringWinner()==cur_player ? 1 : 0;
            randomresult = 1-res;
        }*/
        else
            randomresult = (getScoringWinner()==cur_player || getScoringWinner()==0) ? 1 : 0;
        n.update(1-randomresult); //update node (Node-wins are associated with moves in the Nodes)
        return randomresult;
    }
    // generate a move, using the uct algorithm
    int UCTSearch(int numsim) {
        if (!avMoves() || getScoringWinner() == cur_player && !passCheck) {
            return 0;
        }
        long time = System.currentTimeMillis();
        root=new Node(-1,-1); //init uct tree
        createChildren(root);
        Board clone=new Board(n, komi);
        for (int i=0; i<numsim; i++) {
            clone.copyStateFrom(this);
            clone.playSimulation(root);
        }
        Node n=getBestChild(root);

        System.out.println("Time: "+(System.currentTimeMillis()-time)+"ms");
        if ((double)n.wins/n.visits >= 0.05 && /*(double)root.child.wins/root.child.visits*/(worst_prior <= 0.95 || getScoringWinner() != cur_player)) {
            System.out.println("bestPrior = "+(double)n.wins/n.visits+"; worstPrior = "+worst_prior);
            worst_prior = 1;
            return n.x * 100 + n.y;
        }
        else {
            System.out.println("bestPrior = "+(double)n.wins/n.visits+"; worstPrior = "+worst_prior);
            worst_prior = 1;
            return 0;
        }
    }

    private static Random rand = new Random();



    public void makeRandomMove() {
        int x=0;
        int y=0;
        while (true) {
            x=1+rand.nextInt(n);
            y=1+rand.nextInt(n);
            if (checkMove(x,y) && !isEye(x,y)) break;
        }
        move(x,y);
    }

    public boolean checkCellPattern(int i, int j, char c){
        if (    c == '?' ||
                intArr[i][j]/10000000 == cur_player && c == 'X' ||
                intArr[i][j]/10000000 == 3-cur_player && c == 'O' ||
                intArr[i][j]/10000000 != cur_player && c == 'x' ||
                intArr[i][j]/10000000 != 3-cur_player && c == 'o' ||
                intArr[i][j] == 0 && c == '.')
            return true;
        else
            return false;
    }

    public boolean patterns(int i, int j){
        for (int i1=i; i1 < 32; i1++){
            if ( checkCellPattern(i-1,j-1,patterns[i1].charAt(0)) && checkCellPattern(i-1,j,patterns[i1].charAt(1)) && checkCellPattern(i-1,j+1,patterns[i1].charAt(2)) &&
                    checkCellPattern(i,j-1,patterns[i1].charAt(3)) && checkCellPattern(i,j+1,patterns[i1].charAt(5)) && checkCellPattern(i+1,j-1,patterns[i1].charAt(6)) &&
                    checkCellPattern(i+1,j,patterns[i1].charAt(7)) && checkCellPattern(i+1,j+1,patterns[i1].charAt(8)) ) {
                return true;
            }
        }

        return false;
    }

    // return 0=lose 1=win for current player to move
    int playRandomGame() {

        /*Board clone=new Board(this.n, komi);
        clone.copyStateFrom(this);*/

        int cur_player1 = cur_player;
        while (!endGame) {
            if (avMoves()) {
                /*if (intArr[lastMove/100-1][lastMove%100-1] == 0 && patterns(lastMove/100-1,lastMove%100-1)) {
                    move(lastMove / 100 - 1, lastMove % 100 - 1);
                }
                else if (intArr[lastMove/100-1][lastMove%100] == 0 && patterns(lastMove/100-1,lastMove%100)) {
                    move(lastMove / 100 - 1, lastMove % 100);
                }
                else if (intArr[lastMove/100-1][lastMove%100+1] == 0 && patterns(lastMove/100-1,lastMove%100+1)) {
                    move(lastMove / 100 - 1, lastMove % 100 + 1);
                }
                else if (intArr[lastMove/100][lastMove%100-1] == 0 && patterns(lastMove/100,lastMove%100-1)) {
                    move(lastMove / 100, lastMove % 100 - 1);
                }
                else if (intArr[lastMove/100][lastMove%100+1] == 0 && patterns(lastMove/100,lastMove%100+1)) {
                    move(lastMove / 100, lastMove % 100 + 1);
                }
                else if (intArr[lastMove/100+1][lastMove%100-1] == 0 && patterns(lastMove/100+1,lastMove%100-1)) {
                    move(lastMove / 100 + 1, lastMove % 100 - 1);
                }
                else if (intArr[lastMove/100+1][lastMove%100] == 0 && patterns(lastMove/100+1,lastMove%100)) {
                    move(lastMove / 100 + 1, lastMove % 100);
                }
                else if (intArr[lastMove/100+1][lastMove%100+1] == 0 && patterns(lastMove/100+1,lastMove%100+1)) {
                    move(lastMove / 100 + 1, lastMove % 100 + 1);
                }
                else*/
                    makeRandomMove();

                kolvoHodov++;
            }
            else
                pass();
        }
        kolvoHodov = 0;
        return getWinner()==cur_player1 ? 1 : 0;
    }
    // expand children in Node
    void createChildren(Node parent) {
        Node last=parent;
        Node node /*= new Node(0, 0)*/;
        /*last.child = node;
        last = node;*/

        for (int i=1; i<n+1; i++)
            for (int j=1; j<n+1; j++)
                if (checkMove(i,j) && !isEye(i,j)) {
                    node=new Node(i, j);
                    if (patterns(i,j)) {
                        node.visits += 5;
                        node.wins += 5;
                    }
                    else if (i==1 && (j == 1 || intArr[i][j-2] <= 0) && intArr[i][j-1] <= 0 && intArr[i+1][j-1] <= 0 &&
                            intArr[i+1][j] <= 0 && intArr[i+2][j] <= 0 && intArr[i+1][j+1] <= 0 && intArr[i][j+1] <= 0 && (j == n || intArr[i][j+2] <= 0) ||

                            j == n && (i == 1 || intArr[i-2][j] <= 1) && intArr[i-1][j] <= 0 && intArr[i-1][j-1] <= 0 &&
                                    intArr[i][j-2] <= 0 && intArr[i][j-1] <= 0 && intArr[i+1][j-1] <= 0 && intArr[i+1][j] <= 0 && (i == n || intArr[i+2][j] <= 0) ||

                            i == n && (j == 1 || intArr[i][j-2] <= 0) && intArr[i][j-1] <= 0 && intArr[i-1][j-1] <= 0 &&
                                    intArr[i-2][j] <= 0 && intArr[i-1][j] <= 0 && intArr[i-1][j+1] <= 0 && intArr[i][j+1] <= 0 && (j == n || intArr[i][j+2] <= 0) ||

                            j == 1 && (i == 1 || intArr[i-2][j] <= 0) && intArr[i-1][j] <= 0 && intArr[i-1][j+1] <= 0 &&
                                    intArr[i][j+2] <= 0 && intArr[i][j+1] <= 0 && intArr[i+1][j+1] <= 0 && intArr[i+1][j] <= 0 && (i == n || intArr[i+2][j] <= 0) ||

                            i==2 && j>1 && j<n && intArr[i][j-2] <= 0 && intArr[i][j-1] <= 0 && intArr[i+1][j-1] <= 0 &&
                                    intArr[i-1][j] <= 0 && intArr[i][j+1] <= 0 && intArr[i][j+2] <= 0 && intArr[i+1][j+1] <= 0 && intArr[i+1][j] <= 0 &&
                                    intArr[i+2][j] <= 0 && intArr[i+1][j-1] <= 0 ||

                            j==n-1 && i>1 && i<n && intArr[i][j-2] <= 0 && intArr[i][j-1] <= 0 && intArr[i+1][j-1] <= 0 &&
                                    intArr[i-1][j] <= 0 && intArr[i-2][j] <= 0 && intArr[i-1][j+1] <= 0 && intArr[i][j+1] <= 0 && intArr[i+1][j+1] <= 0 && intArr[i+1][j] <= 0 &&
                                    intArr[i+2][j] <= 0 && intArr[i+1][j-1] <= 0 ||

                            i==n-1 && j>1 && j<n && intArr[i][j-2] <= 0 && intArr[i][j-1] <= 0 && intArr[i+1][j-1] <= 0 &&
                                    intArr[i-1][j] <= 0 && intArr[i-2][j] <= 0 && intArr[i-1][j+1] <= 0 && intArr[i][j+1] <= 0 && intArr[i][j+2] <= 0 && intArr[i+1][j+1] <= 0 &&
                                    intArr[i+1][j] <= 0 && intArr[i+1][j-1] <= 0 ||

                            j==2 && i>1 && i<n && intArr[i][j-1] <= 0 && intArr[i+1][j-1] <= 0 && intArr[i-1][j] <= 0 &&
                                    intArr[i-2][j] <= 0 && intArr[i-1][j+1] <= 0 && intArr[i][j+1] <= 0 && intArr[i][j+2] <= 0 && intArr[i+1][j+1] <= 0 && intArr[i+1][j] <= 0 &&
                                    intArr[i+2][j] <= 0 && intArr[i+1][j-1] <= 0
                            ) {
                        node.visits += 5;
                    }
                    if (last==parent) last.child=node;
                    else last.sibling=node;
                    last=node;
                }

    }

    public boolean avMoves(){
        for (int i = 1; i < n+1; i++){
            for (int j = 1; j < n+1; j++){
                if (checkMove(i,j) && !isEye(i,j))
                    return true;
            }
        }
        return false;
    }
    void copyStateFrom(Board b) {
        for (int i = 0; i < n+2; i++){
            for (int j = 0; j < n+2; j++){
                intArr[i][j] = b.intArr[i][j];
                //intArrTerritory[i][j] = b.intArrTerritory[i][j];
            }
        }
        lastMove = b.lastMove;
        passCheck = b.passCheck;
        cur_player = b.cur_player;
        endGame = b.endGame;
        zone = b.zone;
        zone2 = b.zone2;
        bScore = b.bScore;
        wScore = b.wScore;
        bTotalScore = b.bTotalScore;
        wTotalScore = b.wTotalScore;
        checkRuleKo = b.checkRuleKo;
        bot = true;
    }

    public int getBScore (){
        return bTotalScore;
    }

    public float getWScore (){
        if (komi!=0)
            return wTotalScore+komi;
        else
            return wTotalScore;
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

    //В будущем планируется добавить возвращаемый тип boolean, чтобы понимать были ли съедены камни после хода
    //Это позволит исключить лишние asBoard(), а также позволит передавать детям avMoves или сделать возвращаемый тип массивом со съеденными координатами
    public void move(int i, int j) {
        if (!endGame /*&& checkMove(i, j)*/) {
            passCheck = true;

            intArr[i][j] = cur_player*10000000;
            zone++;

            /*int scoreBefore;

            if (cur_player == 1)
                scoreBefore = bScore;
            else
                scoreBefore = wScore;

            int br;

            if (intArr[i - 1][j]/10000000 == 3-cur_player) {
                fillBreath(i - 1, j, intArr[i - 1][j] % 100 - 1);
                if (intArr[i - 1][j] % 100 == 0)
                    killGroup(i - 1, j);
            }

            if (intArr[i][j+1]/10000000 == 3-cur_player) {
                fillBreath(i, j + 1, intArr[i][j + 1] % 100 - 1);
                if (intArr[i][j + 1] % 100 == 0)
                    killGroup(i, j + 1);
            }

            if (intArr[i+1][j]/10000000 == 3-cur_player) {
                fillBreath(i + 1, j, intArr[i + 1][j] % 100 - 1);
                if (intArr[i+1][j]%100 == 0)
                    killGroup(i+1,j);
            }

            if (intArr[i][j-1]/10000000 == 3-cur_player) {
                fillBreath(i, j - 1, intArr[i][j - 1] % 100 - 1);
                if (intArr[i][j-1]%100 == 0)
                    killGroup(i,j-1);
            }*/

            /*if (intArr[i-1][j]/10000000 == 3-cur_player)
                intArr[i-1][j]--;
            if (intArr[i][j+1]/10000000 == 3-cur_player)
                intArr[i][j+1]--;
            if (intArr[i+1][j]/10000000 == 3-cur_player)
                intArr[i+1][j]--;
            if (intArr[i][j-1]/10000000 == 3-cur_player)
                intArr[i][j-1]--;

            if (intArr[i - 1][j]/10000000 == 3-cur_player && intArr[i - 1][j]%100 > 0)
                fillBreath(i - 1, j, intArr[i - 1][j] % 100);
            else if (intArr[i - 1][j]/10000000 == 3-cur_player && intArr[i - 1][j]%100 == 0)
                killGroup(i - 1,j);

            if (intArr[i][j+1]/10000000 == 3-cur_player && intArr[i][j+1]%100 > 0)
                fillBreath(i, j+1, intArr[i][j+1] % 100);
            else if (intArr[i][j+1]/10000000 == 3-cur_player && intArr[i][j+1]%100 == 0)
                killGroup(i,j+1);

            if (intArr[i + 1][j]/10000000 == 3-cur_player && intArr[i + 1][j]%100 > 0)
                fillBreath(i + 1, j, intArr[i + 1][j] % 100);
            else if (intArr[i + 1][j]/10000000 == 3-cur_player && intArr[i + 1][j]%100 == 0)
                killGroup(i + 1,j);

            if (intArr[i][j-1]/10000000 == 3-cur_player && intArr[i][j-1]%100 > 0)
                fillBreath(i, j-1, intArr[i][j-1] % 100);
            else if (intArr[i][j-1]/10000000 == 3-cur_player && intArr[i][j-1]%100 == 0)
                killGroup(i,j-1);

            br = getBreath(i,j);
            //System.out.println("br="+br);

            fillBreath(i, j, br);


            //Правило Ко
            if ((cur_player == 1 ? bScore-scoreBefore == 1 : wScore-scoreBefore == 1) && (intArr[i-1][j]/10000000 == 3-cur_player || i==1) && (intArr[i][j+1]/10000000 == 3-cur_player || j==n) &&
                    (intArr[i+1][j]/10000000 == 3-cur_player || i==n) && intArr[i][j]%100 == 1) {
                //System.out.println("MDAAAAA intArr[i][j+1]/10000000 = "+intArr[i][j+1]/10000000+"; 3-cur_player = "+(3-cur_player));
                checkRuleKo = (3 - cur_player) * 10000 + i * 100 + j - 1;
            }
            else if ((cur_player == 1 ? bScore-scoreBefore == 1 : wScore-scoreBefore == 1) && (intArr[i][j+1]/10000000 == 3-cur_player || j==n) && (intArr[i+1][j]/10000000 == 3-cur_player || i==n) &&
                    (intArr[i][j-1]/10000000 == 3-cur_player || j==1) && intArr[i][j]%100 == 1)
                checkRuleKo = (3-cur_player)*10000+(i-1)*100+j;
            else if ((cur_player == 1 ? bScore-scoreBefore == 1 : wScore-scoreBefore == 1) && (intArr[i-1][j]/10000000 == 3-cur_player || i==1) && (intArr[i][j-1]/10000000 == 3-cur_player || j==1) &&
                    (intArr[i+1][j]/10000000 == 3-cur_player || i==n) && intArr[i][j]%100 == 1)
                checkRuleKo = (3 - cur_player) * 10000 + i * 100 + j + 1;
            else if ((cur_player == 1 ? bScore-scoreBefore == 1 : wScore-scoreBefore == 1) && (intArr[i-1][j]/10000000 == 3-cur_player || i==1) && (intArr[i][j-1]/10000000 == 3-cur_player || j==1) &&
                    (intArr[i][j+1]/10000000 == 3-cur_player || j==n) && intArr[i][j]%100 == 1)
                checkRuleKo = (3-cur_player)*10000+(i+1)*100+j;
            else
                checkRuleKo = 0;*/

            //System.out.println("checkTuleKo = "+checkRuleKo);

                /*||

                    cur_player == 2 && wScore-scoreBefore == 1 && intArr[i-1][j]/10000000 != cur_player && intArr[i][j+1]/10000000 != cur_player &&
                            intArr[i+1][j]/10000000 != cur_player && intArr[i][j-1]/10000000 != cur_player && intArr[i][j]%1000 == 1){
                checkRuleKo += (3-cur_player)*10000+i*100+j;
            }*/

            //int scoreBefore = cur_player == 1 ? bScore : wScore;
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

            /*if (checkRuleKo == ruleKo[1] && ((ruleKo[1]%100000000)/1000000 != i || (ruleKo[1]%1000000)/10000 != j || ruleKo[1]/100000000 != intArr[i][j]/10000000))
                ruleKo[1] = 0;

            ruleKo[0] = ruleKo[1];*/
            lastMove = i*100+j;
            cur_player = 3-cur_player;
        }
    }

    /*public void killGroup(int i, int j){
        intArr[i][j] = 0;
        if (cur_player == 1)
            bScore++;
        else
            wScore++;

        if (intArr[i-1][j]/10000000 == cur_player)
            fillBreath(i-1, j, getBreath(i-1,j));
        if (intArr[i][j+1]/10000000 == cur_player)
            fillBreath(i, j+1, getBreath(i,j+1));
        if (intArr[i+1][j]/10000000 == cur_player)
            fillBreath(i+1, j, getBreath(i+1,j));
        if (intArr[i][j-1]/10000000 == cur_player)
            fillBreath(i, j-1, getBreath(i,j-1));


        if (intArr[i-1][j]/10000000 == 3-cur_player){
            killGroup(i-1,j);
        }
        if (intArr[i][j+1]/10000000 == 3-cur_player){
            killGroup(i,j+1);
        }
        if (intArr[i+1][j]/10000000 == 3-cur_player){
            killGroup(i+1,j);
        }
        if (intArr[i][j-1]/10000000 == 3-cur_player){
            killGroup(i,j-1);
        }
    }*/

    //Версия уже без учета zone
    public void fillBreath(int i, int j, int breath){

        /*if (intArr[i][j] < 1){
            System.out.println("WTF WTF WTF TFW TFWT FWTWFTWFWTFW WTFWTF");
        }*/


        /*if (intArr[i-1][j]==1)
            intArr[i-1][j] = 0;
        if (intArr[i][j+1]==1)
            intArr[i][j+1] = 0;
        if (intArr[i+1][j]==1)
            intArr[i+1][j] = 0;
        if (intArr[i][j-1]==1)
            intArr[i][j-1] = 0;*/

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

    /*public int getBreath(int i, int j){
        int br = breath(i,j);
        for (int i1 = 1; i1 < n+1; i1++){
            for (int j1 = 1; j1 < n+1; j1++){
                if (intArr[i1][j1] == 1)
                    intArr[i1][j1] = 0;
                else if ((intArr[i1][j1]%1000)/100 == 1)
                    intArr[i1][j1] -= 100;
            }
        }

        return br;
    }*/

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

            //int ruleKoBefore = ruleKo[1];
            int zone = this.zone + 1/*, zone2 = this.zone2 + 1*/;

            intArr[i][j] = cur_player * 10000000;

            intArr[i][j] += zone /*+ zone2 * 1000*/;

            if (!(checkRuleKo/10000 == cur_player && (checkRuleKo%10000)/100 == i && checkRuleKo%100 == j) &&
                    (checkMove1(i - 1, j, i, j) || checkMove1(i, j + 1, i, j) || checkMove1(i + 1, j, i, j) || checkMove1(i, j - 1, i, j) ||
                    checkMove2(i - 1, j, i, j) || checkMove2(i, j + 1, i, j) || checkMove2(i + 1, j, i, j) || checkMove2(i, j - 1, i, j))) {
                /*if (ruleKoBefore == checkRuleKo && ((checkRuleKo % 100000000) / 1000000 != i || (checkRuleKo % 1000000) / 10000 != j || checkRuleKo / 100000000 != intArr[i][j] / 10000000))
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
                }*/
                intArr[i][j] = 0;
                return true;
            } else {
                //checkRuleKo = 0;
                intArr[i][j] = 0;
                return false;
            }
            /*if (intArr[i][j] != 0 || cur_player == checkRuleKo/10000 && i == (checkRuleKo%10000)/100 && j == checkRuleKo%100 ||
                    (intArr[i-1][j] != 0 && !(intArr[i-1][j]/10000000 == cur_player && intArr[i-1][j]%100 > 1) && !(intArr[i-1][j]/10000000 == 3-cur_player && intArr[i-1][j]%1000 == 1)) &&
                            (intArr[i][j+1] != 0 && !(intArr[i][j+1]/10000000 == cur_player && intArr[i][j+1]%100 > 1) && !(intArr[i][j+1]/10000000 == 3-cur_player && intArr[i][j+1]%1000 == 1)) &&
                            (intArr[i+1][j] != 0 && !(intArr[i+1][j]/10000000 == cur_player && intArr[i+1][j]%100 > 1) && !(intArr[i+1][j]/10000000 == 3-cur_player && intArr[i+1][j]%1000 == 1)) &&
                            (intArr[i][j-1] != 0 && !(intArr[i][j-1]/10000000 == cur_player && intArr[i][j-1]%100 > 1) && !(intArr[i][j-1]/10000000 == 3-cur_player && intArr[i][j-1]%1000 == 1))){
                return false;
            }
            else return true;*/


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
            /*byte rule = 0;
            for (int i1 = 1; i1 < n+1; i1++) {
                for (int j1 = 1; j1 < n+1; j1++) {
                    if (intArr[i1][j1] % 1000 == oldzone)
                        rule++;
                }
            }
            if (rule == 1)
                checkRuleKo = (intArr[ii][jj] / 10000000) * 100000000 + ii * 1000000 + jj * 10000 + i * 100 + j;*/

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

        /*System.out.println("wtf dude...");
        for (int i2 = 1; i2 < n+2; i2++) {
            System.out.println();
            for (int j2 = 1; j2 < n + 1; j2++) {
                System.out.print(intArr[i2][j2]+" ");
            }
        }*/

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
                || intArr[i-1][j] == -1 && intArr[i][j-1] == -1 && intArr[i][j+1]%1000 == intArr[i+1][j]%1000 && intArr[i][j+1]/10000000 == cur_player /*||

                j==1 && i == 2 && intArr[i][j+1]%1000 == intArr[i+1][j]%1000 && intArr[i-1][j]/10000000 == cur_player ||
                j==2 && i == 1 && intArr[i+1][j]%1000 == intArr[i][j+1]%1000 && intArr[i-1][j]/10000000 == cur_player ||
                j==1 && i == n-1 && intArr[i-1][j]%1000 == intArr[i][j+1]%1000 && intArr[i+1][j]/10000000 == cur_player*/
                /*intArr[i][j] == 0 &&
                        intArr[i-1][j] != 0 && intArr[i-1][j]/10000000 != 3-cur_player &&
                        intArr[i][j+1] != 0 && intArr[i][j+1]/10000000 != 3-cur_player &&
                        intArr[i+1][j] != 0 && intArr[i+1][j]/10000000 != 3-cur_player &&
                        intArr[i][j-1] != 0 && intArr[i][j-1]/10000000 != 3-cur_player &&
                        (intArr[i-1][j-1] != 0 && intArr[i-1][j-1]/10000000 != 3-cur_player && intArr[i-1][j+1] != 0 && intArr[i-1][j+1]/10000000 != 3-cur_player &&
                            intArr[i+1][j-1] != 0 && intArr[i+1][j-1]/10000000 != 3-cur_player && intArr[i+1][j+1] != 0 && intArr[i+1][j+1]/10000000 != 3-cur_player ||

                                intArr[i-1][j+1] != 0 && intArr[i-1][j+1]/10000000 != 3-cur_player &&
                                        intArr[i+1][j-1] != 0 && intArr[i+1][j-1]/10000000 != 3-cur_player && intArr[i+1][j+1] != 0 && intArr[i+1][j+1]/10000000 != 3-cur_player ||

                                intArr[i-1][j-1] != 0 && intArr[i-1][j-1]/10000000 != 3-cur_player &&
                                        intArr[i+1][j-1] != 0 && intArr[i+1][j-1]/10000000 != 3-cur_player && intArr[i+1][j+1] != 0 && intArr[i+1][j+1]/10000000 != 3-cur_player ||

                                intArr[i-1][j-1] != 0 && intArr[i-1][j-1]/10000000 != 3-cur_player &&
                                        intArr[i-1][j+1] != 0 && intArr[i-1][j+1]/10000000 != 3-cur_player && intArr[i+1][j+1] != 0 && intArr[i+1][j+1]/10000000 != 3-cur_player ||

                                intArr[i-1][j-1] != 0 && intArr[i-1][j-1]/10000000 != 3-cur_player &&
                                        intArr[i-1][j+1] != 0 && intArr[i-1][j+1]/10000000 != 3-cur_player && intArr[i+1][j-1] != 0 && intArr[i+1][j-1]/10000000 != 3-cur_player
                        )*/
                )
            return true;
        else
            return false;
    }

    public int getWinner() {
        int wStrategicScore = 0, bStrategicScore = 0;
        for (int i = 1; i < n + 1; i++) {
            for (int j = 1; j < n + 1; j++) {
                if (intArr[i][j]/10000000 == 1 ||
                        intArr[i][j] == 0 && (intArr[i - 1][j] / 10000000 == 1 || intArr[i + 1][j] / 10000000 == 1 || intArr[i][j + 1] / 10000000 == 1 || intArr[i][j - 1] / 10000000 == 1))
                    bStrategicScore++;
                else if (intArr[i][j]/10000000 == 2 ||
                        intArr[i][j] == 0 && (intArr[i - 1][j] / 10000000 == 2 || intArr[i + 1][j] / 10000000 == 2 || intArr[i][j + 1] / 10000000 == 2 || intArr[i][j - 1] / 10000000 == 2))
                    wStrategicScore++;
            }
        }
        if (bStrategicScore+bScore > wStrategicScore+wScore+komi)
            return 1;
        else /*if (bStrategicScore < wStrategicScore)*/
            return 2;
        //else return 0;
    }
}
