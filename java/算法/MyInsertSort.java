import java.util.Arrays;

public class MyInsertSort {

    /**
     * 插入排序
     * 在已排序数组中，插入新数据
     * 开始时，以 array[j] 为已排序数组，从 0 开始， i 代表循环次数
     *
     * start:
     *      [3, 1, 2, 4, 5, 0]    -- 第一次排序，array[0] 作为已排序数组
     *
     *      [1, 3, 2, 4, 5, 0]
     *
     *      [1, 2, 3, 4, 5, 0]
     *
     *      [1, 2, 3, 4, 5, 0]
     *
     *      [1, 2, 3, 4, 5, 0]
     *
     * end:
     *      [0, 1，2, 3, 4, 5]
     *
     *  时间复杂度：
     *      两层循环，所以是 O(n^2)
     *
     *  空间复杂度：
     *      没有额外空间，所以是 O(1)
     *
     * @param array 待排序数组
     */
    public static void insertSort(int[] array) {

        // 外层表示循环次数
        for (int i = 1; i < array.length; i++) {
            // 每次需要插入的值
            int insertValue = array[i];

            // j 从 0 开始，表示当前已排序数组
            int j = i - 1;

            // 当 j >= 0 时，并且 当前值比要插入的值大，表示可以插入
            while ((j >= 0) && (insertValue < array[j])){

                // 每次插入，不用两两交换，可以先往后移位
                array[j + 1] = array[j];
                j--;
            }

            // 一次循环结束后，把需要插入的值，放到对应位置
            array[j + 1] = insertValue;
        }
    }

    public static void main(String[] args) {
        int[] array = {3, 1, 2, 4, 5, 0};
        insertSort(array);
        System.out.println(Arrays.toString(array));
    }
}
