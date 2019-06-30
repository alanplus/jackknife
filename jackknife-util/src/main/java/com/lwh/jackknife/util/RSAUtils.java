package com.lwh.jackknife.util;

import android.content.Context;
import android.util.Base64;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class RSAUtils {

    public static String ASSETS_PUBLIC_KEY = "rsa_public_key.pem";
    //    public static String STRING_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCz+9OdWejOpGtxlNld9F4dFKoq"
//            + "\r" + "RKCiw+OaPXMGknERDO2sSRXM6ArIVtep4koexJSVVMKbAj+e5qFmRtDfg41ZySCm" + "\r"
//            + "MTMJWlSqlzz2cWBc9Dn1jl8WK6K89kkhoSKG5/kW5ifEuAC3M15YVp3or7lsjSfC" + "\r"
//            + "TAjDxSU7bIu0a4Q7oQIDAQAB" + "\r";
    public static String STRING_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCz+9OdWejOpGtxlNld9F4dFKoqRKCiw+OaPXMGknERDO2sSRXM6ArIVtep4koexJSVVMKbAj+e5qFmRtDfg41ZySCmMTMJWlSqlzz2cWBc9Dn1jl8WK6K89kkhoSKG5/kW5ifEuAC3M15YVp3or7lsjSfCTAjDxSU7bIu0a4Q7oQIDAQAB";

    /**
     * 得到公钥
     *
     * @param bysKey 公钥字符串
     * @return PublicKey  公钥
     */
    private static PublicKey getPublicKeyFromX509(String bysKey) {
        byte[] decodeKey = Base64.decode(bysKey, Base64.NO_WRAP);
        X509EncodedKeySpec x509 = new X509EncodedKeySpec(decodeKey);
        KeyFactory keyFactory;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(x509);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * 得到公钥
     *
     * @return PublicKey  公钥
     */
    private static RSAPublicKey getPublicKey(String key) throws Exception {
        try {
            byte[] buffer = key.getBytes();
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此算法");
        } catch (InvalidKeySpecException e) {
            throw new Exception("公钥非法");
        } catch (NullPointerException e) {
            throw new Exception("公钥数据为空");
        }

    }

    /**
     * 使用公钥加密
     *
     * @param content 要加密的字符
     * @return 加密后的字符
     */
    public static String encryptByPublic(String content) {
        try {
//            String pubKey = getPublicKeyFromAssets(AppUtil.getContext());
            PublicKey publicKey = getPublicKeyFromX509(STRING_PUBLIC_KEY);
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


    /**
     * 获取公钥
     *
     * @return
     */
    private static String getPublicKeyFromAssets(Context context) {
        try {
            InputStreamReader inputReader = new InputStreamReader(context
                    .getResources().getAssets().open(ASSETS_PUBLIC_KEY));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            StringBuilder Result = new StringBuilder();
            while ((line = bufReader.readLine()) != null) {
                if (line.charAt(0) == '-') {
                    continue;
                }
                Result.append(line);
            }
            inputReader.close();
            bufReader.close();
            return Result.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String decrypt(byte[] content, String password) throws Exception {
        // 创建AES秘钥
        SecretKeySpec key = new SecretKeySpec(password.getBytes(), "AES/CBC/PKCS5PADDING");
        // 创建密码器
        Cipher cipher = Cipher.getInstance("AES");
        // 初始化解密器
        cipher.init(Cipher.DECRYPT_MODE, key);
        // 解密
        return new String(cipher.doFinal(content), "UTF-8");
    }

    public static String dataDecrypt(String password, String data) {
        byte[] content = Base64.decode(data.getBytes(), Base64.NO_WRAP);
        try {
            return decrypt(content, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
}