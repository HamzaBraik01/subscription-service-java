package com.subscription.manager.service;

import com.subscription.manager.dao.AbonnementDAO;
import com.subscription.manager.dao.PaiementDAO;
import com.subscription.manager.entity.Abonnement;
import com.subscription.manager.entity.AbonnementAvecEngagement;
import com.subscription.manager.entity.AbonnementSansEngagement;
import com.subscription.manager.entity.Paiement;
import com.subscription.manager.util.DateUtil;
import com.subscription.manager.util.ValidationUtil;
import com.subscription.manager.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AbonnementService {
    
    private final AbonnementDAO abonnementDAO;
    private final PaiementDAO paiementDAO;
    
    public AbonnementService() {
        this.abonnementDAO = new AbonnementDAO();
        this.paiementDAO = new PaiementDAO();
    }
    
    
    private void validateNoDuplicateEngagementService(String nomService) throws SQLException {
        List<Abonnement> existingAbonnements = abonnementDAO.findAll()
                .stream()
                .filter(abonnement -> abonnement.getNomService().equalsIgnoreCase(nomService.trim()))
                .filter(abonnement -> abonnement.getStatut() == Abonnement.Statut.ACTIVE)
                .filter(abonnement -> abonnement instanceof AbonnementAvecEngagement)
                .collect(Collectors.toList());
        
        if (!existingAbonnements.isEmpty()) {
            AbonnementAvecEngagement existingEngagement = (AbonnementAvecEngagement) existingAbonnements.get(0);
            LocalDate dateFin = existingEngagement.getDateFin();
            
            throw new IllegalArgumentException(
                String.format("Erreur : Vous avez deja un abonnement actif pour '%s' avec un engagement en cours.\n" +
                             "Impossible de creer un nouvel abonnement avec le meme nom tant que la duree d'engagement n'est pas terminee.\n" +
                             "Date de fin de l'engagement actuel : %s", 
                             nomService, dateFin != null ? DateUtil.formatDate(dateFin) : "Non definie")
            );
        }
    }
    
    
    private void validateNoDuplicateActiveService(String nomService) throws SQLException {
        List<Abonnement> existingAbonnements = abonnementDAO.findAll()
                .stream()
                .filter(abonnement -> abonnement.getNomService().equalsIgnoreCase(nomService.trim()))
                .filter(abonnement -> abonnement.getStatut() == Abonnement.Statut.ACTIVE)
                .collect(Collectors.toList());
        
        if (!existingAbonnements.isEmpty()) {
            Abonnement existing = existingAbonnements.get(0);
            String typeExisting = existing instanceof AbonnementAvecEngagement ? "avec engagement" : "sans engagement";
            
            throw new IllegalArgumentException(
                String.format("Erreur : Vous avez deja un abonnement actif pour '%s' (%s).\n" +
                             "Veuillez d'abord resilier l'abonnement existant ou choisir un autre nom de service.", 
                             nomService, typeExisting)
            );
        }
    }

    
    public String createAbonnementAvecEngagement(String nomService, double montantMensuel, 
                                               LocalDate dateDebut, int dureeEngagementMois) throws SQLException {
        if (!ValidationUtil.isValidServiceName(nomService)) {
            throw new IllegalArgumentException("Nom du service invalide");
        }
        if (!ValidationUtil.isPositiveAmount(montantMensuel)) {
            throw new IllegalArgumentException("Le montant mensuel doit être positif");
        }
        if (!ValidationUtil.isValidDate(dateDebut)) {
            throw new IllegalArgumentException("Date de début invalide");
        }
        if (!ValidationUtil.isValidEngagementDuration(dureeEngagementMois)) {
            throw new IllegalArgumentException("Durée d'engagement invalide");
        }
        
        validateNoDuplicateEngagementService(nomService);
        
        LocalDate dateFin = DateUtil.addMonths(dateDebut, dureeEngagementMois);
        AbonnementAvecEngagement abonnement = new AbonnementAvecEngagement(
            nomService, montantMensuel, dateDebut, dateFin, dureeEngagementMois
        );
        
        abonnementDAO.create(abonnement);
        
        generateEcheances(abonnement);
        
        return abonnement.getId();
    }
    
    
    public String createAbonnementSansEngagement(String nomService, double montantMensuel, 
                                               LocalDate dateDebut, LocalDate dateFin) throws SQLException {
        if (!ValidationUtil.isValidServiceName(nomService)) {
            throw new IllegalArgumentException("Nom du service invalide");
        }
        if (!ValidationUtil.isPositiveAmount(montantMensuel)) {
            throw new IllegalArgumentException("Le montant mensuel doit être positif");
        }
        if (!ValidationUtil.isValidDateRange(dateDebut, dateFin)) {
            throw new IllegalArgumentException("Plage de dates invalide");
        }
        
        validateNoDuplicateActiveService(nomService);
        
        AbonnementSansEngagement abonnement = new AbonnementSansEngagement(
            nomService, montantMensuel, dateDebut, dateFin
        );
        
        abonnementDAO.create(abonnement);
        
        generateEcheances(abonnement);
        
        return abonnement.getId();
    }
    
    
    public void updateAbonnement(Abonnement abonnement) throws SQLException {
        if (!ValidationUtil.isValidId(abonnement.getId())) {
            throw new IllegalArgumentException("ID d'abonnement invalide");
        }
        
        Optional<Abonnement> existingAbonnement = abonnementDAO.findById(abonnement.getId());
        if (!existingAbonnement.isPresent()) {
            throw new IllegalArgumentException("Abonnement non trouvé");
        }
        
        abonnementDAO.update(abonnement);
    }
    
    
    public void deleteAbonnement(String id) throws SQLException {
        if (!ValidationUtil.isValidId(id)) {
            throw new IllegalArgumentException("ID d'abonnement invalide");
        }
        
        abonnementDAO.delete(id);
    }
    
    
    public void resilierAbonnement(String id) throws SQLException {
        Optional<Abonnement> abonnementOpt = abonnementDAO.findById(id);
        if (!abonnementOpt.isPresent()) {
            throw new IllegalArgumentException("Abonnement non trouvé");
        }
        
        Abonnement abonnement = abonnementOpt.get();
        abonnement.setStatut(Abonnement.Statut.RESILIE);
        abonnement.setDateFin(LocalDate.now());
        
        abonnementDAO.update(abonnement);
    }
    
    
    public Optional<Abonnement> findAbonnementById(String id) throws SQLException {
        return abonnementDAO.findById(id);
    }
    
    
    public List<Abonnement> getAllAbonnements() throws SQLException {
        return abonnementDAO.findAll();
    }
    
    
    public List<Abonnement> getActiveAbonnements() throws SQLException {
        return abonnementDAO.findAll()
                .stream()
                .filter(abonnement -> abonnement.getStatut() == Abonnement.Statut.ACTIVE)
                .collect(Collectors.toList());
    }
    
    
    public List<Abonnement> getAbonnementsByType(Class<? extends Abonnement> type) throws SQLException {
        return abonnementDAO.findAll()
                .stream()
                .filter(type::isInstance)
                .collect(Collectors.toList());
    }
    
    
    public List<Abonnement> getAbonnementsExpiringSoon() throws SQLException {
        LocalDate thirtyDaysFromNow = LocalDate.now().plusDays(30);
        
        return abonnementDAO.findAll()
                .stream()
                .filter(abonnement -> abonnement.getDateFin() != null)
                .filter(abonnement -> abonnement.getDateFin().isBefore(thirtyDaysFromNow))
                .filter(abonnement -> abonnement.getStatut() == Abonnement.Statut.ACTIVE)
                .collect(Collectors.toList());
    }
    
    
    public void generateEcheances(Abonnement abonnement) throws SQLException {
        LocalDate startDate = abonnement.getDateDebut();
        LocalDate endDate = abonnement.getDateFin();
        
        List<LocalDate> dueDates = DateUtil.generateMonthlyDueDates(startDate, endDate);
        
        List<Paiement> paiements = dueDates.stream()
                .map(dueDate -> new Paiement(abonnement.getId(), dueDate, abonnement.getMontantMensuel()))
                .collect(Collectors.toList());
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false); // Start transaction
            
            String sql = "INSERT INTO paiement (id_paiement, id_abonnement, date_echeance, date_paiement, " +
                        "type_paiement, statut, montant) VALUES (?, ?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                for (Paiement paiement : paiements) {
                    stmt.setString(1, paiement.getIdPaiement());
                    stmt.setString(2, paiement.getIdAbonnement());
                    stmt.setDate(3, Date.valueOf(paiement.getDateEcheance()));
                    stmt.setDate(4, null); 
                    stmt.setString(5, null); 
                    stmt.setString(6, paiement.getStatut().name());
                    stmt.setDouble(7, paiement.getMontant());
                    stmt.addBatch();
                }
                
                stmt.executeBatch();
                conn.commit(); 
                
                System.out.println("Échéances de paiement générées avec succès (" + paiements.size() + " paiements).");
                
            } catch (SQLException e) {
                conn.rollback(); 
                throw e;
            } finally {
                conn.setAutoCommit(true); 
            }
        }
    }
    
    
    public double getTotalMonthlyCost() throws SQLException {
        return getActiveAbonnements()
                .stream()
                .mapToDouble(Abonnement::getMontantMensuel)
                .sum();
    }
    
   
    public void printAbonnementsGroupedByStatus() throws SQLException {
        abonnementDAO.findAll()
                .stream()
                .collect(Collectors.groupingBy(Abonnement::getStatut))
                .forEach((statut, abonnements) -> {
                    System.out.println("=== " + statut + " ===");
                    abonnements.forEach(System.out::println);
                    System.out.println();
                });
    }
}