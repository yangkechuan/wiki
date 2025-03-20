package com.example;


public class InterviewGetGreatestCommonDivisor {


    /*
     * 获取两数的最大公约数
     */


    /**
     * 辗转相除法
     * @param a 参数a
     * @param b 参数b
     * @return 公约数
     */
    private static int  getGreatestCommonDivisor(int a, int b){
        int big = a > b ? a : b;
        int small = a > b ? b : a;

        if (big % small == 0){
            return small;
        }
        return getGreatestCommonDivisor(big % small, small);
    }

    public static void main(String[] args) {
        System.out.println(getGreatestCommonDivisor(10, 25));
    }
}
