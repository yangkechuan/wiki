package com.example;


public class MyQueue {

    /* ***********通过数组实现循环队列****** */

    /**
     * 数组
     */
    private int[] array;

    /**
     * 队列头部
     */
    private int front;

    /**
     *  队列尾部
     */
    private int rear;


    public MyQueue(int capaticy) {
        this.array = new int[capaticy];
    }

    /**
     * 入队
     *
     * @param element 入队元素
     */
    public void enQueue(int element) throws Exception {
        if ((rear + 1) % array.length == front) {
            throw new Exception("队列已满");
        }
        array[rear] = element;
        rear = (rear + 1) % array.length;
    }

    /**
     * 出队
     *
     * @return element
     * @throws Exception exception
     */
    public int deQueue() throws Exception {
        if (rear == front) {
            throw new Exception("队列已满");
        }
        int deQueueElement = array[front];
        front = (front + 1) % array.length;
        return deQueueElement;
    }

    private void output() {
        for (int i = front; i != rear; i = (i + 1) % array.length) {
            System.out.println(array[i]);
        }
    }

    public static void main(String[] args) throws Exception {
        MyQueue queue = new MyQueue(6);
        queue.enQueue(1);
        queue.enQueue(2);
        queue.enQueue(3);
        queue.enQueue(4);

        queue.deQueue();

        queue.enQueue(1);
        queue.output();

    }
}
