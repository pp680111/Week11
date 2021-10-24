# 缓存性能数据测试记录

## 测试环境

操作：http请求接口，接口使用MyBatis的Mapper通过主键id从数据库中查询出一行数据

变量：MyBatis层的查询缓存，Controller接口层的ehcache和redis缓存

测试环境：CPU AMD Ryzen 3600， 程序运行在Win10环境， wrk和redis运行在wsl环境

## 测试数据

以下测试数据均是多次运行取中间结果

测试数据1：在没有使用任何缓存的情况下使用wrk测试接口QPS

```
root@DESKTOP-8JC68C0:~/wrk-master# ./wrk -c 30 -t 3 -d 30 http://192.168.123.175:8080/get?id=501918562634432534
Running 30s test @ http://192.168.123.175:8080/get?id=501918562634432534
  3 threads and 30 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     4.01ms    8.00ms 127.20ms   91.88%
    Req/Sec     4.98k   591.35    13.36k    73.92%
  446645 requests in 30.10s, 96.77MB read
Requests/sec:  14838.76
Transfer/sec:      3.22MB
```

QPS大约在14800左右

测试数据2：使用MyBatis的二级缓存

```
root@DESKTOP-8JC68C0:~/wrk-master# ./wrk -c 30 -t 3 -d 30 http://192.168.123.175:8080/get?id=501918562634432534
Running 30s test @ http://192.168.123.175:8080/get?id=501918562634432534
  3 threads and 30 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     1.26ms  836.81us  16.42ms   74.84%
    Req/Sec     7.54k   698.38     9.90k    73.31%
  654700 requests in 30.00s, 141.85MB read
Requests/sec:  21821.96
Transfer/sec:      4.73MB
```

测试数据3：使用ehcache作为接口的缓存

```
root@DESKTOP-8JC68C0:~/wrk-master# ./wrk -c 30 -t 3 -d 30 http://192.168.123.175:8080/get?id=501918562634432534
Running 30s test @ http://192.168.123.175:8080/get?id=501918562634432534
  3 threads and 30 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     0.89ms  771.02us  26.91ms   97.21%
    Req/Sec    10.18k     1.53k   13.37k    81.81%
  798904 requests in 30.01s, 173.09MB read
Requests/sec:  26621.65
Transfer/sec:      5.77MB
```

测试数据4：使用redis作为接口的缓存

```
root@DESKTOP-8JC68C0:~/wrk-master# ./wrk -t 3 -c 30 -d 30 http://192.168.123.175:8080/get?id=501918562634432525
Running 30s test @ http://192.168.123.175:8080/get?id=501918562634432525
  3 threads and 30 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     3.31ms    6.69ms 159.49ms   98.37%
    Req/Sec     3.69k   521.65     4.37k    93.89%
  330444 requests in 30.01s, 71.60MB read
Requests/sec:  11011.87
Transfer/sec:      2.39MB
```

将程序移动到wsl环境中运行，让wrk、程序、redis-server和MySQL都运行在同一个本地网络中，进行测试。

```
root@DESKTOP-8JC68C0:~/wrk-master# ./wrk -t 3 -c 30 -d 30 http://127.0.0.1:8080/get?id=570687886760869889
Running 30s test @ http://127.0.0.1:8080/get?id=570687886760869889
  3 threads and 30 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     1.24ms  531.62us  29.56ms   96.43%
    Req/Sec     8.19k   635.24    17.83k    73.14%
  734150 requests in 30.10s, 166.07MB read
Requests/sec:  24391.20
Transfer/sec:      5.52MB
```

## 总结

1. 缓存的效果越靠近请求方越好。从使用MyBatis二级缓存与接口缓存的测试数据对比可以看出，在排除误差之后，在相同的请求压力下，接口缓存的数据会稍微比MyBatis二级缓存的测试数据好一些。因为在测试代码中的Service层只是简单的根据主键查询了一行数据，如果Service层的查询操作更加复杂的话，这个差距会更加明显一些

2. 在性能上，本地缓存比远程缓存好。从使用ehcache和redis的测试数据对别可以看出，如果redis与程序不在同一个本地网络的情况下，使用redis时的请求响应时间比使用ehcache时高了很多，间接影响到了QPS的高低。将程序与redis运行在同一个本地网络环境下，减少网络传输延迟，可以看到响应时间和QPS有很大的改善。

   虽然本地缓存性能比远程缓存好，但是分布式服务架构下，多个服务器的本地缓存数据可能存在不一致的情况，本地缓存和远程缓存孰优孰略不能只靠性能一概而论，还是得看场景来决定