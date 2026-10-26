package org.apache.activemq.store.kahadb.disk.page.engine;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;

/**
 * Use to keep track of updated pages which have not yet been committed.
 */
public class PageWrite {
    Page page;
    byte[] current;
    byte[] diskBound;
    long currentLocation = -1;
    long diskBoundLocation = -1;
    File tmpFile;
    int length;

    public PageWrite(Page page, byte[] data) {
        this.page = page;
        current = data;
    }

    public PageWrite(Page page, long currentLocation, int length, File tmpFile) {
        this.page = page;
        this.currentLocation = currentLocation;
        this.tmpFile = tmpFile;
        this.length = length;
    }

    public void setCurrent(Page page, byte[] data) {
        this.page = page;
        current = data;
        currentLocation = -1;
    }

    public void setCurrentLocation(Page page, long location, int length) {
        this.page = page;
        this.currentLocation = location;
        this.length = length;
        this.current = null;
    }

    @Override
    public String toString() {
        return "[PageWrite:" + page.getPageId() + "-" + page.getType() + "]";
    }

    @SuppressWarnings("unchecked")
    public Page getPage() {
        return page;
    }

    public byte[] getDiskBound(HashMap<File, RandomAccessFile> tmpFiles) throws IOException {
        if (diskBound == null && diskBoundLocation != -1) {
            diskBound = new byte[length];
            if (tmpFiles.containsKey(tmpFile) && tmpFiles.get(tmpFile).getChannel().isOpen()) {
                RandomAccessFile file = tmpFiles.get(tmpFile);
                file.seek(diskBoundLocation);
                file.read(diskBound);
            } else {
                try (RandomAccessFile file = new RandomAccessFile(tmpFile, "r")) {
                    file.seek(diskBoundLocation);
                    file.read(diskBound);
                }
            }
            diskBoundLocation = -1;
        }
        return diskBound;
    }

    void begin() {
        if (currentLocation != -1) {
            diskBoundLocation = currentLocation;
        } else {
            diskBound = current;
        }
        current = null;
        currentLocation = -1;
    }

    /**
     * @return true if there is no pending writes to do.
     */
    boolean done() {
        diskBoundLocation = -1;
        diskBound = null;
        return current == null || currentLocation == -1;
    }

    boolean isDone() {
        return diskBound == null && diskBoundLocation == -1 && current == null && currentLocation == -1;
    }
}
