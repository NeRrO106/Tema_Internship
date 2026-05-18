package com.internship.documentmanagement.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import jakarta.servlet.Filter;

@Component
public class RateLimitingFilter implements Filter {

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    private Bucket createNewBucket(){
        return Bucket.builder()
                .addLimit(
                        Bandwidth.classic(
                                5,
                                Refill.intervally(
                                        5,
                                        Duration.ofMinutes(1)
                                )
                        )
                )
                .build();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException{
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if ("/api/auth/login".equals(httpRequest.getRequestURI()) && "POST".equalsIgnoreCase(httpRequest.getMethod())) {
            String ip = httpRequest.getRemoteAddr();
            Bucket bucket = cache.computeIfAbsent(ip, k-> createNewBucket());

            if(!bucket.tryConsume(1)){
                httpResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                httpResponse.setContentType("application/json");
                httpResponse.getWriter().write("{\"error\": \"Too many login attempts. Please try again in 1 minute.\"}");
                return;
            }
        }
        chain.doFilter(request, response);
    }
}
