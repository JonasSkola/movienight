package com.jonas.movienight.security;

/**
 * Created by Jonas Karlsson on 2019-01-18.
 */
public class SecurityConstants {

    public static final String SIGN_UP_URLS = "/user/**";
    public static final String H2_URL = "/h2/**";
    public static final String SECRET = "jf9i4jgu83nfl0";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final long EXPIRATION_TIME = 300000;

}
