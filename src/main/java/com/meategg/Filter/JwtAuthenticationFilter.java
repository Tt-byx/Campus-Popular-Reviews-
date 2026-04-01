package com.meategg.Filter;

import com.meategg.Utils.JwtUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Resource
    private JwtUtils jwtUtils;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
       String jwt = request.getHeader("Authorization");
       
       if (jwt == null || jwt.trim().isEmpty()) {
           filterChain.doFilter(request, response);
           return;
       }
       
       if (jwt.startsWith("Bearer")) {
           jwt = jwt.substring(7);
       }
       
       try {
           if (jwtUtils.isExpire(jwt)) {
               response.setStatus(401);
               response.getWriter().write("Token 已过期");
               return;
           }
       } catch (Exception e) {
           response.setStatus(401);
           response.getWriter().write("无效的 Token");
           return;
       }
       
       String username = jwtUtils.getUsername(jwt);
       if (username != null) {
           request.setAttribute("username", username);
       }
        
        filterChain.doFilter(request, response);
    }
}
