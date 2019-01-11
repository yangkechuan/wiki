##  windows下mysql 主从复制

---

### 0.说明

1. 试验环境

- windows 10
- mysql: 5.7 压缩版， 一主一从

2. 注意点

- 以下所有cmd操作需在管理员权限下执行
- 如果本地已经有mysql服务，注意端口是否冲突
- mysql 初始化操作必做

---


### 1. mysql 安装

下载压缩版mysql

    mysql Version: 5.7
    下载类型：Windows (x86, 64-bit), ZIP Archive
    下载地址：https://dev.mysql.com/downloads/mysql/5.7.html#downloads
    
下载后解压，然后复制一份，分别作为 `master`  和 `slave`

如图所示：
![windows_mysql_master_slave_1](../images/windows_mysql_master_slave_1.png)



### 2. master配置my.ini

**注意** ： 压缩版需要自己在**根目录**创建 `my.ini` 文件

```lombok.config
# 以下内容为手动添加
[client]

port=3310
default-character-set=utf8

[mysql]
default-character-set=utf8



[mysqld]
#主库配置

port=3310

server_id=1
log_bin=master-bin
log_bin-index=master-bin.index


#解压目录
basedir=D:/soft/mysql/mysql-5.7-master
datadir=D:/soft/mysql/mysql-5.7-master/data

# The default character set that will be used when a new schema or table is
# created and no character set is defined
character-set-server=utf8

# The default storage engine that will be used when create new tables when
default-storage-engine=INNODB

# Set the SQL mode to strict
sql-mode="STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION"
```


### 3.启动 master

1.安装服务

**管理员**打开cmd，切换到 master 下的 `bin` 目录下，执行：`mysqld  --install master --defaults="D:\soft\mysql\mysql-5.7-master\my.ini"`

如果正常，则可以看到提示：`Service successfully installed`

2.初始化

第一步执行正常后，执行命令 ： `mysqld  --initialize` , 如果没有任何提示，也没有任何错误，说明正常，此命令会执行初始化操作，包括创建根目录

下的`data`文件夹， **注意**，这一步必须执行，不然服务无法启动

3.启动服务

执行：`net start master` 启动服务

如果返回：

    master 服务正在启动 .    
    master 服务已经启动成功。
    
则说明服务启动成功，同时也可以在windows的`服务`中查找`master`服务，查看状态。

服务启动后，在`data`目录下，查找`err`后缀的文件，打开查看日志

```log
2019-01-11T07:43:40.689299Z 1 [Note] A temporary password is generated for root@localhost: 5ik=ru)WkI!p
```

其中上面的一行日志，说明了默认的`root`密码为 `5ik=ru)WkI!p`.

命令行登录 mysql:

    mysql -uroot -p

提示需要输入密码，输入：`5ik=ru)WkI!p` 登录，然后更新`root`密码：

```sql
set password for 'root'@'localhost' = password('123456')
```


### 4.slave配置my.ini

配置信息如下：

```lombok.config
# 以下内容为手动添加
[client]

port=3311
default-character-set=utf8

[mysql]
default-character-set=utf8

[mysqld]
#从库配置

port=3311

server_id=2
relay-log-index=slave-relay-bin.index
relay-log=slave-relay-bin


#解压目录
basedir=D:/soft/mysql/mysql-5.7-slave
datadir=D:/soft/mysql/mysql-5.7-slave/data

# The default character set that will be used when a new schema or table is
# created and no character set is defined
character-set-server=utf8

# The default storage engine that will be used when create new tables when
default-storage-engine=INNODB

# Set the SQL mode to strict
sql-mode="STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION"
```


### 5.启动 slave

过程与master类似

1.安装服务

**管理员**打开cmd，切换到 slave 下的 `bin` 目录下，执行：`mysqld  --install slave --defaults="D:\soft\mysql\mysql-5.7-slave\my.ini"`

如果正常，则可以看到提示：`Service successfully installed`

2.初始化

第一步执行正常后，执行命令 ： `mysqld  --initialize` , 如果没有任何提示，也没有任何错误，说明正常，此命令会执行初始化操作，包括创建根目录

下的`data`文件夹， **注意**，这一步必须执行，不然服务无法启动

3.启动服务

执行：`net start slave` 启动服务

如果返回：

    slave 服务正在启动 .    
    slave 服务已经启动成功。
    
则说明服务启动成功，同时也可以在windows的`服务`中查找`slave`服务，查看状态。

服务启动后，在`data`目录下，查找`err`后缀的文件，打开查看日志

```log
2019-01-11T07:37:37.449498Z 1 [Note] A temporary password is generated for root@localhost: jMs/eqJ4JU<D
```

其中上面的一行日志，说明了默认的`root`密码为 `jMs/eqJ4JU<D`.

命令行登录 mysql:

    mysql -uroot -p

提示需要输入密码，输入：`jMs/eqJ4JU<D` 登录，然后更新`root`密码：

```sql
set password for 'root'@'localhost' = password('123456')
```


### 6.主从同步

`master` 和 `slave` 服务目前已经都在启动，接下来需要设置同步。

首先，需要从 `master` 创建一个用户，专门用来做数据同步，然后 `slave` 用这个账户进行同步。


1. `master`新建用户

```sql
create user slave;
grant replication slave on *.* to 'slave'@'localhost'identified by '123456';
flush privileges;
```

2. `slave` 设置同步

`slave`配置`master`

```sql
change master to master_host='localhost',master_port=3310,master_user='slave',master_password='123456',master_log_file='master-bin.000001',master_log_pos=0;
```

进行数据同步：
```sql
start slave;
```


这时候，在`master` 新建库、创建表等操作，然后在`slave`端查看，会发现已经同步。


---

以上。