本项目的Client.java和ClientBackground.java源文件在余子洵同学的机器上编译不通过，
环境：
Kubuntu Linux 14.04
jdk 1.7.0_65
在另外两位同学的机器上能编译通过,
环境：
Windows 7
jdk 1.8.0_25

数据库配置文件：
本项目使用MySQL，请在Server.java的第11行相应位置填入适合的MySQL账户参数。

编译及运行：
先编译除了Client.java和ClientBackground.java的源程序，
完成后，在A机器上运行lab02/Server.class。
之后编译Client.java和ClientBackground.java，
注意先将A机器的IP地址填入ClientBackground.java第125行相应位置再编译。
之后运行lab02/Client.class。

