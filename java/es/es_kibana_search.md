## es 使用 kibana 搜索

---

            环境：
            
            MAC 系统 10.15.6
            Java Version ：1.8.0_202
            Docker 方式安装
            elasticsearch version:7.8.0
            kibana version:7.8.0

---

## 1. 数据准备

假设有原始数据如下

| id  | name    | desc          | price | tags       | date       |
|:---:|---------|---------------|-------|------------|------------|
|  1  | 小米手机    | 手机中的战斗机       | 13999 | 性价比 发烧 不卡  | 2022-07-18 |
|  2  | 小米nfc手机 | nfc手机，手机中的战斗机 | 4999  | 性价比 发烧 公交卡 | 2022-07-18 |
|  3  | nfc手机   | nfc手机，手机中的轰炸机 | 2999  | 性价比 发烧     | 2022-07-18 |
|  4  | 小米耳机    | 耳机中的黄焖鸡       | 999   | 耳机 小米      | 2022-07-18 |
|  5  | 红米耳机    | 耳机中的肯德基       | 399   | 耳机 红米      | 2022-07-18 |

重新创建索引：

```
PUT phone
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
      "desc": {
        "type": "text",
        "analyzer": "ik_max_word",
        "fields": {
          "keyword": {
            "type": "keyword",
            "ignore_above": 256
          }
        }
      },
      "price": {
        "type": "integer"
      },
      "tags": {
        "type": "text",
        "analyzer": "ik_max_word",
        "fields": {
          "keyword": {
            "type": "keyword",
            "ignore_above": 256
          }
        }
      },
      "date": {
        "type": "date"
      }
    }
  }
}
```

然后分别把上面五条数据，按照以下格式插入

```
PUT phone/_doc/1
{
  "name": "小米手机",
  "desc": "手机中的战斗机",
  "price": 13999,
  "tags": [
    "性价比",
    "不卡",
    "发烧"
  ],
  "date": "2022-07-18"
}
```

## 2. 全文检索 match 相关(fulltext search)

### 2.1 query_string (使用并不多)

#### 2.1.1 如果想查询所有数据，不带参数，可以直接通过以下方式进行查询：

```
GET phone/_search
```

根据响应可以发现，会返回全量数据。

#### 2.1.2 如果想带参数进行查询，例如 `name` 被分词后，带 `小米` 的数据：

```
GET phone/_search?q=name:小米
```

原始数据中，`id` 为 1 的 会被分词为 小米、手机。`id` 为 2 的会被分词为 小米、nfc、手机。`id` 为 4 的会被分词为 小米、耳机。

这三条数据，可以被查询到。

#### 2.1.3 也可以进行分页和排序查询：

```
GET phone/_search?from=0&size=10&sort=price:desc
```

会查询出10条数据，并且按照价格倒序。

#### 2.1.4 可以只指定查询的 `value` ,没有 `key`:

```
GET phone/_search?q=2022-07-18
```

查询会对所有创建了索引的 `key` 去匹配 `value`， 是否与查询的 `value` 一致

### 2.2 全文检索 match

#### 2.2.1 查询一个词

如果，我们想查询 `name` 中，分词包含 `小米` 的数据，通过 `match`,可以用下面的方式：

```
GET phone/_search
{
  "query": {
    "match": {
      "name": "小米"
    }
  }
}
```

该查询方式，类似 `sql` 中的

        select * from phone where name like '%小米%'

注意，只是类似，因为 `name` 在这里是查询的分词信息，只有被正确分词的才能被查询到,假如有个 `name` 是 `小米粥`，没有分词成 `小米`，这时候 `es` 是查不到的。

查询结果，会与上面查询 `小米` 结果一致：

```
{
  "took" : 4,
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
    "max_score" : 0.57843524,
    "hits" : [
      {
        "_index" : "phone",
        "_type" : "_doc",
        "_id" : "1",
        "_score" : 0.57843524,
        "_source" : {
          "name" : "小米手机",
          "desc" : "手机中的战斗机",
          "price" : 13999,
          "tags" : [
            "性价比",
            "不卡",
            "发烧"
          ],
          "date" : "2022-07-18"
        }
      },
      {
        "_index" : "phone",
        "_type" : "_doc",
        "_id" : "4",
        "_score" : 0.57843524,
        "_source" : {
          "name" : "小米耳机",
          "desc" : "耳机中的黄焖鸡",
          "price" : 999,
          "tags" : [
            "耳机",
            "小米"
          ],
          "date" : "2022-07-18"
        }
      },
      {
        "_index" : "phone",
        "_type" : "_doc",
        "_id" : "2",
        "_score" : 0.4889865,
        "_source" : {
          "name" : "小米nfc手机",
          "desc" : "nfc手机，手机中的战斗机",
          "price" : 4999,
          "tags" : [
            "性价比",
            "公交卡",
            "发烧"
          ],
          "date" : "2022-07-18"
        }
      }
    ]
  }
}

```

顺便解释一下返回信息：

1. `took` 指的是查询的所用时间,单位为毫秒
2. `timed_out` 指查询时间是否超时，一般不会超时，除非指定超时时间
3. `_shards` 部分告诉我们在查询中参与分片的总数，以及这些分片成功了多少个失败了多少个。如果查询某个分片原始数据和副本都失败，则该分片失败，但是查询结果会把其他分片正常数据返回
4. `hits.total` 指的是查询结果总数是多少，注意这里是查询结果总数，而不是返回的数据量。
5. `hits.hits` 指查询的结果文档
    1. `hits.hits._index` 指查询的索引
    2. `hits.hits._type` 指 `es` 的 `type` ，由于 `7.*` 版本弱化了 `type`，这个值默认为 `_doc`。所以查询文档详情时，默认都是用 `索引/_doc` 去查询
    3. `hits.hits._id` 指的是该文档详情的 `id`
    4. `hits.hits._score` 指每一个查询文档，都会有一个 `_score`字段，它衡量了文档与查询的匹配程度，默认情况下，首先返回最相关的文档结果。
    5. `hits.hits._source` 返回的是文档详情
6. `hits.max_score` 是与查询所匹配文档的 `_score `的最大值

针对第四点，可以特意说明一下，查询时，可以指定查询结果数量，也可以分页查询，就像：

```
GET phone/_search
{
  "query": {
    "match": {
      "name": "小米"
    }
  },
  "from": 0,
  "size": 1
}
```

上面的意思，是说，查询 `name` 分词包含 `小米` 的数据，从 `0` 开始，只查询 `1` 条。

对应 `sql` 类似：

    select * from phone where name like '%小米%' limit 0, 1

`from` 代表偏移量，从 `0` 开始，如果不传，默认为 `0`，`size` 指查询数量。
像上面的查询，正常查询总数是三条。所以 `hits.total` 应该是 `3`，但是限制了数量，所以，只会返回一条结果。

顺便说一下 `es` 查询过程中的`坑`：

1. 查询时，如果没有指定查询数量，默认最多一次只返回 `10` 条数据
2. 如果指定数量的情况下，值很大，例如一次 `100w` 条数据，但是由于 `es` 的限制，一次最多只能返回 `1w` 条数据。
3. `1w` 的限制属于默认配置，可以修改 `es` 的配置来调整，但是一次数据量返回太大，势必会影响性能，所以，最好的做法，是显示指定分页，并控制数量。

#### 2.2.2 查询多个词

`name` 字段，可以同时指定多个词，中间用空格分隔：

```
GET phone/_search
{
  "query": {
    "match": {
      "name": "小米 耳机"
    }
  }
}
```

类似 `mysql`：

        select * from phone where name like '%小米%' or like '%耳机%'

查询结果如下：

```
{
  "took" : 2,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 4,
      "relation" : "eq"
    },
    "max_score" : 1.5179627,
    "hits" : [
      {
        "_index" : "phone",
        "_type" : "_doc",
        "_id" : "4",
        "_score" : 1.5179627,
        "_source" : {
          "name" : "小米耳机",
          "desc" : "耳机中的黄焖鸡",
          "price" : 999,
          "tags" : [
            "耳机",
            "小米"
          ],
          "date" : "2022-07-18"
        }
      },
      {
        "_index" : "phone",
        "_type" : "_doc",
        "_id" : "5",
        "_score" : 0.79423964,
        "_source" : {
          "name" : "红米耳机",
          "desc" : "耳机中的肯德基 ",
          "price" : 399,
          "tags" : [
            "耳机",
            "红米"
          ],
          "date" : "2022-07-18"
        }
      },
      {
        "_index" : "phone",
        "_type" : "_doc",
        "_id" : "1",
        "_score" : 0.57843524,
        "_source" : {
          "name" : "小米手机",
          "desc" : "手机中的战斗机",
          "price" : 13999,
          "tags" : [
            "性价比",
            "不卡",
            "发烧"
          ],
          "date" : "2022-07-18"
        }
      },
      {
        "_index" : "phone",
        "_type" : "_doc",
        "_id" : "2",
        "_score" : 0.4889865,
        "_source" : {
          "name" : "小米nfc手机",
          "desc" : "nfc手机，手机中的战斗机",
          "price" : 4999,
          "tags" : [
            "性价比",
            "公交卡",
            "发烧"
          ],
          "date" : "2022-07-18"
        }
      }
    ]
  }
}

```

这种查询方式，相当于分别查询， `name` 包含 `小米` 或 `手机` 的数据。并根据 `_score` 相关性进行结果排序。

#### 2.2.3 限制查询返回字段

如果我们想限制查询返回的字段，不想全量返回，可以通过 `_source` 字段来处理：

```
GET phone/_search
{
  "_source": ["name", "price"], 
  "query": {
    "match": {
      "name": "小米"
    }
  }
}
```

类似 `mysql`：

        select name, price from phone where name like '%小米%'

**如果只想返回 id, 可以设置： "_source":false**

### 2.3 短语搜索 match_phrase

如果我们想搜索 `小米` 、`耳机`，并且希望 **这两个被分词后，是紧挨着**。

例如 `小米耳机` 分词后应该是 `小米`、`耳机`。但是 `小米蓝牙耳机` 分词后应该是 `小米`、`蓝牙`、`耳机`。

如果是之前的 `match` 查询多个词的方式，两种都会被查到。如果想只查询前者，可以使用 `match_phrase`：

```
GET phone/_search
{
  "query": {
    "match_phrase": {
      "name": "小米 耳机"
    }
  }
}
```

### 2.4 在多个字段中查询 multi_match

如果我们有个需求，想查询 【`name` 中包含 `小米` 或 `耳机` 的数据】 + 【`desc` 中包含 `小米` 或 `耳机` 的数据】，可以使用该方法：

```
GET phone/_search
{
  "query": {
    "multi_match": {
      "query": "小米 耳机",
      "fields": ["name", "desc"]
    }
  }
}
```

### 2.5 全文检索 match_all

默认没有任何过滤条件，相当于在该索引下全文检索。

```
GET phone/_search
{
  "query": {
    "match_all": {}
  }
}
```

## 3. 精准匹配 term 相关(exact match)

### 3.1 term 精准匹配

`term` 是匹配和搜索词完全相同的结果，所以 `term` 搜索的数据是不分词的。

例如，使用以下方式查询 `小米手机`：

```
GET phone/_search
{
  "query": {
    "term": {
      "name": {
        "value": "小米手机"
      }
    }
  }
}
```

类似 `mysql` 中(注意这里的 `name` 是分词之后的结果)：

      select * from phone where name = '小米手机'

会发现查询结果为空，与 `match` 结果不一致，是因为 `name` 字段已经被分词，没有 `小米手机` 的相关数据了。

如果使用 `小米` 搜索， 分词后，包含 `小米`的数据，就被查到，应该是 id : 1、2、4：

```
GET phone/_search
{
  "query": {
    "term": {
      "name": {
        "value": "小米"
      }
    }
  }
}
```

如果我们想使用 `term` 查询 `小米手机` 的话，可以看一下上面创建索引的时候， `_mapping` 指定的 `name.keyword` 。`keyword` 是不会被分词的，所以，可以通过 `keyword` 查询：

```
GET phone/_search
{
  "query": {
    "term": {
      "name.keyword": {
        "value": "小米手机"
      }
    }
  }
}
```

这里可以看到，只会有 `id` 为 `1` 的数据被查询到：

```
{
  "took" : 0,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 1,
      "relation" : "eq"
    },
    "max_score" : 1.3862942,
    "hits" : [
      {
        "_index" : "phone",
        "_type" : "_doc",
        "_id" : "1",
        "_score" : 1.3862942,
        "_source" : {
          "name" : "小米手机",
          "desc" : "手机中的战斗机",
          "price" : 13999,
          "tags" : [
            "性价比",
            "不卡",
            "发烧"
          ],
          "date" : "2022-07-18"
        }
      }
    ]
  }
}

```

### 3.3 terms 精准匹配

如果想查询多个精确值，可以使用 `terms`：

```
GET phone/_search
{
  "query": {
    "terms": {
      "tags": [
        "性价比",
        "公交卡"
      ]
    }
  }
}
```

类似于 `mysql`：

      select * from phone where tags = '性价比' or tags = '公交卡'

这样 ， `id` 是 `1`、`2`、`3` 的数据将被查询到。

### 3.3 范围查询 range 相关

如果我们想查询 500 ~ 1000(闭区间) 价格范围内的数据信息，可以使用范围查询：

```
GET phone/_search
{
  "query": {
    "range": {
      "price": {
        "gt": 500,
        "lte": 1000
      }
    }
  }
}
```

也可以根据日期进行查询(指定时区)：

```
GET phone/_search
{
  "query": {
    "range": {
      "date": {
        "time_zone": "+08:00", 
        "gte": "2022-07-18",
        "lte": "2022-07-18"
      }
    }
  }
}
```

## 4. 过滤器 filter

使用过滤器，查询 `name` 是 `小米` 的数据：

```
GET phone/_search
{
  "query": {
    "constant_score": {
      "filter": {
        "match": {
          "name": "小米"
        }
      }
    }
  }
}
```

查询结果与使用 `match` 查询结果基本无区别，但是有一些不一样的地方：

filter 不需要计算相关性算分，不需要按照相关分数进行排序，同时还有内置的自动 cache 最常使用的 filter 的数据。
而 query 相反，需要计算相关性算分，按照分数进行排序，而且无法 cache 结果。
因此在某些不需要相关性算分的查询场景，尽量使用 Filter Context 来让查询更加高效。

## 5. 组合查询 bool query

组合查询，可以多种查询条件嵌套，这里只是举最简单的例子。

### 5.1 must 查询

相当于 `mysql` 中的 `and`

如果我们想查询， `name` 分词后，既包含 `小米`,又包含 `耳机` 的数据，可以使用以下方式：

```
GET phone/_search
{
  "_source": false, 
  "query": {
    "bool": {
      "must": [
        {
          "match": {
            "name": "小米"
          }
        },
        {
          "match": {
            "name": "耳机"
          }
        }
      ]
    }
  }
}
```

类似 `mysql`:

      select id from phone where name = '小米' and name = '耳机'

查询结果中，只有 `id` 为 `4` 的符合。

### 5.2 filter 查询

`filter` 可以放在 `bool` 查询方式中：

```
GET phone/_search
{
  "_source": false,
  "query": {
    "bool": {
      "filter": [
        {
          "term": {
            "name": "小米"
          }
        },
        {
          "term": {
            "name": "耳机"
          }
        }
      ]
    }
  }
}
```

与直接使用 `must` 查询结果一致，不同的是，`filter` 不会计算评分。

### 5.3 must_not 查询

相当于 `mysql` 中的 `not`

如果想排除一些数据，可以使用 `must_not`，例如下面的方式：

```
GET phone/_search
{
  "_source": false,
  "query": {
    "bool": {
      "must_not": [
        {
          "match": {
            "name": "小米"
          }
        },
        {
          "match": {
            "name": "耳机"
          }
        }
      ]
    }
  }
}
```

`name` 分词后，所有带 `小米` 和 `耳机` 的数据都会被排除，所以只剩下 `id` 为 `3` 的数据。

### 5.4 should 查询

相当于 `mysql` 中的 `or`

如果想查询 `name.keyword` 为 `小米手机` 或者价格是 `399` 的数据：

```
GET phone/_search
{
  "_source": false,
  "query": {
    "bool": {
      "should": [
        {
          "term": {
            "name.keyword": "小米手机"
          }
        },
        {
          "term": {
            "price": {
              "value": 399
            }
          }
        }
      ]
    }
  }
}
```

符合条件的，只有 `id` 为 `1` 和 `5`。

## 6. 模糊查询

### 6.1 前缀搜索 prefix

注意：

1. 前缀搜索，匹配的是 `term` ，匹配的数据，是分词之后的数据，不是原数据
2. 前缀搜索的性能很差
3. 前缀搜索没有缓存
4. 前缀搜索，尽可能把前缀长度设置更长

查询方式如下(注意：因为查询使用的 desc.keyword ，实际没有分词)：

```
GET phone/_search
{
  "query": {
    "prefix": {
      "desc.keyword": {
        "value": "手机"
      }
    }
  }
}
```

类似 `mysql`:

      select * from  phone where desc like '手机%'

只有第一条数据，符合查询结果

### 6.2 通配符 wildcard

查询方式：

```
GET phone/_search
{
  "query": {
    "wildcard": {
      "desc": {
        "value": "战*机"
      }
    }
  }
}
```

这样会把 `战斗机` 相关的内容查询出来。

### 6.3 正则表达式 regexp

查询方式如下，可以直接使用正则表达式：

```
GET phone/_search
{
  "query": {
    "regexp": {
      "name.keyword": ".*手机.*"
    }
  }
}
```

### 6.4 模糊查询 fuzzy

模糊查询，指的是，如果我查询 `apple`,但是输入错误，输入的是 `applo`。查询的时候，可以帮我自动纠正

通常有以下情形：

1. 混淆字符 (apple --> applo)
2. 缺少字符 (apple --> appl)
3. 多出字符 (apple --> applee)
4. 颠倒次序 (apple --> appel)

`fuzziness` 指的是错误偏差：

1. 如果是 `0`，相当于精准搜索
2. 如果是 `1` ，指可以差一位
3. 如果是 `2` ，指可以差两位
4. 如果是 `AUTO` ，指 `es` 根据当前的 `value`，自动判定 (默认是 `AUTO`)

```
GET phone/_search
{
  "query": {
    "fuzzy": {
      "desc": {
        "value": "战斗鸡",
        "fuzziness": 1 
      }
    }
  }
}
```

### 6.5 短语前缀 match_phrase_prefix

查询方式如下：

```
GET phone/_search
{
  "query": {
    "match_phrase_prefix": {
      "name": "小米nfc"
    }
  }
}
```

查询结果为第二条数据 `小米nfc手机`

`name` 分词后的顺序，与原始数据的分词顺序一致，类似 `match_phrase`。但是运行对文本的最后一个词 (term) 前缀匹配。

例如有个短语是 `小米nfc手机`。被分词后应该是 `小米`、`nfc`、`手机`三个词。

1. 如果是使用 `match_phrase`，可以通过 `小米 nfc` 查询到该短语。但是如果是 `小米 n`，分词后的结果是 `小米`、`n`。对短语并不匹配，查询不到。

2. 如果是使用 `match_phrase_prefix`， 使用 `小米 nfc` 或 `小米nfc` 时，都可以查询到短语，不同的地方在于，如果是使用 `小米 n`。分词后的结果是 `小米`、`n`。
   `n` 会进行前缀匹配，可以匹配到 `nfc` ，并且与短语的分词顺序一致，所以，可以查询到该短语

既也可以通过下面的方式查询到结果：

```
GET phone/_search
{
  "query": {
    "match_phrase_prefix": {
      "name": "小米n"
    }
  }
}

```

## 7. 聚合查询(aggregations)


### 7.1 分桶聚合 (bucket aggregations)

例如，我们想查询有多少个不同的 `tag`。可以给 `tag` 进行分组(查询结果按照数量从小到大排序，取 10 条)：

```
GET phone/_search
{
  "size": 0, 
  "aggs": {
    "aggs_tag": {
      "terms": {
        "field": "tags.keyword",
        "size": 10,
        "order": {
          "_count": "asc"
        }
      }
    }
  }
}
```


返回信息如下：

主要查看  `aggregations` 下返回的信息，可以看到 `tag` 分组，以及对应的数据量

```
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
      "value" : 5,
      "relation" : "eq"
    },
    "max_score" : null,
    "hits" : [ ]
  },
  "aggregations" : {
    "aggs_tag" : {
      "doc_count_error_upper_bound" : 0,
      "sum_other_doc_count" : 0,
      "buckets" : [
        {
          "key" : "不卡",
          "doc_count" : 1
        },
        {
          "key" : "公交卡",
          "doc_count" : 1
        },
        {
          "key" : "小米",
          "doc_count" : 1
        },
        {
          "key" : "红米",
          "doc_count" : 1
        },
        {
          "key" : "耳机",
          "doc_count" : 2
        },
        {
          "key" : "发烧",
          "doc_count" : 3
        },
        {
          "key" : "性价比",
          "doc_count" : 3
        }
      ]
    }
  }
}


```



### 7.2 指标聚合 (metrics aggregations)


可以按照某个指标进行聚合，例如查询最贵的价格：
```
GET phone/_search
{
  "size": 0,
  "aggs": {
    "aggs_max": {
      "max": {
        "field": "price"
      }
    }
  }
}
```


商品均价：
```
GET phone/_search
{
  "size": 0,
  "aggs": {
    "aggs_avg": {
      "avg": {
        "field": "price"
      }
    }
  }
}
```


也可以一次性查询所有指标(最大值、最小值、总数、平均值、总和)：

```
GET phone/_search
{
  "size": 0,
  "aggs": {
    "price_stats": {
      "stats": {
        "field": "price"
      }
    }
  }
}
```

返回如下：

```
{
  "took" : 4,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 5,
      "relation" : "eq"
    },
    "max_score" : null,
    "hits" : [ ]
  },
  "aggregations" : {
    "price_stats" : {
      "count" : 5,
      "min" : 399.0,
      "max" : 13999.0,
      "avg" : 4679.0,
      "sum" : 23395.0
    }
  }
}

```



价格去重后，查询数量：

```
GET phone/_search
{
  "size": 0,
  "aggs": {
    "price_distinct": {
      "cardinality": {
        "field": "price"
      }
    }
  }
}
```

### 7.3 管道聚合 (pipeline aggregations)

对数据可以做多次聚合处理。例如，想知道不同 `tag` 下，每个 `tag` 最高价是多少，可以通过下面的方式：

```
GET phone/_search
{
  "size": 0, 
  "aggs": {
    "aggs_tag": {
      "terms": {
        "field": "tags.keyword",
        "size": 10
      },
      "aggs": {
        "max_price": {
          "max": {
            "field": "price"
          }
        }
      }
    }
  }
}
```



附录：

[es 7.8 官方 api](https://www.elastic.co/guide/en/elasticsearch/reference/7.8/indices.html?baymax=rec&rogue=rec-1&elektra=guide)


---

以上。
