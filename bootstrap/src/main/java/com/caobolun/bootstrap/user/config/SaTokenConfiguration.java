package com.caobolun.bootstrap.user.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import com.caobolun.bootstrap.rag.config.DemoModeInterceptor;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 *  Sa-Token配置
 */
@Configuration
@RequiredArgsConstructor
public class SaTokenConfiguration implements WebMvcConfigurer {

    /**
     * 测试环境只读模式拦截器
     */
    private final DemoModeInterceptor demoModeInterceptor;

    /**
     * 用户上下文拦截器
     */
    private final UserContextInterceptor userContextInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册SaToken登录拦截器
        registry.addInterceptor(new SaInterceptor(handler -> {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if(attributes != null){
                HttpServletRequest request = attributes.getRequest();
                if(request.getDispatcherType() == DispatcherType.ASYNC){
                    return;
                }
                if("OPTIONS".equalsIgnoreCase(request.getMethod())){
                    return;
                }
            }
            StpUtil.checkLogin();}))
                .addPathPatterns("/**") // 拦截所有路径
                .excludePathPatterns("/auth/**", "/error"); // 排除登录认证和报错页面

        // 注册体验环境只读模式拦截器
        registry.addInterceptor(demoModeInterceptor)
                // 拦截所有路径
                .addPathPatterns("/**")
                // 排除认证相关路径和错误页面
                .excludePathPatterns("/auth/**", "/error");

        // 注册用户上下文拦截器
        registry.addInterceptor(userContextInterceptor)
                // 拦截所有路径
                .addPathPatterns("/**")
                // 排除认证相关路径和错误页面
                .excludePathPatterns("/auth/**", "/error");
    }

}
