package org.apache.activemq.store.kahadb.scheduler.legacy;

import java.io.IOException;

import org.apache.activemq.store.kahadb.disk.journal.Location;
import org.apache.activemq.store.kahadb.disk.page.PageFile;
import org.apache.activemq.util.ByteSequence;

public interface ILegacyJobSchedulerStoreImpl {
	PageFile getPageFile();
	public ByteSequence getPayload(Location location)throws IllegalStateException, IOException;
}
