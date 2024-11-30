package com.zyneonstudios.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.CRC32;
import java.util.zip.Checksum;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileUtil {

    public static File downloadFile(String urlString, String path) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                File outputFile = new File(path);
                FileOutputStream outputStream = new FileOutputStream(outputFile);
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                inputStream.close();
                outputStream.close();
                return outputFile;
            }
        } catch (Exception ignore) {}
        return null;
    }

    public static void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if(files!=null) {
            for(File f: files) {
                if(f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }

    public static void extractResourceFile(String sourceFileName, String destinationFileName, Class class_) {
        new File(new File(destinationFileName).getParent()).mkdirs();
        try {
            ClassLoader classLoader = class_.getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream(sourceFileName);
            if (inputStream == null) {
                return;
            }
            OutputStream outputStream = new FileOutputStream(destinationFileName);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static File getResourceFile(String resourceString, Class class_) {
        try {
            return new File(class_.getClassLoader().getResource(resourceString).getFile());
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public static List<Path> list(Path dir) {
        try {
            final List<Path> result = new ArrayList<>();
            if (Files.exists(dir)) {
                try (final Stream<Path> files = Files.list(dir)) {
                    result.addAll(files.collect(Collectors.toList()));
                }
            }
            return result;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public static Long getCRC32(Path path) {
        try {
            final Checksum checksum = new CRC32();
            final byte[] bytes = Files.readAllBytes(path);
            checksum.update(bytes, 0, bytes.length);
            return checksum.getValue();
        } catch (Exception e) {
            return null;
        }
    }


    public static boolean unjar(Path file, Path dest, String... args) {
        try {
            JarFile jarFile = new JarFile(file.toFile());
            Enumeration<JarEntry> content = jarFile.entries();
            while (content.hasMoreElements()) {
                JarEntry entry = content.nextElement();
                Path dest_ = dest.resolve(entry.getName());
                if (args.length >= 1 && args[0] != null && args[0].equals("ignoreMetaInf")) {
                    if (dest_.toString().contains("META-INF"))
                        continue;
                }
                unjar(jarFile,dest_,entry);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static void unjar(ZipFile file, Path dest, ZipEntry e) throws IOException {
        if(e.isDirectory()) {
            return;
        }
        if (Files.notExists(dest)) {
            Files.createDirectories(dest.getParent());
        }
        Files.copy(file.getInputStream(e), dest);
    }
}