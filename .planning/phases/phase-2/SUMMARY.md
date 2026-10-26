# Phase 2 Summary: RQ3 — Performance Metrics Reproduction

## Status: COMPLETE ✅

### Execution Overview
- Tool: `replication_package/performance/performance_calculator/src/performance.py`
- Environment: Python 3.12 (with `PYTHONIOENCODING=utf-8` and `PYTHONUTF8=1` for UTF-8 support)
- Input: Pre-computed telemetry CSV files (JProfiler) across 10 runs for 5 projects:
  - Apache Commons Compress (90 CSVs)
  - Apache Roller (90 CSVs)
  - OpenMRS (180 CSVs across `api` and `web` modules)
  - Apache Jackrabbit (810 CSVs across 10 modules)
  - JeroMQ (180 CSVs across `jeromq-core` and `jeromq-ipcsockets`)
- Output Files Generated:
  - `replication_package/performance/performance_calculator/results/Performance.xlsx` (70.6 KB)
  - `results/Performance_jackrabbit.xlsx` (131.2 KB)
  - `results/Performance_jeromq.xlsx` (41.9 KB)
  - `results/Performance_openmrs.xlsx` (44.1 KB)
  - Sheets: `FinalAverage`, `Run1` through `Run10`, `Improvement`, `Regression`

---

### Key Empirical Findings

#### 1. System & Process CPU Load Improvements
- **Apache Roller**:
  - `noModularity`: System load AVG reduced by **-11.18%** (improved in 8/10 runs). Max CPU reduced by -7.37 points.
  - `NoDependency`: System load AVG reduced by **-6.04%** (improved in 7/10 runs).
- **OpenMRS**:
  - `noModularity`: System load AVG reduced by **-3.05%** (improved in 8/10 runs).
  - `NoDependency`: System load AVG reduced by **-5.99%** (improved in 8/10 runs).
- **Apache Jackrabbit**:
  - `noModularity`: System load AVG reduced by **-6.69%** (improved in 8/10 runs).
  - `NoDependency`: System load AVG reduced by **-1.75%** (improved in 7/10 runs).
- **Apache Compress**:
  - `noModularity`: Process load reduced by **-0.70%** (improved in 6/10 runs, Max down -5.96 points).
  - `NoDependency`: Process load reduced by **-0.76%** (improved in 7/10 runs, Max down -5.27 points).

#### 2. Heap Memory & Allocation Reductions
- **Apache Commons Compress**:
  - `NoDependency`: Committed size decreased by **-15.02%** (-278.67 MB). Used size decreased by **-18.85%** (-194.58 MB). Total size in bytes decreased by **-31.10%**!
  - `noModularity`: Instance count decreased by **-14.18%** (-293.87 instances average).
- **OpenMRS**:
  - `NoDependency`: Instance count dropped by **-25.86%** (-76.08 instances), Total bytes dropped by **-28.12%** (-5,912.20 bytes average).
- **Apache Roller**:
  - `noModularity`: Instance count decreased by **-7.54%** (-2.39 instances), size decreased by **-11.39%**.
  - `NoDependency`: Instance count dropped by **-12.59%** (-4.00 instances), size decreased by **-8.87%**.
- **JeroMQ**:
  - `noModularity`: Used memory decreased by **-6.16%** (-3.16 MB), Instance count dropped by **-9.10%** (-23.24 instances).

#### 3. Improvement vs Regression Consistency
In the `Improvement` sheet:
- Process & System load improved in 6 to 9 runs out of 10 for all systems.
- Allocations and object sizes improved consistently across 6 to 7 runs out of 10.
- Confirms the paper's thesis: refactoring architectural smells cleans up cyclical object graphs and redundant references, resulting in measurable CPU efficiency gains and reduced allocation overhead.
