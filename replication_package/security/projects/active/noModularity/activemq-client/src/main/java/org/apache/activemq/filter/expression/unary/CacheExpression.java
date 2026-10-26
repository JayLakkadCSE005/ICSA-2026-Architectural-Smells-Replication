package org.apache.activemq.filter.expression.unary;

import jakarta.jms.JMSException;
import org.apache.activemq.filter.eval.MessageEvaluationContext;
import org.apache.activemq.filter.expression.core.Expression;
import org.apache.activemq.filter.expression.core.MultiExpressionEvaluator;

/**
 * A UnaryExpression that caches the result of the nested expression. The
 * cached value is valid if the
 * CacheExpression.cview==MultiExpressionEvaluator.view
 */
public class CacheExpression extends UnaryExpression {
    private MultiExpressionEvaluator multiExpressionEvaluator = new MultiExpressionEvaluator();
    public short refCount;
    int cview = multiExpressionEvaluator.view - 1;
    Object cachedValue;
    int cachedHashCode;

    public CacheExpression(MultiExpressionEvaluator multiExpressionEvaluator, Expression realExpression) {
        super(realExpression);
        this.multiExpressionEvaluator = multiExpressionEvaluator;
        cachedHashCode = realExpression.hashCode();
    }

    /**
     * @see Expression#evaluate(MessageEvaluationContext)
     */
    public Object evaluate(MessageEvaluationContext message) throws JMSException {
        if (multiExpressionEvaluator.view == cview) {
            return cachedValue;
        }
        cachedValue = right.evaluate(message);
        cview = multiExpressionEvaluator.view;
        return cachedValue;
    }

    public int hashCode() {
        return cachedHashCode;
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        return ((CacheExpression) o).right.equals(right);
    }

    public String getExpressionSymbol() {
        return null;
    }

    public String toString() {
        return right.toString();
    }

}
