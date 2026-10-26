package org.apache.activemq.broker.region.virtual;

public interface VirtualInterceptor {
	public String getPrefix();
	public String getPostfix();
	public boolean isLocal();
	public boolean isConcurrentSend();
	public boolean isTransactedSend();
	public boolean isDropOnResourceLimit();
	public boolean isSetOriginalDestination();
}
