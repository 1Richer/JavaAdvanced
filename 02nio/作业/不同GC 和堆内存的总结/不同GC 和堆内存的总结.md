# 不同GC和堆内存的总结

操作系统：windows10，6核，16GB

JDK版本：1.8.0_221

测试工具：GCeasy

测试程序：GCLogAnalysis.java

```
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;
/*
演示GC日志生成与解读
*/
public class GCLogAnalysis {
    private static Random random = new Random();
    public static void main(String[] args) {
        // 当前毫秒时间戳
        long startMillis = System.currentTimeMillis();
        // 持续运行毫秒数; 可根据需要进行修改
        long timeoutMillis = TimeUnit.SECONDS.toMillis(1);
        // 结束时间戳
        long endMillis = startMillis + timeoutMillis;
        LongAdder counter = new LongAdder();
        System.out.println("正在执行...");
        // 缓存一部分对象; 进入老年代
        int cacheSize = 2000;
        Object[] cachedGarbage = new Object[cacheSize];
        // 在此时间范围内,持续循环
        while (System.currentTimeMillis() < endMillis) {
            // 生成垃圾对象
            Object garbage = generateGarbage(100*1024);
            counter.increment();
            int randomIndex = random.nextInt(2 * cacheSize);
            if (randomIndex < cacheSize) {
                cachedGarbage[randomIndex] = garbage;
            }
        }
        System.out.println("执行结束!共生成对象次数:" + counter.longValue());
    }

    // 生成对象
    private static Object generateGarbage(int max) {
        int randomSize = random.nextInt(max);
        int type = randomSize % 4;
        Object result = null;
        switch (type) {
            case 0:
                result = new int[randomSize];
                break;
            case 1:
                result = new byte[randomSize];
                break;
            case 2:
                result = new double[randomSize];
                break;
            default:
                StringBuilder builder = new StringBuilder();
                String randomString = "randomString-Anything";
                while (builder.length() < randomSize) {
                    builder.append(randomString);
                    builder.append(max);
                    builder.append(randomSize);
                }
                result = builder.toString();
                break;
        }
        return result;
    }
}
```

## 堆内存256M下各GC之间性能对比

| GC策略   | 吞吐量     | 最大暂停时间 | 总暂停时间 | 平均暂停时间 |
| ------ | ------- | ------ | ----- | ------ |
| 串行GC   | 38.086% | 30ms   | 660ms | 10ms   |
| 并行GC   | 84.877% | 20ms   | 400ms | 7.69ms |
| CMS GC | 64.3%   | 26.5ms | 660ms | 9.30ms |
| G1 GC  | 95.421% | 15.6ms | 181ms | 2.39ms |

| Minor GC次数 | Full GC次数 | Minor GC时间 | Full GC时间 |      |
| ---------- | --------- | ---------- | --------- | ---- |
| 8          | 55        | 80ms       | 580ms     |      |
| 12         | 40        | 0ms        | 400ms     |      |
| 41         | 30        | 79.6ms     | 581ms     |      |
| 36         | 11        | 49ms       | 88ms      |      |

1. 4种策略均发生了OOM导致程序提前崩溃，G1 GC最早崩溃

2. 综合对比256M下性能 :并行GC>CMS GC>串行GC>G1 GC

   ​

## 堆内存512M下各GC之间性能对比

| GC策略   | 吞吐量     | 最大暂停时间 | 总暂停时间 | 平均暂停时间 |
| ------ | ------- | ------ | ----- | ------ |
| 串行GC   | 29.53%  | 60ms   | 630ms | 27ms   |
| 并行GC   | 35.449% | 30ms   | 590ms | 12.8ms |
| CMS GC | 38.693% | 39.6ms | 581ms | 11.6ms |
| G1 GC  | 59.777% | 30.7ms | 448ms | 2.56ms |

| Minor GC次数 | Full GC次数 | Minor GC时间 | Full GC时间 |      |
| ---------- | --------- | ---------- | --------- | ---- |
| 11         | 12        | 190ms      | 440ms     |      |
| 30         | 16        | 120ms      | 470ms     |      |
| 39         | 11        | 176ms      | 405ms     |      |
| 83         | 4         | 152ms      | 120ms     |      |

综合对比512M下GC性能 :G1 GC>CMS GC>并行GC>串行GC



## 堆内存1G下各GC之间性能对比

| GC策略   | 吞吐量     | 最大暂停时间 | 总暂停时间 | 平均暂停时间 |
| ------ | ------- | ------ | ----- | ------ |
| 串行GC   | 49.037% | 40ms   | 450ms | 12.5ms |
| 并行GC   | 46.759% | 40ms   | 460ms | 13.5ms |
| CMS GC | 52.968% | 42.9ms | 414ms | 18.8ms |
| G1 GC  | 76.406% | 12.7ms | 246ms | 4.36ms |

| Minor GC次数 | Full GC次数 | Minor GC时间 | Full GC时间 |      |
| ---------- | --------- | ---------- | --------- | ---- |
| 33         | 3         | 330ms      | 120ms     |      |
| 31         | 3         | 340ms      | 120ms     |      |
| 20         | 2         | 331ms      | 83.2ms    |      |
| 24         | 0         | 123ms      | 0ms       |      |

综合对比1G下GC性能 :G1 GC>CMS GC>串行GC>并行GC



## 堆内存2G下各GC之间性能对比

| GC策略   | 吞吐量     | 最大暂停时间 | 总暂停时间 | 平均暂停时间 |
| ------ | ------- | ------ | ----- | ------ |
| 串行GC   | 45.607% | 70ms   | 390ms | 55.7ms |
| 并行GC   | 56.815% | 30ms   | 320ms | 24.6ms |
| CMS GC | 47.497% | 50ms   | 430ms | 39.1ms |
| G1 GC  | 80.305% | 24.7ms | 185ms | 10.1ms |

| Minor GC次数 | Full GC次数 | Minor GC时间 | Full GC时间 |      |
| ---------- | --------- | ---------- | --------- | ---- |
| 7          | 0         | 390ms      | 0ms       |      |
| 13         | 0         | 320ms      | 0ms       |      |
| 11         | 0         | 430ms      | 0ms       |      |
| 15         | 0         | 164ms      | 0ms       |      |

综合对比1G下GC性能 :G1 GC>并行GC>CMS GC>串行GC



## 堆内存4G下各GC之间性能对比

| GC策略   | 吞吐量     | 最大暂停时间 | 总暂停时间 | 平均暂停时间 |
| ------ | ------- | ------ | ----- | ------ |
| 串行GC   | 33.941% | 110ms  | 290ms | 96.7ms |
| 并行GC   | 67.816% | 40ms   | 140ms | 35ms   |
| CMS GC | 45.709% | 48ms   | 406ms | 40.6ms |
| G1 GC  | 72.798% | 34.1ms | 209ms | 13.9ms |

| Minor GC次数 | Full GC次数 | Minor GC时间 | Full GC时间 |      |
| ---------- | --------- | ---------- | --------- | ---- |
| 3          | 0         | 290ms      | 0ms       |      |
| 4          | 0         | 140ms      | 0ms       |      |
| 15         | 0         | 406ms      | 0ms       |      |
| 15         | 0         | 209ms      | 0ms       |      |

综合对比1G下GC性能 :G1 GC>并行GC>CMS GC>串行GC



## 总结

1.除了内存过小（256m）会发生OOM外，G1 GC的性能都优于其他三个GC

2.256M和1G，CMS GC要优于串行GC和并行GC；2G和4G，并行GC要优于CMS GC和串行GC，没有发生Full GC，并行的Minor GC要优于CMS