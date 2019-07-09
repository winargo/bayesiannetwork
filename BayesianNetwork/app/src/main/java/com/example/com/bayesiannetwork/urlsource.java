package com.example.com.bayesiannetwork;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;

public class urlsource {
    public static String server="192.168.5.211";
    public static String port="3000";

    public static String ip = "";
    public static String apikey = "";

    public static String iplocationurl = "https://api.ipgeolocation.io/ipgeo?apiKey="+apikey+"&ip="+ip;
    
    public static String getloginurl = "http://"+server+":"+port+"/login";

    public static String signupurl = "http://"+server+":"+port+"/register";
    public static String signupcardurl = "http://"+server+":"+port+"/register/card";

    public static String getproducts = "http://"+server+":"+port+"/products";
    public static String gettransaction = "http://"+server+":"+port+"/transaction";
    public static String getpayment = "http://"+server+":"+port+"/payment";
    public static String getproductsimg = "http://"+server+"/bn/uploads/aktivitas/produk/";

    public static String addtocardurl = "http://"+server+":"+port+"/addtocart";
    public static String checkcarturl = "http://"+server+":"+port+"/cart";
    public static String addcarturl = "http://"+server+":"+port+"/cart/add";
    public static String mincarturl = "http://"+server+":"+port+"/cart/minus";
    public static String removecarturl = "http://"+server+":"+port+"/cart/remove";

    public static String checkouturl = "http://"+server+":"+port+"/checkout";
    public static String checkoutnurl = "http://"+server+":"+port+"/checkoutn";
    public static String checkoutpurl = "http://"+server+":"+port+"/checkoutp";
    public static String creditcardurl = "http://"+server+":"+port+"/creditcard";
    public static String deletecreditcardurl = "http://"+server+":"+port+"/deletecreditcard";

    public static String userreporturl = "http://"+server+":"+port+"/userreport";

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

    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':')<0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim<0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) {
            ignored.printStackTrace();
        } // for now eat exceptions
        return "";
    }

}
