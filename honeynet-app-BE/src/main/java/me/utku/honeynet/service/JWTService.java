package me.utku.honeynet.service;

import lombok.extern.slf4j.Slf4j;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.keys.HmacKey;
import org.jose4j.lang.JoseException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JWTService {
    public String generateJWT() throws JoseException {
        byte[] key = "f5ceea00600987dee67e0206b53ad9b42155bc7c038f8dc4e04550b205180d87".getBytes();
        HmacKey hmacKey = new HmacKey(key);

        JwtClaims jwtClaims = new JwtClaims();
        jwtClaims.setIssuedAtToNow();
        jwtClaims.setExpirationTimeMinutesInTheFuture(10080);

        JsonWebSignature jws = new JsonWebSignature();
        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.HMAC_SHA256);
        jws.setKey(hmacKey);
        jws.setPayload(jwtClaims.toJson());

        String jwt = jws.getCompactSerialization();
        return jwt;
    }

    public boolean validateJWT(String jwt) {
        byte[] key = "f5ceea00600987dee67e0206b53ad9b42155bc7c038f8dc4e04550b205180d87".getBytes();
        HmacKey hmacKey = new HmacKey(key);

        JwtConsumer jwtConsumer = new JwtConsumerBuilder()
            .setRequireIssuedAt().setRequireExpirationTime()
            .setVerificationKey(hmacKey).build();

        try{
            jwtConsumer.processToClaims(jwt);
            return true;
        }catch (InvalidJwtException error){
            log.error("JWT is invalid: {}", error.getMessage());
            return false;
        }
    }
}
