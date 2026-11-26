import java.io.*;
import java.util.*;

/**
 * PS No. 64: Bank Management System
 * Concepts: Custom Linked List, Hashing (HashMap), Queue, Stack, File Handling
 */
public class BankManagementSystem {

    private static final String DATA_FILE = "bank_accounts.txt";

    // ==================== 1. ACCOUNT MODEL & STACK ====================
    static class Account {
        String accountId;
        String holderName;
        double balance;
        // Stack (LIFO) for recent transaction history
        Deque<Transaction> transactionHistory;

        public Account(String accountId, String holderName, double balance) {
            this.accountId = accountId;
            this.holderName = holderName;
            this.balance = balance;
            this.transactionHistory = new ArrayDeque<>();
        }

        public void pushTransaction(String type, double amount) {
            transactionHistory.push(new Transaction(type, amount));
        }
    }

    static class Transaction {
        String type;   // "Deposit" or "Withdraw"
        double amount;

        public Transaction(String type, double amount) {
            this.type = type;
            this.amount = amount;
        }

        @Override
        public String toString() {
            return type + " " + formatAmount(amount);
        }
    }

    // ==================== 2. CUSTOM LINKED LIST ====================
    static class AccountNode {
        Account data;
        AccountNode next;

        public AccountNode(Account data) {
            this.data = data;
            this.next = null;
        }
    }

    static class AccountLinkedList {
        private AccountNode head;
        private AccountNode tail;
        private int size;

        public AccountNode add(Account account) {
            AccountNode newNode = new AccountNode(account);
            if (head == null) {
                head = tail = newNode;
            } else {
                tail.next = newNode;
                tail = newNode;
            }
            size++;
            return newNode;
        }

        public AccountNode getHead() {
            return head;
        }

        public int getSize() {
            return size;
        }

        public void clear() {
            head = tail = null;
            size = 0;
        }
    }

    // ==================== 3. SERVICE REQUEST QUEUE ====================
    static class ServiceRequest {
        String accountId;
        String operation; // "DEPOSIT" or "WITHDRAW"
        double amount;

        public ServiceRequest(String accountId, String operation, double amount) {
            this.accountId = accountId;
            this.operation = operation;
            this.amount = amount;
        }

        @Override
        public String toString() {
            return "[" + operation + " | " + accountId + " | Amount: " + formatAmount(amount) + "]";
        }
    }

    // ==================== 4. BANK ENGINE ====================
    static class BankSystem {
        private final AccountLinkedList accountList = new AccountLinkedList();
        private final Map<String, AccountNode> accountMap = new HashMap<>();
        private final Queue<ServiceRequest> serviceQueue = new LinkedList<>();
        private int nextAccountNumber = 101;

        // Helper to format whole numbers without ".0" (e.g., 1000 instead of 1000.0)
        public static String formatAmount(double amount) {
            if (amount == (long) amount) {
                return String.format("%d", (long) amount);
            }
            return String.format("%.2f", amount);
        }

        // Load accounts from bank_accounts.txt on startup
        public void loadFromFile() {
            File file = new File(DATA_FILE);
            if (!file.exists()) {
                return;
            }
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                int maxIdNum = 100;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty()) continue;

                    // Format: ACC101|Aman|1000|Deposit:500,Withdraw:200
                    String[] parts = line.split("\\|");
                    if (parts.length >= 3) {
                        String id = parts[0].trim();
                        String name = parts[1].trim();
                        double bal = Double.parseDouble(parts[2].trim());

                        if (bal < 0) continue; // Enforce non-negative invariant

                        Account acc = new Account(id, name, bal);

                        // Restore transaction stack if present (stored oldest-to-newest in file)
                        if (parts.length == 4 && !parts[3].trim().isEmpty()) {
                            String[] txns = parts[3].trim().split(",");
                            for (String txn : txns) {
                                String[] kv = txn.split(":");
                                if (kv.length == 2) {
                                    acc.pushTransaction(kv[0].trim(), Double.parseDouble(kv[1].trim()));
                                }
                            }
                        }

                        AccountNode node = accountList.add(acc);
                        accountMap.put(id, node);

                        // Track highest account number for auto-generation
                        if (id.startsWith("ACC")) {
                            try {
                                int num = Integer.parseInt(id.substring(3));
                                if (num > maxIdNum) maxIdNum = num;
                            } catch (NumberFormatException ignored) {}
                        }
                    }
                }
                nextAccountNumber = maxIdNum + 1;
            } catch (IOException | NumberFormatException e) {
                System.out.println("Warning: Could not load previous data (" + e.getMessage() + ")");
            }
        }

        // Save all accounts from Linked List to bank_accounts.txt on exit
        public void saveToFile() {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE))) {
                AccountNode current = accountList.getHead();
                while (current != null) {
                    Account acc = current.data;
                    StringBuilder sb = new StringBuilder();
                    sb.append(acc.accountId).append("|")
                      .append(acc.holderName).append("|")
                      .append(formatAmount(acc.balance)).append("|");

                    // Serialize stack from bottom (oldest) to top (newest) so push restores order
                    List<Transaction> ordered = new ArrayList<>(acc.transactionHistory);
                    Collections.reverse(ordered);
                    for (int i = 0; i < ordered.size(); i++) {
                        Transaction t = ordered.get(i);
                        sb.append(t.type).append(":").append(formatAmount(t.amount));
                        if (i < ordered.size() - 1) sb.append(",");
                    }

                    writer.write(sb.toString());
                    writer.newLine();
                    current = current.next;
                }
            } catch (IOException e) {
                System.out.println("Error saving accounts to file: " + e.getMessage());
            }
        }

        // Feature 1: Create Account
        public String createAccount(String holderName, double openingBalance) {
            if (holderName == null || holderName.trim().isEmpty()) {
                return "Error: Account holder name cannot be empty.";
            }
            if (openingBalance < 0) {
                return "Error: Opening balance cannot be negative.";
            }

            String accountId = "ACC" + nextAccountNumber++;
            while (accountMap.containsKey(accountId)) {
                accountId = "ACC" + nextAccountNumber++;
            }

            Account newAccount = new Account(accountId, holderName.trim(), openingBalance);
            AccountNode node = accountList.add(newAccount);
            accountMap.put(accountId, node);

            return "Account created: " + accountId + " | " + newAccount.holderName
                    + " | Balance " + formatAmount(newAccount.balance);
        }

        // Feature 2: Search Account (O(1) HashMap lookup)
        public String searchAccount(String accountId) {
            AccountNode node = accountMap.get(accountId.toUpperCase());
            if (node == null) {
                return "Account " + accountId + " not found.";
            }
            Account acc = node.data;
            return "Account Found: " + acc.accountId + " | " + acc.holderName
                    + " | Balance: " + formatAmount(acc.balance);
        }

        // Feature 3: Deposit
        public String deposit(String accountId, double amount) {
            AccountNode node = accountMap.get(accountId.toUpperCase());
            if (node == null) {
                return "Account " + accountId + " not found.";
            }
            if (amount <= 0) {
                return "Error: Deposit amount must be greater than zero.";
            }
            Account acc = node.data;
            acc.balance += amount;
            acc.pushTransaction("Deposit", amount);
            return "Deposited " + formatAmount(amount) + " into " + acc.accountId
                    + ". New Balance: " + formatAmount(acc.balance);
        }

        // Feature 4: Withdraw
        public String withdraw(String accountId, double amount) {
            AccountNode node = accountMap.get(accountId.toUpperCase());
            if (node == null) {
                return "Account " + accountId + " not found.";
            }
            if (amount <= 0) {
                return "Error: Withdrawal amount must be greater than zero.";
            }
            Account acc = node.data;
            if (amount > acc.balance) {
                return "Error: Insufficient balance. Available Balance: " + formatAmount(acc.balance);
            }
            acc.balance -= amount;
            acc.pushTransaction("Withdraw", amount);
            return "Withdrew " + formatAmount(amount) + " from " + acc.accountId
                    + ". New Balance: " + formatAmount(acc.balance);
        }

        // Feature 5: Check Balance
        public String checkBalance(String accountId) {
            AccountNode node = accountMap.get(accountId.toUpperCase());
            if (node == null) {
                return "Account " + accountId + " not found.";
            }
            return "Balance: " + formatAmount(node.data.balance);
        }

        // Feature 6: View Recent Transactions (Stack LIFO order)
        public String getRecentTransactions(String accountId) {
            AccountNode node = accountMap.get(accountId.toUpperCase());
            if (node == null) {
                return "Account " + accountId + " not found.";
            }
            Account acc = node.data;
            if (acc.transactionHistory.isEmpty()) {
                return "Recent transactions: None";
            }
            List<String> list = new ArrayList<>();
            for (Transaction t : acc.transactionHistory) {
                list.add(t.toString());
            }
            return "Recent transactions: " + String.join(", ", list);
        }

        // Feature 7: Undo Last Transaction using Stack
        public String undoLastTransaction(String accountId) {
            AccountNode node = accountMap.get(accountId.toUpperCase());
            if (node == null) {
                return "Account " + accountId + " not found.";
            }
            Account acc = node.data;
            if (acc.transactionHistory.isEmpty()) {
                return "Nothing to undo for " + acc.accountId + ".";
            }
            Transaction last = acc.transactionHistory.peek();
            if (last.type.equals("Deposit")) {
                if (acc.balance < last.amount) {
                    return "Cannot undo Deposit of " + formatAmount(last.amount) + ": insufficient balance.";
                }
                acc.balance -= last.amount;
            } else if (last.type.equals("Withdraw")) {
                acc.balance += last.amount;
            }
            acc.transactionHistory.pop();
            return "Undid [" + last + "] for " + acc.accountId
                    + ". Updated Balance: " + formatAmount(acc.balance);
        }

        // Feature 8: Queue Pending Service Requests
        public String enqueueRequest(String accountId, String operation, double amount) {
            if (!accountMap.containsKey(accountId.toUpperCase())) {
                return "Account " + accountId + " not found.";
            }
            if (amount <= 0) {
                return "Error: Request amount must be positive.";
            }
            ServiceRequest req = new ServiceRequest(accountId.toUpperCase(), operation.toUpperCase(), amount);
            serviceQueue.offer(req);
            return "Queued service request: " + req + " (Queue size: " + serviceQueue.size() + ")";
        }

        public String processNextRequest() {
            if (serviceQueue.isEmpty()) {
                return "No pending service requests in the queue.";
            }
            ServiceRequest req = serviceQueue.poll();
            if (req.operation.equals("DEPOSIT")) {
                return "Processed Queue Item -> " + deposit(req.accountId, req.amount);
            } else if (req.operation.equals("WITHDRAW")) {
                return "Processed Queue Item -> " + withdraw(req.accountId, req.amount);
            }
            return "Unknown operation in queue: " + req.operation;
        }

        public void displayAllAccounts() {
            if (accountList.getHead() == null) {
                System.out.println("No accounts found in the bank.");
                return;
            }
            System.out.println("\n--- All Accounts (Linked List Traversal) ---");
            AccountNode curr = accountList.getHead();
            while (curr != null) {
                Account a = curr.data;
                System.out.println(a.accountId + " | " + a.holderName + " | Balance: " + formatAmount(a.balance));
                curr = curr.next;
            }
        }

        // Reset memory for isolated test suite execution
        public void resetForTesting() {
            accountList.clear();
            accountMap.clear();
            serviceQueue.clear();
            nextAccountNumber = 101;
        }
    }

    public static String formatAmount(double amount) {
        return BankSystem.formatAmount(amount);
    }

    // ==================== 5. AUTOMATED TEST CASE VERIFIER ====================
    public static void runTestCases() {
        System.out.println("==========================================================");
        System.out.println(" RUNNING REQUIRED TEST CASES (PS No. 64)");
        System.out.println("==========================================================");

        BankSystem testBank = new BankSystem();
        testBank.resetForTesting();

        // Test Case 1: Normal Input
        System.out.println("\n[Test Case 1: Normal Input]");
        System.out.println("Input   : Create account for Aman with opening balance 1000");
        System.out.println("Output  : " + testBank.createAccount("Aman", 1000));

        // Test Case 2: Boundary / Minimum Input
        System.out.println("\n[Test Case 2: Boundary / Minimum Input]");
        System.out.println("Input   : ACC101 check balance");
        System.out.println("Output  : " + testBank.checkBalance("ACC101"));

        // Test Case 3: Invalid Input
        System.out.println("\n[Test Case 3: Invalid Input]");
        System.out.println("Input   : Search for account ACC999");
        System.out.println("Output  : " + testBank.searchAccount("ACC999"));

        // Test Case 4: Different Valid Scenario
        System.out.println("\n[Test Case 4: Different Valid Scenario]");
        System.out.println("Input   : ACC101 deposits 500 and then withdraws 200; view recent transactions");
        testBank.deposit("ACC101", 500);
        testBank.withdraw("ACC101", 200);
        System.out.println("Output  : " + testBank.checkBalance("ACC101"));
        System.out.println("          " + testBank.getRecentTransactions("ACC101"));

        // Bonus Validation Checks (Negative & Overdraft)
        System.out.println("\n[Extra Validation: Negative Amount & Overdraft]");
        System.out.println("Deposit -50   : " + testBank.deposit("ACC101", -50));
        System.out.println("Withdraw 5000 : " + testBank.withdraw("ACC101", 5000));
        System.out.println("==========================================================\n");
    }

    // ==================== 6. INTERACTIVE CLI MENU ====================
    public static void main(String[] args) {
        BankSystem bank = new BankSystem();
        bank.loadFromFile();

        Scanner sc = new Scanner(System.in);
        boolean running = true;

        System.out.println("=== Bank Management System (PS No. 64) ===");
        while (running) {
            System.out.println("\n1. Create Account");
            System.out.println("2. Search Account");
            System.out.println("3. Deposit Money");
            System.out.println("4. Withdraw Money");
            System.out.println("5. Check Balance");
            System.out.println("6. View Recent Transactions (Stack)");
            System.out.println("7. Undo Last Transaction (Stack Pop)");
            System.out.println("8. Enqueue Service Request (Queue)");
            System.out.println("9. Process Next Service Request (Queue Dequeue)");
            System.out.println("10. Display All Accounts (Linked List)");
            System.out.println("11. Run Built-in Test Cases (1-4)");
            System.out.println("0. Save & Exit");
            System.out.print("Enter choice: ");

            String choice = sc.nextLine().trim();
            try {
                switch (choice) {
                    case "1":
                        System.out.print("Enter Account Holder Name: ");
                        String name = sc.nextLine();
                        System.out.print("Enter Opening Balance: ");
                        double openBal = Double.parseDouble(sc.nextLine().trim());
                        System.out.println(bank.createAccount(name, openBal));
                        break;

                    case "2":
                        System.out.print("Enter Account ID (e.g., ACC101): ");
                        System.out.println(bank.searchAccount(sc.nextLine().trim()));
                        break;

                    case "3":
                        System.out.print("Enter Account ID: ");
                        String depId = sc.nextLine().trim();
                        System.out.print("Enter Deposit Amount: ");
                        double depAmt = Double.parseDouble(sc.nextLine().trim());
                        System.out.println(bank.deposit(depId, depAmt));
                        break;

                    case "4":
                        System.out.print("Enter Account ID: ");
                        String witId = sc.nextLine().trim();
                        System.out.print("Enter Withdrawal Amount: ");
                        double witAmt = Double.parseDouble(sc.nextLine().trim());
                        System.out.println(bank.withdraw(witId, witAmt));
                        break;

                    case "5":
                        System.out.print("Enter Account ID: ");
                        System.out.println(bank.checkBalance(sc.nextLine().trim()));
                        break;

                    case "6":
                        System.out.print("Enter Account ID: ");
                        System.out.println(bank.getRecentTransactions(sc.nextLine().trim()));
                        break;

                    case "7":
                        System.out.print("Enter Account ID: ");
                        System.out.println(bank.undoLastTransaction(sc.nextLine().trim()));
                        break;

                    case "8":
                        System.out.print("Enter Account ID: ");
                        String qId = sc.nextLine().trim();
                        System.out.print("Enter Operation (DEPOSIT / WITHDRAW): ");
                        String qOp = sc.nextLine().trim();
                        System.out.print("Enter Amount: ");
                        double qAmt = Double.parseDouble(sc.nextLine().trim());
                        System.out.println(bank.enqueueRequest(qId, qOp, qAmt));
                        break;

                    case "9":
                        System.out.println(bank.processNextRequest());
                        break;

                    case "10":
                        bank.displayAllAccounts();
                        break;

                    case "11":
                        runTestCases();
                        break;

                    case "0":
                        bank.saveToFile();
                        System.out.println("Data saved to " + DATA_FILE + ". Exiting...");
                        running = false;
                        break;

                    default:
                        System.out.println("Invalid option. Please choose between 0 and 11.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid numeric input. Please enter a valid number.");
            }
        }
        sc.close();
    }
}