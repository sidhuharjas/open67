python3 -c '
import sys
lines = open("src/main/java/com/open67/web/WebAssets.java").read().split("\n")

for i, line in enumerate(lines):
    if "<button id=\"resetBtn\" class=\"secondary\">Reset</button>" in line:
        lines.insert(i+1, "                        <button id=\"calibrateBtn\" class=\"secondary\">Calibrate</button>")
        break

for i, line in enumerate(lines):
    if "const resetBtn = document.getElementById(\"resetBtn\");" in line:
        lines.insert(i+1, "                    const calibrateBtn = document.getElementById(\"calibrateBtn\");")
        break

for i, line in enumerate(lines):
    if "resetBtn.addEventListener(" in line:
        patch = """
                    calibrateBtn.addEventListener(\"click\", async () => {
                        await fetch(\"/api/calibrate\", { method: \"POST\" });
                    });
        """
        lines.insert(i+1, patch)
        break

open("src/main/java/com/open67/web/WebAssets.java", "w").write("\n".join(lines))
'
