<?php
/**
 * Created by PhpStorm.
 * User: ykc
 * Date: 17/4/13
 * Time: 下午9:16
 */
/**约瑟夫环:50个人,围在一起报数,数到7剔除,直到最后一个人
 * 参考:
 * @link http://www.myexception.cn/java-other/1886990.html
 * @link http://9iphp.com/web/php/1112.html
 */
//新建一个50位的数组,从1开始
$person = range(1, 50);
//游标,用来计算已经数了几个人
$cursor = 0;
while (count($person) > 1) {
    for ($i = 0 ; $i < count($person) ; $i++) {
        //游标从1开始计算,逐步加1
        $cursor++;
        if ( $cursor % 7 == 0) {
            /**
             * 删除该位置,游标归0,同时 $i 减一作为删除位置后的坐标补偿。
             */
            array_splice($person, $i, 1);
            $cursor = 0;
            $i--;
        }
    }
}
print_r($person);