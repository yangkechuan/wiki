## java client 请求 es

---

            环境：
            
            MAC 系统 10.15.6
            Java Version ：1.8.0_202
            Docker 方式安装
            elasticsearch version:7.8.0




## 0.新建 maven 项目
pom.xml 依赖信息：

```xml
    <dependencies>
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>fastjson</artifactId>
            <version>1.2.76</version>
        </dependency>
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-classic</artifactId>
            <version>1.2.10</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.22</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>org.elasticsearch.client</groupId>
            <artifactId>elasticsearch-rest-high-level-client</artifactId>
            <version>7.8.0</version>
        </dependency>
    </dependencies>
```


## 1.索引


### 1.1 创建索引


```java
package example;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;


import java.io.IOException;

public class EsClient {

    public static void main(String[] args) throws IOException {

        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200)));

        // 创建索引
        CreateIndexRequest request = new CreateIndexRequest("user");
        CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);
        boolean isAcknowledged =  response.isAcknowledged();
        // 响应状态
        System.out.println(isAcknowledged);
        client.close();
    }
}
```

### 1.2 查询索引

```java
package example;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;

import java.io.IOException;

public class EsClient {

    public static void main(String[] args) throws IOException {

        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200)));

        // 查询索引
        GetIndexRequest getIndexRequest = new GetIndexRequest("user");
        GetIndexResponse getIndexResponse = client.indices().get(getIndexRequest, RequestOptions.DEFAULT);
        System.out.println(getIndexResponse.getAliases());
        System.out.println(getIndexResponse.getMappings());
        System.out.println(getIndexResponse.getSettings());
        client.close();
    }
}

```


### 1.3 删除索引

```java
package example;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;

public class EsClient {

    public static void main(String[] args) throws IOException {

        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200)));


        // 删除索引
        DeleteIndexRequest request = new DeleteIndexRequest("user");
        AcknowledgedResponse response = client.indices().delete(request, RequestOptions.DEFAULT);
        System.out.println(response.isAcknowledged());
        client.close();
    }
}

```



## 2.数据

定义对象，作为后续使用的数据格式：

```java
package example;


import lombok.Data;

@Data
public class User {

    private String name;

    private Integer age;

    private String sex;
}

```

### 2.1 插入数据

```java
package example;

import com.alibaba.fastjson.JSON;
import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;

import java.io.IOException;

public class EsDocClient {

    public static void main(String[] args) throws IOException {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200)));

        // 插入数据
        IndexRequest indexRequest = new IndexRequest();
        indexRequest.index("user").id("1001");

        User user = new User();
        user.setName("张三");
        user.setAge(30);
        user.setSex("男");

        // 向 es 插入数据，需要为 json 格式
        indexRequest.source(JSON.toJSONString(user), XContentType.JSON);

        IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
        System.out.println(indexResponse.getResult());
        System.out.println(indexResponse.getId());
        
        client.close();
    }
}

```

### 2.2 更新数据

```java
package example;

import org.apache.http.HttpHost;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;


import java.io.IOException;

public class EsDocClient {

    public static void main(String[] args) throws IOException {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200)));

        // 局部修改
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.index("user").id("1001");
        updateRequest.doc(XContentType.JSON, "sex", "女");
        UpdateResponse updateResponse =  client.update(updateRequest, RequestOptions.DEFAULT);
        System.out.println(updateResponse.getResult());
        client.close();
    }
}

```
### 2.3 查询数据

```java
package example;

import org.apache.http.HttpHost;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;


import java.io.IOException;

public class EsDocClient {

    public static void main(String[] args) throws IOException {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200)));

        // 查询
        GetRequest getRequest = new GetRequest();
        getRequest.index("user").id("1001");
        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
        System.out.println(getResponse.getSourceAsString());
        client.close();
    }
}
```

### 2.3 删除数据

```java
package example;

import org.apache.http.HttpHost;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;


import java.io.IOException;

public class EsDocClient {

    public static void main(String[] args) throws IOException {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200)));

        // 删除
        DeleteRequest deleteRequest = new DeleteRequest();
        deleteRequest.index("user").id("1001");
        DeleteResponse deleteResponse = client.delete(deleteRequest, RequestOptions.DEFAULT);
        System.out.println(deleteResponse.getResult());
        client.close();
    }
}
```

### 2.4 批量插入

```java
package example;

import com.alibaba.fastjson.JSON;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;


import java.io.IOException;

public class EsDocClient {

    public static void main(String[] args) throws IOException {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200)));

        // 批量插入
        BulkRequest bulkRequest = new BulkRequest();

        User u1 = new User();
        u1.setName("李四");
        u1.setAge(20);
        u1.setSex("男");
        IndexRequest indexRequest = new IndexRequest();
        indexRequest.index("user").id("1003").source(JSON.toJSONString(u1), XContentType.JSON);
        bulkRequest.add(indexRequest);

        User u2 = new User();
        u2.setName("王五");
        u2.setAge(20);
        u2.setSex("男");
        IndexRequest indexRequest2 = new IndexRequest();
        indexRequest2.index("user").id("1004").source(JSON.toJSONString(u2), XContentType.JSON);
        bulkRequest.add(indexRequest2);

        BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println(bulkResponse.getTook());
        System.out.println(bulkResponse.getItems());

        client.close();
    }
}
```

### 2.5 批量删除


```java
package example;

import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;


import java.io.IOException;

public class EsDocClient {

    public static void main(String[] args) throws IOException {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200)));

        // 批量删除
        BulkRequest bulkRequest = new BulkRequest();
        DeleteRequest deleteRequest = new DeleteRequest();
        deleteRequest.index("user").id("1003");
        bulkRequest.add(deleteRequest);

        DeleteRequest deleteRequest1 = new DeleteRequest();
        deleteRequest1.index("user").id("1004");
        bulkRequest.add(deleteRequest1);

        BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println(bulkResponse.getTook());
        System.out.println(bulkResponse.getItems());

        client.close();
    }
}
```

### 2.6 高级查询

#### 2.6.1 全量查询


```java
package example;

import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;


import java.io.IOException;

public class EsDocClient {

    public static void main(String[] args) throws IOException {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200)));


        // 全量查询
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("user");

        searchRequest.source(new SearchSourceBuilder().query(QueryBuilders.matchAllQuery()));
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = searchResponse.getHits();
        System.out.println("总条数：" + hits.getTotalHits());
        hits.forEach(m -> {
            System.out.println(m.getId());
            System.out.println(m.getSourceAsString());
        });

        client.close();
    }
}
```

#### 2.6.2 条件查询(termQuery)

```java
package example;

import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import java.io.IOException;

public class EsDocClient {

    public static void main(String[] args) throws IOException {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200)));


        // 条件查询 termQuery
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("user");

        // 查询年龄为 20 的人
        searchRequest.source(new SearchSourceBuilder().query(QueryBuilders.termQuery("age", "20")));
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = searchResponse.getHits();
        System.out.println("总条数：" + hits.getTotalHits());
        hits.forEach(m -> {
            System.out.println(m.getId());
            System.out.println(m.getSourceAsString());
        });

        client.close();
    }
}
```


#### 2.6.3 分页查询

```java
package example;

import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import java.io.IOException;

public class EsDocClient {

    public static void main(String[] args) throws IOException {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200)));


        // 分页查询
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("user");

        // 从第一页开始，每页两条数据
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.from(0);
        builder.size(2);
        searchRequest.source(builder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = searchResponse.getHits();
        System.out.println("总条数：" + hits.getTotalHits());
        hits.forEach(m -> {
            System.out.println(m.getId());
            System.out.println(m.getSourceAsString());
        });

        client.close();
    }
}
```


#### 2.6.4 查询结果排序

```java
package example;

import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;

import java.io.IOException;

public class EsDocClient {

    public static void main(String[] args) throws IOException {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200)));


        // 排序查询
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("user");

        // 根据 age 字段，倒序
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.sort("age", SortOrder.DESC);
        searchRequest.source(builder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = searchResponse.getHits();
        System.out.println("总条数：" + hits.getTotalHits());
        hits.forEach(m -> {
            System.out.println(m.getId());
            System.out.println(m.getSourceAsString());
        });

        client.close();
    }
}
```

#### 2.6.5 过滤字段

```java
package example;

import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;

public class EsDocClient {

    public static void main(String[] args) throws IOException {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200)));


        // 过滤字段
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("user");

        // 只查询 name 相关的字段
        SearchSourceBuilder builder = new SearchSourceBuilder();
        String[] includes = {"name"};
        String[] excludes = {};
        builder.fetchSource(includes, excludes);
        searchRequest.source(builder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = searchResponse.getHits();
        System.out.println("总条数：" + hits.getTotalHits());
        hits.forEach(m -> {
            System.out.println(m.getId());
            System.out.println(m.getSourceAsString());
        });

        client.close();
    }
}
```


#### 2.6.6 组合条件查询

```java
package example;

import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;

public class EsDocClient {

    public static void main(String[] args) throws IOException {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200)));


        // 组合查询
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("user");

        // age = 20, sex = 男 or sex = 女
        SearchSourceBuilder builder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.must(QueryBuilders.matchQuery("age", 20));
        boolQueryBuilder.should(QueryBuilders.matchQuery("sex", "男"));
        boolQueryBuilder.should(QueryBuilders.matchQuery("sex", "女"));
        builder.query(boolQueryBuilder);
        searchRequest.source(builder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = searchResponse.getHits();
        System.out.println("总条数：" + hits.getTotalHits());
        hits.forEach(m -> {
            System.out.println(m.getId());
            System.out.println(m.getSourceAsString());
        });

        client.close();
    }
}
```


#### 2.6.7 范围查询

```java
package example;

import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;

public class EsDocClient {

    public static void main(String[] args) throws IOException {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200)));


        // 范围查询
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("user");

        // 20  >= age >= 10
        SearchSourceBuilder builder = new SearchSourceBuilder();
        RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder("age");
        rangeQueryBuilder.gte(10);
        rangeQueryBuilder.lte(20);
        builder.query(rangeQueryBuilder);
        searchRequest.source(builder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = searchResponse.getHits();
        System.out.println("总条数：" + hits.getTotalHits());
        hits.forEach(m -> {
            System.out.println(m.getId());
            System.out.println(m.getSourceAsString());
        });

        client.close();
    }
}
```

#### 2.6.8 模糊查询


```java
package example;

import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.FuzzyQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;

public class EsDocClient {

    public static void main(String[] args) throws IOException {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200)));


        // 模糊查询
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("user");

        // name like '李%'
        SearchSourceBuilder builder = new SearchSourceBuilder();

        FuzzyQueryBuilder fuzzyQueryBuilder =  QueryBuilders.fuzzyQuery("name", "李*").fuzziness(Fuzziness.ONE);

        builder.query(fuzzyQueryBuilder);
        searchRequest.source(builder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = searchResponse.getHits();
        System.out.println("总条数：" + hits.getTotalHits());
        hits.forEach(m -> {
            System.out.println(m.getId());
            System.out.println(m.getSourceAsString());
        });

        client.close();
    }
}
```

#### 2.6.9 高亮查询

```java
package example;

import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;

import java.io.IOException;

public class EsDocClient {

    public static void main(String[] args) throws IOException {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200)));


        // 高亮查询
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("user");

        // 高亮查询
        SearchSourceBuilder builder = new SearchSourceBuilder();

        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<font color='red'>");
        highlightBuilder.postTags("</font>");
        highlightBuilder.field("age");
        builder.highlighter(highlightBuilder);
        builder.query(QueryBuilders.termQuery("name.keyword", "王五"));

        searchRequest.source(builder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = searchResponse.getHits();
        System.out.println("总条数：" + hits.getTotalHits());
        hits.forEach(m -> {
            System.out.println(m.getId());
            System.out.println(m.getSourceAsString());
            System.out.println(m.getHighlightFields());
        });

        client.close();
    }
}
```

#### 2.6.10 聚合查询

```java
package example;

import com.alibaba.fastjson.JSON;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;

public class EsDocClient {

    public static void main(String[] args) throws IOException {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200)));


        // 聚合查询
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("user");

        // 聚合查询
        SearchSourceBuilder builder = new SearchSourceBuilder();
        AggregationBuilder aggregationBuilder = AggregationBuilders.max("maxAge").field("age");
        builder.aggregation(aggregationBuilder);

        searchRequest.source(builder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = searchResponse.getHits();
        System.out.println("maxAge:" + JSON.toJSONString(searchResponse.getAggregations().get("maxAge")));
        System.out.println("总条数：" + hits.getTotalHits());
        hits.forEach(m -> {
            System.out.println(m.getId());
            System.out.println(m.getSourceAsString());
            System.out.println(m.getHighlightFields());
        });

        client.close();
    }
}
```

---

以上。