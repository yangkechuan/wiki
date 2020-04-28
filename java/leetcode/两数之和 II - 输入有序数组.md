## 两数之和 II - 输入有序数组

题目：

    
    给定一个已按照升序排列 的有序数组，找到两个数使得它们相加之和等于目标数。
    
    函数应该返回这两个下标值 index1 和 index2，其中 index1 必须小于 index2。
    
    说明:
    
    返回的下标值（index1 和 index2）不是从零开始的。
    你可以假设每个输入只对应唯一的答案，而且你不可以重复使用相同的元素。
    示例:
    
    输入: numbers = [2, 7, 11, 15], target = 9
    输出: [1,2]
    解释: 2 与 7 之和等于目标数 9 。因此 index1 = 1, index2 = 2 。
    
    
    链接：https://leetcode-cn.com/problems/two-sum-ii-input-array-is-sorted/
    
    


### 解题：


```java
public class Test {

    public static void main(String[] args) {
        int[] array = new int[]{1, 3, 5, 10};
        int target = 4;
        System.out.println(Arrays.toString(twoSum(array, target)));
    }
    private static int[] twoSum(int[] array, int target){
        int left = 0;
        int right = array.length - 1;
        while (left < right){
            if (array[left] + array[right] > target){
                right--;
            }
            if (array[left] + array[right] < target){
                left++;
            }
            if (array[left] + array[right] == target){
                return new int[]{left + 1, right + 1};
            }
        }
        return new int[]{-1, -1};
    }
}
```