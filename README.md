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
