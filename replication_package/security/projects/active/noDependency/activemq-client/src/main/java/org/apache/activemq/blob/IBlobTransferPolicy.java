package org.apache.activemq.blob;

public interface IBlobTransferPolicy {
	public String getUploadUrl();
	public int getBufferSize();
}
