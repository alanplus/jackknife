/*
 * Copyright (C) 2020 The JackKnife Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lwh.jackknife.util;

import android.content.Context;
import android.util.Base64;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    // <editor-folder desc="DES加密">

    public static String encryptDES(byte[] key, String encryptString) throws Exception {
        IvParameterSpec zeroIv = new IvParameterSpec(new byte[8]);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "DES");
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, zeroIv);
        byte[] encryptedData = cipher.doFinal(encryptString.getBytes());
        return Math.bs2H(encryptedData);
    }

    public static String decryptDES(byte[] key, String decryptString) throws Exception {
        if (TextUtils.isNotEmpty(decryptString)) {
            byte[] byteMi = Math.H2bs(decryptString, "");
            IvParameterSpec zeroIv = new IvParameterSpec(new byte[8]);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "DES");
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, zeroIv);
            byte decryptedData[] = cipher.doFinal(byteMi);
            return new String(decryptedData);
        }
        return "";
    }

    // </editor-folder>

    // <editor-folder desc="RSA加密">

    private final static String RSA_PUBLIC = "rsa_public_key.pem";

    private static PublicKey getPublicKeyFromX509(String pubKeyString) {
        try {
            byte[] decodeKey = Base64.decode(pubKeyString, Base64.NO_WRAP);
            X509EncodedKeySpec x509 = new X509EncodedKeySpec(decodeKey);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(x509);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getPublicKeyFromAssets(Context context) {
        return getPublicKeyFromAssets(context, RSA_PUBLIC);
    }

    public static String getPublicKeyFromAssets(Context context, String pem) {
        InputStreamReader inputStreamReader;
        StringBuilder Result = new StringBuilder();
        try {
            inputStreamReader = new InputStreamReader(context
                    .getResources().getAssets().open(pem));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                if (line.charAt(0) == '-') {
                    continue;
                }
                Result.append(line);
            }
            IoUtils.close(inputStreamReader);
            IoUtils.close(bufferedReader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Result.toString();
    }

    public static String encryptByPublic(String pubKeyString, String content) {
        try {
            PublicKey publicKey = getPublicKeyFromX509(pubKeyString);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] plaintext = content.getBytes();
            byte[] output = cipher.doFinal(plaintext);
            return Base64.encodeToString(output, Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // </editor-folder>
}
