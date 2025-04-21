package 面试算法;

/**
 * @author yangkechuan
 */
public class ReverseWords {


    /**
     * 反转输入字符串中单词的顺序
     * 输入：
     *      "zhangsan lisi wangwu"
     * 输出：
     *      "wangwu lisi zhangsan"
     *
     */

    public static void main(String[] args) {

        String str = "zhangsan lisi wangwu";
        System.out.println(reverseWords(str));

    }

    /**
     * 反转输入字符串中单词的顺序。
     * 该方法接收一个字符串，将其中的单词按逆序排列，然后返回新的字符串。
     * 单词之间用空格分隔。如果输入的字符串为 null 或者为空字符串，则直接返回原字符串。
     *
     * @param str 输入的待处理字符串
     * @return 单词顺序反转后的字符串，如果输入为 null 或空字符串则返回原字符串
     */
    public static String reverseWords(String str) {
        // 检查输入字符串是否为 null 或者长度为 0，如果是则直接返回原字符串
        if (str == null || str.length() == 0) {
            return str;
        }

        // 创建一个 StringBuffer 对象，用于拼接反转后的单词
        StringBuffer sb = new StringBuffer();
        // 使用空格作为分隔符，将输入字符串分割成单词数组
        String[] result = str.split(" ");
        // 从数组的最后一个元素开始遍历，逆序拼接单词
        for (int i = result.length - 1; i >= 0; i--) {
            // 将当前单词添加到 StringBuffer 中
            sb.append(result[i]);
            // 如果不是最后一个单词，添加一个空格分隔
            if (i > 0) {
                sb.append(" ");
            }
        }
        // 将 StringBuffer 中的内容转换为字符串并返回
        return sb.toString();
    }
}
