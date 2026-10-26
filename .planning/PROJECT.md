# PROJECT: Replication of ICSA 2026 Paper

## Vision
Fully reproduce all empirical results from the paper:
**"Impact of Refactoring Architectural Smells on Quality, Security, and Performance Metrics"**
(Fontana, Milanese, Refolli, Trubiani — ICSA 2026)

## Objective
Demonstrate — through re-running analyses — that manually refactoring architectural smells
(Cyclic Dependency, Unstable Dependency, Cyclic Hierarchy) in open-source Java projects leads
to measurable improvements in software quality and performance, with less impact on security.

## Scope
- **RQ1**: Quality metrics across 7 projects (Understand + Designite → Jupyter Notebook)
- **RQ2**: Security metrics for ActiveMQ + Oozie (SonarQube + SpotBugs)
- **RQ3**: Performance metrics for 5 projects (JProfiler + Maven + PowerShell automation)

## Projects Analyzed
| Project | RQ1 | RQ2 | RQ3 |
|---|---|---|---|
| Apache ActiveMQ | ✅ | ✅ | ❌ |
| Apache JackRabbit | ✅ | ✅ | ✅ |
| Apache Oozie | ✅ | ✅ | ❌ |
| Apache Roller | ✅ | ❌ | ✅ |
| OpenMRS-core | ✅ | ❌ | ✅ |
| Apache Commons Compress | ✅ | ❌ | ✅ |
| JeroMQ | ✅ | ❌ | ✅ |

## Three Project Versions (per project)
- **Init** — original unmodified upstream code
- **noModularity** — after refactoring modularity smells (Hub-Like, Scattered Functionality)
- **noDependency** — after refactoring dependency smells (Cyclic Dependency, Unstable Dependency)

## Workspace
- Root: `c:\Users\JAY\Downloads\replication_package`
- Paper preprint: https://cs.gssi.it/catia.trubiani/download/2026-ICSA-Architectural-Smells-Quality-Security-Performance.pdf

## Technology Stack
- Python 3.x (analysis scripts, Jupyter notebook)
- Java 8 / 17 / 22 (project builds)
- Apache Maven (build/test runner)
- JProfiler v15 (performance profiling — RQ3)
- SonarQube Community (security analysis — RQ2)
- Understand + Designite (quality extraction — RQ1, pre-computed CSVs included)
- PowerShell (Windows automation)

## Key Constraint
Pre-computed data is available for RQ1 (quality CSVs) and RQ3 (performance CSVs + Excel).
RQ2 security data must be run manually — no raw data files in the package.
