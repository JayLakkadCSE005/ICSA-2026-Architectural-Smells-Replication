package org.apache.activemq.filter.expression.unary;

import jakarta.jms.JMSException;
import org.apache.activemq.filter.eval.MessageEvaluationContext;
import org.apache.activemq.filter.expression.core.BooleanExpression;
import org.apache.activemq.filter.expression.core.ComparisonExpression;
import org.apache.activemq.filter.expression.core.Expression;

import java.util.regex.Pattern;

public class LikeExpression extends UnaryExpression implements BooleanExpression {

    Pattern likePattern;

    /**
     *
     */
    public LikeExpression(Expression right, String like, int escape) {
        super(right);

        StringBuffer regexp = new StringBuffer(like.length() * 2);
        regexp.append("\\A"); // The beginning of the input
        for (int i = 0; i < like.length(); i++) {
            char c = like.charAt(i);
            if (escape == (0xFFFF & c) && shouldEscapeNext(like, i, c)) {
                i++;
                char t = like.charAt(i);
                regexp.append("\\x");
                regexp.append(Integer.toHexString(0xFFFF & t));
            } else {
                append(regexp, c);
            }
        }
        regexp.append("\\z"); // The end of the input

        likePattern = Pattern.compile(regexp.toString(), Pattern.DOTALL);
    }

    private boolean shouldEscapeNext(String selector, int i, char escape) {
        int next = i + 1;
        if (next < selector.length()) {
            final char c = selector.charAt(next);
            return (c == '_' || c == '%' || c == escape);
        }
        return false;
    }

    private void append(StringBuffer regexp, char c) {
        if (c == '%') {
            regexp.append(".*?"); // Do a non-greedy match
        } else if (c == '_') {
            regexp.append("."); // match one
        } else if (ComparisonExpression.REGEXP_CONTROL_CHARS.contains(c)) {
            regexp.append("\\x");
            regexp.append(Integer.toHexString(0xFFFF & c));
        } else {
            regexp.append(c);
        }
    }

    /**
     * @see UnaryExpression#getExpressionSymbol()
     */
    public String getExpressionSymbol() {
        return "LIKE";
    }

    /**
     * @see Expression#evaluate(MessageEvaluationContext)
     */
    public Object evaluate(MessageEvaluationContext message) throws JMSException {

        Object rv = this.getRight().evaluate(message);

        if (rv == null) {
            return null;
        }

        if (!(rv instanceof String)) {
            return Boolean.FALSE;
            // throw new RuntimeException("LIKE can only operate on String
            // identifiers. LIKE attemped on: '" + rv.getClass());
        }

        return likePattern.matcher((String) rv).matches() ? Boolean.TRUE : Boolean.FALSE;
    }

    public boolean matches(MessageEvaluationContext message) throws JMSException {
        Object object = evaluate(message);
        return object != null && object == Boolean.TRUE;
    }
}
