package com.telefonica.troya;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.Test;

import javax.crypto.Cipher;
import java.security.*;
import java.text.ParseException;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class RsaKeysTest extends RSABaseTest {

    @Test
    void testEncryptDecrypt() throws GeneralSecurityException {

        String message = "Hello!!!";
        // Encrypt using private key
        Cipher encrypter = Cipher.getInstance("RSA");
        encrypter.init(Cipher.ENCRYPT_MODE, privateKey);
        byte[] encryptedMessage = encrypter.doFinal(message.getBytes());
        assertNotEquals(message, new String(encryptedMessage));

        // Decrypt using public key
        Cipher decrypter = Cipher.getInstance("RSA");
        decrypter.init(Cipher.DECRYPT_MODE, publicKey);
        byte[] decryptedMessage = decrypter.doFinal(encryptedMessage);

        assertEquals(message, new String(decryptedMessage));
    }

    @Test
    void testSignVerify() throws GeneralSecurityException {
        // get the signature of the message (sigBytes) using the privateKey
        String messageString = "Hello!!!";
        Signature signature = Signature.getInstance("SHA1withRSA");
        signature.initSign(privateKey, new SecureRandom());
        signature.update(messageString.getBytes());
        byte[] sigBytes = signature.sign();

        // verify the signature using the public key
        Signature signature1 = Signature.getInstance("SHA1withRSA");
        signature1.initVerify(publicKey);
        signature1.update(messageString.getBytes());
        assertTrue(signature1.verify(sigBytes));

        // verify the signature with an altered message
        String messageString2 = "Hell0!!!";
        Signature signature2 = Signature.getInstance("SHA1withRSA");
        signature2.initVerify(publicKey);
        signature2.update(messageString2.getBytes());
        assertFalse(signature2.verify(sigBytes));
    }
}