package com.atguigu.gmall.auth;

import com.atguigu.gmall.common.utils.JwtUtils;
import com.atguigu.gmall.common.utils.RsaUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

public class JwtTest {

    // 别忘了创建D:\\project\rsa目录
	private static final String pubKeyPath = "E:\\code\\project-0522\\rsa\\rsa.pub";
    private static final String priKeyPath = "E:\\code\\project-0522\\rsa\\rsa.pri";

    private PublicKey publicKey;

    private PrivateKey privateKey;

    @Test
    public void testRsa() throws Exception {
        RsaUtils.generateKey(pubKeyPath, priKeyPath, "234");
    }

    @BeforeEach
    public void testGetRsa() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }

    @Test
    public void testGenerateToken() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("id", "11");
        map.put("username", "liuyan");
        // 生成token
        String token = JwtUtils.generateToken(map, privateKey, 2);
        System.out.println("token = " + token);
    }

    @Test
    public void testParseToken() throws Exception {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6IjExIiwidXNlcm5hbWUiOiJsaXV5YW4iLCJleHAiOjE2MDU1NTU5NjF9.Ab1UlIhV8_IXRWnK0dU-w2xO2reH2ThqgklGcUCKBebDDNERNZnNwwrjkgDYV_JczB5V-mx8A-MPHOR3HdeZlGqtEpjKVItr4eJfVVIDDfR4lp4AZz-biMoFfPeS57j8x3xsPVEJbuTCOHg3TJsoO4sS3cWifCrnWS2QV08hQDIAUeBxu6uw0MHIy9mN_EwObjA3VO9JMcnztAxOsR2CMbISmwQAcf3oosYRsD7J4KmhycSSo5fBON1ddZLCO4dZjXUfwRgIcT0y683ECLEgTiGN5nnf6-lP90cmS4hdaAofAiZldirSxs-gx0c7Eyu2CX8r1Xgh963dhGeLcDHsbQ";

        // 解析token
        Map<String, Object> map = JwtUtils.getInfoFromToken(token, publicKey);
        System.out.println("id: " + map.get("id"));
        System.out.println("userName: " + map.get("username"));
    }
}
