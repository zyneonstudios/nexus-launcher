
package com.zyneonstudios.launcher;

import com.zyneonstudios.NexusLauncher;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;

public class VanillaLauncher {

    private String username = "NexusDEV";
    private String version = "1.20.1";
    private String uuid = "00000000-0000-0000-0000-000000000000";
    private String accessToken = "unauthenticated";
    private String clientId = "nexus";
    private String xuid = "dev";
    private String userType = "mojang";
    private String versionType = "release";
    private String assetIndex;

    private Path assetsDirectory;
    private Path gameDirectory;
    private final Path librariesDirectory;
    private final Path nativesDirectory;
    private final Path gameClient;

    private final ArrayList<String> libraries = new ArrayList<>();

    public VanillaLauncher(String path_, String... args) {
        gameClient = Paths.get(path_).toAbsolutePath();
        gameDirectory = gameClient.getParent();
        assetIndex = "5";
        assetsDirectory = gameDirectory.resolve("assets");
        librariesDirectory = gameDirectory.resolve("libraries");
        nativesDirectory = gameDirectory.resolve("natives");

        resolveArguments(args);
    }

    public void launch() {
        try {
            Process process = getProcessBuilder().start();
            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            int exitCode = process.waitFor();
            System.out.println(exitCode);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ProcessBuilder getProcessBuilder() {
        scanClasspath(librariesDirectory.toFile());
        StringBuilder classpath = new StringBuilder(gameClient.toString() + ":" + librariesDirectory + "/*");
        for(String s:libraries) {
            classpath.append(":").append(s).append("/*");
        }
        if(NexusLauncher.getOS().contains("win")) {
            classpath = new StringBuilder(classpath.toString().replace(":", ";"));
        }
        ProcessBuilder processBuilder = new ProcessBuilder(
                "java",
                "-Djava.library.path\\u003d"+nativesDirectory,
                "-Djna.tmpdir\\u003d"+nativesDirectory,
                "-Dorg.lwjgl.system.SharedLibraryExtractPath\\u003d"+nativesDirectory,
                "-Dio.netty.native.workdir\\u003d"+nativesDirectory,
                "-cp", classpath.toString(),
                "net.minecraft.client.main.Main",
                "--username", username,
                "--version", version,
                "--gameDir", gameDirectory.toString(),
                "--assetsDir", assetsDirectory.toString(),
                "--assetIndex", assetIndex,
                "--uuid", uuid,
                "--accessToken", accessToken,
                "--clientId", clientId,
                "--xuid", xuid,
                "--userType", userType,
                "--versionType", versionType);
        processBuilder.redirectErrorStream(true);
        return processBuilder;
    }

    private void scanClasspath(File folder) {
        if(folder.isDirectory()) {
            for(File file: Objects.requireNonNull(folder.listFiles())) {
                if(file.isDirectory()) {
                    scanClasspath(file);
                } else {
                    String f = file.getParentFile().getAbsolutePath();
                    if(!libraries.contains(f)) {
                        libraries.add(f);
                    }
                }
            }
        }
    }

    private void resolveArguments(String[] args) {
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--gameDir":
                    gameDirectory = gameDirectory.resolve(args[i + 1]);
                    break;
                case "--assetsDir":
                    assetsDirectory = assetsDirectory.resolve(args[i + 1]);
                    break;
                case "--assetIndex":
                    assetIndex = args[i + 1];
                    break;
                case "--username":
                    username = args[i + 1];
                    break;
                case "--version":
                    version = args[i + 1];
                    break;
                case "--uuid":
                    uuid = args[i + 1];
                    break;
                case "--accessToken":
                    accessToken = args[i + 1];
                    break;
                case "--clientId":
                    clientId = args[i + 1];
                    break;
                case "--xuid":
                    xuid = args[i + 1];
                    break;
                case "--userType":
                    userType = args[i + 1];
                    break;
                case "--versionType":
                    versionType = args[i + 1];
                    break;
            }
        }
    }
}