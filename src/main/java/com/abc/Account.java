package com.abc;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Date;

public class Account {
    public enum AccountType {
        CHECKING,
        SAVINGS,
        MAXI_SAVINGS
    }

    private final AccountType accountType;
    private List<Transaction> transactions;

    public Account(AccountType accountType) {
        this.accountType = accountType;
        this.transactions = new ArrayList<Transaction>();
    }

    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be greater than zero");
        } else {
            transactions.add(new Transaction(amount));
        }
    }

    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be greater than zero");
        } else {
            if (calculateBalance() < amount) {
                throw new IllegalArgumentException("not enough money in account for withdrawal of that size");
            } else {
                transactions.add(new Transaction(-amount));
            }
        }
    }

    public double interestEarned() {
        double balance = calculateBalance();
        switch(accountType){
            case SAVINGS:
                if (balance <= 1000) {
                    return balance * 0.001;
                } else {
                    return 1 + (balance-1000) * 0.002;
                }

            case MAXI_SAVINGS:
                if (afterMaxiWithdrawalPeriod()) {
                    return balance * 0.05;
                } else {
                    return balance * 0.001;
                }
                
            default:
                return balance * 0.001;
        }
    }

    private boolean afterMaxiWithdrawalPeriod() {
        if (transactions.isEmpty()) {
            return true;
        }

        Date lastTransactionDate = transactions.get(transactions.size() - 1).getDate();

        Date currentDate = DateProvider.getInstance().now();
        Calendar dateCutoff = Calendar.getInstance();
        dateCutoff.setTime(currentDate);

        dateCutoff.roll(Calendar.DATE, -10);

        for (int i = transactions.size() - 1; i >= 0; i--) {
            lastTransactionDate = transactions.get(i).getDate();

            if (lastTransactionDate.after(dateCutoff.getTime())) {
                if (transactions.get(i).getAmount() < 0) {
                    return false;
                }
            } else {
                return true;
            }
        }

        return true;
    }

    public double calculateBalance() {
        double balance = 0.0;
        for (Transaction t: transactions)
            balance += t.getAmount();
        return balance;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public AccountType getAccountType() {
        return accountType;
    }
}
