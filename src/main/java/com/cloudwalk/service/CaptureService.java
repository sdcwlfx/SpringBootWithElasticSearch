package com.cloudwalk.service;

import com.cloudwalk.producer.KafkaSender;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.*;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
public class CaptureService {
    @Autowired
    private KafkaSender kafkaSender;
    private String STOCK_CAPTURE_TOPIC="dl_stock_capture"; //存量capture kafka主题

    @Transactional
    public void writeToKafka() throws IOException {
        RestHighLevelClient client =new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("21.30.36.18",11206,"http")
                )
        );

        final Scroll scroll=new Scroll(TimeValue.timeValueMinutes(1L));
        //索引
        SearchRequest searchRequest=new SearchRequest("capture-202004");
        searchRequest.scroll(scroll);
        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();
        //分页规模
        searchSourceBuilder.size(10000);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse=client.search(searchRequest, RequestOptions.DEFAULT);
        String scrollId=searchResponse.getScrollId();
        SearchHit[] searchHits=searchResponse.getHits().getHits();

        for(SearchHit d:searchHits){
            //System.out.println(d.getSourceAsString());//json形式数据
            kafkaSender.send(STOCK_CAPTURE_TOPIC,d.getSourceAsString());
        }

        //滚动分页
        while (searchHits!=null && searchHits.length >0){
            SearchScrollRequest scrollRequest=new SearchScrollRequest(scrollId);
            scrollRequest.scroll(scroll);
            searchResponse=client.scroll(scrollRequest,RequestOptions.DEFAULT);
            scrollId=searchResponse.getScrollId();
            searchHits=searchResponse.getHits().getHits();
            for(SearchHit d:searchHits){
                //System.out.println(d.getSourceAsString());
                kafkaSender.send(STOCK_CAPTURE_TOPIC,d.getSourceAsString());
            }
        }

        ClearScrollRequest clearScrollRequest=new ClearScrollRequest();
        clearScrollRequest.addScrollId(scrollId);
        ClearScrollResponse clearScrollResponse=client.clearScroll(clearScrollRequest,RequestOptions.DEFAULT);
        boolean succeeded=clearScrollResponse.isSucceeded();

    }

}
