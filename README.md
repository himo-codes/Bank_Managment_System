# 🏦 Bank Management System (PS No. 64)

![Java](https://img.shields.io/badge/Language-Java%20SE-orange?style=flat-square&logo=openjdk)
![DSA](https://img.shields.io/badge/Subject-Data%20Structures%20%26%20Algorithms-blue?style=flat-square)
![Status](https://img.shields.io/badge/Status-Completed-brightgreen?style=flat-square)

A console-based **Bank Management System** built in Core Java demonstrating practical implementations of fundamental Data Structures and Algorithms—**Custom Singly Linked List**, **Hash Table (`HashMap`)**, **Stack (`ArrayDeque`)**, **Queue (`LinkedList`)**, and **File Handling (`bank_accounts.txt`)**.

---

## 📋 Academic Details

* **Project Title:** Bank Management System (PS No. 64)
* **Course:** Data Structures and Algorithms (DSA) — Session 2026–27
* **Submitted By:** Himanshu Sharma (`ERP ID: RU-25-10588`)
* **Program:** B.Tech in Computer Science & Engineering (AI & ML) in association with Google
* **Institution:** School of Computer Science & Engineering, Rungta International Skills University, Bhilai, Chhattisgarh
* **Under the Guidance of:** Mr. Suryamani Verma

---

## 🚀 Key Features

* **Create Account:** Generates unique sequential account IDs (`ACC101`, `ACC102`, ...) and validates against empty names or negative opening balances.
* **Instant Account Lookup:** Uses a `HashMap` to retrieve any account in $O(1)$ average time.
* **Deposit & Withdrawal:** Enforces non-negative transaction rules and prevents overdrafts exceeding the available balance.
* **Recent Transaction History (Stack):** Records every deposit and withdrawal in LIFO (Last-In, First-Out) order so the latest transactions appear first.
* **Undo Last Transaction (Stack Pop):** Reverts the most recent transaction on an account and restores the previous balance.
* **Service Request Queue (FIFO):** Enqueues incoming deposit/withdrawal requests and processes them sequentially in arrival order.
* **Persistent Storage:** Automatically loads existing account records and transaction histories from `bank_accounts.txt` on startup and saves updated states on exit.
* **Built-in Automated Test Suite:** Menu option `11` executes all required test cases to verify system behavior.

---

## 🧠 Data Structures & Complexity Analysis

| Data Structure | Java Implementation | Purpose in System | Time Complexity |
| :--- | :--- | :--- | :--- |
| **Custom Linked List** | `AccountLinkedList` (`AccountNode`) | Dynamic master storage of all bank accounts for traversal & persistence | Insert: $O(1)$ <br> Traverse: $O(n)$ |
| **Hash Table** | `HashMap<String, AccountNode>` | Maps unique `accountId` to its Linked List node for instant access | Lookup / Update: $O(1)$ avg |
| **Stack** | `ArrayDeque<Transaction>` | Stores per-account transaction history in LIFO order & supports `Undo` | Push / Pop / Peek: $O(1)$ |
| **Queue** | `LinkedList<ServiceRequest>` | Manages deferred customer service requests in FIFO order | Enqueue / Dequeue: $O(1)$ |
| **File Handling** | `BufferedReader` & `BufferedWriter` | Reads and writes pipe-delimited records to `bank_accounts.txt` | Load / Save: $O(n)$ |

---

## 📂 Project Structure

```text
BankManagementSystem/
├── BankManagementSystem.java   # Complete Java source code (DSA + CLI + Test Runner)
├── bank_accounts.txt           # Auto-generated persistent storage file

⚙️ How to Compile & Run
PrerequisitesJava Development Kit (JDK 17 or 21+) installed and added to system PATH.Terminal / PowerShell / Command Prompt or an IDE (VS Code, IntelliJ IDEA, Eclipse).Steps
Clone the repository:Bashgit clone [https://github.com/himo-codes/BankManagementSystem.git](https://github.com/himo-codes/BankManagementSystem.git)
cd BankManagementSystem
Compile the Java source file:Bashjavac BankManagementSystem.java
Run the application:Bashjava BankManagementSystem
🧪 Verified Test CasesSelect Option 11 in the CLI menu to run all four test cases automatically:Test CaseScenarioInputExpected & Actual OutputStatusTest Case 1Normal InputCreate account for Aman with opening balance 1000Account created: ACC101 | Aman | Balance 1000✅ PassTest Case 2Boundary / MinimumCheck balance of ACC101Balance: 1000✅ PassTest Case 3Invalid InputSearch for non-existent account ACC999Account ACC999 not found.✅ PassTest Case 4Valid Transaction FlowACC101 deposits 500, withdraws 200, views recent transactionsBalance: 1300Recent transactions: Withdraw 200, Deposit 500✅ Pass💾 Data File Format (bank_accounts.txt)Records are persisted in a human-readable pipe-delimited format (AccountID|HolderName|Balance|TransactionHistory):PlaintextACC101|Aman|1300|Deposit:500,Withdraw:200
ACC102|Riya|2500|Deposit:1000
📜 LicenseThis project is developed for academic purposes under the Data Structures and Algorithms curriculum at Rungta International Skills University.
├── Himanshu_Sharma_10588.pdf   # Project Interim Repor                                                                                                                                                                                    t
└── README.md                   # Project documentation
