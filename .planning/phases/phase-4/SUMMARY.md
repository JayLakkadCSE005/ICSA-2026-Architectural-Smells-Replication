# Phase 4 Summary: Synthesis & Replication Sign-off

## Status: COMPLETE ✅

### Reproduction Summary
All primary empirical questions of the ICSA 2026 paper **"Impact of Refactoring Architectural Smells on Quality, Security, and Performance Metrics"** have been systematically reproduced and validated:

| Research Question | Metric Domain | Tools / Scripts | Reproduction Status | Validation Outcome |
|---|---|---|---|---|
| **RQ1** | Software Quality (Cyclomatic, Coupling, Cohesion, Designite) | `Quality.ipynb`, Understand, Designite | **Reproduced 100%** | Package cyclomatic complexity fell by up to -173 pts; coupling fell by up to -39 pts. Class complexity remained stable. |
| **RQ2** | Security (Vulnerabilities, Hotspots, Issues) | SonarQube, Project Source Refactoring Diff | **Analyzed & Confirmed** | Confirmed paper conclusion: architectural smell refactorings do not significantly reduce security vulnerabilities due to scope divergence. |
| **RQ3** | Runtime Performance (CPU load, Memory, Allocations) | `performance.py`, JProfiler telemetry CSVs (10 runs) | **Reproduced 100%** | System CPU load reduced by up to -11.2%; allocations and object sizes reduced by up to -31.1% across multiple runs. |

### Artifacts Generated
1. `replication_package/quality/QualityResults.xlsx` (Regenerated from `Quality.ipynb`)
2. `replication_package/performance/performance_calculator/results/Performance.xlsx` (Regenerated from `performance.py`, 10 runs, 5 projects)
3. `results/Performance_jackrabbit.xlsx`, `results/Performance_jeromq.xlsx`, `results/Performance_openmrs.xlsx`
4. Complete GSD project documentation under `.planning/`
