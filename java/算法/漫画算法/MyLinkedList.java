package com.example;


public class MyLinkedList {

    /**
     * 单链表
     */
    private Node head;
    private Node last;
    private int size;

    /**
     * 链表节点
     */
    private static class Node{
        private int data;
        private Node next;
        private Node (int data){
            this.data = data;
        }
    }

    /**
     * 链表插入元素
     * @param data 插入元素
     * @param index 插入位置
     */
    public void insert(int data, int index){
        if (index < 0 || index > size){
            throw new IndexOutOfBoundsException("超出链表节点");
        }

        Node insertedNode = new Node(data);
        if (size == 0){
            //空链表
            head = insertedNode;
            last = insertedNode;
        }else if (index == 0){
            //插入头部
            insertedNode.next = head;
            head = insertedNode;
        }else if (size == index){
            //插入尾部
            last.next = insertedNode;
            last = insertedNode;
        }else {
            //插入中间
            Node prevNode = get(index - 1);
            insertedNode.next = prevNode.next;
            prevNode.next = insertedNode;
        }
        size++;
    }

    /**
     * 链表删除元素
     * @param index 查找的位置
     * @return Node
     */
    public Node remove(int index){
        if (index < 0 || index > size){
            throw new IndexOutOfBoundsException("超出链表节点");
        }
        Node removeNode = null;
        if (index == 0){
            //删除头节点
            removeNode = head;
            head = head.next;
        }else if (index == size - 1){
            //删除尾节点
            Node prevNode = get(index - 1);
            removeNode = prevNode.next;
            prevNode.next = null;
            last = prevNode;
        }else {
            //删除中间节点
            Node prevNode = get(index - 1);
            Node nextNode = prevNode.next.next;
            removeNode = prevNode.next;
            prevNode.next = nextNode;
        }
        size--;
        return removeNode;
    }
    /**
     * 链表查找元素
     * @param index 查找的位置
     * @return Node
     */
    private Node get(int index){
        if (index < 0 || index > size){
            throw new IndexOutOfBoundsException("超出链表节点");
        }
        Node temp = head;
        for (int i = 0 ; i < index ; i++){
            temp = temp.next;
        }
        return temp;
    }

    public void output(){
        Node temp = head;
        while (temp != null){
            System.out.println(temp.data);
            temp = temp.next;
        }
    }


    public static void main(String[] args) {
        MyLinkedList linkedList = new MyLinkedList();
        linkedList.insert(3, 0);
        linkedList.insert(7, 1);
        linkedList.insert(9, 2);
        linkedList.insert(5, 3);
        linkedList.insert(6, 1);
        linkedList.output();

        System.out.println("----------------");
        linkedList.remove(0);

        linkedList.output();

    }

}
