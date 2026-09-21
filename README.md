# Java JDBC & Swing Movie Ticket Management System

A Java application demonstrating **JDBC (Java Database Connectivity)** with a MySQL backend (XAMPP). It includes both a **Command Line Interface (CLI)** and a **Swing / Graphical User Interface (GUI)** to perform full management operations on a `movie_ticket_db` database (Movies and Bookings).

---

## 📁 Project Structure

```
.
├── database.sql                     # SQL setup script for MySQL
├── DBConnection.java                # JDBC Connection Manager (MySQL)
├── Movie.java                       # Movie Entity / Model Class
├── Booking.java                     # Booking Entity / Model Class
├── MovieTicketManagementSystem.java # CLI Application Entry Point & SQL Logic
├── MovieTicketGUI.java              # Swing GUI Application Entry Point
├── compile_and_run_cli.bat          # Batch script to compile & run CLI
├── compile_and_run_gui.bat          # Batch script to compile & run GUI
├── README.txt                       # Quick reference file
└── README.md                        # Project documentation
```

---

## 🛠️ Complete XAMPP & MySQL Database Setup Guide

### 1. Installing & Starting XAMPP
1. Download and install **XAMPP** from [Apache Friends](https://www.apachefriends.org/).
2. Open the **XAMPP Control Panel**.
3. Next to **Apache** and **MySQL**, click **Start**.
   - Ensure the MySQL status turns green (running on port `3306`).

---

### 2. Setting Up the Database

#### Option A: Creating Database via phpMyAdmin (Web UI)
1. Open your browser and go to `http://localhost/phpmyadmin/`.
2. Click on the **SQL** tab at the top menu bar.
3. Open the `database.sql` file from this repository, copy its contents, and paste them into the SQL query box.
4. Click **Go** (bottom right).
5. Alternatively, you can click the **Import** tab at the top, choose the `database.sql` file from your folder, and click **Import**.

#### Option B: Creating Database via MySQL Command Line / Terminal
1. Open Command Prompt or PowerShell in XAMPP's MySQL bin directory (`<path-to-xampp>\mysql\bin`):
   ```cmd
   cd <path-to-xampp>\mysql\bin
   mysql -u root
   ```
2. Execute the script directly:
   ```sql
   SOURCE <path-to-project>/database.sql;
   ```

---

### 3. Application Configuration

The database credentials in `DBConnection.java` are set to default XAMPP credentials:
- **URL**: `jdbc:mysql://localhost:3306/movie_ticket_db`
- **Username**: `root`
- **Password**: `""` *(empty string by default)*

---

## 🚀 How to Run the Application

### Prerequisites: Java Development Kit (JDK) & MySQL Connector JAR
- Ensure **JDK 17 or higher** is installed and configured in your environment (`JAVA_HOME` and `PATH`).
- Verify libraries exist at `<path-to-lib>`.

### Option 1: Command Line Interface (CLI)

#### Quick Run (Batch Script - Windows)
```powershell
.\compile_and_run_cli.bat
```

#### Manual Compilation & Execution
```powershell
# 1. Compile source files
javac -cp ".;<path-to-lib>/*" *.java

# 2. Run CLI Application
java -cp ".;<path-to-lib>/*" MovieTicketManagementSystem
```

---

### Option 2: Graphical User Interface (GUI)

#### Quick Run (Batch Script - Windows)
```powershell
.\compile_and_run_gui.bat
```

#### Manual Compilation & Execution
```powershell
# 1. Compile source files
javac -cp ".;<path-to-lib>/*" *.java

# 2. Launch GUI Application
java -cp ".;<path-to-lib>/*" MovieTicketGUI
```

---

## 🗄️ Database Inspection & Management

You can inspect and manage movie listings and customer bookings created by either the CLI or GUI using:
- **phpMyAdmin**: Start Apache & MySQL in XAMPP and open `http://localhost/phpmyadmin/`.
- **MySQL Workbench / DBeaver**: Connect to `localhost:3306` with user `root` (no password).

