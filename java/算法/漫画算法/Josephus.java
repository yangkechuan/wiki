package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class Josephus {


    public static void main(String[] args) {
        int n = 50;
        int m = 7;
        josephus(n, m);
    }

    /**
     * 约瑟夫环：
     *
     *   n个人围成一个环，选取一个正整数m(<n)作为报数值。从第一个人开始按顺时针方向自1开始顺序报数，
     *   报到m停止报数，报m的出列，下一个继续开始重新报数，直到环中只剩下一个为最后的优胜者。
     *
     *
     * @param n 人数
     * @param m 选取值
     */
    private static void josephus(int n, int m){
        if (m > n){
            return;
        }

        List<Integer> list = new ArrayList<>(n);
        IntStream.rangeClosed(1, n).forEach(list::add);

        int current = 0;
        while (list.size() > 1){
            for (int i = 0; i < list.size(); i++) {
                current++;
                if (current == m){
                    list.remove(i);
                    i--;
                    current = 0;
                }
            }
        }
        System.out.println(list.get(0));

    }
}
