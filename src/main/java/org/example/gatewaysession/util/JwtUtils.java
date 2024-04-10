package org.example.gatewaysession.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class JwtUtils {
    private static final long EXPIRE_TIME = 60 * 60 * 1000 * 24;

    private static String TOKEN_SECRET = "jwt-secret-key";


    // 该方法使用HS256算法和Secret:bankgl生成signKey
    /** 生成JWT **/
    public static String createToken(String dataKey, String dataValue, String secret){
        try {
            // 设置过期时间
            Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
            // 私钥和加密算法
            Algorithm algorithm = Algorithm.HMAC256(secret);
            // 设置头部信息
            Map<String, Object> header = new HashMap<>(2);
            header.put("Type", "Jwt");
            header.put("alg", "HS256");
            // 返回token字符串
            return JWT.create()
                    .withHeader(header)
                    .withClaim(dataKey, dataValue)
                    .withExpiresAt(date)
                    .sign(algorithm);
        } catch (Exception ignored) {
            return null;
        }
    }

    /**
     * 检验token是否正确
     */
    public static String verifyToken(String jwtStr, String JwtKey, String secret){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(jwtStr);
            return jwt.getClaim(JwtKey).asString();
        } catch (Exception e){
            log.error("JwtUtils verifyToken error:{}", e.getMessage());
            return "";
        }
    }
}
