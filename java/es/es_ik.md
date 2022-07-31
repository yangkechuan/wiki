##  es 使用 ik 分词器

---

            环境：
            
            MAC 系统 10.15.6
            Java Version ：1.8.0_202
            Docker 方式安装
            elasticsearch version:7.8.0


---
> *es 默认分词，对于中文分词不够智能，只会把每一个字拆开。需要使用更智能的中文分词工具，比如 ik 分词*

### 0.下载使用

- github 主页：https://github.com/medcl/elasticsearch-analysis-ik
- 下载地址：https://github.com/medcl/elasticsearch-analysis-ik/releases。

找到对应的版本下载，例如，当前 `es` 是 `7.8.0`，则下载 [elasticsearch-analysis-ik-7.8.0.zip](https://github.com/medcl/elasticsearch-analysis-ik/releases/download/v7.8.0/elasticsearch-analysis-ik-7.8.0.zip) 版本。

以下方式都可以安装，任选一种即可：

1. 可以下载到 `es` 的 `plugins` 目录下，然后解压
   1. 创建目录：`cd your-es-root/plugins/ && mkdir ik`。
   2. 解压到对应文件下：`your-es-root/plugins/ik`。
2. 可以使用 `es` 自带命令安装：

注意：`docker` 下 `es` 的扩展目录：`/usr/share/elasticsearch/plugins`

```shell
sh-4.2# ./elasticsearch-plugin install https://github.com/medcl/elasticsearch-analysis-ik/releases/download/v7.8.0/elasticsearch-analysis-ik-7.8.0.zip
-> Installing https://github.com/medcl/elasticsearch-analysis-ik/releases/download/v7.8.0/elasticsearch-analysis-ik-7.8.0.zip
-> Downloading https://github.com/medcl/elasticsearch-analysis-ik/releases/download/v7.8.0/elasticsearch-analysis-ik-7.8.0.zip
[=================================================] 100%??
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
@     WARNING: plugin requires additional permissions     @
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
* java.net.SocketPermission * connect,resolve
  See http://docs.oracle.com/javase/8/docs/technotes/guides/security/permissions.html
  for descriptions of what these permissions allow and the associated risks.

Continue with installation? [y/N]y
-> Installed analysis-ik
sh-4.2# pwd
/usr/share/elasticsearch/bin
sh-4.2# cd ../plugins/
sh-4.2# ls
analysis-ik
```

**安装完成后，需要重新启动容器**



### 1.测试效果


- ik_max_word:将文本做最细粒度的拆分
- ik_smart:将文本做最粗力度的拆分


ik_max_word:

            GET

            http://localhost:9200/_analyze

            Request:
            {
                "text": "中国人",
                "analyzer": "ik_max_word"
            }
            

            Response:
            {
                "tokens": [
                    {
                        "token": "中国人",
                        "start_offset": 0,
                        "end_offset": 3,
                        "type": "CN_WORD",
                        "position": 0
                    },
                    {
                        "token": "中国",
                        "start_offset": 0,
                        "end_offset": 2,
                        "type": "CN_WORD",
                        "position": 1
                    },
                    {
                        "token": "国人",
                        "start_offset": 1,
                        "end_offset": 3,
                        "type": "CN_WORD",
                        "position": 2
                    }
                ]
            }



ik_smart:

            GET

            http://localhost:9200/_analyze

            Request:
            {
                "text": "中国人",
                "analyzer": "ik_smart"
            }

            Response:
            {
                "tokens": [
                    {
                        "token": "中国人",
                        "start_offset": 0,
                        "end_offset": 3,
                        "type": "CN_WORD",
                        "position": 0
                    }
                ]
            }




### 2.自定义扩展词

#### 2.1 本地定义

某些场景下，需要制定特定词汇，让 `es` 识别并正确拆分。

例如想把 `弗雷尔卓德` 作为一个词让 `es` 识别。就需要做自定义扩展。


打开文件 `IKAnalyzer.cfg.xml` 文件:（ 路径在 `your-es-root/elasticsearch/config/analysis-ik/IKAnalyzer.cfg.xml` 或者 `your-es-root/elasticsearch/plugins/elasticsearch-analysis-ik-*/config/IKAnalyzer.cfg.xml`）


```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE properties SYSTEM "http://java.sun.com/dtd/properties.dtd">
<properties>
	<comment>IK Analyzer 扩展配置</comment>
	<!--用户可以在这里配置自己的扩展字典 -->
	<entry key="ext_dict"></entry>
	 <!--用户可以在这里配置自己的扩展停止词字典-->
	<entry key="ext_stopwords"></entry>
	<!--用户可以在这里配置远程扩展字典 -->
	<!-- <entry key="remote_ext_dict">words_location</entry> -->
	<!--用户可以在这里配置远程扩展停止词字典-->
	<!-- <entry key="remote_ext_stopwords">words_location</entry> -->
</properties>
```


可以在 `IKAnalyzer.cfg.xml` 同目录下新建字典文件，文件名任意(例如：ext.dic)，并写入扩展词：

```shell
sh-4.2# cat ext.dic
弗雷尔卓德
```

然后更新 `IKAnalyzer.cfg.xml` 文件：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE properties SYSTEM "http://java.sun.com/dtd/properties.dtd">
<properties>
	<comment>IK Analyzer 扩展配置</comment>
	<!--用户可以在这里配置自己的扩展字典 -->
	<!-- 这里更新自定义文件 -->
	<entry key="ext_dict">ext.dic</entry>
	 <!--用户可以在这里配置自己的扩展停止词字典-->
	<entry key="ext_stopwords"></entry>
	<!--用户可以在这里配置远程扩展字典 -->
	<!-- <entry key="remote_ext_dict">words_location</entry> -->
	<!--用户可以在这里配置远程扩展停止词字典-->
	<!-- <entry key="remote_ext_stopwords">words_location</entry> -->
</properties>
```

**修改完成后，重启 es**


然后尝试请求：


            GET

            http://localhost:9200/_analyze

            Request:
            {
                "text": "弗雷尔卓德",
                "analyzer": "ik_max_word"
            }

            Response:
            {
                "tokens": [
                    {
                        "token": "弗雷尔卓德",
                        "start_offset": 0,
                        "end_offset": 5,
                        "type": "CN_WORD",
                        "position": 0
                    }
                ]
            }


会发现 `弗雷尔卓德` 已经是一个特定词汇。



#### 2.2 远程热更新

由于在 `es` 本地配置，每次都需要重启，不是很智能。可以配置远程文件，动态更新远程文件即可。

以下内容从 [github](https://github.com/medcl/elasticsearch-analysis-ik) 主页复制：

      
      目前该插件支持热更新 IK 分词，通过上文在 IK 配置文件中提到的如下配置
      
          <!--用户可以在这里配置远程扩展字典 -->
          <entry key="remote_ext_dict">location</entry>
          <!--用户可以在这里配置远程扩展停止词字典-->
          <entry key="remote_ext_stopwords">location</entry>
      其中 location 是指一个 url，比如 http://yoursite.com/getCustomDict，该请求只需满足以下两点即可完成分词热更新。
      
      该 http 请求需要返回两个头部(header)，一个是 Last-Modified，一个是 ETag，这两者都是字符串类型，只要有一个发生变化，该插件就会去抓取新的分词进而更新词库。
      
      该 http 请求返回的内容格式是一行一个分词，换行符用 \n 即可。
      
      满足上面两点要求就可以实现热更新分词了，不需要重启 ES 实例。
      
      可以将需自动更新的热词放在一个 UTF-8 编码的 .txt 文件里，放在 nginx 或其他简易 http server 下，当 .txt 文件修改时，http server 会在客户端请求该文件时自动返回相应的 Last-Modified 和 ETag。可以另外做一个工具来从业务系统提取相关词汇，并更新这个 .txt 文件




### 3.新建索引中，使用 ik 分词器

如果是新建的索引，可以在新建索引后，直接修改为 `ik` 分词。

#### 3.1 新建索引

新建 `myindex` 索引：

      PUT

      http://localhost:9200/myindex


      Response:
      {
          "acknowledged": true,
          "shards_acknowledged": true,
          "index": "myindex"
      }


#### 3.2 设置 mapping 

需要注意，这里是给 `myindex` 索引下的， `content` 字段设置。

      POST

      http://localhost:9200/myindex/_mapping

      Request:
      {
          "properties": {
              "content": {
                  "type": "text",
                  "analyzer": "ik_max_word",
                  "search_analyzer": "ik_smart"
              }
          }
      }

   
      Response:
      {
          "acknowledged": true
      }


#### 3.3 新增数据


创建 `中国人` 数据，正常情况下，会和上述测试效果一致，被分成 `中国人`、`中国`、`国人` 三种情况。

      POST

      http://localhost:9200/myindex/_doc/1

      Request:
      {
          "content":"中国人"
      }


      Response:
      {
          "_index": "myindex",
          "_type": "_doc",
          "_id": "1",
          "_version": 3,
          "result": "created",
          "_shards": {
              "total": 2,
              "successful": 1,
              "failed": 0
          },
          "_seq_no": 6,
          "_primary_term": 1
      }


#### 3.4 查询数据

通过 `中国` 来查询：

      GET

      http://localhost:9200/myindex/_search

      Request:
      {
          "query": {
              "match": {
                  "content": "中国"
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
                  "value": 1,
                  "relation": "eq"
              },
              "max_score": 0.4264592,
              "hits": [
                  {
                      "_index": "myindex",
                      "_type": "_doc",
                      "_id": "1",
                      "_score": 0.4264592,
                      "_source": {
                          "content": "中国人"
                      }
                  }
              ]
          }
      }


通过 `国人` 来查询：

      GET

      http://localhost:9200/myindex/_search

      Request:
      {
          "query": {
              "match": {
                  "content": "国人"
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
                  "value": 1,
                  "relation": "eq"
              },
              "max_score": 0.4264592,
              "hits": [
                  {
                      "_index": "myindex",
                      "_type": "_doc",
                      "_id": "1",
                      "_score": 0.4264592,
                      "_source": {
                          "content": "中国人"
                      }
                  }
              ]
          }
      }


通过 `中国人` 来查询：

      GET

      http://localhost:9200/myindex/_search

      Request:
      {
          "query": {
              "match": {
                  "content": "中国人"
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
                  "value": 1,
                  "relation": "eq"
              },
              "max_score": 0.2876821,
              "hits": [
                  {
                      "_index": "myindex",
                      "_type": "_doc",
                      "_id": "1",
                      "_score": 0.2876821,
                      "_source": {
                          "content": "中国人"
                      }
                  }
              ]
          }
      }


正常情况下，只会有三种分词，不会把每个字分开，可以试一下：

通过 `中` 来查询：

      GET

      http://localhost:9200/myindex/_search

      Request:
      {
          "query": {
              "match": {
                  "content": "中"
              }
          }
      }

      Response:
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
                  "value": 0,
                  "relation": "eq"
              },
              "max_score": null,
              "hits": []
          }
      }

可以看到，并没与查询结果。


---

以上。


