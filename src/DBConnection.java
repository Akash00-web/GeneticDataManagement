import java.io.File;
import java.io.FileInputStream;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.io.*;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/project";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    // Flag to track if tables have been initialized in this session
    private static boolean tablescreated = false;


    public static Connection getConnection() throws Exception {

        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            //     System.out.println("Connecting to database...");
        } catch (ClassNotFoundException e) {
            System.out.println("❌ MySQL JDBC Driver not found.");
            throw new RuntimeException("MySQL JDBC Driver not found.", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    //  method to check if username exists
    public static boolean isUsernameTaken( String username) throws SQLException {
        String query = "SELECT COUNT(*) FROM users WHERE username = ?";
        try( Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(query))
        {    pst.setString(1, username);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0; // true if count > 0
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    // satck to store deleted profiles
    static Stack<Profile> deletedProfiles = new Stack<>();

    // New method to initialize all tables
    public static void initializeTables() {
        if (!tablescreated) {
            System.out.println("\nInitializing database tables...");
            createUsersTable();
            createAccessLogsTable();
            createPatientGeneticProfileTable();
            createClinicalNotesTable();
            tablescreated = true; // Set the flag to true after initialization
            System.out.println("Database table initialization complete.\n");
        }
    }

    //     creates user table
    public static void createUsersTable() {
        String createTable = "CREATE TABLE IF NOT EXISTS users ("
                + "id INT AUTO_INCREMENT PRIMARY KEY ,"
                + "username VARCHAR(100) NOT NULL ,"
                + "password VARCHAR(255) NOT NULL,"
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

    //   method to create the accessLogs table
    public static void createAccessLogsTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS AccessLogs ("
                + "logID INT AUTO_INCREMENT PRIMARY KEY,"
                + "userID INT,"
                + "Username VARCHAR(100) NOT NULL,"
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
            System.out.println("❌ Error creating 'SystemData' table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // method to create a patient genetic profile
    public static void createPatientGeneticProfileTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS patientGeneticProfile ("
                + "profileID INT AUTO_INCREMENT PRIMARY KEY,"
                + "patientName VARCHAR(100) NOT NULL,"
                + "dob text NOT NULL DEFAULT '1990-03-23',"
                + "dnaSequence TEXT,"
                + "diseaseMarkers TEXT,"
                + "doctorID INT,"
                +"DoctorName VARCHAR(100) NOT NULL,"
                +"Photo Blob"
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

    // Method to create the clinicalNotes table
    public static void createClinicalNotesTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS clinicalNotes ("
                + "noteID INT AUTO_INCREMENT PRIMARY KEY," // Unique ID for each note
                + "profileID INT NOT NULL," // References patientGeneticProfile
                + "noteText TEXT NOT NULL,"
                + "noteDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP," // Renamed 'date' to 'noteDate' to avoid keyword conflict
                + "doctorID INT NOT NULL," // References user table
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

        String sql = "{CALL insertUser(?, ?, ?)}";
        try (Connection conn = getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setString(1, username);
            cstmt.setString(2, password);
            cstmt.setString(3, role);

            int rowsAffected = cstmt.executeUpdate();

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
        System.out.println("\n--- 🗑 Delete User ---");
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
            //  DBConnection.logAction(User.id , "Admin deleted user with id :   "+identifier);


        } else if (deleteOption.equals("username")) {
            System.out.print("Enter the username of the user to delete: ");
            String username = sc.next().trim();
            identifier = username;
            identifierType = "Username";
            checkSql = "SELECT id, username FROM users WHERE username = ?";
            deleteSql = "DELETE FROM users WHERE username = ?";
            //  DBConnection.logAction(User.id , " Admin deleted  " + username+ " user from database.");
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
                //DBConnection.logAction(User.id ,"Doctor searched profile with id : "+ searchValue);
                break;
            case 2:
                System.out.print("Enter Patient Name (or part of it): ");
                searchValue = "%" + sc.nextLine().trim() + "%";
                sql = "SELECT profileID, patientName, dob, dnaSequence, diseaseMarkers, doctorID FROM patientGeneticProfile WHERE patientName LIKE ?";
                searchCriterion = "Patient Name";
                //  DBConnection.logAction(User.id ,"Doctor searched profile with id : "+ searchValue);
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
                //  DBConnection.logAction(User.id ,"Doctor with id "+searchValue+" searched profiles");
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

            System.out.println("\n══════════════════════════════════════════════════════════════════════════════════════════════════════");
            System.out.println("             🔎 Search Results for " + searchCriterion + ": '" + searchValue + "'");
            System.out.println("══════════════════════════════════════════════════════════════════════════════════════════════════════");
            System.out.printf("%-10s %-25s %-12s %-10s %-20s %-10s\n", "Profile ID", "Patient Name", "DOB", "Doctor ID", "DNA ", "Markers ");
            System.out.println("-------------------------------------------------------------------------------------------------------");

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
            System.out.println("======================================================================================================\n");

        } catch (SQLException e) {
            System.err.println("❌ Database error searching profiles: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void viewDeletedProfiles() {
        System.out.println("\n═════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════");
        System.out.println("                           🗑  DELETED PROFILES 🗑");
        System.out.println("═════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════");
        System.out.printf("%-8s %-20s %-12s %-25s %-40s %-10s\n",
                "ProfID", "Patient Name", "DOB", "DNA Sequence", "Disease Markers", "DoctorID");
        System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------");

        if (deletedProfiles.isEmpty()) {
            System.out.println("⚠  No profiles deleted in this session.");
        } else {
            for (Profile profile : deletedProfiles) {
                System.out.printf("%-8s %-20s %-12s %-25s %-40s %-10s\n",
                        profile.getProfileID(),  // profileID
                        profile.getPatientName(),  // patientName
                        profile.getDob(),  // dob
                        (profile.getDnaSequence().length() > 20 ? profile.getDnaSequence().substring(0, 20) + "..." : profile.getDnaSequence()),  // dnaSequence (trim long text)
                        profile.getDiseaseMarkers(),  // diseaseMarkers
                        profile.getDoctorID()); // doctorID
            }
        }

        System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------");
    }

    public static void logAction(int userId, String username, String action) {
        String sql = "INSERT INTO AccessLogs (userID,username ,action) VALUES (?, ?,?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, username);
            pstmt.setString(3, action);
            pstmt.executeUpdate();

        } catch (Exception e) {
            System.err.println("❌ Error logging action: " + e.getMessage());
        }
    }

    public static void viewAccessLogs() {
        String sql = """
        SELECT a.logID, a.userID,u.username, a.action, a.timestamp 
        FROM AccessLogs a 
        JOIN users u ON a.userID = u.id 
        ORDER BY a.timestamp DESC
        """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            System.out.println("\n════════════════════════════════════════════════════════════════════════════════════════════════");
            System.out.println("                                  📋 SYSTEM ACCESS LOGS 📋");
            System.out.println("════════════════════════════════════════════════════════════════════════════════════════════════");
            System.out.printf("%-8s %-8s %-15s %-40s %-20s\n", "logId", "userId", "username", "action", "timestamp");

            System.out.println("------------------------------------------------------------------------------------------------");

            boolean hasLogs = false;
            while (rs.next()) {
                hasLogs = true;
                int logID = rs.getInt("logID");
                int userid=rs.getInt("userID");
                String username = rs.getString("username");
                String action = rs.getString("action");
                Timestamp timestamp = rs.getTimestamp("timestamp");

                System.out.printf("%-8d %-8d %-15s %-40s %-20s\n",
                        logID,
                        userid,
                        (username != null ? username : "Unknown"),
                        (action.length() > 50 ? action.substring(0, 35) + "..." : action),
                        timestamp.toString());

            }

            if (!hasLogs) {
                System.out.println("⚠  No access logs found in the system.");
            }

            System.out.println("════════════════════════════════════════════════════════════════════════════════════════════════\n");

        } catch (SQLException e) {
            System.err.println("❌ Database error while viewing access logs: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ Unexpected error while viewing access logs: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void exportUsersToCSV() {
        // ⏱️ Generate timestamp for unique filename
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filePath = "D:/UsersExport_" + timestamp + ".csv";

        String query = "SELECT ID, username, role, created_at FROM users";
        // 👆 adjust column names as per your actual table

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query);
             PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {

            // Write CSV Header
            writer.println("UserID,Username,Role,CreatedAt");

            // Write Rows
            while (rs.next()) {
                writer.printf("%d,%s,%s,%s%n",
                        rs.getInt("ID"),
                        rs.getString("username"),
                        rs.getString("role"),
                        rs.getTimestamp("created_at"));
            }

            System.out.println("✅ Users exported successfully! File saved at: " + filePath);

        } catch (Exception e) {
            System.err.println("❌ Error exporting users: " + e.getMessage());
            e.printStackTrace();
        }
    }


    // 2.----------------------------DOCTOR MENU ------------------------------------

    public static void AddNewPatient(String pname ,String dob, String dnaSequence,String diseaseMarkers ,int doctorID,FileInputStream fis) {

        String sql="{CALL insertPatient(?, ?, ?, ?, ?, ?, ?)}";

        try(Connection conn=getConnection();
            CallableStatement cst=conn.prepareCall(sql))
        {
            cst.setString(1, pname);
            cst.setString(2, dob);
            cst.setString(3, dnaSequence);
            cst.setString(4,diseaseMarkers);
            cst.setInt(5, doctorID);
            cst.setString(6, User.username);


            if (fis != null) {
                cst.setBinaryStream(7, fis);
            } else {
                cst.setNull(7, java.sql.Types.BLOB);
            }

            int r=cst.executeUpdate();
            if (r > 0) {
                System.out.println(User.GREEN+"✅ NEW Patient  " + pname + " Added successfully."+User.RESET);

            } else {
                System.out.println(User.RED+"❌ Failed to add user :'" + pname + "'. No rows affected."+User.RESET);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void viewAllProfiles() {
        String sql = """
        SELECT p.profileID, p.patientName, p.dob, p.dnaSequence, 
               p.diseaseMarkers, p.doctorID, u.username AS doctorName
        FROM patientGeneticProfile p
        LEFT JOIN users u ON p.doctorID = u.id
        ORDER BY p.profileID
        """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            System.out.println("\n═════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════");
            System.out.println("                       🧬 PATIENT GENETIC PROFILES 🧬");
            System.out.println("═════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════");
            System.out.printf("%-8s %-20s %-12s %-25s %-40s %-10s %-15s\n",
                    "ProfileID", "Patient Name", "DOB", "DNA Sequence", "Disease Markers", "DocID", "DoctorName");

            System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------");

            boolean hasProfiles = false;
            while (rs.next()) {
                hasProfiles = true;

                int profileID = rs.getInt("profileID");
                String patientName = rs.getString("patientName");
                String dob = rs.getString("dob");
                String dnaSequence = rs.getString("dnaSequence");
                String diseaseMarkers = rs.getString("diseaseMarkers");
                int doctorID = rs.getInt("doctorID");
                String doctorName = rs.getString("doctorName");

                System.out.printf("%-8d %-20s %-12s %-25s %-40s %-10d %-15s\n",
                        profileID,
                        (patientName != null ? patientName : "Unknown"),
                        (dob != null ? dob : "N/A"),
                        (dnaSequence != null ? dnaSequence.substring(6, Math.min(dnaSequence.length(), 47)) + "..." : "N/A"),
                        (diseaseMarkers != null ? diseaseMarkers : "N/A"),
                        doctorID,
                        (doctorName != null ? doctorName : "Unknown"));
            }

            if (!hasProfiles) {
                System.out.println("⚠  No patient profiles found in the system.");
            }

            System.out.println("═════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════\n");

        } catch (SQLException e) {
            System.err.println("❌ Database error while viewing patient profiles: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ Unexpected error while viewing patient profiles: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static boolean analyzeDnaForRiskMarkers(int profileId) {
        String selectSQL = "SELECT dnaSequence, diseaseMarkers FROM patientGeneticProfile WHERE profileID = ?";
        String updateSQL = "UPDATE patientGeneticProfile SET diseaseMarkers = ? WHERE profileID = ?";

        System.out.println(User.BLUE+"📢 Starting DNA analysis for profile ID: "+User.RESET+User.RED + profileId+User.RESET);

        try (Connection conn = getConnection();
             PreparedStatement selectStmt = conn.prepareStatement(selectSQL)) {

            selectStmt.setInt(1, profileId);

            String dnaSequence = null;
            String existingMarkers = null;

            try (ResultSet rs = selectStmt.executeQuery()) {
                if (rs.next()) {
                    dnaSequence = rs.getString("dnaSequence");
                    existingMarkers = rs.getString("diseaseMarkers");
                    System.out.println("📄 DNA Sequence: " +User.GREEN+ dnaSequence+User.RESET);
                    System.out.println("🧬 Existing Markers: " + existingMarkers);
                } else {
                    System.out.println("❌ No patient found with profile ID: " + profileId);
                    return false;
                }
            }

            if (dnaSequence == null || dnaSequence.isEmpty()) {
                System.out.println("⚠ No DNA sequence found.");
                return false;
            }

            // Step 2: Detect markers based on DNA patterns
            Set<String> detectedMarkers = new LinkedHashSet<>();

            if (dnaSequence.contains("AGTCAGT")) detectedMarkers.add("BRCA1 – Breast Cancer risk");
            if (dnaSequence.contains("TTGACA")) detectedMarkers.add("BRCA2 – Breast Cancer risk");
            if (dnaSequence.contains("CAGTTC")) detectedMarkers.add("TP53 – Tumor Suppressor (cancer)");
            if (dnaSequence.contains("GATA")) detectedMarkers.add("APOE4 – Alzheimer’s risk");
            if (dnaSequence.contains("ATCGTG")) detectedMarkers.add("CFTR – Cystic Fibrosis");
            if (dnaSequence.contains("GGATCC")) detectedMarkers.add("HTT – Huntington’s Disease");
            if (dnaSequence.contains("CCTGA")) detectedMarkers.add("HBB – Sickle Cell Anemia");
            if (dnaSequence.contains("TGGAG")) detectedMarkers.add("FBN1 – Marfan Syndrome");
            if (dnaSequence.contains("GGGCG")) detectedMarkers.add("KRAS – Colon/Lung Cancer");
            if (dnaSequence.contains("GGAAT")) detectedMarkers.add("EGFR – Lung Cancer");
            if (dnaSequence.contains("CCACC")) detectedMarkers.add("HER2 – Breast Cancer");
            if (dnaSequence.contains("ATGCCG")) detectedMarkers.add("BCR-ABL – Leukemia");
            if (dnaSequence.contains("CTTGA")) detectedMarkers.add("MLH1 – Colon Cancer");
            if (dnaSequence.contains("TACGA")) detectedMarkers.add("MSH2 – Colon Cancer");
            if (dnaSequence.contains("ACGTT")) detectedMarkers.add("MSH6 – Colon Cancer");
            if (dnaSequence.contains("GCTAC")) detectedMarkers.add("PMS2 – Colon Cancer");
            if (dnaSequence.contains("TTCCG")) detectedMarkers.add("PTEN – Cancer risk");
            if (dnaSequence.contains("CGGTA")) detectedMarkers.add("RB1 – Retinoblastoma");
            if (dnaSequence.contains("CCGGA")) detectedMarkers.add("CDKN2A – Skin Cancer");
            if (dnaSequence.contains("TTGTT")) detectedMarkers.add("VHL – Kidney Cancer risk");
            if (dnaSequence.contains("GAGCT")) detectedMarkers.add("NF1 – Neurofibromatosis 1");
            if (dnaSequence.contains("TTAAC")) detectedMarkers.add("NF2 – Neurofibromatosis 2");
            if (dnaSequence.contains("AACCG")) detectedMarkers.add("SMN1 – Spinal Muscular Atrophy");
            if (dnaSequence.contains("CAGGA")) detectedMarkers.add("DMD – Muscular Dystrophy");
            if (dnaSequence.contains("GTACC")) detectedMarkers.add("PKD1 – Polycystic Kidney Disease");
            if (dnaSequence.contains("GGCTT")) detectedMarkers.add("PKD2 – Polycystic Kidney Disease");
            if (dnaSequence.contains("CTAGG")) detectedMarkers.add("G6PD – Anemia Deficiency");
            if (dnaSequence.contains("AAGCT")) detectedMarkers.add("PAH – Phenylketonuria");
            if (dnaSequence.contains("CCGTT")) detectedMarkers.add("ATP7B – Wilson’s Disease");
            if (dnaSequence.contains("TTCGA")) detectedMarkers.add("HFE – Hemochromatosis");
            if (dnaSequence.contains("CGTAC")) detectedMarkers.add("LCT – Lactose Intolerance");
            if (dnaSequence.contains("TGACC")) detectedMarkers.add("FTO – Obesity Risk");
            if (dnaSequence.contains("GGCTC")) detectedMarkers.add("ACE – Hypertension");
            if (dnaSequence.contains("ATCCG")) detectedMarkers.add("CYP2C9 – Warfarin Sensitivity");
            if (dnaSequence.contains("AGGTC")) detectedMarkers.add("VKORC1 – Warfarin Sensitivity");
            if (dnaSequence.contains("CCTCG")) detectedMarkers.add("CYP2D6 – Drug Metabolism");
            if (dnaSequence.contains("TGGCC")) detectedMarkers.add("MT-RNR1 – Hearing Loss (antibiotics)");
            if (dnaSequence.contains("AGGCC")) detectedMarkers.add("MTHFR – Folate/Heart risk");
            if (dnaSequence.contains("GATCC")) detectedMarkers.add("ALDH2 – Alcohol Sensitivity");
            if (dnaSequence.contains("CCGCGG")) detectedMarkers.add("TPMT – Drug Sensitivity");

            System.out.println("🔍 Detected Markers in DNA: " +User.RED +detectedMarkers+User.RESET);

            if (detectedMarkers.isEmpty()) {
                System.out.println(User.GREEN+" No new disease markers found."+User.RESET);
                return true;
            }

            // Step 3: Merge existing markers safely
            if (existingMarkers != null && !existingMarkers.trim().isEmpty()) {
                if (existingMarkers.startsWith("[") && existingMarkers.endsWith("]")) {
                    // Format: ["BRCA1", "TP53"]
                    String trimmed = existingMarkers.substring(1, existingMarkers.length() - 1); // remove [ ]
                    String[] existingArray = trimmed.split(",");
                    for (String m : existingArray) {
                        String clean = m.trim().replaceAll("\"", "");
                        if (!clean.isEmpty()) {
                            detectedMarkers.add(clean);
                        }
                    }
                } else {
                    // Format: plain text like BRCA1,TP53
                    String[] existingArray = existingMarkers.split(",");
                    for (String m : existingArray) {
                        String clean = m.trim();
                        if (!clean.isEmpty()) {
                            detectedMarkers.add(clean);
                        }
                    }
                }
            }

            // Step 4: Convert back to string format
            StringBuilder finalMarkers = new StringBuilder("[");
            int count = 0;
            for (String marker : detectedMarkers) {
                if (count > 0) finalMarkers.append(", ");
                finalMarkers.append("\"").append(marker).append("\"");
                count++;
            }
            finalMarkers.append("]");

            // Step 5: Update table
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSQL)) {
                updateStmt.setString(1, finalMarkers.toString());
                updateStmt.setInt(2, profileId);

                int updated = updateStmt.executeUpdate();
                if (updated > 0) {
                    System.out.println(User.GREEN + "✅ Disease markers updated to: " + finalMarkers+User.RESET);
                    return true;
                } else {
                    System.out.println("⚠ No update occurred.");
                    return false;
                }
            }

        } catch (Exception e) {
            System.err.println("❌ Exception during analysis: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static void addClinicalNote(int profileID, String noteText, int doctorID) {
        String insertSQL = "{CALL insertClinicalNotes(?, ?, ?)}";

        try (Connection conn = getConnection();
             CallableStatement cstmt = conn.prepareCall(insertSQL))
        {
            cstmt.setInt(1, profileID);
            cstmt.setString(2, noteText);
            cstmt.setInt(3, doctorID);

            int rowsAffected = cstmt.executeUpdate();

            if (rowsAffected > 0) {System.out.println(User.GREEN+"✅ Clinical note added successfully for profileID: "+User.RESET + User.BLUE+ profileID+User.RESET);}
            else {System.out.println(User.RED+"❌ Failed to add clinical note for profileID: "+User.BLUE + profileID+User.RESET + ". No rows affected.");}

        } catch (SQLException e) {
            System.err.println("❌ Database error adding clinical note: " + e.getMessage());
            e.printStackTrace();

        } catch (Exception e) {
            System.err.println("❌ An unexpected error occurred while adding clinical note: " + e.getMessage());
            e.printStackTrace();

        }
    }

    public static void viewClinicalNotes() {
        String sql = """
        SELECT c.noteID, c.profileID, c.noteText, c.noteDate, c.doctorID, u.username AS doctorName
        FROM clinicalnotes c
        LEFT JOIN users u ON c.doctorID = u.id
        ORDER BY c.noteDate DESC
        """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            System.out.println("\n═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════");
            System.out.println("                         📝 CLINICAL NOTES 📝");
            System.out.println("═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════");
            System.out.printf("%-8s %-10s %-60s %-20s %-8s %-15s\n",
                    "NoteID", "ProfileID", "Note Text", "Note Date", "DocID", "Doctor");

            System.out.println("-----------------------------------------------------------------------------------------------------------------------");

            boolean hasNotes = false;
            while (rs.next()) {
                hasNotes = true;

                int noteID = rs.getInt("noteID");
                int profileID = rs.getInt("profileID");
                String noteText = rs.getString("noteText");
                Timestamp noteDate = rs.getTimestamp("noteDate");
                int doctorID = rs.getInt("doctorID");
                String doctorName = rs.getString("doctorName");

                System.out.printf("%-8d %-10d %-60s %-20s %-8d %-15s\n",
                        noteID,
                        profileID,
                        (noteText != null ? (noteText.length() > 57 ? noteText.substring(0, 54) + "..." : noteText) : "N/A"),
                        (noteDate != null ? noteDate.toString() : "N/A"),
                        doctorID,
                        (doctorName != null ? doctorName : "Unknown"));
            }

            if (!hasNotes) {
                System.out.println("⚠  No clinical notes found in the system.");
            }

            System.out.println("═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════\n");

        } catch (SQLException e) {
            System.err.println("❌ Database error while viewing clinical notes: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("❌ Unexpected error while viewing clinical notes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void searchPatientProfilesbyDisease(Scanner sc) {
        System.out.println("\n--- 🔍 Search Patient Genetic Profiles ---");
        System.out.println();
        System.out.println("Search by:");
        System.out.println("1. Profile ID");
        System.out.println("2. Patient Name");
        System.out.println("3. Disease");
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
        String searchCriteria = "";

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
                sql = "SELECT p.profileID, p.patientName, p.dob, p.dnaSequence, \n" +
                        "               p.diseaseMarkers, p.doctorID, u.username AS doctorName\n" +
                        "        FROM patientGeneticProfile p\n" +
                        "        LEFT JOIN users u ON p.doctorID = u.id\n" +
                        "          Where profileid=? ORDER BY p.profileID";
                searchCriteria = "Profile ID";

                break;
            case 2:
                System.out.print("Enter Patient Name (or part of it): ");
                searchValue = "%" + sc.nextLine().trim() + "%";

                sql = "SELECT p.profileID, p.patientName, p.dob, p.dnaSequence, \n" +
                        "               p.diseaseMarkers, p.doctorID, u.username AS doctorName\n" +
                        "        FROM patientGeneticProfile p\n" +
                        "         JOIN users u ON p.doctorID = u.id\n" +
                        "       WHERE patientName LIKE ? ORDER BY p.profileID";
                searchCriteria = "Patient Name";

                break;
            case 3:
                System.out.print("Enter Disease name: ");
                if (!sc.hasNextLine()) {
                    System.out.println("❌ Invalid input. Please enter a disease name.");
                    return;
                }
                searchValue = sc.nextLine().trim();

                sql = "SELECT p.profileID, p.patientName, p.dob, p.dnaSequence, \n" +
                        "               p.diseaseMarkers, p.doctorID, u.username AS doctorName\n" +
                        "        FROM patientGeneticProfile p\n" +
                        "         JOIN users u ON p.doctorID = u.id\n" +
                        "      WHERE diseaseMarkers LIKE ?  ORDER BY p.profileID  ";

                searchValue = "%" + searchValue + "%";
                searchCriteria = "Disease";
                break;
            default:
                System.out.println("❌ Invalid choice. Please select 1, 2, or 3.");
                return;
        }

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (searchValue instanceof Integer) {
                pstmt.setInt(1, (Integer) searchValue);
            } else if (searchValue instanceof String) {
                pstmt.setString(1, (String) searchValue);
            }

            ResultSet rs = pstmt.executeQuery();

            System.out.println("\n═════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════");
            System.out.println("             🔎 Search Results for " + searchCriteria + ": '" + searchValue + "'");
            System.out.println("═════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════");
            System.out.printf("%-8s %-20s %-12s %-25s %-40s %-10s %-15s\n",
                    "ProfileID", "Patient Name", "DOB", "DNA Sequence", "Disease Markers", "DocID", "DoctorName");
            System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------");

            if (!rs.isBeforeFirst()) {
                System.out.println("No profiles found matching your criteria.");
            }
            else
            {
                while (rs.next()) {
                    int profileID = rs.getInt("profileID");
                    String patientName = rs.getString("patientName");
                    String dob = rs.getString("dob");
                    String dnaSequence = rs.getString("dnaSequence");
                    String diseaseMarkers = rs.getString("diseaseMarkers");
                    int doctorID = rs.getInt("doctorID");
                    String doctorName = rs.getString("doctorName");

                    System.out.printf("%-8d %-20s %-12s %-25s %-40s %-10d %-15s\n",
                            profileID,
                            (patientName != null ? patientName : "Unknown"),
                            (dob != null ? dob : "N/A"),
                            (dnaSequence != null ? dnaSequence.substring(6, Math.min(dnaSequence.length(), 47)) + "..." : "N/A"),
                            (diseaseMarkers != null ? diseaseMarkers : "N/A"),
                            doctorID,
                            (doctorName != null ? doctorName : "Unknown"));
                }

            }
            System.out.println("═════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════");

        } catch (SQLException e) {
            System.err.println("❌ Database error searching profiles: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }


    // Delete Patient Profile by ID
    public static void deleteProfile(int profileID) {
        String checksql = "SELECT profileID, patientName, dob, dnaSequence, diseaseMarkers, doctorID " +
                "FROM patientgeneticprofile WHERE profileID = ?";
        String deletesql = "DELETE FROM patientgeneticprofile WHERE profileID = ?";
        String deleteClinicalNotes = "DELETE FROM clinicalnotes WHERE profileID = ?";

        try (Connection conn = getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checksql)) {

            // Step 1: Check if profile exists
            checkStmt.setInt(1, profileID);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (!rs.next()) {
                    System.out.println(User.YELLOW+"⚠ No patient profile found with ID: " + profileID+User.RESET);
                    return;
                }

                // Save details before deletion (for Undo)
                Profile deletedProfile = new Profile(
                        rs.getInt("profileID"),
                        rs.getString("patientName"),
                        rs.getString("dob"),
                        rs.getString("dnaSequence"),
                        rs.getString("diseaseMarkers"),
                        rs.getInt("doctorID")
                );

                String patientName = rs.getString("patientName");

                // Step 2: Delete dependent clinical notes first
                try (PreparedStatement delNotes = conn.prepareStatement(deleteClinicalNotes)) {
                    delNotes.setInt(1, profileID);
                    delNotes.executeUpdate();
                }

                // Step 3: Delete patient profile
                try (PreparedStatement deleteStmt = conn.prepareStatement(deletesql)) {
                    deleteStmt.setInt(1, profileID);
                    int rows = deleteStmt.executeUpdate();

                    if (rows > 0) {
                        deletedProfiles.push(deletedProfile); // ✅ Push into stack
                        System.out.println("🗑 Patient profile (" + patientName +
                                ") with ID " + User.BLUE+profileID+User.RESET + " deleted successfully.");
                    } else {
                        System.out.println("❌ Failed to delete profile with ID: " + profileID);
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("❌ Database error while deleting patient profile: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Undo Delete (Restore from  Stack)
    public static void undoDeleteProfile() {
        if (deletedProfiles.isEmpty()) {
            System.out.println(User.YELLOW+"⚠ No deleted profiles to restore."+User.RESET);
            return;
        }

        // Pop the last deleted profile (LIFO)
        Profile restoredProfile = deletedProfiles.pop();

        String sql = "INSERT INTO patientgeneticprofile " +
                "(profileID, patientName, dob, dnaSequence, diseaseMarkers, doctorID) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, restoredProfile.getProfileID());
            pst.setString(2, restoredProfile.getPatientName());
            pst.setString(3, restoredProfile.getDob());
            pst.setString(4, restoredProfile.getDnaSequence());
            pst.setString(5, restoredProfile.getDiseaseMarkers());
            pst.setInt(6, restoredProfile.getDoctorID());

            int rows = pst.executeUpdate();
            if (rows > 0) {
                System.out.println("✅ Restoring  deleted patient profile (" + restoredProfile.getPatientName() +
                        ") with ID " + restoredProfile.getProfileID() + ".");

                try{
                    Thread.sleep(2000);
                    System.out.println(User.GREEN+"✅ Restored deleted profils Successfully.."+User.RESET);
                }catch(Exception e){
                    System.out.println(" error " + e.getMessage());
                }
            } else {
                System.out.println(User.RED+"❌ Failed to restore deleted profile."+User.RESET);
            }

        } catch (SQLException e) {
            System.err.println("❌ Database error while restoring patient profile: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ Unexpected error while restoring patient profile: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Export profiles as CSV
    public static void exportProfilesToCSV() {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filePath = "D:/GeneticProfilesExport_" + timestamp + ".csv"; // 📂 Save to D drive
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT profileID, patientName, dob, dnaSequence, diseaseMarkers, doctorID FROM patientGeneticProfile");
             PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {


            writer.println("ProfileID,PatientName,DOB,DNASequence,DiseaseMarkers,DoctorID");


            while (rs.next()) {
                writer.printf("%d,%s,%s,%s,%s,%d%n",
                        rs.getInt("profileID"),
                        rs.getString("patientName"),
                        rs.getString("dob"),
                        rs.getString("dnaSequence").replace(",", ";"),
                        rs.getString("diseaseMarkers").replace(",", ";"),
                        rs.getInt("doctorID"));
            }

            System.out.println(User.GREEN+"✅ Export completed! File saved at: "+User.RESET +User.BLUE + filePath+User.RESET);

        } catch (Exception e) {
            System.err.println("❌ Error exporting data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 3 . -------------STUDENT MENU-----------------------------------------------------

    private static final Scanner sc = new Scanner(System.in);
    private static final Random random = new Random();

    public  static final Map<Character, String> baseColors = new HashMap<>();
    static {
        baseColors.put('A', "\u001B[31mA\u001B[0m"); // Red
        baseColors.put('T', "\u001B[34mT\u001B[0m"); // Blue
        baseColors.put('C', "\u001B[32mC\u001B[0m"); // Green
        baseColors.put('G', "\u001B[33mG\u001B[0m"); // Yellow
    }

    // 1. DNA Sequence Viewer (uses LinkedList)
    public static void dnaSequenceViewer(String sequence) {
        LinkedList<Character> dnaList = new LinkedList<>();
        for (char base : sequence.toCharArray()) dnaList.add(base);

        System.out.println("\n  ---   DNA  in Sequence:   ---");
        for (char base : dnaList) {
            System.out.print(baseColors.getOrDefault(base, String.valueOf(base)) + " ");
        }
        System.out.println("\n");
    }

    public static void dnaHelixViewer(String sequence) {
        // ANSI color codes
        final String RESET = "\u001B[0m";
        final String RED = "\u001B[31m";
        final String YELLOW = "\u001B[33m";
        final String BLUE = "\u001B[34m";
        final String GREEN = "\u001B[32m";

        System.out.println("\n  ---   🌀 DNA in Helix Style    ---\n");

        String[] helix = {
                "    %s --- %s",
                "   %s     %s",
                " %s         %s"
        };

        int i = 0;

        for (char base : sequence.toUpperCase().toCharArray()) {
            String pair;
            switch (base) {
                case 'A': pair = "T"; break;
                case 'T': pair = "A"; break;
                case 'G': pair = "C"; break;
                case 'C': pair = "G"; break;
                default: pair = "?";
            }

            // Colorize base and its pair
            String colorBase = colorize(base, RED, YELLOW, BLUE, GREEN, RESET);
            String colorPair = colorize(pair.charAt(0), RED, YELLOW, BLUE, GREEN, RESET);

            System.out.printf(helix[i % 3] + "\n", colorBase, colorPair);
            i++;
        }

        System.out.println();
    }

    private static String colorize(char base, String red, String yellow, String blue, String green, String reset) {
        switch (base) {
            case 'A': return red + "A" + reset;
            case 'T': return yellow + "T" + reset;
            case 'G': return blue + "G" + reset;
            case 'C': return green + "C" + reset;
            default: return base + "";
        }
    }


    // 2. GC Content Calculator (uses Array)
    public static void gcContentCalculator(String sequence) {
        char[] dnaArray = sequence.toCharArray();
        int gcCount = 0;

        for (char base : dnaArray) {
            if (base == 'G' || base == 'C') gcCount++;
        }

        double gcContent = (dnaArray.length > 0) ?
                (gcCount * 100.0 / dnaArray.length) : 0;

        System.out.printf("GC Content: %.2f%%\n\n", gcContent);
    }

    // 3. Find a Pattern in DNA (uses ArrayList)
    public static void findPattern(String sequence, String pattern) {
        ArrayList<Character> seqList = new ArrayList<>();
        for (char base : sequence.toCharArray()) seqList.add(base);

        StringBuilder seqBuilder = new StringBuilder();
        for (char base : seqList) seqBuilder.append(base);

        int index = seqBuilder.toString().indexOf(pattern);
        if (index != -1) {
            System.out.println("Pattern found at position: " + index + "\n");
        } else {
            System.out.println("Pattern not found.\n");
        }
    }

    // 4. Reverse & Complement Generator (uses Stack + HashMap)
    public static void reverseAndComplement(String sequence) {
        Stack<Character> stack = new Stack<>();
        Map<Character, Character> complement = new HashMap<>();
        complement.put('A', 'T');
        complement.put('T', 'A');
        complement.put('C', 'G');
        complement.put('G', 'C');

        for (char base : sequence.toCharArray()) stack.push(base);

        StringBuilder revComp = new StringBuilder();
        while (!stack.isEmpty()) {
            char base = stack.pop();
            revComp.append(complement.getOrDefault(base, 'N'));
        }

        System.out.println("Reverse Complement: " + revComp + "\n");
    }

    // 5. DNA Fun Facts (uses Queue)
    public static void dnaFunFacts() {

        Queue<String> factsQueue = new LinkedList<>();

        final String RESET = "\u001B[0m";
        final String RED = "\u001B[31m";
        final String YELLOW = "\u001B[33m";
        final String BLUE = "\u001B[34m";
        final String GREEN = "\u001B[32m";
        final String CYAN = "\u001B[36m";
        final String PURPLE = "\u001B[35m";

        factsQueue.add(RED + "DNA is made of only 4 bases: A, T, G, and C." + RESET);
        factsQueue.add(YELLOW + "You share about 60% of your DNA with bananas!" + RESET);
        factsQueue.add(GREEN + "The human genome has about 3 billion base pairs." + RESET);
        factsQueue.add(BLUE + "If stretched out, your DNA would be about 2 meters long." + RESET);
        factsQueue.add(CYAN + "Every cell in your body contains the same DNA." + RESET);
        factsQueue.add(PURPLE + "DNA is universal – found in all living things." + RESET);
        factsQueue.add(RED + "If stretched, your DNA could reach the Sun and back 600 times!" + RESET);
        factsQueue.add(YELLOW + "Humans share about 60% of their DNA with bananas." + RESET);
        factsQueue.add(BLUE + "1 gram of DNA can store ~215 petabytes of data." + RESET);
        factsQueue.add(GREEN + "Only 1.5% of human DNA codes for proteins – the rest is regulatory or 'junk'." + RESET);
        factsQueue.add(PURPLE + "DNA can survive for thousands of years in the right conditions." + RESET);
        factsQueue.add(CYAN + "DNA replicates at a speed of ~50 nucleotides per second." + RESET);
        factsQueue.add(YELLOW + "When extracted, DNA looks like a white, stringy substance." + RESET);
        factsQueue.add(RED + "The double helix structure was discovered in 1953." + RESET);


        // Rotate facts randomly
        int skip = random.nextInt(factsQueue.size());
        for (int i = 0; i < skip; i++) {
            factsQueue.add(factsQueue.poll());
        }
        System.out.println("Fun Fact: " + factsQueue.peek() + "\n");
    }
}