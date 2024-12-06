package com.zyneonstudios.nexus.launcher.integrations;

import com.zyneonstudios.nexus.launcher.game.GameLauncher;

import java.nio.file.Path;

public interface Launcher {

    boolean init(Path path, String gameVersion, String modloader);
    boolean launch(Path path);
    GameLauncher.Type getType();
    String getVersion();

    default void beforeLaunch() {}
    default void afterLaunch() {}
    default void gameClosed() {}
}