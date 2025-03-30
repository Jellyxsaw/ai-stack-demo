package com.example.demo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);

    // 定義不需要過濾的路徑列表（使用常量以保持一致性）
    private static final List<String> EXCLUDED_PATHS = Arrays.asList(
        "/api/v1/auth/",
        "/v1/auth/",
        "/api-docs",
        "/api-docs/",
        "/v3/api-docs",
        "/v3/api-docs/",
        "/v2/api-docs",
        "/v2/api-docs/",
        "/swagger-ui/",
        "/swagger-ui.html",
        "/swagger-resources/",
        "/webjars/",
        "/favicon.ico",
        "/error",
        "/actuator/"
    );

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        logger.debug("檢查路徑: " + path);
        
        // 優化的路徑匹配邏輯
        boolean shouldExclude = EXCLUDED_PATHS.stream()
                .anyMatch(excludedPath -> {
                    // 精確匹配
                    if (path.equals(excludedPath)) {
                        return true;
                    }
                    // 如果排除路徑以 / 結尾，則檢查前綴
                    if (excludedPath.endsWith("/")) {
                        return path.startsWith(excludedPath);
                    }
                    // 對於不以 / 結尾的路徑，檢查精確匹配或以該路徑後跟 / 開頭
                    return path.equals(excludedPath) || path.startsWith(excludedPath + "/");
                });
        
        logger.debug("路徑 " + path + " 是否排除過濾: " + shouldExclude);
        return shouldExclude;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request, 
            @NonNull HttpServletResponse response, 
            @NonNull FilterChain chain)
            throws ServletException, IOException {
    
        // 如果路徑不需要過濾，直接放行
        if (shouldNotFilter(request)) {
            chain.doFilter(request, response);
            return;
        }
    
        String path = request.getServletPath();
        
        // 针对健康检查请求特殊处理，不记录详细日志
        if (path.equals("/actuator/health")) {
            chain.doFilter(request, response);
            return;
        }
        
        logger.debug("JwtRequestFilter 處理請求路徑: " + path);
    
        // 记录所有请求头，不仅限于调试模式
        logger.info("===== 請求頭信息開始 =====");
        java.util.Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            logger.info(headerName + ": " + request.getHeader(headerName));
        }
        logger.info("===== 請求頭信息結束 =====");
        
        final String authorizationHeader = request.getHeader("Authorization");
    
        String username = null;
        String jwt = null;
    
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtTokenUtil.extractUsername(jwt);
                logger.debug("提取的用戶名: " + username);
            } catch (Exception e) {
                logger.error("JWT 驗證失敗: " + e.getMessage(), e);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT Token");
                return;
            }
        } else {
            logger.warn("授權頭不存在或格式不正確");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authorization header is missing or invalid");
            return;
        }
    
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
    
            if (jwtTokenUtil.validateToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // 創建新的 SecurityContext 並設置認證信息
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(authenticationToken);
                SecurityContextHolder.setContext(context);
                
                logger.debug("JWT Token 驗證成功，用戶: " + username);
            } else {
                logger.warn("JWT Token 驗證失敗，用戶: " + username);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT Token");
                return;
            }
        }
    
        chain.doFilter(request, response);
    }
}