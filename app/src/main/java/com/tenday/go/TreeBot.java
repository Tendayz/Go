package com.tenday.go;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by 18006 on 18.11.2017.
 */

public class TreeBot {

    private Node root;
    private Node parent;

    private static Random random = new Random();

    static int generateRandom(int n) {
        return (Math.abs(random.nextInt()) % n)+1;
    }


    public TreeBot(int[][] intArr) {
        root = new Node();
        root.data = intArr;
        root.children = new ArrayList<Node>();
    }

    public static class Node{
        private int[][] data;
        private List<Node> children;
        private Node parent = this.parent;
    }

    public TreeBot addChild(int[][] child) {
        TreeBot childNode = new TreeBot(child);
        childNode.parent = root;
        root.children.add(childNode.root);
        return childNode;
    }

    public int[][] getIntArr(){
        return root.data;
    }

    public static void main(String[] args) {
        int[][] intArr = new int[11][11];
        TreeBot theTree = new TreeBot(intArr);

    }

    public int bot(TreeBot root){
        int[][] intArr = root.getIntArr();
        int n = intArr.length;

        int[][] avMoves = new int[n][n];

        for (int i = 1; i < n-1; i++){
            for (int j = 1; j < n-1; j++){
                if (intArr[generateRandom(n)][generateRandom(n)] != 0){

                }
            }
        }

        return 0;
    }
}

/*class Node{
    int[][] intArr;
    Node(int[][] intArr, int n){
        this.intArr = intArr;
    }
}*/
