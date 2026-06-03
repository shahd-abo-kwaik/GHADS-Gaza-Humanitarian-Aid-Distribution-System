
package controllers;

import java.time.LocalDate;

import dao.AidDistributionDAO;
import dao.FamilyDAO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import models.AidDistribution;
import models.Family;
import models.Organization;
import models.User;
import utils.AppStyleManager;
import utils.Session;

public class CoordinatorAidDistributionsController {

    @FXML private ComboBox<Family> familyComboBox;
    @FXML private ComboBox<Organization> organizationComboBox;
    @FXML private ComboBox<User> coordinatorComboBox;
    @FXML private ComboBox<String> aidTypeComboBox;
    @FXML private DatePicker distributionDatePicker;

    @FXML private TableView<AidDistribution> distributionsTable;

    @FXML private TableColumn<AidDistribution, Integer> idColumn;
    @FXML private TableColumn<AidDistribution, String> familyColumn;
    @FXML private TableColumn<AidDistribution, String> organizationColumn;
    @FXML private TableColumn<AidDistribution, String> coordinatorColumn;
    @FXML private TableColumn<AidDistribution, String> aidTypeColumn;
    @FXML private TableColumn<AidDistribution, LocalDate> dateColumn;

    private FamilyDAO familyDAO = new FamilyDAO();
    private AidDistributionDAO aidDistributionDAO = new AidDistributionDAO();

    @FXML
    public void initialize() {

        User currentUser = Session.getCurrentUser();

        familyComboBox.setItems(
                FXCollections.observableArrayList(familyDAO.getAllFamilies())
        );

        aidTypeComboBox.getItems().addAll(
                "Food Package",
                "Medical Aid",
                "Cash Assistance",
                "Clothes",
                "Blankets"
        );

        if (currentUser != null) {
            Organization organization = new Organization(
                    currentUser.getOrgId(),
                    currentUser.getOrganizationName(),
                    "",
                    ""
            );

            organizationComboBox.setItems(FXCollections.observableArrayList(organization));
            organizationComboBox.setValue(organization);
            organizationComboBox.setDisable(true);

            coordinatorComboBox.setItems(FXCollections.observableArrayList(currentUser));
            coordinatorComboBox.setValue(currentUser);
            coordinatorComboBox.setDisable(true);
        }

        idColumn.setCellValueFactory(new PropertyValueFactory<>("distributionId"));
        familyColumn.setCellValueFactory(new PropertyValueFactory<>("familyName"));
        organizationColumn.setCellValueFactory(new PropertyValueFactory<>("organizationName"));
        coordinatorColumn.setCellValueFactory(new PropertyValueFactory<>("coordinatorName"));
        aidTypeColumn.setCellValueFactory(new PropertyValueFactory<>("aidType"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("distributionDate"));

        loadDistributions();
    }

    @FXML
    private void handleAdd() {

        Family selectedFamily = familyComboBox.getValue();
        Organization selectedOrganization = organizationComboBox.getValue();
        User selectedCoordinator = coordinatorComboBox.getValue();
        String aidType = aidTypeComboBox.getValue();
        LocalDate date = distributionDatePicker.getValue();

        if (selectedFamily == null
                || selectedOrganization == null
                || selectedCoordinator == null
                || aidType == null
                || date == null) {

            showAlert("Validation Error", "All fields are required.");
            return;
        }

        if (!selectedFamily.getVulnerabilityLevel().trim().equalsIgnoreCase("HIGH")) {

            AidDistribution lastAid =
                    aidDistributionDAO.getLastAidWithin30Days(
                            selectedFamily.getFamilyId()
                    );

            if (lastAid != null) {
                showAlert(
                        "Duplicate Aid Rejected",
                        "This family has already received aid within the last 30 days.\n\n"
                        + "Family: " + selectedFamily.getHouseholdName() + "\n"
                        + "Vulnerability Level: " + selectedFamily.getVulnerabilityLevel() + "\n"
                        + "Organization: " + lastAid.getOrganizationName() + "\n"
                        + "Aid Date: " + lastAid.getDistributionDate()
                );
                return;
            }
        }

        AidDistribution distribution = new AidDistribution(
                selectedFamily.getFamilyId(),
                selectedOrganization.getOrgId(),
                selectedCoordinator.getUserId(),
                date,
                aidType
        );

        boolean result = aidDistributionDAO.addDistribution(distribution);
        if (result) {
            familyDAO.updateLastAidDate(
                    selectedFamily.getFamilyId(),
                    date
            );

            showAlert("Success", "Aid distribution recorded successfully.");
            loadDistributions();
            handleReset();
        }
    }
    @FXML
    private void loadDistributions() {

        User currentUser = Session.getCurrentUser();

        if (currentUser == null) {
            return;
        }

        distributionsTable.setItems(
                FXCollections.observableArrayList(
                        aidDistributionDAO.getDistributionsByOrganization(
                                currentUser.getOrgId()
                        )
                )
        );
    }

    @FXML
    private void handleReset() {
        familyComboBox.setValue(null);
        aidTypeComboBox.setValue(null);
        distributionDatePicker.setValue(null);
        distributionsTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleExit() {
        Platform.exit();
    }

    @FXML
    private void handleFontSize() {
        AppStyleManager.toggleFontSize(distributionsTable.getScene());
    }

    @FXML
    private void handleFontFamily() {
        AppStyleManager.toggleFontFamily(distributionsTable.getScene());
    }

    @FXML
    private void handleTheme() {
        AppStyleManager.toggleTheme(distributionsTable.getScene());
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
    private void goToProfile() {
        openScreen("/views/CoordinatorProfile.fxml", "GHADS - Coordinator Profile");
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