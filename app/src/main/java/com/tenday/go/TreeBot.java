package com.tenday.go;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * Created by 18006 on 18.11.2017.
 */

public class TreeBot {

    public Node root;
    public Node parent = null;

    private static Random random = new Random();

    //Присваивает экзмпляру board1 экземпляр board2
    private Board asBoard(Board board2){
        Board board1 = new Board(board2.n, board2.komi);
        board1.ruleKo[0]=board2.ruleKo[0];
        board1.ruleKo[1]=board2.ruleKo[1];
        for (int i = 0; i < board1.n+2; i++){
            for (int j = 0; j < board1.n+2; j++){
                board1.intArr[i][j] = board2.intArr[i][j];
                board1.intArrTerritory[i][j] = board2.intArrTerritory[i][j];
            }
        }
        board1.passCheck = board2.passCheck;
        board1.m = board2.m;
        board1.endGame = board2.endGame;
        board1.zone = board2.zone;
        board1.zone2 = board2.zone2;
        board1.bScore = board2.bScore;
        board1.wScore = board2.wScore;
        board1.bTotalScore = board2.bTotalScore;
        board1.wTotalScore = board2.wTotalScore;
        board1.checkRuleKo = board2.checkRuleKo;
        return board1;
    }

    static int[] generateRandom(int k) {
        int [] arr = new int[k];
        int tmp, i = 0, j;
        boolean addElement;
        while(i < k){
            tmp = Math.abs(random.nextInt()) % k;
            addElement = true;
            for (j = 0; j < i; j++){
                if (arr[j] == tmp)
                    addElement = false;
            }

            if (addElement){
                arr[i] = tmp;
                i++;
            }
        }
        return arr;
    }


    public TreeBot(Board board) {
        root = new Node();
        root.data = asBoard(board);
        root.children = new ArrayList<Node>();
    }

    public static class Node{
        private Board data;
        private List<Node> children;
        private Node parent = this.parent;
    }

    public TreeBot addChild(Board child) {
        TreeBot childNode = new TreeBot(child);
        childNode.parent = this.root;
        root.children.add(childNode.root);
        return childNode;
    }

    public Board getBoard(){
        return root.data;
    }

    public int bot(TreeBot root) {
        Board board1 = asBoard(root.getBoard()), board2;
        int n = board1.n, i, j;
        int winner;

        int[] avMoves = new int[n * n];
        int k = 0;

        //Определяем доступные ходы и записываем их в массив avMoves
        for (i = 1; i < n+1; i++) {
            for (j = 1; j < n+1; j++) {
                if (board1.checkMove(i, j) && !board1.checkEye(i, j)) {
                    avMoves[k] = i * 100 + j;
                    k++;
                }
            }
        }

        //Если доступных ходов нет, то возвращаем победителя
        if (k == 0) {
            board1.scoring();
            if (board1.getBScore() > board1.getWScore()) {
                return 1;
            } else if (board1.getBScore() < board1.getWScore()) {
                return 2;
            } else
                return 0;
        }


        //Зарандомленные доступные ходы
        int[] rand = generateRandom(k);

        //Кол-во партий
        int amount = 0;

        //Приоритетность ходов
        int movePriority[] = new int[k];

        //Инициализация
        while (amount < 1000){ //В данном случае, 5000 раз будет выполняться только в самом верху,
            // так как ниже есть проверка на самый верхний узел, иначе return, и, следовательно, выход из цикла
            for (i = 0; i < k; i++) {
                board2 = asBoard(board1);
                board2.move(avMoves[rand[i]] / 100, avMoves[rand[i]] % 100);
                if (root.parent != null)
                    return bot(root.addChild(board2)); //Добавит новый узел в дерево и вызовит функцию бот с этим узлом в качестве аргумента. И возвратит все этого вышестоящей функции, если имеется
                else if (root.parent == null){
                    amount++;
                    winner = bot(root.addChild(board2));
                    if (board1.getMovesColor() == winner)
                        movePriority[rand[i]]++;
                }

            }
        }

        //Определяем самый приоритетный ход
        int bestMoveI = 0;
        int bestPriority = 0;
        for (i=0; i<k; i++){
            if (movePriority[i] > bestPriority) {
                bestPriority = movePriority[i];
                bestMoveI = i;
            }
        }

        for (i = 0; i < k; i++){
            System.out.println("movePriority of "+avMoves[i]+" = "+movePriority[i]);
        }

            return avMoves[bestMoveI];
    }

}