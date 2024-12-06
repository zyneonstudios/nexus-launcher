package com.zyneonstudios.nexus;

import com.zyneonstudios.nexus.utilities.NexusUtilities;
import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {
        NexusUtilities.getLogger().printErr("NEXUS","Not a runnable program","Hey there! You have opened the Zyneon Nexus Lux library JAR file.","This is not intended to be an executable program.",null,"Read the Lux docs","-> https://github.com/zyneonstudios/nexus-launcher/tree/main/lux","","or maybe you wanted to start the NEXUS Launcher or the NEXUS app?","-> https://github.com/zyneonstudios/nexus-launcher","-> https://github.com/zyneonstudios/nexus-app");
        if(Desktop.isDesktopSupported()) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignore) {}
            JOptionPane.showMessageDialog(null, "You have opened the Zyneon Nexus Lux library JAR file.\nUnfortunately this is not intended to be an executable program.\n\n Visit this for help: https://github.com/zyneonstudios/nexus-launcher/", "Hey there!", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
