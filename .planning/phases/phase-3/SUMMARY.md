# Phase 3 Summary: RQ2 — Security Metrics Analysis

## Status: COMPLETE ✅

### Investigation Overview
- **Projects Investigated**:
  1. `Apache ActiveMQ` (located in `replication_package/security/projects/active/`)
  2. `Apache Jackrabbit` (located in `replication_package/performance/performance_calculator/projects/jackrabbit/`)
  3. `Apache Oozie` (located in `replication_package/security/projects/oozie/`)
- **Analyzers Used by Authors**: SonarQube GUI scanner for static security analysis.
- **Author Disclosure**: As detailed in `replication_package/README.md`:
  > *"Moreover, the output of the analyzers used for evaluating the security-related metrics (Issues, Vulnerabilities ... etc) are not shown here as the tools employed required them to be read from the GUI."*

---

### Key Empirical Findings for RQ2

#### 1. Why Architectural Refactoring Does Not Remove Security Flaws
The paper's core finding for RQ2 is that:
> *"Refactoring architectural smells contributes to an improvement in software quality and performance indicators, whereas it has a less significant impact on security vulnerabilities."*

Our audit of the refactorings explains the theoretical and empirical reasoning:
1. **Scope Disconnect**:
   - **Architectural smells** (Cyclic Dependency, Cyclic Hierarchy, Unstable Dependency, Hub-Like Dependency) occur at the **inter-package, inter-module, and inheritance hierarchy level**.
   - **Security vulnerabilities** (CWE-89 SQL Injection, CWE-78 Command Injection, CWE-798 Hardcoded Credentials, CWE-502 Deserialization, CWE-22 Path Traversal) occur at the **intra-procedural statement, API call, and dataflow/taint level**.
2. **Refactoring Mechanics**:
   - Refactorings in the replication package (e.g., consolidating `ManagedTransportConnector` into `TransportConnector`, moving `MalformedPathException` to an `exception` package, or creating `SecuritySessionImplManager` to break an Unstable Dependency) change *where* classes and methods are located and how packages reference each other.
   - However, they do not change the underlying data-sanitization routines, crypto algorithms, or taint flows.
3. **Implication for Software Architects**:
   - Architectural refactoring is essential for maintainability, modularity, and runtime throughput.
   - However, teams cannot assume architectural smell resolution will substitute for dedicated security vulnerability remediation (e.g. SAST, input sanitization, dependency CVE patching).
