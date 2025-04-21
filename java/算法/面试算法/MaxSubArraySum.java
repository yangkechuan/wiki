package 面试算法;

public class MaxSubArraySum {

    /**
     * 计算给定整数数组中最大子数组的和。
     * 输入：
     *   {1, 2, -3, 4, 5}
     *
     * 输出 ：
     *   9
     *
     * 解释：
     *   最大子数组是 {4, 5}，其和为 9。
     */


    public static void main(String[] args) {
        int[] array = {1, 2, -3, 4, 5};
        System.out.println(maxSubArraySum(array));
        System.out.println(maxSubArraySumV2(array));

    }


    /**
     * 计算给定整数数组中最大子数组的和。
     * 该方法通过遍历数组，动态更新当前子数组的和，找出其中的最大值。
     * 当当前子数组的和小于 0 时，会重置子数组的和，从下一个元素重新开始计算。
     * 时间复杂度为 O(n)，其中 n 是数组的长度。
     *
     * @param array 输入的整数数组，可能为 null 或空数组。
     * @return 数组中最大子数组的和，如果数组为 null 或空数组，则返回 0。
     */
    public static int maxSubArraySum(int[] array) {
        // 检查数组是否为 null 或者数组长度是否为 0，如果是则直接返回 0
        if (array == null || array.length == 0) {
            return 0;
        }
        // 用于记录遍历过程中遇到的最大子数组和
        int max = 0;
        // 用于记录当前正在计算的子数组的和
        int currentSum = 0;
        // 遍历数组中的每个元素
        for (int j : array) {
            // 将当前元素累加到当前子数组的和中
            currentSum += j;
            // 比较当前子数组的和与之前记录的最大子数组和，取较大值更新 max
            max = Math.max(max, currentSum);
            // 如果当前子数组的和小于 0，说明这个子数组对后续的子数组和没有正向贡献，
            // 将 currentSum 重置为 0，从下一个元素开始重新计算子数组的和
            if (currentSum < 0) {
                currentSum = 0;
            }
        }
        // 返回最大子数组的和
        return max;
    }

    /**
     * 计算给定整数数组中最大子数组的和。
     * 该方法使用动态规划的思想，通过一次遍历数组来找出最大子数组和。
     * 时间复杂度为 O(n)，其中 n 是数组的长度。
     *
     * @param array 输入的整数数组，可能为 null 或空数组。
     * @return 数组中最大子数组的和，如果数组为 null 或空数组，则返回 0。
     */
    public static int maxSubArraySumV2(int[] array) {
        // 检查数组是否为 null 或者数组长度是否为 0，如果是则直接返回 0
        if (array == null || array.length == 0) {
            return 0;
        }
        // 用于记录遍历过程中遇到的最大子数组和
        int max = 0;
        // 用于记录当前正在计算的子数组的和
        int currentSum = 0;
        // 遍历数组中的每个元素
        for (int i : array) {
            // 比较当前元素 i 和将当前元素添加到当前子数组和后的结果
            // 若加上当前元素后子数组和变小，不如从当前元素开始一个新的子数组
            currentSum = Math.max(i, currentSum + i);
            // 比较当前子数组的和与之前记录的最大子数组和，取较大值更新 max
            max = Math.max(currentSum, max);
        }
        // 返回最大子数组的和
        return max;
    }
}
