package com.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

/**
 * @author ykc
 */
public class MyBucketSort {

    /**
     * 桶排序
     *
     * @param array 待排序数组
     * @return 排序完成数组
     */
    private static double[] bucketSort(double[] array) {

        /* 得到数列的最大值和最小值，并算出差值d */
        double max = array[0];
        double min = array[0];
        for (double value : array) {
            if (value > max) {
                max = value;
            }
            if (value < min) {
                min = value;
            }
        }
        double d = max - min;

        /* 初始化桶 */
        int bucketNum = array.length;
        ArrayList<LinkedList<Double>> bucketList = new ArrayList<>(bucketNum);

        for (int i = 0; i < bucketNum; i++) {
            bucketList.add(new LinkedList<>());
        }

        /* 遍历原始数组，将每个元素放入桶中 */
        for (int i = 0; i < array.length; i++) {
            int num = (int) ((array[i] - min) * (bucketNum - 1) / d);
            bucketList.get(num).add(array[i]);
        }

        /* 对每个桶内部进行排序 */
        for (int i = 0; i < bucketList.size(); i++) {
            Collections.sort(bucketList.get(i));
        }

        /* 输出全部元素 */
        double[] sortedArray = new double[array.length];
        int index = 0;
        for(LinkedList<Double> list : bucketList){
            for (double element : list){
                sortedArray[index] = element;
                index++;
            }
        }
        return sortedArray;



    }

    public static void main(String[] args) {
        double[] array = new double[] {4.12,6.421,0.0023,3.0,2.123,8.122,4.12, 10.09};
        double[] sortedArray = bucketSort(array);
        System.out.println(Arrays.toString(sortedArray));
    }
}
