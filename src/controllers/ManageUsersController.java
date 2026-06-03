package controllers;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import utils.AppStyleManager;
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
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import models.Organization;
import models.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ManageUsersController implements Initializable {

    @FXML private TextField fullNameField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField emailField;

    @FXML private ComboBox<String> roleComboBox;
    @FXML private ComboBox<Organization> organizationComboBox;

    @FXML private ImageView photoImageView;
    @FXML private Label photoPathLabel;

    @FXML private TableView<User> usersTable;

    @FXML private TableColumn<User, Integer> idColumn;
    @FXML private TableColumn<User, String> fullNameColumn;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, String> roleColumn;
    @FXML private TableColumn<User, String> organizationColumn;
    @FXML private TableColumn<User, String> photoColumn;

    private UserDAO userDAO = new UserDAO();
    private OrganizationDAO organizationDAO = new OrganizationDAO();

    private String selectedPhotoPath = "";

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        roleComboBox.getItems().addAll("ADMIN", "COORDINATOR");

        organizationComboBox.setItems(
                FXCollections.observableArrayList(
                        organizationDAO.getAllOrganizations()
                )
        );

        idColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        organizationColumn.setCellValueFactory(new PropertyValueFactory<>("organizationName"));
        photoColumn.setCellValueFactory(new PropertyValueFactory<>("photoPath"));

        loadUsers();

        usersTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, selectedUser) -> {

                    if (selectedUser != null) {
                        fullNameField.setText(selectedUser.getFullName());
                        usernameField.setText(selectedUser.getUsername());
                        passwordField.setText(selectedUser.getPassword());
                        emailField.setText(selectedUser.getEmail());
                        roleComboBox.setValue(selectedUser.getRole());

                        selectedPhotoPath = selectedUser.getPhotoPath();
                        photoPathLabel.setText(
                                selectedPhotoPath == null || selectedPhotoPath.isEmpty()
                                        ? "No image selected"
                                        : selectedPhotoPath
                        );

                        if (selectedPhotoPath != null && !selectedPhotoPath.isEmpty()) {
                            try {
                                photoImageView.setImage(new Image(new File(selectedPhotoPath).toURI().toString()));
                            } catch (Exception e) {
                                photoImageView.setImage(null);
                            }
                        } else {
                            photoImageView.setImage(null);
                        }

                        for (Organization organization : organizationComboBox.getItems()) {
                            if (organization.getOrgId() == selectedUser.getOrgId()) {
                                organizationComboBox.setValue(organization);
                                break;
                            }
                        }
                    }
                }
        );
    }

    @FXML
    private void handleAdd() {

        if (!validateInputs()) {
            return;
        }

        if (userDAO.usernameExists(usernameField.getText().trim())) {
            showAlert("Validation Error", "Username already exists.");
            return;
        }

        if (userDAO.emailExists(emailField.getText().trim())) {
            showAlert("Validation Error", "Email already exists.");
            return;
        }

        Organization selectedOrganization = organizationComboBox.getValue();

        User user = new User(
                usernameField.getText().trim(),
                passwordField.getText().trim(),
                fullNameField.getText().trim(),
                emailField.getText().trim(),
                roleComboBox.getValue(),
                selectedOrganization.getOrgId(),
                selectedPhotoPath
        );

        boolean result = userDAO.addUser(user);

        if (result) {
            showAlert("Success", "User added successfully.");
            loadUsers();
            handleReset();
        } else {
            showAlert("Error", "Failed to add user.");
        }
    }

    
    @FXML
    private void handleUpdate() {

        User selectedUser = usersTable.getSelectionModel().getSelectedItem();

        if (selectedUser == null) {
            showAlert("Validation Error", "Please select a user to update.");
            return;
        }

        if (!validateInputs()) {
            return;
        }

        Organization selectedOrganization = organizationComboBox.getValue();

        selectedUser.setUsername(usernameField.getText().trim());
        selectedUser.setPassword(passwordField.getText().trim());
        selectedUser.setFullName(fullNameField.getText().trim());
        selectedUser.setEmail(emailField.getText().trim());
        selectedUser.setRole(roleComboBox.getValue());
        selectedUser.setOrgId(selectedOrganization.getOrgId());
        selectedUser.setPhotoPath(selectedPhotoPath);

        boolean result = userDAO.updateUser(selectedUser);

        if (result) {
            showAlert("Success", "User updated successfully.");
            loadUsers();
            handleReset();
        } else {
            showAlert("Error", "Failed to update user.");
        }
    }
    @FXML
    private void handleDelete() {

        User selectedUser = usersTable.getSelectionModel().getSelectedItem();

        if (selectedUser == null) {
            showAlert("Validation Error", "Please select a user to delete.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Delete User");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Are you sure you want to delete this user?");

        if (confirmAlert.showAndWait().get() == ButtonType.OK) {

            boolean result = userDAO.deleteUser(selectedUser.getUserId());

            if (result) {
                showAlert("Success", "User deleted successfully.");
                loadUsers();
                handleReset();
            } else {
                showAlert("Error", "Failed to delete user.");
            }
        }
    }
    @FXML
    private void handleReset() {
        fullNameField.clear();
        usernameField.clear();
        passwordField.clear();
        emailField.clear();
        roleComboBox.setValue(null);
        organizationComboBox.setValue(null);
        photoImageView.setImage(null);
        selectedPhotoPath = "";
        photoPathLabel.setText("No image selected");
        usersTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleBrowseImage() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose User Photo");

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Image Files",
                        "*.png", "*.jpg", "*.jpeg"
                )
        );

        File selectedFile = fileChooser.showOpenDialog(
                fullNameField.getScene().getWindow()
        );

        if (selectedFile != null) {
            selectedPhotoPath = selectedFile.getAbsolutePath();
            photoPathLabel.setText(selectedPhotoPath);
            photoImageView.setImage(
                    new Image(selectedFile.toURI().toString())
            );
        }
    }

    @FXML
    public void loadUsers() {
        ObservableList<User> list =
                FXCollections.observableArrayList(
                        userDAO.getAllUsers()
                );

        usersTable.setItems(list);
    }

    private boolean validateInputs() {

        if (fullNameField.getText().trim().isEmpty()
                || usernameField.getText().trim().isEmpty()
                || passwordField.getText().trim().isEmpty()
                || emailField.getText().trim().isEmpty()
                || roleComboBox.getValue() == null
                || organizationComboBox.getValue() == null) {

            showAlert("Validation Error", "All fields are required.");
            return false;
        }

        if (passwordField.getText().trim().length() < 8) {
            showAlert("Validation Error", "Password must be at least 8 characters.");
            return false;
        }

        if (!emailField.getText().trim().contains("@")) {
            showAlert("Validation Error", "Please enter a valid email.");
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
        AppStyleManager.toggleFontSize(usersTable.getScene());
    }

    @FXML
    private void handleFontFamily() {
        AppStyleManager.toggleFontFamily(usersTable.getScene());
    }

    @FXML
    private void handleTheme() {
        AppStyleManager.toggleTheme(usersTable.getScene());
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
   private void openScreen(String fxmlPath, String title) {
	    try {
	        FXMLLoader loader =
	                new FXMLLoader(getClass().getResource(fxmlPath));

	        Scene scene =
	                new Scene(loader.load());

	        Stage stage =
	                (Stage) usersTable.getScene().getWindow();

	        stage.setTitle(title);
	        stage.setScene(scene);
	        stage.show();

	    } catch (Exception e) {
	        e.printStackTrace();
	        showAlert("Error", "Could not open screen.");
	    }
	}
   

   @FXML
   private void goToOrganizations() {
       openScreen("/views/ManageOrganizations.fxml", "GHADS - Manage Organizations");
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

  

}