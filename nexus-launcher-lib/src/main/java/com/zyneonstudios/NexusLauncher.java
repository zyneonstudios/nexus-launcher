package com.zyneonstudios;

import com.zyneonstudios.installer.VanillaInstaller;
import com.zyneonstudios.launcher.VanillaLauncher;
import com.zyneonstudios.meta.VersionManifest;

import java.util.ArrayList;

public class NexusLauncher {

    private static VersionManifest versionManifest = null;

    public static void main(String[] args) {
        String version = "1.18.2";
        VanillaInstaller installer = new VanillaInstaller(version,"nexus-launcher-lib/target/minecraft/"+version+"/");
        installer.download();
        VanillaLauncher launcher = new VanillaLauncher("nexus-launcher-lib/target/minecraft/"+version+"/"+version+".jar","--gameDir","B:/Workspaces/IntelliJ/nexus-launcher/nexus-launcher-lib/target/minecraft/"+version+"/game","--version",version,"--assetIndex","1.18");
        launcher.launch();
    }

    public static VersionManifest getVersionManifest() {
        if(versionManifest == null) {
            versionManifest = new VersionManifest("https://launchermeta.mojang.com/mc/game/version_manifest.json");
        }
        return versionManifest;
    }

    private static String os = null;
    public static String getOS() {
        if(os == null) {
            String os_ = System.getProperty("os.name").toLowerCase();
            if(os_.contains("win")) {
                os = "windows"+getArch();
            } else if(os_.contains("mac")) {
                os = "macos"+getArch();
            } else {
                os = "linux"+getArch();
            }
        }
        return os;
    }

    private static String getArch() {
        String os = System.getProperty("os.arch");
        ArrayList<String> aarch = new ArrayList<>();
        aarch.add("ARM");
        aarch.add("ARM64");
        aarch.add("aarch64");
        aarch.add("armv6l");
        aarch.add("armv7l");
        for(String arch_os:aarch) {
            if(arch_os.equalsIgnoreCase(os)) {
                return "-arm64";
            }
        }
        if(os.contains("64")) {
            return "";
        } else {
            return "-x86";
        }
    }
}