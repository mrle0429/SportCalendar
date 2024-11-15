package com.test.sport;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void main() {
        // 输入的时间字符串
        String input = "2024-11-03T15:30:00+00:00";

        // 定义输入时间的格式
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

        // 解析输入时间字符串为ZonedDateTime对象
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(input, inputFormatter);

        // 转换为中国时间（东八区）
        ZoneId chinaZoneId = ZoneId.of("Asia/Shanghai");
        ZonedDateTime chinaTime = zonedDateTime.withZoneSameInstant(chinaZoneId);

        // 定义输出时间的格式
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // 格式化输出时间字符串
        String output = chinaTime.format(outputFormatter);

        // 输出结果
        System.out.println(output);
    }
}