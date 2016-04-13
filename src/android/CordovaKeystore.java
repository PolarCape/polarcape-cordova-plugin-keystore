package com.polarcape.plugins.keystore;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.interfaces.RSAPrivateKey;
import java.util.Calendar;

import javax.security.auth.x500.X500Principal;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.security.KeyPairGeneratorSpec;


public class CordovaKeystore extends CordovaPlugin {
    
    
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("getKey")) {
            JSONObject jsonObject= (JSONObject) args.get(0);
            JSONObject result = getKey(jsonObject.getString("keyName"));
            PluginResult pluginResult;
            if(result!=null){
                pluginResult = new PluginResult(PluginResult.Status.OK, result);
                pluginResult.setKeepCallback(true);
                callbackContext.sendPluginResult(pluginResult);
            }else{
                pluginResult = new PluginResult(PluginResult.Status.ERROR, "Device not suppoting keystore");
                pluginResult.setKeepCallback(true);
            }
            
            return true;
        }
        return false;
    }
    
    @SuppressLint("NewApi")
    private JSONObject getKey(String key) {
        JSONObject returnObject = new JSONObject();
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2){
                // in another part of the app, access the keys
                KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
                keyStore.load(null);
                KeyStore.PrivateKeyEntry keyEntry = (KeyStore.PrivateKeyEntry)keyStore.getEntry(key, null);
                if(keyEntry == null){
                    // generate a key pair
                    Context ctx = this.cordova.getActivity();
                    Calendar notBefore = Calendar.getInstance();
                    Calendar notAfter = Calendar.getInstance();
                    notAfter.add(1, Calendar.YEAR);
                    KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(ctx)
                    .setAlias(key)
                    .setSubject(
                                new X500Principal(String.format("CN=%s, OU=%s", key,
                                                                ctx.getPackageName())))
                    .setSerialNumber(BigInteger.ONE).setStartDate(notBefore.getTime())
                    .setEndDate(notAfter.getTime()).build();
                    
                    KeyPairGenerator kpGenerator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
                    kpGenerator.initialize(spec);
                    KeyPair kp = kpGenerator.generateKeyPair();
                    
                    
                    keyStore = KeyStore.getInstance("AndroidKeyStore");
                    keyStore.load(null);
                    keyEntry = (KeyStore.PrivateKeyEntry)keyStore.getEntry(key, null);
                    RSAPrivateKey privKey = (RSAPrivateKey) keyEntry.getPrivateKey();
                    //                    RSAPublicKey pubKey = (RSAPublicKey)keyEntry.getCertificate().getPublicKey();
                    returnObject.put("modulus",privKey.getModulus().toString());
                }else{
                    RSAPrivateKey privKey = (RSAPrivateKey) keyEntry.getPrivateKey();
                    //                  RSAPublicKey pubKey = (RSAPublicKey)keyEntry.getCertificate().getPublicKey();
                    returnObject.put("modulus",privKey.getModulus().toString());
                }
            }else{
                returnObject.put("modulus", "");
            }
            return returnObject;
        } catch (Exception e) {
            try {
                returnObject.put("modulus", "");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
            return returnObject;
        }
    }
    
}
