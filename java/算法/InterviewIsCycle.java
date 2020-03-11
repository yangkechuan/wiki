package com.example;



public class InterviewIsCycle {


    /**
     * 用来记录首次相遇位置
     */
    private static Node flag = null;

    private static class Node {
        int data;
        Node next;

        Node(int data) {
            this.data = data;
        }
    }

    /**
     * 判断一个单向链表是否有环
     * <p>
     * 1-->2-->3-->4-->5
     *             ↑   |
     *             |   ↓
     *             7<--6
     * <p>
     * 思路：
     * 两个指针p1、p2,分别指向头节点，循环，p1每次向前移动一个位置，p2每次移动两个位置。
     * 当p1、p2所指相同，则说明有环
     *
     * @param node 链表
     * @return 是否有环
     */
    private static boolean isCycle(Node node) {
        Node p1 = node;
        Node p2 = node;
        while (p1 != null && p2 != null) {
            p1 = p1.next;
            p2 = p2.next.next;
            if (p1 == p2) {
                flag = p1;
                return true;
            }
        }
        return false;
    }

    /**
     * 如果有环，求环长度
     *
     * @return 环长
     */
    private static int cycleLength() {
        Node p1 = flag;
        Node p2 = flag;
        int length = 0;
        for (; ; ) {
            p1 = p1.next;
            p2 = p2.next.next;
            length++;
            if (p1 == p2) {
                return length;
            }
        }
    }

    public static void main(String[] args) {
        Node node1 = new Node(1);
        Node node2 = new Node(2);
        Node node3 = new Node(3);
        Node node4 = new Node(4);
        Node node5 = new Node(5);
        Node node6 = new Node(6);
        Node node7 = new Node(7);
        node1.next = node2;
        node2.next = node3;
        node3.next = node4;
        node4.next = node5;
        node5.next = node6;
        node6.next = node7;
        node7.next = node4;

        System.out.println(isCycle(node1));
        System.out.println(cycleLength());

    }
}
