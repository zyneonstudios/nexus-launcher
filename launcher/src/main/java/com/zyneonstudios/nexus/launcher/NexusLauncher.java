package com.zyneonstudios.nexus.launcher;

import com.zyneonstudios.nexus.desktop.frame.web.NexusWebFrame;
import com.zyneonstudios.nexus.desktop.frame.web.NexusWebSetup;

import javax.imageio.ImageIO;
import java.awt.*;
import java.util.Objects;

public class NexusLauncher {

    private NexusWebSetup setup;
    private NexusWebFrame frame;

    public NexusLauncher(String runDir, String lockDir, String configFile, String launchId, boolean runSilent) {
        if(!runSilent) {
            setup = new NexusWebSetup("libraries/cef/");
            setup.enableCache(false);
            setup.setup();
            frame = new NexusWebFrame(setup.getWebClient(),"file:///D:/Workspaces/IntelliJ/nexus-launcher/launcher/src/main/html/main.html",true);
            Image icon = null;
            try {
                icon = ImageIO.read(Objects.requireNonNull(getClass().getResource("/grass.png"))).getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            } catch (Exception ignore) {}
            frame.setTitlebar("NEXUS Launcher", Color.decode("#131313"), Color.decode("#f5f5f5"), (Image)icon);
            frame.setMinimumSize(new Dimension(1024,640));
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        }
    }

    public NexusWebSetup getSetup() {
        return setup;
    }

    public NexusWebFrame getFrame() {
        return frame;
    }
}
