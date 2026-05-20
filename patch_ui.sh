python3 -c '
import sys
content = open("src/main/java/com/open67/ui/MainWindow.java").read()

# Add button declaration
lines = content.split("\n")
for i, line in enumerate(lines):
    if "private final JButton startButton = new JButton(\"Start\");" in line:
        lines.insert(i+1, "    private final JButton calibrateButton = new JButton(\"Calibrate\");")
        break
content = "\n".join(lines)

# Add button to UI
lines = content.split("\n")
for i, line in enumerate(lines):
    if "sidebar.add(startButton);" in line:
        lines.insert(i+1, "        sidebar.add(calibrateButton);")
        break
content = "\n".join(lines)

# Wire the button
lines = content.split("\n")
for i, line in enumerate(lines):
    if "startButton.addActionListener(event -> {" in line:
        lines.insert(i, "        calibrateButton.addActionListener(event -> { detector.startCalibration(); });")
        break
content = "\n".join(lines)

open("src/main/java/com/open67/ui/MainWindow.java", "w").write(content)
'
