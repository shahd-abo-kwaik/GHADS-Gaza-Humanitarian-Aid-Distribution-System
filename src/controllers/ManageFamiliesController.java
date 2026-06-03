package controllers;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import utils.AppStyleManager;
import dao.FamilyDAO;
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
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Family;

public class ManageFamiliesController implements Initializable {

    @FXML private TextField householdNameField;
    @FXML private TextField phoneField;
    @FXML private TextField locationField;
    @FXML private TextField familySizeField;
    @FXML private TextField nationalIdField;

    @FXML private ComboBox<String> vulnerabilityComboBox;

    @FXML private DatePicker registrationDatePicker;
    @FXML private DatePicker lastAidDatePicker;

    @FXML private TableView<Family> familiesTable;

    @FXML private TableColumn<Family, Integer> idColumn;
    @FXML private TableColumn<Family, String> householdNameColumn;
    @FXML private TableColumn<Family, String> phoneColumn;
    @FXML private TableColumn<Family, String> locationColumn;
    @FXML private TableColumn<Family, Integer> familySizeColumn;
    @FXML private TableColumn<Family, String> nationalIdColumn;
    @FXML private TableColumn<Family, String> vulnerabilityColumn;
    @FXML private TableColumn<Family, LocalDate> registrationDateColumn;
    @FXML private TableColumn<Family, LocalDate> lastAidDateColumn;

    private FamilyDAO familyDAO = new FamilyDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        vulnerabilityComboBox.getItems().addAll("HIGH", "MEDIUM", "LOW");

        idColumn.setCellValueFactory(new PropertyValueFactory<>("familyId"));
        householdNameColumn.setCellValueFactory(new PropertyValueFactory<>("householdName"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        familySizeColumn.setCellValueFactory(new PropertyValueFactory<>("familySize"));
        nationalIdColumn.setCellValueFactory(new PropertyValueFactory<>("nationalId"));
        vulnerabilityColumn.setCellValueFactory(new PropertyValueFactory<>("vulnerabilityLevel"));
        registrationDateColumn.setCellValueFactory(new PropertyValueFactory<>("registrationDate"));
        lastAidDateColumn.setCellValueFactory(new PropertyValueFactory<>("lastAidDate"));

        loadFamilies();

        familiesTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, selectedFamily) -> {

                    if (selectedFamily != null) {
                        householdNameField.setText(selectedFamily.getHouseholdName());
                        phoneField.setText(selectedFamily.getPhone());
                        locationField.setText(selectedFamily.getLocation());
                        familySizeField.setText(String.valueOf(selectedFamily.getFamilySize()));
                        nationalIdField.setText(selectedFamily.getNationalId());
                        vulnerabilityComboBox.setValue(selectedFamily.getVulnerabilityLevel());
                        registrationDatePicker.setValue(selectedFamily.getRegistrationDate());
                        lastAidDatePicker.setValue(selectedFamily.getLastAidDate());
                    }
                }
        );
    }

    @FXML
    private void handleAdd() {

        if (!validateInputs()) {
            return;
        }

        if (familyDAO.nationalIdExists(nationalIdField.getText().trim())) {
            showAlert("Validation Error", "This national ID already exists.");
            return;
        }

        Family family = new Family(
                householdNameField.getText().trim(),
                phoneField.getText().trim(),
                locationField.getText().trim(),
                Integer.parseInt(familySizeField.getText().trim()),
                nationalIdField.getText().trim(),
                vulnerabilityComboBox.getValue(),
                registrationDatePicker.getValue(),
                lastAidDatePicker.getValue()
        );

        boolean result = familyDAO.addFamily(family);

        if (result) {
            showAlert("Success", "Family added successfully.");
            loadFamilies();
            handleReset();
        } else {
            showAlert("Error", "Failed to add family.");
        }
    }

    @FXML
    private void handleUpdate() {

        Family selectedFamily = familiesTable.getSelectionModel().getSelectedItem();

        if (selectedFamily == null) {
            showAlert("Validation Error", "Please select a family to update.");
            return;
        }

        if (!validateInputs()) {
            return;
        }

        selectedFamily.setHouseholdName(householdNameField.getText().trim());
        selectedFamily.setPhone(phoneField.getText().trim());
        selectedFamily.setLocation(locationField.getText().trim());
        selectedFamily.setFamilySize(Integer.parseInt(familySizeField.getText().trim()));
        selectedFamily.setNationalId(nationalIdField.getText().trim());
        selectedFamily.setVulnerabilityLevel(vulnerabilityComboBox.getValue());
        selectedFamily.setRegistrationDate(registrationDatePicker.getValue());
        selectedFamily.setLastAidDate(lastAidDatePicker.getValue());

        boolean result = familyDAO.updateFamily(selectedFamily);

        if (result) {
            showAlert("Success", "Family updated successfully.");
            loadFamilies();
            handleReset();
        } else {
            showAlert("Error", "Failed to update family.");
        }
    }

    @FXML
    private void handleDelete() {

        Family selectedFamily = familiesTable.getSelectionModel().getSelectedItem();

        if (selectedFamily == null) {
            showAlert("Validation Error", "Please select a family to delete.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Delete Family");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Are you sure you want to delete this family?");

        if (confirmAlert.showAndWait().get() == ButtonType.OK) {

            boolean result = familyDAO.deleteFamily(selectedFamily.getFamilyId());

            if (result) {
                showAlert("Success", "Family deleted successfully.");
                loadFamilies();
                handleReset();
            } else {
                showAlert("Error", "Failed to delete family.");
            }
        }
    }

    @FXML
    private void handleReset() {
        householdNameField.clear();
        phoneField.clear();
        locationField.clear();
        familySizeField.clear();
        nationalIdField.clear();
        vulnerabilityComboBox.setValue(null);
        registrationDatePicker.setValue(null);
        lastAidDatePicker.setValue(null);
        familiesTable.getSelectionModel().clearSelection();
    }

    @FXML
    public void loadFamilies() {
        ObservableList<Family> list =
                FXCollections.observableArrayList(
                        familyDAO.getAllFamilies()
                );

        familiesTable.setItems(list);
    }

    private boolean validateInputs() {

        if (householdNameField.getText().trim().isEmpty()
                || phoneField.getText().trim().isEmpty()
                || locationField.getText().trim().isEmpty()
                || familySizeField.getText().trim().isEmpty()
                || nationalIdField.getText().trim().isEmpty()
                || vulnerabilityComboBox.getValue() == null
                || registrationDatePicker.getValue() == null) {

            showAlert("Validation Error", "All required fields must be filled.");
            return false;
        }

        try {
            int size = Integer.parseInt(familySizeField.getText().trim());

            if (size <= 0) {
                showAlert("Validation Error", "Family size must be greater than 0.");
                return false;
            }

        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Family size must be a valid number.");
            return false;
        }
        if (!nationalIdField.getText().trim().matches("\\d{9}")) {
            showAlert("Validation Error", "National ID must be exactly 9 digits.");
            return false;
        }

        return true;
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
        AppStyleManager.toggleFontSize(familiesTable.getScene());
    }

    @FXML
    private void handleFontFamily() {
        AppStyleManager.toggleFontFamily(familiesTable.getScene());
    }

    @FXML
    private void handleTheme() {
        AppStyleManager.toggleTheme(familiesTable.getScene());
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
    private void goToOrganizations() {
        openScreen("/views/ManageOrganizations.fxml", "GHADS - Manage Organizations");
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
                    (javafx.stage.Stage) familiesTable.getScene().getWindow();

            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Could not open screen.");
        }
    }
    @FXML
    private void goToUsers() {
        openScreen("/views/ManageUsers.fxml", "GHADS - Manage Users");
    }
    @FXML
    private void goToAidDistributions() {
        try {
            javafx.fxml.FXMLLoader loader =
                    new javafx.fxml.FXMLLoader(getClass().getResource("/views/ManageAidDistributions.fxml"));

            javafx.scene.Scene scene =
                    new javafx.scene.Scene(loader.load());

            javafx.stage.Stage stage =
                    (javafx.stage.Stage) familiesTable.getScene().getWindow();

            stage.setTitle("GHADS - Manage Aid Distributions");
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Could not open Aid Distributions screen.");
        }
    }
}