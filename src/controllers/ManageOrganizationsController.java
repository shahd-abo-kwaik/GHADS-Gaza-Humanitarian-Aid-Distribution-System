package controllers;

import java.net.URL;
import java.util.ResourceBundle;
import utils.AppStyleManager;
import dao.OrganizationDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Organization;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
public class ManageOrganizationsController implements Initializable {

    @FXML
    private TextField nameField;

    @FXML
    private TextField typeField;

    @FXML
    private TextField contactInfoField;

    @FXML
    private TableView<Organization> organizationsTable;

    @FXML
    private TableColumn<Organization, Integer> idColumn;

    @FXML
    private TableColumn<Organization, String> nameColumn;

    @FXML
    private TableColumn<Organization, String> typeColumn;

    @FXML
    private TableColumn<Organization, String> contactColumn;

    private OrganizationDAO dao = new OrganizationDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        idColumn.setCellValueFactory(
                new PropertyValueFactory<>("orgId"));

        nameColumn.setCellValueFactory(
                new PropertyValueFactory<>("name"));

        typeColumn.setCellValueFactory(
                new PropertyValueFactory<>("type"));

        contactColumn.setCellValueFactory(
                new PropertyValueFactory<>("contactInfo"));

        loadOrganizations();
        
        organizationsTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, selectedOrganization) -> {

                    if (selectedOrganization != null) {
                        nameField.setText(selectedOrganization.getName());
                        typeField.setText(selectedOrganization.getType());
                        contactInfoField.setText(selectedOrganization.getContactInfo());
                    }
                }
        );
    }

    
    
    
    @FXML
    public void handleAdd() {

    	if (nameField.getText().trim().isEmpty()
    	        || typeField.getText().trim().isEmpty()
    	        || contactInfoField.getText().trim().isEmpty()) {
            showAlert("Validation Error",
                    "All fields are required.");
            return;
        }
        if (dao.organizationNameExists(nameField.getText().trim())) {
            showAlert("Validation Error", "This organization already exists.");
            return;
        }
        Organization organization = new Organization(
                nameField.getText().trim(),
                typeField.getText().trim(),
                contactInfoField.getText().trim());
        boolean result = dao.addOrganization(organization);

        if (result) {

            showAlert("Success",
                    "Organization added successfully.");

            loadOrganizations();
            handleReset();

        } else {

            showAlert("Error",
                    "Failed to add organization.");
        }
    }

    @FXML
    public void handleUpdate() {

        Organization selectedOrganization =
                organizationsTable.getSelectionModel().getSelectedItem();

        if (selectedOrganization == null) {
            showAlert("Validation Error", "Please select an organization to update.");
            return;
        }

        if (nameField.getText().isEmpty()
                || typeField.getText().isEmpty()
                || contactInfoField.getText().isEmpty()) {

            showAlert("Validation Error", "All fields are required.");
            return;
        }

        selectedOrganization.setName(nameField.getText().trim());
        selectedOrganization.setType(typeField.getText().trim());
        selectedOrganization.setContactInfo(contactInfoField.getText().trim());
        boolean result = dao.updateOrganization(selectedOrganization);

        if (result) {
            showAlert("Success", "Organization updated successfully.");
            loadOrganizations();
            handleReset();
        } else {
            showAlert("Error", "Failed to update organization.");
        }
    }

    @FXML
    public void handleDelete() {

        Organization selectedOrganization =
                organizationsTable.getSelectionModel().getSelectedItem();

        if (selectedOrganization == null) {

            showAlert("Validation Error",
                    "Please select an organization.");

            return;
        }

        Alert confirmAlert = new Alert(
                Alert.AlertType.CONFIRMATION);

        confirmAlert.setTitle("Delete Organization");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText(
                "Are you sure you want to delete this organization?");

        if (confirmAlert.showAndWait().get() == ButtonType.OK) {

            boolean result =
                    dao.deleteOrganization(
                            selectedOrganization.getOrgId());

            if (result) {

                showAlert("Success",
                        "Organization deleted successfully.");

                loadOrganizations();
                handleReset();

            } else {

                showAlert("Error",
                        "Failed to delete organization.");
            }
        }
    }

    @FXML
    public void handleReset() {

        nameField.clear();
        typeField.clear();
        contactInfoField.clear();
    }

    @FXML
    public void loadOrganizations() {

        ObservableList<Organization> list =
                FXCollections.observableArrayList(
                        dao.getAllOrganizations()
                );

        organizationsTable.setItems(list);
    }

    private void showAlert(String title, String message) {

        Alert alert = new Alert(
                Alert.AlertType.INFORMATION,
                message,
                ButtonType.OK);

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
        AppStyleManager.toggleFontSize(organizationsTable.getScene());
    }

    @FXML
    private void handleFontFamily() {
        AppStyleManager.toggleFontFamily(organizationsTable.getScene());
    }

    @FXML
    private void handleTheme() {
        AppStyleManager.toggleTheme(organizationsTable.getScene());
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
        openScreen("/views/Login.fxml", "GHADS - Login");
    }

    @FXML
    private void goToDashboard() {
        openScreen("/views/AdminDashboard.fxml", "GHADS - Admin Dashboard");
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
                    (Stage) organizationsTable.getScene().getWindow();

            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Could not open screen.");
        }
    }
    
}