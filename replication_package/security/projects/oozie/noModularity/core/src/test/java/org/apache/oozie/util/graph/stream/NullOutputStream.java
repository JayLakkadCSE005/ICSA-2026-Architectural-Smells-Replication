package org.apache.oozie.util.graph.stream;

import java.io.IOException;
import java.io.OutputStream;

public class NullOutputStream extends OutputStream {
    @Override
    public void write(final int b) throws IOException {
    }
}
