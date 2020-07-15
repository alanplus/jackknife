package com.lwh.jackknife.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.DigestInputStream;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Locale;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class SecurityUtils {

    public static class DES {

        private static final byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0};

        public static String encryptDES(String KEY_DES, String encryptString) throws Exception {
            IvParameterSpec zeroIv = new IvParameterSpec(iv);
            SecretKeySpec key = new SecretKeySpec(KEY_DES.getBytes(), "DES");
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
            byte[] encryptedData = cipher.doFinal(encryptString.getBytes());
            return parseByte2HexStr(encryptedData);
        }

        public static String encryptDES(String keyString, int keyPos, String encryptString, String ivString, int ivPos) throws Exception {
            String keyValue = keyString.substring(keyPos - 1, 17);
            String ivValue = ivString.substring(ivPos - 1, 25);
            IvParameterSpec zeroIv = new IvParameterSpec(ivValue.getBytes());
            SecretKeySpec key = new SecretKeySpec(keyValue.getBytes(), "DES");
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
            byte[] encryptedData = cipher.doFinal(encryptString.getBytes());
            String encryptedStr = parseByte2HexStr(encryptedData);
            return encryptedStr;
        }

        public static String parseByte2HexStr(byte buf[]) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < buf.length; i++) {
                String hex = Integer.toHexString(buf[i] & 0xFF);
                if (hex.length() == 1) {
                    hex = '0' + hex;
                }
                Locale loc = Locale.getDefault();
                sb.append(hex.toUpperCase(loc));
            }
            return sb.toString();
        }

        public static byte[] hexStringToBytes(String hexString) {
            if (hexString == null || hexString.equals("")) {
                return null;
            }
            Locale loc = Locale.getDefault();
            hexString = hexString.toUpperCase(loc);
            int length = hexString.length() / 2;
            char[] hexChars = hexString.toCharArray();
            byte[] d = new byte[length];
            for (int i = 0; i < length; i++) {
                int pos = i * 2;
                d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
            }
            return d;
        }

        private static byte charToByte(char c) {
            return (byte) "0123456789ABCDEF".indexOf(c);
        }

        public static String decryptDES(String keyString, String decryptString) throws Exception {
            if (!android.text.TextUtils.isEmpty(decryptString)) {

                byte[] byteMi = hexStringToBytes(decryptString);
                IvParameterSpec zeroIv = new IvParameterSpec(iv);
                byte[] byteKey = keyString.getBytes();
                SecretKeySpec key = new SecretKeySpec(byteKey, "DES");
                Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
                cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
                byte decryptedData[] = cipher.doFinal(byteMi);
                return new String(decryptedData);
            }
            return "";
        }

        public static String decryptDES(String keyString, int keyPos, String decryptString, String ivString, int ivPos) throws Exception {
            if (!TextUtils.isEmpty(decryptString)) {
                String keyValue = keyString.substring(keyPos - 1, 16);
                String ivValue = ivString.substring(ivPos - 1, 24);
                byte[] byteMi = hexStringToBytes(decryptString);
                IvParameterSpec zeroIv = new IvParameterSpec(ivValue.getBytes());
                SecretKeySpec key = new SecretKeySpec(keyValue.getBytes(), "DES");
                Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
                cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
                byte decryptedData[] = cipher.doFinal(byteMi);
                return new String(decryptedData);
            }
            return "";
        }
    }

    public static class MD5 {

        static char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

        private MD5() {
        }

        public static String getMD5(String inStr) {
            MessageDigest md5;
            try {
                md5 = MessageDigest.getInstance("MD5");
            } catch (Exception e) {
                System.out.println(e.toString());
                e.printStackTrace();
                return "";
            }
            char[] charArray = inStr.toCharArray();
            byte[] byteArray = new byte[charArray.length];
            for (int i = 0; i < charArray.length; i++) {
                byteArray[i] = (byte) charArray[i];
            }
            byte[] md5Bytes = md5.digest(byteArray);
            StringBuffer hexValue = new StringBuffer();
            for (int i = 0; i < md5Bytes.length; i++) {
                int val = ((int) md5Bytes[i]) & 0xff;
                if (val < 16) {
                    hexValue.append("0");
                }
                hexValue.append(Integer.toHexString(val));
            }
            return hexValue.toString();
        }

        public static String getMessageDigest(byte[] buffer) {
            try {
                MessageDigest mdTemp = MessageDigest.getInstance("MD5");
                mdTemp.update(buffer);
                byte[] md = mdTemp.digest();
                int j = md.length;
                char str[] = new char[j * 2];
                int k = 0;
                for (int i = 0; i < j; i++) {
                    byte byte0 = md[i];
                    str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                    str[k++] = hexDigits[byte0 & 0xf];
                }
                return new String(str);
            } catch (Exception e) {
                return null;
            }
        }

        public static String getMD5(File file) {
            FileInputStream fis = null;
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                fis = new FileInputStream(file);
                byte[] buffer = new byte[2048];
                int length;
                while ((length = fis.read(buffer)) != -1) {
                    md.update(buffer, 0, length);
                }
                byte[] b = md.digest();
                return byteToHexString(b);
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            } finally {
                try {
                    fis.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        private static String byteToHexString(byte[] tmp) {
            String s;
            char str[] = new char[16 * 2];
            int k = 0;
            for (int i = 0; i < 16; i++) {
                byte byte0 = tmp[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            s = new String(str);
            return s;
        }

        private static synchronized MessageDigest checkAlgorithm() {
            MessageDigest messageDigest;
            try {
                messageDigest = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                throw new NullPointerException("No md5 algorithm found");
            }
            return messageDigest;
        }

        public static String digest32(String src) {
            if (src == null) {
                return null;
            }
            MessageDigest messageDigest = checkAlgorithm();
            byte[] ret = null;
            try {
                ret = messageDigest.digest(src.getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            return ret == null ? null : Math.bs2H(ret);
        }

        public static String digest32(String src, String charset) {
            if (src == null) {
                return null;
            }
            MessageDigest messageDigest = checkAlgorithm();
            byte[] ret = null;
            try {
                ret = messageDigest.digest(src.getBytes(charset));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            return ret == null ? null : Math.bs2H(ret);
        }

        public static String digest32(File src) throws IOException {
            if (src == null) {
                return null;
            }
            MessageDigest messageDigest = checkAlgorithm();
            InputStream fis = null;
            DigestInputStream dis = null;
            try {
                fis = new FileInputStream(src);
                dis = new DigestInputStream(fis, messageDigest);
                byte[] buffer = new byte[2048];
                while (dis.read(buffer) > 0) {
                    ;
                }
                messageDigest = dis.getMessageDigest();
            } finally {
                if (fis != null) {
                    fis.close();
                }
                if (dis != null) {
                    dis.close();
                }
            }
            return Math.bs2H(messageDigest.digest());
        }

        public static String digest32(byte[] src) {
            if (src == null) {
                return null;
            }
            MessageDigest messageDigest = checkAlgorithm();
            byte[] ret = messageDigest.digest(src);
            return ret == null ? null : Math.bs2H(ret);
        }

        public static String digest16(String src) {
            String encrypt = digest32(src);
            return encrypt == null ? null : encrypt.substring(8, 24);
        }

        public static String digest16(String src, String charset) {
            String encrypt = digest32(src, charset);
            return encrypt == null ? null : encrypt.substring(8, 24);
        }

        public static String digest16(File src) throws IOException {
            String encrypt = digest32(src);
            return encrypt == null ? null : encrypt.substring(8, 24);
        }

        public static String digest16(byte[] src) {
            String encrypt = digest32(src);
            return encrypt == null ? null : encrypt.substring(8, 24);
        }
    }

    public static class RSA {

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
}
