# Folder To Text - Project Overview Generator

A lightweight and simple desktop utility built entirely in **Java** to analyze a given project folder and generate a clean, text-based overview report.

This tool is designed to help developers quickly understand the structure and composition of a codebase, making it ideal for project documentation, handovers, or getting a high-level summary at a glance. It focuses on creating a functional Graphical User Interface (GUI) using Java's native libraries and efficiently traversing the file system to gather statistics.

## Features

* **Project Statistics:** Calculates and displays the total number of files and the total size of the folder.
* **File Type Analysis:** Groups files by their extension and shows a count for each type, sorted by frequency.
* **Technology Detection:** Performs a basic scan of file names and extensions to suggest the technologies used in the project (e.g., Java, Maven, Node.js, React).
* **Directory Tree View:** Generates a classic, easy-to-read tree structure of the folder and its subdirectories.
* **Simple GUI:** Uses **Java Swing** to provide a straightforward user interface for selecting the target folder.
* **Formatted Output:** Presents all the collected information in a single, well-formatted report that can be easily copied and shared.

## How to Get Started

There are two ways to get this application running.

### Option 1: Download the Release (Easiest Way) 🚀

If you just want to use the application, you can download the latest pre-compiled version.

1.  Go to the **[Releases page (v2.1)](https://github.com/SametCirik/FolderToText/releases/tag/v2.1)**.
2.  Download the latest `FolderToText.rar` (or `.zip`) file from the assets.
3.  Extract the archive to a folder on your computer using a tool like WinRAR or 7-Zip.
4.  Open the extracted folder and double-click on **`FolderToText.exe`** to start the application.

**Note:** The application requires *the Java Runtime Environment (JRE)* to be installed on your system. If the `.exe` file doesn't open, please make sure you have Java installed.

### Option 2: Build from Source (For Developers) 💻

If you want to modify the code or build it yourself, follow these steps:

1.  **Prerequisites:** Make sure you have the Java Development Kit (JDK) installed on your system.
2.  **Clone the repository:**
    ```bash
    git clone [https://github.com/your-username/Folder-To-Text.git](https://github.com/your-username/Folder-To-Text.git)
    cd Folder-To-Text
    ```
3.  **Compile the code:**
    ```bash
    javac FolderToText.java
    ```
4.  **Run the application:**
    ```bash
    java FolderToText
    ```

After running the application using either method, a file chooser dialog will appear. Select the project folder you want to analyze, and the report will be displayed in a new window.

## Project Status and Future Roadmap

**Current Status:** The project is fully functional and stable in its `v1.0` release. All the core features listed above are implemented and working.

- [Download v1.0 Pre-Alpha](https://github.com/SametCirik/FolderToText/releases/tag/v1.0)

**Future Plans (Roadmap):**

- **Command-Line Interface (CLI):** Add support for running the analyzer from the terminal without a GUI, allowing for automation and scripting.
-  **Configuration File:** Implement a `.FTTignore` file (similar to `.gitignore`) to exclude specific files or directories (like `node_modules/` or `.git/`) from the analysis.
- **Multiple Output Formats:** Add options to export the report in different formats, such as **Markdown** or **JSON**.
- **"Copy to Clipboard" Button:** A simple UI enhancement to copy the entire report with a single click.
- **Advanced Technology Detection:** Improve the logic for detecting frameworks and libraries more accurately.

## Development Environment and Technologies

* **Language:** **Java**
* **Core Libraries:**
    * **Java Swing/AWT:** For the Graphical User Interface (GUI).
    * **Java NIO (`java.nio.file`):** For modern and efficient file system traversal.
* **IDE:** Developed in **Eclipse IDE** but can be compiled and run with any standard Java environment.

## Contributing

Contributions are welcome and highly encouraged!

This project is open to anyone who wants to help improve it. If you have an idea for a new feature, a bug fix, or an improvement to the code, please feel free to get involved.

You can contribute by:
- **Reporting a bug:** Open an issue with a detailed description.
- **Suggesting a feature:** Open an issue to discuss your idea.
- **Submitting a pull request:** Fork the repository, make your changes in a separate branch, and then submit a pull request for review.

I look forward to collaborating with you!

---

<p align="center">
   <img width="256" height="256" alt="AppLogoImage" src="https://github.com/user-attachments/assets/4409279f-c7e3-432c-9533-60b492c63206" />
</p>

<p align="center">
    <i>
        Application Icon
    </i>
</p>

---

<img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/5a5ecec8-efda-41fe-b821-ef62a0d32da1" />
<img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/4c466d79-0772-4e88-9df7-947c3b2cc2eb" />
<img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/364b86bd-5996-41db-8907-fc25872ac2e2" />
<img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/aafdbb6c-b244-4ee7-8304-24f3db1bb575" />
<img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/62adfe8d-f16c-47bb-90b8-9e5280df078e" />
<img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/2b32ad6a-d1b6-4814-b0c9-941c0cdf2e55" />

---

![GitHub all releases](https://img.shields.io/github/downloads/SametCirik/FolderToText/total)

