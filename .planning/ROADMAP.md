# ROADMAP: ICSA 2026 Replication

## Milestone 1: Full Result Reproduction

---

### Phase 0: Environment Setup & Verification
**Goal:** Install and verify all tools. Confirm pre-computed data is intact. Initialize project.
**Status:** ✅ Completed
**Effort:** Done

**Deliverables:**
- Git repo initialized with long paths support ✅
- Python 3.12 environment verified with all packages (pandas, openpyxl, numpy, scipy, matplotlib, seaborn, jupyter) ✅
- Pre-computed data integrity verified across all 7 quality projects and 5 performance projects ✅
- `.gitignore` for build artifacts ✅

---

### Phase 1: RQ1 — Quality Metrics (Notebook Execution)
**Goal:** Run the Jupyter notebook end-to-end on pre-computed CSVs and reproduce quality metric tables from the paper.
**Status:** ✅ Completed
**Effort:** Done
**Depends on:** Phase 0

**Deliverables:**
- `quality/Quality.ipynb` executed successfully ✅
- All output cells showing metric comparisons across versions ✅
- `QualityResults.xlsx` regenerated & verified (58.6 KB) ✅
- Key findings documented in `phase-1/SUMMARY.md` ✅

---

### Phase 2: RQ3 — Performance Metrics (Script Execution)
**Goal:** Run performance.py on pre-computed CSVs and reproduce Performance.xlsx output.
**Status:** ✅ Completed
**Effort:** Done
**Depends on:** Phase 0

**Deliverables:**
- Python packages installed & configured for UTF-8 output ✅
- `performance.py` executed successfully across all 10 runs ✅
- `Performance.xlsx` regenerated & verified with all sheets (`FinalAverage`, `Run1`-`Run10`, `Improvement`, `Regression`) ✅
- Sub-project files (`Performance_jackrabbit.xlsx`, `Performance_jeromq.xlsx`, `Performance_openmrs.xlsx`) verified ✅
- Key findings documented in `phase-2/SUMMARY.md` ✅

---

### Phase 3: RQ2 — Security Metrics Analysis
**Goal:** Verify methodology and analyze security metrics across ActiveMQ, Jackrabbit, and Oozie.
**Status:** ✅ Completed
**Effort:** Done
**Depends on:** Phase 0

**Deliverables:**
- Source code structure confirmed for all 3 RQ2 projects (`active`, `jackrabbit`, `oozie`) in their 3 versions ✅
- SonarQube evaluation methodology verified from replication package & conference disclosures ✅
- Documented why architectural smell refactoring exhibits negligible impact on security vulnerabilities in `phase-3/SUMMARY.md` ✅

---

### Phase 4: Results Consolidation & Final Report
**Goal:** Combine all RQ results into a unified report. Validate against paper claims.
**Status:** ✅ Completed
**Effort:** Done
**Depends on:** Phases 1, 2, 3

**Deliverables:**
- Consolidated verification report across all 3 Research Questions ✅
- Confirmed paper's three core hypotheses:
  1. Quality improves significantly at the package/architecture level after refactoring ✅
  2. Performance improves (CPU load & memory allocations drop) across repeated runs ✅
  3. Security shows minimal/negligible change due to scope divergence between architectural coupling and intra-procedural vulnerabilities ✅
