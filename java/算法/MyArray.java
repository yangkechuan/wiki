package com.example;


public class MyArray {
    private int[] array;
    private int size;

    public MyArray(int capacity) {
        this.array = new int[capacity];
        size = 0;
    }

    /*
     * 插入
     */
    public void insert(int element, int index) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("超出数组范围");
        }

        // 超过数组容量上限，对数组扩容
        if (size >= array.length) {
            resize();
        }
        // 从右向左循环，将元素逐个向右挪一位
        for (int i = size - 1; i >= index; i--) {
            array[i + 1] = array[i];
        }
        // 腾出的位置放新元素
        array[index] = element;
        size++;
    }

    /*
     * 删除
     */
    public int delete(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("超出数组范围");
        }
        int deleteElement = array[index];

        // 从左向右循环，将元素逐个向左挪一位
        for (int i = index; i < size - 1; i++) {
            array[i] = array[i + 1];
        }
        size--;
        return deleteElement;
    }

    /*
     * 扩容
     */
    public void resize() {
        int[] arrayNew = new int[array.length * 2];
        System.arraycopy(array, 0, arrayNew, 0, array.length);
        array = arrayNew;
    }

    public void output() {
        for (int i = 0; i < size; i++) {
            System.out.println(array[i]);
        }
    }


    public static void main(String[] args) {
        MyArray array = new MyArray(4);
        array.insert(3, 0);
        array.insert(7, 1);
        array.insert(9, 2);
        array.insert(5, 3);
        array.insert(6, 1);
        array.output();


        int deleteElement = array.delete(0);
        System.out.println("--------delete: " + deleteElement + "-------");

        array.output();
    }
}
