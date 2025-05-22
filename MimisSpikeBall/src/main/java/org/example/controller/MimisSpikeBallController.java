package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import org.example.DAOs.JoueurDAO;
import org.example.models.Equipe;
import org.example.models.Joueur;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.layout.Priority;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MimisSpikeBallController {

    @FXML
    private TextField nomNewPlayer;
    @FXML
    private Button addPlayerOK;
    @FXML
    private ListView<Joueur> listeAllJoueurs;
    @FXML
    private ListView<Joueur> listeAllJoueursQuiJouent;
    @FXML
    private ComboBox<String> typeTournois;
    @FXML
    private Button btnCreerEquipe;
    @FXML
    private Button genererTournoi;
    @FXML
    private ListView<Equipe> listeEquipe;
    @FXML
    private Spinner<Integer> nbTerrain;
    @FXML
    private Text benchTextArea;
    @FXML
    private AnchorPane mainContent;

    private ObservableList<Joueur> joueurs = FXCollections.observableArrayList();
    private ObservableList<Joueur> joueursQuiJouent = FXCollections.observableArrayList();
    private ObservableList<Equipe> equipes = FXCollections.observableArrayList();


    private final JoueurDAO joueurDAO = new JoueurDAO();

    @FXML
    public void initialize() {
        listeAllJoueurs.setItems(joueurs);
        listeAllJoueursQuiJouent.setItems(joueursQuiJouent);
        listeEquipe.setItems(equipes);
        typeTournois.getItems().addAll("League", "Bracket");
        nbTerrain.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1));
        nbTerrain.setEditable(true);
        updatePlayerList();

        typeTournois.getSelectionModel().selectFirst();

        genererTournoi.setOnAction(e -> {
            String selection = typeTournois.getValue();
            try {
                loadTournamentView(selection);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        // Cell factory pour ajouter un bouton "+" dans listeAllJoueurs
        listeAllJoueurs.setCellFactory(lv -> new ListCell<Joueur>() {
            private final Button addButton = new Button("+");
            private final Label nameLabel = new Label();
            private final Region spacer = new Region();
            private final HBox hbox = new HBox(nameLabel, spacer, addButton);

            {
                hbox.setSpacing(10);
                HBox.setHgrow(spacer, Priority.ALWAYS);
                addButton.setPrefWidth(15);
                addButton.setPrefHeight(15);
                addButton.setOnAction(event -> {
                    Joueur joueur = getItem();
                    if (joueur != null) {
                        if (!joueursQuiJouent.contains(joueur)) {
                            joueursQuiJouent.add(joueur);
                            joueurs.remove(joueur);
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Joueur joueur, boolean empty) {
                super.updateItem(joueur, empty);
                if (empty || joueur == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    nameLabel.setText(joueur.getNom());
                    setGraphic(hbox);
                }
            }
        });

        listeAllJoueursQuiJouent.setCellFactory(lv -> CliqueDroitSurJoueurQuiJouent());

        addPlayerOK.setOnAction(event -> ajouterJoueur());

        btnCreerEquipe.setOnAction(event -> {
            if (joueursQuiJouent.size() >= 4) {
                CreerEquipe();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Conditions non remplies");
                alert.setHeaderText(null);
                alert.setContentText("Il faut au moins 4 joueurs");
                alert.showAndWait();
            }
        });
    }

    private void loadTournamentView(String type) throws IOException {
        String fxmlFile;
        if ("League".equals(type)) {
            fxmlFile = "/view/League.fxml";
        } else if ("Bracket".equals(type)) {
            fxmlFile = "/view/Bracket.fxml";
        } else {
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
        Parent view = loader.load();

        mainContent.getChildren().clear();
        mainContent.getChildren().add(view);

        AnchorPane.setTopAnchor(view, 0.0);
        AnchorPane.setBottomAnchor(view, 0.0);
        AnchorPane.setLeftAnchor(view, 0.0);
        AnchorPane.setRightAnchor(view, 0.0);
    }
    private void CreerEquipe(){
        List<Joueur> bench = new ArrayList<>();
        List<Joueur> onForSure = new ArrayList<>();
        List<Joueur> maybeOn = new ArrayList<>();

        List<Joueur> onNext = new ArrayList<>();

        for (Joueur joueur : joueursQuiJouent){
            if (joueur.getStatus() == Joueur.Status.NON){
                bench.add(joueur);
            }
            if (joueur.getStatus() == Joueur.Status.PAS_OBLIGER){
                maybeOn.add(joueur);
            }
            if (joueur.getStatus() == Joueur.Status.OUI){
                onForSure.add(joueur);
                onNext.add(joueur);
            }
        }

        Collections.shuffle(onNext);
        Collections.shuffle(maybeOn);
        Collections.shuffle(onForSure);
        Collections.shuffle(bench);

        if (nbTerrain.getValue() == 1) {
            AddJoueurs(2, bench, maybeOn, onNext);
        }

        else if (nbTerrain.getValue() == 2) {
            AddJoueurs(4, bench, maybeOn, onNext);
        }
        else if (nbTerrain.getValue() == 3){
            AddJoueurs(6, bench, maybeOn, onNext);
        }
    }

    private void AddJoueurs(int nbEquipe, List<Joueur> bench, List<Joueur> maybeOn, List<Joueur> onNext){
        int joueurNecessaires = nbEquipe * 2;

        if (onNext.size() < joueurNecessaires){
            int joueursManquants = joueurNecessaires - onNext.size();

            while (joueursManquants > 0 && !maybeOn.isEmpty()) {
                Joueur j = maybeOn.remove(0);
                onNext.add(j);
                joueursManquants--;
            }

            while (joueursManquants > 0 && !bench.isEmpty()) {
                Joueur j = bench.remove(0);
                onNext.add(j);
                joueursManquants--;
            }
        }
        creerEquipes(onNext, nbEquipe);

        List<String> joueursRestants = new ArrayList<>();

        for (Joueur j : maybeOn) {
            joueursRestants.add(j.getNom());
        }
        for (Joueur j : bench) {
            joueursRestants.add(j.getNom());
        }

        String texte = "bench: " + String.join(" | ", joueursRestants);

        benchTextArea.setText(texte);
    }

    private void creerEquipes(List<Joueur> joueurs, int nbEquipes) {
        equipes.clear();
        Collections.shuffle(joueurs);

        for(Joueur joueur : joueurs){
            System.out.println(joueur.getNom());
        }

        for (int i = 0; i < nbEquipes; i++) {
            int index = i * 2;
            if (index + 1 < joueurs.size()) {
                Joueur j1 = joueurs.get(index);
                Joueur j2 = joueurs.get(index + 1);
                Equipe equipe = new Equipe(j1, j2);
                equipes.add(equipe);
                System.out.println("Équipe " + (i+1) + " : " + equipe.getNom());
            } else {
                System.out.println("Pas assez de joueurs pour créer l'équipe " + (i+1));
            }
        }
    }

    private ListCell<Joueur> CliqueDroitSurJoueurQuiJouent() {
        return new ListCell<>() {
            private final Button removeButton = new Button("-");
            private final Label nameLabel = new Label();
            private final Region spacer = new Region();
            private final HBox hbox = new HBox(nameLabel, spacer, removeButton);

            {
                hbox.setSpacing(10);
                HBox.setHgrow(spacer, Priority.ALWAYS);

                removeButton.setPrefWidth(25);
                removeButton.setPrefHeight(25);
                removeButton.setOnAction(event -> {
                    Joueur joueur = getItem();
                    if (joueur != null) {
                        joueursQuiJouent.remove(joueur);
                        joueurs.add(joueur);
                    }
                });

                ContextMenu contextMenu = new ContextMenu();
                for (Joueur.Status s : Joueur.Status.values()) {
                    MenuItem item = new MenuItem("" + s);
                    item.setOnAction(e -> {
                        Joueur joueur = getItem();
                        if (joueur != null) {
                            joueur.setStatus(s);
                            nameLabel.setText(joueur.toString());
                            updateColor(joueur);
                            listeAllJoueursQuiJouent.refresh();
                        }
                    });
                    contextMenu.getItems().add(item);
                }
                setContextMenu(contextMenu);
            }

            private void updateColor(Joueur joueur) {
                if (joueur != null) {
                    switch (joueur.getStatus()) {
                        case OUI -> setStyle("-fx-background-color: lightgreen;");
                        case PAS_OBLIGER -> setStyle("-fx-background-color: khaki;");
                        case NON -> setStyle("-fx-background-color: lightcoral;");
                        default -> setStyle(""); // reset
                    }
                }
            }

            @Override
            protected void updateItem(Joueur joueur, boolean empty) {
                super.updateItem(joueur, empty);
                if (empty || joueur == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                } else {
                    nameLabel.setText(joueur.toString()); // toString pour afficher le nom + statut
                    setGraphic(hbox);
                    updateColor(joueur);
                }
            }
        };
    }

    private void updatePlayerList() {
        List<Joueur> loaded = joueurDAO.load();
        joueurs.setAll(loaded);
    }

    private void ajouterJoueur() {
        String nom = nomNewPlayer.getText().trim();
        if (!nom.isEmpty()) {
            Joueur joueur = new Joueur(nom);
            joueurDAO.save(joueur);
            nomNewPlayer.clear();
            System.out.println("Joueur ajouté : " + nom);
        } else {
            System.out.println("Le champ de nom est vide.");
        }
        updatePlayerList();
    }
}
