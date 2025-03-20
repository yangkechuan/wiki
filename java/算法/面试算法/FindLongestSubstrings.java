package example;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FindLongestSubstrings {

    /**
     * 题目描述：
     * 给定一个字符串，请你找出其中不含有重复字符的最长子串，返回的是一个字符串数组。
     * 示例：
     * 输入："1231"
     * 输出：[123, 231]。
     *
     * 与 leetcode 第三题类似，但是 leetcode 返回的是最大长度：
     * https://leetcode.cn/problems/longest-substring-without-repeating-characters/description/
     *
     * 解题思路：
     * 1. 采用双指针方式，一个指针用于记录子串的起始位置，另一个指针用于遍历字符串。
     * 2. 遍历字符串时，判断当前字符是否已经在子串中出现过，如果出现过，则将子串起始位置移动到该字符的下一个位置。
     * 3. 同时，记录当前子串的长度，如果大于最大长度，则更新最大长度，并清空结果列表。
     * 4. 如果当前子串长度等于最大长度，则将其添加到结果列表中。
     * 5. 最后返回结果列表。
     */


    public static List<String> findLongestSubstrings(String str) {
        List<String> result = new ArrayList<>();
        int left = 0;
        int maxLength = 0;
        // 用于存储字符及其最后一次出现的索引
        HashMap<Character, Integer> hashMap = new HashMap<>();
        for (int right = 0; right < str.length(); right++) {
            char c = str.charAt(right);
            // 如果当前字符已经在窗口中出现过，并且其最后一次出现的位置在左指针或之后
            if (hashMap.containsKey(c) && hashMap.get(c) >= left) {
                left = hashMap.get(c) + 1;
            }
            // 更新字符的最后出现位置
            hashMap.put(c, right);
            int currentLength = right - left + 1;
            if (currentLength > maxLength) {
                // 如果当前子串长度大于最大长度，更新最大长度并清空结果列表
                maxLength = currentLength;
                result.clear();
                result.add(str.substring(left, right + 1));
            } else if (currentLength == maxLength) {
                // 如果当前子串长度等于最大长度，将其添加到结果列表
                result.add(str.substring(left, right + 1));
            }
        }
        return result;
    }
    public static void main(String[] args) {
        String str = "1231";
        System.out.println(findLongestSubstrings(str));
    }
}
