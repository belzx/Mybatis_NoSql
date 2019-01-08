package com.lizhi.conf;

import com.lizhi.orm.EasyOrmSqlBuilder;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Configuration
@ComponentScan("com.lizhi")
public class ZCommonsAutoConfiguration {
    private final static Logger logger = LoggerFactory.getLogger(ZCommonsAutoConfiguration.class);

    @Resource
    private  SqlSessionFactory sqlSession;

    @Bean
    public EasyOrmSqlBuilder init(){
        logger.warn("zx commons start!!!");
        EasyOrmSqlBuilder.setSqlSession(sqlSession);
        return EasyOrmSqlBuilder.getInstance();
    }
}
