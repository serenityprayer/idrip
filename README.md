## DRIP 
分布式全局唯一ID生成器    

---
#### API接口文档
DRIP SERVICE API 
http://ip:port/drip/swagger-ui.html
- 生成新ID
- 生成带特定因子的ID
- 批量生成新ID
- 批量生成带特定因子的ID
- 解析ID元素
- 批量解析ID元素
- 生成指定元素的ID


---
#### ID算法说明
基于twitter snowflake算法，ID的组成元素（由高位到低位）:
- 41 bits: Timestamp，毫秒级，可支持使用69年，由用户自定义起始时间原点twepoch
- 10 bits: 机器节点ID，进一步拆分为datacenter ID 5 bits + worker ID 5 bits
- 9 bits: sequence number，毫秒内序列，9 bits可支持最高512/ms的tps
- 3 bits: sharding gene bits，用于分库分片的预留因子位，3 bits支持%8分片

加上最高位0（预留标志位，未使用）一共64 bits；使用中可根据业务实际情况调整ID的组成分布。

优点：全局唯一、粗略有序；高性能、易于扩展；可反解、可制造。     
缺点：机器ID获取易产生外部依赖（ZK、DB等）；依赖时钟连续性，如出现时钟回拨将无法正常工作。


---
#### 在Docker容器中运行

1.Dockerfile编写说明：  
- VOLUME 创建/tmp目录并持久化到Docker数据文件夹，因为Spring Boot使用的内嵌Tomcat容器默认使用/tmp作为工作目录     
- ADD drip-0.1.0.jar app.jar 将应用jar包复制到/app.jar       
- ENTRYPOINT表示容器运行后默认执行项目 app.jar。为了缩短Tomcat启动时间，添加一个系统属性指向"/dev/./urandom"作为Entropy Source   
    

2.运行docker构建镜像：    
<pre><code>docker build -t app-demo/drip .
</code></pre>

然后运行Docker容器：
<pre><code>docker run -d -p 8088:8088 --name drip-app app-demo/drip
</code></pre>

    
3.Maven插件支持：        
- 参考源码pom.xml中docker-maven-plugin的配置：     
imageName指定了镜像的名字；dockerDirectory指定Dockerfile的位置；resources是指那些需要和Dockerfile放在一起，在构建镜像时使用的文件          

- 经过以上配置后，运行下列命令可以在本地Docker中创建一个镜像：       
<pre><code>mvn package docker:build
</code></pre>

**note: docker repository name must be lowercase**    
**docker镜像名称需为小写，否则创建时会报not valid错误**
