# sql-tools
    sql解析工具基于druid的小工具,能够根据参数动态的剔除sql语句中的条件.
## 初步完成  目前只支持mysql
1. 支持SELECT 语句 92 语法和99语法
2. 支持UPDATE 语句
3. 支持DELETE 语句
3. 支持INSERT INTO 语句
***
       反正自己测试通过了,关于性能问题,后面在进行优化
***
       
## 使用方法        
    例如有这样的sql语句:select * from ccp_mem where Name = ? and Age = ?
    请看代码:
```
     public void test(){
            String sql = "select * from ccp_mem where Name = ? and Age = ?";
            List<String> para = new ArrayList<>();
            para.add("Name");
            String s = ZSQLUtils.mysqSqlFilter(sql, para);
            System.out.println(s);
        }     
```
    结果如下;
```
        SELECT *
        FROM ccp_mem
        WHERE  Name = ? 
```    
