package com.example.gestionutilisateurs.controllers;


import com.example.gestionutilisateurs.entities.User;
import com.example.gestionutilisateurs.tools.MyConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.scene.control.Alert.AlertType;


public class UserController implements Initializable {
    Connection con =null;
    PreparedStatement st = null;
    ResultSet rs = null;

    @FXML
    private Button btnClear;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnUpdate;

    @FXML
    private TextField tFName;

    @FXML
    private TextField tLastName;

    @FXML
    private TextField tRole;
    @FXML
    private TextField tTel;

    @FXML
    private TextField tUsername;

    @FXML
    private TextField tImage;
    @FXML
    private ImageView pdp;



    @FXML
    private TableColumn<User, String> colRole;

    @FXML
    private TableColumn<User, String> colfName;

    @FXML
    private TableColumn<User, Integer> colid;

    @FXML
    private TableColumn<User, String> collName;

    @FXML
    private TableColumn<User, String> colEmail;


    @FXML
    private TableColumn<User, Integer> colTel;

    @FXML
    private TableColumn<User, String> colUsername;

    @FXML
    private TableColumn<User, String> colImage;

    @FXML
    private ImageView goBackBtn;

    @FXML
    private TableView<User> table;
    int id =0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        showUsers();

    }
    //observableList
    public ObservableList<User> getUsers(){
        ObservableList<User> users = FXCollections.observableArrayList();
        String query = "select * from user";
        con = MyConnection.getInstance().getCnx();
        try {
            st= con.prepareStatement(query);
            rs= st.executeQuery();
            while (rs.next()){
                User st =new User();
                st.setId(rs.getInt("id"));
                st.setFirstname(rs.getString("FirstName"));
                st.setLastname(rs.getString("Lastname"));
                st.setRole(rs.getString("Roles"));
                st.setUsername(rs.getString("Username"));
                st.setEmail(rs.getString("Email"));
                st.setTel(rs.getInt("Tel"));
                st.setImage(rs.getString("Image"));
                users.add(st);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;
    }
    public void showUsers(){
        ObservableList<User> list = getUsers();
        table.setItems(list);
        colid.setCellValueFactory(new PropertyValueFactory<User,Integer>("id"));
        colfName.setCellValueFactory(new PropertyValueFactory<User,String >("firstname"));
        collName.setCellValueFactory(new PropertyValueFactory<User,String >("lastname"));
        colRole.setCellValueFactory(new PropertyValueFactory<User,String >("role"));
        colUsername.setCellValueFactory(new PropertyValueFactory<User,String >("username"));
        colEmail.setCellValueFactory(new PropertyValueFactory<User,String >("email"));
        colTel.setCellValueFactory(new PropertyValueFactory<User,Integer>("tel"));
        colImage.setCellValueFactory(new PropertyValueFactory<User,String >("image"));

    }

    @FXML
    void clearField(ActionEvent event) {
        clear();

    }

    @FXML
    void creatUser(ActionEvent event) {
        String insert = "INSERT INTO user (FirstName,LastName,Roles,Username,Tel,Image) VALUES(?,?,?,?,?,?)";
        con=MyConnection.getInstance().getCnx();
        try {
            st = con.prepareStatement(insert);
            st.setString(1, tFName.getText());
            st.setString(2, tLastName.getText());
            st.setString(3, tRole.getText());
            st.setString(4, tUsername.getText());
            st.setInt(5, Integer.parseInt(tTel.getText()));
            st.setString(6, tImage.getText());

            st.executeUpdate();
            clear();
            showUsers();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }

    }
    @FXML
    void getData(MouseEvent event) {
        User user = table.getSelectionModel().getSelectedItem();
        id= user.getId();
        tFName.setText(user.getFirstname());
        tLastName.setText(user.getLastname());
        tRole.setText(user.getRole());
        tUsername.setText(user.getUsername());
        tTel.setText(String.valueOf(user.getTel()));
        tImage.setText(user.getImage());
        //
        File imageFile = new File(user.getImage());
        Image image = new Image(imageFile.toURI().toString());
        pdp.setImage(image);
        btnSave.setDisable(true);

    }
    void clear (){
        tLastName.setText(null);
        tFName.setText(null);
        tRole.setText(null);
        tImage.setText(null);
        tTel.setText(null);
        tUsername.setText(null);
        btnSave.setDisable(false);
    }

    @FXML
    void deleteUser(ActionEvent event) {
        User user = table.getSelectionModel().getSelectedItem();
        // Vérifier si un utilisateur est sélectionné
        if (user != null) {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirmation de suppression");
            confirmation.setHeaderText(null);
            confirmation.setContentText("Êtes-vous sûr de supprimer cet utilisateur : " + user.getUsername() + " ?");

            Optional<ButtonType> result = confirmation.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Supprimer l'utilisateur de la base de données
                String deleteQuery = "DELETE FROM user WHERE id = ?";
                con = MyConnection.getInstance().getCnx();
                try {
                    st = con.prepareStatement(deleteQuery);
                    st.setInt(1, user.getId());
                    st.executeUpdate();

                    // Actualiser la liste des utilisateurs affichée dans la table
                    showUsers();
                    // Effacer les champs de saisie après la suppression
                    clear();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            // Afficher un message d'erreur si aucun utilisateur n'est sélectionné
            System.out.println("Veuillez sélectionner un utilisateur à supprimer.");
        }
    }

    @FXML
    void updateUser(ActionEvent event) {
        String update = "UPDATE user SET FirstName=?, LastName=?, Roles=?, Username=?, Tel=?, Image=? WHERE id = ?";
        con = MyConnection.getInstance().getCnx();
        try {
            // Validation du numéro de téléphone
            String tel = tTel.getText();
            if (!tel.matches("\\d{8}")) { // Vérifie si le numéro de téléphone contient exactement 8 chiffres
                // Affiche un message d'erreur si le numéro de téléphone est invalide
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Erreur de saisie");
                alert.setHeaderText(null);
                alert.setContentText("Le numéro de téléphone doit contenir exactement 8 chiffres.");
                alert.showAndWait();
                return; // Arrête l'exécution de la méthode
            }

            // Validation du rôle
            String role = tRole.getText();
            if (!role.equals(" simple utilisateur") && !role.equals("Coach") && !role.equals("Nutritionniste")) {
                // Affiche un message d'erreur si le rôle est invalide
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Erreur de saisie");
                alert.setHeaderText(null);
                alert.setContentText("Le rôle doit être 'simple utilisateur', 'Coach' ou 'Nutritionniste'.");
                alert.showAndWait();
                return; // Arrête l'exécution de la méthode
            }

            // Préparation de la requête
            st = con.prepareStatement(update);
            st.setString(1, tFName.getText());
            st.setString(2, tLastName.getText());
            st.setString(3, role);
            st.setString(4, tUsername.getText());
            st.setString(5, tel);
            st.setString(6, tImage.getText());
            st.setInt(7, id);

            // Exécution de la requête
            st.executeUpdate();

            // Mise à jour de l'affichage et réinitialisation des champs
            showUsers();
            clear();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @FXML
    private void goBackHandler(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/MainCotainer.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }


}

