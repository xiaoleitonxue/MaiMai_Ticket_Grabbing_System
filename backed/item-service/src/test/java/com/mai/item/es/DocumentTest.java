package com.mai.item.es;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mai.common.utils.BeanUtils;
import com.mai.item.domain.po.Item;
import com.mai.item.domain.po.ItemDoc;
import com.mai.item.service.IItemService;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

@SpringBootTest(properties = {"spring.profiles.active=dev"})
public class DocumentTest {

    @Autowired
    private IItemService itemService;

    private static final String INDEX_NAME = "items";

    private RestHighLevelClient client;

    @BeforeEach
    public void init() {
        client = new RestHighLevelClient(RestClient.builder(HttpHost.create("http://192.168.6.102:9200")));
    }

    @AfterEach
    public void close() throws IOException {
        client.close();
    }

    @Test
    public void testCreateDocument() throws IOException {

        Item item = itemService.getById(577967L);

        ItemDoc itemDoc = BeanUtils.copyBean(item, ItemDoc.class);

        IndexRequest indexRequest = new IndexRequest(INDEX_NAME).id(itemDoc.getId().toString());

        String json = JSONUtil.toJsonStr(itemDoc);
        indexRequest.source(json, XContentType.JSON);

        client.index(indexRequest, RequestOptions.DEFAULT);
    }

    @Test
    public void testGetDocument() throws IOException {
        GetRequest request = new GetRequest(INDEX_NAME).id("577967");
        GetResponse response = client.get(request, RequestOptions.DEFAULT);
        String json = response.getSourceAsString();
        System.out.println(json);
    }

    @Test
    public void testUpdateDocument() throws IOException {
        UpdateRequest request = new UpdateRequest(INDEX_NAME, "577967");
        request.doc(
                "name", "测试商品",
                "price", 1000
        );
        client.update(request, RequestOptions.DEFAULT);
    }

    @Test
    public void testDeleteDocument() throws IOException {
        DeleteRequest request = new DeleteRequest(INDEX_NAME, "577967");
        client.delete(request, RequestOptions.DEFAULT);
    }

    @Test
    public void testImportItem() throws IOException {

        int pageNo = 1;
        int pageSize = 1000;

        while (true) {
            System.out.println("----------------------开始导入第" + pageNo + "页数据--------------------------");

            Page<Item> page = itemService.lambdaQuery().eq(Item::getStatus, 1)
                    .page(new Page<>(pageNo, pageSize));
            List<Item> itemList = page.getRecords();
            if (itemList.isEmpty()) {
                break;
            }

            List<ItemDoc> itemDocList = BeanUtils.copyList(itemList, ItemDoc.class);

            BulkRequest bulkRequest = new BulkRequest();
            for (ItemDoc itemDoc : itemDocList) {
                IndexRequest request = new IndexRequest(INDEX_NAME).id(itemDoc.getId().toString());
                String json = JSONUtil.toJsonStr(itemDoc);
                request.source(json, XContentType.JSON);
                bulkRequest.add(request);
            }

            client.bulk(bulkRequest, RequestOptions.DEFAULT);
            System.out.println("----------------------结束导入第" + pageNo + "页数据--------------------------");

            pageNo++;
        }
    }

}
