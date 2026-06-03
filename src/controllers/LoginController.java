package controllers;
import models.User;
import utils.Session;
import dao.UserDAO;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import utils.AppStyleManager;
public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ComboBox<String> roleComboBox;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    public void initialize() {
        roleComboBox.getItems().addAll("ADMIN", "COORDINATOR");
    }

    
    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String role = roleComboBox.getValue();

        if (username.isEmpty() || password.isEmpty() || role == null) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please enter username, password, and role.");
            return;
        }

        User loggedInUser = userDAO.getUserByLogin(username, password, role);

        if (loggedInUser != null) {

            Session.setCurrentUser(loggedInUser);

            if (role.equals("ADMIN")) {
                openAdminDashboard();
            } else {
                openCoordinatorDashboard();
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Incorrect username, password, or role.");
        }
    }
    private void openCoordinatorDashboard() {
        try {
            javafx.fxml.FXMLLoader loader =
                    new javafx.fxml.FXMLLoader(getClass().getResource("/views/CoordinatorDashboard.fxml"));

            javafx.scene.Scene scene =
                    new javafx.scene.Scene(loader.load());

            javafx.stage.Stage stage =
                    (javafx.stage.Stage) usernameField.getScene().getWindow();

            stage.setTitle("GHADS - Coordinator Dashboard");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not open Coordinator Dashboard.");
        }
    }
    private void openAdminDashboard() {
        try {
            javafx.fxml.FXMLLoader loader =
                    new javafx.fxml.FXMLLoader(getClass().getResource("/views/AdminDashboard.fxml"));

            javafx.scene.Scene scene = new javafx.scene.Scene(loader.load());

            javafx.stage.Stage stage =
                    (javafx.stage.Stage) usernameField.getScene().getWindow();

            stage.setTitle("GHADS - Admin Dashboard");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not open Admin Dashboard.");
        }
    }

    @FXML
    private void handleExit() {
        Platform.exit();
    }

    @FXML
    private void handleFontSize() {
        AppStyleManager.toggleFontSize(usernameField.getScene());
    }

    @FXML
    private void handleFontFamily() {
        AppStyleManager.toggleFontFamily(usernameField.getScene());
    }

    @FXML
    private void handleTheme() {
        AppStyleManager.toggleTheme(usernameField.getScene());
    }
    @FXML
    private void handleAboutApp() {
        showAlert(
                Alert.AlertType.INFORMATION,
                "About GHADS",
                "GHADS - Gaza Humanitarian Aid Distribution System\n\n"
                + "This application helps coordinate humanitarian aid distribution in Gaza.\n\n"
                + "Developer: Shahd Abo Kwaik"
        );
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}