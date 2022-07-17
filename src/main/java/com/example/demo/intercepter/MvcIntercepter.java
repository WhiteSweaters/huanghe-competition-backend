package com.example.demo.intercepter;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Component
@PropertySource("classpath:jwt.properties")
public class MvcIntercepter implements HandlerInterceptor {

    @Value("${jwt.secret}")
    private String secret;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * token拦截器
     *
     * @param request 请求
     * @param response 响应
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            return false;
        }
        Map<String, Object> body = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        long id = (long) body.get("id");
//        检查redis中是否存有相应的token
        String tokenInRedis = (String) redisTemplate.opsForValue().get("user" + id);
        return tokenInRedis != null && tokenInRedis.equals(token);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
