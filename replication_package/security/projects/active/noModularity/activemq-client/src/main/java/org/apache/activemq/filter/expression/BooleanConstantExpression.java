package org.apache.activemq.filter.expression;

import jakarta.jms.JMSException;
import org.apache.activemq.filter.eval.MessageEvaluationContext;
import org.apache.activemq.filter.expression.core.BooleanExpression;
import org.apache.activemq.filter.expression.core.ConstantExpression;

public class BooleanConstantExpression extends ConstantExpression implements BooleanExpression {
    public BooleanConstantExpression(Object value) {
        super(value);
    }

    public boolean matches(MessageEvaluationContext message) throws JMSException {
        Object object = evaluate(message);
        return object != null && object == Boolean.TRUE;
    }
}
