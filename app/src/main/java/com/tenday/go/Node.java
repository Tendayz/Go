package com.tenday.go;

/**
 * Created by 18006 on 06.01.2018.
 */

public class Node {
    public int wins=0;
    public int visits=0;
    public int x, y; // position of move
    //public Node parent; //optional
    public Node child;
    public Node sibling;
    public Node(/*Node parent, */int x, int y) {
        this.x=x;
        this.y=y;
    }
    public void update(int val) {
        visits++;
        wins+=val;
    }
    public double getWinRate() {
        if (visits>0) return (double)wins / visits;
        else return 0; /* should not happen */
    }
}