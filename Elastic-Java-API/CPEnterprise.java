package usr.enterprise;

import com.google.gson.Gson;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.ParseException;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsRequest;
import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsResponse;
import org.elasticsearch.action.admin.indices.shrink.ResizeRequest;
import org.elasticsearch.action.admin.indices.shrink.ResizeResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.*;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.cardinality.Cardinality;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import usr.es.op.ESHighLevelRestClient;

import java.io.IOException;
import java.util.*;

@Component
public class CPEnterprise {
    @Autowired
    ESHighLevelRestClient esHighRestClient;

    /*获取活跃用户数*/
    public String getEPSActiveUserCount(String startTime, String endTime) {
        String result = "";
        try {
            RestHighLevelClient client = esHighRestClient.getRestClient();
            SearchRequest searchRequest = new SearchRequest("cloudplus-clientpv-*");
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            QueryBuilder timeRangeBuilder = QueryBuilders.rangeQuery("collectTime")
                    .gte(startTime)
                    .lte(endTime).format("yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis").timeZone("+08:00");
            TermsAggregationBuilder aggregation = AggregationBuilders.terms("group_by_enterpriseID")
                    .field("enterpriseID.keyword")
                    .order(BucketOrder.aggregation("userCount ", false)).size(1000);

            aggregation.subAggregation(AggregationBuilders.cardinality("userCount")
                    .field("userID.keyword"));
            searchSourceBuilder.query(timeRangeBuilder);
            searchSourceBuilder.aggregation(aggregation);
            searchSourceBuilder.size(0);
            searchRequest.source(searchSourceBuilder);

            SearchResponse searchResponse = client.search(searchRequest);
            SearchHits hits = searchResponse.getHits();
            Aggregations agg = searchResponse.getAggregations();
            Terms terms = agg.get("group_by_enterpriseID");
            List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();

            for (Terms.Bucket bucket : terms.getBuckets()) {
                Map<String, String> infoMap = new HashMap<>();
                String enterpriseID = bucket.getKeyAsString();
                Cardinality count = bucket.getAggregations().get("userCount");
                long userCount = count.getValue();
                infoMap.put("enterpriseID", enterpriseID);
                infoMap.put("userCount", String.valueOf(userCount));
                mapList.add(infoMap);
            }
            result = new Gson().toJson(mapList);
        } catch (IOException e) {
            e.printStackTrace();
            result = e.toString();
        }
        return result;
    }

    /*获取用户PV数*/
    public String getEPSUserPVCount(String enterpriseID, String startTime, String endTime) {
        String result = "";
        try {
            RestHighLevelClient client = esHighRestClient.getRestClient();
            SearchRequest searchRequest = new SearchRequest("cloudplus-clientpv-*");
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            QueryBuilder nameQuery = QueryBuilders.termQuery("enterpriseID.keyword", enterpriseID);
            QueryBuilder timeRangeBuilder = QueryBuilders.rangeQuery("collectTime")
                    .gte(startTime)
                    .lte(endTime).format("yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis").timeZone("+08:00");
            TermsAggregationBuilder aggregation = AggregationBuilders.terms("group_by_userID")
                    .field("userID.keyword")
                    .order(BucketOrder.count(false)).size(10000);

            aggregation.subAggregation(AggregationBuilders.terms("group_by_username")
                    .field("userName.keyword"));
            searchSourceBuilder.query(QueryBuilders.boolQuery().must(nameQuery).must(timeRangeBuilder));
            searchSourceBuilder.aggregation(aggregation);
            searchSourceBuilder.size(0);
            searchRequest.source(searchSourceBuilder);

            SearchResponse searchResponse = client.search(searchRequest);
            SearchHits hits = searchResponse.getHits();
            Aggregations agg = searchResponse.getAggregations();
            Terms terms = agg.get("group_by_userID");
            List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();

            for (Terms.Bucket bucket : terms.getBuckets()) {
                Map<String, String> infoMap = new HashMap<>();
                String userID = bucket.getKeyAsString();
                long pvCount = bucket.getDocCount();
                Terms terms1 = bucket.getAggregations().get("group_by_username");
                String userName = terms1.getBuckets().get(0).getKeyAsString();
                infoMap.put("userID", userID);
                infoMap.put("userName", userName);
                infoMap.put("pvCount", String.valueOf(pvCount));
                mapList.add(infoMap);
            }
            result = new Gson().toJson(mapList);
        } catch (IOException e) {
            e.printStackTrace();
            result = e.toString();
        }
        return result;
    }

    /*获取企业PV数*/
    public String getEPSPVCount(String indexName, String startTime, String endTime) {
        String result = "";
        try {
            RestHighLevelClient client = esHighRestClient.getRestClient();
            SearchRequest searchRequest = new SearchRequest(indexName);
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            QueryBuilder timeRangeBuilder = QueryBuilders.rangeQuery("collectTime")
                    .gte(startTime)
                    .lte(endTime).format("yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis").timeZone("+08:00");
            TermsAggregationBuilder aggregation = AggregationBuilders.terms("group_by_enterpriseID")
                    .field("enterpriseID.keyword")
                    .order(BucketOrder.count(false)).size(1000);

            searchSourceBuilder.query(QueryBuilders.boolQuery().must(timeRangeBuilder));
            searchSourceBuilder.aggregation(aggregation);
            searchSourceBuilder.size(0);
            searchRequest.source(searchSourceBuilder);

            SearchResponse searchResponse = client.search(searchRequest);
            SearchHits hits = searchResponse.getHits();
            Aggregations agg = searchResponse.getAggregations();
            Terms terms = agg.get("group_by_enterpriseID");
            List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();

            for (Terms.Bucket bucket : terms.getBuckets()) {
                Map<String, String> infoMap = new HashMap<>();
                String enterpriseID = bucket.getKeyAsString();
                long pvCount = bucket.getDocCount();

                infoMap.put("enterpriseID", enterpriseID);
                infoMap.put("pvCount", String.valueOf(pvCount));
                mapList.add(infoMap);
            }
            result = new Gson().toJson(mapList);
        } catch (IOException e) {
            e.printStackTrace();
            result = e.toString();
        }
        return result;
    }

    /*获取企业异常数*/
    public String getEPExceptionCount(String indexName, String startTime, String endTime) {
        String result = "";
        try {
            RestHighLevelClient client = esHighRestClient.getRestClient();
            SearchRequest searchRequest = new SearchRequest(indexName);
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            QueryBuilder timeRangeBuilder = QueryBuilders.rangeQuery("happenTime")
                    .gte(startTime)
                    .lte(endTime).format("yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis").timeZone("+08:00");
            TermsAggregationBuilder aggregation = AggregationBuilders.terms("group_by_enterpriseID")
                    .field("enterpriseID.keyword")
                    .order(BucketOrder.count(false)).
                            size(1000);

            searchSourceBuilder.query(QueryBuilders.boolQuery().must(timeRangeBuilder));
            searchSourceBuilder.aggregation(aggregation);
            searchSourceBuilder.size(0);
            searchRequest.source(searchSourceBuilder);

            SearchResponse searchResponse = client.search(searchRequest);
            SearchHits hits = searchResponse.getHits();
            Aggregations agg = searchResponse.getAggregations();
            Terms terms = agg.get("group_by_enterpriseID");
            List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();

            for (Terms.Bucket bucket : terms.getBuckets()) {
                Map<String, String> infoMap = new HashMap<>();
                String enterpriseID = bucket.getKeyAsString();
                long exCount = bucket.getDocCount();
                infoMap.put("enterpriseID", enterpriseID);
                infoMap.put("exCount", String.valueOf(exCount));
                mapList.add(infoMap);
            }
            result = new Gson().toJson(mapList);
        } catch (IOException e) {
            e.printStackTrace();
            result = e.toString();
        }
        return result;
    }

    /*获取用户异常信息*/
    public String getUserExceptionInfo(String userID, String indexName, String startTime, String endTime) {
        String result = "";
        try {
            RestHighLevelClient client = esHighRestClient.getRestClient();
            SearchRequest searchRequest = new SearchRequest(indexName);
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            QueryBuilder timeRangeBuilder = QueryBuilders.rangeQuery("happenTime")
                    .gte(startTime)
                    .lte(endTime).format("yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis").timeZone("+08:00");

            searchSourceBuilder.query(QueryBuilders.boolQuery().must(timeRangeBuilder));
            searchSourceBuilder.size(10000);
            searchRequest.source(searchSourceBuilder);

            SearchResponse searchResponse = client.search(searchRequest);
            SearchHits hits = searchResponse.getHits();
            List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
            for (SearchHit hit : hits.getHits()) {
                Map amap = hit.getSourceAsMap();
                mapList.add(amap);
            }
            result = new Gson().toJson(mapList);
        } catch (IOException e) {
            e.printStackTrace();
            result = e.toString();
        }
        return result;
    }

    public String settingProxy(String nodeName, String indexName) {
        String result = "";
        String settingKey = "index.routing.allocation.require._name";
        String settingValue = nodeName;
        String settingKey1 = "index.blocks.write";
        Boolean settingValue1 = true;

        Settings settingsBuilder =
                Settings.builder()
                        .put(settingKey, settingValue)
                        .put(settingKey1, settingValue1)
                        .build();

        result = this.setSettings(settingsBuilder, indexName);
        return result;
    }

    private String setSettings(Settings settingsBuilder, String indexName) {
        String result = "";
        try {
            RestHighLevelClient client = esHighRestClient.getRestClient();
            UpdateSettingsRequest request = new UpdateSettingsRequest(indexName);
            request.settings(settingsBuilder);
            UpdateSettingsResponse updateSettingsResponse = null;

            updateSettingsResponse = client.indices().putSettings(request);

            result = String.valueOf(updateSettingsResponse.isAcknowledged());
            System.out.println("setting is : " + result);
            if (updateSettingsResponse.isAcknowledged() == true) {
                String shrink = Shrink(indexName + "_backup", indexName);
                System.out.println("Shrink : " + shrink);
                Delete(indexName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private String Shrink(String targetIndex, String sourceIndex) {
        String result = "false";
        RestHighLevelClient client = esHighRestClient.getRestClient();
        ResizeRequest request = new ResizeRequest(targetIndex, sourceIndex);
        request.getTargetIndexRequest().settings(Settings.builder()
                .put("index.number_of_shards", 1)
                .put("index.number_of_replicas", 1)
                .put("index.codec", "best_compression"));
        try {
            String responseStr = client.indices().shrink(request).toString();
            System.out.println("shrink is : " + responseStr);
        } catch (IOException e) {
            e.printStackTrace();
            if(e.getMessage().contains("OK"))
            {
                result = "true";
            }
        }
        return result;
    }

    private void Delete(String indexName) {
        try {
            RestHighLevelClient client = esHighRestClient.getRestClient();
            DeleteIndexRequest request = new DeleteIndexRequest(indexName);
            AcknowledgedResponse deleteIndexResponse = client.indices().delete(request);
            if(deleteIndexResponse.isAcknowledged() == true)
            {
                System.out.println("delete success !! ");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
