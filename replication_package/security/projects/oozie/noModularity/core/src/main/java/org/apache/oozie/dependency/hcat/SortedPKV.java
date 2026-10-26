package org.apache.oozie.dependency.hcat;

import org.apache.oozie.dependency.hcat.cache.EhcacheHCatDependencyCache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class SortedPKV {
    private StringBuilder partKeys;
    private StringBuilder partVals;

    public SortedPKV(Map<String, String> partitions) {
        this.partKeys = new StringBuilder();
        this.partVals = new StringBuilder();
        ArrayList<String> keys = new ArrayList<String>(partitions.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            this.partKeys.append(key).append(EhcacheHCatDependencyCache.PARTITION_DELIMITER);
            this.partVals.append(partitions.get(key)).append(EhcacheHCatDependencyCache.PARTITION_DELIMITER);
        }
        this.partKeys.setLength(partKeys.length() - 1);
        this.partVals.setLength(partVals.length() - 1);
    }

    public String getPartKeys() {
        return partKeys.toString();
    }

    public String getPartVals() {
        return partVals.toString();
    }

}
