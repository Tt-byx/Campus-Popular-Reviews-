package com.meategg.Filter;

import com.meategg.Utils.JwtUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Resource
    private JwtUtils jwtUtils;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String username = resolveUsername(request);
        if (username != null) {
            request.setAttribute("username", username);
            filterChain.doFilter(request, response);
            return;
        }

        String path = request.getRequestURI();
        if (isPublicPath(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (isApiRequest(request)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"请先登录\"}");
            return;
        }

        String next = path;
        String query = request.getQueryString();
        if (query != null && !query.trim().isEmpty()) {
            next = next + "?" + query;
        }
        String redirectUrl = "/login?next=" + URLEncoder.encode(next, StandardCharsets.UTF_8.name());
        response.sendRedirect(redirectUrl);
    }

    private String resolveUsername(HttpServletRequest request) {
        String jwt = resolveToken(request);
        if (jwt == null || jwt.trim().isEmpty()) {
            return null;
        }
        if (jwt.startsWith("Bearer ")) {
            jwt = jwt.substring(7);
        }
        try {
            if (jwtUtils.isExpire(jwt)) {
                return null;
            }
            return jwtUtils.getUsername(jwt);
        } catch (Exception ignored) {
            return null;
        }
    }

    private boolean isPublicPath(String path) {
        return "/".equals(path)
                || "/login".equals(path)
                || "/register".equals(path)
                || "/user/login".equals(path)
                || "/user/create".equals(path)
                ||"/post-detail".equals(path)
                || "/error".equals(path)
                || "/favicon.ico".equals(path)
                || path.startsWith("/css/")
                || path.startsWith("/js/")
                || path.startsWith("/images/")
                || path.startsWith("/webjars/")
                || path.startsWith("/uploads/");
    }

    private boolean isApiRequest(HttpServletRequest request) {
        String path = request.getRequestURI();
        if ("/post".equals(path)) {
        return "POST".equals(request.getMethod());
    }
    if (path.startsWith("/api/") || path.startsWith("/user/") || path.startsWith("/post/")) {
        return true;
    }
    String accept = request.getHeader("Accept");
    if (accept != null && accept.contains("application/json")) {
        return true;
    }
    String requestedWith = request.getHeader("X-Requested-With");
    return "XMLHttpRequest".equalsIgnoreCase(requestedWith);
}

    private String resolveToken(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (auth != null && !auth.trim().isEmpty()) {
            return auth.trim();
        }
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if ("token".equals(cookie.getName()) && cookie.getValue() != null && !cookie.getValue().trim().isEmpty()) {
                try {
                    return URLDecoder.decode(cookie.getValue().trim(), StandardCharsets.UTF_8.name());
                } catch (Exception ignored) {
                    return cookie.getValue().trim();
                }
            }
        }
        return null;
    }
}
