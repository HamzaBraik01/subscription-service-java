package com.subscription.manager;

import com.subscription.manager.ui.ConsoleMenu;


public class Main {
    
    public static void main(String[] args) {
        try {
            ConsoleMenu menu = new ConsoleMenu();
            menu.start();
            
        } catch (Exception e) {
            System.err.println("Erreur fatale lors du démarrage de l'application: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}