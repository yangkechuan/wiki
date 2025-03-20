package com.example;


import java.util.Stack;

public class InterviewQueue {

    /*
     * 用栈来实现队列
     */

    private static Stack<Integer> s1 = new Stack<>();
    private static Stack<Integer> s2 = new Stack<>();


    /**
     * 入队
     * @param element 元素
     */
    private void enQueue(int element){
        s1.push(element);
    }

    /**
     * 出栈
     * @return 元素
     */
    private Integer deQueue(){
        if (s2.isEmpty()){
            if (s1.isEmpty()){
                return null;
            }
            transfer();
        }
        return s2.pop();
    }

    /**
     * s1 内容转移到 s2
     */
    private void transfer(){
        while (!s1.isEmpty()){
            s2.push(s1.pop());
        }
    }
    
    public static void main(String[] args) {
        InterviewQueue queue = new InterviewQueue();
        queue.enQueue(1);
        queue.enQueue(2);
        queue.enQueue(3);
        queue.enQueue(4);

        System.out.println(queue.deQueue());
        System.out.println(queue.deQueue());
        System.out.println(queue.deQueue());
        System.out.println(queue.deQueue());
        System.out.println(queue.deQueue());

        queue.enQueue(5);

        System.out.println(queue.deQueue());

    }
}
