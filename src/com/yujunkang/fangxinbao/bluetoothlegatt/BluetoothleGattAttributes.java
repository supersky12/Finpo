/*
 * Copyright (C) 2013 The Android Open Source Project
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

package com.yujunkang.fangxinbao.bluetoothlegatt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.text.TextUtils;

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class BluetoothleGattAttributes {
    private static HashMap<String, String> attributes = new HashMap();
    public static String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";
    public static String BATTERY_SERVICE = "0000180f-0000-1000-8000-00805f9b34fb";
    public static String BATTERY_CHARACTERISTIC_CONFIG = "00002a19-0000-1000-8000-00805f9b34fb";
    public static String HEALTH_THEMOMETER_CHARACTERISTIC_CONFIG = "00002a1c-0000-1000-8000-00805f9b34fb";
    public static String HEALTH_THEMOMETER_SERVICE = "00001809-0000-1000-8000-00805f9b34fb";
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    public static String CLIENT_CHARACTERISTIC_SPS_NOTIFY = "0000ffb1-0000-1000-8000-00805f9b34fb";
    public static String CLIENT_CHARACTERISTIC_SPS_RESPONSE = "0000ffb2-0000-1000-8000-00805f9b34fb";
    
    //命令
    public static int Cmd_QueryRecordStatus = 0x70;
    public static int Cmd_EnableRecord = 0x71;
    public static int Cmd_UpdateTime = 0x72;
    public static int Cmd_QueryAllRecord= 0x73;
    public static int Cmd_GetModuleTime = 0x76;
    public static int Cmd_DeleteAllRecord = 0x77;
    
    
    public static int Response_Success = 0x00;
    public static int Response_Failed = 0x01;
    
    static {
        attributes.put(BATTERY_SERVICE, "Battery Service");
        attributes.put(BATTERY_CHARACTERISTIC_CONFIG, "Battery Characteristic");
        attributes.put(HEALTH_THEMOMETER_SERVICE, "Health Themometer Service");
        attributes.put(HEALTH_THEMOMETER_CHARACTERISTIC_CONFIG, "Health Characteristic");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
    
   
    public static String bytesToHexString(byte[] src){  
        StringBuilder stringBuilder = new StringBuilder("");  
        if (src == null || src.length <= 0) {  
            return null;  
        }  
        for (int i = 0; i < src.length; i++) {  
            int v = src[i] & 0xFF;  
            String hv = Integer.toHexString(v);  
            if (hv.length() < 2) {  
                stringBuilder.append(0);  
            }  
            stringBuilder.append(hv);  
        }  
        return stringBuilder.toString();  
    }  
}
