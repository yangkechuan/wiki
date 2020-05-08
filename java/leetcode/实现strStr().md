## 实现strStr()


题目：

    实现 strStr() 函数。
    
    给定一个 haystack 字符串和一个 needle 字符串，在 haystack 字符串中找出 needle 字符串出现的第一个位置 (从0开始)。如果不存在，则返回  -1。
    
    示例 1:
    
    输入: haystack = "hello", needle = "ll"
    输出: 2
    示例 2:
    
    输入: haystack = "aaaaa", needle = "bba"
    输出: -1
    说明:
    
    当 needle 是空字符串时，我们应当返回什么值呢？这是一个在面试中很好的问题。
    
    对于本题而言，当 needle 是空字符串时我们应当返回 0 。这与C语言的 strstr() 以及 Java的 indexOf() 定义相符。
    
    链接：https://leetcode-cn.com/problems/implement-strstr


### 解题一

子串逐一比较 - 线性时间复杂度

```java
public class Test {
    
    public static void main(String[] args) {
        String haystack = "abcdef";
        String needle = "ef";
        System.out.println(strStr(haystack, needle));
    }
    private static int strStr(String haystack, String needle){
        if (haystack == null || needle == null){
            return -1;
        }
        if ("".equals(needle)){
            return 0;
        }

        int m = haystack.length();
        int n = needle.length();
        for (int i = 0 ; i < m - n + 1 ; i++){
            if (needle.equals(haystack.substring(i, i + n))){
                return i;
            }
        }
        return -1;
    }
}
```


// TODO ...