# lzx-commons
springboot-mybatis-mysql 框架下的的一套CURD基础框架，支持动态查询修改,简易上手,直接导入jar即可。
框架参照[hsweb](https://github.com/hs-web/hsweb-framework/tree/master/hsweb-commons)，简化代码，可塑性更高；
## 使用方法：
  1. 继承各类接口 
      CustomEntity<PK>
      CustomController<Po extends CustomEntity, PK> 
      CustomService<Po extends CustomEntity, PK> 
      CustomMapper<Po extends CustomEntity, PK>
  2. 注解导入 @Import(LZXAutoConfiguration.class) 
  3. 注意要扫描到jar中的/resources/basicmapper.xml
  4. 按此模板书写创建mapper.后续使用代码生成器。。
  ```xml
  <?xml version="1.0" encoding="UTF-8"?> <!DOCTYPE mapper PUBLIC "-//testMybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.lizhi.mapper.UserMapper">
    <resultMap id="BaseResultMap" type="com.lizhi.bean.User">
        <result column="id" jdbcType="INTEGER" property="id" />
        <result column="username" jdbcType="VARCHAR" property="username" />
        <result column="password" jdbcType="VARCHAR" property="password" />
        <result column="password_salt" jdbcType="VARCHAR" property="salt" />
        <result column="status" jdbcType="INTEGER" property="status" />
    </resultMap>

    <!--用于动态生成sql所需的配置-->
    <sql id="config">
        <bind name="resultMapId" value="'BaseResultMap'"/>
        <bind name="tableName" value="'users'"/>
    </sql>

    <insert id="insert" parameterType="com.lizhi.bean.User">
        <include refid="config"/>
        <include refid="BasicMapper.buildInsertSql"/>
    </insert>

    <insert id="batchInsert" parameterType="com.lizhi.bean.User">
        <include refid="config"/>
        <include refid="BasicMapper.buildBatchInsertSql"/>
    </insert>

    <delete id="deleteByPK" >
        delete from users where id =#{id}
    </delete>

    <select id="selectByPK"  resultMap="BaseResultMap">
		select * from users where id = #{id}
	</select>

    <delete id="delete" parameterType="com.lizhi.bean.User">
        <include refid="config"/>
        <include refid="BasicMapper.buildDeleteSql"/>
    </delete>

    <update id="update" parameterType="com.lizhi.bean.User">
        <include refid="config"/>
        <include refid="BasicMapper.buildUpdateSql"/>
    </update>

    <select id="query" parameterType="com.lizhi.bean.User" resultMap="BaseResultMap">
        <include refid="config"/>
        <include refid="BasicMapper.buildSelectSql"/>
    </select>

    <select id="count" parameterType="com.lizhi.bean.User" resultType="int">
        <include refid="config"/>
        <include refid="BasicMapper.buildTotalSql"/>
    </select>
</mapper>
```
