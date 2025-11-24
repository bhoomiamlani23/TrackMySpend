# 💰 TrackMySpend

TrackMySpend is a comprehensive, user-specific personal finance application built using **Java Swing**. It allows individual users to securely track expenses, manage dual balances (Online/Offline), set monthly goals, and analyze their spending trends through interactive graphical charts.

***

## ✨ Key Features

This project incorporates robust features for personal financial management:

### 🛡️ User Authentication & Profile
* **Secure Access:** Separate **Login**, **Sign Up**, and **Forgot Password** workflows.
* **Profile Management:** Users can update their **Name, Email, Age, and Mobile number** on the dedicated Profile tab.
* **Security:** Users can **Change Password** once logged in.

### 💵 Financial Tracking
* **Dual Balance System:** Tracks finances across two distinct sources: **Online Balance** and **Offline Cash (₹)**.
* **Expense Deduction Logic:** When a user logs an expense, the application asks for the **Source (Online/Offline)** and automatically deducts the amount from the corresponding balance.
* **Monthly Goals:** Users can set and update a **Monthly Spending Goal (₹)**.
* **Persistent Status Bar:** A status bar visible on every screen displays the **Total Balance (₹)**, **Online/Offline Breakdown**, and **Monthly Goal**.

### 📝 Data Entry & Management
* **Date Constraint:** Prevents users from entering expenses for **past dates**.
* **Categorization:** Expenses are categorized for clear tracking and analysis (Food, Travel, Entertainment, etc.).
* **Filtering:** Expense data table supports filtering by **Category**.

### 📈 Analysis and Reporting
The application provides detailed visualizations for current month spending:
* **Pie Chart:** Shows the percentage distribution of spending across all categories.
* **Bar Chart:** Displays the total amount spent per category.
* **Line Chart:** Visualizes the weekly spending trend over the current month.

***

## 📁 Project Structure

The entire application is organized into the following files within the `TrackMySpend` package:
```
TrackMySpend/
├── TrackMySpendJava.java   
├── AuthFrame.java         
├── AuthPanel.java         
├── User.java             
├── Expense.java
├── assets/
│         ├── img1.jpg
│         ├── ...
│         ├── img7.jpg
└── *.class files

             
```

## 🛠️ Developer Information

* **Developer:** Bhoomi Amlani
* **Contact:** bhoomiamlani363@gmail.com

***

## 🚀 Installation and Run Instructions

This application requires **Java Development Kit (JDK) 8 or newer**.

### 1. Compilation

Navigate to the directory *containing* the `TrackMySpend` folder and compile all Java source files:

javac TrackMySpend/*.java

### 2. Execution

Run the main application class:

java TrackMySpend.TrackMySpendJava
