## netty

- NIO
- 事件驱动

> 事件触发-》handler被调用-》处理

```
netty包
io.netty-all
    |---io
        |--netty
            |--bootstrap 配置并启动服务类
            |--buffer 缓冲相关类，对NIO Buffer封装
            |--channel 核心部分，处理连接
            |--handler 基于handler的扩展部分，实现协议编解码等附加功能
            |--resolver
            |--util
```

