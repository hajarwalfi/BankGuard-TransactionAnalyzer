package ui;

import util.DatabaseConnection;

public class Main {
    public static void main(String[] args) {
        if (!DatabaseConnection.testConnection()) {
            System.out.println("Impossible de se connecter à la base de données.");
            System.out.println("Vérifiez votre fichier db.properties et PostgreSQL.");
            return;
        }
        Menu menu = new Menu();
        menu.start();
        DatabaseConnection.closeConnection();
    }
}