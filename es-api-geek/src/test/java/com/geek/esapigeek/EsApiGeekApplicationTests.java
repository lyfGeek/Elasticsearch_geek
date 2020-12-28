package com.geek.esapigeek;

import com.alibaba.fastjson.JSON;
import com.geek.esapigeek.pojo.User;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class EsApiGeekApplicationTests {

//    @Autowired
//    @Qualifier("restHighLevelClient")
//    private RestHighLevelClient client;

    // or
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * 测试索引的创建。
     *
     * @throws IOException
     */
    @Test
    void testCreateIndex() throws IOException {
        // 创建索引。
        CreateIndexRequest request = new CreateIndexRequest("geek_index");
        // 执行创建请求 IndicesClient。请求后获得相应。
        CreateIndexResponse response = restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
        System.out.println(response);// org.elasticsearch.client.indices.CreateIndexResponse@88c8e176
    }

    /**
     * 测试获取索引。判断是否存在。
     *
     * @throws IOException
     */
    @Test
    void testExistIndex() throws IOException {
        GetIndexRequest request = new GetIndexRequest("geek_index");
        boolean exists = restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);
        System.out.println("exists = " + exists);// exists = true
    }

    /**
     * 测试删除索引。
     *
     * @throws IOException
     */
    @Test
    void testDeleteIndex() throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest("geek_index");
        AcknowledgedResponse delete = restHighLevelClient.indices().delete(request, RequestOptions.DEFAULT);
        System.out.println(delete);// org.elasticsearch.action.support.master.AcknowledgedResponse@4ee
        System.out.println(delete.isAcknowledged());// true。
    }

    // ~ ~ ~ 文档。

    /**
     * 测试增加文档。
     *
     * @throws IOException
     */
    @Test
    void testAddDocument() throws IOException {
        // 创建对象。
        User user = new User("geek", 23);
        // 创建请求。
        IndexRequest request = new IndexRequest("geek_index");

        // 规则。PUT /geek_index/_doc/1
        request.id("3");
//        request.timeout(TimeValue.timeValueSeconds(1));
        request.timeout("1s");

        // 将数据放入请求。
        request.source(JSON.toJSONString(user), XContentType.JSON);

        // 客户端发送请求。
        IndexResponse response = restHighLevelClient.index(request, RequestOptions.DEFAULT);

        System.out.println(response);
        // IndexResponse[index=geek_index,type=_doc,id=1,version=1,result=created,seqNo=0,primaryTerm=1,shards={"total":2,"successful":1,"failed":0}]
        System.out.println(response.status());
        // CREATED
    }

    /**
     * 获取文档。判断是否存在。
     *
     * @throws IOException
     */
    @Test
    void testIsExist() throws IOException {
        GetRequest getRequest = new GetRequest("geek_index", "1");
        // 不获取返回的 _source 上下文。
        getRequest.fetchSourceContext(new FetchSourceContext(false));
        getRequest.storedFields("_none_");

        boolean exists = restHighLevelClient.exists(getRequest, RequestOptions.DEFAULT);
        System.out.println("exists = " + exists);// true。
    }

    /**
     * 获取文档信息。
     *
     * @throws IOException
     */
    @Test
    void testGetDocument() throws IOException {
        GetRequest getRequest = new GetRequest("geek_index", "1");
        GetResponse getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
        System.out.println(getResponse.getSourceAsString());// 文档内容。
        // {"age":3,"name":"geek"}
        System.out.println(getResponse);
        // {"_index":"geek_index","_type":"_doc","_id":"1","_version":1,"_seq_no":0,"_primary_term":1,"found":true,"_source":{"age":3,"name":"geek"}}
    }

    /**
     * 更新文档信息。
     *
     * @throws IOException
     */
    @Test
    void testUpdateDocument() throws IOException {
        UpdateRequest updateRequest = new UpdateRequest("geek_index", "1");
        updateRequest.timeout("1s");

        User user = new User("Geek", 3);
        updateRequest.doc(JSON.toJSONString(user), XContentType.JSON);

        UpdateResponse response = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);

        System.out.println(response);
        // UpdateResponse[index=geek_index,type=_doc,id=1,version=2,seqNo=1,primaryTerm=1,result=updated,shards=ShardInfo{total=2, successful=1, failures=[]}]
        System.out.println(response.status());// OK
    }

    /**
     * 删除文档记录。
     *
     * @throws IOException
     */
    @Test
    void testDelete() throws IOException {
        DeleteRequest request = new DeleteRequest("geek_index", "3");
        request.timeout(TimeValue.timeValueSeconds(1));

        DeleteResponse response = restHighLevelClient.delete(request, RequestOptions.DEFAULT);
        System.out.println(response);
        // DeleteResponse[index=geek_index,type=_doc,id=3,version=3,result=deleted,shards=ShardInfo{total=2, successful=1, failures=[]}]
        // DeleteResponse[index=geek_index,type=_doc,id=3,version=4,result=not_found,shards=ShardInfo{total=2, successful=1, failures=[]}]
    }

    /**
     * 批量。
     *
     * @throws IOException
     */
    @Test
    void testBulkRequest() throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("10s");

        List<User> userList = new ArrayList<>();
        userList.add(new User("geek1", 3));
        userList.add(new User("geek2", 3));
        userList.add(new User("geek3", 3));
        userList.add(new User("李1", 3));
        userList.add(new User("李2", 3));
        userList.add(new User("李3", 3));

        for (int i = 0; i < userList.size(); i++) {
            bulkRequest.add(
                    new IndexRequest("geek_index")
                            .id("" + (i + 1))// 如果不设置 id，为随机 id。
                            .source(JSON.toJSONString(userList.get(i)), XContentType.JSON));
        }

        BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println(bulkResponse.hasFailures());// false。代表成功。
    }

    /**
     * 查询。
     *
     * @throws IOException
     */
    @Test
    void testSearch() throws IOException {
        SearchRequest searchRequest = new SearchRequest("geek_index");
        // 构建搜索条件。
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

//        new QueryBuilder()
        // 查询条件。可以使用 QueryBuilders 工具实现。
        // 匹配所有。QueryBuilders.matchAllQuery()
        // 精确匹配。QueryBuilders.termQuery("name", "geek1");
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name", "geek1");

        searchSourceBuilder.query(termQueryBuilder);
//        searchSourceBuilder.from();
//        searchSourceBuilder.size()
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        searchRequest.source(searchSourceBuilder);

        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println(JSON.toJSONString(response.getHits()));
        // {"fragment":true,"hits":[{"fields":{},"fragment":false,"highlightFields":{},"id":"1","matchedQueries":[],"primaryTerm":0,"rawSortValues":[],"score":1.7836733,"seqNo":-2,"sortValues":[],"sourceAsMap":{"name":"geek1","age":3},"sourceAsString":"{\"age\":3,\"name\":\"geek1\"}","sourceRef":{"fragment":true},"type":"_doc","version":-1}],"maxScore":1.7836733,"totalHits":{"relation":"EQUAL_TO","value":1}}
        System.out.println("～　～　～　～　～　～　～");
        for (SearchHit documentFields : response.getHits().getHits()) {
            System.out.println(documentFields.getSourceAsMap());// {name=geek1, age=3}
        }
    }

    @Test
    void contextLoads() {

    }

}
