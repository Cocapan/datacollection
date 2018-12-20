## Build Setup

``` bash
#正式环境打包命令
mvn clean package -DskipTests -Ppro

#测试环境打包命令
mvn clean package -DskipTests -Ptest

#开发环境打包命令
mvn clean package -DskipTests -Pdev  
```