package org.apache.oozie.dependency.handler;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;

public class FSContext extends Context {

    private FileSystem fs;

    /**
     * Create a FSContext that can be used to access a filesystem URI
     *
     * @param conf Configuration to access the URI
     * @param user name of the user the URI should be accessed as
     * @param fs   FileSystem to access
     */
    public FSContext(Configuration conf, String user, FileSystem fs) {
        super(conf, user);
        this.fs = fs;
    }

    /**
     * Get the FileSystem to access the URI
     *
     * @return FileSystem to access the URI
     */
    public FileSystem getFileSystem() {
        return fs;
    }
}
