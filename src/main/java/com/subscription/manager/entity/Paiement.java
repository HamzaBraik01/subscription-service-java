package com.subscription.manager.entity;

import java.time.LocalDate;
import java.util.UUID;

public class Paiement {
    
    public enum StatutPaiement {
        PAYE, NON_PAYE, EN_RETARD
    }
    
    public enum TypePaiement {
        CARTE_BANCAIRE, VIREMENT, PRELEVEMENT, PAYPAL, ESPECES
    }
    
    private String idPaiement;
    private String idAbonnement;
    private LocalDate dateEcheance;
    private LocalDate datePaiement;
    private TypePaiement typePaiement;
    private StatutPaiement statut;
    private double montant;
    
    public Paiement() {
        this.idPaiement = UUID.randomUUID().toString();
        this.statut = StatutPaiement.NON_PAYE;
    }
    
    public Paiement(String idAbonnement, LocalDate dateEcheance, double montant) {
        this();
        this.idAbonnement = idAbonnement;
        this.dateEcheance = dateEcheance;
        this.montant = montant;
    }
    
    public Paiement(String idAbonnement, LocalDate dateEcheance, LocalDate datePaiement, 
                   TypePaiement typePaiement, double montant) {
        this(idAbonnement, dateEcheance, montant);
        this.datePaiement = datePaiement;
        this.typePaiement = typePaiement;
        this.statut = StatutPaiement.PAYE;
    }
    
    // Getters and Setters
    public String getIdPaiement() {
        return idPaiement;
    }
    
    public void setIdPaiement(String idPaiement) {
        this.idPaiement = idPaiement;
    }
    
    public String getIdAbonnement() {
        return idAbonnement;
    }
    
    public void setIdAbonnement(String idAbonnement) {
        this.idAbonnement = idAbonnement;
    }
    
    public LocalDate getDateEcheance() {
        return dateEcheance;
    }
    
    public void setDateEcheance(LocalDate dateEcheance) {
        this.dateEcheance = dateEcheance;
    }
    
    public LocalDate getDatePaiement() {
        return datePaiement;
    }
    
    public void setDatePaiement(LocalDate datePaiement) {
        this.datePaiement = datePaiement;
    }
    
    public TypePaiement getTypePaiement() {
        return typePaiement;
    }
    
    public void setTypePaiement(TypePaiement typePaiement) {
        this.typePaiement = typePaiement;
    }
    
    public StatutPaiement getStatut() {
        return statut;
    }
    
    public void setStatut(StatutPaiement statut) {
        this.statut = statut;
    }
    
    public double getMontant() {
        return montant;
    }
    
    public void setMontant(double montant) {
        this.montant = montant;
    }
    
    @Override
    public String toString() {
        return String.format("Paiement{idPaiement='%s', idAbonnement='%s', dateEcheance=%s, " +
                "datePaiement=%s, typePaiement=%s, statut=%s, montant=%.2f}",
                idPaiement, idAbonnement, dateEcheance, datePaiement, typePaiement, statut, montant);
    }
}