package org.apache.oozie.workflow.lite.manager.istance;

class NodeInstance {
    String nodeName;
    boolean started = false;

    NodeInstance(String nodeName) {
        this.nodeName = nodeName;
    }
}
