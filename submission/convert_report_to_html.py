"""
convert_report_to_html.py - Converts Project_Report.md to a beautifully styled standalone HTML document.
"""
import markdown

with open("submission/Project_Report.md", "r", encoding="utf-8") as f:
    text = f.read()

html_body = markdown.markdown(text, extensions=["tables", "fenced_code"])

template = """<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Project Report - ICSA 2026 Replication</title>
  <style>
    body {
      font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
      line-height: 1.65;
      color: #1e293b;
      background: #f8fafc;
      padding: 40px 20px;
      margin: 0;
    }
    .container {
      max-width: 960px;
      margin: 0 auto;
      background: #ffffff;
      padding: 60px 80px;
      border-radius: 12px;
      box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05), 0 2px 4px -2px rgba(0,0,0,0.05);
      border: 1px solid #e2e8f0;
    }
    h1, h2, h3, h4 {
      color: #0f172a;
      font-weight: 700;
    }
    h1 {
      font-size: 2.1rem;
      border-bottom: 2px solid #e2e8f0;
      padding-bottom: 12px;
      margin-top: 0;
      color: #0f172a;
    }
    h2 {
      font-size: 1.45rem;
      border-bottom: 1px solid #f1f5f9;
      padding-bottom: 8px;
      margin-top: 36px;
      color: #1e3a8a;
    }
    h3 {
      font-size: 1.15rem;
      margin-top: 24px;
      color: #0f172a;
    }
    table {
      width: 100%;
      border-collapse: collapse;
      margin: 24px 0;
      font-size: 0.90rem;
    }
    th, td {
      border: 1px solid #cbd5e1;
      padding: 10px 12px;
      text-align: left;
    }
    th {
      background: #f1f5f9;
      color: #0f172a;
      font-weight: 600;
    }
    tr:nth-child(even) {
      background: #f8fafc;
    }
    blockquote {
      border-left: 4px solid #3b82f6;
      margin: 20px 0;
      padding: 12px 20px;
      background: #eff6ff;
      color: #1e40af;
      border-radius: 0 6px 6px 0;
    }
    code {
      background: #f1f5f9;
      padding: 2px 6px;
      border-radius: 4px;
      font-family: Consolas, monospace;
      font-size: 0.88em;
      color: #0f172a;
    }
    pre {
      background: #0f172a;
      color: #f8fafc;
      padding: 16px;
      border-radius: 8px;
      overflow-x: auto;
    }
    pre code {
      background: none;
      color: inherit;
      padding: 0;
    }
    hr {
      border: none;
      border-top: 1px solid #e2e8f0;
      margin: 32px 0;
    }
    @media print {
      body {
        background: #fff;
        padding: 0;
      }
      .container {
        box-shadow: none;
        border: none;
        padding: 0;
        max-width: 100%;
      }
    }
  </style>
</head>
<body>
  <div class="container">
""" + html_body + """
  </div>
</body>
</html>
"""

with open("submission/Project_Report.html", "w", encoding="utf-8") as f:
    f.write(template)

print("Generated submission/Project_Report.html successfully")
