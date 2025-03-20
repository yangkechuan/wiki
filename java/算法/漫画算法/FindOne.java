package com.example;

import java.util.HashSet;
import java.util.Set;

public class FindOne {

    /**
     * 一个整数数组，数组中除一个整数出现过一次外，其余都出现过两次，请找出只出现一次的整数
     *
     *  使用异或运算，两个相同的整数，异或后等于0：  1 ^ 1 = 0
     */
    private static int findOne(int[] array){
        int num = 0;
        for (int i = 0; i < array.length; i++) {
            num ^= array[i];
        }
        return num;
    }

    /**
     * 一个字符数组，数组中除一个字符出现过一次外，其余都出现过两次，请找出只出现一次的字符
     *
     * 使用异或运算，两个相同的字符，异或后等于 0 ：  'a' ^ 'a' = 0
     */
    private static char findOne(char[] array){
        char c = 0;
        for (int i = 0; i < array.length; i++) {
            c ^= array[i];
        }
        return c;
    }

    /**
     * 一个整数数组，数组中除一个整数出现过两次外，其余都出现过一次，请找出出现两次的整数
     */
    private static Integer findTwo(int[] array){
        Set<Integer> set = new HashSet<>();
        for (int i = 0; i < array.length; i++) {
            if (set.contains(array[i])){
                return array[i];
            }
            set.add(array[i]);
        }
        return null;
    }


    public static void main(String[] args) {
        int[] arrayV1 = {1, 1, 2, 2, 3};
        System.out.println(findOne(arrayV1));

        char[] arrayV2 = {'a', 'a', 'b', 'b', 'c'};
        System.out.println(findOne(arrayV2));

        int[] arrayV3 = {1, 2, 3, 4, 5, 1};
        System.out.println(findTwo(arrayV3));
    }
}
