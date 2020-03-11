package com.example;


import java.util.Stack;

public class InterviewMinStack {


    /*
     * 题目：
     *     实现一个栈，该栈带有出栈（pop）、入栈（push）、取最小元素（getMin）3个方法。
     *     要保证这3个方法的时间复杂度都是O(1)
     */



    /**
     * 存储所有入栈元素
     */
    private Stack<Integer> mainStack = new Stack<>();

    /**
     * 存储最小值元素
     */
    private Stack<Integer> minStack = new Stack<>();

    /**
     * 入栈
     * @param element 元素
     */
    private void push(int element){
        mainStack.push(element);

        if (minStack.isEmpty() || minStack.peek() > element){
            minStack.push(element);
        }
    }

    /**
     * 出栈
     * @return 元素
     */
    private Integer pop(){
        if (minStack.peek().equals(mainStack.peek())){
            minStack.pop();
        }
        return mainStack.pop();

    }

    /**
     * 获取栈最小值
     * @return 最小值
     * @throws Exception e
     */
    private Integer getMin() throws Exception {
        if (minStack.isEmpty()){
            throw new Exception("stack is empty");
        }
        return minStack.peek();
    }
    public static void main(String[] args) throws Exception {
        InterviewMinStack stack = new InterviewMinStack();
        stack.push(4);
        stack.push(3);
        stack.push(2);
        stack.push(1);
        stack.pop();
        System.out.println(stack.getMin());

        stack.pop();
        System.out.println(stack.getMin());

    }
}
