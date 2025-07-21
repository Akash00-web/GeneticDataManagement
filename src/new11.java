import java.sql.*;
import java.util.*;
import static java.lang.Thread.sleep;
import java.util.Date;

// ---------- DB Connection  ----------
class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/project";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    // Flag to track if tables have been initialized in this session
    private static boolean tablescreated = false;

    public static Connection getConnection() throws Exception {
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            try{ System.out.println("Connecting to database...");
            }
            catch(Exception e){
                System.out.println(" ❌ Error connecting to database");
            }
        } catch (ClassNotFoundException e) {
            System.out.println("❌ MySQL JDBC Driver not found.");
            throw new RuntimeException("MySQL JDBC Driver not found.", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // New method to initialize all tables
    public static void initializeTables() {
        if (!tablescreated) {
            System.out.println("\nInitializing database tables...");
            createUsersTable();
            createAccessLogsTable();
            createPatientGeneticProfileTable();
            createTreatmentTable();
            createClinicalNotesTable();
            createEducationalContentTable();
            createDNASampleTable();
            tablescreated = true; // Set the flag to true after initialization
            System.out.println("Database table initialization complete.\n");
        }
    }

    //   1.  creates user table
    public static void createUsersTable() {
        String createTable = "CREATE TABLE IF NOT EXISTS users ("
                + "id INT AUTO_INCREMENT PRIMARY KEY ,"
                + "username VARCHAR(50) NOT NULL UNIQUE,"
                + "password VARCHAR(255) NOT NULL," // Store hashed passwords
                + "role VARCHAR(20) NOT NULL,"
                + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                + ")";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTable);
            if (!tablescreated) {
                System.out.println("✅ 'users' table created or already exists.");
            }
        } catch (Exception e) {
            System.err.println("❌ Error creating 'users' table: " + e.getMessage());
        }
    }

    //2.  New method to create the accessLogs table
    public static void createAccessLogsTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS AccessLogs ("
                + "logID INT AUTO_INCREMENT PRIMARY KEY,"
                + "userID INT,"
                + "action longtext NOT NULL,"
                + "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                + ")";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
            if (!tablescreated) {
                System.out.println("✅ 'AccessLogs' table created or already exists.");
            }
        } catch (Exception e) {
            System.err.println("❌ Error creating 'SystemData' table: " + e.getMessage());
            e.printStackTrace(); // Keep this for better debugging if issues persist
        }
    }

    // 3 .create a patient genetic profile
    public static void createPatientGeneticProfileTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS patientGeneticProfile ("
                + "profileID INT AUTO_INCREMENT PRIMARY KEY,"
                + "patientName VARCHAR(100) NOT NULL,"
                + "dob DATE," // Date of Birth
                + "dnaSequence TEXT," // For long DNA sequences
                + "diseaseMarkers TEXT," // For JSON or text data about disease markers
                + "doctorID INT" // Assuming doctorID references a user in the 'users' table
                + ")";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
            if (!tablescreated) {
                System.out.println("✅ 'patientGeneticProfile' table created or already exists.");
            }
        } catch (Exception e) {
            System.err.println("❌ Error creating 'patientGeneticProfile' table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //  4 .Method to create the treatment table
    public static void createTreatmentTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS treatment ("
                + "treatmentID INT AUTO_INCREMENT PRIMARY KEY," // Added a primary key for the treatment table itself
                + "profileID INT NOT NULL," // References patientGeneticProfile
                + "recommendationText TEXT NOT NULL,"
                + "treatmentDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP," // Renamed 'date' to 'treatmentDate' to avoid keyword conflict
                + "doctorID INT NOT NULL," // References users table
                + "FOREIGN KEY (profileID) REFERENCES patientGeneticProfile(profileID),"
                + "FOREIGN KEY (doctorID) REFERENCES users(id)"
                + ")";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
            if (!tablescreated) {
                System.out.println("✅ 'treatment' table created or already exists.");
            }
        } catch (Exception e) {
            System.err.println("❌ Error creating 'treatment' table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //5 . Method to create the clinicalNotes table
    public static void createClinicalNotesTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS clinicalNotes ("
                + "noteID INT AUTO_INCREMENT PRIMARY KEY," // Unique ID for each note
                + "profileID INT NOT NULL," // References patientGeneticProfile
                + "noteText TEXT NOT NULL,"
                + "noteDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP," // Renamed 'date' to 'noteDate' to avoid keyword conflict
                + "doctorID INT NOT NULL," // References users table
                + "FOREIGN KEY (profileID) REFERENCES patientGeneticProfile(profileID),"
                + "FOREIGN KEY (doctorID) REFERENCES users(id)"
                + ")";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
            if (!tablescreated) {
                System.out.println("✅ 'clinicalNotes' table created or already exists.");
            }
        } catch (Exception e) {
            System.err.println("❌ Error creating 'clinicalNotes' table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 6 .Method to create the EducationalContent table
    public static void createEducationalContentTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS EducationalContent ("
                + "topicID INT AUTO_INCREMENT PRIMARY KEY," // Unique ID for each topic
                + "topicTitle VARCHAR(255) NOT NULL UNIQUE," // Title of the educational topic
                + "contentText TEXT" // The actual educational content (can be long)
                + ")";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
            if (!tablescreated) {
                System.out.println("✅ 'EducationalContent' table created or already exists.");
            }
        } catch (Exception e) {
            System.err.println("❌ Error creating 'EducationalContent' table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //7.  Method to create the DNASample table
    public static void createDNASampleTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS DNASample ("
                + "sampleID INT AUTO_INCREMENT PRIMARY KEY," // Unique ID for each DNA sample
                + "sequenceData TEXT NOT NULL," // The actual DNA sequence data
                + "description VARCHAR(500)" // Optional description of the sample
                + ")";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
            if (!tablescreated) {
                System.out.println("✅ 'DNASample' table created or already exists.");
            }
        } catch (Exception e) {
            System.err.println("❌ Error creating 'DNASample' table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /* 1.-------------------- ADMIN MENU ------------------------------------------*/
    public static void viewAllUsers() {
        String sql = "SELECT id, username, role FROM users ORDER BY id";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            System.out.println("\n══════════════════════════════════════════════════════");
            System.out.println("                 📋 All System Users 📋");
            System.out.println("══════════════════════════════════════════════════════");
            System.out.printf("%-5s %-20s %-15s\n", "ID", "Username", "Role");
            System.out.println("--------------------------------------------------");

            if (!rs.isBeforeFirst()) { // Check if ResultSet is empty
                System.out.println("No users found in the system.");
            } else {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String username = rs.getString("username");
                    String role = rs.getString("role");
                    System.out.printf("%-5d %-20s %-15s\n", id, username, role);
                }
            }
            System.out.println("══════════════════════════════════════════════════════\n");

        } catch (Exception e) {
            System.err.println("❌ Error viewing all users: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void insertUser(String username, String password, String role) {

        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) { // No Statement.RETURN_GENERATED_KEYS

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, role);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ User '" + username + "' (" + role + ") added successfully.");

            } else {
                System.out.println("❌ Failed to add user '" + username + "'. No rows affected.");
            }
        } catch (Exception e) {
            System.err.println("❌ Error adding user: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void deleteUser(Scanner sc) {
        System.out.println("\n--- 🗑️ Delete User ---");
        System.out.print("Do you want to delete by 'id' or 'username'? (type 'id' or 'username'): ");
        String deleteOption = sc.next().trim().toLowerCase();

        String checkSql;
        String deleteSql;
        Object identifier = null;
        String identifierType = "";
        int userId = -1; // To store the actual ID for confirmation
        String foundUsername = null;

        // delete user by id
        if (deleteOption.equals("id")) {
            System.out.print("Enter the ID of the user to delete: ");
            if (!sc.hasNextInt()) {
                System.out.println("❌ Invalid input. Please enter a numeric user ID.");
                sc.next(); // Consume invalid input
                sc.nextLine(); // Consume remaining newline
                return;
            }
            userId = sc.nextInt();
            sc.nextLine(); // Consume newline
            identifier = userId;
            identifierType = "ID";
            checkSql = "SELECT id, username FROM users WHERE id = ?";
            deleteSql = "DELETE FROM users WHERE id = ?";
        } else if (deleteOption.equals("username")) {
            System.out.print("Enter the username of the user to delete: ");
            String username = sc.next().trim();
            identifier = username;
            identifierType = "Username";
            checkSql = "SELECT id, username FROM users WHERE username = ?";
            deleteSql = "DELETE FROM users WHERE username = ?";
        } else {
            System.out.println("❌ Invalid option. Please type 'id' or 'username'.");
            return;
        }

        try (Connection conn = getConnection()) {
            // 1. Check if the user exists and get details for confirmation
            try (PreparedStatement checkPstmt = conn.prepareStatement(checkSql)) {
                if (identifier instanceof Integer) {
                    checkPstmt.setInt(1, (Integer) identifier);
                } else {
                    checkPstmt.setString(1, (String) identifier);
                }

                ResultSet rs = checkPstmt.executeQuery();
                if (rs.next()) {
                    userId = rs.getInt("id");
                    foundUsername = rs.getString("username");
                } else {
                    System.out.println("❌ User with " + identifierType + " '" + identifier + "' not found.");
                    return;
                }
            }

            // 2. Get confirmation from the user
            System.out.print("Are you sure you want to delete user '" + foundUsername + "' (ID: " + userId + ")? (yes/no): ");
            String confirmation = sc.next().trim().toLowerCase();

            if (!confirmation.equals("yes")) {
                System.out.println("Operation cancelled. User not deleted.");
                return;
            }

            // 3. Execute the deletion
            try (PreparedStatement deletePstmt = conn.prepareStatement(deleteSql)) {
                if (identifier instanceof Integer) {
                    deletePstmt.setInt(1, (Integer) identifier);
                } else {
                    deletePstmt.setString(1, (String) identifier);
                }

                int rowsAffected = deletePstmt.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("✅ User '" + foundUsername + "' (ID: " + userId + ") deleted successfully.");
                    System.out.println("📝 Note: Access logs for this user were NOT deleted.");
                } else {
                    System.out.println("❌ Failed to delete user '" + identifier + "'. No rows affected.");
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Database error during user deletion: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to view patient profiles by ID, name, or Doctor ID
    public static void searchPatientProfiles(Scanner sc) {
        System.out.println("\n--- 🔍 Search Patient Genetic Profiles ---");
        System.out.println();
        System.out.println("Search by:");
        System.out.println("1. Profile ID");
        System.out.println("2. Patient Name");
        System.out.println("3. Doctor ID");
        System.out.println();
        System.out.print("Enter your choice (1, 2, or 3): ");

        int searchChoice;
        if (!sc.hasNextInt()) {
            System.out.println("❌ Invalid input. Please enter a number (1, 2, or 3).");
            sc.next();
            sc.nextLine();
            return;
        }
        searchChoice = sc.nextInt();
        sc.nextLine();

        String sql;
        Object searchValue = null;
        String searchCriterion = "";

        switch (searchChoice) {
            case 1:
                System.out.print("Enter Profile ID: ");
                if (!sc.hasNextInt()) {
                    System.out.println("❌ Invalid input. Please enter a numeric ID.");
                    sc.next();
                    sc.nextLine();
                    return;
                }
                searchValue = sc.nextInt();
                sc.nextLine(); // Consume newline
                sql = "SELECT profileID, patientName, dob, dnaSequence, diseaseMarkers, doctorID FROM patientGeneticProfile WHERE profileID = ?";
                searchCriterion = "Profile ID";
                break;
            case 2:
                System.out.print("Enter Patient Name (or part of it): ");
                searchValue = "%" + sc.nextLine().trim() + "%";
                sql = "SELECT profileID, patientName, dob, dnaSequence, diseaseMarkers, doctorID FROM patientGeneticProfile WHERE patientName LIKE ?";
                searchCriterion = "Patient Name";
                break;
            case 3:
                System.out.print("Enter Doctor ID: ");
                if (!sc.hasNextInt()) {
                    System.out.println("❌ Invalid input. Please enter a numeric Doctor ID.");
                    sc.next();
                    sc.nextLine();
                    return;
                }
                searchValue = sc.nextInt();
                sc.nextLine();
                sql = "SELECT profileID, patientName, dob, dnaSequence, diseaseMarkers, doctorID FROM patientGeneticProfile WHERE doctorID = ?";
                searchCriterion = "Doctor ID";
                break;
            default:
                System.out.println("❌ Invalid choice. Please select 1, 2, or 3.");
                return;
        }

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Set the appropriate parameter based on type
            if (searchValue instanceof Integer) {
                pstmt.setInt(1, (Integer) searchValue);
            } else if (searchValue instanceof String) {
                pstmt.setString(1, (String) searchValue);
            }

            ResultSet rs = pstmt.executeQuery();

            System.out.println("\n════════════════════════════════════════════════════════════════════");
            System.out.println("             🔎 Search Results for " + searchCriterion + ": '" + searchValue + "'");
            System.out.println("════════════════════════════════════════════════════════════════════");
            System.out.printf("%-10s %-25s %-12s %-10s %-20s %-10s\n", "Profile ID", "Patient Name", "DOB", "Doctor ID", "DNA ", "Markers ");
            System.out.println("----------------------------------------------------------------------");

            if (!rs.isBeforeFirst()) { // Check if ResultSet is empty
                System.out.println("No profiles found matching your criteria.");
            }
            else
            {
                while (rs.next()) {
                    int profileID = rs.getInt("profileID");
                    String patientName = rs.getString("patientName");
                    Date dob = rs.getDate("dob");
                    String dnaSequence = rs.getString("dnaSequence");
                    String diseaseMarkers = rs.getString("diseaseMarkers");
                    int doctorID = rs.getInt("doctorID");

                    // Truncate long strings for display
                    String displayDna = dnaSequence != null && dnaSequence.length() > 20 ? dnaSequence.substring(0, 17) + "..." : dnaSequence;
                    String displayMarkers = diseaseMarkers != null && diseaseMarkers.length() > 20 ? diseaseMarkers.substring(0, 17) + "..." : diseaseMarkers;

                    System.out.printf("%-10d %-25s %-12s %-10d %-20s %-20s\n",
                            profileID, patientName, dob != null ? dob.toString() : "N/A", doctorID,
                            displayDna, displayMarkers);
                }
            }
            System.out.println("==========================================================\n");

        } catch (SQLException e) {
            System.err.println("❌ Database error searching profiles: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void viewAccessLogs() {
        // Removed 'attributes' from the SELECT statement
        String sql = "SELECT logID, userID, action, timestamp FROM AccessLogs ORDER BY timestamp DESC";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            System.out.println("\n════════════════════════════════════════════════════════════════════"); // Adjusted width
            System.out.println("                            📋 **System Access Logs** 📋");
            System.out.println("════════════════════════════════════════════════════════════════════"); // Adjusted width
            // Removed 'Attributes (start)' header
            System.out.printf("%-8s %-8s %-25s %-20s\n", "**Log ID**", "**User ID**", "**Action**", "**Timestamp**");
            System.out.println("--------------------------------------------------------------------"); // Adjusted width

            if (!rs.isBeforeFirst()) {
                System.out.println("No access logs found in the system.");
            } else {
                while (rs.next()) {
                    int logID = rs.getInt("logID");
                    int userID = rs.getInt("userID");
                    String action = rs.getString("action");
                    Timestamp timestamp = rs.getTimestamp("timestamp");
                    System.out.printf("%-8d %-8d %-25s %-20s\n",
                            logID, userID, action, timestamp.toString());
                }
            }
            System.out.println("===================================================================\n");

        } catch (SQLException e) {
            System.err.println("❌ **Database error viewing access logs**: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ **An unexpected error occurred while viewing access logs**: " + e.getMessage());
            e.printStackTrace();
        }
    }


}


// --- Main Application Class ---//
public class new11 {

    // A single Scanner instance is shared across the application to avoid resource leaks.
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        // Initialize tables once at the very beginning of the application
        DBConnection.initializeTables();

        // The main application loop. It continues until the user chooses to exit.
        while (true) {
            User user = new User();

            // Step 1: Show the main role selection menu.
            // The selectRole method now returns 'true' if the user wants to exit the app.
            boolean shouldExit = user.selectRole(sc);

            if (shouldExit) {
                System.out.println("\n👋 Thank you for using the Genetic Data Management System. Goodbye!");
                break; // Exit the main while loop, terminating the program.
            }

            // Step 2: If a valid role was chosen, proceed with login.
            if (user.role != null && !user.role.equals("unknown")) {
                // Step 2a: Verify the user with an OTP.
                boolean isLoggedIn = user.verifyOTP(sc);

                // Step 2b: If OTP is correct, start the user's session.
                if (isLoggedIn) {
                    // It will loop until the user chooses to log out from their role.
                    RoleHandler.handleRoleSession(user, sc);
                }
            } else {
                System.out.println("❌ Invalid role selection. Please try again.\n");
            }
        }
        sc.close();
    }
}

// --- User Class ---//
class User {
    public String role;

    /**
     * by sending an OTP and verifies the user's input.
     * return true if the OTP is verified, false otherwise.
     */
    public boolean verifyOTP(Scanner sc) {
        Random rand = new Random();
        int otp = 1000 + rand.nextInt(9000); // 4-digit OTP

        System.out.println("\n📩 Your One-Time Password (OTP) is: " + otp);

        int attempts = 3;
        while (attempts > 0) {
            System.out.print("🔐 Enter OTP: ");
            // Basic input validation
            if (!sc.hasNextInt()) {
                System.out.println("❌ Invalid input. Please enter numbers only.");
                sc.next(); // Consume the invalid token
                attempts--;
                System.out.println("   Attempts left: " + attempts);
                continue;
            }
            int enteredOtp = sc.nextInt();

            if (enteredOtp == otp) {
                System.out.println("✅ OTP verified successfully.");

                System.out.println("✅ Logged in as " + this.role + ".\n");
                return true;
            } else {
                attempts--;
                System.out.println("❌ Incorrect OTP. Attempts left: " + attempts);
            }
        }

        System.out.println("⛔ Too many wrong attempts. Returning to the main menu.\n");
        return false;
    }

    /**
     * Displays the main role selection menu and sets the user's role.
     * return true if the user chooses to exit the application, false otherwise.
     */
    public boolean selectRole(Scanner sc) {
        System.out.println("""
                    ══════════════════════════════════════════════════════
                     🔬✨   Welcome to the Future of Genetics  ✨🔬
                     
                         🧬  GENETIC DATA MANAGEMENT SYSTEM  🧬
                                                                          
                    ══════════════════════════════════════════════════════
            
            Select your role (press only numbers):
            1. Admin 🔐
            2. Doctor 👨‍⚕️
            3. Student 🎓
            4. Exit System
        """);
        System.out.print("Enter your choice: ");

        if (!sc.hasNextInt()) {
            System.out.println("❌ Invalid input. Please enter a number.");
            sc.next(); // Consume the invalid token to prevent an infinite loop
            this.role = "unknown";
            return false;
        }

        int choice = sc.nextInt();
        switch (choice) {
            case 1: this.role = "Admin";break;
            case 2: this.role = "Doctor"; break;
            case 3: this.role = "Student"; break;
            case 4: return true; // Signal that the user wants to exit the application.
            default: this.role = "unknown"; break;
        }
        return false; // Signal to continue within the application.
    }
}

// ---------- Role Handler ----------
class RoleHandler {
    // --- Menu Display Methods --- //

    public static void DoctorActions() {
        System.out.println("""
            --- 👨‍⚕️ Doctor Menu ---
            
            1. Create new patient genetic profile
            2. View genetic profile
            3. Analyze DNA for risk markers
            4. Recommend treatment
            5. Add clinical notes
            6. Search profiles (By name, disease, ID)
            7. View analysis history
            8. Logout (Return to Main Menu)
        """);
    }

    public static void StudentActions() {
        System.out.println("""
            --- 🎓 Student Practice Menu ---
            
            1. Learn about DNA structure
            2. View sample sequences
            3. Take a quiz
            4. Fun facts about DNA
            5. Logout (Return to Main Menu)
        """);
    }

    public static void AdminActions() {
        System.out.println("""
            --- 🔐 Admin Panel ---
            
            1. View all users
            2. Add new user
            3. Delete user
            4. View  profiles (By id,name or Doctor id)
            5. View access logs
            6. Logout (Return to Main Menu)
        """);
    }

    public static void handleRoleSession(User user, Scanner sc) {

        /**
         * Manages the entire session for a logged-in user based on their role.
         * This method contains the menu loops for each role. The loops will only
         * terminate when the user selects the 'Logout' option for their role.

         */
        int choice;
        switch (user.role) {
            case "Admin":
                do {
                    AdminActions();
                    System.out.print("Enter your choice: ");
                    choice = sc.nextInt();

                    switch (choice) {
                        case 1: System.out.println("Action: View all users\n");

                            DBConnection.viewAllUsers();
                            break;

                        case 2: System.out.println("Action: Add new user\n");
                            System.out.println( );
                            System.out.println("enter new user name: ");
                            String name = sc.next();
                            System.out.println("enter new user password: ");
                            String password = sc.next();
                            System.out.println("enter new user role: ");
                            String role = sc.next();
                            try{
                                DBConnection.insertUser(name, password, role);
                            }
                            catch(Exception e){
                                System.out.println("❌  failed to insert user");
                            }

                            break;
                        case 3: System.out.println("Action: Delete user\n");
                        DBConnection.deleteUser(sc);
                            break;
                        case 4: System.out.println("Action: View patient profile \n");
                        DBConnection.searchPatientProfiles(sc);
                            break;

                        case 5: System.out.println("Action: View access logs\n");
                        DBConnection.viewAccessLogs();
                            break;
                        case 6:
                            System.out.println("\n🔒 Logged out from Admin panel. Returning to main menu...\n");
                            break;
                        default: System.out.println("❌ Invalid choice. Please try again.\n");
                            break;
                    }
                } while (choice != 6);
                break; // Exit the main switch, allowes  the method to return

            case "Doctor":

                do {
                    DoctorActions();
                    System.out.print("Enter your choice: ");
                    choice = sc.nextInt();

                    switch (choice) {
                        case 1: System.out.println("Action: Create new patient genetic profile\n");
                            break;
                        case 2: System.out.println("Action: View genetic profile\n");
                            break;
                        case 3: System.out.println("Action: Analyze DNA for risk markers\n");
                            break;
                        case 4: System.out.println("Action: Recommend treatment\n");
                            break;
                        case 5: System.out.println("Action: Add clinical notes\n");
                            break;
                        case 6: System.out.println("Action: Search profiles\n");
                            break;
                        case 7: System.out.println("Action: View analysis history\n");
                            break;
                        case 8:
                            System.out.println("\n🔒 Logged out from Doctor panel. Returning to main menu...\n");
                            break;
                        default: System.out.println("❌ Invalid choice. Please try again.\n");
                            break;
                    }
                } while (choice != 8);
                break;

            case "Student":

                do {
                    StudentActions();
                    System.out.print("Enter your choice: ");
                    choice = sc.nextInt();

                    switch (choice) {
                        case 1: System.out.println("Action: Learn about DNA structure\n");
                            break;
                        case 2: System.out.println("Action: View sample sequences\n");
                            break;
                        case 3: System.out.println("Action: Take a quiz\n");
                            break;
                        case 4: System.out.println("Action: Fun facts about DNA\n");
                            break;
                        case 5:
                            System.out.println("\n🔒 Logged out from Student panel. Returning to main menu...\n");
                            break;
                        default: System.out.println("❌ Invalid choice. Please try again.\n");
                            break;
                    }
                } while (choice != 5);
                break;
        }
    }
}