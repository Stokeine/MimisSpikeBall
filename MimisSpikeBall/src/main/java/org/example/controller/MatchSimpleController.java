package org.example.controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.example.DAOs.Participation;
import org.example.models.Equipe;
import org.example.models.Joueur;
import javafx.scene.control.Button;
import org.example.models.Match;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MatchSimpleController {

    @FXML
    private AnchorPane MatchSimpleContainer;
    @FXML
    private Button retourMainMenu;
    @FXML
    private Label equipe1Label;
    @FXML
    private Label equipe2Label;
    @FXML
    private Button validerButton;
    @FXML
    private TextField scoreAField;
    @FXML
    private TextField scoreBField;
    @FXML
    private Label statusEquipe1;
    @FXML
    private Label statusEquipe2;
    private Equipe equipe1;
    private Equipe equipe2;
    private List<Joueur> joueurs = new ArrayList<>();
    private Match match;
    private Participation participation;

    @FXML
    public void initialize(){

        retourMainMenu.setOnAction(e -> {
            retourAccueil();
        });
        validerButton.setOnAction(e ->{
            validerResult();
        });
    }

    private void validerResult() {
        participation = new Participation();
        try{
            int s1 = Integer.parseInt(scoreAField.getText());
            int s2 = Integer.parseInt(scoreBField.getText());

            if (s1 < 0 || s2 < 0 || (s1 < 21 && s2 < 21)) {
                throw new IllegalArgumentException("Un score doit atteindre au moins 21 points.");
            }

            match.setScoreEquipe1(s1);
            match.setScoreEquipe2(s2);

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

            if (match.getScoreEquipe1() != -1 && match.getScoreEquipe2() != -1) {

                if (s1 > s2) {
                    statusEquipe1.setText("✅");
                    statusEquipe2.setText("❌");
                    statusEquipe1.setStyle("-fx-text-fill: green;");
                    statusEquipe2.setStyle("-fx-text-fill: red;");
                } else if (s2 > s1) {
                    statusEquipe1.setText("❌");
                    statusEquipe2.setText("✅");
                    statusEquipe1.setStyle("-fx-text-fill: red;");
                    statusEquipe2.setStyle("-fx-text-fill: green;");
                }
            }
        } catch (NumberFormatException ex){
            System.out.println("Veuillez entrer des scores valides.");
        }
    }

    public void setEquipes(Equipe e1, Equipe e2) {
        this.equipe1 = e1;
        this.equipe2 = e2;

        match = new Match();

        match.setEquipe1(e1);
        match.setEquipe2(e2);

        updateLabel();
        setJoueurs();
    }

    public void setJoueurs() {
        joueurs.addAll(equipe1.getJoueurs());
        joueurs.addAll(equipe2.getJoueurs());
    }

    private void retourAccueil() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Main.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) MatchSimpleContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateLabel(){
        equipe1Label.setText(equipe1.getNom());
        equipe2Label.setText(equipe2.getNom());
    }
}
