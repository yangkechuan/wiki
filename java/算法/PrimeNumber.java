public class PrimeNumber {


    /**
     * 100 以内的素数
     */
    public static void main(String[] args) {
        primeNumber();
        primeNumberV2();
    }

    public static void primeNumber(){

        /*
         * i 表示从 1 ~ 100 的所有数字， j 表示小于 i 的数字，验证 i 是否可以整除 j
         */
        int i;
        int j;
        int count = 0;
        for(i = 2 ; i < 100 ; i++){
            for (j = 2 ; j <  i ; j++){
                // 如果可以整除，跳出当前循环
                if (i % j == 0){
                    break;
                }
            }
            // 如果循环结束，说明 i 只能被 1 和 i 本身整除，则是素数
            if (i == j){
                count += 1;
                System.out.println(i);
            }
        }
        System.out.println(String.format("1 ~ 100 共有素数 %s 个", count));
    }


    public static void primeNumberV2(){

        /*
         * 埃氏筛法
         *
         */

        // 定义 0 ~ 100 的数组，使用非 0 值表示该下标是素数
        int[] array = new int[101];

        // 数据初始化，假设 2 ~ 100 都是素数
        for (int i = 2; i <= 100 ; i++) {
            array[i] = 1;
        }


        int p, q;
        // 遍历数组，每遇到一个新的最小素数，则把其倍数标记为非素数
        for (int i = 2; i <= 100 ; i++) {
            if (array[i] != 0){
                for (q = 2 ; (p = q * i) <= 100 ; q++){
                    array[p] = 0;
                }
            }
        }

        int count = 0;
        // 输出所有素数
        for (int i = 0; i < array.length; i++) {
            if (array[i] != 0){
                count++;
                System.out.println(i);
            }
        }
        System.out.println(String.format("1 ~ 100 共有素数 %s 个", count));

    }
}
