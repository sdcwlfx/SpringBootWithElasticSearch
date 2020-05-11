## 环境  
    springboot2.1、elasticsearch7.3.2、kafka2.3.0  

## 目录  
    /producer/KafkaSender ->kafka生产者  
    /runner/MyApplicationRunner ->springboot程序启动后，自动执行的类，其实现了ApplicationRunner接口  ，调用CaptureService服务  
    /service/CaptureService ->服务，连接es,检索索引，滚动检索数据并写入kafka集群中
    
