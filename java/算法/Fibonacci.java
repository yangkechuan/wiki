
public class Fibonacci {


    /**
     *  斐波那契数列
     *
     *  1,1,2,3,5,8,13,21,34,55...
     *
     */

    public static void main(String[] args) {
        System.out.println(fib(10));
        System.out.println(fibv2(10));
    }

    /**
     * 迭代法
     * @param num 计算位数
     * @return int
     */
    public static int fib(int num){
        if(num <= 0){
            return 0;
        }
        if (num == 1 || num == 2){
            return 1;
        }
        int first = 1, second = 1, third = 0;
        for (int i = 3; i <= num; i++) {
            third = first + second;
            first = second;
            second = third;
        }
        return third;
    }

    /**
     * 递归法
     * @param num 计算位数
     * @return int
     */
    public static int fibv2(int num){
        if(num <= 0){
            return 0;
        }
        if (num == 1 || num == 2){
            return 1;
        }
        return fibv2(num - 1) + fibv2(num - 2);
    }
}
