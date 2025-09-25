package com.subscription.manager.dao;

import com.subscription.manager.entity.Paiement;
import com.subscription.manager.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PaiementDAO {
    
    public void create(Paiement paiement) throws SQLException {
        String sql = "INSERT INTO paiement (id_paiement, id_abonnement, date_echeance, date_paiement, " +
                    "type_paiement, statut, montant) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, paiement.getIdPaiement());
            stmt.setString(2, paiement.getIdAbonnement());
            stmt.setDate(3, Date.valueOf(paiement.getDateEcheance()));
            stmt.setDate(4, paiement.getDatePaiement() != null ? Date.valueOf(paiement.getDatePaiement()) : null);
            stmt.setString(5, paiement.getTypePaiement() != null ? paiement.getTypePaiement().name() : null);
            stmt.setString(6, paiement.getStatut().name());
            stmt.setDouble(7, paiement.getMontant());
            
            stmt.executeUpdate();
        }
    }
    
    public Optional<Paiement> findById(String id) throws SQLException {
        String sql = "SELECT * FROM paiement WHERE id_paiement = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToPaiement(rs));
            }
            
            return Optional.empty();
        }
    }
    
    public List<Paiement> findByAbonnement(String idAbonnement) throws SQLException {
        String sql = "SELECT * FROM paiement WHERE id_abonnement = ? ORDER BY date_echeance";
        List<Paiement> paiements = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, idAbonnement);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                paiements.add(mapResultSetToPaiement(rs));
            }
        }
        
        return paiements;
    }
    
    public List<Paiement> findAll() throws SQLException {
        String sql = "SELECT * FROM paiement ORDER BY date_echeance DESC";
        List<Paiement> paiements = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                paiements.add(mapResultSetToPaiement(rs));
            }
        }
        
        return paiements;
    }
    
    public void update(Paiement paiement) throws SQLException {
        String sql = "UPDATE paiement SET date_echeance = ?, date_paiement = ?, type_paiement = ?, " +
                    "statut = ?, montant = ? WHERE id_paiement = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(paiement.getDateEcheance()));
            stmt.setDate(2, paiement.getDatePaiement() != null ? Date.valueOf(paiement.getDatePaiement()) : null);
            stmt.setString(3, paiement.getTypePaiement() != null ? paiement.getTypePaiement().name() : null);
            stmt.setString(4, paiement.getStatut().name());
            stmt.setDouble(5, paiement.getMontant());
            stmt.setString(6, paiement.getIdPaiement());
            
            stmt.executeUpdate();
        }
    }
    
    public void delete(String id) throws SQLException {
        String sql = "DELETE FROM paiement WHERE id_paiement = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            stmt.executeUpdate();
        }
    }
    
    public List<Paiement> findUnpaidByAbonnement(String idAbonnement) throws SQLException {
        String sql = "SELECT * FROM paiement WHERE id_abonnement = ? AND " +
                    "(statut = 'NON_PAYE' OR statut = 'EN_RETARD') ORDER BY date_echeance";
        List<Paiement> paiements = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, idAbonnement);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                paiements.add(mapResultSetToPaiement(rs));
            }
        }
        
        return paiements;
    }
    
    public List<Paiement> findLastPayments(int limit) throws SQLException {
        String sql = "SELECT * FROM paiement WHERE statut = 'PAYE' ORDER BY date_paiement DESC LIMIT ?";
        List<Paiement> paiements = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                paiements.add(mapResultSetToPaiement(rs));
            }
        }
        
        return paiements;
    }
    
    public List<Paiement> findOverduePayments() throws SQLException {
        String sql = "SELECT * FROM paiement WHERE date_echeance < CURDATE() AND statut != 'PAYE' " +
                    "ORDER BY date_echeance";
        List<Paiement> paiements = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                paiements.add(mapResultSetToPaiement(rs));
            }
        }
        
        return paiements;
    }
    
    public double getTotalPaidByAbonnement(String idAbonnement) throws SQLException {
        String sql = "SELECT SUM(montant) as total FROM paiement WHERE id_abonnement = ? AND statut = 'PAYE'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, idAbonnement);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("total");
            }
        }
        
        return 0.0;
    }
    
    private Paiement mapResultSetToPaiement(ResultSet rs) throws SQLException {
        Paiement paiement = new Paiement();
        
        paiement.setIdPaiement(rs.getString("id_paiement"));
        paiement.setIdAbonnement(rs.getString("id_abonnement"));
        paiement.setDateEcheance(rs.getDate("date_echeance").toLocalDate());
        
        Date datePaiement = rs.getDate("date_paiement");
        if (datePaiement != null) {
            paiement.setDatePaiement(datePaiement.toLocalDate());
        }
        
        String typePaiement = rs.getString("type_paiement");
        if (typePaiement != null) {
            paiement.setTypePaiement(Paiement.TypePaiement.valueOf(typePaiement));
        }
        
        paiement.setStatut(Paiement.StatutPaiement.valueOf(rs.getString("statut")));
        paiement.setMontant(rs.getDouble("montant"));
        
        return paiement;
    }
}