package org.apache.activemq.filter.expression;

import jakarta.jms.JMSException;
import org.apache.activemq.filter.eval.MessageEvaluationContext;
import org.apache.activemq.filter.expression.core.ComparisonExpression;
import org.apache.activemq.filter.expression.core.Expression;

public class EqualsExpression extends ComparisonExpression {
    public EqualsExpression(Expression left, Expression right) {
        super(left, right);
    }

    public Object evaluate(MessageEvaluationContext message) throws JMSException {
        Object lv = left.evaluate(message);
        Object rv = right.evaluate(message);

        // If one of the values is null
        if (lv == null ^ rv == null) {
            if (lv == null) {
                return null;
            }
            return Boolean.FALSE;
        }
        if (lv == rv || lv.equals(rv)) {
            return Boolean.TRUE;
        }
        if (lv instanceof Comparable && rv instanceof Comparable) {
            return compare((Comparable) lv, (Comparable) rv);
        }
        return Boolean.FALSE;
    }

    @Override
    public boolean matches(MessageEvaluationContext message) throws JMSException {
        Object lv = left.evaluate(message);
        Object rv = right.evaluate(message);

        // If one of the values is null
        if (lv == null ^ rv == null) {
            return false;
        }
        if (lv == rv || lv.equals(rv)) {
            return true;
        }
        if (lv.getClass() == rv.getClass()) {
            // same class, but 'equals' return false, and they are not the same object
            // there is no point in doing 'compare'
            // this case happens often while comparing non equals Strings
            return false;
        }
        if (lv instanceof Comparable && rv instanceof Comparable) {
            Boolean compareResult = compare((Comparable) lv, (Comparable) rv);
            return compareResult != null && compareResult;
        }
        return false;
    }

    protected boolean asBoolean(int answer) {
        return answer == 0;
    }

    public String getExpressionSymbol() {
        return "=";
    }
}
