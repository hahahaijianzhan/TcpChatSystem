本项目使用了JavaSwing 、JDBC、MySql、TCP技术实现
使用的软件：IDEA、Navicat Premium
本项目实现了登录、注册、添加好友、私聊、群聊的功能。有点简陋也有些bug，但是核心功能已经实现了

1、sqlserver.properties文件用于存储连接数据的相关信息
  运行前需要在sqlserver.properties文件中更改自己的数据库连接名，密码
   
2、在MySql数据库中创建一个数据库，名为qqchat
  创建相关表
  在DataBase软件包中含有三个表的.sql文件，里面有创建表的信息，也可以将其导入Navicat Premium中

3、自己去更改PrivateChatFrame和GroupChatFrame中的ip地址和端口号，修改端口号的同时也需要更改
Server类中的端口号，必须使PrivateChatFrame、GroupChatFrame和Server中的端口号一致。
ip地址可以更改为自己的本地ip地址。
4、记得装好JDBC的驱动程序

对Main类进行编辑配置，配置成允许多个实例，这样就可以登录多个客户端

程序的运行：
    先运行Server类再运行Main类


