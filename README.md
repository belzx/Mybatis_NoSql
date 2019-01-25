# lzx-commons
##
springboot-mybatis-mysql 框架下的的一个CURD小工具，支持动态查询修改,简易上手,直接导入jar即可。
框架参照[hsweb](https://github.com/hs-web/hsweb-framework/tree/master/hsweb-commons)，

## 如何引入
~~~
//springboot
@EnableScheduling
@SpringBootApplication
@Import(ZCommonsAutoConfiguration.class) //扫描到这个class
@ImportResource("classpath:context.xml")
public class Application {
    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(Application.class, args);
    }
}
~~~
  1. 继承各类接口 
      CustomEntity<PK>
      CustomController<Po extends CustomEntity, PK> 
      CustomService<Po extends CustomEntity, PK> 
      CustomMapper<Po extends CustomEntity, PK>
  2. 注解导入 @Import(LZXAutoConfiguration.class) 
  3. 注意要扫描到jar中的/resources/basic/mapper.xml
  4. 创建模板mapper

### 关于mapper如何配置
```
  <?xml version="1.0" encoding="UTF-8"?>
  <!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
  <mapper namespace="com.lizhi.mapper.BlogLabelMapper">
    <resultMap id="blogResultMap" type="com.lizhi.bean.BlogLabel">
      <result column="id" property="id"   javaType="String" jdbcType="VARCHAR"/>
      <result column="labelName" property="labelName"   javaType="String" jdbcType="VARCHAR"/>
      <result column="parentId" property="parentId"   javaType="String" jdbcType="VARCHAR"/>
      <result column="articleId" property="articleId" javaType="String" jdbcType="VARCHAR"/>
    </resultMap>
    <cache></cache>
    <!--用于动态生成sql所需的配置-->
    <sql id="config">
      <bind name="resultMapId" value="'blogResultMap'"/>
      <bind name="tableName" value="'t_blog_label'"/>
    </sql>
  
    <insert id="insert" parameterType="com.lizhi.bean.BlogLabel">
      <include refid="config"/>
      <include refid="BasicMapper.buildInsertSql"/>
    </insert>
  
    <insert id="batchInsert" parameterType="java.util.List">
      <include refid="config"/>
      <include refid="BasicMapper.buildBatchInsertSql"/>
    </insert>
  
    <delete id="deleteByPK" >
          delete from t_blog_label where id =#{id}
      </delete>
  
    <select id="selectByPK"  resultMap="blogResultMap">
  		select * from t_blog_label where id = #{id}
  	</select>
  
      <select id="selectByPKS" parameterType="java.util.List" resultMap="blogResultMap">
          <foreach collection="t_list" index="index" item="id"  separator="union all" >
              select * from t_blog_label where id = #{id}
          </foreach>
      </select>
  
    <delete id="delete" parameterType="com.lizhi.bean.BlogLabel">
      <include refid="config"/>
      <include refid="BasicMapper.buildDeleteSql"/>
    </delete>
  
    <update id="update" parameterType="com.lizhi.bean.BlogLabel">
      <include refid="config"/>
      <include refid="BasicMapper.buildUpdateSql"/>
    </update>
  
    <select id="query" parameterType="com.lizhi.bean.BlogLabel" resultMap="blogResultMap">
      <include refid="config"/>
      <include refid="BasicMapper.buildSelectSql"/>
    </select>
  
    <select id="queryByJoin" parameterType="com.lizhi.bean.BlogLabel" resultType="java.util.HashMap">
      <include refid="config"/>
      <include refid="BasicMapper.buildSelectSql"/>
    </select>
  
    <select id="count" parameterType="com.lizhi.bean.BlogLabel" resultType="int">
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
                    .andIn("parentId", Arrays.asList("3","5"))
                    .orIn("labelName",Arrays.asList("前端")));

blogLabelService.selectByPKS(Arrays.asList("1","2","3"))

//select labelName,count(*) from table group by labelName
blogLabelService.selectByJoin(QueryParam.build().qert().includes("labelName","count(*)").group("labelName"))

//联表查询
QueryJoinParam bt = QueryJoinParam.build("bt");
            bt.includes("id");
            bt.join("t_blog_label_copy", "tbc")
                    .on("id", "id")
                    .joinCludes("id", "ttid")
                    .group("labelName")
                    .limit(0,20);
                    .where("labelName", "前端");
blogLabelService.select(bt);

//更新：
//update tablename set labelName  = 1k1 where id = "222"
blogLabelService.update(UpdateParam.build().set("labelName","lkl").where("id","222"))

//删除
blogLabelService.delete(DeleteParam.build().where("labelName","kk"));

//插入
blogLabelService.insert(arrayList)

~~~
