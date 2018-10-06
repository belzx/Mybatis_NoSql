package com.lizhi.conf;

import com.lizhi.builder.EasyOrmSqlBuilder;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Configuration
@ComponentScan("com.lizhi")
@Component
public class LZXAutoConfiguration {

    private final static Logger logger = LoggerFactory.getLogger(LZXAutoConfiguration.class);

    @Resource
    private  SqlSessionFactory sqlSession;

    @Bean
    public  EasyOrmSqlBuilder init(){
        logger.warn("begin to start lzx-commons!!!");
        EasyOrmSqlBuilder.setSqlSession(sqlSession);
        return EasyOrmSqlBuilder.getInstance();
    }
}
