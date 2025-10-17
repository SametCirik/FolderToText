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

You can run this project directly from the source code.

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
5.  A file chooser dialog will appear. Select the project folder you want to analyze, and the report will be displayed in a new window.

## Project Status and Future Roadmap

**Current Status:** The project is fully functional and stable in its `v1.0` release. All the core features listed above are implemented and working.

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

