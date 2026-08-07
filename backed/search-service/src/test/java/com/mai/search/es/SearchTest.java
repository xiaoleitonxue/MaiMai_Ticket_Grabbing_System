package com.mai.search.es;

import cn.hutool.json.JSONUtil;
import com.mai.search.domain.po.ItemDoc;
import org.apache.http.HttpHost;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.metrics.Stats;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class SearchTest {

    private RestHighLevelClient client;
    private final String INDEX_NAME = "items";

    @BeforeEach
    public void init() {
        client = new RestHighLevelClient(
                RestClient.builder(
                        HttpHost.create("http://192.168.6.102:9200")
                )
        );
    }

    @AfterEach
    public void close() throws IOException {
        client.close();
    }

    private static void handleResponse(SearchResponse searchResponse) {
        SearchHits searchHits = searchResponse.getHits();
        long total = searchHits.getTotalHits().value;
        System.out.println("total: " + total);

        SearchHit[] hits = searchHits.getHits();
        if (hits != null && hits.length > 0) {
            for (SearchHit hit : hits) {
                String jsonStr = hit.getSourceAsString();
                ItemDoc itemDoc = JSONUtil.toBean(jsonStr, ItemDoc.class);

                Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                if (highlightFields != null && highlightFields.containsKey("name")) {
                    HighlightField highlightField = highlightFields.get("name");
                    String highlightName = highlightField.getFragments()[0].string();
                    itemDoc.setName(highlightName);
                }

                System.out.println(itemDoc);
            }
        }
    }

    @Test
    public void testMatchAll() throws IOException {

        SearchRequest searchRequest = new SearchRequest(INDEX_NAME);

        searchRequest.source().query(QueryBuilders.matchAllQuery());

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        handleResponse(searchResponse);
    }

    @Test
    public void testMatch() throws IOException {

        SearchRequest searchRequest = new SearchRequest(INDEX_NAME);

        searchRequest.source().query(QueryBuilders.matchQuery("name", "周杰伦"));

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        handleResponse(searchResponse);
    }

    @Test
    public void testMultiMatch() throws IOException {

        SearchRequest searchRequest = new SearchRequest(INDEX_NAME);

        searchRequest.source().query(QueryBuilders.multiMatchQuery("脱脂", "name", "category"));

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        handleResponse(searchResponse);
    }

    @Test
    public void testTerm() throws IOException {

        SearchRequest searchRequest = new SearchRequest(INDEX_NAME);

        searchRequest.source().query(QueryBuilders.termQuery("brand.keyword", "小米"));

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        handleResponse(searchResponse);
    }

    @Test
    public void testRange() throws IOException {

        SearchRequest searchRequest = new SearchRequest(INDEX_NAME);

        searchRequest.source().query(QueryBuilders.rangeQuery("price").gte(100).lte(200));

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        handleResponse(searchResponse);
    }

    @Test
    public void testBool() throws IOException {

        SearchRequest searchRequest = new SearchRequest(INDEX_NAME);

        QueryBuilder query = QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery("name", "手机"))
                .filter(QueryBuilders.rangeQuery("price").gte(100).lte(30000))
                .filter(QueryBuilders.termQuery("brand.keyword", "华为"));

        searchRequest.source().query(query);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        handleResponse(searchResponse);
    }

    @Test
    public void testSortAndPage() throws IOException {

        SearchRequest searchRequest = new SearchRequest(INDEX_NAME);

        QueryBuilder query = QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery("name", "手机"))
                .filter(QueryBuilders.rangeQuery("price").gte(100).lte(30000))
                .filter(QueryBuilders.termQuery("brand.keyword", "华为"));

        searchRequest.source().query(query)
                .sort("price", SortOrder.ASC)
                .from(1)
                .size(5);

        searchRequest.source().query(query);

               SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        handleResponse(searchResponse);
    }

    @Test
    public void testHighlight() throws IOException {

        SearchRequest searchRequest = new SearchRequest(INDEX_NAME);

        searchRequest.source().query(QueryBuilders.matchQuery("brand", "小米"))
                .highlighter(new HighlightBuilder()
                        .field("name")
                        .preTags("<font color='red'>")
                        .postTags("</font>")
                        .requireFieldMatch(false));

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        handleResponse(searchResponse);
    }

    @Test
    public void testAggs() throws IOException {
        SearchRequest searchRequest = new SearchRequest(INDEX_NAME);

        searchRequest.source().query(QueryBuilders.termQuery("category.keyword", "手机"));

        searchRequest.source()
                .size(0)
                .aggregation(
                        AggregationBuilders
                                .terms("brand_agg")
                                .field("brand.keyword")
                                .size(20)
                                .subAggregation(
                                        AggregationBuilders
                                                .stats("price_stats")
                                                .field("price")
                                )
                );

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        Aggregations aggregations = searchResponse.getAggregations();
        Terms brandAgg = aggregations.get("brand_agg");
        if (brandAgg != null) {
            List<? extends Terms.Bucket> buckets = brandAgg.getBuckets();
            for (Terms.Bucket bucket : buckets) {

                System.out.println("---------------------------------------------");
                System.out.println(bucket.getKeyAsString() + ":" + bucket.getDocCount());

                Aggregations statAgg = bucket.getAggregations();
                Stats stats = statAgg.get("price_stats");

                System.out.println("-----------------");
                System.out.println("平均价格：" + stats.getAvg());
                System.out.println("最大价格：" + stats.getMax());
                System.out.println("最小价格：" + stats.getMin());
                System.out.println("总价格：" + stats.getSum());
                System.out.println("-----------------");
            }
        }
    }

}