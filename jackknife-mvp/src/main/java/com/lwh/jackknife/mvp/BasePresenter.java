/*
 * Copyright (C) 2017 The JackKnife Open Source Project
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

package com.lwh.jackknife.mvp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Base64;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public abstract class BasePresenter<V extends IBaseView> {

    protected WeakReference<V> mViewRef;
    public static String DEFAULT_ASSETS_PUBLIC_KEY = "rsa_public_key.pem";
    public static String DEFAULT_STRING_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCz+9OdWejOpGtxlNld9F4dFKoqRKCiw+OaPXMGknERDO2sSRXM6ArIVtep4koexJSVVMKbAj+e5qFmRtDfg41ZySCmMTMJWlSqlzz2cWBc9Dn1jl8WK6K89kkhoSKG5/kW5ifEuAC3M15YVp3or7lsjSfCTAjDxSU7bIu0a4Q7oQIDAQAB";


    public void attachView(V view) {
        mViewRef = new WeakReference<>(view);
    }

    public void detachView() {
        if (mViewRef != null) {
            mViewRef.clear();
            mViewRef = null;
        }
    }

    protected V getV() {
        if (isViewAttached()) {
            return mViewRef.get();
        } else {
            return null;
        }
    }

    /**
     * It's useful to check view is attached.
     *
     * @return True means successful attachment, false otherwise.
     */
    protected boolean isViewAttached() {
        return mViewRef != null && mViewRef.get() != null;
    }

    protected <T> void doGet(Map<String, Object> parameters, XHttpResponse<T> callback) {
    }

    protected <T> void doPost(String action, Map<String, Object> parameters, XHttpResponse<T> callback) {
    }

    /**
     * 得到公钥
     *
     * @param bysKey 公钥字符串
     * @return PublicKey  公钥
     */
    private PublicKey getPublicKeyFromX509(String bysKey) {
        byte[] decodeKey = Base64.decode(bysKey, Base64.NO_WRAP);
        X509EncodedKeySpec x509 = new X509EncodedKeySpec(decodeKey);
        KeyFactory keyFactory = null;
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
    private RSAPublicKey getPublicKey(String key) throws Exception {
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
    public String encryptByPublic(String content) {
        try {
//            String pubKey = getPublicKeyFromAssets(AppUtil.getContext());
            PublicKey publicKey = getPublicKeyFromX509(DEFAULT_STRING_PUBLIC_KEY);
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
    private String getPublicKeyFromAssets(Context context) {
        try {
            InputStreamReader inputReader = new InputStreamReader(context
                    .getResources().getAssets().open(DEFAULT_ASSETS_PUBLIC_KEY));
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

    private String decrypt(byte[] content, String password) throws Exception {
        // 创建AES秘钥
        SecretKeySpec key = new SecretKeySpec(password.getBytes(), "AES/CBC/PKCS5PADDING");
        // 创建密码器
        Cipher cipher = Cipher.getInstance("AES");
        // 初始化解密器
        cipher.init(Cipher.DECRYPT_MODE, key);
        // 解密
        return new String(cipher.doFinal(content), "UTF-8");
    }

    public String dataDecrypt(String password, String data) {
        byte[] content = Base64.decode(data.getBytes(), Base64.NO_WRAP);
        try {
            return decrypt(content, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getKeyPassword() {
        Context context = getV().getContext();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String key = preferences.getString("TokenKey", "");
        if (TextUtils.isEmpty(key)) {
            key = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("TokenKey", key).commit();
        }
        return key;
    }

    public String encryptAes(String text) {
        try {
            SecretKeySpec skeySpec = new SecretKeySpec(getKeyPassword().getBytes("UTF-8"), "AES/CBC/PKCS5PADDING");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            byte[] encrypted = cipher.doFinal(text.getBytes());
            return Base64.encodeToString(encrypted, Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
