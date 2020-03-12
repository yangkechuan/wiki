package com.example;


public class InterviewGetBestGoldMining {


    /**
     * 获得金矿最优收益
     *
     * 递归算法
     * @param w 工人数量
     * @param n 可选金矿数量
     * @param p 金矿开采所需的工人数量
     * @param g 金矿储量
     * @return 最优收益
     */
    private static int getBestGoldMining(int w, int n , int[] p , int[] g){
        if (w == 0 || n == 0){
            return 0;
        }

        if (w < p[n-1]){
            return getBestGoldMining(w, n - 1, p, g);
        }
        return Math.max(getBestGoldMining(w, n - 1, p, g), getBestGoldMining(w - p[n -1], n - 1, p, g ) + g[n-1]);
    }

    public static void main(String[] args) {
        int w = 10;
        int[] p = { 5, 5, 3, 4, 3};
        int[] g = {400, 500, 200, 300, 350};
        System.out.println(getBestGoldMining(w, g.length, p, g));
    }
}
