package com.mai.item.es;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class IndexText {

    private static final String INDEX_NAME = "items";

    private static final String MAPPING_TEMPLATE = "{\n" +
            "  \"mappings\": {\n" +
            "    \"_doc\": {\n" +
            "      \"properties\": {\n" +
            "        \"id\": {\n" +
            "          \"type\": \"keyword\"\n" +
            "        },\n" +
            "        \"name\": {\n" +
            "          \"type\": \"text\",\n" +
            "          \"analyzer\": \"ik_max_word\"\n" +
            "        },\n" +
            "        \"price\": {\n" +
            "          \"type\": \"integer\"\n" +
            "        },\n" +
            "        \"stock\": {\n" +
            "          \"type\": \"integer\"\n" +
            "        },\n" +
            "        \"image\": {\n" +
            "          \"type\": \"keyword\",\n" +
            "          \"index\": false\n" +
            "        },\n" +
            "        \"category\": {\n" +
            "          \"type\": \"keyword\"\n" +
            "        },\n" +
            "        \"brand\": {\n" +
            "          \"type\": \"keyword\"\n" +
            "        },\n" +
            "        \"sold\": {\n" +
            "          \"type\": \"integer\"\n" +
            "        },\n" +
            "        \"commentCount\": {\n" +
            "          \"type\": \"integer\"\n" +
            "        },\n" +
            "        \"isAD\": {\n" +
            "          \"type\": \"boolean\"\n" +
            "        },\n" +
            "        \"updateTime\": {\n" +
            "          \"type\": \"date\"\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";

    private RestHighLevelClient client;

    @BeforeEach
    public void init() {
        client = new RestHighLevelClient(RestClient.builder(HttpHost.create("http://192.168.6.102:9200")));
    }

    @Test
    public void testClient() {
        System.out.println(client);
    }

    @AfterEach
    public void close() throws IOException {
        client.close();
    }

    @Test
    public void createIndex() throws IOException {
        CreateIndexRequest request = new CreateIndexRequest(INDEX_NAME);
        request.source(MAPPING_TEMPLATE, XContentType.JSON);
        client.indices().create(request, RequestOptions.DEFAULT);
    }

    @Test
    public void isIndexExist() throws IOException {
        GetIndexRequest request = new GetIndexRequest(INDEX_NAME);
        boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
        System.out.println(exists?"存在":"不存在");
    }

    @Test
    public void deleteIndex() throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest(INDEX_NAME);
        client.indices().delete(request, RequestOptions.DEFAULT);
        System.out.println("索引删除成功");
    }
}