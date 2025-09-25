package com.subscription.manager.entity;

import java.time.LocalDate;
import java.util.UUID;

public abstract class Abonnement {
    
    public enum Statut {
        ACTIVE, SUSPENDU, RESILIE
    }
    
    protected String id;
    protected String nomService;
    protected double montantMensuel;
    protected LocalDate dateDebut;
    protected LocalDate dateFin;
    protected Statut statut;
    
    public Abonnement() {
        this.id = UUID.randomUUID().toString();
        this.statut = Statut.ACTIVE;
    }
    
    public Abonnement(String nomService, double montantMensuel, LocalDate dateDebut, LocalDate dateFin) {
        this();
        this.nomService = nomService;
        this.montantMensuel = montantMensuel;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getNomService() {
        return nomService;
    }
    
    public void setNomService(String nomService) {
        this.nomService = nomService;
    }
    
    public double getMontantMensuel() {
        return montantMensuel;
    }
    
    public void setMontantMensuel(double montantMensuel) {
        this.montantMensuel = montantMensuel;
    }
    
    public LocalDate getDateDebut() {
        return dateDebut;
    }
    
    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }
    
    public LocalDate getDateFin() {
        return dateFin;
    }
    
    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }
    
    public Statut getStatut() {
        return statut;
    }
    
    public void setStatut(Statut statut) {
        this.statut = statut;
    }
    
    @Override
    public String toString() {
        return String.format("Abonnement{id='%s', nomService='%s', montantMensuel=%.2f, dateDebut=%s, dateFin=%s, statut=%s}",
                id, nomService, montantMensuel, dateDebut, dateFin, statut);
    }
}