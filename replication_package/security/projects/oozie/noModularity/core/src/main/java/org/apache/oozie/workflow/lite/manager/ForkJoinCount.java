package org.apache.oozie.workflow.lite.manager;

// Tiny utility class where we keep track of how many fork and join nodes we have found
class ForkJoinCount {
    int forks = 0;
    int joins = 0;
}
