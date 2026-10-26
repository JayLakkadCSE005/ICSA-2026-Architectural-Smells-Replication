package org.apache.oozie.cli;

import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.MissingOptionException;

public class OozieGnuParser extends GnuParser {
    private boolean ignoreMissingOption;

    public OozieGnuParser(final boolean ignoreMissingOption) {
        this.ignoreMissingOption = ignoreMissingOption;
    }

    @Override
    protected void checkRequiredOptions() throws MissingOptionException {
        if (!ignoreMissingOption) {
            super.checkRequiredOptions();
        }
    }
}
