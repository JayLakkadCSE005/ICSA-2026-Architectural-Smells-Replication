"""
patch_notebook.py — Patches Quality.ipynb for local execution.
Replaces Google Colab-specific code with local paths.
"""
import json
import os
import sys

NOTEBOOK_PATH = os.path.join(os.path.dirname(__file__), "Quality.ipynb")
DATA_DIR = os.path.abspath(os.path.join(os.path.dirname(__file__), "data"))
EXCEL_PATH = os.path.abspath(os.path.join(os.path.dirname(__file__), "QualityResults.xlsx"))

print(f"Patching: {NOTEBOOK_PATH}")
print(f"Data dir: {DATA_DIR}")
print(f"Excel path: {EXCEL_PATH}")

with open(NOTEBOOK_PATH, "r", encoding="utf-8") as f:
    nb = json.load(f)

patched = 0
for cell in nb["cells"]:
    if cell["cell_type"] != "code":
        continue

    source = cell["source"]
    new_source = []
    cell_patched = False

    for line in source:
        # 1) Replace google.colab import + drive.mount with a no-op comment
        if "from google.colab import drive" in line:
            new_source.append("# [LOCAL PATCH] google.colab removed\n")
            cell_patched = True
            continue
        if "drive.mount(" in line:
            new_source.append("# [LOCAL PATCH] drive.mount removed\n")
            cell_patched = True
            continue

        # 2) Replace the base_path assignment (Colab path → local data dir)
        if '"/content/drive/MyDrive/tesiMagistrale/" + project + "/"' in line:
            # Use raw string with forward slashes for cross-platform safety
            local_data = DATA_DIR.replace("\\", "/")
            new_source.append(
                f'  base_path = r"{DATA_DIR}\\\\" + project + "\\\\"\n'
            )
            cell_patched = True
            continue

        # 3) Replace the Excel load path
        if 'path_excel = "/content/drive/MyDrive/tesiMagistrale/QualityResults.xlsx"' in line:
            new_source.append(
                f'path_excel = r"{EXCEL_PATH}"\n'
            )
            cell_patched = True
            continue

        # 4) Replace the Excel save path
        if 'wb.save("/content/drive/MyDrive/tesiMagistrale/QualityResults.xlsx")' in line:
            new_source.append(
                f'wb.save(r"{EXCEL_PATH}")\n'
            )
            cell_patched = True
            continue

        new_source.append(line)

    if cell_patched:
        cell["source"] = new_source
        patched += 1

print(f"\nPatched {patched} cell(s).")

backup_path = NOTEBOOK_PATH + ".bak"
import shutil
shutil.copy2(NOTEBOOK_PATH, backup_path)
print(f"Original backed up to: {backup_path}")

with open(NOTEBOOK_PATH, "w", encoding="utf-8") as f:
    json.dump(nb, f, indent=1, ensure_ascii=False)

print("Notebook patched successfully!")
print("\nNow run:")
print('  python -m jupyter nbconvert --to notebook --execute --inplace Quality.ipynb')
