package org.apache.activemq.store.kahadb.disk.page;

import org.apache.activemq.store.kahadb.disk.page.engine.TransactionTest;
import org.apache.activemq.store.kahadb.disk.util.sequence.Marshaller;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TransactionTestMarshaller implements Marshaller<List<Byte>> {

    public static TransactionTestMarshaller INSTANCE = new TransactionTestMarshaller();

    @Override
    public void writePayload(final List<Byte> object, final DataOutput dataOut) throws IOException {
        for (Byte b : object) {
            dataOut.write(b);
        }
    }

    @Override
    public List<Byte> readPayload(final DataInput dataIn) throws IOException {
        List<Byte> result = new ArrayList<>();
        for (int i = 0; i < TransactionTest.getNumberOfBytes(); i++) {
            result.add(dataIn.readByte());
        }

        return result;
    }

    @Override
    public int getFixedSize() {
        return 0;
    }

    @Override
    public boolean isDeepCopySupported() {
        return false;
    }

    @Override
    public List<Byte> deepCopy(final List<Byte> source) {
        return new ArrayList<>(source);
    }
}
