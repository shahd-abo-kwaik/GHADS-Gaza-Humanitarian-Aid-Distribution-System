package controllers;

import dao.UserDAO;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import utils.AppStyleManager;
import utils.Session;
public class ChangePasswordController {

    @FXML
    private PasswordField currentPasswordField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    private UserDAO userDAO = new UserDAO();

    
    @FXML
    private void handleChangePassword() {

        String currentPassword = currentPasswordField.getText().trim();
        String newPassword = newPasswordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        if (currentPassword.isEmpty()
                || newPassword.isEmpty()
                || confirmPassword.isEmpty()) {

            showAlert("Validation Error", "All fields are required.");
            return;
        }

        if (newPassword.length() < 8) {
            showAlert("Validation Error", "New password must be at least 8 characters.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showAlert("Validation Error", "New password and confirm password do not match.");
            return;
        }
        String username = Session.getCurrentUser().getUsername();

        boolean result =
                userDAO.changePassword(username, currentPassword, newPassword);
        if (result) {
            showAlert("Success", "Password changed successfully.");
            handleReset();
        } else {
            showAlert("Error", "Current password is incorrect.");
        }
    }
    @FXML
    private void handleReset() {
        currentPasswordField.clear();
        newPasswordField.clear();
        confirmPasswordField.clear();
    }

    @FXML
    private void handleExit() {
        Platform.exit();
    }

      private void showAlert(String title, String message) {

        Alert alert =
                new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    @FXML
  private void handleAboutApp() {
    	showAlert (
              "About GHADS",
              "GHADS - Gaza Humanitarian Aid Distribution System\n\n"
              + "This application helps coordinate humanitarian aid distribution in Gaza.\n\n"
              + "Developer: Shahd Abo Kwaik"
      );
   
}
    @FXML
    private void handleFontSize() {
        AppStyleManager.toggleFontSize(currentPasswordField.getScene());
    }

    @FXML
    private void handleFontFamily() {
        AppStyleManager.toggleFontFamily(currentPasswordField.getScene());
    }

    @FXML
    private void handleTheme() {
        AppStyleManager.toggleTheme(currentPasswordField.getScene());
    }}