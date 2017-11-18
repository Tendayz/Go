package com.tenday.go;

/*public class BinaryTree {

    Node root;

    public void addNode(int key, String name){

        Node newNode = new Node(key, name);

        if(root == null){
            root = newNode;
        } else {
            Node focusNode = root;
            Node parent;
            while(true){
                parent = focusNode;
                if(key < focusNode.key){
                    focusNode = focusNode.leftChild;
                    if(focusNode == null){
                        parent.leftChild = newNode;
                        return;
                    }
                } else {
                    focusNode = focusNode.rightChild;
                    if (focusNode == null){
                        parent.rightChild = newNode;
                        return;
                    }
                }
            }
        }
    }

    public void inOrderTraverseTree(Node focusNode){
        if(focusNode != null){
            inOrderTraverseTree(focusNode.leftChild);
            System.out.println(focusNode);
            inOrderTraverseTree(focusNode.rightChild);
        }
    }

    public static void main(String[] args){

        BinaryTree theTree = new BinaryTree();

        theTree.addNode(50, "Boss");
        theTree.addNode(25, "Vice Pres");
        theTree.addNode(15, "Office Manager");
        theTree.addNode(30, "Secretary");
        theTree.addNode(75, "Sales Manager");
        theTree.addNode(85, "Salesman 1");

        theTree.inOrderTraverseTree(theTree.root);
    }


}

class Node{

    int key;
    String name;

    Node leftChild;
    Node rightChild;

    Node(int key, String name){
        this.key = key;
        this.name = name;
    }

    public String toString() {
        return name + " has a key " + key;
    }
}*/