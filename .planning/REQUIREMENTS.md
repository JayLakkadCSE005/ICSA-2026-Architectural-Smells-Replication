# REQUIREMENTS: Replication Package Implementation

## Functional Requirements

### FR-1: Environment Setup (Phase 0)
- [x] Git repository initialized in workspace root
- [ ] Python 3.x verified with required packages installed
- [ ] Java versions (8, 17, 22) installed and accessible via JAVA_HOME switching
- [ ] Maven installed and on PATH
- [ ] JProfiler v15 installed (for RQ3 only)
- [ ] SonarQube Community running locally or via Docker (for RQ2 only)

### FR-2: RQ1 — Quality Analysis (Phase 1)
- [ ] All 7 project quality CSV files verified present in `quality/data/`
- [ ] `quality/Quality.ipynb` notebook runs end-to-end without errors
- [ ] Statistical tests (Wilcoxon/Mann-Whitney) execute for all metric pairs
- [ ] Output matches `quality/QualityResults.xlsx` from authors
- [ ] Key metrics reported: CBO, WMC, LCOM, RFC, LOC, smell count

### FR-3: RQ3 — Performance Analysis (Phase 2)
- [ ] `performance_data/` pre-computed CSVs verified (90 files per project, 5 projects)
- [ ] `requirements.txt` packages installed
- [ ] `performance.py` runs successfully and produces `Performance.xlsx`
- [ ] Output matches authors' pre-computed `results/Performance.xlsx`
- [ ] Full re-run path documented (requires JProfiler + 8–24h execution)

### FR-4: RQ2 — Security Analysis (Phase 3)
- [ ] SonarQube running at `http://localhost:9000`
- [ ] All 6 project versions built (ActiveMQ Init/noMod/noDep, Oozie Init/noMod/noDep)
- [ ] SonarQube scanner run on all 6 versions
- [ ] SpotBugs run on all 6 versions
- [ ] Security metrics recorded in comparison table
- [ ] Results compared against paper's RQ2 findings

### FR-5: Results Dashboard (Phase 4)
- [ ] All three RQ results consolidated into a single comparison report
- [ ] Statistical significance noted per metric
- [ ] Paper tables replicated with actual values

## Non-Functional Requirements

### NFR-1: Reproducibility
All analysis steps must be documented with exact commands so any researcher can reproduce them.

### NFR-2: Traceability
Each result must be traceable back to the specific tool, project version, and run number.

### NFR-3: Correctness
Results must match or closely approximate the paper's published findings within statistical variance bounds.
