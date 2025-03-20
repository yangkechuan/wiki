package com.example;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class MyQuickSort {


    /*---------------------------------分治(双边循环法)-----------------------------------------------------*/

    private static void quickSort(int[] array, int startIndex, int endIndex){
        // 递归结束条件，startIndex 大于或等于 endIndex 时
        if (startIndex > endIndex){
            return;
        }
        // 得到基准元素位置
        int pivotIndex = partition(array, startIndex, endIndex);
        quickSort(array, startIndex, pivotIndex - 1);
        quickSort(array, pivotIndex + 1 , endIndex);
    }

    /**
     * 分治(双边循环法)
     * @param array 待交换的数组
     * @param startIndex 起始下标
     * @param endIndex 结束下标
     * @return 基准位置
     */
    private static int partition(int[] array, int startIndex, int endIndex){
        // 取第一个位置(也可以选择随机位置)的元素作为基准元素
        int pivot = array[startIndex];
        int left = startIndex;
        int right = endIndex;

        while (left < right){
            // 控制 right 指针比较并左移
            while (left < right && array[right] > pivot){
                right--;
            }

            // 控制 left 指针并右移, 注意这里需要有 '=' 号
            while (left < right && array[left] <= pivot){
                left++;
            }

            // 交换 left 和 right 指针所指向的元素
            if (left < right){
                int p = array[left];
                array[left] = array[right];
                array[right] = p;
            }
        }

        // pivot 和指针重合点交换
        array[startIndex] = array[left];
        array[left] = pivot;
        return left;
    }


    /*---------------------------------分治(单边循环法)-----------------------------------------------------*/


    private static void quickSort_1(int[] array, int startIndex, int endIndex){
        // 递归结束条件，startIndex 大于或等于 endIndex 时
        if (startIndex > endIndex){
            return;
        }
        // 得到基准元素位置
        int pivotIndex = partition_1(array, startIndex, endIndex);
        quickSort(array, startIndex, pivotIndex - 1);
        quickSort(array, pivotIndex + 1 , endIndex);
    }

    /**
     * 分支(单边循环法)
     * @param array 待交换的数组
     * @param startIndex 起始下标
     * @param endIndex 结束下标
     * @return 基准位置
     */
    private static int partition_1(int[] array, int startIndex, int endIndex){
        // 取第一个位置(也可以选择随机位置)的元素作为基准元素
        int pivot = array[startIndex];
        int mark = startIndex;

        for (int i = startIndex + 1 ; i <= endIndex ; i++){
            if (array[i] < pivot){
                mark++;
                int p = array[mark];
                array[mark] = array[i];
                array[i] = p;
            }
        }
        array[startIndex] = array[mark];
        array[mark] = pivot;
        return mark;
    }


    /*---------------------------------非递归方式实现-----------------------------------------------------*/

    private static void quickSort_2(int[] array, int startIndex, int endIndex){
        // 用一个集合栈来代替递归的函数栈
        Stack<Map<String, Integer>> quickSortStack = new Stack<>();

        //整个数列的起止下标，以哈希的形式入栈
        Map<String, Integer> rootParam  = new HashMap<>();
        rootParam.put("startIndex", startIndex);
        rootParam.put("endIndex", endIndex);
        quickSortStack.push(rootParam);

        // 循环结束，栈为空时
        while (!quickSortStack.isEmpty()){
            // 栈顶元素出栈，得到起止下标
            Map<String, Integer> param = quickSortStack.pop();
            int pivotIndex = partition_1(array, param.get("startIndex"), param.get("endIndex"));

            // 根据基准元素分成两部分，把每一部分的起止下标入栈
            if (param.get("startIndex") < pivotIndex - 1){
                Map<String, Integer> leftParam = new HashMap<>();
                leftParam.put("startIndex", param.get("startIndex"));
                leftParam.put("endIndex", pivotIndex - 1);
                quickSortStack.push(leftParam);
            }
            if (pivotIndex + 1 < param.get("endIndex")){
                Map<String, Integer> rightParam = new HashMap<>();
                rightParam.put("startIndex", pivotIndex + 1);
                rightParam.put("endIndex", param.get("endIndex"));
                quickSortStack.push(rightParam);
            }
        }
    }

    public static void main(String[] args) {
        int[] array = new int[]{4, 4, 6, 5, 3, 2, 8, 1};
        quickSort(array, 0 , array.length - 1);
        System.out.println(Arrays.toString(array));

        int[] array1 = new int[]{4, 4, 6, 5, 3, 2, 8, 1};
        quickSort_1(array1, 0 , array1.length - 1);
        System.out.println(Arrays.toString(array1));

        int[] array2 = new int[]{4, 4, 6, 5, 3, 2, 8, 1};
        quickSort_2(array2, 0 , array2.length - 1);
        System.out.println(Arrays.toString(array2));
    }
}
