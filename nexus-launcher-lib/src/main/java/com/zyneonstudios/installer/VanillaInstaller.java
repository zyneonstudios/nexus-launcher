package com.zyneonstudios.installer;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.zyneonstudios.NexusLauncher;
import com.zyneonstudios.util.FileUtil;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class VanillaInstaller {

    private final File assets;
    private final File folder;
    private final File libraries;
    private final File natives;

    private final String version;
    private final String versionInfo;

    private File jsonFile = null;
    private JSONObject json = null;

    public VanillaInstaller(String version, String path) {
        System.out.println("Initializing MinecraftInstaller for "+version+" in "+path+"...");
        this.version = version;
        if(!path.endsWith("/")) {
            path = path+"/";
        }

        System.out.println("Creating folders...");
        this.folder = new File(path);
        this.assets = new File(path+"assets/");
        this.libraries = new File(path+"libraries/");
        this.natives = new File(path+"natives/");
        this.jsonFile = new File(path+version+".json");

        System.out.println("Created assets folder: "+assets.mkdirs());
        System.out.println("Created libraries folder: "+libraries.mkdirs());
        System.out.println("Created natives folder: "+natives.mkdirs());

        String versionInfo = null;

        System.out.println("Getting version info...");
        if(jsonFile.exists()) {
            System.out.println("Found version info in local files!");
            versionInfo = "local";
        } else {
            System.out.println("Searching version info in manifest...");
            for (Object o : NexusLauncher.getVersionManifest().getVersions()) {
                try {
                    JSONObject json = (JSONObject) o;
                    if (json.getString("id").equals(version)) {
                        versionInfo = json.getString("url");
                        System.out.println("Found version info in manifest!");
                        System.out.println("Downloading JSON file...");
                        jsonFile = FileUtil.downloadFile(versionInfo,folder.getAbsolutePath()+"/"+version+".json");
                        break;
                    }
                } catch (Exception ignore) {}
            }
        }

        if(jsonFile == null) {
            System.err.println("Could not find version info for "+version+" in manifest!");
            throw new RuntimeException("Unknown version "+version);
        }

        try {
            FileReader reader = new FileReader(jsonFile);
            json = JSON.parseObject(reader);
        } catch (Exception e) {
            System.err.println("Could not read version info!");
            throw new RuntimeException("Could not read json object in "+jsonFile.getAbsolutePath());
        }

        this.versionInfo = versionInfo;
        System.out.println("Successfully initialized MinecraftInstaller for version "+version+" in "+path+"!");
    }

    public String getVersion() {
        return version;
    }

    public JSONObject getJson() {
        return json;
    }

    public File getAssetsFolder() {
        return assets;
    }

    public File getFolder() {
        return folder;
    }

    public File getJsonFile() {
        return jsonFile;
    }

    public File getLibrariesFolder() {
        return libraries;
    }

    public File getNativesFolder() {
        return natives;
    }

    public String getVersionUrl() {
        return versionInfo;
    }

    public boolean download() {
        try {
            System.out.println("Downloading assets...");
            downloadAssets();
            System.out.println("Downloading client...");
            downloadClient();
            System.out.println("Downloading libraries...");
            downloadLibraries();
            System.out.println("Successfully downloaded version "+version+"!");
            System.out.println(" ");
            System.out.println("Installing version "+version+"...");
            System.out.println("Extracting natives...");
            extractNatives(this.libraries);
            extractNatives(this.natives);
            System.out.println("Successfully installed version "+version+"!");
            return true;
        } catch (Exception e) {
            System.err.println("Could not complete the download for version "+version+": "+e.getMessage());
            return false;
        }
    }

    private void downloadLibraries() {
        System.out.println(" ");
        JSONArray libraries = json.getJSONArray("libraries");
        boolean error = false;
        for(Object o:libraries) {
            JSONObject lib = (JSONObject)o;
            if(lib.containsKey("downloads")) {
                lib = lib.getJSONObject("downloads");
                if(lib.containsKey("artifact")) {
                    error = downloadLibrary(lib.getJSONObject("artifact"),false);
                }
            }
            if(lib.containsKey("classifiers")) {
                lib = lib.getJSONObject("classifiers");
                ArrayList<String> natives = new ArrayList<>();
                natives.add("windows"); natives.add("linux"); natives.add("macos");
                for(String s:natives) {
                    s = "natives-"+s;
                    if(lib.containsKey(s)) {
                        error = downloadLibrary(lib.getJSONObject(s),true);
                        error = downloadLibrary(lib.getJSONObject(s),false);
                    }
                }
            }
        }
        if(error) {
            throw new RuntimeException("Could not download all libraries!");
        }
    }

    private boolean downloadLibrary(JSONObject lib,boolean natives) {
        String path;
        if(natives) {
            String[] name_ = lib.getString("path").replace("\\","/").split("/");
            String name = name_[name_.length-1];
            path = this.natives.getAbsolutePath().replace("\\", "/") + "/" + name;
        } else {
            path = this.libraries.getAbsolutePath().replace("\\", "/") + "/" + lib.getString("path");
        }
        String url = lib.getString("url");
        System.out.println("Downloading library "+url+" to "+path+"...");
        new File(path).getParentFile().mkdirs();
        File libFile = new File(path);
        if(!libFile.exists()) {
            libFile = FileUtil.downloadFile(url, path);
            if (libFile != null) {
                System.out.println("Downloaded " + path + "!");
                System.out.println(" ");
            } else {
                System.err.println("Could not download " + url + "!");
                System.err.println(" ");
                return true;
            }
        } else {
            System.out.println("File " + path + " already exists!");
            System.out.println(" ");
        }
        return false;
    }

    private void downloadClient() {
        System.out.println(" ");
        JSONObject data = json.getJSONObject("downloads").getJSONObject("client");
        String path = folder.getAbsolutePath()+"/"+version+".jar";
        File client = new File(path);
        String url = data.getString("url");
        System.out.println("Downloading "+url+" to "+path+"...");
        if(client.exists()) {
            System.out.println("Client file already exists!");
            System.out.println(" ");
        } else {
            client = FileUtil.downloadFile(url,path);
            if(client == null) {
                throw new NullPointerException("The client file should not be null!");
            } else {
                System.out.println("Successfully downloaded the "+version+" client!");
                System.out.println(" ");
            }
        }
    }

    public File downloadServer() {
        System.out.println(" ");
        JSONObject data = json.getJSONObject("downloads").getJSONObject("server");
        String path = folder.getAbsolutePath()+"/"+version+"-server.jar";
        File server = new File(path);
        String url = data.getString("url");
        System.out.println("Downloading "+url+" to "+path+"...");
        if(server.exists()) {
            System.out.println("Client file already exists!");
            System.out.println(" ");
        } else {
            server = FileUtil.downloadFile(url,path);
            if(server == null) {
                System.err.println("Couldn't download the "+version+" server file...");
                System.err.println(" ");
            } else {
                System.out.println("Successfully downloaded the "+version+" server!");
                System.out.println(" ");
                return server;
            }
        }
        return null;
    }

    private void downloadAssets() {
        System.out.println(" ");
        JSONObject assetIndex = json.getJSONObject("assetIndex");
        String url = assetIndex.getString("url");
        String id = assetIndex.getString("id");
        System.out.println("Downloading asset index "+id+"...");
        boolean error = false;
        new File(assets.getAbsolutePath()+"/indexes/").mkdirs();
        File indexFile = new File(assets.getAbsolutePath() + "/indexes/" + id + ".json");
        if(!indexFile.exists()) {
            indexFile = FileUtil.downloadFile(url,indexFile.getAbsolutePath());
            if(indexFile != null) {
                System.out.println("Downloaded "+indexFile.getAbsolutePath()+"!");
            }
        } else {
            System.out.println("File " + indexFile.getAbsolutePath() + " already exists!");
        }
        try {
            if(indexFile!=null) {
                FileReader reader = new FileReader(indexFile);
                assetIndex = JSON.parseObject(reader).getJSONObject("objects");
                for (Map.Entry<String, Object> object: assetIndex.entrySet()) {
                    JSONObject jsonAsset = (JSONObject)object.getValue();
                    VanillaAsset asset = new VanillaAsset(jsonAsset.getString("hash"),jsonAsset.getInteger("size"));
                    String path = this.assets.getAbsolutePath().replace("\\","/")+"/"+asset.getFile();
                    String download = asset.getUrl();
                    System.out.println("Downloading asset "+download+" to "+path+"...");
                    File assetFile = new File(path);
                    assetFile.getParentFile().mkdirs();
                    if(!assetFile.exists()) {
                        assetFile = FileUtil.downloadFile(download,path);
                        if(assetFile == null) {
                            System.err.println("Could not download " + download + "!");
                            System.err.println(" ");
                            error = true;
                        } else {
                            System.out.println("Downloaded "+assetFile.getAbsolutePath()+"!");
                            System.out.println(" ");
                        }
                    } else {
                        System.out.println("File " + path + " already exists!");
                        System.out.println(" ");
                    }
                }
            } else {
                throw new RuntimeException("assetIndex should not be null");
            }
            if(error) {
                throw new RuntimeException("Could not download all assets!");
            }
        } catch (Exception e) {
            System.err.println("Could not download all assets: "+e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void extractNatives(File folder) {
        for (File file : Objects.requireNonNull(folder.listFiles())) {
            if (file.isDirectory()) {
                extractNatives(file);
            } else {
                String filter = "natives-" + NexusLauncher.getOS()+".jar";
                if (file.getName().endsWith(".jar")) {
                    if (file.getName().contains(filter)) {
                        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file))) {
                            ZipEntry entry;
                            while ((entry = zipInputStream.getNextEntry()) != null) {
                                extractEntry(entry, zipInputStream);
                            }
                        } catch (IOException e) {
                            throw new RuntimeException("Error extracting natives: " + e.getMessage());
                        }
                    }
                }
            }
        }
    }

    private void extractEntry(ZipEntry entry, InputStream zipInputStream) throws IOException {
        if (!entry.isDirectory()) {
            String[] name_ = entry.getName().replace("\\","/").split("/");
            String name = name_[name_.length-1];
            if(name.toLowerCase().endsWith(".jar")||name.toLowerCase().endsWith(".dll")||name.toLowerCase().endsWith(".dll.x")) {
                System.out.println("Extracting "+name+"...");
                File outputFile = new File(natives, name);
                if(!outputFile.exists()) {
                    if (!outputFile.getParentFile().exists()) {
                        outputFile.getParentFile().mkdirs();
                    }
                    try (OutputStream outputStream = Files.newOutputStream(outputFile.toPath())) {
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = zipInputStream.read(buffer)) > 0) {
                            outputStream.write(buffer, 0, length);
                        }
                    }
                    System.out.println("Successfully extracted "+name+"!");
                    System.out.println(" ");
                } else {
                    System.out.println(name+" is already extracted!");
                    System.out.println(" ");
                }
            }
        }
    }
}