# Medicine Management System

A small Java desktop application for keeping track of medicines in a pharmacy. It uses a Swing window for the screens and a MySQL database to store medicine records.

## Project structure

```text
Medicine Management/
|-- src/
|   |-- MedicineGUI.java       # Starts the desktop app and builds its screens
|   |-- Medicine.java          # Holds one medicine's details
|   |-- MedicineDAO.java       # Reads and changes medicine records in MySQL
|   |-- DBConnection.java      # Opens a connection to the MySQL database
|   |-- *.class                # Compiled Java output (generated when building)
|-- database/
|   |-- database.sql           # Creates the medicine table and contains SQL checks
|-- lib/
|   |-- mysql-connector-j-26.7.0.jar  # MySQL driver used by Java
|-- .vscode/
|   |-- settings.json          # Editor settings for VS Code
|-- .gitignore                 # Files Git should ignore
|-- README.md                  # This guide
```

## What each Java file does

- **`MedicineGUI.java`** is the starting point (`main`). It creates the window and screens for adding, managing, searching, viewing, and checking stock and expiry dates. Button actions call `MedicineDAO` to work with saved records.
- **`Medicine.java`** is the simple data object for a medicine: ID, name, category, price, quantity, expiry date, and whether a prescription is required.
- **`MedicineDAO.java`** contains the database operations: add, list, find by ID or name, update, delete, and list medicines ordered by expiry date. It also converts expiry dates between the form's `dd-MM-yyyy` format and MySQL's date format.
- **`DBConnection.java`** contains the MySQL connection URL and login details. Update these settings to match your local MySQL setup. The current file has a password written directly in source code; avoid sharing or committing real database credentials.

## How the app works

1. Start `MedicineGUI`; it opens the main window.
2. Choose an action from the navigation on the left and enter or select medicine details.
3. The screen passes the requested operation to `MedicineDAO`.
4. `MedicineDAO` opens a connection through `DBConnection`, sends an SQL query to MySQL, and turns returned rows into `Medicine` objects.
5. The screen displays the result. Changes are stored in the database, so they remain after the app closes.

The stock and expiry screen lists records ordered by expiry date. Its status rules in the current code are: expired date means **Expired**, otherwise quantity of 10 or less means **Low Stock**, and the rest means **Available**.


## Notes

- Enter expiry dates as `dd-MM-yyyy`, for example `25-12-2027`.
- The stock and expiry screen currently parses the displayed date in a different format from the one `MedicineDAO` returns. As a result, that screen may fail when it tries to show records. The date formats need to be made consistent in the code.
