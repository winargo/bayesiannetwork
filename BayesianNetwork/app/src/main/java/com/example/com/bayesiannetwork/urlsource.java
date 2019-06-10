package com.example.com.bayesiannetwork;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class urlsource {
    public static String server="192.168.5.8";
    public static String port="3000";
    
    public static String getloginurl = "http://"+server+":"+port+"/login";

    public static String signupurl = "http://"+server+":"+port+"/signup";

    public static String getproducts = "http://"+server+":"+port+"/products";
    public static String gettransaction = "http://"+server+":"+port+"/transaction";

    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
