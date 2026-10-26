package org.apache.oozie.coordinator;

public enum FILTER_COMPARATORS {
    //This ordering is important, dont change this
    GREATER_EQUAL(">="), GREATER(">"), LESSTHAN_EQUAL("<="), LESSTHAN("<"), NOT_EQUALS("!="), EQUALS("=");

    private final String sign;

    FILTER_COMPARATORS(String sign) {
        this.sign = sign;
    }

    public String getSign() {
        return sign;
    }
}
