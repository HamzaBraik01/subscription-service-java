package com.subscription.manager.ui;

import com.subscription.manager.entity.Abonnement;
import com.subscription.manager.entity.AbonnementAvecEngagement;
import com.subscription.manager.entity.AbonnementSansEngagement;
import com.subscription.manager.entity.Paiement;
import com.subscription.manager.service.AbonnementService;
import com.subscription.manager.service.PaiementService;
import com.subscription.manager.util.DateUtil;
import com.subscription.manager.util.DatabaseConnection;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

public class ConsoleMenu {
    
    private final Scanner scanner;
    private final AbonnementService abonnementService;
    private final PaiementService paiementService;
    
    public ConsoleMenu() {
        this.scanner = new Scanner(System.in);
        this.abonnementService = new AbonnementService();
        this.paiementService = new PaiementService();
    }
    
    public void start() {
        try {
            DatabaseConnection.createTables();
        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation de la base de données: " + e.getMessage());
            return;
        }
        
        System.out.println("===========================================");
        System.out.println("   SYSTÈME DE SUIVI DES ABONNEMENTS");
        System.out.println("===========================================\n");
        
        boolean continuer = true;
        
        while (continuer) {
            afficherMenuPrincipal();
            int choix = lireChoixUtilisateur();
            
            try {
                switch (choix) {
                    case 1:
                        gererAbonnements();
                        break;
                    case 2:
                        gererPaiements();
                        break;
                    case 3:
                        afficherRapports();
                        break;
                    case 4:
                        afficherStatistiques();
                        break;
                    case 0:
                        continuer = false;
                        System.out.println("Au revoir!");
                        DatabaseConnection.closeConnection();
                        break;
                    default:
                        System.out.println("Choix invalide. Veuillez réessayer.");
                        break;
                }
            } catch (Exception e) {
                System.err.println("Erreur: " + e.getMessage());
            }
            
            if (continuer) {
                System.out.println("\nAppuyez sur Entrée pour continuer...");
                scanner.nextLine();
            }
        }
    }
    
    private void afficherMenuPrincipal() {
        System.out.println("\n=== MENU PRINCIPAL ===");
        System.out.println("1. Gestion des Abonnements");
        System.out.println("2. Gestion des Paiements");
        System.out.println("3. Rapports Financiers");
        System.out.println("4. Statistiques");
        System.out.println("0. Quitter");
        System.out.print("Votre choix: ");
    }
    
    private void gererAbonnements() throws SQLException {
        boolean retour = false;
        
        while (!retour) {
            System.out.println("\n=== GESTION DES ABONNEMENTS ===");
            System.out.println("1. Créer un abonnement avec engagement");
            System.out.println("2. Créer un abonnement sans engagement");
            System.out.println("3. Modifier un abonnement");
            System.out.println("4. Supprimer un abonnement");
            System.out.println("5. Résilier un abonnement");
            System.out.println("6. Consulter la liste des abonnements");
            System.out.println("7. Afficher les abonnements actifs");
            System.out.println("8. Afficher les abonnements par type");
            System.out.println("0. Retour au menu principal");
            System.out.print("Votre choix: ");
            
            int choix = lireChoixUtilisateur();
            
            switch (choix) {
                case 1:
                    creerAbonnementAvecEngagement();
                    break;
                case 2:
                    creerAbonnementSansEngagement();
                    break;
                case 3:
                    modifierAbonnement();
                    break;
                case 4:
                    supprimerAbonnement();
                    break;
                case 5:
                    resilierAbonnement();
                    break;
                case 6:
                    afficherTousAbonnements();
                    break;
                case 7:
                    afficherAbonnementsActifs();
                    break;
                case 8:
                    afficherAbonnementsParType();
                    break;
                case 0:
                    retour = true;
                    break;
                default:
                    System.out.println("Choix invalide.");
                    break;
            }
        }
    }
    
    private void gererPaiements() throws SQLException {
        boolean retour = false;
        
        while (!retour) {
            System.out.println("\n=== GESTION DES PAIEMENTS ===");
            System.out.println("1. Enregistrer un paiement");
            System.out.println("2. Modifier un paiement");
            System.out.println("3. Supprimer un paiement");
            System.out.println("4. Afficher les paiements d'un abonnement");
            System.out.println("5. Consulter les paiements manqués");
            System.out.println("6. Afficher la somme payée d'un abonnement");
            System.out.println("7. Afficher les 5 derniers paiements");
            System.out.println("8. Détecter les paiements en retard");
            System.out.println("0. Retour au menu principal");
            System.out.print("Votre choix: ");
            
            int choix = lireChoixUtilisateur();
            
            switch (choix) {
                case 1:
                    enregistrerPaiement();
                    break;
                case 2:
                    modifierPaiement();
                    break;
                case 3:
                    supprimerPaiement();
                    break;
                case 4:
                    afficherPaiementsAbonnement();
                    break;
                case 5:
                    afficherPaiementsManques();
                    break;
                case 6:
                    afficherSommePayeeAbonnement();
                    break;
                case 7:
                    afficherDerniersPaiements();
                    break;
                case 8:
                    detecterPaiementsEnRetard();
                    break;
                case 0:
                    retour = true;
                    break;
                default:
                    System.out.println("Choix invalide.");
                    break;
            }
        }
    }
    
    private void afficherRapports() throws SQLException {
        boolean retour = false;
        
        while (!retour) {
            System.out.println("\n=== RAPPORTS FINANCIERS ===");
            System.out.println("1. Rapport mensuel");
            System.out.println("2. Rapport annuel");
            System.out.println("3. Rapport des impayés");
            System.out.println("0. Retour au menu principal");
            System.out.print("Votre choix: ");
            
            int choix = lireChoixUtilisateur();
            
            switch (choix) {
                case 1:
                    Map<String, Object> rapportMensuel = paiementService.generateRapportMensuel();
                    paiementService.printRapport(rapportMensuel, "MENSUEL");
                    break;
                case 2:
                    Map<String, Object> rapportAnnuel = paiementService.generateRapportAnnuel();
                    paiementService.printRapport(rapportAnnuel, "ANNUEL");
                    break;
                case 3:
                    Map<String, Object> rapportImpayes = paiementService.generateRapportImpayes();
                    paiementService.printRapport(rapportImpayes, "IMPAYÉS");
                    break;
                case 0:
                    retour = true;
                    break;
                default:
                    System.out.println("Choix invalide.");
                    break;
            }
        }
    }
    
    private void afficherStatistiques() throws SQLException {
        System.out.println("\n=== STATISTIQUES ===");
        
        List<Abonnement> abonnements = abonnementService.getAllAbonnements();
        List<Abonnement> actifs = abonnementService.getActiveAbonnements();
        
        System.out.println("Nombre total d'abonnements: " + abonnements.size());
        System.out.println("Abonnements actifs: " + actifs.size());
        System.out.println("Coût mensuel total: " + String.format("%.2f €", abonnementService.getTotalMonthlyCost()));
        
        long avecEngagement = abonnements.stream()
                .filter(abonnement -> abonnement instanceof AbonnementAvecEngagement)
                .count();
        long sansEngagement = abonnements.stream()
                .filter(abonnement -> abonnement instanceof AbonnementSansEngagement)
                .count();
        
        System.out.println("Abonnements avec engagement: " + avecEngagement);
        System.out.println("Abonnements sans engagement: " + sansEngagement);
        
        System.out.println("\n=== RÉPARTITION PAR STATUT ===");
        abonnementService.printAbonnementsGroupedByStatus();
    }
    
    private void creerAbonnementAvecEngagement() throws SQLException {
        System.out.println("\n--- Création d'un abonnement avec engagement ---");
        
        try {
            System.out.print("Nom du service: ");
            String nomService = scanner.nextLine();
            
            System.out.print("Montant mensuel (DH): ");
            double montant = Double.parseDouble(scanner.nextLine());
            
            System.out.print("Date de début (dd/MM/yyyy): ");
            LocalDate dateDebut = DateUtil.parseDate(scanner.nextLine());
            
            System.out.print("Durée d'engagement (mois): ");
            int duree = Integer.parseInt(scanner.nextLine());
            
            String id = abonnementService.createAbonnementAvecEngagement(nomService, montant, dateDebut, duree);
            System.out.println("Abonnement cree avec succes. ID: " + id);
            
        } catch (IllegalArgumentException e) {
            System.err.println("\n" + e.getMessage());
            System.out.println("\nSuggestions :");
            System.out.println("   • Verifiez si vous avez deja un abonnement actif pour ce service");
            System.out.println("   • Consultez la liste des abonnements (option 6)");
            System.out.println("   • Resiliez l'ancien abonnement si necessaire (option 5)");
        } catch (Exception e) {
            System.err.println("Erreur lors de la creation : " + e.getMessage());
        }
    }
    
    private void creerAbonnementSansEngagement() throws SQLException {
        System.out.println("\n--- Création d'un abonnement sans engagement ---");
        
        try {
            System.out.print("Nom du service: ");
            String nomService = scanner.nextLine();
            
            System.out.print("Montant mensuel (€): ");
            double montant = Double.parseDouble(scanner.nextLine());
            
            System.out.print("Date de début (dd/MM/yyyy): ");
            LocalDate dateDebut = DateUtil.parseDate(scanner.nextLine());
            
            System.out.print("Date de fin (dd/MM/yyyy): ");
            LocalDate dateFin = DateUtil.parseDate(scanner.nextLine());
            
            String id = abonnementService.createAbonnementSansEngagement(nomService, montant, dateDebut, dateFin);
            System.out.println("Abonnement cree avec succes. ID: " + id);
            
        } catch (IllegalArgumentException e) {
            System.err.println("\n" + e.getMessage());
            System.out.println("\nSuggestions :");
            System.out.println("   • Verifiez si vous avez deja un abonnement actif pour ce service");
            System.out.println("   • Consultez la liste des abonnements (option 6)");
            System.out.println("   • Resiliez l'ancien abonnement si necessaire (option 5)");
        } catch (Exception e) {
            System.err.println("Erreur lors de la creation : " + e.getMessage());
        }
    }
    
    private void modifierAbonnement() throws SQLException {
        System.out.print("ID de l'abonnement à modifier: ");
        String id = scanner.nextLine();
        
        Optional<Abonnement> abonnementOpt = abonnementService.findAbonnementById(id);
        if (!abonnementOpt.isPresent()) {
            System.out.println("Abonnement non trouvé.");
            return;
        }
        
        Abonnement abonnement = abonnementOpt.get();
        System.out.println("Abonnement actuel: " + abonnement);
        
        System.out.print("Nouveau nom du service (actuel: " + abonnement.getNomService() + "): ");
        String nomService = scanner.nextLine();
        if (!nomService.isEmpty()) {
            abonnement.setNomService(nomService);
        }
        
        System.out.print("Nouveau montant mensuel (actuel: " + abonnement.getMontantMensuel() + "): ");
        String montantStr = scanner.nextLine();
        if (!montantStr.isEmpty()) {
            abonnement.setMontantMensuel(Double.parseDouble(montantStr));
        }
        
        abonnementService.updateAbonnement(abonnement);
        System.out.println("Abonnement modifié avec succès.");
    }
    
    private void supprimerAbonnement() throws SQLException {
        System.out.print("ID de l'abonnement à supprimer: ");
        String id = scanner.nextLine();
        
        abonnementService.deleteAbonnement(id);
        System.out.println("Abonnement supprimé avec succès.");
    }
    
    private void resilierAbonnement() throws SQLException {
        System.out.print("ID de l'abonnement à résilier: ");
        String id = scanner.nextLine();
        
        abonnementService.resilierAbonnement(id);
        System.out.println("Abonnement résilié avec succès.");
    }
    
    private void afficherTousAbonnements() throws SQLException {
        List<Abonnement> abonnements = abonnementService.getAllAbonnements();
        
        if (abonnements.isEmpty()) {
            System.out.println("Aucun abonnement trouvé.");
        } else {
            System.out.println("\n=== LISTE DES ABONNEMENTS ===");
            for (Abonnement abonnement : abonnements) {
                System.out.println(abonnement);
            }
        }
    }
    
    private void afficherAbonnementsActifs() throws SQLException {
        List<Abonnement> actifs = abonnementService.getActiveAbonnements();
        
        if (actifs.isEmpty()) {
            System.out.println("Aucun abonnement actif.");
        } else {
            System.out.println("\n=== ABONNEMENTS ACTIFS ===");
            for (Abonnement abonnement : actifs) {
                System.out.println(abonnement);
            }
        }
    }
    
    private void afficherAbonnementsParType() throws SQLException {
        System.out.println("1. Avec engagement");
        System.out.println("2. Sans engagement");
        System.out.print("Choisir le type: ");
        
        int choix = lireChoixUtilisateur();
        List<Abonnement> abonnements;
        
        if (choix == 1) {
            abonnements = abonnementService.getAbonnementsByType(AbonnementAvecEngagement.class);
            System.out.println("\n=== ABONNEMENTS AVEC ENGAGEMENT ===");
        } else {
            abonnements = abonnementService.getAbonnementsByType(AbonnementSansEngagement.class);
            System.out.println("\n=== ABONNEMENTS SANS ENGAGEMENT ===");
        }
        
        if (abonnements.isEmpty()) {
            System.out.println("Aucun abonnement de ce type.");
        } else {
            for (Abonnement abonnement : abonnements) {
                System.out.println(abonnement);
            }
        }
    }
    
    private void enregistrerPaiement() throws SQLException {
        System.out.println("\n--- Enregistrement d'un paiement ---");
        
        System.out.print("ID de l'abonnement: ");
        String idAbonnement = scanner.nextLine();
        
        System.out.print("Date d'échéance (dd/MM/yyyy): ");
        LocalDate dateEcheance = DateUtil.parseDate(scanner.nextLine());
        
        System.out.print("Date de paiement (dd/MM/yyyy): ");
        LocalDate datePaiement = DateUtil.parseDate(scanner.nextLine());
        
        System.out.print("Montant (€): ");
        double montant = Double.parseDouble(scanner.nextLine());
        
        System.out.println("Type de paiement:");
        Paiement.TypePaiement[] types = Paiement.TypePaiement.values();
        for (int i = 0; i < types.length; i++) {
            System.out.println((i + 1) + ". " + types[i]);
        }
        System.out.print("Choisir le type: ");
        int typeIndex = lireChoixUtilisateur() - 1;
        
        String id = paiementService.enregistrerPaiement(idAbonnement, dateEcheance, datePaiement, 
                                                       types[typeIndex], montant);
        System.out.println("Paiement enregistré avec succès. ID: " + id);
    }
    
    private void modifierPaiement() throws SQLException {
        System.out.print("ID du paiement à modifier: ");
        String id = scanner.nextLine();
        
        System.out.println("Fonctionnalité de modification en cours de développement.");
    }
    
    private void supprimerPaiement() throws SQLException {
        System.out.print("ID du paiement à supprimer: ");
        String id = scanner.nextLine();
        
        paiementService.deletePaiement(id);
        System.out.println("Paiement supprimé avec succès.");
    }
    
    private void afficherPaiementsAbonnement() throws SQLException {
        System.out.print("ID de l'abonnement: ");
        String id = scanner.nextLine();
        
        List<Paiement> paiements = paiementService.getPaiementsByAbonnement(id);
        
        if (paiements.isEmpty()) {
            System.out.println("Aucun paiement trouvé pour cet abonnement.");
        } else {
            System.out.println("\n=== PAIEMENTS DE L'ABONNEMENT ===");
            for (Paiement paiement : paiements) {
                System.out.println(paiement);
            }
        }
    }
    
    private void afficherPaiementsManques() throws SQLException {
        Map<String, Double> impayesMap = paiementService.getPaiementsImpayesAvecMontantTotal();
        
        if (impayesMap.isEmpty()) {
            System.out.println("Aucun paiement manqué.");
        } else {
            System.out.println("\n=== PAIEMENTS MANQUÉS (ABONNEMENTS AVEC ENGAGEMENT) ===");
            double totalImpaye = 0;
            for (Map.Entry<String, Double> entry : impayesMap.entrySet()) {
                System.out.printf("Abonnement %s: %.2f € impayés%n", entry.getKey(), entry.getValue());
                totalImpaye += entry.getValue();
            }
            System.out.printf("TOTAL IMPAYÉ: %.2f €%n", totalImpaye);
        }
    }
    
    private void afficherSommePayeeAbonnement() throws SQLException {
        System.out.print("ID de l'abonnement: ");
        String id = scanner.nextLine();
        
        double somme = paiementService.getSommePapeeAbonnement(id);
        System.out.printf("Somme payée pour l'abonnement %s: %.2f €%n", id, somme);
    }
    
    private void afficherDerniersPaiements() throws SQLException {
        List<Paiement> derniers = paiementService.getLast5Paiements();
        
        if (derniers.isEmpty()) {
            System.out.println("Aucun paiement trouvé.");
        } else {
            System.out.println("\n=== 5 DERNIERS PAIEMENTS ===");
            for (Paiement paiement : derniers) {
                System.out.println(paiement);
            }
        }
    }
    
    private void detecterPaiementsEnRetard() throws SQLException {
        List<Paiement> enRetard = paiementService.detecterPaiementsEnRetard();
        
        if (enRetard.isEmpty()) {
            System.out.println("Aucun paiement en retard détecté.");
        } else {
            System.out.println("\n=== PAIEMENTS EN RETARD DÉTECTÉS ===");
            for (Paiement paiement : enRetard) {
                System.out.println(paiement);
            }
            System.out.println("Statuts mis à jour automatiquement.");
        }
    }
    
    private int lireChoixUtilisateur() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}