
package controllers;
import utils.AppStyleManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import dao.OrganizationDAO;
import dao.UserDAO;
import dao.FamilyDAO;
import dao.AidDistributionDAO;
public class AdminDashboardController {

    @FXML
    private Label totalOrganizationsLabel;

    @FXML
    private Label totalUsersLabel;

    @FXML
    private Label totalFamiliesLabel;

    @FXML
    private Label familiesServedLabel;

    @FXML
    private Label familiesNotServedLabel;

    private OrganizationDAO organizationDAO = new OrganizationDAO();
    private UserDAO userDAO = new UserDAO();
    private FamilyDAO familyDAO = new FamilyDAO();
    private AidDistributionDAO aidDistributionDAO = new AidDistributionDAO();
    @FXML
    public void initialize() {

        totalOrganizationsLabel.setText(
                String.valueOf(organizationDAO.getOrganizationsCount())
        );

        totalUsersLabel.setText(
                String.valueOf(userDAO.getCoordinatorsCount())
        );

        totalFamiliesLabel.setText(
                String.valueOf(familyDAO.getFamiliesCount())
        );

        familiesServedLabel.setText(
                String.valueOf(aidDistributionDAO.getServedFamiliesCount())
        );

        familiesNotServedLabel.setText(
                String.valueOf(aidDistributionDAO.getNotServedFamiliesCount())
        );
    }

    @FXML
    private void handleExit() {
        Platform.exit();
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource("/views/Login.fxml"));

            Scene scene = new Scene(loader.load());

            Stage stage =
                    (Stage) totalOrganizationsLabel.getScene().getWindow();

            stage.setTitle("GHADS - Login");
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showInfo("Error", "Could not logout.");
        }
    }
    @FXML
    private void handleFontSize() {
        AppStyleManager.toggleFontSize(totalOrganizationsLabel.getScene());
    }

    @FXML
    private void handleFontFamily() {
        AppStyleManager.toggleFontFamily(totalOrganizationsLabel.getScene());
    }

    @FXML
    private void handleTheme() {
        AppStyleManager.toggleTheme(totalOrganizationsLabel.getScene());
    }
        @FXML
  private void handleAboutApp() {
      showInfo(
              "About GHADS",
              "GHADS - Gaza Humanitarian Aid Distribution System\n\n"
              + "This application helps coordinate humanitarian aid distribution in Gaza.\n\n"
              + "Developer: Shahd Abo Kwaik"
      );
    } private void showInfo(String title, String message) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }
    
    
    @FXML
    private void goToOrganizations() {

        try {

            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource("/views/ManageOrganizations.fxml"));

            Scene scene = new Scene(loader.load());

            Stage stage =
                    (Stage) totalOrganizationsLabel.getScene().getWindow();

            stage.setTitle("GHADS - Manage Organizations");
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void goToUsers() {
        openScreen("/views/ManageUsers.fxml", "GHADS - Manage Users");
    }

    @FXML
    private void goToFamilies() {
        openScreen("/views/ManageFamilies.fxml", "GHADS - Manage Families");
    }

    @FXML
    private void goToAidDistributions() {
        openScreen("/views/ManageAidDistributions.fxml", "GHADS - Manage Aid Distributions");
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

            Stage stage =
                    (Stage) totalOrganizationsLabel.getScene().getWindow();

            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showInfo("Error", "Could not open screen.");
        }
    }
    
    
}