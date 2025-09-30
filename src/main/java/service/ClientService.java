package service;

import dao.AccountDAO;
import dao.ClientDAO;
import entity.accounts.Account;
import entity.client.Client;
import util.Validation;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class ClientService {

    private final ClientDAO clientDAO;
    private final AccountDAO accountDAO;

    public ClientService() {
        this.clientDAO = new ClientDAO();
        this.accountDAO = new AccountDAO();
    }

    // ========== CREATE ==========
    public Optional<Client> addClient(String name, String email) {
        if (!Validation.isValidString(name)) {
            System.err.println("Erreur : Le nom du client ne peut pas etre vide");
            return Optional.empty();
        }

        if (!Validation.isValidEmail(email)) {
            System.err.println("Erreur : L'email est invalide");
            return Optional.empty();
        }

        try {
            var client = new Client(name.trim(), email.trim());
            return clientDAO.create(client);
        } catch (SQLException e) {
            System.err.println("Echec de l'ajout du client : " + e.getMessage());
            return Optional.empty();
        }
    }

    // ========== UPDATE ==========
    public boolean updateClient(Long id, String name, String email) {
        if (!Validation.isValidId(id)) {
            System.err.println("Erreur : ID invalide");
            return false;
        }

        if (!Validation.isValidString(name)) {
            System.err.println("Erreur : Le nom du client ne peut pas etre vide");
            return false;
        }

        if (!Validation.isValidEmail(email)) {
            System.err.println("Erreur : L'email est invalide");
            return false;
        }

        Optional<Client> existingClient = clientDAO.findById(id);
        if (existingClient.isEmpty()) {
            System.err.println("Erreur : Client introuvable avec l'ID : " + id);
            return false;
        }

        var updatedClient = new Client(id, name.trim(), email.trim());
        clientDAO.update(updatedClient);
        return true;
    }

    // ========== DELETE ==========
    public boolean deleteClient(Long id) {
        if (!Validation.isValidId(id)) {
            System.err.println("Erreur : ID invalide");
            return false;
        }

        Optional<Client> client = clientDAO.findById(id);
        if (client.isEmpty()) {
            System.err.println("Erreur : Client introuvable avec l'ID : " + id);
            return false;
        }

        // Verifier si le client a des comptes
        List<Account> accounts = accountDAO.findByClientId(id);
        if (!accounts.isEmpty()) {
            System.err.println("Erreur : Impossible de supprimer le client. Il possede " +
                    accounts.size() + " compte(s)");
            return false;
        }

        return clientDAO.delete(id);  // Retourne le résultat du DAO
    }

    // ========== READ BY ID ==========
    public Optional<Client> findClientById(Long id) {
        if (!Validation.isValidId(id)) {
            System.err.println("Erreur : ID invalide");
            return Optional.empty();
        }
        return clientDAO.findById(id);
    }

    // ========== READ BY NAME ==========
    public List<Client> findClientsByName(String name) {
        if (!Validation.isValidString(name)) {
            System.err.println("Erreur : Le nom ne peut pas etre vide");
            return List.of();
        }
        return clientDAO.findByName(name.trim());
    }

    // ========== READ ALL ==========
    public List<Client> getAllClients() {
        return clientDAO.findAll();
    }

    // ========== RAPPORT METHODS  ==========
    public int getAccountCount(Long clientId) {
        if (!Validation.isValidId(clientId)) {
            System.err.println("Erreur : ID invalide");
            return 0;
        }

        return accountDAO.findByClientId(clientId).size();
    }

    public double getTotalBalance(Long clientId) {
        if (!Validation.isValidId(clientId)) {
            System.err.println("Erreur : ID invalide");
            return 0.0;
        }

        var accounts = accountDAO.findByClientId(clientId);

        return accounts.stream()
                .mapToDouble(Account::getBalance)
                .sum();
    }

    public Optional<Account> getMaxBalanceAccount(Long clientId) {
        if (!Validation.isValidId(clientId)) {
            System.err.println("Erreur : ID invalide");
            return Optional.empty();
        }

        var accounts = accountDAO.findByClientId(clientId);

        return accounts.stream()
                .max(Comparator.comparingDouble(Account::getBalance));
    }

    public Optional<Account> getMinBalanceAccount(Long clientId) {
        if (!Validation.isValidId(clientId)) {
            System.err.println("Erreur : ID invalide");
            return Optional.empty();
        }

        var accounts = accountDAO.findByClientId(clientId);

        return accounts.stream()
                .min(Comparator.comparingDouble(Account::getBalance));
    }

    public void displayClientReport(Long clientId) {
        Optional<Client> clientOpt = findClientById(clientId);

        if (clientOpt.isEmpty()) {
            System.err.println("Client introuvable");
            return;
        }

        var client = clientOpt.get();
        var accounts = accountDAO.findByClientId(clientId);

        System.out.println("\n========== RAPPORT CLIENT ==========");
        System.out.println("ID : " + client.id());
        System.out.println("Nom : " + client.name());
        System.out.println("Email : " + client.email());
        System.out.println("Nombre de comptes : " + accounts.size());

        if (!accounts.isEmpty()) {
            double totalBalance = getTotalBalance(clientId);
            System.out.println("Solde total : " + String.format("%.2f", totalBalance) + " MAD");

            getMaxBalanceAccount(clientId).ifPresent(account ->
                    System.out.println("Compte avec solde max : " + account.getNumber() +
                            " (" + String.format("%.2f", account.getBalance()) + " MAD)")
            );

            getMinBalanceAccount(clientId).ifPresent(account ->
                    System.out.println("Compte avec solde min : " + account.getNumber() +
                            " (" + String.format("%.2f", account.getBalance()) + " MAD)")
            );
        } else {
            System.out.println("Ce client n'a aucun compte");
        }
        System.out.println("====================================\n");
    }
}