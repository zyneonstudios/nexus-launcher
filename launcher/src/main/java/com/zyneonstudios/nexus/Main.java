package com.zyneonstudios.nexus;

import com.zyneonstudios.nexus.desktop.NexusDesktop;
import com.zyneonstudios.nexus.launcher.NexusLauncher;

public class Main {

    private static boolean silent = false;
    private static String workDir = null;
    private static String lockDir = null;
    private static String configFile = null;
    private static String launchId = null;
    private static NexusLauncher launcher = null;

    public static void main(String[] args) {
        NexusDesktop.init();
        NexusDesktop.getLogger().setName("NL",true);
        resolveArguments(args);
        launcher = new NexusLauncher(workDir,lockDir,configFile,launchId,silent);
    }

    public static NexusLauncher getLauncher() {
        return launcher;
    }

    private static void resolveArguments(String[] args) {
        for (int i = 0; i < args.length; ++i) {
            String arg = args[i].toLowerCase().replace("-","");
            switch (arg) {
                case "w", "workdir" -> {
                    try {
                        NexusDesktop.getLogger().log("Setting working directory to "+args[i+1]+"...");
                        workDir = args[i+1];
                    } catch (Exception e) {
                        NexusDesktop.getLogger().err("Couldn't resolve working directory: " + e.getMessage());
                        showHelp();
                        System.exit(1);
                    }
                }
                case "l", "lockdir" -> {
                    try {
                        NexusDesktop.getLogger().log("Restricting launcher to "+args[i+1]+"...");
                        lockDir = args[i+1];
                    } catch (Exception e) {
                        NexusDesktop.getLogger().err("Couldn't resolve locking directory: " + e.getMessage());
                        showHelp();
                        System.exit(1);
                    }
                }
                case "c", "config" -> {
                    try {
                        NexusDesktop.getLogger().log("Loading config from "+args[i+1]+"...");
                        configFile = args[i+1];
                    } catch (Exception e) {
                        NexusDesktop.getLogger().err("Couldn't resolve config file: " + e.getMessage());
                        showHelp();
                        System.exit(1);
                    }
                }
                case "r", "run" -> {
                    try {
                        NexusDesktop.getLogger().log("Launching game profile "+args[i+1]+"...");
                        launchId = args[i+1];
                    } catch (Exception e) {
                        NexusDesktop.getLogger().err("Couldn't resolve profile to run: " + e.getMessage());
                        showHelp();
                        System.exit(1);
                    }
                }
                case "rq", "runQuiet" -> {
                    try {
                        NexusDesktop.getLogger().log("Launching game profile "+args[i+1]+" silently...");
                        silent = true;
                    } catch (Exception e) {
                        NexusDesktop.getLogger().err("Couldn't resolve profile to run: " + e.getMessage());
                        showHelp();
                        System.exit(1);
                    }
                }
                case "h", "help" -> {
                    showHelp();
                    System.exit(0);
                }
            }
        }
    }

    private static void showHelp() {
        NexusDesktop.getLogger().log("NEXUS Launcher [OPTION...]");
        NexusDesktop.getLogger().log(" ");
        NexusDesktop.getLogger().log("-w, --workDir arg     Launcher working directory");
        NexusDesktop.getLogger().log("-l, --lockDir arg     Restrict launcher to directory");
        NexusDesktop.getLogger().log("-c, --config arg      Launcher configuration");
        NexusDesktop.getLogger().log("-r, --run arg         Run game profile");
        NexusDesktop.getLogger().log("-rq, --runQuiet arg   Run game profile without showing launcher");
        NexusDesktop.getLogger().log("-h, --help            Display this message");
    }
}