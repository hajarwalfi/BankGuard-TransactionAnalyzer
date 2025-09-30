package service;

import dao.AccountDAO;
import dao.ClientDAO;
import dao.TransactionDAO;
import entity.accounts.Account;
import entity.client.Client;
import entity.transactions.Transaction;
import enums.TransactionType;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class ReportService {

    private final ClientDAO clientDAO;
    private final AccountDAO accountDAO;
    private final TransactionDAO transactionDAO;
    private final TransactionService transactionService;

    public ReportService() {
        this.clientDAO = new ClientDAO();
        this.accountDAO = new AccountDAO();
        this.transactionDAO = new TransactionDAO();
        this.transactionService = new TransactionService();
    }

    // ========== TOP 5 CLIENTS BY BALANCE ==========
    public List<Map.Entry<Client, Double>> getTop5ClientsByBalance() {
        var allClients = clientDAO.findAll();

        return allClients.stream()
                .map(client -> {
                    double totalBalance = accountDAO.findByClientId(client.id()).stream()
                            .mapToDouble(Account::getBalance)
                            .sum();
                    return Map.entry(client, totalBalance);
                })
                .sorted(Map.Entry.<Client, Double>comparingByValue().reversed())
                .limit(5)
                .toList();
    }

    public void displayTop5ClientsByBalance() {
        var top5 = getTop5ClientsByBalance();

        System.out.println("\n========== TOP 5 CLIENTS PAR SOLDE ==========");

        if (top5.isEmpty()) {
            System.out.println("Aucun client trouve");
        } else {
            int rank = 1;
            for (var entry : top5) {
                var client = entry.getKey();
                double balance = entry.getValue();
                int accountCount = accountDAO.findByClientId(client.id()).size();

                System.out.println(rank + ". " + client.name() + " (" + client.email() + ")");
                System.out.println("   Solde total : " + String.format("%.2f", balance) + " MAD");
                System.out.println("   Nombre de comptes : " + accountCount);
                rank++;
            }
        }

        System.out.println("=============================================\n");
    }

    // ========== MONTHLY REPORT ==========
    public Map<TransactionType, Long> getTransactionCountByType(YearMonth month) {
        var allTransactions = transactionDAO.findAll();

        return allTransactions.stream()
                .filter(t -> YearMonth.from(t.date()).equals(month))
                .collect(Collectors.groupingBy(
                        Transaction::type,
                        Collectors.counting()
                ));
    }

    public Map<TransactionType, Double> getTransactionVolumeByType(YearMonth month) {
        var allTransactions = transactionDAO.findAll();

        return allTransactions.stream()
                .filter(t -> YearMonth.from(t.date()).equals(month))
                .collect(Collectors.groupingBy(
                        Transaction::type,
                        Collectors.summingDouble(Transaction::amount)
                ));
    }

    public void displayMonthlyReport(YearMonth month) {
        var countByType = getTransactionCountByType(month);
        var volumeByType = getTransactionVolumeByType(month);

        System.out.println("\n========== RAPPORT MENSUEL : " + month + " ==========");

        if (countByType.isEmpty()) {
            System.out.println("Aucune transaction pour ce mois");
        } else {
            long totalCount = countByType.values().stream().mapToLong(Long::longValue).sum();
            double totalVolume = volumeByType.values().stream().mapToDouble(Double::doubleValue).sum();

            System.out.println("Nombre total de transactions : " + totalCount);
            System.out.println("Volume total : " + String.format("%.2f", totalVolume) + " MAD");
            System.out.println("\n--- Details par type ---");

            for (TransactionType type : TransactionType.values()) {
                long count = countByType.getOrDefault(type, 0L);
                double volume = volumeByType.getOrDefault(type, 0.0);

                if (count > 0) {
                    System.out.println(type + " :");
                    System.out.println("  Nombre : " + count);
                    System.out.println("  Volume : " + String.format("%.2f", volume) + " MAD");
                    System.out.println("  Moyenne : " + String.format("%.2f", volume / count) + " MAD");
                }
            }
        }

        System.out.println("=================================================\n");
    }

    // ========== DETECT SUSPICIOUS TRANSACTIONS ==========
    public List<Transaction> detectSuspiciousTransactions(double amountThreshold, String usualCountry, long maxMinutesBetween) {
        var allTransactions = transactionDAO.findAll();

        var highAmount = transactionService.detectHighAmountTransactions(allTransactions, amountThreshold);
        var unusualLocation = transactionService.detectUnusualLocation(allTransactions, usualCountry);
        var highFrequency = transactionService.detectHighFrequency(allTransactions, maxMinutesBetween);

        Set<Transaction> suspicious = new HashSet<>();
        suspicious.addAll(highAmount);
        suspicious.addAll(unusualLocation);
        suspicious.addAll(highFrequency);

        return suspicious.stream()
                .sorted(Comparator.comparing(Transaction::date).reversed())
                .toList();
    }

    public void displaySuspiciousTransactions(double amountThreshold, String usualCountry, long maxMinutesBetween) {
        var suspicious = detectSuspiciousTransactions(amountThreshold, usualCountry, maxMinutesBetween);

        System.out.println("\n========== TRANSACTIONS SUSPECTES ==========");
        System.out.println("Criteres de detection :");
        System.out.println("  - Montant superieur a : " + amountThreshold + " MAD");
        System.out.println("  - Pays habituel : " + (usualCountry != null ? usualCountry : "Non specifie"));
        System.out.println("  - Frequence maximale : " + maxMinutesBetween + " minute(s)");
        System.out.println();

        if (suspicious.isEmpty()) {
            System.out.println("Aucune transaction suspecte detectee");
        } else {
            System.out.println("Nombre de transactions suspectes : " + suspicious.size());
            System.out.println("\nDetails :");

            for (var transaction : suspicious) {
                System.out.println("  - ID : " + transaction.id());
                System.out.println("    Date : " + transaction.date());
                System.out.println("    Montant : " + String.format("%.2f", transaction.amount()) + " MAD");
                System.out.println("    Type : " + transaction.type());
                System.out.println("    Lieu : " + transaction.location());
                System.out.println("    Compte ID : " + transaction.accountId());
                System.out.println();
            }
        }

        System.out.println("============================================\n");
    }

    // ========== IDENTIFY INACTIVE ACCOUNTS ==========
    public List<Account> findInactiveAccounts(int daysInactive) {
        var allAccounts = accountDAO.findAll();
        var now = LocalDateTime.now();

        return allAccounts.stream()
                .filter(account -> {
                    var transactions = transactionDAO.findByAccountId(account.getId());

                    if (transactions.isEmpty()) {
                        return true;
                    }

                    var lastTransaction = transactions.stream()
                            .max(Comparator.comparing(Transaction::date))
                            .orElse(null);

                    if (lastTransaction == null) {
                        return true;
                    }

                    long daysSinceLastTransaction = ChronoUnit.DAYS.between(lastTransaction.date(), now);
                    return daysSinceLastTransaction > daysInactive;
                })
                .toList();
    }

    public void displayInactiveAccounts(int daysInactive) {
        var inactiveAccounts = findInactiveAccounts(daysInactive);

        System.out.println("\n========== COMPTES INACTIFS ==========");
        System.out.println("Critere : Aucune transaction depuis " + daysInactive + " jours");
        System.out.println();

        if (inactiveAccounts.isEmpty()) {
            System.out.println("Aucun compte inactif trouve");
        } else {
            System.out.println("Nombre de comptes inactifs : " + inactiveAccounts.size());
            System.out.println("\nDetails :");

            for (var account : inactiveAccounts) {
                var clientOpt = clientDAO.findById(account.getClientId());
                var transactions = transactionDAO.findByAccountId(account.getId());

                System.out.println("  - Numero : " + account.getNumber());
                System.out.println("    Type : " + account.getType());
                System.out.println("    Solde : " + String.format("%.2f", account.getBalance()) + " MAD");

                clientOpt.ifPresent(client ->
                        System.out.println("    Proprietaire : " + client.name())
                );

                if (transactions.isEmpty()) {
                    System.out.println("    Statut : Aucune transaction enregistree");
                } else {
                    var lastTransaction = transactions.stream()
                            .max(Comparator.comparing(Transaction::date))
                            .get();
                    long daysSince = ChronoUnit.DAYS.between(lastTransaction.date(), LocalDateTime.now());
                    System.out.println("    Derniere transaction : Il y a " + daysSince + " jours");
                }
                System.out.println();
            }
        }

        System.out.println("======================================\n");
    }

}