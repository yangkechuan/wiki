package lib;

import java.util.Arrays;

/**
 * Created by IntelliJ IDEA
 * User: ykc
 * Date: 2018/2/9
 * Time: 下午3:57
 */
public class QuickSort {
    public static void main(String[] args) {
        int[] arr = {6, 5, 4, 2, 1};

        quickSort(arr, 0, arr.length - 1);

        System.out.println(Arrays.toString(arr));
    }

    private static void quickSort(int[] array, int low, int high) {
        //判断终止条件
        if (low > high) {
            return;
        }

        int left = low;
        int right = high;
        int base = array[low];  //  基准数
        int temp;

        // 一轮交换完成条件
        while (left < right) {

            //从右侧开始查找，如果比基准数大，继续查找
            while (left < right && array[right] >= base) {
                right--;
            }
            // 从左侧开始查找，如果比基准数小，继续查找
            while (left < right && array[left] <= base) {
                left++;
            }
            // 一次交换条件
            if (left < right) {
                temp = array[left];
                array[left] = array[right];
                array[right] = temp;
            }
        }

        // 将基准数与查找到位置相等的位置交换
        array[low] = array[left];
        array[left] = base;

        quickSort(array, low, left - 1);
        quickSort(array, right + 1, high);
    }
}
