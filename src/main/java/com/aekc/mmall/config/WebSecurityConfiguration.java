package com.aekc.mmall.config;

import com.aekc.mmall.security.*;
import com.aekc.mmall.security.exception.MyAccessDeniedHandler;
import com.aekc.mmall.security.exception.MyAuthenticationEntryPoint;
import com.aekc.mmall.security.login.MyAuthenticationFailHandler;
import com.aekc.mmall.security.login.MyAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsUtils;

@SpringBootConfiguration
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private MyAuthenticationSuccessHandler myAuthenticationSuccessHandler;

    @Autowired
    private MyAuthenticationFailHandler myAuthenticationFailHandler;

    @Autowired
    private MyFilterSecurityInterceptor myFilterSecurityInterceptor;

    @Bean
    UserDetailsService customUserService() {
        return new MyCustomUserService();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 使用自定义UserDetailsService
        auth.userDetailsService(customUserService()).passwordEncoder(new BCryptPasswordEncoder());
    }


    @Autowired
    private MyAuthenticationEntryPoint myAuthenticationEntryPoint;

    @Autowired
    private MyAccessDeniedHandler myAccessDeniedHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin()
                .loginProcessingUrl("/user/login")
                //　自定义的登录验证成功或失败后的去向
                .successHandler(myAuthenticationSuccessHandler).failureHandler(myAuthenticationFailHandler)
                // 每个子匹配器将会按照声明的顺序起作用(不会拦截/user/login登录请求)
                .and().authorizeRequests()
                // PreflightRequest请求不做拦截(OPTIONS)
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                // 其他请求，用户被验证(就是已经登录即可)
                //.antMatchers("/sys/acl/save").hasRole("SYS")
                //.anyRequest().authenticated()
                // 禁用csrf防御机制(跨域请求伪造)，这么做在测试和开发会比较方便。
                .and().csrf().disable();
        http.addFilterBefore(myFilterSecurityInterceptor, FilterSecurityInterceptor.class);
        http.addFilter(myUsernamePasswordAuthenticationFilter());
        //http.addFilterAfter(new MyUsernamePasswordAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        http.exceptionHandling().authenticationEntryPoint(myAuthenticationEntryPoint).accessDeniedHandler(myAccessDeniedHandler);
    }

    @Bean
    public MyUsernamePasswordAuthenticationFilter myUsernamePasswordAuthenticationFilter() throws Exception {
        MyUsernamePasswordAuthenticationFilter myUsernamePasswordAuthenticationFilter = new MyUsernamePasswordAuthenticationFilter();
        myUsernamePasswordAuthenticationFilter.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/user/login", "POST"));
        myUsernamePasswordAuthenticationFilter.setAuthenticationManager(authenticationManager());
        //自定义UsernamePasswordAuthenticationFilter就会导致原来configure中的.successHandler和.failureHandler失效
        myUsernamePasswordAuthenticationFilter.setAuthenticationSuccessHandler(myAuthenticationSuccessHandler);
        myUsernamePasswordAuthenticationFilter.setAuthenticationFailureHandler(myAuthenticationFailHandler);
        return myUsernamePasswordAuthenticationFilter;
    }
}
