import java.util.Arrays;

public class MySelectSort {


    /**
     * 选择排序
     * 从左侧开始，找到一个最小的值，然后和第一个值进行交换
     *
     *
     * start:
     *      [3, 1, 2, 4, 5, 0]
     *
     *      [0, 1, 2, 4, 5, 3]
     *
     *      [0, 1, 2, 4, 5, 3]
     *
     *      [0, 1, 2, 4, 5, 3]
     *
     *      [0, 1, 2, 3, 5, 4]
     *
     * end:
     *     [0, 1, 2, 3, 4, 5]
     *
     *  时间复杂度：
     *      两层循环，所以是 O(n²)
     *
     *  空间复杂度：
     *      没有额外空间，所以是 O(1)
     *
     * 与冒泡排序不同的地方在于：
     *      1.没有过多的元素交换
     *      2.选择排序是不稳定排序
     *      3.当数组中大部分元素有序时，冒泡效率更高
     *
     *
     * @param array 待排序数组
     */
    private static void selectSort(int[] array){
        for (int i = 0; i < array.length; i++) {
            int minIndex = i;
            for (int j = i + 1; j < array.length; j++) {
                if (array[j] < array[minIndex]){
                    minIndex = j;
                }
            }
            // 需要交换的条件
            if (i != minIndex){
                int temp = array[i];
                array[i] = array[minIndex];
                array[minIndex] = temp;
            }
        }
    }


    public static void main(String[] args) {
        int[] array = {3, 1, 2, 4, 5, 0};
        selectSort(array);
        System.out.println(Arrays.toString(array));
    }

}
