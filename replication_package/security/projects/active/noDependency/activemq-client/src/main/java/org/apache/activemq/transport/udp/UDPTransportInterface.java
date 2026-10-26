package org.apache.activemq.transport.udp;

import java.io.IOException;

import org.apache.activemq.transport.reliable.ReplayBuffer;
import org.apache.activemq.transport.reliable.Replayer;
import org.apache.activemq.util.IntSequenceGenerator;

public interface UDPTransportInterface {
	public IntSequenceGenerator getSequenceGenerator();
	 public Replayer createReplayer() throws IOException;
	 public void setReplayBuffer(ReplayBuffer replayBuffer) throws IOException;
}
