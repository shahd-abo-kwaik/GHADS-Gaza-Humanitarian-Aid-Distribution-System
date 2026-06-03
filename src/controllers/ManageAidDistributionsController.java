package controllers;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import utils.AppStyleManager;
import dao.AidDistributionDAO;
import dao.FamilyDAO;
import dao.OrganizationDAO;
import dao.UserDAO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import models.AidDistribution;
import models.Family;
import models.Organization;
import models.User;

public class ManageAidDistributionsController implements Initializable {

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

    private AidDistributionDAO aidDistributionDAO = new AidDistributionDAO();
    private FamilyDAO familyDAO = new FamilyDAO();
    private OrganizationDAO organizationDAO = new OrganizationDAO();
    private UserDAO userDAO = new UserDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        familyComboBox.setItems(
                FXCollections.observableArrayList(
                        familyDAO.getAllFamilies()
                )
        );

        organizationComboBox.setItems(
                FXCollections.observableArrayList(
                        organizationDAO.getAllOrganizations()
                )
        );

        coordinatorComboBox.setItems(
                FXCollections.observableArrayList(
                        userDAO.getAllUsers()
                )
        );

        aidTypeComboBox.getItems().addAll(
                "Food Package",
                "Medical Aid",
                "Cash Assistance",
                "Clothes",
                "Hygiene Kit",
                "Blankets",
                "Water Supply"
        );

        distributionDatePicker.setValue(LocalDate.now());

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

        if (familyComboBox.getValue() == null
                || organizationComboBox.getValue() == null
                || coordinatorComboBox.getValue() == null
                || aidTypeComboBox.getValue() == null
                || distributionDatePicker.getValue() == null) {

            showAlert("Validation Error", "All fields are required.");
            return;
        }

        Family selectedFamily = familyComboBox.getValue();
        Organization selectedOrganization = organizationComboBox.getValue();
        User selectedCoordinator = coordinatorComboBox.getValue();
        String selectedAidType = aidTypeComboBox.getValue();

        if (!selectedFamily.getVulnerabilityLevel().equals("HIGH")) {
        	boolean duplicate =
        	        aidDistributionDAO.hasReceivedAidWithin30Days(
        	                selectedFamily.getFamilyId()
        	        );
            if (duplicate) {
            	showAlert(
            	        "Duplicate Aid Rejected",
            	        "This family has already received aid within the last 30 days.\n\n"
            	        + "Family: " + selectedFamily.getHouseholdName() + "\n"
            	        + "Vulnerability Level: " + selectedFamily.getVulnerabilityLevel() + "\n"
            	        + "Aid Type: " + selectedAidType
            	);
                return;
            }
        }

        AidDistribution distribution = new AidDistribution(
                selectedFamily.getFamilyId(),
                selectedOrganization.getOrgId(),
                selectedCoordinator.getUserId(),
                distributionDatePicker.getValue(),
                selectedAidType
        );

        boolean result =
                aidDistributionDAO.addDistribution(distribution);

        if (result) {
            familyDAO.updateLastAidDate(
                    selectedFamily.getFamilyId(),
                    distributionDatePicker.getValue()
            );

            showAlert("Success", "Aid distribution recorded successfully.");
            loadDistributions();
            handleReset();
        }
    }

    @FXML
    private void handleDelete() {

        AidDistribution selectedDistribution =
                distributionsTable.getSelectionModel().getSelectedItem();

        if (selectedDistribution == null) {
            showAlert("Validation Error", "Please select a distribution to delete.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Delete Distribution");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Are you sure you want to delete this distribution?");

        if (confirmAlert.showAndWait().get() == ButtonType.OK) {

            boolean result =
                    aidDistributionDAO.deleteDistribution(
                            selectedDistribution.getDistributionId()
                    );

            if (result) {
                showAlert("Success", "Aid distribution deleted successfully.");
                loadDistributions();
                handleReset();
            } else {
                showAlert("Error", "Failed to delete distribution.");
            }
        }
    }

    @FXML
    private void handleReset() {
        familyComboBox.setValue(null);
        organizationComboBox.setValue(null);
        coordinatorComboBox.setValue(null);
        aidTypeComboBox.setValue(null);
        distributionDatePicker.setValue(LocalDate.now());
        distributionsTable.getSelectionModel().clearSelection();
    }

    @FXML
    public void loadDistributions() {

        ObservableList<AidDistribution> list =
                FXCollections.observableArrayList(
                        aidDistributionDAO.getAllDistributions()
                );

        distributionsTable.setItems(list);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(
                Alert.AlertType.INFORMATION,
                message,
                ButtonType.OK
        );

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
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
    private void handleLogout() {
        try {
            javafx.fxml.FXMLLoader loader =
                    new javafx.fxml.FXMLLoader(getClass().getResource("/views/Login.fxml"));

            javafx.scene.Scene scene =
                    new javafx.scene.Scene(loader.load());

            javafx.stage.Stage stage =
                    (javafx.stage.Stage) distributionsTable.getScene().getWindow();

            stage.setTitle("GHADS - Login");
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Could not logout.");
        }
    }
    @FXML
    private void goToChangePassword() {
        try {
            javafx.fxml.FXMLLoader loader =
                    new javafx.fxml.FXMLLoader(getClass().getResource("/views/ChangePassword.fxml"));

            javafx.scene.Scene scene =
                    new javafx.scene.Scene(loader.load());

            javafx.stage.Stage stage =
                    (javafx.stage.Stage) distributionsTable.getScene().getWindow();

            stage.setTitle("GHADS - Change Password");
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Could not open Change Password screen.");
        }
    }
    
    @FXML
    private void goToOrganizations() {
        openScreen("/views/ManageOrganizations.fxml", "GHADS - Manage Organizations");
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
    private void goToDashboard() {
        openScreen("/views/AdminDashboard.fxml", "GHADS - Admin Dashboard");
    }
    private void openScreen(String fxmlPath, String title) {
        try {
            javafx.fxml.FXMLLoader loader =
                    new javafx.fxml.FXMLLoader(getClass().getResource(fxmlPath));

            javafx.scene.Scene scene =
                    new javafx.scene.Scene(loader.load());

            javafx.stage.Stage stage =
                    (javafx.stage.Stage) distributionsTable.getScene().getWindow();

            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Could not open screen.");
        }
    }
    
    
    
    
    
    @FXML
    private void handleSearchByOrganization() {

        Organization selectedOrganization =
                organizationComboBox.getValue();

        if (selectedOrganization == null) {
            showAlert("Validation Error", "Please select an organization to search.");
            return;
        }

        distributionsTable.setItems(
                javafx.collections.FXCollections.observableArrayList(
                        aidDistributionDAO.getDistributionsByOrganization(
                                selectedOrganization.getOrgId()
                        )
                )
        );
    }
}