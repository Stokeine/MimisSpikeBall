package org.example.DAOs;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Participation {

    public void enregistrerParticipation(int idJoueur, int idMatch) {
        File file = new File("data/participations.txt");
        file.getParentFile().mkdirs();

        // Vérifie si la participation existe déjà
        if (!participationExiste(idJoueur, idMatch, file)) {
            String ligne = String.format("%d;%d\n", idJoueur, idMatch);

            try (FileWriter fw = new FileWriter(file, true)) { // append = true
                fw.write(ligne);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean participationExiste(int idJoueur, int idMatch, File file) {
        if (!file.exists()) return false;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String ligne;
            while ((ligne = br.readLine()) != null) {
                String[] parts = ligne.split(";");
                if (parts.length == 2) {
                    int joueur = Integer.parseInt(parts[0]);
                    int match = Integer.parseInt(parts[1]);
                    if (joueur == idJoueur && match == idMatch) {
                        return true;
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }

        return false;
    }


    public static List<Integer> getMatchsPourJoueur(int idJoueur) {
        List<Integer> matchIds = new ArrayList<>();
        File fichier = new File("data/participations.txt");

        if (!fichier.exists()) return matchIds;

        try (BufferedReader br = new BufferedReader(new FileReader(fichier))) {
            String ligne;
            while ((ligne = br.readLine()) != null) {
                String[] parties = ligne.split(";");
                if (Integer.parseInt(parties[0]) == idJoueur) {
                    matchIds.add(Integer.parseInt(parties[1]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return matchIds;
    }
}
