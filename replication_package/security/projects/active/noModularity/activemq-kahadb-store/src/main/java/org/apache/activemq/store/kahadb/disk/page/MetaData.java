package org.apache.activemq.store.kahadb.disk.page;

/**
 * The MetaData object hold the persistent data associated with a PageFile object.
 */
public class MetaData {

    String fileType;
    String fileTypeVersion;

    public long metaDataTxId = -1;
    int pageSize;
    boolean cleanShutdown;
    long lastTxId;
    long freePages;

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileTypeVersion() {
        return fileTypeVersion;
    }

    public void setFileTypeVersion(String version) {
        this.fileTypeVersion = version;
    }


    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public boolean isCleanShutdown() {
        return cleanShutdown;
    }

    public void setCleanShutdown(boolean cleanShutdown) {
        this.cleanShutdown = cleanShutdown;
    }

    public long getLastTxId() {
        return lastTxId;
    }

    public void setLastTxId(long lastTxId) {
        this.lastTxId = lastTxId;
    }

    public long getFreePages() {
        return freePages;
    }

    public void setFreePages(long value) {
        this.freePages = value;
    }
}
