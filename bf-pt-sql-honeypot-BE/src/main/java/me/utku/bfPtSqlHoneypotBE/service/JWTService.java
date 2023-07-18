package me.utku.bfPtSqlHoneypotBE.service;

import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.keys.HmacKey;
import org.jose4j.lang.JoseException;
import org.springframework.stereotype.Service;


@Service
public class JWTService {
    public String generateJWT() throws JoseException {
        byte[] key = "f5ceea00600987dee67e0206b53ad9b42155bc7c038f8dc4e04550b205180d87".getBytes();
        HmacKey hmacKey = new HmacKey(key);

        JwtClaims jwtClaims = new JwtClaims();
        jwtClaims.setIssuedAtToNow();  // set iat
        jwtClaims.setExpirationTimeMinutesInTheFuture(10080); // set exp

        JsonWebSignature jws = new JsonWebSignature();
        // Set alg header as HMAC_SHA256
        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.HMAC_SHA256);
        // Set key to hmacKey
        jws.setKey(hmacKey);
        jws.setPayload(jwtClaims.toJson());

        String jwt = jws.getCompactSerialization(); //produce eyJ.. JWT
        return jwt;
    }
}
