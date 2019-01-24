package com.lizhi.conf;

import com.lizhi.orm.EasyOrmSqlBuilder;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @Author https://github.com/lizhixiong1994
 */
@Component
@Configuration
@ComponentScan("com.lizhi")
public class ZCommonsAutoConfiguration {
    private final static Logger logger = LoggerFactory.getLogger(ZCommonsAutoConfiguration.class);

    @Bean
    public EasyOrmSqlBuilder init(@Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory){
        logger.warn("zx commons start!!!");
        EasyOrmSqlBuilder.setSqlSessionFactory(sqlSessionFactory);
        return EasyOrmSqlBuilder.getInstance();
    }
}
