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




public class RoleHandler {

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



    // ---- Menu Display Methods ---- //
    public static void AdminActions() {
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

    public static void DoctorActions() {
        System.out.println("""
            --- 👨‍⚕ Doctor Menu ---
        
            1. Create new patient genetic profile
            2. View genetic profile
            3. Analyze DNA for risk markers
            4. Add clinical notes
            5. view clinical notes
            6. Search profiles (By name, disease, ID)
            7. Delete profile by ID
            8. Restore Deleted Profiles
            9. Export Data of Profiles (CSV File)
            10. Logout (Return to Main Menu)
        """);
    }

    public static void StudentActions() {
        System.out.println("""
            --- 🎓 Student Practice Menu ---
        
           1. DNA Sequence Viewer
           2. GC Content Calculator
           3. Find a Pattern in DNA
           4. Reverse & Complement Generator
           5. DNA Fun Facts
           6. Logout (Return to Main Menu)
        
        """);
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

    public static boolean handleRoleSession(User user, int userid , String username,Scanner sc) throws SQLException{

        /*
         * Manages the entire session for a logged-in user based on their role.
         * This method contains the menu loops for each role. The loops will only
         * terminate when the user selects the 'Logout' option for their role.

         */
        int choice;

        switch (user.role) {
            case "Admin":

                do {
                    AdminActions();
                    choice =getValidInt(sc, "Enter your choice: ");
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

                                if (DBConnection.isUsernameTaken( NEWusername)) {
                                    System.out.println("❌ Username already available. Please enter a unique username.\n");
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
                                    System.out.println("✅ Password accepted!");
                                    break;
                                } else {
                                    System.out.println("❌ Password must contain: ");
                                    System.out.println("   → At least one uppercase letter");
                                    System.out.println("   → At least one digit");
                                    System.out.println("   → At least one special character (@, #, $, %, etc.)");
                                    System.out.println("   → At least one lowercase letter");
                                    System.out.println("Please re-enter your password.\n");
                                }
                            }

                            System.out.println("enter new user role  ( DOCTOR / STUDENT ): ");
                            String role = sc.next().toLowerCase();

                            try{
                                DBConnection.insertUser(NEWusername, password, role);
                            }
                            catch(Exception e){
                                System.out.println("❌  failed to insert user");
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
                        case 6 :DBConnection.viewDeletedProfiles();
                            break;
                        case 7 :// EXPORT DATA AS CSV FILE
                            DBConnection.exportUsersToCSV();

                            break;
                        case 8: DBConnection.logAction(userid, username, "Admin : "+username+" logged out  from  system");
                            System.out.println("\n🔒 Logged out from Admin panel. ↩ Returning to main menu...\n");
                            break;
                        default: System.out.println("❌ Invalid choice. Please try again.\n");
                            break;
                    }
                } while (choice != 8);
                break;

            case "Doctor":

                do {
                    DoctorActions();

                    choice =getValidInt(sc, "Enter your choice: ");

                    switch (choice) {
                        case 1: //System.out.println("Action: Create new patient genetic profile\n");
                            System.out.println("<---   Creating New Patient Profile   --->");
                            try {
                                System.out.println("enter new patient name: ");
                                String name = sc.next();

                                System.out.println("enter date of birth (YYYY-MM-DD) : ");
                                String birthDate = sc.next();

                                System.out.print("Enter DNA sequence (minimum 6 characters or more): ");
                                String dnaSequence = sc.next().toUpperCase().trim();

                                while (dnaSequence.length() < 6) {
                                    System.out.println("⚠ DNA sequence must be at least 6 characters. Please re-enter:");
                                    dnaSequence = sc.next().trim().toUpperCase();
                                }

                                System.out.println("enter diseaseMarkers: ");
                                String diseaseMarkers = sc.next().trim();

                                System.out.println("enter doctor  ID: ");
                                int doctorID = sc.nextInt();
                                sc.nextLine(); // consume newline

                                // ✅ Ask for photo number
                                System.out.print("Enter photo number (or press Enter to skip): ");
                                String photoNumber = sc.nextLine().trim();
                                FileInputStream fis = null;

                                if (!photoNumber.isEmpty()) {
                                    String photoPath = "D:\\PatientPhoto\\PatientPhoto" + photoNumber + ".jpg";
                                    File file = new File(photoPath);
                                    if (file.exists()) {
                                        fis = new FileInputStream(file);
                                    } else {
                                        System.out.println("⚠ File not found: " + photoPath);
                                    }
                                }

                                // ✅ Pass fis to DB
                                DBConnection.AddNewPatient(name, birthDate, dnaSequence, diseaseMarkers, doctorID, fis);

                                if (fis != null) fis.close(); // close stream after use

                            } catch (Exception e) {
                                System.out.println("⚠ An error happened..");
                                e.printStackTrace();
                            }

                            break;
                        case 2: //System.out.println("Action: View genetic profile\n");
                            DBConnection.viewAllProfiles();

                            break;
                        case 3: //System.out.println("Action: Analyze DNA for risk markers\n");
                            System.out.println("Enter profile ID To Search ");
                            int id  = sc.nextInt();
                            DBConnection.analyzeDnaForRiskMarkers(id);
                            break;

                        case 4://adding note for patient
                            try {
                                int patientID = 0;
                                int doctorID = 0;
                                String note = "";
                                while (true) {
                                    try {
                                        System.out.print("Enter patient profile ID: ");
                                        patientID = sc.nextInt();
                                        break; // exit loop if valid
                                    } catch (Exception ex) {
                                        System.out.println("❌ Invalid input! Please enter a valid numeric Patient ID.");
                                        sc.nextLine(); // clear wrong input from buffer
                                    }
                                }
                                while (true) {
                                    try {
                                        System.out.print("Enter doctor ID: ");
                                        doctorID = sc.nextInt();
                                        break;
                                    } catch (Exception ex) {
                                        System.out.println("❌ Invalid input! Please enter a valid numeric Doctor ID.");
                                        sc.nextLine();
                                    }
                                }
                                sc.nextLine();
                                while (true) {
                                    System.out.print("Write note for patient: ");
                                    note = sc.nextLine().trim();
                                    if (!note.isEmpty()) {
                                        break;
                                    } else {
                                        System.out.println("❌ Note cannot be empty! Please enter again.");
                                    }
                                }
                                DBConnection.addClinicalNote(patientID, note, doctorID);

                            } catch (Exception e) {
                                System.out.println("⚠ An error occurred while inserting note.");
                                e.printStackTrace();
                            }
                            break;

                        case 5 :
                            //  System.out.println("-- Viewing Clinical Notes from Patient---");
                            DBConnection.viewClinicalNotes();
                            break;
                        case 6: //System.out.println("Action: Search profiles\n");
                            DBConnection.searchPatientProfilesbyDisease(sc);
                            break;
                        case 7:// System.out.println("Action: Deleting patient Profile \n");
                            System.out.println("enter profile id to delete : ");
                            int pid= sc.nextInt();
                            DBConnection.deleteProfile(pid);
                            break;
                        case 8 :
                            System.out.println("---  Restoring Deleted profiles -----");
                            DBConnection.undoDeleteProfile();

                            break;
                        case 9: // Export profile data to D drive
                            DBConnection.exportProfilesToCSV();
                            break;
                        case 10:
                            DBConnection.logAction(userid, username, "Doctor : "+username+" logged out  from system");

                            System.out.println("\n🔒 Logged out from Doctor panel. Returning to main menu...\n");
                            break;
                        default: System.out.println("❌ Invalid choice. Please try again.\n");
                            break;
                    }
                } while (choice != 10);
                break;

            case "Student":
                do {
                    StudentActions();

                    choice =getValidInt(sc, "Enter your choice: ");

                    switch (choice) {
                        case 1: //System.out.println("Action: DNA Sequence Viewer\n");
                            System.out.print("Enter DNA sequence: ");
                            String dna=sc.nextLine().toUpperCase();
                            DBConnection.  dnaSequenceViewer(dna);
                            System.out.println();
                            DBConnection.dnaHelixViewer(dna);

                            break;
                        case 2: //System.out.println("Action: GC Content Calculator\n");
                            System.out.print("Enter DNA sequence: ");
                            DBConnection.  gcContentCalculator(sc.nextLine().toUpperCase());
                            break;
                        case 3: //System.out.println("Action: Find a Pattern in DNA\n");
                            System.out.print("Enter DNA sequence: ");
                            String seq = sc.nextLine().toUpperCase();
                            System.out.print("Enter pattern to search: ");
                            String pattern = sc.nextLine().toUpperCase();
                            DBConnection. findPattern(seq, pattern);
                            break;
                        case 4:// System.out.println("Action: Reverse & Complement Generator\n");
                            System.out.print("Enter DNA sequence: ");
                            DBConnection.reverseAndComplement(sc.nextLine().toUpperCase());
                            break;
                        case 5:
                            // System.out.println("  Action: DNA Fun Facts");
                            DBConnection. dnaFunFacts();
                            break;
                        case 6 : DBConnection.logAction(userid, username, "Student : "+username+" logged out  from system");
                            System.out.println("\n🔒 Logged out from Student panel.  ↩Returning to main menu...\n");
                            break;
                        default: System.out.println("❌ Invalid choice. Please try again.\n");
                            break;
                    }
                } while (choice != 6);
                break;
            default:
                System.out.println("Invalid role selection ..");
                break;

        }
        return false;
    }
}