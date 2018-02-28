package com.telefonica.troya;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JwtTest extends RSABaseTest {

    @Test
    void testJWT() throws JOSEException, ParseException {
        // Create RSA-signer with the private key
        JWSSigner signer = new RSASSASigner(privateKey);

        //JWSHeader header = new JWSHeader(JWSAlgorithm.RS256);
        JWSHeader header = new JWSHeader(
                JWSAlgorithm.RS256, JOSEObjectType.JWT,
                null, null, null,  null, null, null, null, null, null, null, null
        );
        // Prepare JWT with claims set
        // {"sub":"alice","iss":"https:\/\/c2id.com","exp":1519494512}
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject("user-id")
                .issuer("https://c2id.com")
                .audience("https://auth.default-aws-nightly.e2etest.baikalplatform.com/")
                .claim("scope", "SCOPE")
                .issueTime(new Date())
                .expirationTime(new Date(new Date().getTime() + 60 * 1000))
                .build();
        SignedJWT signedJWT = new SignedJWT(header, claimsSet);

        // Compute the RSA signature
        signedJWT.sign(signer);
        String s = signedJWT.serialize();
        System.out.println(s);

        // On the consumer side, parse the JWS and verify its RSA signature
        signedJWT = SignedJWT.parse(s);
        System.out.println(signedJWT.getHeader());
        System.out.println(signedJWT.getJWTClaimsSet());
        System.out.println("issued: " + signedJWT.getJWTClaimsSet().getIssueTime());
        System.out.println("expires: " + signedJWT.getJWTClaimsSet().getExpirationTime());

        JWSVerifier verifier = new RSASSAVerifier(publicKey);
        assertTrue(signedJWT.verify(verifier));

        // Retrieve / verify the JWT claims according to the app requirements
        assertEquals("user-id", signedJWT.getJWTClaimsSet().getSubject());
        assertEquals("https://c2id.com", signedJWT.getJWTClaimsSet().getIssuer());
        assertTrue(new Date().before(signedJWT.getJWTClaimsSet().getExpirationTime()));
    }
}