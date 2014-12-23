## Java 第二次大作业 文档
# 在线词典

### 项目成员
* 121220079 邵菲
* 121220083 沈宇桔
+ 121220130 余子洵（组长）

## 项目背景
本项目完成一个网络在线词典。

### 基本功能
对于用户提供的要查询的单词，该词典搜索多个网站， 得到不同的单词翻译结果，最终返回给用户。

### 扩展功能：用户反馈
用户可以以某些方式影响搜索结果，比如给某个网站对某个单词的解释**点赞**，
而系统可以根据评价情况将不同网站搜索到的结果作排序。

### 扩展功能：帐号管理
该词典带有帐号管理功能，可以注册新用户，可以登录和登出。
在线用户可以对单词**点赞**或**拍砖**，在线用户也可以向其他在线用户发送单词卡。

## 关键技术

### 数据库
用户相关信息（用户名、密码、是否在线）、词条相关信息（关键词、注音、解释来源等）都需要存储，
而现有的数据库技术是一个非常理想的工具，本项目使用的数据库是MySQL。
![Database Relationship](file:///home/vocryan/Documents/java/diki/Database_diki.png)
本项目使用经典的关系模式设计数据库，有如下几张关系表：

#### 用户
关键字为用户名。密码、是否在线、连接的IP地址和端口地址等为Satellite数据。

#### 词条
关键字为单词字符串。没有其他属性项。

#### 词条信息
关键字为**单词+解释来源**（比如，"hello"+"baidu"就构成一个关键字）。
其中单词为引用**词条**表的外键。
注音、词性、释义、点赞数、拍砖数等构成Satellite数据。

#### 点赞记录
一个用户只能给一个单词来自某一个网站的解释点**一次**赞。
每一条记录由用户名、单词、来源构成，是**全关键字**关系。
用户名和单词分别是引用**用户**表和**词条**表的外键。

#### 拍砖记录
和点赞记录类似。

#### 单词卡表
一个用户给另一个用户发送一张单词卡，而一张单词卡又由单词和解释来源唯一确定。
因此发送方、接收方、单词、解释来源构成一张卡，是全关键字关系。
发送方、接收方是引用**用户**表的外键，单词是引用**词条**的外键。

### 网络通信：获取网络解释
OnlineSearcher对象的search方法建立三个线程分别执行三个网站的单词解释爬取功能。
主要使用了非常方便的HTML解析器**Jsoup**来进行网页Document中单词、音标、词性和解释标签内容的爬取，
然后对因*网络连接失败*和*网上没有这个单词*造成没有查到单词的两种情况进行了区分处理，
最后将获得的网络解释写入词条中，等待三个线程结束后返回查询结果的词条。

### 网络通信：客户端和服务器的交互
客户端和服务器通过套接字连接，
服务器监听服务器套接字，每次客户端运行时就与服务器建立连接，在连接上相互传送报文来交互。
本项目使用的报文形式是简单的字符串形式，有一个简单的格式：前三个字符为操作码，后面的内容根据操作码而异：

* qrg (Query on ReGister): 注册请求操作，后面跟着用户名和密码。
    用户名和密码之间用'^'分隔。
* rrg (Reply on ReGister)：回复注册请求，后面跟着注册成功('true')或者失败('false')。
* qli (Query on Log In): 登录请求操作，后面跟着用户名和密码。
    用户名和密码之间用'^'分隔。
* rli (Reply on Log In): 登录回复操作，后面跟着登录成功('true')或者失败。
	成功的场合，后面再跟上当前在线用户的列表。各个用户名之间用'^'分隔。
* qlo (Query on Log Out): 下线请求操作，后面跟着用户名。
* rlo (Reply on Log Out): 回复下线操作，后面跟着下线成功('true')或失败('false')。
* qse (Query on SEarch): 搜索请求操作，后面跟着单词。
* rse (Reply on SEarch): 回复搜索操作，后面跟着单词的三个解释。每个解释之间用'^'分隔，
	解释中的来源、音标、词性、解释信息之间用'$'分隔。
* qza (Query on ZAn): 点赞请求操作，后面跟着用户名、单词和解释来源。
	用户名、单词和解释来源用'^'分隔。
* rza (Reply on ZAn): 回复点赞操作，后面跟着成功或失败。
* quz (Query on UnZan): 拍砖请求操作，类似qza操作。
* ruz (Reply on UnZan): 回复拍砖操作，类似rza操作。
* qsc (Query on Send Card): 发卡请求操作，后面跟着发送方、接收方、单词和解释来源。
	四个字段用'^'分隔。
* rsc (Reply on Send Card): 回复发卡操作，附加成功或失败信息。
* qgc (Query on Get Card): 获取卡片请求操作，附加用户名。
* rgc (Reply on Get Card): 回复获取卡片操作，后面跟上某个用户的所有单词卡。
						   单词卡中的发送方、单词、解释构成，中间由'^'分隔。

### 多线程：服务器
服务器监听服务器套接字，每一次有一个客户端发起连接请求，就将创建一个新线程，处理该客户端和服务器之间的交互。
各个线程之间互相不干扰，写入数据库时产生的冲突由数据库系统来解决。

### 客户端UI
![example](file:///home/vocryan/Documents/java/diki/screenshots/image1.jpg)
主要由三个部分构成：log panel，search panel，show panel
#### log panel
* login按钮 - 登录
* logout按钮 - 注销
* register按钮 - 注册
* note按钮 - 查看单词卡
#### search panel
* input输入框 - 输入单词
* search按钮 - 搜索
* 三个复选框 - 选择显示来源
* refresh按钮 - 刷新当前在线用户列表
#### show panel
* 一个在线用户列表 - 显示当前所有在线用户
* 三个小的panel，每个panel有一个文本显示框，一个文本输入框和三个按钮 - 根据排序情况和选择来源情况将词条解释显示在三个文本框中，选择当前在线用户发送单词卡，给对应来源的单词解释点赞或者拍砖

### 服务器UI
主要由两部分构成，左半边为当前在线用户列表，右半边为系统日志信息。
![Server UI](file:///home/vocryan/Documents/java/diki/server-ui.png)


## 具体实现
请查阅本项目`doc`目录下的Java文档，索引页为`index.html`。

（如果显示乱码请将编码设置为UTF-8）


## 运行环境

### 服务器
* 操作系统： Kubuntu Linux 14.04
* 数据库版本： 5.5.40
* JDK/JRE版本：1.7.0_65

### 客户端
* 操作系统： Windows 7
* JDK/JRE版本：1.8.0_25

## 项目展示


![Screenshot](file:///home/vocryan/Documents/java/diki/screenshots/image1.jpg)

运行界面

![Screenshot](file:///home/vocryan/Documents/java/diki/screenshots/image2.jpg)

离线查词功能，在输入框中输入单词，按enter或者鼠标点击搜索进行离线查词

![Screenshot](file:///home/vocryan/Documents/java/diki/screenshots/image3.jpg)

当不想查youdao的时候，去掉复选框里的勾，可以不显示这个来源

![Screenshot](file:///home/vocryan/Documents/java/diki/screenshots/image4.jpg)

输入用户名密码注册

![Screenshot](file:///home/vocryan/Documents/java/diki/screenshots/image5.jpg)

注册成功

![Screenshot](file:///home/vocryan/Documents/java/diki/screenshots/image6.jpg)

密码错误导致注册失败

![Screenshot](file:///home/vocryan/Documents/java/diki/screenshots/image7.jpg)

用户名已经存在导致注册失败

![Screenshot](file:///home/vocryan/Documents/java/diki/screenshots/image8.jpg)

注册成功

![Screenshot](file:///home/vocryan/Documents/java/diki/screenshots/image9.jpg)

输入刚刚注册的用户名密码

![Screenshot](file:///home/vocryan/Documents/java/diki/screenshots/image10.jpg)

登录成功，原来login按钮上显示登陆的用户姓名

![Screenshot](file:///home/vocryan/Documents/java/diki/screenshots/image11.jpg)

再登陆几个用户并刷新在线用户列表

![Screenshot](file:///home/vocryan/Documents/java/diki/screenshots/image12.jpg)

在线搜索的结果，比离线搜索更快（因为如果单词已经被搜过只有直接从数据库中提取），并且显示了点赞人数和点不赞人数

![Screenshot](file:///home/vocryan/Documents/java/diki/screenshots/image13.jpg)

点了赞和点了不赞之后，单词按照点赞-点不赞的总字数排列

![Screenshot](file:///home/vocryan/Documents/java/diki/screenshots/image14.jpg)

搜索我已经点过赞的某个来源的单词，我无法再为这个来源的单词点赞

![Screenshot](file:///home/vocryan/Documents/java/diki/screenshots/image15.jpg)

选中用户，这个用户名显示在文本框中，点击send card给这个用户发送单词卡

![Screenshot](file:///home/vocryan/Documents/java/diki/screenshots/image16.jpg)

或者直接输入用户名，为这个用户发送单词卡

![Screenshot](file:///home/vocryan/Documents/java/diki/screenshots/image17.jpg)

我的单词本，登录denny用户，查看单词本，里面已经有一些其他用户发送给denny的单词了

![Screenshot](file:///home/vocryan/Documents/java/diki/screenshots/image18.jpg)

翻到最后，看到了来自error发给denny的单词卡

![Screenshot](file:///home/vocryan/Documents/java/diki/screenshots/image19.jpg)

同样的，wall用户也受到了来自error的单词卡

![Screenshot](file:///home/vocryan/Documents/java/diki/screenshots/image20.jpg)

点击logout按钮注销当前用户，在线用户列表清空，同时弹出消息框提醒注销成功

## 小组互评
组员进行了激烈的组内讨论，由于评判标准不唯一，综合brainstorm、代码编写、代码调试等多个方面，最终决定用随机数的方式决定每个人的分数，以下是结果：

* 121220079 邵菲（负责客户端设计） 95.2
* 121220083 沈宇桔（负责网络查词和数据库设计） 97.6
+ 121220130 余子洵（负责服务器设计） 94.7

（其他部分如数据结构，通信协议等由组员讨论共同设计）