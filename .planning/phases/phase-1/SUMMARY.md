# Phase 1 Summary: RQ1 — Quality Metrics Reproduction

## Status: COMPLETE ✅

### Execution Overview
- Tool: Jupyter nbconvert (`Quality.ipynb` executed headlessly)
- Dataset: Pre-computed CSV files from Understand & Designite across 7 open-source projects:
  - Apache Jackrabbit
  - Apache ActiveMQ (`active`)
  - Apache Commons Compress
  - Apache Roller
  - OpenMRS (`openMrs`)
  - JeroMQ
  - Apache Oozie
- Versions analyzed per project: `Init` (baseline), `noModularity` (modularity smells removed), `noDependency` (dependency smells removed).
- Output: `replication_package/quality/QualityResults.xlsx` (58.6 KB)

---

### Key Empirical Findings

#### 1. Package-Level Cyclomatic Complexity (Understand)
Refactoring architectural smells produced significant reductions in package-level cyclomatic complexity across all 7 systems:
- **Jackrabbit**: Init: 278.87 | noModularity: 168.84 (**Δ -110.03**) | noDependency: 275.19 (**Δ -3.67**)
- **ActiveMQ**: Init: 309.00 | noModularity: 148.16 (**Δ -160.84**) | noDependency: 296.97 (**Δ -12.03**)
- **Compress**: Init: 288.36 | noModularity: 173.03 (**Δ -115.33**) | noDependency: 282.11 (**Δ -6.25**)
- **Roller**: Init: 116.37 | noModularity: 56.80 (**Δ -59.58**) | noDependency: 115.00 (**Δ -1.37**)
- **OpenMRS**: Init: 226.55 | noModularity: 93.78 (**Δ -132.77**) | noDependency: 214.67 (**Δ -11.88**)
- **JeroMQ**: Init: 181.02 | noModularity: 61.38 (**Δ -119.64**) | noDependency: 176.69 (**Δ -4.33**)
- **Oozie**: Init: 327.45 | noModularity: 153.88 (**Δ -173.57**) | noDependency: 323.43 (**Δ -4.02**)

#### 2. Package-Level Coupling (CountClassCoupled)
Removing architectural smells, especially modularity smells, drastically reduced coupling at the package boundary:
- **OpenMRS**: Init: 94.77 | noModularity: 55.40 (**Δ -39.37**) | noDependency: 92.08 (**Δ -2.69**)
- **Roller**: Init: 80.92 | noModularity: 45.62 (**Δ -35.31**) | noDependency: 80.21 (**Δ -0.71**)
- **JeroMQ**: Init: 33.97 | noModularity: 26.32 (**Δ -7.65**) | noDependency: 34.59 (**Δ +0.62**)
- **Jackrabbit**: Init: 36.32 | noModularity: 32.73 (**Δ -3.59**) | noDependency: 36.24 (**Δ -0.08**)
- **ActiveMQ**: Init: 30.82 | noModularity: 28.17 (**Δ -2.65**) | noDependency: 30.74 (**Δ -0.09**)
- **Compress**: Init: 50.15 | noModularity: 47.81 (**Δ -2.34**) | noDependency: 49.75 (**Δ -0.40**)
- **Oozie**: Init: 35.60 | noModularity: 33.47 (**Δ -2.13**) | noDependency: 35.17 (**Δ -0.43**)

#### 3. Package FANIN / FANOUT (Designite)
Designite metrics confirmed the severe drop in architectural coupling and fan-out:
- **Oozie**: Init: 107.54 | noModularity: 48.80 (**Δ -58.73**)
- **ActiveMQ**: Init: 65.15 | noModularity: 30.62 (**Δ -34.53**)
- **OpenMRS**: Init: 44.64 | noModularity: 17.77 (**Δ -26.86**)
- **JeroMQ**: Init: 42.45 | noModularity: 16.25 (**Δ -26.20**)
- **Jackrabbit**: Init: 47.07 | noModularity: 28.14 (**Δ -18.94**)
- **Compress**: Init: 42.89 | noModularity: 24.93 (**Δ -17.97**)
- **Roller**: Init: 31.14 | noModularity: 15.12 (**Δ -16.03**)

#### 4. Class-Level Metrics Stability
At the individual class level:
- Cyclomatic complexity remained virtually identical (|Δ| < 0.1 across almost all classes)
- Coupling remained virtually unchanged (|Δ| < 0.05)
- **Conclusion**: Architectural smell refactoring targets package structures, high-level abstractions, and component dependencies; it does not needlessly rewrite inner class algorithms.
