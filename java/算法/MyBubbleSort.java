package com.example;


import java.util.Arrays;

public class MyBubbleSort {


    /*----------------------冒泡排序---------------------------*/

    /**
     * 基础版冒泡
     * @param array 待排序数据
     */
    private static void sort_1(int[] array){
        for (int i = 0 ; i < array.length - 1 ; i++){
            for (int j = 0 ; j < array.length - i - 1 ; j++){
                if (array[j] > array[j+1]){
                    int temp = array[j+1];
                    array[j+1] = array[j];
                    array[j] = temp;
                }
            }
        }
    }

    /**
     * 优化第一版:
     * 每一轮循环后，如果没有元素交换，说明数组已经有序，
     * 不用再次循环，直接结束
     * @param array 待排序数据
     */
    private static void sort_2(int[] array){
        for (int i = 0 ; i < array.length - 1 ; i++){
            /*有序标记，每一轮的初始值都是true*/
            boolean isSorted = true;
            for (int j = 0 ; j < array.length - i - 1 ; j++){
                if (array[j] > array[j+1]){
                    int temp = array[j+1];
                    array[j+1] = array[j];
                    array[j] = temp;
                    // 因为有序元素进行交换，所以不是有序的，标记变为 false
                    isSorted = false;
                }
            }
            if (isSorted){
                break;
            }
        }
    }

    /**
     * 优化第二版
     * 每一次循环，如果尾部部分元素已经有序，则不用循环到底
     * @param array 待排序数据
     */
    private static void sort_3(int[] array){
        // 记录最后一次交换的位置
        int lastExchangeIndex = 0;

        // 无序的边界，每次比较只需要比到这里为止
        int sortBorder = array.length - 1;
        for (int i = 0 ; i < array.length - 1 ; i++){
            /*有序标记，每一轮的初始值都是true*/
            boolean isSorted = true;
            for (int j = 0 ; j < sortBorder ; j++){
                if (array[j] > array[j+1]){
                    int temp = array[j+1];
                    array[j+1] = array[j];
                    array[j] = temp;
                    // 因为有序元素进行交换，所以不是有序的，标记变为 false
                    isSorted = false;
                    // 更新为最后一次交换元素的位置
                    lastExchangeIndex = j;
                }
            }
            sortBorder = lastExchangeIndex;
            if (isSorted){
                break;
            }
        }
    }

    /**
     * 鸡尾酒排序
     * 元素比较和交换是双向过程
     * 适用于大部分元素已经有序的场景
     * @param array 待排序数据
     */
    private static void sort_4(int[] array){
        int tmp = 0;
        for (int i = 0 ; i < array.length / 2 ; i++){
            // 有序标记，每一句的初始值都是true
            boolean isSorted = true;
            // 奇数轮，从左向右比较和交换
            for (int j  = i ; j < array.length - i - 1 ; j++){
                if (array[j] > array[j+1]){
                    tmp = array[j];
                    array[j] = array[j+1];
                    array[j+1] = tmp;

                    // 有元素交换，所以并不是有序的，标记变为 false
                    isSorted = false;
                }
            }
            if (isSorted){
                break;
            }

            // 在偶数轮之前，将 isSorted 重新标记为 true
            isSorted = true;
            // 偶数轮，从右向左比较和交换
            for (int j = array.length - i - 1; j > i ; j--){
                if (array[j] < array[j-1]){
                    tmp = array[j];
                    array[j] = array[j-1];
                    array[j-1] = tmp;

                    // 有元素交换，所以并不是有序的，标记变为 false
                    isSorted = false;
                }
            }
            if (isSorted){
                break;
            }
        }
    }
    public static void main(String[] args) {
        int[] array1 = new int[]{5, 8, 6, 3, 4, 2, 1, 7};
        sort_1(array1);
        System.out.println(Arrays.toString(array1));

        int[] array2 = new int[]{5, 8, 6, 3, 4, 2, 1, 7};
        sort_2(array2);
        System.out.println(Arrays.toString(array2));

        int[] array3 = new int[]{3, 4, 2, 1, 5, 6, 7, 8};
        sort_3(array3);
        System.out.println(Arrays.toString(array3));

        int[] array4 = new int[]{2, 3, 4, 5, 6, 7, 1, 8};
        sort_4(array4);
        System.out.println(Arrays.toString(array4));

    }
}
