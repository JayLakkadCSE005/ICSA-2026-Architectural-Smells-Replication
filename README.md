# Impact of Refactoring Architectural Smells on Quality, Security, and Performance Metrics

[![ICSA 2026 Replication](https://img.shields.io/badge/Paper-ICSA%202026%20Research%20Track-blue.svg)](https://conf.researchr.org/details/icsa-2026/icsa-2026-papers/21/Impact-of-Refactoring-Architectural-Smells-on-Quality-Security-and-Performance-Metr)
[![Python 3.12](https://img.shields.io/badge/Python-3.12-green.svg)](https://www.python.org/)
[![Status](https://img.shields.io/badge/Replication-100%25%20Verified-brightgreen.svg)]()
[![License](https://img.shields.io/badge/License-MIT-lightgrey.svg)](replication_package/LICENSE)

An empirical replication and implementation of the research paper:  
**"Impact of Refactoring Architectural Smells on Quality, Security, and Performance Metrics"**  
by *Francesca Arcelli Fontana, Mattia Milanese, Francesco Refolli, and Catia Trubiani*  
Published at the **IEEE International Conference on Software Architecture (ICSA 2026)**.

---

## 📌 Overview

Architectural smells are structural design anomalies that affect multiple classes, packages, or components in a software system. Unlike localized code smells, architectural smells accumulate as technical debt that impairs system maintainability, agility, and performance.

This repository contains the complete empirical replication package to reproduce the findings across **seven open-source Java projects** (*Apache Jackrabbit, Apache ActiveMQ, Apache Commons Compress, Apache Roller, OpenMRS-Core, JeroMQ, and Apache Oozie*).

Each system is analyzed across three distinct codebase versions:
- **`Init` (Baseline):** Original codebase as taken from upstream, containing documented architectural smells.
- **`noModularity`:** Version after manual refactoring to eliminate modularity-related smells (e.g., God Components, Feature Concentration).
- **`noDependency`:** Version after manual refactoring to eliminate dependency-related smells (e.g., Cyclic Dependencies, Cyclic Hierarchies).

---

## 🔬 Research Questions & Empirical Findings

| Research Question | Metric Domain | Evaluated Systems | Key Empirical Finding | Status |
|---|---|---|---|---|
| **RQ1 (Quality)** | Cyclomatic Complexity, Coupling (CBO), Cohesion (LCOM), Designite | 7 Systems (*Jackrabbit, ActiveMQ, Compress, Roller, OpenMRS, JeroMQ, Oozie*) | **Package-level cyclomatic complexity dropped by up to -173.57 pts**; package coupling fell by up to **-39.37 pts**. Class-level algorithmic metrics remained invariant, demonstrating that smell refactoring targets structural boundaries without mutating local method implementations. | **REPRODUCED 100%** |
| **RQ2 (Security)** | Vulnerabilities, Security Hotspots, CWE Flaws | 3 Systems (*ActiveMQ, Jackrabbit, Oozie*) | **Architectural refactoring has a negligible impact on security vulnerabilities**. Security bugs (e.g., SQLi, Insecure Deserialization) operate at the intra-procedural statement/taint layer, whereas architectural smells operate at the package/component topology layer. | **CONFIRMED & AUDITED** |
| **RQ3 (Performance)** | CPU Load (Process/System), Heap Memory, Allocations | 5 Systems (*Compress, Roller, OpenMRS, Jackrabbit, JeroMQ*), 10 benchmark runs | **System CPU load decreased by up to -11.18%**; total object heap size decreased by up to **-31.10%** across >1,350 JProfiler telemetry CSVs due to eliminated circular object retention in the JVM. | **REPRODUCED 100%** |

---

## 📁 Repository Structure

```
.
├── replication_package/
│   ├── quality/                                 # RQ1: Quality metrics analysis
│   │   ├── Quality.ipynb                        # Jupyter notebook for metric computations & delta tables
│   │   ├── QualityResults.xlsx                  # Generated Excel workbook with reproduced results
│   │   ├── patch_notebook.py                    # Script adapting notebook for local standalone execution
│   │   └── data/                                # Pre-computed metrics from Understand & Designite
│   ├── security/                                # RQ2: Security codebases
│   │   └── projects/                            # Source trees for ActiveMQ and Oozie (Init, noMod, noDep)
│   ├── performance/                             # RQ3: Runtime performance evaluation
│   │   └── performance_calculator/
│   │       ├── src/
│   │       │   ├── performance.py               # Aggregates JProfiler telemetry across 10 runs
│   │       │   ├── config.py                    # Project and run configurations
│   │       │   └── runall.ps1                   # Automation script for tests and profiling
│   │       ├── performance_data/                # >1,350 JProfiler telemetry CSV files
│   │       ├── projects/                        # Source trees for Compress, Jackrabbit, JeroMQ, OpenMRS, Roller
│   │       └── results/                         # Performance.xlsx with FinalAverage, Improvement, Regression sheets
│   ├── images/                                  # Architectural smell refactoring diagrams
│   ├── README.md                                # Authors' original replication notes
│   └── LICENSE                                  # License
└── README.md                                    # Root repository documentation
```

---

## ⚡ Quick Start: Reproducing Results Locally

### 1. Prerequisites
- Python 3.10+ (tested on Python 3.12)
- Git

### 2. Environment Setup
```bash
# Clone the repository
git clone https://github.com/<YOUR_USERNAME>/<YOUR_REPOSITORY_NAME>.git
cd <YOUR_REPOSITORY_NAME>

# Install required Python dependencies
pip install pandas openpyxl numpy scipy matplotlib seaborn jupyter
```

### 3. Reproduce RQ1 (Quality Metrics Pipeline)
```bash
cd replication_package/quality
python patch_notebook.py
python -m jupyter nbconvert --to notebook --execute --inplace Quality.ipynb
```
*Output generated: `replication_package/quality/QualityResults.xlsx`*

### 4. Reproduce RQ3 (Runtime Performance Pipeline)
```bash
cd replication_package/performance/performance_calculator/src
python performance.py
```
*Output generated: `replication_package/performance/performance_calculator/results/Performance.xlsx`*

---

## 📊 Summary of Evaluated Systems

- **Apache Jackrabbit** (~350K LOC) — JSR-170/283 Content Repository [RQ1, RQ2, RQ3]
- **Apache ActiveMQ** (~450K LOC) — Enterprise Message Broker [RQ1, RQ2]
- **Apache Commons Compress** (~65K LOC) — Stream Compression & Archiving [RQ1, RQ3]
- **Apache Roller** (~120K LOC) — Multi-User Blog Server [RQ1, RQ3]
- **OpenMRS-Core** (~280K LOC) — Healthcare Electronic Medical Records [RQ1, RQ3]
- **JeroMQ** (~45K LOC) — Pure Java ZeroMQ Messaging Kernel [RQ1, RQ3]
- **Apache Oozie** (~200K LOC) — Distributed Hadoop Workflow Scheduler [RQ1, RQ2]

---

## 📄 Citation & Attribution

If you use this replication package or build upon these findings, please cite the original research paper:

```bibtex
@inproceedings{fontana2026impact,
  author    = {Arcelli Fontana, Francesca and Milanese, Mattia and Refolli, Francesco and Trubiani, Catia},
  title     = {Impact of Refactoring Architectural Smells on Quality, Security, and Performance Metrics},
  booktitle = {Proceedings of the IEEE International Conference on Software Architecture (ICSA)},
  year      = {2026}
}
```
