package org.example.models;

import java.util.List;

public class Joueur {

    private String nom;
    private Status status;
    private static List<Integer> matchesId;
    public enum Status{
        OUI, PAS_OBLIGER, NON
    };
    public Joueur() {
    }

    public Joueur(String nom) {
        this.nom = nom;
        this.status = Status.PAS_OBLIGER;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public static List<Integer> getMatchesId() {
        return matchesId;
    }

    public static void setMatchesId(List<Integer> matchesId) {
        Joueur.matchesId = matchesId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status newStatus) {
        this.status = newStatus;
    }

    @Override
    public String toString() {
        return this.nom;
    }
}
