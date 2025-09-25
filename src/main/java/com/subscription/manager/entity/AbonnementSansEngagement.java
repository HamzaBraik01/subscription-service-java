package com.subscription.manager.entity;

import java.time.LocalDate;

public class AbonnementSansEngagement extends Abonnement {
    
    public AbonnementSansEngagement() {
        super();
    }
    
    public AbonnementSansEngagement(String nomService, double montantMensuel, LocalDate dateDebut, LocalDate dateFin) {
        super(nomService, montantMensuel, dateDebut, dateFin);
    }
    
    @Override
    public String toString() {
        return String.format("AbonnementSansEngagement{id='%s', nomService='%s', montantMensuel=%.2f, " +
                "dateDebut=%s, dateFin=%s, statut=%s}",
                id, nomService, montantMensuel, dateDebut, dateFin, statut);
    }
}