package com.subscription.manager.service;

import com.subscription.manager.dao.PaiementDAO;
import com.subscription.manager.dao.AbonnementDAO;
import com.subscription.manager.entity.Paiement;
import com.subscription.manager.entity.Abonnement;
import com.subscription.manager.util.DateUtil;
import com.subscription.manager.util.ValidationUtil;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class PaiementService {
    
    private final PaiementDAO paiementDAO;
    private final AbonnementDAO abonnementDAO;
    
    public PaiementService() {
        this.paiementDAO = new PaiementDAO();
        this.abonnementDAO = new AbonnementDAO();
    }
    
    
    public String enregistrerPaiement(String idAbonnement, LocalDate dateEcheance, 
                                     LocalDate datePaiement, Paiement.TypePaiement typePaiement, 
                                     double montant) throws SQLException {
        
        if (!ValidationUtil.isValidId(idAbonnement)) {
            throw new IllegalArgumentException("ID d'abonnement invalide");
        }
        if (!ValidationUtil.isValidDate(dateEcheance)) {
            throw new IllegalArgumentException("Date d'échéance invalide");
        }
        if (!ValidationUtil.isValidDate(datePaiement)) {
            throw new IllegalArgumentException("Date de paiement invalide");
        }
        if (!ValidationUtil.isPositiveAmount(montant)) {
            throw new IllegalArgumentException("Le montant doit être positif");
        }
        
        Optional<Abonnement> abonnement = abonnementDAO.findById(idAbonnement);
        if (!abonnement.isPresent()) {
            throw new IllegalArgumentException("Abonnement non trouvé");
        }
        
        Paiement paiement = new Paiement(idAbonnement, dateEcheance, datePaiement, typePaiement, montant);
        paiement.setStatut(Paiement.StatutPaiement.PAYE);
        
        paiementDAO.create(paiement);
        return paiement.getIdPaiement();
    }
    
    
    public void updatePaiement(Paiement paiement) throws SQLException {
        if (!ValidationUtil.isValidId(paiement.getIdPaiement())) {
            throw new IllegalArgumentException("ID de paiement invalide");
        }
        
        Optional<Paiement> existingPaiement = paiementDAO.findById(paiement.getIdPaiement());
        if (!existingPaiement.isPresent()) {
            throw new IllegalArgumentException("Paiement non trouvé");
        }
        
        paiementDAO.update(paiement);
    }
    
    
    public void deletePaiement(String id) throws SQLException {
        if (!ValidationUtil.isValidId(id)) {
            throw new IllegalArgumentException("ID de paiement invalide");
        }
        
        paiementDAO.delete(id);
    }
    
    
    public List<Paiement> getPaiementsByAbonnement(String idAbonnement) throws SQLException {
        return paiementDAO.findByAbonnement(idAbonnement);
    }
    
    
    public List<Paiement> detecterPaiementsEnRetard() throws SQLException {
        LocalDate today = LocalDate.now();
        
        List<Paiement> overduePayments = paiementDAO.findAll()
                .stream()
                .filter(paiement -> paiement.getStatut() == Paiement.StatutPaiement.NON_PAYE)
                .filter(paiement -> DateUtil.isOverdue(paiement.getDateEcheance()))
                .collect(Collectors.toList());
        
        overduePayments.forEach(paiement -> {
            paiement.setStatut(Paiement.StatutPaiement.EN_RETARD);
            try {
                paiementDAO.update(paiement);
            } catch (SQLException e) {
                System.err.println("Erreur lors de la mise à jour du statut: " + e.getMessage());
            }
        });
        
        return overduePayments;
    }
    
    
    public Map<String, Double> getPaiementsImpayesAvecMontantTotal() throws SQLException {
        return abonnementDAO.findByType("AVEC_ENGAGEMENT")
                .stream()
                .collect(Collectors.toMap(
                    Abonnement::getId,
                    abonnement -> {
                        try {
                            return paiementDAO.findUnpaidByAbonnement(abonnement.getId())
                                    .stream()
                                    .mapToDouble(Paiement::getMontant)
                                    .sum();
                        } catch (SQLException e) {
                            return 0.0;
                        }
                    }
                ))
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() > 0)
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue
                ));
    }
    
    
    public double getSommePapeeAbonnement(String idAbonnement) throws SQLException {
        return paiementDAO.getTotalPaidByAbonnement(idAbonnement);
    }
    
    
    public List<Paiement> getLast5Paiements() throws SQLException {
        return paiementDAO.findLastPayments(5);
    }
    
    
    public Map<String, Object> generateRapportMensuel() throws SQLException {
        LocalDate startOfMonth = DateUtil.getFirstDayOfMonth();
        LocalDate endOfMonth = DateUtil.getLastDayOfMonth();
        
        List<Paiement> monthlyPayments = paiementDAO.findAll()
                .stream()
                .filter(paiement -> paiement.getDatePaiement() != null)
                .filter(paiement -> !paiement.getDatePaiement().isBefore(startOfMonth))
                .filter(paiement -> !paiement.getDatePaiement().isAfter(endOfMonth))
                .filter(paiement -> paiement.getStatut() == Paiement.StatutPaiement.PAYE)
                .collect(Collectors.toList());
        
        Map<String, Object> rapport = new HashMap<>();
        rapport.put("periode", startOfMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.FRENCH)));
        rapport.put("nombrePaiements", monthlyPayments.size());
        rapport.put("montantTotal", monthlyPayments.stream().mapToDouble(Paiement::getMontant).sum());
        rapport.put("montantMoyen", monthlyPayments.stream().mapToDouble(Paiement::getMontant).average().orElse(0.0));
        
        Map<Paiement.TypePaiement, Double> byType = monthlyPayments.stream()
                .filter(p -> p.getTypePaiement() != null)
                .collect(Collectors.groupingBy(
                    Paiement::getTypePaiement,
                    Collectors.summingDouble(Paiement::getMontant)
                ));
        rapport.put("repartitionParType", byType);
        
        return rapport;
    }
    
    
    public Map<String, Object> generateRapportAnnuel() throws SQLException {
        LocalDate startOfYear = DateUtil.getFirstDayOfYear();
        LocalDate endOfYear = DateUtil.getLastDayOfYear();
        
        List<Paiement> yearlyPayments = paiementDAO.findAll()
                .stream()
                .filter(paiement -> paiement.getDatePaiement() != null)
                .filter(paiement -> !paiement.getDatePaiement().isBefore(startOfYear))
                .filter(paiement -> !paiement.getDatePaiement().isAfter(endOfYear))
                .filter(paiement -> paiement.getStatut() == Paiement.StatutPaiement.PAYE)
                .collect(Collectors.toList());
        
        Map<String, Object> rapport = new HashMap<>();
        rapport.put("annee", startOfYear.getYear());
        rapport.put("nombrePaiements", yearlyPayments.size());
        rapport.put("montantTotal", yearlyPayments.stream().mapToDouble(Paiement::getMontant).sum());
        
        Map<Integer, Double> monthlyBreakdown = yearlyPayments.stream()
                .collect(Collectors.groupingBy(
                    paiement -> paiement.getDatePaiement().getMonthValue(),
                    Collectors.summingDouble(Paiement::getMontant)
                ));
        rapport.put("repartitionMensuelle", monthlyBreakdown);
        
        Map<Paiement.TypePaiement, Long> typeBreakdown = yearlyPayments.stream()
                .filter(p -> p.getTypePaiement() != null)
                .collect(Collectors.groupingBy(
                    Paiement::getTypePaiement,
                    Collectors.counting()
                ));
        rapport.put("repartitionParType", typeBreakdown);
        
        return rapport;
    }
    
    
    public Map<String, Object> generateRapportImpayes() throws SQLException {
        List<Paiement> unpaidPayments = paiementDAO.findAll()
                .stream()
                .filter(paiement -> paiement.getStatut() != Paiement.StatutPaiement.PAYE)
                .collect(Collectors.toList());
        
        Map<String, Object> rapport = new HashMap<>();
        rapport.put("nombreImpayes", unpaidPayments.size());
        rapport.put("montantTotalImpaye", unpaidPayments.stream().mapToDouble(Paiement::getMontant).sum());
        
        Map<String, List<Paiement>> bySubscription = unpaidPayments.stream()
                .collect(Collectors.groupingBy(Paiement::getIdAbonnement));
        
        Map<String, Double> amountBySubscription = bySubscription.entrySet()
                .stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> entry.getValue().stream().mapToDouble(Paiement::getMontant).sum()
                ));
        
        rapport.put("repartitionParAbonnement", amountBySubscription);
        
        Map<Paiement.StatutPaiement, Long> statusBreakdown = unpaidPayments.stream()
                .collect(Collectors.groupingBy(
                    Paiement::getStatut,
                    Collectors.counting()
                ));
        rapport.put("repartitionParStatut", statusBreakdown);
        
        return rapport;
    }
    
    
    public void printRapport(Map<String, Object> rapport, String type) {
        System.out.println("==========================================");
        System.out.println("        RAPPORT " + type.toUpperCase());
        System.out.println("==========================================");
        
        rapport.forEach((key, value) -> {
            if (value instanceof Map) {
                System.out.println(key + ":");
                ((Map<?, ?>) value).forEach((k, v) -> 
                    System.out.println("  " + k + ": " + v));
            } else {
                System.out.println(key + ": " + value);
            }
            System.out.println();
        });
    }
}