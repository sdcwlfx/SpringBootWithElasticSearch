package com.cloudwalk.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

//将普通pojo实例化到spring容器中，泛指各种组件，当类不属于各种归类的时候(不属于@Controller、@Service等)，
//就可以使用@Component来标注类
@Component
public class KafkaSender {

    @Autowired
    private KafkaTemplate<String,Object> kafkaTemplate;
    private final Logger logger= LoggerFactory.getLogger(KafkaSender.class);

    public void send(String topic,String jsonStr){
        //发送消息
        ListenableFuture<SendResult<String,Object>> future=kafkaTemplate.send(topic,jsonStr);
        future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
            @Override
            public void onFailure(Throwable ex) {
                logger.error(topic+" 写入消息失败："+ex.getMessage());

            }

            @Override
            public void onSuccess(SendResult<String, Object> result) {

                logger.info(topic+" 消息写入成功："+result.toString());

            }
        });
    }
}
