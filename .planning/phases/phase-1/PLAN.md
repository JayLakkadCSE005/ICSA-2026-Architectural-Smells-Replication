---
phase: 1
title: RQ1 — Quality Metrics (Jupyter Notebook)
status: completed
wave: 1
---

# Phase 1 Plan: RQ1 Quality Analysis

## Goal
Execute `quality/Quality.ipynb` on the pre-computed CSVs to reproduce quality metric
results from the paper (RQ1). Verify output matches `QualityResults.xlsx`.

## Input Data (pre-computed, verified ✅)
- `quality/data/Compress/` — 6 CSV files ✅
- `quality/data/jackrabbit/` — 6 CSV files ✅
- `quality/data/jeromq/` — 6 CSV files ✅
- `quality/data/openMrs/` — 6 CSV files ✅
- `quality/data/ApacheRoller/` — 6 CSV files ✅
- `quality/data/active/` — 6 CSV files ✅
- `quality/data/oozie/` — 6 CSV files ✅

## Tasks

### Task 1.1 — Run notebook via nbconvert [Wave 1]
Execute the notebook headlessly to produce output cells.

### Task 1.2 — Verify QualityResults.xlsx output [Wave 1]
Confirm output file was written/matches expectations.

### Task 1.3 — Extract key findings [Wave 2]
Read result summaries and document which metrics improved per project.

## Verification Criteria
- [ ] Notebook runs to completion without errors
- [ ] All 7 projects processed
- [ ] `QualityResults.xlsx` exists and is non-empty
- [ ] Key metrics (CBO, WMC, LCOM) show trends matching paper claims
