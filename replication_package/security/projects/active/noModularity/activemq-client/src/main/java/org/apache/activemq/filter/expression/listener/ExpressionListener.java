package org.apache.activemq.filter.expression.listener;

import org.apache.activemq.filter.eval.MessageEvaluationContext;
import org.apache.activemq.filter.expression.core.Expression;

/**
 * Objects that are interested in the results of an expression should
 * implement this interface.
 */
public interface ExpressionListener {
    void evaluateResultEvent(Expression selector, MessageEvaluationContext message, Object result);
}
