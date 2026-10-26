package org.apache.oozie.cli;

import com.google.common.io.ByteStreams;

import java.io.*;
import java.nio.charset.StandardCharsets;

abstract class OutputReaderTemplate {
    protected String read() throws IOException {
        ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
        PipedOutputStream pipeOut = new PipedOutputStream();
        PipedInputStream pipeIn = new PipedInputStream(pipeOut, 1024 * 50);
        System.setOut(new PrintStream(pipeOut, false, StandardCharsets.UTF_8.name()));
        execute();
        pipeOut.close();
        ByteStreams.copy(pipeIn, outBytes);
        pipeIn.close();
        return new String(outBytes.toByteArray(), StandardCharsets.UTF_8);
    }

    abstract void execute() throws IOException;
}
