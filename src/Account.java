import java.util.Scanner;

public abstract class Account {
    private String username;
    private String password;
    private String type;
    private String status;
    private double creditLimit;
    private double balance;

    public Account(String username, String password, String type, String status, double creditLimit, double balance) {
        this.username = username;
        this.password = password;
        this.type = type;
        this.status = status;
        this.creditLimit = creditLimit;
        this.balance = balance;
    }

    // Getters
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getType() { return type; }
    public String getStatus() { return status; }
    public double getCreditLimit() { return creditLimit; }
    public double getBalance() { return balance; }

    // Controlled setters — no outside class can set these directly to invalid values
    public void setStatus(String status) { this.status = status; }
    public void setCreditLimit(double creditLimit) { this.creditLimit = creditLimit; }

    public void addToBalance(double amount) {
        this.balance += amount;
    }

    public void subtractFromBalance(double amount) {
        this.balance -= amount;
        if (this.balance < 0) this.balance = 0;
    }

    public boolean canBuyOnCredit(double saleAmount) {
        return (this.balance + saleAmount) <= this.creditLimit;
    }

    // Polymorphism: every role implements its OWN menu — no switch(type) needed anywhere
    public abstract void showMenu(Scanner sc, Store store);
}
