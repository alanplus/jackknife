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

package com.lwh.jackknife.util;

import java.util.List;  
  
import android.content.Context;  
import android.net.wifi.ScanResult;  
import android.net.wifi.WifiConfiguration;  
import android.net.wifi.WifiInfo;  
import android.net.wifi.WifiManager;  
import android.net.wifi.WifiManager.WifiLock;

public class WifiAdmin {

    private WifiManager mWifiManager;
    private WifiInfo mWifiInfo;
    private List<ScanResult> mScanResults;
    private List<WifiConfiguration> mWifiConfigurations;
    private WifiLock mWifiLock;

    public WifiAdmin(Context context) {
        mWifiManager=(WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        mWifiInfo = mWifiManager.getConnectionInfo();
    }

    public void openWifi() {
        if(!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);  
        }  
    }  

    public void closeWifi() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);  
        }  
    }

    public int getWifiState() {
        return mWifiManager.getWifiState();    
    }

    public void acquireWifiLock(){
        mWifiLock.acquire();  
    }

    public void releaseWifiLock() {
        if (mWifiLock.isHeld()) {
            mWifiLock.acquire();  
        }  
    }

    public void createWifiLock(String tag) {
        mWifiLock = mWifiManager.createWifiLock(tag);
    }

    public List<WifiConfiguration> getConfiguration() {
        return mWifiConfigurations;  
    }

    public void applyConnectionConfiguration(int index) {
        if (index > mWifiConfigurations.size()) {
            return;
        }  
        mWifiManager.enableNetwork(mWifiConfigurations.get(index).networkId, true);
    }

    public void startScan() {
        mWifiManager.startScan();
        mScanResults = mWifiManager.getScanResults();
        mWifiConfigurations = mWifiManager.getConfiguredNetworks();
    }

    public List<ScanResult> getScanResults() {
        return mScanResults;
    }

    public StringBuffer lookUpScan() {
        StringBuffer sb=new StringBuffer();  
        for(int i=0;i<mScanResults.size();i++){
            sb.append("Index_" + new Integer(i + 1).toString() + ":");  
            sb.append((mScanResults.get(i)).toString()).append("\n");
        }  
        return sb;    
    }

    public String getMacAddress() {
        return mWifiInfo == null ? "" : mWifiInfo.getMacAddress();
    }

    public String getBSSID() {
        return mWifiInfo == null ? "" : mWifiInfo.getBSSID();
    }

    public int getIpAddress() {
        return mWifiInfo == null ? 0 : mWifiInfo.getIpAddress();
    }

    public int getNetWordId() {
        return mWifiInfo == null ? 0 : mWifiInfo.getNetworkId();
    }

    public String getWifiInfo() {
        return mWifiInfo == null ? "" : mWifiInfo.toString();
    }

    public void addNetWork(WifiConfiguration configuration) {
        int wcgId = mWifiManager.addNetwork(configuration);
        mWifiManager.enableNetwork(wcgId, true);  
    }

    public void disconnectWifi(int netId) {
        mWifiManager.disableNetwork(netId);  
        mWifiManager.disconnect();  
    }  
}  