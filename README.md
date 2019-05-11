# lzx-commons
##
springboot-mybatis-mysql 框架下的的一个CURD小工具，支持动态查询修改,简易上手,直接导入jar即可。
框架参照[hsweb](https://github.com/hs-web/hsweb-framework/tree/master/hsweb-commons)，

## 使用说明
~~~
已经配置号META-INF
依赖即可使用
~~~
  1. 继承各类接口 
      CustomService<PO>  //PO:为bean，bean如果没有指定主键，则无法使用BYPK后缀名的方法
      CustomMapper<PO>
  2. 注意要扫描到jar中的/resources/basic/BasicMapper.xml
  3. 创建模板mapper

### 配置mapper
```
  <?xml version="1.0" encoding="UTF-8"?>
  <!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
  <mapper namespace="com.lizhi.mapper.BlogLabelMapper">
    <resultMap id="blogResultMap" type="com.lizhi.bean.BlogLabel">
      <!-- <id> 指定主键，没有指定则service无法使用byPk方法-->
      <id column="id" property="id"   javaType="String" jdbcType="VARCHAR"/>
      <result column="labelName" property="labelName"   javaType="String" jdbcType="VARCHAR"/>
      <result column="parentId" property="parentId"   javaType="String" jdbcType="VARCHAR"/>
      <result column="articleId" property="articleId" javaType="String" jdbcType="VARCHAR"/>
    </resultMap>
     <!--用于动态生成sql所需的配置-->
       <sql id="config">
            <!--必须加上'',否则会报错-->
           <bind name="resultMapId" value="'SingleResultMap'"/>
           <bind name="tableName" value="'t_blog_article'"/>
       </sql>
   
       <insert id="insert" parameterType="com.lizhi.orm.param.CreateParam">
           <include refid="config"/>
           <include refid="BasicMapper.buildInsertSql"/>
       </insert>
   
       <delete id="delete" parameterType="com.lizhi.orm.param.DeleteParam">
           <include refid="config"/>
           <include refid="BasicMapper.buildDeleteSql"/>
       </delete>
   
       <update id="update" parameterType="com.lizhi.orm.param.IUpdateParam">
           <include refid="config"/>
           <include refid="BasicMapper.buildUpdateSql"/>
       </update>
   
       <select id="select" resultMap="SingleResultMap">
           <include refid="config"/>
           <include refid="BasicMapper.buildSelectSql"/>
       </select>
   
       <select id="selectMap" parameterType="com.lizhi.orm.param.QueryParam" resultType="java.util.HashMap">
           <include refid="config"/>
           <include refid="BasicMapper.buildSelectSql"/>
       </select>
   
       <select id="count" parameterType="com.lizhi.orm.param.QueryParam" resultType="int">
           <include refid="config"/>
           <include refid="BasicMapper.buildTotalSql"/>
       </select>
  </mapper>
```

  
### 一些简单的使用案例
~~~
//查询：
//select ... where label = "前端"
blogLabelService.select(QueryParam.build().where("labelName", "前端")); 

//select ... where id = "222"
blogLabelService.selectByPK("222");

//select ... where parentId in ("3","5") or labelName in("前端") order by labelName desc
blogLabelService.select(QueryParam.build()
                    .sortDesc("labelName")
                    .and("parentId", Arrays.asList("3","5"))
                    .and("labelName",Arrays.asList("前端")));

blogLabelService.selectByPKS(Arrays.asList("1","2","3"))

//select labelName,count(*) from table group by labelName
blogLabelService.selectByJoin(QueryParam.build().qert().includes("labelName","count(*)").group("labelName"))

//更新：
//update tablename set labelName  = 1k1 where id = "222"
blogLabelService.update(UpdateSetParam.build().set("labelName","lkl").where("id","222"))

//删除
blogLabelService.delete(DeleteParam.build().where("labelName","kk"));

//插入
blogLabelService.insert(arrayList)

~~~
