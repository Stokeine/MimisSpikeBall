package org.example.DAOs;

import org.example.models.Joueur;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class JoueurDAO {

    private static final String FILENAME = "data/joueurs.txt";

    public void save(Joueur joueur) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILENAME, true))) {
            writer.write(joueur.getNom());
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Erreur lors de la sauvegarde : " + e.getMessage());
        }
    }

    public List<Joueur> load() {
        List<Joueur> joueurs = new ArrayList<>();
        File file = new File(FILENAME);
        if (!file.exists()) return joueurs;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Joueur j = new Joueur(line);
                if (j != null) joueurs.add(j);
            }
        } catch (IOException e) {
            System.out.println("Erreur lors du chargement : " + e.getMessage());
        }

        return joueurs;
    }
}
