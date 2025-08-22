import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.SequencedCollection;

public abstract class RoleHandler {


//  methos to validate password
    public static boolean isValidPassword(String password) {
        boolean hasUppercase = false;
        boolean hasLowercase = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (char ch : password.toCharArray()) {
            if (Character.isUpperCase(ch)) {
                hasUppercase = true;
            } else if (Character.isLowerCase(ch)) {
                hasLowercase = true;
            } else if (Character.isDigit(ch)) {
                hasDigit = true;
            } else if ("@#$%&*!".contains(String.valueOf(ch))) {
                hasSpecial = true;
            }
        }

        return hasUppercase && hasLowercase && hasDigit && hasSpecial;
    }

    // method to chech input is valid or not
    public static int getValidInt(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            if (sc.hasNextInt()) {
                int value = sc.nextInt();
                sc.nextLine(); // consume newline
                return value;
            } else {
                System.out.println("❌ Invalid input. Please enter a number.");
                sc.next(); // discard wrong token
            }
        }
    }

    public abstract  void actions();

    public  abstract boolean  handleSession(User user, int userid , String username, Scanner sc) throws SQLException;


    }