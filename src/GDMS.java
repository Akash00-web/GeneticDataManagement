import java.sql.SQLException;
import java.util.Scanner;

public class GDMS {

    private static final Scanner sc = new Scanner(System.in);
    public static void main(String[] args) throws SQLException {
        // Initialize database tables
        DBConnection.initializeTables();


        while (true) {
            User user = new User();

            // Step 1: Show the menu and handle role/login/OTP
            boolean shouldExit = user.selectRole(sc);

            if (shouldExit) {
                System.out.println("\n👋 Thank you for using the Genetic Data Management System. Goodbye!");
                break; // exit program
            }

            // Step 2: If role is valid after successful login & OTP, go to role session
            if (user.role != null && !user.role.equals("unknown")) {
                RoleHandler.handleRoleSession(user, User.userid, User.username, sc);
            }
            // Step 3: Otherwise, loop back to menu automatically

        }
        sc.close();
    }
}
