package com.aurora.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

import java.util.Collections;

/**
 * Knife4j 接口文档配置
 *
 * 作用：生成在线 API 文档（Swagger UI）
 * 访问地址：http://localhost:8081/doc.html
 *
 * 为什么有它：前后端分离，前端开发需要知道每个接口的 URL、参数、返回结构
 * 通过 @Api(tags)、@ApiOperation、@ApiImplicitParam 注解自动生成文档
 */
@Configuration
@EnableSwagger2WebMvc
public class Knife4jConfig {

    /**
     * 配置 Docket（Swagger 的核心配置对象）
     * 指定：文档范围（com.aurora.controller 包）、主机地址、文档信息
     */
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .protocols(Collections.singleton("https"))   // 文档显示的协议
                .host("https://www.linhaojun.top")          // 文档里的主机地址
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.aurora.controller"))  // 只扫描 controller 包
                .paths(PathSelectors.any())
                .build();
    }

    /**
     * 文档基本信息：标题、描述、联系人、版本
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("aurora文档")
                .description("aurora")
                .contact(new Contact("xcy", "", "1909925152@qq.com"))
                .termsOfServiceUrl("https://www.linhaojun.top/api")
                .version("1.0")
                .build();
    }

}
