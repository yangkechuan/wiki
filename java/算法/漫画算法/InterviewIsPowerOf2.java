package com.example;


public class InterviewIsPowerOf2 {


    /*
     * 判断一个数是否是2的整数次幂
     */


    public static void main(String[] args) {
        System.out.println(isPowerOf2V1(8));
        System.out.println(isPowerOf2V2(8));
        System.out.println(isPowerOf2V3(8));
    }

    /**
     * 通过不断 * 2，判断是否会相等
     * @param num num
     * @return boolean
     */
    private static boolean isPowerOf2V1(int num){
        if (num < 2){
            return false;
        }
        int temp = 1;
        while (temp <= num){
            if (temp == num){
                return true;
            }
            temp = temp * 2;
        }
        return false;
    }

    /**
     *  "*" 改为位操作
     * @param num num
     * @return boolean
     */
    private static boolean isPowerOf2V2(int num){
        if (num < 2){
            return false;
        }
        int temp = 1;
        while (temp <= num){
            if (temp == num){
                return true;
            }
            temp = temp << 1;
        }
        return false;
    }


    /**
     * 通过位运算
     * @param num num
     * @return boolean
     */
    private static boolean isPowerOf2V3(int num){
        return (num & (num - 1)) == 0;
    }
}
