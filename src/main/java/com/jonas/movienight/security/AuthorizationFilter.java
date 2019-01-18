package com.jonas.movienight.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.jonas.movienight.security.SecurityConstants.HEADER_STRING;
import static com.jonas.movienight.security.SecurityConstants.SECRET;
import static com.jonas.movienight.security.SecurityConstants.TOKEN_PREFIX;

/**
 * Created by Jonas Karlsson on 2019-01-18.
 */
public class AuthorizationFilter extends BasicAuthenticationFilter {


    public AuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String header = request.getHeader(HEADER_STRING);

        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authenticationToken = getAuthentication(request);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {

        String token = request.getHeader(HEADER_STRING);

        if (token != null) {

            token = token.replace(TOKEN_PREFIX, "");

            Claims claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();

            String user = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token)
                    .getBody()
            .getSubject();


            if (user != null) {
                return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());

            }

            return null;
        }
        return null;


    }
}