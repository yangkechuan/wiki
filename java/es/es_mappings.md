## es 配置 mappings

---

            环境：
            
            MAC 系统 10.15.6
            Java Version ：1.8.0_202
            Docker 方式安装
            elasticsearch version:7.8.0
            kibana version:7.8.0

---

## 1. mappings 介绍

`es` 中的 `mappings` 相当于 `mysql` 的表结构。就像：

```sql
create table `user`
(
    id   int auto_increment primary key,
    name varchar(255) null,
    age  int null
);

```

`es` 插入数据时，可以不指定 `mappings` ，则 `es` 会根据插入的数据类型动态映射或者自动映射(`dynamic mapping`)。

当然，也可以在创建索引时，手动或显示(`explicit mapping`)指定 `mappings`。

想要查看 `user` 索引结构，可以通过以下接口查询当前索引。(由于版本问题， `_mappings` 和 `_mapping` 同样生效，但是建议使用后者，为新版本语法)

```
GET user/_mapping
{}
```

### 1.1 mappings 常用类型

- 数字类型(`number`):

  一般常用：`long`、`integer`、`short`、`double`、`float` 等类型。


- 字符串类型(`string`):

  一般常用: `text`、 `keyword`。

  **注意： `text` 类型是会被分词的，例如使用 `ik` 分词器，就是给 `text` 类型做分词。
  `keyword` 类型是不会被分词的，只能精准查询，例如查询 `id` 、查询手机号都可以设置为 `keyword` 。`keyword` 可以用来做排序、过滤、聚合**

  `text` 类型会创建倒排索引，作用是为了做文本检索。

  `keyword` 类型会创建正排索引，作用是为了做排序和聚合。

- 日期类型(`date`)

  存储时间格式，可以作为范围查询，并可以指定存储格式(`format`)


- 对象类型(`object`)

  一般作用于嵌套 `json`，例如 `user` 文档有一个 `detail` 字段，对应的值可能是一个 `json`。

  查询方式：`user.detail`


- 数组(`nested`)

  存储数组类型，例如：`{"group": "fans","user": [{"first": "John","last": "Smith"}, {"first": "Alice","last": "White"}]}`。


- 布尔类型(`boolean`)

  指 `true` 、`false` 类型

### 1.2 自动映射

如果不手动创建映射，则 `es` 自动映射的规则如下：

        整数               --->    long
        浮点数             --->    float
        true | false      --->    boolean
        日期               --->    date
        数组               --->    取决于数组的第一个有效值
        对象               --->    object
        字符串             --->    如果不是数字和日期，类型，那么会被映射成 text 和 keyword
 

        除上述字段类型，其他类型必须显示映射，不然 es 无法自动识别

### 1.3 手动映射

假设我们需要创建一个 `phone` 索引，具体需要以下内容：

1. `name` 字段，手机名称，字符串类型，需要分词
2. `desc` 字段，手机描述，字符串类型，需要分词
3. `count` 字段，手机存货，整型
4. `price` 字段，手机价格，整型
5. `tags` 字段，手机关键词，例如 性价比、旗舰机等，字符串类型，需要分词
6. `parts` 字段，手机电源情况，对象类型，包含以下内容：
    1. `name` 字段，电源名称
    2. `desc` 字段，电源型号
7. `partlist` 字段，手机赠品，例如耳机、电源、充电线等。数组对象类型，包含以下内容：
    1. `name` 字段，赠品名称
    2. `desc` 字段，赠品型号
8. `date` 字段，手机上架日期，时间类型
9. `isdel` 字段，手机是否被下架删除

通过以上信息，可以创建 `mappings`:

**同时，需要注意以下几点：**

- `es` 需要先配置好 `ik` 分词器，才能支持 `ik_max_word` 分词。默认的 `standard` 分词器，只会把中文一个一个字分开。默认分词器，拆分英文，是根据空格拆分。
- 以 `name` 字段为例，`name` 信息可以为 `text` 类型，被 `ik_max_word` 方式分词。并且，也可以同时作为 `keyword` ，当做关键字精准查询
    - 查询时，如果想通过 `keyword` 方式查询，需要用 `name.keyword` 方式指定。
- `ignore_above` 指的是，如果作为关键字时， `es` 只会把多长范围内的数据当做关键字去查询。例如通过百度或者谷歌查询信息时，如果输入查询文本过长，后面的内容会被忽略掉
- 索引创建完成之后就不可再修改字段类型
    - 例如 `count` 为 `integer`，是不可以修改为 `text`的。只可以创建新的索引和 `mappings`，然后再把原始数据移过去
    - 如果想新增 `mappings` 中不存在的字段，直接插入新的字段是可以的

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
      "count": {
        "type": "integer"
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
      "parts": {
        "type": "object",
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
          }
        }
      },
      "partlist": {
        "type": "nested",
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
          }
        }
      },
      "date": {
        "type": "date"
      },
      "isdel": {
        "type": "boolean"
      }
    }
  }
}
```

如果返回以下内容，说明创建索引成功：

```
{
  "acknowledged" : true,
  "shards_acknowledged" : true,
  "index" : "phone"
}

```

然后，根据创建的索引，往里面插入第一条数据：

```
PUT phone/_doc/1
{
  "name": "小米手机",
  "desc": "手机中的战斗机",
  "count": 9999,
  "price": 4399,
  "tags": [
    "性价比",
    "不卡",
    "发烧"
  ],
  "parts": {
    "name": "电源",
    "desc": "5V 2A"
  },
  "partlist": [
    {
      "name": "电源",
      "desc": "5V 2A"
    },
    {
      "name": "耳机",
      "desc": "小米耳机"
    },
    {
      "name": "电源线",
      "desc": "1.5米"
    }
  ],
  "date": "2022-07-17",
  "isdel": false
}
```

插入成功后，可以查询一下：

```
GET phone/_doc/1
{}
```

返回数据：

```
{
  "_index" : "phone",
  "_type" : "_doc",
  "_id" : "1",
  "_version" : 3,
  "_seq_no" : 2,
  "_primary_term" : 1,
  "found" : true,
  "_source" : {
    "name" : "小米手机",
    "desc" : "手机中的战斗机",
    "count" : 9999,
    "price" : 4399,
    "tags" : [
      "性价比",
      "不卡",
      "发烧"
    ],
    "parts" : {
      "name" : "电源",
      "desc" : "5V 2A"
    },
    "partlist" : [
      {
        "name" : "电源",
        "desc" : "5V 2A"
      },
      {
        "name" : "耳机",
        "desc" : "小米耳机"
      },
      {
        "name" : "电源线",
        "desc" : "1.5米"
      }
    ],
    "date" : "2022-07-17",
    "isdel" : false
  }
}

```

可以看到，查询结果与创建信息一致。


---

以上。