package com.telefonica.troya;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import java.util.Base64;

public class RsaKeys {

    private static Base64.Decoder base64decoder = Base64.getDecoder();
    private static Base64.Encoder base64encoder = Base64.getEncoder();

    public String readFileResourceToString(String pemFileName) throws IOException {
        URL resourceUrl = getClass().getClassLoader().getResource(pemFileName);
        if (resourceUrl != null) {
            String fileName = resourceUrl.getFile();
            return readFileToString(fileName);
        } else {
            throw new IOException("File " + pemFileName + " not found in resources");
        }
    }

    public String readFileToString(String fileName) throws IOException {
        StringBuilder strKeyPEM = new StringBuilder();
        FileReader fr = new FileReader(fileName);
        BufferedReader br = new BufferedReader(fr);
        String line;
        while ((line = br.readLine()) != null) {
            strKeyPEM.append(line).append("\n");
        }
        br.close();
        fr.close();
        return strKeyPEM.toString();
    }

    public RSAPublicKey getPublicKeyFromString(String key) throws GeneralSecurityException {
        String publicKeyPEM = key;
        publicKeyPEM = publicKeyPEM.replace("-----BEGIN PUBLIC KEY-----\n", "");
        publicKeyPEM = publicKeyPEM.replace("-----END PUBLIC KEY-----", "");
        publicKeyPEM = publicKeyPEM.replaceAll("\n", "");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(base64decoder.decode(publicKeyPEM));
        return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(keySpec);
    }

    public RSAPrivateKey getPrivateKeyFromString(String key) throws GeneralSecurityException {
        String privateKeyPEM = key;
        privateKeyPEM = privateKeyPEM.replace("-----BEGIN PRIVATE KEY-----\n", "");
        privateKeyPEM = privateKeyPEM.replace("-----END PRIVATE KEY-----", "");
        privateKeyPEM = privateKeyPEM.replaceAll("\n", "");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(base64decoder.decode(privateKeyPEM));
        return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(keySpec);
    }

    static private void printKeyInfo(Key key) {
        System.out.println(key.toString());
        byte[] keyEncoded = key.getEncoded();
        String base64 = base64encoder.encodeToString(keyEncoded);
        System.out.format("base64:\n%s\n", base64.replaceAll("(.{64})", "$1\n"));
        System.out.format("algorithm: %s, format: %s\n", key.getAlgorithm(), key.getFormat());
    }

    public static void main(String [] args) {
        try {
            //KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            //keyGen.initialize(1024);
            //KeyPair kp = keyGen.genKeyPair();
            //RSAPublicKey publicKey = (RSAPublicKey) kp.getPublic();
            //RSAPrivateKey privateKey = (RSAPrivateKey) kp.getPrivate();

            RsaKeys rsaKeys = new RsaKeys();

            String privateKeyString = rsaKeys.readFileToString("/home/rps/scripts/rsa/private_key.pem");
            RSAPrivateKey privateKey = rsaKeys.getPrivateKeyFromString(privateKeyString);
            printKeyInfo(privateKey);
            //System.out.println("exponent: " + privateKey.getPrivateExponent());
            System.out.println("  modulus: " + privateKey.getModulus());

            System.out.println();

            String publicKeyString = rsaKeys.readFileResourceToString("certs/public_key.pem");
            RSAPublicKey publicKey = rsaKeys.getPublicKeyFromString(publicKeyString);
            printKeyInfo(publicKey);
            //System.out.println("exponent: " + publicKey.getPublicExponent());
            //System.out.println("modulus: " + publicKey.getModulus());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
