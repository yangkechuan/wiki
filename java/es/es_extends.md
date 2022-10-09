## es 补充部分

---

            环境：
            
            MAC 系统 10.15.6
            Java Version ：1.8.0_202
            Docker 方式安装
            elasticsearch version:7.8.0
            kibana version:7.8.0

---

主要记录，在使用过程中不断发现的问题，以及更新之前的知识点

- max_result_window 官方链接：[index.max_result_window](https://www.elastic.co/guide/en/elasticsearch/reference/7.8/index-modules.html#index-max-result-window)
- scroll api 官方链接：[https://www.elastic.co/guide/en/elasticsearch/reference/7.8/paginate-search-results.html#scroll-search-results](https://www.elastic.co/guide/en/elasticsearch/reference/7.8/paginate-search-results.html#scroll-search-results)
- search after 官方链接：[https://www.elastic.co/guide/en/elasticsearch/reference/7.8/paginate-search-results.html#search-after](https://www.elastic.co/guide/en/elasticsearch/reference/7.8/paginate-search-results.html#search-after)


### 1. 查询 10000 条限制问题


#### 1.1 问题说明

之前提到，`es` 查询过程中，会有 `1w` 的限制问题。这个可以从官方文档 [index.max_result_window](https://www.elastic.co/guide/en/elasticsearch/reference/7.8/index-modules.html#index-max-result-window)
中查看到。

文档说明主要如下：

    index.max_result_window

    The maximum value of from + size for searches to this index. 
    Defaults to 10000. Search requests take heap memory and time proportional to from + size and this limits that memory. 
    See Scroll or Search After for a more efficient alternative to raising this.
    

简单来说，如果想一次性查询所有用户，但是用户信息大于 `1w` ，一次性最多只能查回 `1w` 条数据。而且，即使使用分页的话(from + size) ，也会遇到同样问题。

例如: 
    
        GET user/_search
        {
            "query": {
                "match_all": {}
            },
            "from": 0,
            "size": 10001
        }

这样，只能查询回 `1w` 条数据 (注意：totalHits 只会返回 `1w` 条，查询详情也是 `1w` 条)。

也许有人会说，如果每次查询一部分，例如 `1000` 条，然后不断分页，是不是就不会有这个问题。**其实不是的，问题依然存在。**



修改上面的分页条件，如果改成从第 `10000` 条开始查询，只查询 `1` 条数据：


        GET user/_search
        {
            "query": {
                "match_all": {}
            },
            "from": 10000,
            "size": 1
        }


然后，会发现查询错误：

```json
{
  "error" : {
    "root_cause" : [
      {
        "type" : "illegal_argument_exception",
        "reason" : "Result window is too large, from + size must be less than or equal to: [10000] but was [10001]. See the scroll api for a more efficient way to request large data sets. This limit can be set by changing the [index.max_result_window] index level setting."
      }
    ],
    "type" : "search_phase_execution_exception",
    "reason" : "all shards failed",
    "phase" : "query",
    "grouped" : true,
    "failed_shards" : [
      {
        "shard" : 0,
        "index" : "user",
        "node" : "kQa71HMkRi6QfrfwtrKIvw",
        "reason" : {
          "type" : "illegal_argument_exception",
          "reason" : "Result window is too large, from + size must be less than or equal to: [10000] but was [10001]. See the scroll api for a more efficient way to request large data sets. This limit can be set by changing the [index.max_result_window] index level setting."
        }
      }
    ],
    "caused_by" : {
      "type" : "illegal_argument_exception",
      "reason" : "Result window is too large, from + size must be less than or equal to: [10000] but was [10001]. See the scroll api for a more efficient way to request large data sets. This limit can be set by changing the [index.max_result_window] index level setting.",
      "caused_by" : {
        "type" : "illegal_argument_exception",
        "reason" : "Result window is too large, from + size must be less than or equal to: [10000] but was [10001]. See the scroll api for a more efficient way to request large data sets. This limit can be set by changing the [index.max_result_window] index level setting."
      }
    }
  },
  "status" : 400
}

```



通过返回的错误信息，可以知道 `from + size` 的总数大于 `10000` 时，就会有问题。

虽然我查询只查询了 `1` 条数据，但是对于 `es` 来说，实际需要从每个分片中，查询出 `10001` 条数据，然后合并数据，并舍弃多余的数据，最后只取 `1` 条数据。
这些计算都是在内存中运算的。也就是说，一次性在内存中的运算数据，不能超过 `10000`， 而不是只针对查询结果的限制。

这一点，类似于 `mysql` 的 `limit` 查询:
    
    select * from user limit 10000, 1;


同样的问题，虽然查询结果只需要一条数据，但是需要先查询出 `10001` 条数据，然后舍弃之前的多余数据。

所以， `es` 在分页查询中，会遇到两个问题：

- 查询最多只有 `10000` 条
- 查询后面的页数时，会对性能有影响。

这种分页查询的场景，一般用在百度首页查询，或者某个品类的商品查询，会对分页数量有限制。


#### 1.2 解决方案

然后，如何解决这个限制。

##### 1.2.1 第一种方案： 调大index.max_result_window(不推荐)

这种方案会影响性能，并可能导致 OOM，并且，没有从根本解决问题。

如果是已有的索引，使用以下方式更新：

        PUT _all/_settings
        {
            "index.max_result_window":20000
        }


如果是新建索引，可以配置 settings:


```json
{
  "mappings": {
    "properties": {
      "name": {
        "type": "text",
        "analyzer": "ik_max_word",
        "fields": {
          "keyword": {
            "type": "keyword",
            "ignore_above": 256
          }
        }
      },
      "age": {
        "type": "integer"
      },
      "desc": {
        "type": "text",
        "analyzer": "ik_max_word"
      },
      "type": {
        "type": "keyword"
      }
    }
  },
  "settings": {
    "index": {
      "max_result_window": 20000
    }
  }
}
```


这时候，再查询，会发现，查询结果最多还是只有 10000 条，需要在查询时，新增一个条件：

    GET user/_search
    {
        "query": {
            "match_all": {}
        },
        "from": 10000,
        "size": 1,
        "track_total_hits": true
    }


`"track_total_hits": true` 默认为 `false`。表示禁用跟踪匹配查询的总点击次数。


##### 1.2.2 第二种方案：使用 scroll api


分页查询，就像翻书一样，每次翻一页, 也可以到任意指定的页码。但是滚动查询，就像微信朋友圈翻页一样，可以一直往下翻，但是不能跳转到某一页。

`scroll` 方式，实际是对当前数据生成一个快照。会隔绝外来的新数据。新数据的增删，不会改变本次快照内容


如果我们想使用滚动查询的方式，查询出所有的用户数据，可以使用以下 `api`:

```java
package example;

import org.apache.http.HttpHost;
import org.elasticsearch.action.search.*;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;

public class EsDocClient {

    public static void main(String[] args) throws IOException {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200)));


        // 聚合查询
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("user");

        SearchSourceBuilder builder = new SearchSourceBuilder();
        // 设置一次最多查询 10000 条，从第 10001 条开始，启动滚动查询
        builder.size(10000);

        // 滚动查询，设置查询超时时间
        Scroll scroll = new Scroll(TimeValue.MINUS_ONE);
        searchRequest.source(builder);
        searchRequest.scroll(scroll);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = searchResponse.getHits();

        // TODO 使用查询结果

        // 需要每次记录滚动 ID
        String scrollId = searchResponse.getScrollId();

        while (hits != null && hits.getHits().length > 0){
            // 构造滚动查询
            SearchScrollRequest searchScrollRequest = new SearchScrollRequest(scrollId);
            searchScrollRequest.scroll(scroll);

            // 响应必须是上面的响应对象，需要对上一层进行覆盖
            searchResponse = client.scroll(searchScrollRequest, RequestOptions.DEFAULT);

            // TODO 使用滚动的查询结果
            scrollId = searchResponse.getScrollId();
            hits = searchResponse.getHits();
        }


        // 清除滚动，否则影响下次查询
        ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
        clearScrollRequest.addScrollId(scrollId);
        ClearScrollResponse clearScrollResponse = client.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
        // 判定清除滚动是否成功
        System.out.println(clearScrollResponse.isSucceeded());
        client.close();
    }
}
```

也可以在 `kibana` 操作, 生成快照，快照 scroll_id 存活时间为 1 分钟：


    GET user/_search?scroll=1m
    {
        "query": {
            "match_all": {}
        }
    }


在响应中，会返回 `_scroll_id` 字段。

```json
{
  "_scroll_id" : "FGluY2x1ZGVfY29udGV4dF91dWlkDXF1ZXJ5QW5kRmV0Y2gBFFRGVkt2WU1CeVdjRkFTY0piaWtHAAAAAAAADPEWa1FhNzFITWtSaTZRZnJmd3RyS0l2dw==",
  "took" : 1,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 0,
      "relation" : "eq"
    },
    "max_score" : null,
    "hits" : [ ]
  }
}

```


然后在后续的查询中，传入上一次的 `scroll_id` 即可：


    GET _search/scroll
    {
        "scroll": "1m",
        "scroll_id": "FGluY2x1ZGVfY29udGV4dF91dWlkDXF1ZXJ5QW5kRmV0Y2gBFFRGVkt2WU1CeVdjRkFTY0piaWtHAAAAAAAADPEWa1FhNzFITWtSaTZRZnJmd3RyS0l2dw=="
    }


这种查询，会很耗费资源，即使有过期时间，查询量大时，对资源也有影响。所以，在 `kibana` 查询完成后，最好也主动删除一下快照数据：


    DELETE _search/scroll/FGluY2x1ZGVfY29udGV4dF91dWlkDXF1ZXJ5QW5kRmV0Y2gBFHBsVk92WU1CeVdjRkFTY0pJeW45AAAAAAAADUsWa1FhNzFITWtSaTZRZnJmd3RyS0l2dw==


**注意**:

- 这里的查询，并不用指定具体是哪个 `index`
- 每次查询，都会生成一个新的 `scroll_id`。所以，每次查询都需要替换成上一次查询结果中的 `scroll_id`。


##### 1.2.3 第三种方案：使用 search after


    scroll 的方式，官方的建议不用于实时的请求（一般用于数据导出），因为每一个 scroll_id 不仅会占用大量的资源，而且会生成历史快照，对于数据的变更不会反映到快照上。
    search_after 分页的方式是根据上一页的最后一条数据来确定下一页的位置，同时在分页请求的过程中，如果有索引数据的增删改查，这些变更也会实时的反映到游标上。
    但是需要注意，因为每一页的数据依赖于上一页最后一条数据，所以无法跳页请求。
    为了找到每一页最后一条数据，每个文档必须有一个全局唯一值，官方推荐使用 _uid 作为全局唯一值，其实使用业务层的 id 也可以。




假如有索引 `user`，分别插入三条数据：


    PUT user/_doc/1
    {
        "id":1,
        "name":"张1",
        "age":1
    }


    PUT user/_doc/2
    {
        "id":2,
        "name":"张2",
        "age":2
    }

    PUT user/_doc/3
    {
        "id":3,
        "name":"张3",
        "age":3
    }



然后，我们在查询时，加上一个排序方式，例如按照业务 id 排序：


    GET user/_search
    {
        "query": {
            "match_all": {}
        },
        "sort": [
            {
                "id": {
                    "order": "asc"
                }
            }
        ]
    }


查询结果，与之前相比，返回值中，也会增加 `sort` 字段：

```json
{
  "took" : 3,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 3,
      "relation" : "eq"
    },
    "max_score" : null,
    "hits" : [
      {
        "_index" : "user",
        "_type" : "_doc",
        "_id" : "1",
        "_score" : null,
        "_source" : {
          "id" : 1,
          "name" : "张1",
          "age" : 1
        },
        "sort" : [
          1
        ]
      },
      {
        "_index" : "user",
        "_type" : "_doc",
        "_id" : "2",
        "_score" : null,
        "_source" : {
          "id" : 2,
          "name" : "张2",
          "age" : 2
        },
        "sort" : [
          2
        ]
      },
      {
        "_index" : "user",
        "_type" : "_doc",
        "_id" : "3",
        "_score" : null,
        "_source" : {
          "id" : 3,
          "name" : "张3",
          "age" : 3
        },
        "sort" : [
          3
        ]
      }
    ]
  }
}

```


然后我们在查询时，新增一个 `search_after` 字段：


    GET user/_search
    {
        "query": {
            "match_all": {}
        },
        "sort": [
            {
                "id": {
                    "order": "asc"
                }
            }
        ],
        "search_after":[2]
    }


这样，就只会把 id > 2 后面的数据查询出来。


**注意**：

- 使用 `search_after` 必须要设置 `from=0`

---

以上。
