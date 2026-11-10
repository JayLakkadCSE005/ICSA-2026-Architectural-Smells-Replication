"""
generate_diagrams.py - Generate high-quality architecture diagrams for the submission package.
Produces both SVG and PNG versions.
"""

import matplotlib.pyplot as plt
import matplotlib.patches as patches
import os

os.makedirs("submission/architecture_diagrams", exist_ok=True)

# -------------------------------------------------------------
# DIAGRAM 1: Overall Empirical Evaluation & Replication Architecture
# -------------------------------------------------------------
fig, ax = plt.subplots(figsize=(16, 10), dpi=300)
ax.set_xlim(0, 16)
ax.set_ylim(0, 10)
ax.axis("off")

# Title
ax.text(8, 9.6, "Empirical Evaluation Framework & Replication Architecture", 
        fontsize=18, fontweight="bold", ha="center", va="center", color="#1E293B", fontfamily="sans-serif")
ax.text(8, 9.2, "Impact of Refactoring Architectural Smells on Quality, Security, and Performance (ICSA 2026)",
        fontsize=12, style="italic", ha="center", va="center", color="#64748B", fontfamily="sans-serif")

# Helper function to draw rounded boxes
def draw_card(ax, x, y, w, h, bg_color, border_color, title, subtitle="", items=None):
    rect = patches.FancyBboxPatch((x, y), w, h, boxstyle="round,pad=0.1,rounding_size=0.15",
                                  facecolor=bg_color, edgecolor=border_color, linewidth=1.5, zorder=2)
    ax.add_patch(rect)
    ax.text(x + w/2, y + h - 0.35, title, fontsize=11, fontweight="bold", ha="center", va="top", color="#0F172A", zorder=3)
    if subtitle:
        ax.text(x + w/2, y + h - 0.65, subtitle, fontsize=8.5, style="italic", ha="center", va="top", color="#475569", zorder=3)
    if items:
        start_y = y + h - (1.0 if subtitle else 0.7)
        for i, item in enumerate(items):
            ax.text(x + 0.25, start_y - i*0.28, f"• {item}", fontsize=8.5, ha="left", va="top", color="#334155", zorder=3)

# Arrow helper
def draw_arrow(ax, x1, y1, x2, y2, label=""):
    ax.annotate("", xy=(x2, y2), xytext=(x1, y1),
                arrowprops=dict(arrowstyle="->,head_width=0.25,head_length=0.35", color="#3B82F6", lw=2), zorder=4)
    if label:
        ax.text((x1 + x2)/2, (y1 + y2)/2 + 0.15, label, fontsize=8, fontweight="bold", ha="center", color="#1D4ED8",
                bbox=dict(boxstyle="round,pad=0.2", facecolor="#EFF6FF", edgecolor="#BFDBFE", lw=1), zorder=5)

# Layer 1: Target Systems
draw_card(ax, 0.5, 4.0, 3.2, 4.6, "#F8FAFC", "#CBD5E1", "1. Target Java Systems", "7 Open-Source Projects", [
    "Apache Jackrabbit (JCR)",
    "Apache ActiveMQ (Broker)",
    "Apache Commons Compress",
    "Apache Roller (Blog Server)",
    "OpenMRS-Core (Medical)",
    "JeroMQ (Pure Java ZMQ)",
    "Apache Oozie (Workflow)",
    "--- Scale ---",
    "Size: 30K - 500K LOC",
    "Domains: Messaging, CMS,",
    "Storage, Healthcare, BigData"
])

# Layer 2: Smell Detection & Technical Debt
draw_card(ax, 4.4, 6.4, 3.4, 2.2, "#FEF3C7", "#F59E0B", "2. Smell Detection", "Automated Architecture Scanners", [
    "Arcan: CD, CH, UD, HL detection",
    "Designite: God Comp., Feature Conc.",
    "Understand: Package/Class topology",
    "Technical Debt Index (TDI) scored"
])

# Layer 3: Refactoring Versions
draw_card(ax, 4.4, 2.5, 3.4, 3.3, "#F0FDF4", "#22C55E", "3. Architectural Refactoring", "3 Codebase Versions Evaluated", [
    "Init (Baseline): Unmodified code",
    "noModularity: Smells removed",
    "  (Decomposed God Components,",
    "   Package re-modularization)",
    "noDependency: Smells removed",
    "  (Decoupled Cyclic Dependencies,",
    "   Flattened Cyclic Hierarchies)",
    "Guaranteed 100% test compatibility"
])

# Connections from System to Smell Detection and Refactoring
draw_arrow(ax, 3.7, 7.3, 4.4, 7.3, "Analyze")
draw_arrow(ax, 3.7, 4.2, 4.4, 4.2, "Refactor")
draw_arrow(ax, 6.1, 6.4, 6.1, 5.8, "Guides")

# Layer 4: Multi-Dimensional Evaluation (3 Research Questions)
# RQ1: Quality
draw_card(ax, 8.6, 6.4, 3.4, 2.2, "#EFF6FF", "#3B82F6", "RQ1: Software Quality", "Structural & CK Metrics", [
    "Tools: Understand + Designite",
    "Metrics: WMC (Cyclomatic),",
    "  CBO (Coupling), LCOM (Cohesion),",
    "  DIT, NOC, FANIN, FANOUT",
    "Evaluation: Package vs Class levels"
])

# RQ2: Security
draw_card(ax, 8.6, 4.4, 3.4, 1.8, "#FEE2E2", "#EF4444", "RQ2: Security Analysis", "Vulnerabilities & Hotspots", [
    "Tools: SonarQube + SpotBugs",
    "Metrics: Vulnerabilities, Hotspots,",
    "  Security Technical Debt",
    "Projects: ActiveMQ, Jackrabbit, Oozie"
])

# RQ3: Performance
draw_card(ax, 8.6, 1.5, 3.4, 2.7, "#FAF5FF", "#A855F7", "RQ3: Runtime Performance", "Dynamic Profiling Telemetry", [
    "Tool: JProfiler 15 automation",
    "Benchmarks: 10 repeated runs/proj",
    "Metrics: Process & System CPU load,",
    "  Committed/Used/Free Heap memory,",
    "  Recorded Objects & Allocations",
    "Data: >1,350 execution CSVs"
])

# Arrows from Refactored code to the 3 RQ layers
draw_arrow(ax, 7.8, 4.8, 8.6, 7.2, "Evaluate")
draw_arrow(ax, 7.8, 4.2, 8.6, 5.2, "Evaluate")
draw_arrow(ax, 7.8, 3.5, 8.6, 2.8, "Profile")

# Layer 5: Replication Pipeline & Key Findings
draw_card(ax, 12.6, 1.5, 3.0, 7.1, "#F1F5F9", "#475569", "4. Empirical Findings", "Replicated Results Summary", [
    "--- RQ1 Quality: CONFIRMED ---",
    "• Package Cyclomatic: -173 pts",
    "• Package Coupling: -39 pts",
    "• Class metrics: Invariant",
    "• Structural modularity up",
    "",
    "--- RQ2 Security: MINIMAL ---",
    "• Negligible vulnerability impact",
    "• Reason: Smells = Architecture,",
    "  Vulnerabilities = Statement taint",
    "",
    "--- RQ3 Performance: CONFIRMED ---",
    "• System CPU load: -11.2%",
    "• Total object size: -31.1%",
    "• Object count: -25.8%",
    "• Memory heap footprint down",
    "• Consistent across 8/10 runs"
])

draw_arrow(ax, 12.0, 7.4, 12.6, 7.0, "Result")
draw_arrow(ax, 12.0, 5.3, 12.6, 5.0, "Result")
draw_arrow(ax, 12.0, 2.9, 12.6, 3.0, "Result")

# Footer metadata
ax.text(8, 0.4, "Source: Replication of ICSA 2026 Paper • Generated for Academic Project Submission",
        fontsize=9, ha="center", va="center", color="#64748B")

plt.tight_layout()
plt.savefig("submission/architecture_diagrams/Overall_Workflow_Architecture.png", dpi=300, bbox_inches="tight")
plt.savefig("submission/architecture_diagrams/Overall_Workflow_Architecture.svg", bbox_inches="tight")
plt.close()

print("Created Overall_Workflow_Architecture.png and .svg")

# -------------------------------------------------------------
# DIAGRAM 2: Architectural Smells Taxonomy & Refactoring Strategy
# -------------------------------------------------------------
fig, ax = plt.subplots(figsize=(15, 8), dpi=300)
ax.set_xlim(0, 15)
ax.set_ylim(0, 8)
ax.axis("off")

ax.text(7.5, 7.6, "Architectural Smells Taxonomy & Refactoring Solutions", 
        fontsize=16, fontweight="bold", ha="center", va="center", color="#0F172A")
ax.text(7.5, 7.2, "Core architectural antipatterns analyzed in the ICSA 2026 replication study",
        fontsize=11, style="italic", ha="center", va="center", color="#64748B")

smells = [
    {
        "title": "Cyclic Dependency (CD)",
        "color": "#EF4444", "bg": "#FEF2F2",
        "prob": "Two or more packages depend on each other directly or indirectly, forming a closed dependency cycle.",
        "sol": "Extract shared types into a dedicated common/exception package; apply Dependency Inversion Principle (DIP).",
        "case": "Apache Jackrabbit: Moved MalformedPathException to dedicated exception package."
    },
    {
        "title": "Cyclic Hierarchy (CH)",
        "color": "#F59E0B", "bg": "#FFFBEB",
        "prob": "A parent class depends on its own child/subclass, violating abstraction boundaries and inheritance hierarchies.",
        "sol": "Consolidate duplicated child logic directly into parent class, or introduce abstract interface.",
        "case": "Apache ActiveMQ: Merged ManagedTransportConnector into TransportConnector."
    },
    {
        "title": "Unstable Dependency (UD)",
        "color": "#8B5CF6", "bg": "#F5F3FF",
        "prob": "A component depends on other components that are less stable than itself (violates Stable Dependencies Principle).",
        "sol": "Extract unstable functionality into helper/manager utility in separate package.",
        "case": "Apache Jackrabbit: Created SecuritySessionImplManager in util package."
    },
    {
        "title": "Hub-Like Dependency (HL)",
        "color": "#06B6D4", "bg": "#ECFEFF",
        "prob": "A component has both high incoming (FANIN) and outgoing (FANOUT) connections, acting as an architectural bottleneck.",
        "sol": "Split responsibilities into cohesive modular sub-components; delegate peripheral tasks.",
        "case": "Resolved via noModularity package boundary splits and interface segregation."
    }
]

for idx, s in enumerate(smells):
    col = idx % 2
    row = idx // 2
    x = 0.8 + col * 7.0
    y = 4.0 - row * 3.3
    w = 6.4
    h = 2.9
    
    rect = patches.FancyBboxPatch((x, y), w, h, boxstyle="round,pad=0.1,rounding_size=0.15",
                                  facecolor=s["bg"], edgecolor=s["color"], linewidth=1.5)
    ax.add_patch(rect)
    
    ax.text(x + 0.3, y + h - 0.4, s["title"], fontsize=12, fontweight="bold", color=s["color"])
    ax.text(x + 0.3, y + h - 0.8, "Problem:", fontsize=9.5, fontweight="bold", color="#334155")
    ax.text(x + 0.3, y + h - 1.25, s["prob"], fontsize=8.5, color="#475569", wrap=True)
    ax.text(x + 0.3, y + h - 1.7, "Refactoring Solution:", fontsize=9.5, fontweight="bold", color="#1E293B")
    ax.text(x + 0.3, y + h - 2.1, s["sol"], fontsize=8.5, color="#475569", wrap=True)
    ax.text(x + 0.3, y + h - 2.5, f"Case Study: {s['case']}", fontsize=8.5, style="italic", fontweight="bold", color="#0F766E")

plt.tight_layout()
plt.savefig("submission/architecture_diagrams/Architectural_Smells_Taxonomy.png", dpi=300, bbox_inches="tight")
plt.savefig("submission/architecture_diagrams/Architectural_Smells_Taxonomy.svg", bbox_inches="tight")
plt.close()

print("Created Architectural_Smells_Taxonomy.png and .svg")
