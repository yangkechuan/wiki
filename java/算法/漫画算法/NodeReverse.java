package com.example;


import java.util.Stack;

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

    /**
     * 迭代方式进行反转
     *
     * 主要思路：
     *
     * 1.定义上一个临时节点 (prev) 和当前临时节点 (current)
     * 2.存储当前节点的下一个节点 (next)
     * 3.将当前节点指向上一个节点 (如果不提前定义好下一个节点数据，原来当前节点的下一个节点会丢失)
     * 3. 上一个节点 (prev) 和当前节点 (current) 指针往后移动
     *
     *  初始状态
     * [1 next] --> [2 next] --> [3 next] --> [4 next] --> null
     *
     *
     * 定义变量：
     * prev   current     next
     *  |        |         |
     * null  [1 next] --> [2 next] --> [3 next] --> [4 next] --> null
     *
     *
     * 第一次变更：
     *
     * prev   current     next
     *  |        |         |
     * null <-- [1 next]   [2 next] --> [3 next] --> [4 next] --> null
     *
     * 第一次指针移动：
     *
     *           prev      current       next
     *            |        |             |
     * null <-- [1 next]   [2 next] --> [3 next] --> [4 next] --> null
     *
     *
     * 第二次变更：
     *
     *            prev        current    next
     *            |            |         |
     * null <-- [1 next] <--  [2 next]  [3 next] --> [4 next] --> null
     *
     *
     *
     * 第二次指针移动：
     *
     *                          prev      current       next
     *                          |        |             |
     * null <-- [1 next]  <-- [2 next]   [3 next] --> [4 next] --> null
     *
     *
     *   .
     *   .
     *   .
     *   .
     *   .
     *   .
     *
     * 依此类推，直到：
     *
     *
     *                                                   prev      current       next
     *                                                     |        |             |
     * null <-- [1 next]  <-- [2 next] <--  [3 next] <-- [4 next]  null         null
     *
     *
     * 然后，返回 prev 即可
     *
     * @param node 节点信息
     * @return Node
     */
    public static Node reverseV2(Node node){
        // 定义上一个临时节点
        Node prev = null;
        // 定义当前临时节点
        Node current = node;
        // 终止条件
        while (current != null){
            // 先存储好下一个节点信息
            Node next = current.next;
            // 将当前节点指向上一个节点
            current.next = prev;
            // 上一个临时节点，指针向前移动
            prev = current;
            // 当前节点，指针向前移动
            current = next;
        }
        return prev;
    }


    /**
     * 使用栈结构来解决
     * @param node 节点信息
     * @return Node
     */
    private static Node reverseV3(Node node){
        // 边界值判定
        if (node == null || node.next == null){
            return node;
        }
        // 数据先入栈
        Stack<Integer> stack = new Stack<>();
        while (node != null){
            stack.push(node.data);
            node = node.next;
        }

        // 数据出栈，并定义指针进行移动
        Node newNode = new Node(0);
        Node temp = newNode;
        while (!stack.isEmpty()){
            temp.next = new Node(stack.pop());
            temp = temp.next;
        }
        return newNode.next;

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
