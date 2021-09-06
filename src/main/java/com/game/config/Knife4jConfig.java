package com.game.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * @author lzh
 * @Title:
 * @Package
 * @Description: knife4j 配置文件
 * @date 2021/8/27 22:09
 */
@Configuration
@EnableKnife4j
public class Knife4jConfig {
    @Bean(value = "knife4jApi")
    public Docket knife4jApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfoBuilder()
                        .title("Oasis 2200")
                        .description("Restful api document for game Oasis 2200")
                        .version("1.0")
                        .build())
                .groupName("Oasis 2200")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.game"))
                .paths(PathSelectors.any())
                .build();
    }
}
