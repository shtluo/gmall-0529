package comm.atguigu.test;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.Test;

import javax.crypto.SecretKey;
import java.util.HashMap;
import java.util.Map;

public class JwtTest {
    private String key = "asdfghgxczxxcvgawterytjhvcbxvdfsdgfsdfgvcxvzcXgd";
    @Test
    public void createjwt(){
        Map<String, Object> user = new HashMap<>();
        user.put("id","123456");
        user.put("userName","张三");
        SecretKey secretKey = Keys.hmacShaKeyFor(key.getBytes());
        String string = Jwts.builder()
                .addClaims(user)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
        System.out.println(string);
        //eyJhbGciOiJIUzI1NiJ9
        // .eyJpZCI6IjEyMzQ1NiIsInVzZXJOYW1lIjoi5byg5LiJIn0
        // .Wsc7sWpsW-pkv-NWi3yLb3gcwZifV0VyRRJKglP9I1I

    }

    @Test
    public void verf(){
        String s = "eyJhbGciOiJIUzI1NiJ9.eyJpZCI6IjEyMzQ1NiIsInVzZXJOYW1lIjoi5byg5LiJIn0.Wsc7sWpsW-pkv-NWi3yLb3gcwZifV0VyRRJKglP9I1I";
        SecretKey secretKey = Keys.hmacShaKeyFor(key.getBytes());
        Object body = Jwts.parser().setSigningKey(secretKey).parse(s).getBody();
        System.out.println(body);
    }
}
