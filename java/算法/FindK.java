package com.example;

public class FindK {

    public static class ListNode{
        int val;
        ListNode next;

        ListNode(int val) {
            this.val = val;
        }
    }

    /**
     * 题目：  链表中倒数第 K 个节点
     *
     *  链表中倒数第k个节点也就是正数第(L-K+1)个节点
     *
     *  解题思路：
     *     两个头部指针，第一个先跑 k - 1 ，然后两个同时开始跑，当第一个跑到终点后，第二个位置在倒数第 k 个
     *
     *
     *  start:
     *
     *  node --> node --> node --> node --> node --> node --> node
     *    |                           |                         |
     *   head  <------- k ------->   tail                       |
     *                               |                          |
     *                               |---------- k -------------|
     *
     *  end:
     *
     *
     *  node --> node --> node --> node --> node --> node --> node
     *                                |                         |
     *                              head  <------- k ------->   tail
     *                               |                          |
     *                               |---------- k -------------|
     *
     */
    public static void main(String[] args) {
        ListNode node = new ListNode(1);
        node.next = new ListNode(2);
        node.next.next = new ListNode(3);

        ListNode result = findK(node, 1);
        if (result != null){
            System.out.println(result.val);
        }

        ListNode result1 = findK(node, 3);
        if (result1 != null){
            System.out.println(result1.val);
        }

        ListNode result2 = findK(node, 4);
        if (result2 != null){
            System.out.println(result2.val);
        }

    }

    public static ListNode findK(ListNode head, int k) {
        if (head == null || k < 0) {
            return null;
        }
        ListNode node1 = head, node2 = head;

        // node1 先跑 k -1 位置
        for (int i = 0; i < k; i++) {
            if (node1 == null){
                return null;
            }
            node1 = node1.next;
        }

        // node1 和 node2 同时跑，直到 node1 为 null
        while (node1 != null){
            node1 = node1.next;
            node2 = node2.next;
        }
        return node2;
    }
}





