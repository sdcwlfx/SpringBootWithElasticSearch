package com.cloudwalk.search;

import com.cloudwalk.config.ElasticSearchConfig;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.function.Consumer;

//未使用

public class ElasticSearch{
    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient client;
    private static final String UID="_id";

    public static void searchForHit(SearchRequestBuilder requestBuilder, Consumer<SearchHit> consumer){
        searchForResponse(requestBuilder,searchResponse -> {
            SearchHit[] searchHits=searchResponse.getHits().getHits();
            for(SearchHit searchHit:searchHits){
                consumer.accept(searchHit);
            }
        });
    }

    public static void searchForResponse(SearchRequestBuilder requestBuilder,Consumer<SearchResponse> consumer){
        if(requestBuilder==null || consumer==null){
            return;
        }

        SearchRequestBuilder builder=requestBuilder.addSort(UID, SortOrder.ASC);
        builder.setIndices("capture-202004");
        SearchResponse searchResponse=builder.get();
        SearchHit[] searchHits=searchResponse.getHits().getHits();
        while (searchHits!=null && searchHits.length > 0){
            consumer.accept(searchResponse);
            SearchHit last=searchHits[searchHits.length-1];
            builder=builder.searchAfter(last.getSortValues());
            searchResponse=builder.get();
            searchHits=searchResponse.getHits().getHits();

        }

    }


    public void searchFromES(){
        SearchRequest searchRequest=new SearchRequest("capture-202004");



    }
}