package com.zyneonstudios.installer;

public class VanillaAsset {

    private final String hash;
    private final long size;
    private final String url;
    private final String file;

    public VanillaAsset(String hash, long size) {
        this.hash = hash;
        this.size = size;
        final String assetsPath = "/" + this.hash.substring(0, 2) + "/" + this.hash;
        this.url = "https://resources.download.minecraft.net" + assetsPath;
        this.file = "objects" + assetsPath;
    }

    public String getHash() {
        return this.hash;
    }

    public long getSize() {
        return this.size;
    }

    public String getUrl() {
        return this.url;
    }

    public String getFile() {
        return this.file;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final VanillaAsset that = (VanillaAsset)o;
        return this.file.equals(that.file) && this.size == that.size && this.hash.equals(that.hash) && this.url.equals(that.url);
    }

    @Override
    public int hashCode() {
        int result = this.hash.hashCode();
        result = 31 * result + Long.hashCode(this.size);
        result = 31 * result + this.url.hashCode();
        return result;
    }
}