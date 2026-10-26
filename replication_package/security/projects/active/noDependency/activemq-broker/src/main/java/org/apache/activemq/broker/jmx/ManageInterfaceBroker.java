package org.apache.activemq.broker.jmx;

import javax.management.ObjectName;

import org.apache.activemq.broker.region.Subscription;

public interface ManageInterfaceBroker {
	public ObjectName getSubscriberObjectName(Subscription key);
	public Subscription getSubscriber(ObjectName key);
}
