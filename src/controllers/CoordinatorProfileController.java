package controllers;

import java.io.File;

import dao.UserDAO;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.User;
import utils.AppStyleManager;
import utils.Session;

public class CoordinatorProfileController {

    @FXML private TextField fullNameField;
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private TextField roleField;
    @FXML private TextField organizationField;

    @FXML private ImageView profileImageView;

    private UserDAO userDAO = new UserDAO();

    private String selectedPhotoPath = "";

    @FXML
    public void initialize() {
        loadProfile();
    }

    @FXML
    private void loadProfile() {
        User currentUser = Session.getCurrentUser();

        if (currentUser != null) {
            fullNameField.setText(currentUser.getFullName());
            usernameField.setText(currentUser.getUsername());
            emailField.setText(currentUser.getEmail());
            roleField.setText(currentUser.getRole());
            organizationField.setText(currentUser.getOrganizationName());

            selectedPhotoPath = currentUser.getPhotoPath();

            if (selectedPhotoPath != null && !selectedPhotoPath.isEmpty()) {
                try {
                    profileImageView.setImage(
                            new Image(new File(selectedPhotoPath).toURI().toString())
                    );
                } catch (Exception e) {
                    profileImageView.setImage(null);
                }
            } else {
                profileImageView.setImage(null);
            }
        }
    }

    @FXML
    private void handleChoosePhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Profile Photo");

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Image Files",
                        "*.png", "*.jpg", "*.jpeg"
                )
        );

        File selectedFile =
                fileChooser.showOpenDialog(fullNameField.getScene().getWindow());

        if (selectedFile != null) {
            selectedPhotoPath = selectedFile.getAbsolutePath();

            profileImageView.setImage(
                    new Image(selectedFile.toURI().toString())
            );
        }
    }

    @FXML
    private void handleUpdateProfile() {
        User currentUser = Session.getCurrentUser();

        if (currentUser == null) {
            showAlert("Error", "No logged-in user found.");
            return;
        }

        if (fullNameField.getText().trim().isEmpty()
                || emailField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Full name and email are required.");
            return;
        }

        if (!emailField.getText().trim().contains("@")) {
            showAlert("Validation Error", "Please enter a valid email.");
            return;
        }

        currentUser.setFullName(fullNameField.getText().trim());
        currentUser.setEmail(emailField.getText().trim());
        currentUser.setPhotoPath(selectedPhotoPath);

        boolean result = userDAO.updateUser(currentUser);

        if (result) {
            Session.setCurrentUser(currentUser);
            showAlert("Success", "Profile updated successfully.");
        } else {
            showAlert("Error", "Failed to update profile.");
        }
    }

    @FXML
    private void handleExit() {
        Platform.exit();
    }

    @FXML
    private void handleFontSize() {
        AppStyleManager.toggleFontSize(fullNameField.getScene());
    }

    @FXML
    private void handleFontFamily() {
        AppStyleManager.toggleFontFamily(fullNameField.getScene());
    }

    @FXML
    private void handleTheme() {
        AppStyleManager.toggleTheme(fullNameField.getScene());
    }

    @FXML
    private void handleAboutApp() {
        showAlert(
                "About GHADS",
                "GHADS - Gaza Humanitarian Aid Distribution System\n\n"
                + "This application helps coordinate humanitarian aid distribution in Gaza.\n\n"
                + "Developer: Shahd Abo Kwaik"
        );
    }

    @FXML
    private void goToDashboard() {
        openScreen("/views/CoordinatorDashboard.fxml", "GHADS - Coordinator Dashboard");
    }

    @FXML
    private void goToFamilies() {
        openScreen("/views/CoordinatorFamilies.fxml", "GHADS - Coordinator Families");
    }

    @FXML
    private void goToAidDistributions() {
        openScreen("/views/CoordinatorAidDistributions.fxml", "GHADS - Coordinator Aid Distributions");
    }

    @FXML
    private void goToChangePassword() {
        openScreen("/views/ChangePassword.fxml", "GHADS - Change Password");
    }

    @FXML
    private void handleLogout() {
        Session.clearSession();
        openScreen("/views/Login.fxml", "GHADS - Login");
    }

    private void openScreen(String fxmlPath, String title) {
        try {
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource(fxmlPath));

            Scene scene =
                    new Scene(loader.load());

            AppStyleManager.applyStyles(scene);

            Stage stage =
                    (Stage) Stage.getWindows().filtered(window -> window.isShowing()).get(0);

            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Could not open screen.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}