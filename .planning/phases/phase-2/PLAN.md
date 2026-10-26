---
phase: 2
title: RQ3 — Performance Metrics (Script Re-run)
status: completed
wave: 1
---

# Phase 2 Plan: RQ3 Performance Analysis

## Goal
Run `performance.py` on the pre-computed CSV telemetry files to regenerate
`Performance.xlsx`, verifying it matches the authors' output.

## Input Data (pre-computed, verified ✅)
- `performance_data/compress/` — 90 CSV files ✅
- `performance_data/jackrabbit/` — 810 CSV files ✅
- `performance_data/jeromq/` — 180 CSV files ✅
- `performance_data/openmrs/` — 180 CSV files ✅
- `performance_data/roller/` — 90 CSV files ✅

## Tasks

### Task 2.1 — Install performance Python dependencies [Wave 1]
`pip install -r requirements.txt` in performance_calculator/

### Task 2.2 — Run performance.py [Wave 1]
Execute performance.py from the src/ directory. This reads pre-computed CSVs
and writes Performance.xlsx.

### Task 2.3 — Verify and review Performance.xlsx [Wave 2]
Open and inspect the output:
- FinalAverage sheet
- Improvement sheet
- Regression sheet
- Compare to pre-existing results/Performance.xlsx

## Verification Criteria
- [ ] `performance.py` runs without error
- [ ] `Performance.xlsx` generated with all expected sheets
- [ ] Improvement/Regression tabs populated for all 5 projects
