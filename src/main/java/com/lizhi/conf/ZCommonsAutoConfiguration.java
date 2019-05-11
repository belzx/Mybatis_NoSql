package com.lizhi.conf;

import com.lizhi.orm.SqlBuilder;
import com.lizhi.orm.SqlGenerator;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @Author https://github.com/lizhixiong1994
 */
@Configuration
@ConditionalOnClass({SqlSessionFactory.class})// 注：当SqlSessionFactory.class 存在的情况下，该配置路径才生效
@ComponentScan("com.lizhi")
public class ZCommonsAutoConfiguration {

    private final static Logger logger = LoggerFactory.getLogger(ZCommonsAutoConfiguration.class);

    @Bean
    @ConditionalOnMissingBean(SqlBuilder.class)// 注：当EasyOrmSqlBuilder 不存在的情况下，生效
    public SqlBuilder init(SqlSessionFactory sqlSessionFactory) {
        logger.info("ZCommonsAutoConfiguration  start!!!");
        SqlGenerator.instance().init(sqlSessionFactory);
        return SqlBuilder.getInstance();
    }
}
