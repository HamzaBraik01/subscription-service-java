package com.subscription.manager.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    
    private static final String URL = "jdbc:mysql://localhost:3306/subscription_manager";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";
    
    // Alternative PostgreSQL configuration (commented out)
    // private static final String URL = "jdbc:postgresql://localhost:5432/subscription_manager";
    // private static final String USERNAME = "postgres";
    // private static final String PASSWORD = "password";
    
    private static Connection connection = null;
    private static boolean firstConnection = true;
    
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                // For PostgreSQL, use: Class.forName("org.postgresql.Driver");
                
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                
                if (firstConnection) {
                    System.out.println("Connexion à la base de données établie avec succès.");
                    firstConnection = false;
                }
            } catch (ClassNotFoundException e) {
                throw new SQLException("Driver de base de données non trouvé", e);
            }
        }
        return connection;
    }
    
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Connexion à la base de données fermée.");
                firstConnection = true; 
            } catch (SQLException e) {
                System.err.println("Erreur lors de la fermeture de la connexion : " + e.getMessage());
            }
        }
    }
    
    public static void createTables() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            String createAbonnementTable = 
                "CREATE TABLE IF NOT EXISTS abonnement (" +
                "    id VARCHAR(36) PRIMARY KEY," +
                "    nom_service VARCHAR(100) NOT NULL," +
                "    montant_mensuel DECIMAL(10,2) NOT NULL," +
                "    date_debut DATE NOT NULL," +
                "    date_fin DATE," +
                "    statut ENUM('ACTIVE', 'SUSPENDU', 'RESILIE') NOT NULL DEFAULT 'ACTIVE'," +
                "    type_abonnement ENUM('AVEC_ENGAGEMENT', 'SANS_ENGAGEMENT') NOT NULL," +
                "    duree_engagement_mois INT DEFAULT NULL" +
                ")";
            
            String createPaiementTable = 
                "CREATE TABLE IF NOT EXISTS paiement (" +
                "    id_paiement VARCHAR(36) PRIMARY KEY," +
                "    id_abonnement VARCHAR(36) NOT NULL," +
                "    date_echeance DATE NOT NULL," +
                "    date_paiement DATE," +
                "    type_paiement ENUM('CARTE_BANCAIRE', 'VIREMENT', 'PRELEVEMENT', 'PAYPAL', 'ESPECES')," +
                "    statut ENUM('PAYE', 'NON_PAYE', 'EN_RETARD') NOT NULL DEFAULT 'NON_PAYE'," +
                "    montant DECIMAL(10,2) NOT NULL," +
                "    FOREIGN KEY (id_abonnement) REFERENCES abonnement(id) ON DELETE CASCADE" +
                ")";
            
            stmt.execute(createAbonnementTable);
            stmt.execute(createPaiementTable);
            
            System.out.println("Tables créées avec succès.");
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création des tables : " + e.getMessage());
        }
    }
}