package org.example.models;

public class Equipe {

    private String nom;
    private Joueur joueur1;
    private Joueur joueur2;
    private int nbVictoire;
    private int nbPresqueVictoire;
    private int nbDefaite;
    private int nbPresqqueDefaite;
    private int nbPoint;

    public Equipe() {
    }

    public Equipe(Joueur joueur1, Joueur joueur2) {
        this.joueur1 = joueur1;
        this.joueur2 = joueur2;
        this.nom = joueur1.getNom() + " - " + joueur2.getNom();
        this.nbVictoire = 0;
        this.nbDefaite = 0;
        this.nbPresqqueDefaite = 0;
        this.nbPresqueVictoire = 0;
        this.nbPoint = 0;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Joueur getJoueur1() {
        return joueur1;
    }

    public void setJoueur1(Joueur joueur1) {
        this.joueur1 = joueur1;
    }

    public Joueur getJoueur2() {
        return joueur2;
    }

    public void setJoueur2(Joueur joueur2) {
        this.joueur2 = joueur2;
    }

    public int getNbVictoire() {
        return nbVictoire;
    }

    public void setNbVictoire(int nbVictoire) {
        this.nbVictoire = nbVictoire;
    }

    public int getNbPresqueVictoire() {
        return nbPresqueVictoire;
    }

    public void setNbPresqueVictoire(int nbPresqueVictoire) {
        this.nbPresqueVictoire = nbPresqueVictoire;
    }

    public int getNbDefaite() {
        return nbDefaite;
    }

    public void setNbDefaite(int nbDefaite) {
        this.nbDefaite = nbDefaite;
    }

    public int getNbPresqqueDefaite() {
        return nbPresqqueDefaite;
    }

    public void setNbPresqqueDefaite(int nbPresqqueDefaite) {
        this.nbPresqqueDefaite = nbPresqqueDefaite;
    }

    public int getNbPoint() {
        return nbPoint;
    }

    public void setNbPoint(int nbPoint) {
        this.nbPoint = nbPoint;
    }

    @Override
    public String toString() {
        return this.nom;
    }
}
