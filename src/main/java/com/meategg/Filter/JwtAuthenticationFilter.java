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
       String jwt=request.getHeader("Authorization");
       if (jwtUtils.isExpire(jwt)) {
           response.setStatus(401);
           return;
       }
       String username=null;
       String token=null;
       if (request.getHeader("Authorization") != null && jwt.startsWith("Bearer")) {
             token=request.getHeader("Authorization").substring(7);
             username=jwtUtils.getUsername(token);
       }
       if(username!=null&&!jwtUtils.isExpire( token))
       {request.setAttribute("username",username);}


        filterChain.doFilter(request, response);
    }

}
