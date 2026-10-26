package org.apache.activemq.filter.expression.binary;

import jakarta.jms.JMSException;
import org.apache.activemq.filter.eval.MessageEvaluationContext;
import org.apache.activemq.filter.expression.core.BooleanExpression;
import org.apache.activemq.filter.expression.core.LogicExpression;

public class ANDExpression extends LogicExpression {

    public ANDExpression(BooleanExpression lvalue, BooleanExpression rvalue) {
        super(lvalue, rvalue);
    }

    @Override
    public Object evaluate(MessageEvaluationContext message) throws JMSException {
        boolean someNulls = false;
        for (BooleanExpression expression : expressions) {
            Boolean lv = (Boolean) expression.evaluate(message);
            if (lv != null && !lv) {
                return Boolean.FALSE;
            }
            if (lv == null) {
                someNulls = true;
            }
        }
        if (someNulls) {
            return null;
        }
        return Boolean.TRUE;
    }

    @Override
    public boolean matches(MessageEvaluationContext message) throws JMSException {
        for (BooleanExpression expression : expressions) {
            boolean lv = expression.matches(message);
            if (!lv) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String getExpressionSymbol() {
        return "AND";
    }
}
