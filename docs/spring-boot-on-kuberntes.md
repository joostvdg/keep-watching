# Spring Boot on Kubernetes

## Java on Kubernetes

* https://www.youtube.com/watch?v=wApqCjHWF8Q
* https://www.alibabacloud.com/help/en/sae/use-cases/best-practices-for-jvm-heap-size-configuration
* For Containers
    * Set to whatever the applications needs, but no more than 75% of container memory limit
    * e.g., container request: 1024mi, limit: 1024mi
    * --XX:MaxRAMPercentage=75
    * the larger the total memory, the fraction can be larger (e.g., the off-heap needs minimal)
* Base recommendation:
    * Set CPU requests to 2000m
    * Set --XX:ActiveProcessorCount=2 (e.g., CPU requests / 1000, with minimal 1)
    * --XX:MaxRAMPercentage=75
    * minimal of 2gb of ram
        * if using 2-4gb of ram, use ParallelGC
        * if using 4GB or more, use G1GC
    * if using more than 4000m (e.g., 4 cores of time)
        * 4-32GB, use Shenandoah or ZGV
        * >32GB use ZGC

### GC Recommendations - Starting Points

| Measure       | Serial        | Parallel      | G1            | Z             | Shenandoah        |
| Cores         | 1             | 2+            | 2+            | 2+            | 2+                |
| Multi-thread  | No            | Yes           | Yes           | Yes           | Yes               |
| Heap Size     | <4GB          | <4GB          | >4GB          | >4GB          | >4GB              |
| Pause         | Yes           | Yes           | Yes           | Yes (<1ms)    | Yes (<1ms)        |
| Overhead      | Minimal       | Minimal       | Moderate      | Moderate      | Moderate          |
| Tail-latency E| High          | High          | High          | Low           | Moderate          |
| JDK           | All           | All           | 8+            | 17+           | 11+               |
| Target        | small 1cpu    | small 2cpu up | responsive M/L| responsive M/L| responsive M/L    |


## Optimize Spring Boot

* https://www.alibabacloud.com/help/en/sae/use-cases/configure-health-checks-for-a-spring-boot-application?spm=a2c63.p38356.0.0.3e4e43b6oJA9KY
