package org.apache.activemq.store.kahadb.disk.util.tools;

public class Report {

    public int size;

    public int writes;
    public long writeDuration;

    public int syncWrites;
    public long syncWriteDuration;

    public int reads;
    public long readDuration;

    @Override
    public String toString() {
        return "Writes: \n" + "  " + writes + " writes of size " + size + " written in " + (writeDuration / 1000.0) + " seconds.\n" + "  " + getWriteRate()
                + " writes/second.\n" + "  " + getWriteSizeRate() + " megs/second.\n" + "\n" + "Sync Writes: \n" + "  " + syncWrites + " writes of size "
                + size + " written in " + (syncWriteDuration / 1000.0) + " seconds.\n" + "  " + getSyncWriteRate() + " writes/second.\n" + "  "
                + getSyncWriteSizeRate() + " megs/second.\n" + "\n" + "Reads: \n" + "  " + reads + " reads of size " + size + " read in "
                + (readDuration / 1000.0) + " seconds.\n" + "  " + getReadRate() + " writes/second.\n" + "  " + getReadSizeRate() + " megs/second.\n" + "\n"
                + "";
    }

    private float getWriteSizeRate() {
        float rc = writes;
        rc *= size;
        rc /= (1024 * 1024); // put it in megs
        rc /= (writeDuration / 1000.0); // get rate.
        return rc;
    }

    private float getWriteRate() {
        float rc = writes;
        rc /= (writeDuration / 1000.0); // get rate.
        return rc;
    }

    private float getSyncWriteSizeRate() {
        float rc = syncWrites;
        rc *= size;
        rc /= (1024 * 1024); // put it in megs
        rc /= (syncWriteDuration / 1000.0); // get rate.
        return rc;
    }

    private float getSyncWriteRate() {
        float rc = syncWrites;
        rc /= (syncWriteDuration / 1000.0); // get rate.
        return rc;
    }

    private float getReadSizeRate() {
        float rc = reads;
        rc *= size;
        rc /= (1024 * 1024); // put it in megs
        rc /= (readDuration / 1000.0); // get rate.
        return rc;
    }

    private float getReadRate() {
        float rc = reads;
        rc /= (readDuration / 1000.0); // get rate.
        return rc;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getWrites() {
        return writes;
    }

    public void setWrites(int writes) {
        this.writes = writes;
    }

    public long getWriteDuration() {
        return writeDuration;
    }

    public void setWriteDuration(long writeDuration) {
        this.writeDuration = writeDuration;
    }

    public int getSyncWrites() {
        return syncWrites;
    }

    public void setSyncWrites(int syncWrites) {
        this.syncWrites = syncWrites;
    }

    public long getSyncWriteDuration() {
        return syncWriteDuration;
    }

    public void setSyncWriteDuration(long syncWriteDuration) {
        this.syncWriteDuration = syncWriteDuration;
    }

    public int getReads() {
        return reads;
    }

    public void setReads(int reads) {
        this.reads = reads;
    }

    public long getReadDuration() {
        return readDuration;
    }

    public void setReadDuration(long readDuration) {
        this.readDuration = readDuration;
    }
}
