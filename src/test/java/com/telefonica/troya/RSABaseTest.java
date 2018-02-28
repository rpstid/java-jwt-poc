package com.telefonica.troya;

import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

public class RSABaseTest {

    protected static RSAPublicKey publicKey;
    protected static RSAPrivateKey privateKey;

    @BeforeAll
    protected static void initTests() {
        RsaKeys rsaKeys = new RsaKeys();
        try {
            String publicKeyString = rsaKeys.readFileResourceToString("certs/public_key.pem");
            publicKey = rsaKeys.getPublicKeyFromString(publicKeyString);
            String privateKeyString = rsaKeys.readFileResourceToString("certs/private_key.pem");
            privateKey = rsaKeys.getPrivateKeyFromString(privateKeyString);
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }
    }
}
