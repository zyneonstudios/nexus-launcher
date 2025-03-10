package com.zyneonstudios.nexus.launcher.integrations.flowarg;

import com.zyneonstudios.nexus.launcher.game.GameLauncher;
import com.zyneonstudios.nexus.launcher.integrations.Launcher;
import com.zyneonstudios.verget.Verget;
import com.zyneonstudios.verget.minecraft.MinecraftVerget;

import java.nio.file.Path;

public class OLLLauncher implements Launcher {

    private String version = Verget.getMinecraftVersions(MinecraftVerget.Filter.RELEASES).getFirst();

    public OLLLauncher() {

    }

    @Override
    public boolean init(Path path, String gameVersion, String modloader) {
        version = gameVersion;
        return false;
    }

    @Override
    public boolean launch(Path path) {
        return false;
    }

    @Override
    public GameLauncher.Type getType() {
        return GameLauncher.Type.OpenLauncherLib;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public void beforeLaunch() {
        Launcher.super.beforeLaunch();
    }

    @Override
    public void afterLaunch() {
        Launcher.super.afterLaunch();
    }

    @Override
    public void gameClosed() {
        Launcher.super.gameClosed();
    }
}
