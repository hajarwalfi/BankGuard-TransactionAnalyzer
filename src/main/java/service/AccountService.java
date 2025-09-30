package service;

import dao.AccountDAO;
import dao.ClientDAO;
import dao.TransactionDAO;
import entity.accounts.Account;
import entity.accounts.CheckingAccount;
import entity.accounts.SavingsAccount;
import entity.client.Client;
import entity.transactions.Transaction;
import util.Validation;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class AccountService {

    private final AccountDAO accountDAO;
    private final ClientDAO clientDAO;
    private final TransactionDAO transactionDAO;

    public AccountService() {
        this.accountDAO = new AccountDAO();
        this.clientDAO = new ClientDAO();
        this.transactionDAO = new TransactionDAO();
    }

    // ========== GENERATE ACCOUNT NUMBER ==========
    private String generateNextAccountNumber() {
        Optional<String> lastNumber = accountDAO.getLastAccountNumber();

        if (lastNumber.isEmpty()) {
            return "CPT-10000";
        }

        // Extraire le numéro de "CPT-10005" -> 10005
        String numberPart = lastNumber.get().replace("CPT-", "");
        int currentNumber = Integer.parseInt(numberPart);

        // Incrémenter
        int nextNumber = currentNumber + 1;

        return "CPT-" + nextNumber;
    }

    // ========== CREATE CHECKING ACCOUNT ==========
    public boolean createCheckingAccount(double balance, Long clientId, double overdraft) {
        if (!Validation.isValidBalance(balance)) {
            System.err.println("Erreur : Le solde doit etre positif ou nul");
            return false;
        }

        if (!Validation.isValidId(clientId)) {
            System.err.println("Erreur : ID client invalide");
            return false;
        }

        if (overdraft < 0) {
            System.err.println("Erreur : Le decouvert autorise ne peut pas etre negatif");
            return false;
        }

        Optional<Client> client = clientDAO.findById(clientId);
        if (client.isEmpty()) {
            System.err.println("Erreur : Client introuvable avec l'ID : " + clientId);
            return false;
        }

        String accountNumber = generateNextAccountNumber();

        try {
            var account = new CheckingAccount(accountNumber, balance, clientId, overdraft);
            Optional<Account> createdAccount = accountDAO.create(account);

            if (createdAccount.isPresent()) {
                System.out.println("Numero de compte genere : " + accountNumber);
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Echec de la creation du compte courant : " + e.getMessage());
            return false;
        }
    }

    // ========== CREATE SAVINGS ACCOUNT ==========
    public boolean createSavingsAccount(double balance, Long clientId, double interest) {
        if (!Validation.isValidBalance(balance)) {
            System.err.println("Erreur : Le solde doit etre positif ou nul");
            return false;
        }

        if (!Validation.isValidId(clientId)) {
            System.err.println("Erreur : ID client invalide");
            return false;
        }

        if (!Validation.isValidPercentage(interest)) {
            System.err.println("Erreur : Le taux d'interet doit etre entre 0 et 100");
            return false;
        }

        Optional<Client> client = clientDAO.findById(clientId);
        if (client.isEmpty()) {
            System.err.println("Erreur : Client introuvable avec l'ID : " + clientId);
            return false;
        }

        String accountNumber = generateNextAccountNumber();

        try {
            var account = new SavingsAccount(accountNumber, balance, clientId, interest);
            Optional<Account> createdAccount = accountDAO.create(account);

            if (createdAccount.isPresent()) {
                System.out.println("Numero de compte genere : " + accountNumber);
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Echec de la creation du compte epargne : " + e.getMessage());
            return false;
        }
    }

    // ========== UPDATE BALANCE ==========
    public boolean updateBalance(String number, double newBalance) {
        if (!Validation.isValidAccountNumber(number)) {
            System.err.println("Erreur : numero de compte invalide");
            return false;
        }

        if (!Validation.isValidBalance(newBalance)) {
            System.err.println("Erreur : Le solde doit etre positif ou nul");
            return false;
        }

        Optional<Account> accountOpt = accountDAO.findByNumber(number);
        if (accountOpt.isEmpty()) {
            System.err.println("Erreur : Compte introuvable avec l'ID : " + number);
            return false;
        }

        var account = accountOpt.get();
        account.setBalance(newBalance);
        return accountDAO.update(account);
    }

    // ========== UPDATE OVERDRAFT (Checking Account) ==========
    public boolean updateOverdraft(String number, double newOverdraft) {
        if (!Validation.isValidAccountNumber(number)) {
            System.err.println("Erreur : nombre de compte invalide");
            return false;
        }

        if (newOverdraft < 0) {
            System.err.println("Erreur : Le decouvert autorise ne peut pas etre negatif");
            return false;
        }

        Optional<Account> accountOpt = accountDAO.findByNumber(number);
        if (accountOpt.isEmpty()) {
            System.err.println("Erreur : Compte introuvable avec l'ID : " + number);
            return false;
        }

        var account = accountOpt.get();
        if (!(account instanceof CheckingAccount)) {
            System.err.println("Erreur : Ce compte n'est pas un compte courant");
            return false;
        }

        ((CheckingAccount) account).setOverdraft(newOverdraft);
        return accountDAO.update(account);
    }

    // ========== UPDATE INTEREST (Savings Account) ==========
    public boolean updateInterest(String number, double newInterest) {
        if (!Validation.isValidAccountNumber(number)) {
            System.err.println("Erreur : Nombre du compte invalide");
            return false;
        }

        if (!Validation.isValidPercentage(newInterest)) {
            System.err.println("Erreur : Le taux d'interet doit etre entre 0 et 100");
            return false;
        }

        Optional<Account> accountOpt = accountDAO.findByNumber(number);
        if (accountOpt.isEmpty()) {
            System.err.println("Erreur : Compte introuvable avec l'ID : " + number);
            return false;
        }

        var account = accountOpt.get();
        if (!(account instanceof SavingsAccount)) {
            System.err.println("Erreur : Ce compte n'est pas un compte epargne");
            return false;
        }

        ((SavingsAccount) account).setInterest(newInterest);
        return accountDAO.update(account);
    }

    // ========== DELETE ==========
    public boolean deleteAccount(String number) {
        if (!Validation.isValidAccountNumber(number)) {
            System.err.println("Erreur : Numero compte invalide");
            return false;
        }

        Optional<Account> account = accountDAO.findByNumber(number);
        if (account.isEmpty()) {
            System.err.println("Erreur : Compte introuvable avec le numero : " + number);
            return false;
        }
        long id = account.get().getId();

        List<Transaction> transactions = transactionDAO.findByAccountId(id);
        if (!transactions.isEmpty()) {
            System.err.println("Erreur : Impossible de supprimer le compte. Il possede " +
                    transactions.size() + " transaction(s)");
            return false;
        }

        return accountDAO.delete(id);
    }

    // ========== READ ==========
    public Optional<Account> findAccountById(Long id) {
        if (!Validation.isValidId(id)) {
            System.err.println("Erreur : ID invalide");
            return Optional.empty();
        }
        return accountDAO.findById(id);
    }

    public Optional<Account> findAccountByNumber(String number) {
        if (!Validation.isValidString(number)) {
            System.err.println("Erreur : Numero de compte invalide");
            return Optional.empty();
        }
        return accountDAO.findByNumber(number);
    }

    public List<Account> findAccountsByClient(Long clientId) {
        if (!Validation.isValidId(clientId)) {
            System.err.println("Erreur : ID client invalide");
            return List.of();
        }
        return accountDAO.findByClientId(clientId);
    }

    public List<Account> getAllAccounts() {
        return accountDAO.findAll();
    }

    // ========== FIND MAX/MIN BALANCE ==========
    public Optional<Account> getAccountWithMaxBalance() {
        var accounts = accountDAO.findAll();
        return accounts.stream()
                .max(Comparator.comparingDouble(Account::getBalance));
    }

    public Optional<Account> getAccountWithMinBalance() {
        var accounts = accountDAO.findAll();
        return accounts.stream()
                .min(Comparator.comparingDouble(Account::getBalance));
    }

    public Optional<Account> getAccountWithMaxBalanceByClient(Long clientId) {
        if (!Validation.isValidId(clientId)) {
            System.err.println("Erreur : ID client invalide");
            return Optional.empty();
        }

        var accounts = accountDAO.findByClientId(clientId);
        return accounts.stream()
                .max(Comparator.comparingDouble(Account::getBalance));
    }

    public Optional<Account> getAccountWithMinBalanceByClient(Long clientId) {
        if (!Validation.isValidId(clientId)) {
            System.err.println("Erreur : ID client invalide");
            return Optional.empty();
        }

        var accounts = accountDAO.findByClientId(clientId);
        return accounts.stream()
                .min(Comparator.comparingDouble(Account::getBalance));
    }

    // ========== DISPLAY ACCOUNT REPORT ==========
    public void displayAccountReport(Long accountId) {
        Optional<Account> accountOpt = findAccountById(accountId);

        if (accountOpt.isEmpty()) {
            System.err.println("Compte introuvable");
            return;
        }

        var account = accountOpt.get();
        Optional<Client> clientOpt = clientDAO.findById(account.getClientId());
        List<Transaction> transactions = transactionDAO.findByAccountId(accountId);

        System.out.println("\n========== RAPPORT COMPTE ==========");
        System.out.println("ID : " + account.getId());
        System.out.println("Numero : " + account.getNumber());
        System.out.println("Type : " + account.getType());
        System.out.println("Solde : " + String.format("%.2f", account.getBalance()) + " MAD");

        if (account instanceof CheckingAccount) {
            System.out.println("Decouvert autorise : " +
                    String.format("%.2f", ((CheckingAccount) account).getOverdraft()) + " MAD");
        } else if (account instanceof SavingsAccount) {
            System.out.println("Taux d'interet : " +
                    String.format("%.2f", ((SavingsAccount) account).getInterest()) + "%");
        }

        clientOpt.ifPresent(client ->
                System.out.println("Proprietaire : " + client.name() + " (" + client.email() + ")")
        );

        System.out.println("Nombre de transactions : " + transactions.size());
        System.out.println("====================================\n");
    }
}