package org.example.models;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Joueur {

    private int id;
    private String nom;
    private Status status;
    private static List<Integer> matchIds = new ArrayList<>();
    public enum Status{
        OUI, PAS_OBLIGER, NON
    }
    public Joueur() {
    }

    public Joueur(String nom) {
        this.nom = nom;
        this.status = Status.PAS_OBLIGER;
        this.id = getLigneNom();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public static List<Integer> getMatchIds() {
        return matchIds;
    }

    public static void setMatchIds(List<Integer> matchIds) {
        Joueur.matchIds = matchIds;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status newStatus) {
        this.status = newStatus;
    }


    public void ajouterMatchId(int id) {
        if (!matchIds.contains(id)) {
            matchIds.add(id);
        }
    }

    @Override
    public String toString() {
        return this.nom;
    }

    public int getLigneNom() {
        File fichier = new File("data/joueurs.txt");
        int ligneNum = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(fichier))) {
            String ligne;
            while ((ligne = br.readLine()) != null) {
                ligneNum++;
                if (ligne.trim().equalsIgnoreCase(this.nom.trim())) {
                    return ligneNum;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Si le nom n'a pas été trouvé
        return -1;
    }
}
