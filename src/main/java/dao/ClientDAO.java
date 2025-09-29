package dao;

import entity.client.Client;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClientDAO {

    // ========== CREATE ==========
    public Optional<Client> create(Client client) throws SQLException {
        String sql = "INSERT INTO client (name, email) VALUES (?, ?)";
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = DatabaseConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, client.name());
            stmt.setString(2, client.email());
            stmt.executeUpdate();

            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                Long generatedId = rs.getLong(1);
                return Optional.of(new Client(generatedId, client.name(), client.email()));
            }
        } catch (SQLException e) {
            System.err.println("Erreur de creation du client");
            throw e;
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
        }

        return Optional.empty();
    }

    // ========== UPDATE ==========
    public boolean update(Client client) {
        String sql = "UPDATE client SET name = ?, email = ? WHERE id = ?";
        PreparedStatement stmt = null;

        try {
            stmt = DatabaseConnection.getConnection().prepareStatement(sql);
            stmt.setString(1, client.name());
            stmt.setString(2, client.email());
            stmt.setLong(3, client.id());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Erreur de la mise a jour");
            return false;
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("Erreur de fermeture des ressources: " + e.getMessage());
            }
        }
    }

    // ========== DELETE ==========
    public boolean delete(Long id) {
        String sql = "DELETE FROM client WHERE id = ?";
        PreparedStatement stmt = null;

        try {
            stmt = DatabaseConnection.getConnection().prepareStatement(sql);
            stmt.setLong(1, id);

            int rowsDeleted = stmt.executeUpdate();
            return rowsDeleted > 0;

        } catch (SQLException e) {
            System.err.println("Erreur de la suppression du client");
            return false;
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("Erreur de fermeture des ressources: " + e.getMessage());
            }
        }
    }

    // ========== READ BY ID ==========
    public Optional<Client> findById(Long id) {
        String sql = "SELECT id, name, email FROM client WHERE id = ?";
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = DatabaseConnection.getConnection().prepareStatement(sql);
            stmt.setLong(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                Client client = new Client(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("email")
                );
                return Optional.of(client);
            }
        } catch (SQLException e) {
            System.err.println("Erreur de recherche");
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("Erreur de fermeture des ressources: " + e.getMessage());
            }
        }
        return Optional.empty();
    }

    // ========== READ BY NAME ==========
    public List<Client> findByName(String name) {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT id, name, email FROM client WHERE name ILIKE ? ORDER BY id";
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = DatabaseConnection.getConnection().prepareStatement(sql);
            stmt.setString(1, "%" + name + "%");
            rs = stmt.executeQuery();

            while (rs.next()) {
                Client client = new Client(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("email")
                );
                clients.add(client);
            }
        } catch (SQLException e) {
            System.err.println("Erreur de recherche par nom");
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("Erreur de fermeture des ressources: " + e.getMessage());
            }
        }
        return clients;
    }

    // ========== READ ALL ==========
    public List<Client> findAll() {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT id, name, email FROM client ORDER BY id";
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = DatabaseConnection.getConnection().prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Client client = new Client(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("email")
                );
                clients.add(client);
            }
        } catch (SQLException e) {
            System.err.println("Erreur de recuperation des clients");
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("Erreur de fermeture des ressources: " + e.getMessage());
            }
        }
        return clients;
    }
}