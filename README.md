# springboot-config-refresh-starter

    功能：实现配置信息的自动刷新：
    v1.0: 
        暂未做配置文件的环境切换
        支持普通配置信息动态刷新
        数据源的配置,目前只支持druid,mysql数据源
        
    使用方式:
        1.clone项目到本地目录
        2.mvn install 打包到本地仓库
        3.pom依赖导入启动器:
            <dependency>
                <groupId>com.lb</groupId>
                <artifactId>springboot-config-refresh-starter</artifactId>
                <version>0.0.1-SNAPSHOT</version>
            </dependency>
            
        4.application.properties文件
            config.refresh.enable=true  //开启自动刷新功能
            config.refresh.key=config   //指定存放配置信息的redis_key
          并配置相关redis配置信息,如
            #spring.redis.host=localhost
            #spring.redis.password=123456
            #spring.redis.database=1    //指定数据库（默认为0）,redis_key 会根据这个去读取存放在redis的配置
        
        5.需要动态刷新的配置信息放到redis.以map的形式
        
没事干写的,有点烂，希望大佬给点指点改进下，啊哈哈哈
        
