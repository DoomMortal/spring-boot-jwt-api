package com.example.demo.config

import com.example.demo.filters.AuthFilter
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter


@Configuration
class MyConfig {

    @Bean
    fun corsFilter(): FilterRegistrationBean<CorsFilter>? {
        val registrationBean: FilterRegistrationBean<CorsFilter> = FilterRegistrationBean<CorsFilter>()
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        config.addAllowedOrigin("*")
        config.addAllowedHeader("*")
        source.registerCorsConfiguration("/**", config)
        registrationBean.setFilter(CorsFilter(source))
        registrationBean.setOrder(0)
        return registrationBean
    }

    @Bean
    fun filterRegistrationBean(): FilterRegistrationBean<AuthFilter> {
        val registrationBean = FilterRegistrationBean<AuthFilter>()
        val authFilter = AuthFilter()
        registrationBean.filter = authFilter
        registrationBean.addUrlPatterns("/api/categories/*")
        return registrationBean
    }

}