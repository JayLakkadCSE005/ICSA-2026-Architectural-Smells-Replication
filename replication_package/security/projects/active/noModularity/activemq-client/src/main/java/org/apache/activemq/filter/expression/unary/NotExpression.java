package org.apache.activemq.filter.expression.unary;

import jakarta.jms.JMSException;
import org.apache.activemq.filter.eval.MessageEvaluationContext;
import org.apache.activemq.filter.expression.core.BooleanExpression;

class NotExpression extends BooleanUnaryExpression {
    public NotExpression(BooleanExpression right) {
        super(right);
    }

    public Object evaluate(MessageEvaluationContext message) throws JMSException {
        Boolean lvalue = (Boolean) right.evaluate(message);
        if (lvalue == null) {
            return null;
        }
        return lvalue.booleanValue() ? Boolean.FALSE : Boolean.TRUE;
    }

    @Override
    public boolean matches(MessageEvaluationContext message) throws JMSException {
        Boolean lvalue = (Boolean) right.evaluate(message);
        if (lvalue == null) {
            // NOT NULL returns NULL that eventually fails the selector
            return false;
        }
        return !lvalue;
    }

    public String getExpressionSymbol() {
        return "NOT";
    }
}
