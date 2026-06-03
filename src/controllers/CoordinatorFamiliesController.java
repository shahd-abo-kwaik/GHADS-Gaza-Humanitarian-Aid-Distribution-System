package controllers;

import java.time.LocalDate;

import dao.FamilyDAO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import models.Family;
import utils.AppStyleManager;
import utils.Session;

public class CoordinatorFamiliesController {

    @FXML private TextField householdNameField;
    @FXML private TextField phoneField;
    @FXML private TextField locationField;
    @FXML private TextField familySizeField;
    @FXML private TextField nationalIdField;

    @FXML private ComboBox<String> vulnerabilityComboBox;
    @FXML private DatePicker registrationDatePicker;

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

    @FXML
    public void initialize() {
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
                null
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
    private void handleReset() {
        householdNameField.clear();
        phoneField.clear();
        locationField.clear();
        familySizeField.clear();
        nationalIdField.clear();
        vulnerabilityComboBox.setValue(null);
        registrationDatePicker.setValue(null);
        familiesTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void loadFamilies() {
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
    
    @FXML
    private void handleShowMostVulnerable() {
        ObservableList<Family> list =
                FXCollections.observableArrayList(
                        familyDAO.getFamiliesSortedByVulnerability()
                );

        familiesTable.setItems(list);
    }
    @FXML
    private void handleShowNotServedFamilies() {
        ObservableList<Family> list =
                FXCollections.observableArrayList(
                        familyDAO.getNotServedFamilies()
                );

        familiesTable.setItems(list);
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
    private void goToDashboard() {
        openScreen("/views/CoordinatorDashboard.fxml", "GHADS - Coordinator Dashboard");
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
        Alert alert =
                new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}