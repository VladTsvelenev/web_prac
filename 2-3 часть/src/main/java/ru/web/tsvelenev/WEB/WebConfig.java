package ru.web.tsvelenev.WEB;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public ClassLoaderTemplateResolver templateResolver() {
        var templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML");
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setCacheable(false); // отключить кэш на время разработки
        return templateResolver;
    }

    @Bean
    public ViewResolver thymeleafViewResolver(SpringTemplateEngine templateEngine) {
        var resolver = new ThymeleafViewResolver();
        resolver.setTemplateEngine(templateEngine); // бин уже будет создан Spring Boot автоматически
        resolver.setCharacterEncoding("UTF-8");
        return resolver;
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("index");
    }
}
