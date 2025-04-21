package 面试算法;

import java.util.*;

public class FindTopTenFrequentCharacters {

    /**
     * 找出字符串中出现频率最高的前十个字符
     */
    public static void main(String[] args) {

        String str = randomString(10000);
        System.out.println(findTopTenFrequentCharacters(str));
    }

    /**
     * 生成指定长度的随机字符串，字符串仅包含小写英文字母。
     *
     * @param length 要生成的随机字符串的长度
     * @return 包含小写英文字母的随机字符串
     */
    public static String randomString(int length) {
        // 创建一个 Random 对象，用于生成随机数
        Random random = new Random();
        // 创建一个 StringBuilder 对象，初始容量设置为指定的长度，用于构建随机字符串
        StringBuilder sb = new StringBuilder(length);
        // 循环指定的次数，每次生成一个随机的小写英文字母并添加到 StringBuilder 中
        for (int i = 0; i < length; i++) {
            // 生成一个 0 到 25 之间的随机整数，然后加上字符 'a' 的 ASCII 值，得到一个随机的小写英文字母
            char c = (char) (random.nextInt(26) + 'a');
            // 将生成的随机字符添加到 StringBuilder 中
            sb.append(c);
        }
        // 将 StringBuilder 中的内容转换为字符串并返回
        return sb.toString();
    }

    /**
     * 找出输入字符串中出现频率最高的前十个字符。
     *
     * @param str 输入的字符串
     * @return 包含出现频率最高的前十个字符的列表
     */
    public static List<Character> findTopTenFrequentCharacters(String str) {
        // 创建一个 HashMap 用于存储每个字符及其出现的频率
        // 由于已知字符集为小写英文字母，共 26 个，可指定初始容量为 26 以提高性能
        HashMap<Character, Integer> frequencyMap = new HashMap<>(26);

        // 遍历输入字符串中的每个字符
        for (char c : str.toCharArray()) {
            // 获取当前字符的频率，如果不存在则默认为 0，然后加 1 并更新到频率映射中
            frequencyMap.put(c, frequencyMap.getOrDefault(c, 0) + 1);
        }

        // 创建一个最小堆，用于存储字符及其频率的映射项
        // 最小堆根据字符的频率进行排序，频率最小的元素位于堆顶
        PriorityQueue<Map.Entry<Character, Integer>> minHeap = new PriorityQueue<>(Comparator.comparingInt(Map.Entry::getValue));

        // 遍历频率映射中的每个条目
        for (Map.Entry<Character, Integer> entry : frequencyMap.entrySet()) {
            // 将当前条目添加到最小堆中
            minHeap.offer(entry);
            // 如果最小堆的大小超过 10，则移除堆顶元素（频率最小的元素）
            if (minHeap.size() > 10) {
                minHeap.poll();
            }
        }

        // 创建一个列表，用于存储出现频率最高的前十个字符
        List<Character> topTen = new ArrayList<>();
        // 从最小堆中依次取出元素，添加到列表中
        while (!minHeap.isEmpty()) {
            topTen.add(minHeap.poll().getKey());
        }

        // 由于最小堆取出的元素是按频率从小到大排列的，需要反转列表以得到按频率从大到小的顺序
        Collections.reverse(topTen);
        return topTen;
    }
}
