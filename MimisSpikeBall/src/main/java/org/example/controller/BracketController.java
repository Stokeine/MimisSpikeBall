package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.DAOs.JoueurDAO;
import org.example.DAOs.Participation;
import org.example.models.Equipe;
import org.example.models.Joueur;
import org.example.models.Match;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class BracketController implements Initializable {


    @FXML
    private AnchorPane bracketContainer;
    private Participation participation;
    private List<Equipe> equipes = new ArrayList<Equipe>();

    private List<Match> matchs = new ArrayList<>();

    private List<VBox> matchBoxes = new ArrayList<>();
    private List<Joueur> joueurs = new ArrayList<>();
    private JoueurDAO joueurDAO = new JoueurDAO();

    public void setJoueurs(List<Joueur> joueurs) {
        this.joueurs = joueurs;
    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    private void generateDoubleEliminationBracket() {
        int numTeams = equipes.size();
        if (numTeams != 4) {
            throw new IllegalArgumentException("Ce format nécessite exactement 4 équipes.");
        }

        // Si les matchs sont déjà générés, on ne les régénère pas, on met juste à jour les équipes
        if (matchs.size() == 4) {
            Match match1 = matchs.get(0);
            Match match2 = matchs.get(1);

            // Mettre à jour les équipes de base si pas encore définies
            if (match1.getEquipe1() == null && match1.getEquipe2() == null) {
                Collections.shuffle(equipes); // Mélanger une seule fois
                match1.setEquipe1(equipes.get(0));
                match1.setEquipe2(equipes.get(1));
                match2.setEquipe1(equipes.get(2));
                match2.setEquipe2(equipes.get(3));
            }

            // Regénérer uniquement l'affichage avec les mêmes objets Match
        } else {
            // Initialisation des matchs (uniquement la première fois)

            Collections.shuffle(equipes);

            Runnable updateCallback = this::updateMatchBoxes;

            Match match1 = new Match(equipes.get(0), equipes.get(1), updateCallback);
            Match match2 = new Match(equipes.get(2), equipes.get(3), updateCallback);
            Match matchGagnants = new Match(null, null, updateCallback);
            Match matchPerdants = new Match(null, null, updateCallback);

            match1.setNextMatchGagnant(matchGagnants);
            match2.setNextMatchGagnant(matchGagnants);
            match1.setNextMatchPerdant(matchPerdants);
            match2.setNextMatchPerdant(matchPerdants);

            matchs.clear();
            matchs.addAll(List.of(match1, match2, matchGagnants, matchPerdants));
        }

        // UI
        Match match1 = matchs.get(0);
        Match match2 = matchs.get(1);
        Match matchGagnants = matchs.get(2);
        Match matchPerdants = matchs.get(3);

        GridPane winnerBracket = new GridPane();
        GridPane loserBracket = new GridPane();
        winnerBracket.setHgap(200);
        winnerBracket.setVgap(100);
        loserBracket.setHgap(200);
        loserBracket.setVgap(100);

        VBox matchBox1 = createMatchBox("W R1 - Match 1 - Terrain 1", match1, this::updateMatchBoxes);
        VBox matchBox2 = createMatchBox("W R1 - Match 2 - Terrain 2", match2, this::updateMatchBoxes);
        VBox matchBoxGagnants = createMatchBox("W R2 - Gagnants - Terrain 1", matchGagnants, null);
        VBox matchBoxPerdants = createMatchBox("L R1 - Perdants - Terrain 2", matchPerdants, null);

        matchBoxes.clear();
        matchBoxes.addAll(List.of(matchBox1, matchBox2, matchBoxGagnants, matchBoxPerdants));

        winnerBracket.add(matchBox1, 0, 0);
        winnerBracket.add(matchBox2, 0, 1);
        winnerBracket.add(matchBoxGagnants, 1, 0);
        loserBracket.add(matchBoxPerdants, 0, 0);

        VBox layout = new VBox(40);

        Button retourButton = new Button("Retour à l'accueil");
        retourButton.setOnAction(e -> retourAccueil());
        retourButton.setStyle("-fx-font-size: 16px; -fx-padding: 10 20;");
        retourButton.setMinHeight(40);
        retourButton.setMinWidth(200);

        HBox boutonContainer = new HBox(retourButton);
        boutonContainer.setAlignment(Pos.TOP_RIGHT); // ou Pos.TOP_RIGHT si tu préfères

        layout.getChildren().addAll(
                new Label("Winner Bracket"),
                winnerBracket,
                new Label("Loser Bracket"),
                loserBracket,
                boutonContainer
        );

        bracketContainer.getChildren().setAll(layout);
        AnchorPane.setTopAnchor(layout, 10.0);
        AnchorPane.setLeftAnchor(layout, 10.0);
    }

    private void retourAccueil() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Main.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) bracketContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private VBox createMatchBox(String labelText, Match match, Runnable onValidated) {
        VBox box = new VBox(10);
        participation = new Participation();
        Label title = new Label(labelText);

        // Création des labels et champs
        Label equipe1Label = new Label(match.getEquipe1() != null ? match.getEquipe1().getNom() : "Équipe A");
        TextField score1 = new TextField();
        score1.setPromptText("Score");
        score1.setPrefWidth(50);
        Label status1 = new Label(); // ✅ ou ❌

        HBox equipe1Box = new HBox(5, equipe1Label, score1, status1);
        equipe1Box.setSpacing(10);

        Label equipe2Label = new Label(match.getEquipe2() != null ? match.getEquipe2().getNom() : "Équipe B");
        TextField score2 = new TextField();
        score2.setPromptText("Score");
        score2.setPrefWidth(50);
        Label status2 = new Label(); // ✅ ou ❌

        HBox equipe2Box = new HBox(5, equipe2Label, score2, status2);
        equipe2Box.setSpacing(10);

        Button validerButton = new Button("Valider les scores");
        validerButton.setOnAction(e -> {
            try {
                int s1 = Integer.parseInt(score1.getText());
                int s2 = Integer.parseInt(score2.getText());

                if (s1 < 0 || s2 < 0 || (s1 < 21 && s2 < 21)) {
                    throw new IllegalArgumentException("Un score doit atteindre au moins 21 points.");
                }

                match.setScoreEquipe1(s1);
                match.setScoreEquipe2(s2);

                Equipe gagnant = (s1 > s2) ? match.getEquipe1() : match.getEquipe2();
                Equipe perdant = (s1 > s2) ? match.getEquipe2() : match.getEquipe1();

                match.sauvegarderDansFichier();


                int matchId = match.getId();

                for (Joueur joueur : match.getEquipe1().getJoueurs()) {
                    joueur.ajouterMatchId(matchId);
                    participation.enregistrerParticipation(joueur.getId(), match.getId());
                }

                for (Joueur joueur : match.getEquipe2().getJoueurs()) {
                    joueur.ajouterMatchId(matchId);
                    participation.enregistrerParticipation(joueur.getId(), match.getId());
                }

                // Affichage des statuts visuels
                if (match.getScoreEquipe1() != -1 && match.getScoreEquipe2() != -1) {

                    if (s1 > s2) {
                        status1.setText("✅");
                        status2.setText("❌");
                        status1.setStyle("-fx-text-fill: green;");
                        status2.setStyle("-fx-text-fill: red;");
                    } else if (s2 > s1) {
                        status1.setText("❌");
                        status2.setText("✅");
                        status1.setStyle("-fx-text-fill: red;");
                        status2.setStyle("-fx-text-fill: green;");
                    }
                }

                // Mise à jour des prochains matchs
                if (match.getNextMatchGagnant() != null) {
                    match.getNextMatchGagnant().setEquipe(gagnant);
                }

                if (match.getNextMatchPerdant() != null) {
                    match.getNextMatchPerdant().setEquipe(perdant);
                }

                if (onValidated != null) {
                    onValidated.run();
                }

            } catch (NumberFormatException ex) {
                System.out.println("Veuillez entrer des scores valides.");
            }
        });

        box.getChildren().addAll(title, equipe1Box, equipe2Box, validerButton);
        box.setStyle("-fx-border-color: black;");
        box.setPadding(new Insets(10));
        box.setPrefSize(250, 150);
        return box;
    }

    private void updateMatchBoxes() {
        for (int i = 0; i < matchs.size(); i++) {
            Match match = matchs.get(i);
            VBox box = matchBoxes.get(i);

            HBox equipe1Box = (HBox) box.getChildren().get(1);
            Label equipe1Label = (Label) equipe1Box.getChildren().get(0);
            Label status1 = (Label) equipe1Box.getChildren().get(2);

            HBox equipe2Box = (HBox) box.getChildren().get(2);
            Label equipe2Label = (Label) equipe2Box.getChildren().get(0);
            Label status2 = (Label) equipe2Box.getChildren().get(2);

            // Mise à jour des noms
            equipe1Label.setText(match.getEquipe1() != null ? match.getEquipe1().getNom() : "Équipe A");
            equipe2Label.setText(match.getEquipe2() != null ? match.getEquipe2().getNom() : "Équipe B");

            // Mise à jour des statuts visuels SEULEMENT si les deux équipes sont définies ET les scores valides
            if (match.getEquipe1() != null && match.getEquipe2() != null &&
                    match.getScoreEquipe1() >= 0 && match.getScoreEquipe2() >= 0 &&
                    (match.getScoreEquipe1() >= 21 || match.getScoreEquipe2() >= 21)) {

                if (match.getScoreEquipe1() > match.getScoreEquipe2()) {
                    status1.setText("✅");
                    status1.setStyle("-fx-text-fill: green;");
                    status2.setText("❌");
                    status2.setStyle("-fx-text-fill: red;");
                } else if (match.getScoreEquipe2() > match.getScoreEquipe1()) {
                    status1.setText("❌");
                    status1.setStyle("-fx-text-fill: red;");
                    status2.setText("✅");
                    status2.setStyle("-fx-text-fill: green;");
                } else {
                    status1.setText("");
                    status2.setText("");
                }

            } else {
                // Pas encore de scores valides ou d'équipes
                status1.setText("");
                status2.setText("");
            }
        }
    }

    public void setEquipes(List<Equipe> equipes){
        this.equipes = equipes;
        if (bracketContainer != null) {
            generateDoubleEliminationBracket();
        } else {
            System.err.println("bracketContainer est null !");
        }
    }
}
