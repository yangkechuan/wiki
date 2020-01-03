package com.example;


public class MyStack {

    /*------------------ 通过数组实现栈----------------------------*/


    /**
     * 数组大小
     */
    private int[] array;

    /**
     * 记录当前栈位置
     */
    private int size;

    private MyStack(int capacity){
        array = new int[capacity];
        size = 0;
    }

    /**
     * 入栈
     * @param data 入栈数据
     */
    private void push(int data){
        reSize();
        array[size] = data;
        size++;
    }

    /**
     * 出栈
     * @return 出栈数据
     */
    private int pop(){
        if (size <= 0 ){
            throw new IndexOutOfBoundsException("数组为空");
        }
        int temp = array[size - 1];
        // 这里只根据 size 记录当前栈深，可能超过 size 的索引还有值，但是是不需要的数据，不用处理。
        size--;
        return temp;
    }

    /**
     * 数组扩容
     */
    private void reSize(){
        if (size >= array.length){
            int[] newArray = new int[array.length * 2];
            System.arraycopy(array, 0, newArray, 0, array.length);
            array = newArray;
        }
    }
    public static void main(String[] args) {
        MyStack stack = new MyStack(2);
        stack.push(1);
        stack.push(2);
        stack.push(3);
        while (stack.size > 0){
            System.out.println(stack.pop());
        }
    }
}
