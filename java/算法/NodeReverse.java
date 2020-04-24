package com.example;


public class NodeReverse {

    /*------------------------单链表翻转------------------------------------*/


    private static class Node{
        int data;
        Node next;
        Node(int data){
            this.data = data;
        }
    }

    /**
     * 递归方式实现
     *
     *
     * 1 --> 2 --> 3 -> 4 --> 5
     *          |
     *          |
     *          |
     * 5 --> 4 --> 3 --> 2 --> 1
     *
     * @param node node
     * @return node
     */
    private static Node reverse(Node node){
        if (node == null || node.next == null){
            return node;
        }
        Node temp = node.next;
        Node newNode = reverse(temp);
        temp.next = node;
        node.next = null;
        return newNode;
    }

    public static void main(String[] args) {
        Node n1 = new Node(1);
        Node n2 = new Node(2);
        Node n3 = new Node(3);
        n1.next = n2;
        n2.next = n3;
        n1 = reverse(n1);

        while (n1 != null){
            System.out.println(n1.data);
            n1 = n1.next;
        }
    }
}
