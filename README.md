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





