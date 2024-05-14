package com.daxton.rolemaster.nms;

import org.bukkit.Bukkit;

public class NMSVersion {


    //目前版本大於判定版本1.16
    public static boolean compareNMSVersion(String version){
        return getMinecraftVersionSize(getMinecraftVersion()) >= getMinecraftVersionSize(version);
    }

    public static int getMinecraftVersionSize(String nms){
        switch(nms){
            case "1.7.2":
                return 1;
            case "1.7.5":
                return 2;
            case "1.7.8":
                return 3;
            case "1.7.10":
                return 4;
            case "1.8.1":
                return 5;
            case "1.8.4":
                return 6;
            case "1.8.8":
                return 7;
            case "1.9.2":
                return 8;
            case "1.9.4":
                return 9;
            case "1.10.2":
                return 10;
            case "1.11.2":
                return 11;
            case "1.12":
                return 12;
            case "1.12.1":
                return 13;
            case "1.12.2":
                return 14;
            case "1.13":
                return 15;
            case "1.13.1":
                return 16;
            case "1.13.2":
                return 17;
            case "1.14":
                return 18;
            case "1.14.1":
                return 19;
            case "1.14.2":
                return 20;
            case "1.14.3":
                return 21;
            case "1.14.4":
                return 22;
            case "1.15":
                return 23;
            case "1.15.2":
                return 24;
            case "1.16":
                return 25;
            case "1.16.1":
                return 26;
            case "1.16.3":
                return 27;
            case "1.16.5":
                return 28;
            case "1.17":
                return 29;
            case "1.17.1":
                return 30;
            case "1.18":
                return 31;
            case "1.18.1":
                return 32;
            case "1.18.2":
                return 33;
            case "1.19":
                return 34;
            case "1.19.1":
                return 35;
            case "1.19.2":
                return 36;
            case "1.19.3":
                return 37;
            case "1.19.4":
                return 38;
            case "1.20":
                return 39;
            case "1.20.1":
                return 40;
            case "1.20.2":
                return 41;
            case "1.20.3":
                return 42;
            case "1.20.4":
                return 43;
        }
        throw new IllegalArgumentException(nms + " isn't a know version");
    }

    public static String getMinecraftVersion(){
        String v = Bukkit.getServer().getClass().getPackage().getName();
        String nms = v.substring(v.lastIndexOf('.') + 1);
        switch(nms){
            case "v_1_7_R1":
                return "1.7.2";
            case "v_1_7_R2":
                return "1.7.5";
            case "v_1_7_R3":
                return "1.7.8";
            case "v_1_7_R4":
                return "1.7.10";
            case "v1_8_R1":
                return "1.8.1";
            case "v1_8_R2":
                return "1.8.4";
            case "v_1_8_R3":
                return "1.8.8";
            case "v1_9_R1":
                return "1.9.2";
            case "v1_9_R2":
                return "1.9.4";
            case "v1_10_R1":
                return "1.10.2";
            case "v1_11_R1":
                return "1.11.2";
            case "v1_12_R1":
                return "1.12.2";
            case "v1_13_R1":
                return "1.13";
            case "v1_13_R2":
                return "1.13.2";
            case "v1_14_R1":
                return "1.14.4";
            case "v1_15_R1":
                return "1.15.2";
            case "v1_16_R1":
                return "1.16.1";
            case "v1_16_R2":
                return "1.16.3";
            case "v1_16_R3":
                return "1.16.5";
            case "v1_17_R1":
                return "1.17.1";
            case "v1_18_R1":
                return "1.18.1";
            case "v1_18_R2":
                return "1.18.2";
            case "v1_19_R1":
                return "1.19.2";
            case "v1_19_R2":
                return "1.19.3";
            case "v1_19_R3":
                return "1.19.4";
            case "v1_20_R1":
                return "1.20.1";
            case "v1_20_R2":
                return "1.20.2";
            case "v1_20_R3":
                return "1.20.4";
        }
        throw new IllegalArgumentException(nms + " isn't a know version");
    }

}
