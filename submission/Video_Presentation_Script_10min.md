# 10-Minute Video Presentation & Demo Script

**Project Title:** Empirical Replication of ICSA 2026 Paper: *Impact of Refactoring Architectural Smells on Quality, Security, and Performance Metrics*  
**Presenter:** Student / Candidate  
**Total Target Duration:** 9 to 10 Minutes  
**Recommended Recording Software:** OBS Studio, Loom, or Windows Game Bar (Win + G)  
**Display Setup:** 1920x1080 Fullscreen; have Browser/PowerPoint, Terminal/VSCode, and Excel open.

---

## Video Timeline Summary

| Time Range | Section | Visual Focus | Spoken Objective |
|---|---|---|---|
| **0:00 – 1:15** | Introduction & Paper Overview | Slide 1 & 2 (Title, Motivation) | Introduce paper, authors, venue (ICSA 2026), and core research question. |
| **1:15 – 2:30** | Architectural Smells & Subject Systems | Slide 3 & 4 (Smells, 7 Systems) | Define Cyclic Dependency, Cyclic Hierarchy, Unstable Dependency, God Components. |
| **2:30 – 3:45** | System Architecture & Replication Pipeline | Slide 5 & `Overall_Workflow_Architecture.png` | Explain the 3 versions (`Init`, `noMod`, `noDep`) and the multi-tool pipeline. |
| **3:45 – 5:30** | RQ1: Software Quality Demonstration | Terminal + Jupyter Notebook + `QualityResults.xlsx` | Show `Quality.ipynb`, explain package complexity drops (-173 pts), show class invariance. |
| **5:30 – 6:45** | RQ2: Security Analysis & Finding | Slide 8 + Project Source Tree | Explain why architectural smell refactoring has negligible impact on security vulnerabilities. |
| **6:45 – 8:30** | RQ3: Runtime Performance Demonstration | Terminal + `performance.py` + `Performance.xlsx` | Run performance verification, show FinalAverage (-11.2% CPU, -31% Memory, -25% allocations). |
| **8:30 – 9:15** | Real-World Refactoring Case Studies | Diagram Gallery (`index.html` / `refactoring-CD.png`) | Walk through Jackrabbit CD fix and ActiveMQ CH consolidation. |
| **9:15 – 10:00** | Conclusion & Submission Sign-Off | Slide 13 (Conclusion) | Summarize key findings, practical takeaways for software architects, and sign off. |

---

## Detailed Minute-by-Minute Script

---

### [0:00 – 1:15] Section 1: Introduction & Research Motivation
**On-Screen:** Slide 1 (Title Slide) transitioning to Slide 2 (Research Motivation).

**Voiceover Script:**
> *"Hello everyone, and welcome to my project presentation. Today, I am presenting the empirical replication and implementation of the ICSA 2026 research paper titled:*  
> **'Impact of Refactoring Architectural Smells on Quality, Security, and Performance Metrics'** *by Francesca Arcelli Fontana, Mattia Milanese, Francesco Refolli, and Catia Trubiani.*
>
> *(Switch to Slide 2)*  
> *In modern software engineering, code-level smells like Long Methods or Duplicated Code are well-studied. However, large systems frequently suffer from **Architectural Smells**—structural design flaws that span across entire packages, modules, and subsystems. These smells accumulate as technical debt, making systems fragile and hard to evolve.*  
>
> *When architects decide to refactor these smells, they face a critical trade-off: Will refactoring actually improve software quality? Does removing architectural debt eliminate security vulnerabilities? And crucially, does decoupling components improve runtime performance, or does it introduce inter-module overhead?*  
>
> *In this project, I have fully implemented and reproduced the authors' replication package on a local environment to answer these questions."*

---

### [1:15 – 2:30] Section 2: Architectural Smells & Subject Systems
**On-Screen:** Slide 3 (Smells Taxonomy) transitioning to Slide 4 (7 Systems).

**Voiceover Script:**
> *(On Slide 3)*  
> *Let us first understand the four major architectural smells evaluated in this research:*  
> *1. **Cyclic Dependency (CD):** where two or more packages depend on each other in a closed loop, preventing autonomous deployment.*  
> *2. **Cyclic Hierarchy (CH):** where a parent class directly depends on its own subclass, inverting the inheritance abstraction.*  
> *3. **Unstable Dependency (UD):** where stable core packages depend on volatile, less stable modules, violating the Stable Dependencies Principle.*  
> *4. **God Components and Feature Concentration:** where monolithic components take on excessive system duties.*
>
> *(Switch to Slide 4)*  
> *The authors evaluated these smells across **seven major open-source Java systems** totaling over 1.5 million lines of code: Apache Jackrabbit, Apache ActiveMQ, Apache Commons Compress, Apache Roller, OpenMRS-Core, JeroMQ, and Apache Oozie.*  
> *They formulated three research questions:*  
> *• RQ1 examines Software Quality metrics,*  
> *• RQ2 investigates Security Vulnerabilities, and*  
> *• RQ3 measures Runtime Performance issues."*

---

### [2:30 – 3:45] Section 3: Replication Framework & Pipeline Architecture
**On-Screen:** Slide 5 or `Overall_Workflow_Architecture.png`.

**Voiceover Script:**
> *(Highlighting the Architecture Diagram)*  
> *Here is the overarching replication pipeline. To isolate the effects of refactoring, each project was evaluated across three distinct codebase versions:*  
> *• **Init:** the original baseline code with documented architectural smells;*  
> *• **noModularity:** the version where modularity smells like God Components were refactored; and*  
> *• **noDependency:** the version where dependency smells like Cyclic Dependencies were eliminated.*  
>
> *All versions preserved 100% test compatibility.*  
> *The evaluation pipeline combines static code analyzers—Arcan, Understand, and Designite for quality metrics, SonarQube for security scanning, and JProfiler 15 with automated test harnesses to capture dynamic runtime telemetry over 10 repeated executions.*  
>
> *In our local replication, we adapted their Google Colab notebook for local execution, patched Windows UTF-8 console encoding conflicts, and automated the verification workflow using Get Shit Done (GSD)."*

---

### [3:45 – 5:30] Section 4: Live Demonstration — RQ1 Quality Metrics
**On-Screen:** Switch screen to VSCode / Terminal, show `Quality.ipynb`, then open `QualityResults.xlsx`.

**Voiceover Script:**
> *(Showing VSCode / Terminal)*  
> *Let us look at the live implementation for RQ1. We adapted `Quality.ipynb` to execute headlessly using `jupyter nbconvert`. The script processes pre-computed metrics from Understand and Designite across all 7 projects.*
>
> *(Switch screen to Excel showing `QualityResults.xlsx`)*  
> *Here is the generated output workbook, `QualityResults.xlsx`. Looking at the results, the empirical evidence is striking:*  
> *• In Apache Oozie, package-level cyclomatic complexity dropped from 327.45 down to 153.88—a reduction of **173.57 points**, or over 53%!*  
> *• In Apache ActiveMQ, package complexity dropped by **160.84 points**.*  
> *• In OpenMRS, package complexity fell by **132.77 points**, while package coupling dropped from 94.77 down to 55.40—a massive **39.37 point decoupling**!*  
> *• In Designite's fan-in and fan-out metrics, Oozie saw a reduction of **58.73 points**, and ActiveMQ dropped by **34.53 points**.*
>
> *(Highlight class-level rows)*  
> *Now notice this crucial observation: while package-level metrics improved drastically, class-level complexity and coupling remained virtually invariant, changing by less than 0.1 across all projects!*  
> *This confirms that architectural refactoring reorganizes component wiring without mutating localized class algorithms."*

---

### [5:30 – 6:45] Section 5: RQ2 Security Analysis & The Abstraction Mismatch
**On-Screen:** Slide 8 (RQ2 Security Results) and briefly show `security/projects/active` in VSCode.

**Voiceover Script:**
> *(Showing Slide 8)*  
> *Now let us turn to RQ2: Does refactoring architectural smells eliminate security vulnerabilities?*  
>
> *The authors analyzed Apache ActiveMQ, Apache Jackrabbit, and Apache Oozie using SonarQube and SpotBugs.*  
> *Their finding—which our analysis confirms—is that **refactoring architectural smells has a negligible impact on security vulnerabilities**.*  
>
> *Why does this disconnect exist? It comes down to an abstraction level mismatch:*  
> *• Architectural smells exist at the **macro package topology level**—cycles between packages or inverted inheritance.*  
> *• In contrast, security vulnerabilities—such as SQL Injection (CWE-89), Insecure Deserialization (CWE-502), or Path Traversal—exist at the **intra-procedural statement and dataflow level**.*  
>
> *Moving a class to a new package or breaking an inheritance cycle does not add input validation or sanitization routines! The practical takeaway for architects is clear: **Never assume architectural refactoring replaces dedicated application security testing (SAST)**."*

---

### [6:45 – 8:30] Section 6: Live Demonstration — RQ3 Runtime Performance
**On-Screen:** Terminal running `python performance.py` (or displaying its output), then switch to `Performance.xlsx` showing the `FinalAverage` and `Improvement` sheets.

**Voiceover Script:**
> *(Showing Terminal / Output)*  
> *Next, let us examine RQ3: Runtime Performance. In our replication, `performance.py` processes over 1,350 JProfiler telemetry CSV files across 10 repeated runs for 5 benchmarked systems.*
>
> *(Switch screen to Excel showing `Performance.xlsx` -> `FinalAverage` sheet)*  
> *Here is the generated `Performance.xlsx` workbook. In the `FinalAverage` tab, we see consistent, measurable runtime improvements:*  
> *• In **Apache Roller**, System CPU load dropped by **11.18%** in the `noModularity` version, and peak CPU load decreased by 7.37 points!*  
> *• In **Apache Jackrabbit**, System CPU load decreased by **6.69%**.*  
> *• In **OpenMRS**, System CPU load dropped by **5.99%**.*  
>
> *(Scroll to Memory & Allocations)*  
> *Looking at memory utilization:*  
> *• In **Apache Commons Compress**, the `NoDependency` refactoring reduced total allocated object size in the heap by **31.10%**, while used memory dropped by **18.85%**—a reduction of nearly 195 megabytes!*  
> *• In **OpenMRS**, object instance count dropped by **25.86%**, and allocated object size fell by **28.12%**.*
>
> *(Click on `Improvement` sheet)*  
> *In the `Improvement` sheet, we can see that these gains are statistically consistent, improving in **7 to 9 runs out of 10**.*  
> *Why does architecture impact performance? Because breaking circular dependencies eliminates circular object reference chains, allowing the JVM Garbage Collector to reclaim heap instances much faster, reducing GC pause times and thread contention."*

---

### [8:30 – 9:15] Section 7: Concrete Refactoring Case Studies
**On-Screen:** Open `submission/architecture_diagrams/index.html` or display `refactoring-CD.png` and `refactoring-CH.png`.

**Voiceover Script:**
> *(Pointing to the diagram gallery)*  
> *To see how these refactorings look in practice, our submission package includes visual case studies from the codebase:*  
> *• In **Apache Jackrabbit**, a cyclic dependency between two packages was resolved by creating a dedicated `exception` package and moving `MalformedPathException`.*  
> *• In **Apache ActiveMQ**, a cyclic hierarchy where `ManagedTransportConnector` duplicated its parent class was resolved by consolidating the logic directly into `TransportConnector`.*  
> *• In Jackrabbit's security module, an unstable dependency was decoupled into a dedicated `SecuritySessionImplManager` utility class.*  
> *These concrete patterns demonstrate exactly how architectural refactoring translates into cleaner design."*

---

### [9:15 – 10:00] Section 8: Conclusion & Submission Sign-Off
**On-Screen:** Slide 13 (Conclusion & Replication Sign-off).

**Voiceover Script:**
> *(On Slide 13)*  
> *In conclusion, this project has achieved a **100% complete empirical reproduction** of the ICSA 2026 paper:*  
> *1. **RQ1 Quality is Confirmed:** Modularity refactoring delivers up to -173 points in package cyclomatic reduction.*  
> *2. **RQ2 Security is Confirmed:** Architectural smell refactoring has negligible impact on security vulnerabilities due to scope divergence.*  
> *3. **RQ3 Performance is Confirmed:** Refactoring achieves up to -11.2% CPU savings and -31.1% heap allocation reductions.*  
>
> *All six required submission deliverables—the Selected Paper PDF, Project Report, Architecture Diagrams, GitHub repository, PPT Slides, and this video presentation—are complete and packaged in the `submission/` directory.*  
>
> *Thank you very much for your time and attention. I am now open to any questions!"*

---

## Practical Recording Tips for the Student
1. **Audio Clarity:** Use a headset or external microphone if possible; speak at a steady, confident pace.
2. **Smooth Transitions:** Keep `slides.html` or `Presentation_Slides.pptx` in one window, VSCode / Terminal in a second window, and `Performance.xlsx` in a third. Use `Alt + Tab` smoothly during transitions.
3. **Rehearsal:** Practice reading the script once before recording; the word count is carefully calibrated to take between 9:00 and 9:45 minutes at a natural speaking speed.
