---
phase: 0
title: Environment Setup & Verification
status: in_progress
wave: 1
---

# Phase 0 Plan: Environment Setup & Verification

## Goal
Install and verify all required tools. Confirm pre-computed data integrity. Set up `.gitignore`.

## Tasks

### Task 0.1 — Create .gitignore [Wave 1]
Prevent build artifacts, Java targets, Python cache from polluting the repo.

### Task 0.2 — Verify Python environment [Wave 1]
Check Python version and install required packages:
- `pandas`, `openpyxl`, `numpy` (for performance.py)
- `scipy`, `matplotlib`, `seaborn`, `jupyter`, `notebook` (for Quality.ipynb)

### Task 0.3 — Verify Java & Maven [Wave 1]
Confirm `java -version` and `mvn -version` work.
Note: full Java multi-version setup only needed for RQ3 full re-run.

### Task 0.4 — Verify pre-computed data integrity [Wave 1]
Count CSV files in performance_data/ and quality/data/ to confirm they match README.

### Task 0.5 — Verify Docker availability [Wave 1]
Check if Docker is installed (needed for SonarQube in Phase 3).

## Verification
- [ ] Python packages install without errors
- [ ] `import pandas, openpyxl, scipy, matplotlib` all succeed
- [ ] `java -version` returns output
- [ ] `mvn -version` returns output
- [ ] performance_data/compress/ has 90 CSV files
- [ ] quality/data/ has 7 project directories with 6 CSVs each
