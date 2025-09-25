package com.subscription.manager.entity;

import java.time.LocalDate;

public class AbonnementAvecEngagement extends Abonnement {
    
    private int dureeEngagementMois;
    
    public AbonnementAvecEngagement() {
        super();
    }
    
    public AbonnementAvecEngagement(String nomService, double montantMensuel, LocalDate dateDebut, 
                                   LocalDate dateFin, int dureeEngagementMois) {
        super(nomService, montantMensuel, dateDebut, dateFin);
        this.dureeEngagementMois = dureeEngagementMois;
    }
    
    public int getDureeEngagementMois() {
        return dureeEngagementMois;
    }
    
    public void setDureeEngagementMois(int dureeEngagementMois) {
        this.dureeEngagementMois = dureeEngagementMois;
    }
    
    @Override
    public String toString() {
        return String.format("AbonnementAvecEngagement{id='%s', nomService='%s', montantMensuel=%.2f, " +
                "dateDebut=%s, dateFin=%s, statut=%s, dureeEngagementMois=%d}",
                id, nomService, montantMensuel, dateDebut, dateFin, statut, dureeEngagementMois);
    }
}