# GitHub Submission & Repository Setup Guide

This guide provides exact, copy-paste instructions to push this local replication package to your personal GitHub account and generate the **Source code GitHub link** for your minor/major project submission.

---

## Step 1: Create a New GitHub Repository

1. Log into your account on [GitHub.com](https://github.com/).
2. In the top right corner, click the **`+`** icon and select **New repository** (or navigate to `https://github.com/new`).
3. Fill in the repository details:
   - **Repository name:** `ICSA-2026-Architectural-Smells-Replication` (or your preferred name, e.g., `minor-project-architectural-smells`)
   - **Description:** *Replication and Empirical Implementation of ICSA 2026 Paper: Impact of Refactoring Architectural Smells on Quality, Security, and Performance Metrics*
   - **Visibility:** Select **Public** (recommended so evaluators can review directly) or **Private** (if required by your university/institution).
   - **Initialize repository:** **DO NOT** check "Add a README file", **DO NOT** add `.gitignore`, and **DO NOT** choose a license (our repository already has all these configured!).
4. Click **Create repository**.

---

## Step 2: Push Local Code to GitHub

Open a terminal (PowerShell or Git Bash) in your project directory (`c:\Users\JAY\Downloads\replication_package`) and run the following commands:

```bash
# 1. Verify you are in the workspace directory
cd c:\Users\JAY\Downloads\replication_package

# 2. Rename default branch to main (standard on GitHub)
git branch -M main

# 3. Add your remote GitHub repository (replace with your actual GitHub username and repo name)
git remote add origin https://github.com/<YOUR_GITHUB_USERNAME>/<YOUR_REPOSITORY_NAME>.git

# 4. Push your codebase and all commits to GitHub
git push -u origin main
```

*(Note: If prompted, enter your GitHub username and Personal Access Token or authenticate via GitHub CLI / Browser.)*

---

## Step 3: Your Submission URL

Once pushed, your GitHub repository link will be:
```
https://github.com/<YOUR_GITHUB_USERNAME>/<YOUR_REPOSITORY_NAME>
```

Paste this URL into your project submission form as the **Source code GitHub link**.

---

## Step 4: What Evaluators Will See on Your GitHub Repo

Your repository has been structured for academic evaluation:
1. **Root `README.md`:** Displays the paper citation, abstract, architectural diagram, 1-command reproduction steps, and result summary tables.
2. **`submission/` Folder:** Contains all evaluation deliverables:
   - `Selected_Paper_ICSA_2026.pdf` (The original research paper PDF)
   - `Project_Report.md` & `Project_Report.html` (Complete academic project report)
   - `Presentation_Slides.pptx` & `slides.html` (13-slide PowerPoint & web presentation)
   - `PPT_Slides_Content.md` (Detailed slide outlines & speaker notes)
   - `Video_Presentation_Script_10min.md` (Word-for-word 10-minute video presentation guide)
   - `architecture_diagrams/` (High-resolution SVG and PNG diagrams + interactive gallery)
3. **Execution Pipelines:**
   - `replication_package/quality/Quality.ipynb` (Quality pipeline for RQ1)
   - `replication_package/performance/performance_calculator/src/performance.py` (Performance pipeline for RQ3)
   - Pre-computed datasets, results workbooks (`QualityResults.xlsx`, `Performance.xlsx`), and refactored Java source code.
