## 安装 es， postman 请求 es

---


            环境：
            
            MAC 系统 10.15.6
            Docker 方式安装
            elasticsearch version:7.8.0
   

### 0.基本信息

- 参考视频：https://www.bilibili.com/video/BV1hh411D7sb?p=1
- 参考视频2：https://www.bilibili.com/video/BV1LY4y167n5
- 官网 ES 下载地址：https://www.elastic.co/cn/downloads/elasticsearch
- ES github 地址：https://github.com/elastic/elasticsearch
- docker 基本使用学习地址：https://github.com/yeasy/docker_practice
- docker 下载地址：https://hub.docker.com/
- docker 镜像下载地址：https://hub.docker.com/_/elasticsearch?tab=tags&page=1&ordering=last_updated
- 阿里云加速器：https://cr.console.aliyun.com/cn-hangzhou/instances/mirrors



### 1.下载

安装和启动 docker 过程这里不讲述，可以直接通过 [学习入口](https://github.com/yeasy/docker_practice) 学习 docker 基本使用。

执行以下命令，安装指定版本镜像
```shell script
docker pull elasticsearch:7.8.0
```

等下载完成之后，就可以创建并启动 es 容器。

这里需要注意，因为使用的7.X 版本(低版本不一定需要)：

    默认需要更改配置：
    1. Es  自带 java，需要修改启动参数
    2. Es 单节点启动需要配置
    
### 2.启动

查看本地 es 镜像：
    
    ➜  ~ docker images
    REPOSITORY                 TAG       IMAGE ID       CREATED         SIZE
    elasticsearch              7.8.0     121454ddad72   20 months ago   810MB


记录 IMAGE ID ，然后执行命令：

```shell script
docker run -itd -p 9200:9200 -e ES_JAVA_POTS="-Xms256m -Xmx256m" -e "discovery.type=single-node"  --name es 121454ddad72
```


容器启动后，如果没有报错，请求地址：http://localhost:9200/

可以看到类似下面返回，说明启动成功。

    {
      "name": "5f33ce1fb29b",
      "cluster_name": "docker-cluster",
      "cluster_uuid": "sOOgIeTwQPaVmGPUUfSYSQ",
      "version": {
        "number": "7.8.0",
        "build_flavor": "default",
        "build_type": "docker",
        "build_hash": "757314695644ea9a1dc2fecd26d1a43856725e65",
        "build_date": "2020-06-14T19:35:50.234439Z",
        "build_snapshot": false,
        "lucene_version": "8.5.1",
        "minimum_wire_compatibility_version": "6.8.0",
        "minimum_index_compatibility_version": "6.0.0-beta1"
      },
      "tagline": "You Know, for Search"
    }
    

es 和 mysql 的概念对比：

    ES ：         Index(索引)           Type(类型)        Document(文档)            Fields(字段)
    
    Mysql  ：     DataBase(数据库)       Table(表)        Row(行)                  Cloumn(字段)
    
    
**ES 7.X 之后，去除 Type 概念。**


### 3. postman 请求 es


#### 3.1 索引


es 请求遵循 Restful 风格，以下请求，都是在 postman 下完成。


创建索引(索引名称是 `shopping`)：

    PUT
    
    http://localhost:9200/shopping
    
    Response:
    {
        "acknowledged": true,
        "shards_acknowledged": true,
        "index": "shopping"
    }
    
查询索引：

    GET
    
    http://localhost:9200/shopping
    
    Response:
    {
        "shopping": {
            "aliases": {},
            "mappings": {},
            "settings": {
                "index": {
                    "creation_date": "1644148837613",
                    "number_of_shards": "1",
                    "number_of_replicas": "1",
                    "uuid": "kcyt1LhkRfy-KkcT27-tmg",
                    "version": {
                        "created": "7080099"
                    },
                    "provided_name": "shopping"
                }
            }
        }
    }    
    
    
查询全部索引：

    GET
    
    http://localhost:9200/_cat/indices?v
    
    Response:
    health status index    uuid                   pri rep docs.count docs.deleted store.size pri.store.size
    yellow open   shopping kcyt1LhkRfy-KkcT27-tmg   1   1          0            0       208b           208b
    
删除索引：

    DELETE
    
    http://localhost:9200/shopping
    
    Response:
    {
        "acknowledged": true
    }
    
    
#### 3.2 数据

创建数据(POST不是幂等，PUT方式是幂等)

    POST
    
    http://localhost:9200/shopping/_doc/2
    
    Request:
    {
        "name": "zhangsan",
        "age": 10
    }
    
    Response:
    {
        "_index": "shopping",
        "_type": "_doc",
        "_id": "2",
        "_version": 1,
        "result": "created",
        "_shards": {
            "total": 2,
            "successful": 1,
            "failed": 0
        },
        "_seq_no": 0,
        "_primary_term": 1
    }   
    
查询单条：

    GET
    
    http://localhost:9200/shopping/_doc/2
    
    Response:
    {
        "_index": "shopping",
        "_type": "_doc",
        "_id": "2",
        "_version": 1,
        "_seq_no": 0,
        "_primary_term": 1,
        "found": true,
        "_source": {
            "name": "zhangsan",
            "age": 10
        }
    }

单条更新(每个字段都更新)：

    PUT
    
    http://localhost:9200/shopping/_doc/2
    
    Request:
    {
        "name": "lisi",
        "age": 12
    }
    
    Response:
    {
        "_index": "shopping",
        "_type": "_doc",
        "_id": "2",
        "_version": 2,
        "result": "updated",
        "_shards": {
            "total": 2,
            "successful": 1,
            "failed": 0
        },
        "_seq_no": 2,
        "_primary_term": 1
    }


单条更新(指定更新某个字段)：

    POST
    
    http://localhost:9200/shopping/_update/2
    
    Request:
    {
        "doc": {
            "age": 100
        }
    }
    
    Response:
    {
        "_index": "shopping",
        "_type": "_doc",
        "_id": "2",
        "_version": 3,
        "result": "updated",
        "_shards": {
            "total": 2,
            "successful": 1,
            "failed": 0
        },
        "_seq_no": 3,
        "_primary_term": 1
    }  
    
单条删除：

    DELETE
    
    http://localhost:9200/shopping/_doc/1
    
    Response:
    {
        "_index": "shopping",
        "_type": "_doc",
        "_id": "1",
        "_version": 2,
        "result": "deleted",
        "_shards": {
            "total": 2,
            "successful": 1,
            "failed": 0
        },
        "_seq_no": 4,
        "_primary_term": 1
    }
    
查询所有数据(可以手动多加一些数据)：

    GET                   
    
    http://localhost:9200/shopping/_search
    
    Response:
    {
        "took": 2,
        "timed_out": false,
        "_shards": {
            "total": 1,
            "successful": 1,
            "skipped": 0,
            "failed": 0
        },
        "hits": {
            "total": {
                "value": 2,
                "relation": "eq"
            },
            "max_score": 1.0,
            "hits": [
                {
                    "_index": "shopping",
                    "_type": "_doc",
                    "_id": "1",
                    "_score": 1.0,
                    "_source": {
                        "name": "lisi",
                        "age": 10
                    }
                },
                {
                    "_index": "shopping",
                    "_type": "_doc",
                    "_id": "2",
                    "_score": 1.0,
                    "_source": {
                        "name": "lisi",
                        "age": 100
                    }
                }
            ]
        }
    }
    
    
条件查询(模糊查询，索引倒排， name 中包含 list 的都会查出)：

    POST
    
    http://localhost:9200/shopping/_search
    
    Request:
    {
        "query":{
            "match":{
                "name":"lisi"
            }
        }
    }
    
    Response:
    {
        "took": 4,
        "timed_out": false,
        "_shards": {
            "total": 1,
            "successful": 1,
            "skipped": 0,
            "failed": 0
        },
        "hits": {
            "total": {
                "value": 2,
                "relation": "eq"
            },
            "max_score": 0.24116206,
            "hits": [
                {
                    "_index": "shopping",
                    "_type": "_doc",
                    "_id": "1",
                    "_score": 0.24116206,
                    "_source": {
                        "name": "lisi",
                        "age": 10
                    }
                },
                {
                    "_index": "shopping",
                    "_type": "_doc",
                    "_id": "2",
                    "_score": 0.24116206,
                    "_source": {
                        "name": "lisi",
                        "age": 100
                    }
                }
            ]
        }
    }    
    
    
条件查询--查询全部(from 和 size 是分页可选项)：

    POST
    
    http://localhost:9200/shopping/_search
    
    Request：
    {
        "query": {
            "match_all": {}
        },
        "from": 0,  // 偏移量
        "size": 1  // 查询条数
    }    
    
    Response:
    {
        "took": 3,
        "timed_out": false,
        "_shards": {
            "total": 1,
            "successful": 1,
            "skipped": 0,
            "failed": 0
        },
        "hits": {
            "total": {
                "value": 2,
                "relation": "eq"
            },
            "max_score": 1.0,
            "hits": [
                {
                    "_index": "shopping",
                    "_type": "_doc",
                    "_id": "1",
                    "_score": 1.0,
                    "_source": {
                        "name": "lisi",
                        "age": 10
                    }
                }
            ]
        }
    }
    
and 查询：

    POST    
    
    http://localhost:9200/shopping/_search
    
    Request:
    {
        "query": {
            "bool": {
                "must": [
                    {
                        "match": {
                            "name": "lisi"
                        }
                    },
                    {
                        "match": {
                            "age": 10
                        }
                    }
                ]
            }
        }
    }
    
    Response：
    {
        "took": 4,
        "timed_out": false,
        "_shards": {
            "total": 1,
            "successful": 1,
            "skipped": 0,
            "failed": 0
        },
        "hits": {
            "total": {
                "value": 1,
                "relation": "eq"
            },
            "max_score": 1.1823215,
            "hits": [
                {
                    "_index": "shopping",
                    "_type": "_doc",
                    "_id": "1",
                    "_score": 1.1823215,
                    "_source": {
                        "name": "lisi",
                        "age": 10
                    }
                }
            ]
        }
    }
    
or 查询：

    POST
    
    http://localhost:9200/shopping/_search
    
    Request:
    {
        "query": {
            "bool": {
                "should": [
                    {
                        "match": {
                            "name": "lisi"
                        }
                    },
                    {
                        "match": {
                            "age": 10
                        }
                    }
                ]
            }
        }
    }
    
    Response：
    {
        "took": 5,
        "timed_out": false,
        "_shards": {
            "total": 1,
            "successful": 1,
            "skipped": 0,
            "failed": 0
        },
        "hits": {
            "total": {
                "value": 2,
                "relation": "eq"
            },
            "max_score": 1.1823215,
            "hits": [
                {
                    "_index": "shopping",
                    "_type": "_doc",
                    "_id": "1",
                    "_score": 1.1823215,
                    "_source": {
                        "name": "lisi",
                        "age": 10
                    }
                },
                {
                    "_index": "shopping",
                    "_type": "_doc",
                    "_id": "2",
                    "_score": 0.18232156,
                    "_source": {
                        "name": "lisi",
                        "age": 100
                    }
                }
            ]
        }
    }
    
    
范围查询：

    POST
    
    http://localhost:9200/shopping/_search
    
    Request:
    {
        "query": {
            "bool": {
                "filter": {
                    "range": {
                        "age": {
                            "gt": 10
                        }
                    }
                }
            }
        }
    }
    
    Response:
    {
        "took": 2,
        "timed_out": false,
        "_shards": {
            "total": 1,
            "successful": 1,
            "skipped": 0,
            "failed": 0
        },
        "hits": {
            "total": {
                "value": 1,
                "relation": "eq"
            },
            "max_score": 0.0,
            "hits": [
                {
                    "_index": "shopping",
                    "_type": "_doc",
                    "_id": "2",
                    "_score": 0.0,
                    "_source": {
                        "name": "lisi",
                        "age": 100
                    }
                }
            ]
        }
    }
    
    
查询结果精确匹配：

    POST
    
    http://localhost:9200/shopping/_search
    
    Request:
    {
        "query": {
            "match_phrase": {
                "name": "lisi"
            }
        }
    }
    
    Response:
    {
        "took": 3,
        "timed_out": false,
        "_shards": {
            "total": 1,
            "successful": 1,
            "skipped": 0,
            "failed": 0
        },
        "hits": {
            "total": {
                "value": 2,
                "relation": "eq"
            },
            "max_score": 0.18232156,
            "hits": [
                {
                    "_index": "shopping",
                    "_type": "_doc",
                    "_id": "1",
                    "_score": 0.18232156,
                    "_source": {
                        "name": "lisi",
                        "age": 10
                    }
                },
                {
                    "_index": "shopping",
                    "_type": "_doc",
                    "_id": "2",
                    "_score": 0.18232156,
                    "_source": {
                        "name": "lisi",
                        "age": 100
                    }
                }
            ]
        }
    }
    
查询结果高亮：

    POST
    
    http://localhost:9200/shopping/_search        
            
    Request：
    {
        "query": {
            "match": {
                "name": "lisi"
            }
        },
        "highlight": {
            "fields": {
                "name": {}
            }
        }
    }
    
    Response：
    {
        "took": 5,
        "timed_out": false,
        "_shards": {
            "total": 1,
            "successful": 1,
            "skipped": 0,
            "failed": 0
        },
        "hits": {
            "total": {
                "value": 2,
                "relation": "eq"
            },
            "max_score": 0.18232156,
            "hits": [
                {
                    "_index": "shopping",
                    "_type": "_doc",
                    "_id": "1",
                    "_score": 0.18232156,
                    "_source": {
                        "name": "lisi",
                        "age": 10
                    },
                    "highlight": {
                        "name": [
                            "<em>lisi</em>"
                        ]
                    }
                },
                {
                    "_index": "shopping",
                    "_type": "_doc",
                    "_id": "2",
                    "_score": 0.18232156,
                    "_source": {
                        "name": "lisi",
                        "age": 100
                    },
                    "highlight": {
                        "name": [
                            "<em>lisi</em>"
                        ]
                    }
                }
            ]
        }
    }
    
聚合查询--分组：

    POST
    
    http://localhost:9200/shopping/_search
    
    Request：
    {
        "aggs":{ // 聚合查询
            "age_group": { // 聚合名称，随意取名
                "terms":{
                    "field":"age"
                }
            }
        },
        "size":0 // 不查询出原始数据，只返回统计结构
    }     
    
    Response：
    {
        "took": 6,
        "timed_out": false,
        "_shards": {
            "total": 1,
            "successful": 1,
            "skipped": 0,
            "failed": 0
        },
        "hits": {
            "total": {
                "value": 2,
                "relation": "eq"
            },
            "max_score": null,
            "hits": []
        },
        "aggregations": {
            "age_group": {
                "doc_count_error_upper_bound": 0,
                "sum_other_doc_count": 0,
                "buckets": [
                    {
                        "key": 10,
                        "doc_count": 1
                    },
                    {
                        "key": 100,
                        "doc_count": 1
                    }
                ]
            }
        }
    }       
    
    
    
---

以上。    