package org.example.models;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Match {

    private int id;
    private Equipe equipe1;
    private Equipe equipe2;
    private int scoreEquipe1;
    private int scoreEquipe2;
    private Match nextMatchGagnant;
    private Match nextMatchPerdant;
    private Runnable updateCallback;

    public Match() {
    }

    public Match(Equipe e1, Equipe e2, Runnable updateCallback) {
        this.equipe1 = e1;
        this.equipe2 = e2;
        this.updateCallback = updateCallback;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Equipe getEquipe1() {
        return equipe1;
    }

    public void setEquipe1(Equipe equipe1) {
        this.equipe1 = equipe1;
    }

    public Equipe getEquipe2() {
        return equipe2;
    }

    public void setEquipe2(Equipe equipe2) {
        this.equipe2 = equipe2;
    }

    public int getScoreEquipe1() {
        return scoreEquipe1;
    }

    public void setScoreEquipe1(int scoreEquipe1) {
        this.scoreEquipe1 = scoreEquipe1;
    }

    public int getScoreEquipe2() {
        return scoreEquipe2;
    }

    public void setScoreEquipe2(int scoreEquipe2) {
        this.scoreEquipe2 = scoreEquipe2;
    }

    public void setNextMatchGagnant(Match match) {
        this.nextMatchGagnant = match;
    }

    public void setNextMatchPerdant(Match match) {
        this.nextMatchPerdant = match;
    }

    public Match getNextMatchGagnant() {
        return nextMatchGagnant;
    }

    public Match getNextMatchPerdant() {
        return nextMatchPerdant;
    }
    public void setEquipe(Equipe equipe) {
        if (equipe1 == null) {
            equipe1 = equipe;
        } else if (equipe2 == null) {
            equipe2 = equipe;
        } else {
            System.out.println("Les deux équipes sont déjà définies pour ce match.");
        }
    }

    public void sauvegarderDansFichier() {
        if (equipe1 == null || equipe2 == null) return;

        if (id == 0) {
            this.id = getDernierIdMatch() + 1;
        }

        String ligne = String.format("%d;%s;%d;%s;%d\n",
                id,
                equipe1.getNom(), scoreEquipe1,
                equipe2.getNom(), scoreEquipe2
        );

        try {
            File file = new File("data/matchs.txt");
            file.getParentFile().mkdirs(); // Crée le dossier s’il n’existe pas
            FileWriter fw = new FileWriter(file, true); // Append = true
            fw.write(ligne);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getDernierIdMatch() {
        File fichier = new File("data/matchs.txt");
        int dernierId = 0;

        if (!fichier.exists()) return 0;

        try (BufferedReader br = new BufferedReader(new FileReader(fichier))) {
            String ligne;
            while ((ligne = br.readLine()) != null) {
                if (!ligne.isBlank()) {
                    String[] parties = ligne.split(";");
                    int id = Integer.parseInt(parties[0]);
                    if (id > dernierId) {
                        dernierId = id;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return dernierId;
    }
}
