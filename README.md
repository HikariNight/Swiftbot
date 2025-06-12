
# <p align="center"> Swiftbot Navigation </p>


Welcome to **Swiftbot Navigation** – a Java-powered interface for guiding your SwiftBot with QR commands and manual inputs! This bot doesn't just move... it *logs*, it *retraces*, it *scans*, and it looks good doing it >:)

---

## Features

* 📷 **QR Code Scanning**: Control movement using QR-code instructions.
* 🕹️ **Live Command-Line Input**: Use the `F`, `B`, `R`, `L` inputs and more to move the bot on the fly.
* 📜 **Command Logging**: Every move is remembered.
* ⏪ **Traceback Functionality**: Rewinds the bot’s steps like a little robot time machine.
* 📂 **Log File Output**: Saves session history to a file.
* ✨ **Light-Up Error Feedback**: Flashing LEDs warn you of any errors.

---

## How It Works

This program allows interaction with a SwiftBot through Java code, using either:

* **Direct input** through the console, or
* **Scanning QR codes** with encoded commands in the form:

  ```
  <Command> <Duration (in seconds)> <Speed>
  ```

### 📝 Available Commands:

| Command | Action                      | Example  |
| ------- | --------------------------- | -------- |
| `F`     | Move forward                | `F 3 50` |
| `B`     | Move backward               | `B 2 40` |
| `R`     | Turn right                  | `R 1 60` |
| `L`     | Turn left                   | `L 1 60` |
| `T`     | Traceback previous steps    | `T 2`    |
| `S`     | Scan a QR code for commands | N/A      |
| `W`     | Write log to file           | N/A      |
| `X`     | Exit the program            | N/A      |

---

## 📦 Getting Started

### Requirements

* Java 8+
* A SwiftBot (with I2C enabled)
* Raspberry Pi (with SwiftBot API installed)
* QR codes (optional but way more fun! (when it works :D))

### Setup

1. **Enable I2C** (once only):

   ```bash
   sudo raspi-config nonint do_i2c 0
   ```

2. **Compile the program**:

   ```bash
   javac Navigation.java
   ```

3. **Run it**:

   ```bash
   java Navigation
   ```

---

## 🧪 Sample Session

```
> Enter your command:
F
> Enter your speed:
40
> Enter your duration:
3
→ Moving Forwards at speed 40 for 3 seconds...
```

```
> Enter your command:
T
> Enter number of steps to retrace:
2
→ Rewinding the last 2 moves...
```

---

## Output File

At any point, run the `W` command to write your command history to a `CommandLogHistory.txt` file, time-stamped and ready to share.

---

## Clean Code

✔️ Helpful command-line messages
✔️ LED feedback on errors
✔️ Robust error handling (no silent fails!)

---

## Developer Notes

* You can tweak command speed and duration limits inside the code (currently 0 < speed ≤ 100, duration ≤ 6).
* QR codes must include all three elements: command, duration, speed (e.g., `F 2 40`).
* The traceback mechanism skips non-movement logs (`S`, `T`, `W`) for clean replays.

---

## QR Tip!

You can generate QR codes using websites like [qr-code-generator.com](https://www.qr-code-generator.com/) with inputs like `F 3 60`.

