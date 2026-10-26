package org.apache.oozie.workflow.lite.manager;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Writable;
import org.apache.oozie.util.WritableUtils;
import org.apache.oozie.util.XConfiguration;
import org.apache.oozie.util.XmlUtils;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

class GlobalSectionData implements Writable {
    String jobTracker;
    String nameNode;
    List<String> jobXmls;
    Configuration conf;

    public GlobalSectionData() {
    }

    public GlobalSectionData(String jobTracker, String nameNode, List<String> jobXmls, Configuration conf) {
        this.jobTracker = jobTracker;
        this.nameNode = nameNode;
        this.jobXmls = jobXmls;
        this.conf = conf;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        WritableUtils.writeStr(dataOutput, jobTracker);
        WritableUtils.writeStr(dataOutput, nameNode);

        if (jobXmls != null && !jobXmls.isEmpty()) {
            dataOutput.writeInt(jobXmls.size());
            for (String content : jobXmls) {
                WritableUtils.writeStr(dataOutput, content);
            }
        } else {
            dataOutput.writeInt(0);
        }
        if (conf != null) {
            WritableUtils.writeStr(dataOutput, XmlUtils.prettyPrint(conf).toString());
        } else {
            WritableUtils.writeStr(dataOutput, null);
        }
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        jobTracker = WritableUtils.readStr(dataInput);
        nameNode = WritableUtils.readStr(dataInput);
        int length = dataInput.readInt();
        if (length > 0) {
            jobXmls = new ArrayList<String>();
            for (int i = 0; i < length; i++) {
                jobXmls.add(WritableUtils.readStr(dataInput));
            }
        }
        String confString = WritableUtils.readStr(dataInput);
        if (confString != null) {
            conf = new XConfiguration(new StringReader(confString));
        }
    }
}
