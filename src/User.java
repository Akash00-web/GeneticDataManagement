import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Random;
import java.util.Scanner;

public  class User extends GDMS{
    public static String role;
    public static int id;
    public static String username;
    public static int userid;


    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
   public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    /**
     * Displays role selection and handles login for Doctor/Admin/Student.
     * Returns true if the user wants to exit the app.
     */
    public boolean selectRole(Scanner sc)  {
        System.out.println("""


                ══════════════════════════════════════════════════════════════
                                                                              
                     🧬   GENETIC DATA MANAGEMENT SYSTEM  🧬   
                                                                             
                ══════════════════════════════════════════════════════════════

                  🌍 Secure   |       📊 Organized     |         ⚡ Fast 

                ────────────────────────────────────────────────────────────────
                Please select your role to continue (press only numbers):
                
                    1️⃣  Admin      – Manage Users & System
                    2️⃣  Doctor     – Access & Update Genetic Profiles
                    3️⃣  Student    – Explore DNA Tools & Learning
                    4️⃣  Exit       
                ────────────────────────────────────────────────────────────────

                """);

        System.out.print("Enter your choice: ");

        if (!sc.hasNextInt()) {
            System.out.println(RED+"❌ Invalid input. Please enter a number."+RESET);// in  RED color
            sc.next();
            role = "unknown";
            return false;
        }

        int choice = sc.nextInt();
        sc.nextLine(); // clear buffer

        switch (choice) {
            case 1:
                role = "Admin";
                break;
            case 2:
                role = "Doctor";
                break;
            case 3:
                role = "Student";
                break;
            case 4:
                return true; // exit app
            default: {
                role = "unknown";
                System.out.println(RED+"❌ Invalid choice. Please try again."+RESET);
                return false;
            }
        }

        // Step 1: Login
        if (!loginUserFromDB(sc)) {
            System.out.println("🔄 Returning to main menu...\n");
            role = "unknown"; // mark as invalid
            return false;
        }

        // Step 2: OTP verification
        if (!verifyOTP(sc)) {
            System.out.println("🔄 Returning to main menu...\n");
            role = "unknown"; // mark as invalid
            return false;
        }

        return false; // continue to the main app
    }

    /**
     * Asks for username and password, validates from DB.
     */// in User.java
    public boolean loginUserFromDB(Scanner sc)  {
        System.out.print("👤 Enter username: ");
        String inputUsername = sc.nextLine();
        System.out.print("🔑 Enter password: ");
        String inputPassword = sc.nextLine();
        String sql = "SELECT id FROM users WHERE username = ? AND password = ? AND role = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, inputUsername);
            stmt.setString(2, inputPassword);
            stmt.setString(3, role);


            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    id = rs.getInt("id");
                    username = inputUsername;
                    userid = id;
                    return true;
                } else {
                    System.out.println(RED+"❌ Invalid username, password, or role."+RESET);
                    return false;
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Database error during login: " + e.getMessage());
            return false;
        }
    }


    /**
     * Verifies the OTP.
     */
    public boolean verifyOTP(Scanner sc) {
        Random random = new Random();
        int otp = 1000 + random.nextInt(9000);

        System.out.println("\n📩 Your One-Time Password (OTP) is: "+User.GREEN + otp+User.RESET);

        int attempts = 3;
        while (attempts > 0) {
            System.out.print("🔐 Enter OTP: ");

            if (!sc.hasNextInt()) {
                System.out.println(RED+"❌ Invalid input. Please enter numbers only."+RESET);
                sc.next();
                attempts--;
                System.out.println("   Attempts left: " + attempts);
                continue;
            }

            int enteredOtp = sc.nextInt();
            if (enteredOtp == otp) {
                System.out.println(GREEN+" ✅ OTP verified successfully."+RESET);
                System.out.println();
                System.out.println("✅ Logged in as " + role + " (" + username + ")\n " + "  Your  "+ role +" ID : "+BLUE +userid +RESET);

                //Enter log action by role

                if (role.equalsIgnoreCase("admin")) {
                    DBConnection.  logAction(userid, username, "Admin : "+username+ " logged in  into system");

                } else if (role.equalsIgnoreCase("doctor")) {
                    DBConnection.  logAction(userid, username, "Doctor "+username+" logged in into system");

                } else if (role.equalsIgnoreCase("student")) {
                    DBConnection.  logAction(userid, username, "Student : " +username+" logged in  into system");

                }
                return true;
            }
            else
            {
                attempts--;
                System.out.println(RED+"❌ Incorrect OTP. Attempts left: " + attempts+RESET);
            }
        }
        System.out.println(User.RED+"⛔ Too many wrong attempts."+RESET);
        return false;
    }
}