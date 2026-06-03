
package controllers;

import dao.AidDistributionDAO;
import dao.FamilyDAO;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import models.User;
import utils.AppStyleManager;
import utils.Session;

public class CoordinatorDashboardController {

    @FXML
    private Label totalFamiliesLabel;

    @FXML
    private Label servedByOrgLabel;

    @FXML
    private Label notServedLabel;

    @FXML
    private Label coordinatorNameLabel;

    @FXML
    private Label organizationNameLabel;

    @FXML
    private Label organizationInfoLabel;

    private FamilyDAO familyDAO = new FamilyDAO();
    private AidDistributionDAO aidDistributionDAO = new AidDistributionDAO();

    @FXML
    public void initialize() {

        User currentUser = Session.getCurrentUser();

        if (currentUser != null) {
            coordinatorNameLabel.setText(currentUser.getFullName());
            organizationNameLabel.setText(currentUser.getOrganizationName());

            organizationInfoLabel.setText(
                    "Organization: " + currentUser.getOrganizationName()
                    + "\nCoordinator: " + currentUser.getFullName()
                    + "\nEmail: " + currentUser.getEmail()
            );

            totalFamiliesLabel.setText(
                    String.valueOf(familyDAO.getFamiliesCount())
            );

            servedByOrgLabel.setText(
                    String.valueOf(
                            aidDistributionDAO.getServedFamiliesCountByOrganization(
                                    currentUser.getOrgId()
                            )
                    )
            );

            notServedLabel.setText(
                    String.valueOf(aidDistributionDAO.getNotServedFamiliesCount())
            );
        }
    }

    @FXML
    private void handleExit() {
        Platform.exit();
    }

    @FXML
    private void handleFontSize() {
        AppStyleManager.toggleFontSize(totalFamiliesLabel.getScene());
    }

    @FXML
    private void handleFontFamily() {
        AppStyleManager.toggleFontFamily(totalFamiliesLabel.getScene());
    }

    @FXML
    private void handleTheme() {
        AppStyleManager.toggleTheme(totalFamiliesLabel.getScene());
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
    private void handleLogout() {
        Session.clearSession();
        openScreen("/views/Login.fxml", "GHADS - Login");
    }

    @FXML
    private void goToFamilies() {
        openScreen("/views/CoordinatorFamilies.fxml", "GHADS - Coordinator Families");
    }
    @FXML
    private void goToAidDistributions() {
        openScreen(
                "/views/CoordinatorAidDistributions.fxml",
                "GHADS - Coordinator Aid Distributions"
        );
    }
    @FXML
    private void goToProfile() {
        openScreen("/views/CoordinatorProfile.fxml", "GHADS - Coordinator Profile");
    }

    @FXML
    private void goToChangePassword() {
        openScreen("/views/ChangePassword.fxml", "GHADS - Change Password");
    }

    private void openScreen(String fxmlPath, String title) {
        try {
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource(fxmlPath));

            Scene scene =
                    new Scene(loader.load());

            AppStyleManager.applyStyles(scene);

            Stage stage =
                    (Stage) totalFamiliesLabel.getScene().getWindow();

            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Could not open screen.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert =
                new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}