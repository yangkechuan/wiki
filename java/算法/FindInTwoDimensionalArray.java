public class FindInTwoDimensionalArray {

    /*
     * 二维数组中查找
     *
     * 在一个二维数组中，每一行都按照从左到右递增的顺序排序，每一列都按照从上到下递增的顺序排序。
     * 请完成一个函数，输入这样的一个二维数组和一个整数，判断数组中是否含有该整数。
     *
     *    |--------------|
     *    |  1  | 2 |  3 |
     *    |--------------|
     *    |  4  | 5  | 6 |
     *    |--------------|
     *    |  7  | 8  | 9 |
     *    |--------------|
     *
     *
     */


    /**
     * 从左下角开始，向上数字递减，向右数字递增，当查找数字比当前位置值大时，右移，小时上移。相等则返回
     *
     *
     * @param array 数组
     * @param target 目标值
     * @return 结果
     */
    public static boolean find(int[][] array, int target){

        int x = 0;
        int y = array[0].length - 1;


        while (y >= 0 && x <= array.length - 1){
            if (array[x][y] > target){
                y--;
            }else if (array[x][y] < target){
                x++;
            }else {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        int[][] array = {
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 9}
        };

        System.out.println(find(array, 1));
        System.out.println(find(array, 2));
        System.out.println(find(array, 3));
        System.out.println(find(array, 4));
        System.out.println(find(array, 5));
        System.out.println(find(array, 6));
        System.out.println(find(array, 7));
        System.out.println(find(array, 8));
        System.out.println(find(array, 9));
        System.out.println(find(array, 10));
    }
}
