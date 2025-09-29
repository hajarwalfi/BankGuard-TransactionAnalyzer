package ui;

import util.DatabaseConnection;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Test de connexion à la base de données ===\n");

        if (DatabaseConnection.testConnection()) {
            System.out.println("✅ La connexion fonctionne !");
        } else {
            System.out.println("❌ Problème de connexion");
        }

        DatabaseConnection.closeConnection();
    }
}