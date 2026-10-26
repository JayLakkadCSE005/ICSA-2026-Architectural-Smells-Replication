package org.apache.activemq.filter.expression.listener;

import org.apache.activemq.filter.expression.core.Expression;

import java.util.ArrayList;
import java.util.List;

/**
 * Multiple listeners my be interested in the results of a single
 * expression.
 */
public class ExpressionListenerSet {
    public Expression expression;
    public List<ExpressionListener> listeners = new ArrayList<ExpressionListener>();
}
