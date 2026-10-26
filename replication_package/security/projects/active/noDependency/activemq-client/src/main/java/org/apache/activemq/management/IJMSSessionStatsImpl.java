package org.apache.activemq.management;

public interface IJMSSessionStatsImpl {
	public CountStatisticImpl getMessageCount();

    public CountStatisticImpl getPendingMessageCount();

    public CountStatisticImpl getExpiredMessageCount();

    public TimeStatisticImpl getMessageWaitTime();
    

    public CountStatisticImpl getDurableSubscriptionCount();
    public TimeStatisticImpl getMessageRateTime();
}
