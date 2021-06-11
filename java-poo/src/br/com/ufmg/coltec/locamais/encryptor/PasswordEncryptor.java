package br.com.ufmg.coltec.locamais.encryptor;


import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.HashMap;
import java.util.Map;

public class PasswordEncryptor {

    public static final String SALT_KEY = "PRIVATE_SALT";
    public static final String PASSWORD_KEY = "ENCRYPTED_PASSWORD";
    public static final String TOKEN_KEY = "PRIVATE_TOKEN";

    private static final int iterations = 500000;

    private PasswordEncryptor() {
    }
    
    public static Map<String, String> encrypt(String password) {
        final String privateSalt = RandomStringUtils.randomAscii(50);
        password = Constants.PUBLIC_SALT + password;

        for (int i = 0; i < iterations; i++) {
            password = DigestUtils.sha384Hex(password);
        }

        final String finalToken = DigestUtils.sha384Hex(password + privateSalt);
        final String finalPassword = password;


        return new HashMap<String, String>() {
            private static final long serialVersionUID = 1L;

            {
                put(SALT_KEY, privateSalt);
                put(PASSWORD_KEY, finalPassword);
                put(TOKEN_KEY, finalToken);
            }
        };
    }

}