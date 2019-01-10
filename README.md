# lzx-commons
## 如何使用
@Import(ZCommonsAutoConfiguration.class) 
~~~
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
##
springboot-mybatis-mysql 框架下的的一套CURD基础框架，支持动态查询修改,简易上手,直接导入jar即可。
框架参照[hsweb](https://github.com/hs-web/hsweb-framework/tree/master/hsweb-commons)，简化代码，可塑性更高；
## 使用方法：
  1. 继承各类接口 
      CustomEntity<PK>
      CustomController<Po extends CustomEntity, PK> 
      CustomService<Po extends CustomEntity, PK> 
      CustomMapper<Po extends CustomEntity, PK>
  2. 注解导入 @Import(LZXAutoConfiguration.class) 
  3. 注意要扫描到jar中的/resources/basic/mapper.xml
  4. 按此模板创建mapper
  ```xml
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
