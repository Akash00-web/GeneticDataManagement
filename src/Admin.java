import java.io.File;
import java.io.FileInputStream;
import java.sql.SQLException;
import java.util.Scanner;

public class Admin extends RoleHandler{

    public void actions() {
        System.out.println("""
            --- 🔐 Admin Panel ---
        
                1. View All System Users
                2. Add New User
                3. Delete User
                4. View Profiles of patients (By ID, Name, or Doctor ID)
                5. View Access Logs
                6. View Deleted Profiles of patients
                7. Export Data of users (CSV File)
                8. Logout (Return to Main Menu)
        """);
    }

    public boolean handleSession(User user, int userid , String username, Scanner sc) throws SQLException {

        /*
         * Manages the entire session for a logged-in user based on their role.
         * This method contains the menu loops for each role. The loops will only
         * terminate when the user selects the 'Logout' option for their role.

         */
        int choice;

        do {
            System.out.println();
            actions();
            choice = getValidInt(sc, "Enter your choice: ");
            switch (choice) {
                case 1:// System.out.println("Action: View all users\n");

                    DBConnection.viewAllUsers();
                    break;

                case 2:
                    // System.out.println("Action: Add new user\n");
                    String NEWusername;
                    while (true) {
                        System.out.print("Enter username: ");
                        NEWusername = sc.next();

                        if (DBConnection.isUsernameTaken(NEWusername)) {
                            System.out.println(User.RED+"❌ Username already available. Please enter a unique username.\n"+User.RESET);
                        } else {
                            System.out.println("✅ Username is valid.");
                            break;
                        }
                    }

                    String password;
                    while (true) {
                        System.out.print("Enter Strong  password: ");
                        password = sc.next();

                        if (isValidPassword(password)) {
                            System.out.println(User.GREEN+"✅ Password accepted!"+User.RESET);
                            break;
                        } else {
                            System.out.println("❌ Password must contain: ");
                            System.out.println("   →  At least one uppercase letter");
                            System.out.println("   →  At least one digit");
                            System.out.println("   →  At least one special character (@, #, $, %, etc.)");
                            System.out.println("   →  At least one lowercase letter");
                            System.out.println("Please re-enter your password.\n");
                        }
                    }

                    System.out.println("enter new user role  ( DOCTOR / STUDENT ): ");
                    String role = sc.next().toLowerCase();

                    try {
                        DBConnection.insertUser(NEWusername, password, role);
                    } catch (Exception e) {
                        System.out.println(User.RED+"❌  failed to insert user"+User.RESET);
                    }

                    break;
                case 3: //System.out.println("Action: Delete user\n");
                    DBConnection.deleteUser(sc);
                    break;
                case 4: //System.out.println("Action: View patient profile \n");
                    DBConnection.searchPatientProfiles(sc);
                    break;

                case 5: //System.out.println("Action: View access logs\n");
                    DBConnection.viewAccessLogs();
                    break;
                case 6:
                    DBConnection.viewDeletedProfiles();
                    break;
                case 7:// EXPORT DATA AS CSV FILE
                    DBConnection.exportUsersToCSV();

                    break;
                case 8:
                    DBConnection.logAction(userid, username, "Admin : " + username + " logged out  from  system");
                    System.out.println("\n🔒 Logged out from Admin panel. ↩ Returning to main menu...\n");
                    break;
                default:
                    System.out.println(User.RED+"❌ Invalid choice. Please try again.\n"+User.RESET);
                    break;
            }
        } while (choice != 8);

        return false;

    }


}
