/*
 * Copyright (C) 2011 The Android Open Source Project
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

package com.android.settings.vpn2;

import java.nio.charset.Charsets;

/**
 * Parcel-like entity class for VPN profiles. To keep things simple, all
 * fields are package private. Methods are provided for serialization, so
 * storage can be implemented easily. Two rules are set for this class.
 * First, all fields must be kept non-null. Second, always make a copy
 * using clone() before modifying.
 */
class VpnProfile implements Cloneable {
    // Match these constants with R.array.vpn_types.
    static final int TYPE_PPTP = 0;
    static final int TYPE_L2TP_IPSEC_PSK = 1;
    static final int TYPE_L2TP_IPSEC_RSA = 2;
    static final int TYPE_IPSEC_XAUTH_PSK = 3;
    static final int TYPE_IPSEC_XAUTH_RSA = 4;
    static final int TYPE_IPSEC_HYBRID_RSA = 5;
    static final int TYPE_MAX = 5;

    // Entity fields.
    final String key;           // -1
    String name = "";           // 0
    int type = TYPE_PPTP;       // 1
    String server = "";         // 2
    String username = "";       // 3
    String password = "";       // 4
    String domains = "";        // 5
    String routes = "";         // 6
    boolean mppe = false;       // 7
    String l2tpSecret = "";     // 8
    String ipsecIdentifier = "";// 9
    String ipsecSecret = "";    // 10
    String ipsecUserCert = "";  // 11
    String ipsecCaCert = "";    // 12

    // Helper fields.
    boolean saveLogin = false;

    VpnProfile(String key) {
        this.key = key;
    }

    static VpnProfile decode(String key, byte[] value) {
        try {
            if (key == null) {
                return null;
            }

            String[] values = new String(value, Charsets.UTF_8).split("\0", -1);
            // Currently it always has 13 fields.
            if (values.length < 13) {
                return null;
            }

            VpnProfile profile = new VpnProfile(key);
            profile.name = values[0];
            profile.type = Integer.valueOf(values[1]);
            if (profile.type < 0 || profile.type > TYPE_MAX) {
                return null;
            }
            profile.server = values[2];
            profile.username = values[3];
            profile.password = values[4];
            profile.domains = values[5];
            profile.routes = values[6];
            profile.mppe = Boolean.valueOf(values[7]);
            profile.l2tpSecret = values[8];
            profile.ipsecIdentifier = values[9];
            profile.ipsecSecret = values[10];
            profile.ipsecUserCert = values[11];
            profile.ipsecCaCert = values[12];

            profile.saveLogin = !profile.username.isEmpty() || !profile.password.isEmpty();
            return profile;
        } catch (Exception e) {
            // ignore
        }
        return null;
    }

    byte[] encode() {
        StringBuilder builder = new StringBuilder(name);
        builder.append('\0').append(type);
        builder.append('\0').append(server);
        builder.append('\0').append(saveLogin ? username : "");
        builder.append('\0').append(saveLogin ? password : "");
        builder.append('\0').append(domains);
        builder.append('\0').append(routes);
        builder.append('\0').append(mppe);
        builder.append('\0').append(l2tpSecret);
        builder.append('\0').append(ipsecIdentifier);
        builder.append('\0').append(ipsecSecret);
        builder.append('\0').append(ipsecUserCert);
        builder.append('\0').append(ipsecCaCert);
        return builder.toString().getBytes(Charsets.UTF_8);
    }
}
