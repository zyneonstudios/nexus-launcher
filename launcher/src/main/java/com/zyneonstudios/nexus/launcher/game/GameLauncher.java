package com.zyneonstudios.nexus.launcher.game;

import com.zyneonstudios.nexus.launcher.integrations.Launcher;

public class GameLauncher {

    private final Launcher launcher;
    private final String gameVersion;

    private Modloader modloader = Modloader.VANILLA;
    private String modloaderVersion = null;

    public GameLauncher(Launcher launcher) {
        this.launcher = launcher;
        this.gameVersion = launcher.getVersion();
    }

    public void setModloader(Modloader modloader, String modloaderVersion) {
        this.modloader = modloader;
        this.modloaderVersion = modloaderVersion;
    }

    public Type getType() {
        return launcher.getType();
    }

    public String getGameVersion() {
        return gameVersion;
    }

    public Modloader getModloader() {
        return modloader;
    }

    public String getModloaderVersion() {
        return modloaderVersion;
    }

    public enum Type {
        OpenLauncherLib,
        Lux,
        Mojang
    }
}