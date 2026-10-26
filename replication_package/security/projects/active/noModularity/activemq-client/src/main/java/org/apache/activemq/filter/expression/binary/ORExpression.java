package org.apache.activemq.filter.expression.binary;

import jakarta.jms.JMSException;
import org.apache.activemq.filter.eval.MessageEvaluationContext;
import org.apache.activemq.filter.expression.core.BooleanExpression;
import org.apache.activemq.filter.expression.core.LogicExpression;

public class ORExpression extends LogicExpression {

    public ORExpression(BooleanExpression lvalue, BooleanExpression rvalue) {
        super(lvalue, rvalue);
    }

    @Override
    public Object evaluate(MessageEvaluationContext message) throws JMSException {
        boolean someNulls = false;
        for (BooleanExpression expression : expressions) {
            Boolean lv = (Boolean) expression.evaluate(message);
            if (lv != null && lv) {
                return Boolean.TRUE;
            }
            if (lv == null) {
                someNulls = true;
            }
        }
        if (someNulls) {
            return null;
        }
        return Boolean.FALSE;
    }

    @Override
    public boolean matches(MessageEvaluationContext message) throws JMSException {
        for (BooleanExpression expression : expressions) {
            boolean lv = expression.matches(message);
            if (lv) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getExpressionSymbol() {
        return "OR";
    }
}
