## 分布式高級篇

### Elasticsearch
全文搜索屬於最常見的需求，開源的Elasticsearch是目前全文搜索引擎的首選。他可以快速儲存、搜索和分析海量數據。

#### 基本概念
1. **Index**(索引)
    * 動詞，相當於MySQL中的insert
    * 名詞，相當於MySQL中的**Database**
2. **Type**(類型)：在Index(索引)中，可以定義一個或多個類型。類似於MySQL的**Table**，**每一種類型的數據放在一起**。
3. **Document**(文檔)：保存在某個索引(Index)下，某種類型(Type)的一個數據(Document)，文檔是**JSON**格式的，**Document就像是MySQL中的某個Table裡面的內容**。
* *ex*.
  ![](https://i.imgur.com/xkRoQ3s.png)
* ElasticSearch查詢快速的原因：**倒排索引**
  ![](https://i.imgur.com/PYGdCVG.png)

#### Docker安裝
1. 下載鏡像文件
    * **elasticsearch**：存儲、檢索數據
    * **kibana**：可視化檢索數據
    ```
    docker pull elasticsearch:7.4.2
    docker pull kibana:7.4.2
    ```
2. 創建實例
    1. **ElasticSearch**
        1. 本機建好elasticsearch config和data的相關文件夾
            ```
            mkdir -p C:\Users\Jason\Desktop\gulimall\mydata\elasticsearch\config
            mkdir -p C:\Users\Jason\Desktop\gulimall\mydata\elasticsearch\data
            mkdir -p C:\Users\Jason\Desktop\gulimall\mydata\elasticsearch\plugins
            echo "http.host: 0.0.0.0" > C:\Users\Jason\Desktop\gulimall\mydata\elasticsearch\config\elasticsearch.yml
            ```
        2. Run container
            ```
            docker run --name elasticsearch -p 9200:9200 -p 9300:9300 
            -e  "discovery.type=single-node" 
            -e ES_JAVA_OPTS="-Xms64m -Xmx512m" 
            -v C:\Users\Jason\Desktop\gulimall\mydata\elasticsearch\config\elasticsearch.yml:/usr/share/elasticsearch/config/elasticsearch.yml
            -v C:\Users\Jason\Desktop\gulimall\mydata\elasticsearch\data:/usr/share/elasticsearch/data 
            -v C:\Users\Jason\Desktop\gulimall\mydata\elasticsearch\plugins:/usr/share/elasticsearch/plugins
            -d elasticsearch:7.4.2
            ```
           ==**note**==：`docker logs [containerName]`可以查啟動的log
        3. 訪問`localhost:9200`即可確認elasticsearch是否成功啟動
        4. 也可測試使用Postman發送請求
            * `localhost:9200`：elasticsearch相關信息
            * `localhost:9200/_cat_/nodes`：節點相關信息
    2. Kibaba(操作ElasticSearch的可視化工具)
        1. Run container(需要等一小段時間才會啟動成功，可以使用`docker logs [containerId]`來確認啟動的log)
            ```
            docker run --name kibana -e ELASTICSEARCH_HOSTS=http://192.168.0.14:9200 -p 5601:5601 -d kibana:7.4.2
            ```
           ==**注意**==：`ELASTICSEARCH_HOSTS`記得要填上本機ElasticSearch的訪問地址(不能填localhost，因為localhost代表kibana container的那台小型虛擬機)

#### 初步檢索
* **對ElasticSearch的所有操作，都被ElasticSearch封裝成了RestAPI，因此直接發送請求即可**
1. `_cat`
    * **GET** `/_cat/nodes`：查看節點狀況
    * **GET** `/_cat/health`：查看es健康狀況
    * **GET** `/_cat/master`：查看主節點信息
    * **GET** `/_cat/indices`：查看索引，類似MySQL的`show databases`查看所有數據庫
2. 索引一個文檔(保存)：保存一個數據，保存在哪個索引的哪個類型下，指定用哪個唯一標示，ex. `PUT customer/external/1`表示在customer索引下的external類型下保存1號數據為`{"name" : "Jason"}`(RequestBody中)
    * **PUT** `/customer/external/1`, `{"name" : "Jason"}`
      ![](https://i.imgur.com/9jP5WFU.png)
      再次發送後變成**Update**
      ![](https://i.imgur.com/OlMkYSL.png)
    * **POST** `/customer/external`, `{"name" : "Jason"}`，不帶ID則ElasticSearch會幫我們建立一個id，帶id也行(第二次發請求後一樣變成**Update**)
    * **PUT & POST差別**：PUT必須帶上ID
3. **查詢**文檔
    * **GET** `/customer/external/1`：查詢`customer`索引底下、`external`類型的`1`號數據
      Response:point_right:
        ```json=
        {
            "_index": "customer",
            "_type": "external",
            "_id": "1",
            "_version": 2,
            "_seq_no": 1,
            "_primary_term": 1,
            "found": true,
            "_source": {
                "name": "Jason"
            }
        }
        ```
        * `_index`：在哪個索引
        * `_type`：在哪個類型
        * `_id`：紀錄id
        * `_version`：版本號
        * `_seq_no`：**併發控制字段**，每次更新就會+1，用來做**樂觀鎖**
        * `_primary_term`：同上，主分片重新分配，如重啟就會變化
        * `found`：是否能被查詢
        * `_source`：真正的內容

      ==**樂觀鎖使用，更新攜帶條件**==： URL後方加上條件`?if_seq_no=1&if_primary_term=1`，代表只有在`_seq_no`為`1`且`_primary_term`也為`1`的時候才會做修改:point_right:並免併發問題發生
4. 更新文檔
    * **POST** `/customer/external/1/_update`, `{"doc" : {"name" : "UPDATE"}}`:point_right:一定要加`doc`，**會比較原數據**，若與原數據相同(沒有發生變化)則`result`會為`noop`、`_seq_no`也不會變
      ![](https://i.imgur.com/LXS4fWk.png)
        * **POST**不帶`_update`的話，response body則不用帶`docs`，這種則**不會檢查原數據**
    * **PUT** `/customer/external/1`, `{"name" : "UPDATE"}`
    * 更新皆可對Response Body添加新的數據
5. 刪除文檔&索引
    * **DELETE** `/customer/external/1`
    * **DELETE** `/customer`
    * **ElasticSearch不能刪除類型**(ex.external)
6. bulk批量API
    * **POST** `/customer/external/_bulk`，Request Body兩行為一組，每條紀錄都是獨立的，前一行失敗不會影響下一行
      ![](https://i.imgur.com/I5iGU1H.png)
    * 發送**POST** `/bank/account/_bulk`，新增測試數據，body連結：https://github.com/elastic/elasticsearch/blob/v7.4.2/docs/src/test/resources/accounts.json

#### 進階檢索
1. SearchAPI
    * ElasticSearch支持2種基本方式檢索：
        1. 通過使用**REST request URI**，發送搜索參數(uri+檢索參數):point_right:**GET** `/bank/_search?q=*&sort=account_number:asc`
        2. 通過使用**REST request body**來發送他們(uri+請求體):point_right:**GET** `/bank/_search`(**較常用！也就是下方的Query DSL**)
            ```
            {
                "query" : {
                    "match_all" : {}
                },
                "sort": [
                  {
                    "account_number": "asc"
                  }
                ]
            }
            ```
2. Query DSL
    1. 基本語法格式：ElasticSearch提供了一個可以執行查詢的JSON風格的**DSL**(domain-specific language領域特定語言)。這個被稱為Query DSL。該查詢語言非常全面，並且剛開始的時候感覺有點複雜，真正學好他的方法是從一些基礎的示例開始的。
        * 一個查詢語句的典型結構
            ```
            {
                QUERY_NAME: {
                    ARGUMENT: VALUE,
                    ARGUMENT: VALUE
                }
            }
            ```
        * 如果是針對某個字段，那麼結構如下
            ```
            {
                QUERY_NAME: {
                    FIELD_NAME: {
                        ARGUMENT: VALUE,
                        ARGUMENT: VALUE
                    }
                }
            }
            ```
        * `sort`：排序，多字段排序，會在前序字段相等時後續字段內部排序，否則以前序為準
        * `from` + `size`：分頁功能，`from`第幾筆開始，`size`查幾筆
        * 範例：
          ![](https://i.imgur.com/luD3SDy.png)
    2. 返回部分
        * `_source`：欲查詢之欄位，類似select，多組欄位則使用["col1", "col2"]
          ![](https://i.imgur.com/tKbYn0E.png)
   3. match匹配查詢
       * 基本類型(非字串)，精確匹配
         ![](https://i.imgur.com/600Po6c.png)
       * 字串，全文檢索
         ![](https://i.imgur.com/1NBBDaF.png)
           * 查詢結果`_score`越大代表越匹配
           * 全文檢索按照評分進行排序，會對檢索條件進行分詞匹配
   4. match_phrase短語匹配：將需要匹配的值當成一個**整體單詞**(**不分詞**)進行檢索
      ![](https://i.imgur.com/oMB2EF2.png)
   5.  `multi_match`：多字段匹配，以下案例為address或state有包含mill字段，**會進行分詞**
       ![](https://i.imgur.com/3JPMUut.png)
   6. `bool`複合查詢：複合語句可以合併任何其他語句，包括複合語句。:point_right:了解這點非常重要，因為這意味著**複合語句之間可以互相嵌套**，以表達非常複雜的邏輯
       * `must`為以下條件必須**皆**滿足
       * `must_not`為以下條件必須**皆不**滿足
       * `should`為應該滿足，可以滿足也可不滿足以下條件，差別在若滿足的紀錄`_score`會比較高
         ![](https://i.imgur.com/M1ZXH3K.png)
   7. `filter`結果過濾：並不是所有的查詢都需要產生分數，特別是那些僅用於**filter**(過濾)的文檔。為了不計算分數ElasticSearch會自動檢查場景並且優化查詢的執行
      ![](https://i.imgur.com/76qmbSV.png)
       * **最主要的差別是`filter`不會計算相關性得分(`_score`)** 
   8. `term`：和`match`一樣，匹配某個屬性的值。**全文檢索字段用`match`**，**其他非text字段匹用`term`**
      ![](https://i.imgur.com/4DCaJPs.png)
       * ==**補充**==：`.keyword`是代表**整個內容就是這個字串值(精確匹配)**，`match_phrase`則是短語匹配，是整個字串**包含**這個短語就算
         ![](https://i.imgur.com/NkWe7yp.png)
   9. `aggregations`：聚合提供了從數據中分組、提取數據的能力。最簡單的聚合方法大致等於SQL GROUP BY和SQL聚合函數。
       * 範例一：搜索`address`中包含`mill`的所有人的年齡分布以及平均年齡
           ```
           {
             "query": {
               "match": {
                 "address": "mill"
               }
             },
             "aggs": {
               "ageAgg": {
                 "terms": {
                   "field": "age",
                   "size": 10
                 }
               },
               "ageAvg":{
                 "avg": {
                   "field": "age"
                 }
               },
               "balanceAvg":{
                 "avg": {
                   "field": "balance"
                 }
               }
             }
           }
           ```
       * 範例二：按照年齡聚合，並且請求這些年齡段的這些人的平均薪資(使用子聚合)
           ```
           {
             "query": {
               "match_all": {}
             },
             "aggs": {
               "ageAgg":{
                 "terms": {
                   "field": "age",
                   "size": 100
                 },
                 "aggs": {
                   "ageAvg": {
                     "avg": {
                       "field": "balance"
                     }
                   }
                 }
               }
             }
           }
           ```
       * 範例三：查出所有年齡分布，並且這些年齡段中M的平均薪資和F的平均薪資以及這些年齡段的總體平均薪資
           ```
           {
             "query": {
               "match_all": {}
             },
             "aggs": {
               "ageAgg": {
                 "terms": {
                   "field": "age",
                   "size": 100
                 },
                 "aggs": {
                   "genderAgg": {
                     "terms": {
                       "field": "gender.keyword"
                     },
                     "aggs": {
                       "balanceAvg": {
                         "avg": {
                           "field": "balance"
                         }
                       }
                     }
                   },
                   "ageBalanceAvg":{
                     "avg": {
                       "field": "balance"
                     }
                   }
                 }
               }
             }
           }
           ```
3. **Mapping**映射
    * ==**ElasticSearch7去掉Type的概念**==，為了提升ES處理數據的效率
    * ElasticSearch數據類型：https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping-types.html
    1. 查看映射信息：**GET** `/bank/_mapping`
    2. 創建索引並指定映射：**PUT** `/my_index`
       ![](https://i.imgur.com/mNihxyp.png)
    3. 添加新的字段映射：**PUT** `/my_index/_mapping`:point_right:`index`若為false，代表該字段**不會被檢索到**
       ![Uploading file..._6pmdkj6j1]()
    4. 更新映射：對於已經存在的映射字段，我們**不能更新**，更新必須創建新的索引進行數據遷移
    5. 數據遷移
        1. 創建一個新索引：**PUT** `/newbank`
        2. 數據遷移：**POST** `_reindex`
            ```
            {
              "source": {
                "index": "bank",
                "type": "account"
              },
              "dest": {
                "index": "newbank"
              }
            }
            ```
4. **分詞**：一個tokenizer(分詞器)接收一個字符流，將之分割為獨立的tokens(詞元，通常是獨立單詞)，然後輸出成tokens流。
    * 默認使用`standard`分詞
      ![](https://i.imgur.com/FcBiFSy.png)
    1. 安裝**ik分詞器**：https://github.com/medcl/elasticsearch-analysis-ik/releases?expanded=true&page=4&q=v7.4.2
        1. 下載跟ElasticSearch一樣的版本(v7.4.2)
        2. 將下載的zip放到本機的`/mydata/elasticsearch/plugins`中(創建容器時已有建立**容器數據卷**作為映射，因此容器內也有這份zip了)
        3. 解壓縮該zip
        4. 進入容器內部(`docker exec -it [id] /bin/bash`)的`bin`目錄，下`elasticsearch-plugin list`命令，可以看現在有的plugin(目前有ik)
        5. 重啟elasticsearch容器
    2. 使用ik分詞
       1.`ik_smart`
       ![](https://i.imgur.com/uEsVGpc.png)
        2. `ik_max_word`
           ![](https://i.imgur.com/6M8t27Z.png)           