//import java.sql.*;
//import java.util.*;
//import java.util.Scanner;
//
//// ---------- DB Connection ----------
//class DBConnection {
//    private static final String URL = "jdbc:mysql://localhost:3306/project";
//    private static final String USER = "root"; // Change as needed
//    private static final String PASSWORD = ""; // Change as needed
//
//    public static Connection getConnection() throws SQLException {
//        try {
//            Class.forName("com.mysql.cj.jdbc.Driver"); // Load MySQL JDBC Driver
//            System.out.println("Connecting to database...");
//        } catch (ClassNotFoundException e) {
//            System.out.println("❌ MySQL JDBC Driver not found.");
//        }
//        return DriverManager.getConnection(URL, USER, PASSWORD);
//    }
//}
////  --- main ---//
//public class GenDM {
//    public static void main(String[] args) {
//        Scanner sc = new Scanner(System.in);
//        User user = new User();
//        user.login();
//
//        while (true) {
//            RoleHandler.showMenuByRole(user.role);
//            int choice = user.getChoice();
//            RoleHandler.handleChoiceByRole(user.role, choice);
//
//        }
//    }
//
//
//
//static class User {
//    public String name;
//    public String role;
//
//    // Simulate sending OTP and verifying
//    public  boolean verifyOTP() {
//        Random rand = new Random();
//        int otp = 1000 + rand.nextInt(9000);  // 4-digit OTP
//
//        System.out.println("📩 Your OTP is: " + otp);
//
//        Scanner sc = new Scanner(System.in);
//        int attempts = 3;
//
//        while (attempts > 0) {
//            System.out.print("🔐 Enter OTP: ");
//            int enteredOtp = sc.nextInt();
//            if (enteredOtp == otp) {
//                System.out.println("✅ OTP verified.");
//                System.out.println("✅ Logged in as " + role);
//                return true;
//            } else {
//                attempts--;
//                System.out.println("❌ Incorrect OTP. Attempts left: " + attempts);
//            }
//        }
//
//        System.out.println("⛔ Too many wrong attempts. Exiting...");
//        return false;
//    }
//
//    // Updated login() method
//    public void login() {
//        Scanner sc = new Scanner(System.in);
//
//        System.out.println("""
//                    ══════════════════════════════════════════════════════
//                     🔬✨   Welcome to the Future of Genetics  ✨🔬
//
//                         🧬  GENETIC DATA MANAGEMENT SYSTEM  🧬
//
//                    ══════════════════════════════════════════════════════
//
//
//
//            Select your role (press only numbers):
//            1. Admin 🔐
//            2. Doctor 👨‍⚕️
//            3. Student 🎓
//            4. Logout
//        """);
//
//        int choice = sc.nextInt();
//        switch (choice) {
//            case 1: role = "Admin"; break;
//            case 2: role = "Doctor"; break;
//            case 3: role = "Student"; break;
//            case 4: System.exit(0); break;
//            default: role = "unknown"; break;
//        }
//
//
//    }
//
//    public int getChoice() {
//        Scanner sc = new Scanner(System.in);
//        System.out.print("Enter menu choice: ");
//        return sc.nextInt();
//    }
//}
//
//// ---------- Role Handler ----------
//class RoleHandler {
//    public static void showMenuByRole(String role) {
//        switch (role) {
//            case "Admin" : AdminActions();break;
//            case "Doctor" :DoctorActions();break;
//            case "Student" : StudentActions();break;
//            default : System.out.println("❌ No valid role.");
//            break;
//        }
//    }
//
//    public static void handleChoiceByRole(String role, int choice) {
//        Scanner sc = new Scanner(System.in);
//
//        switch (role) {
//            case "Admin": {
//                do {
//                    AdminActions();
//                    System.out.print("Enter your choice: ");
//                    choice = sc.nextInt();
//
//                    switch (choice) {
//                        case 1 : System.out.println("1. View all users");break;
//                        case 2 : System.out.println("2. Add new user");break;
//                        case 3 : System.out.println("3. Delete user");break;
//                        case 4 : System.out.println("4. View all profiles");break;
//                        case 5 : System.out.println("5. Delete genetic profile");break;
//                        case 6 : System.out.println("6. View access logs");break;
//                        case 7 : System.out.println("7. Backup data");break;
//                        case 8 : System.out.println("🔒 Logged out from Admin panel.");break;
//                        default : System.out.println("❌ Invalid choice.");break;
//                    }
//                } while (choice != 8);
//                break;
//            }
//
//            case "Doctor": {
//                do {
//                    DoctorActions();
//                    System.out.print("Enter your choice: ");
//                    choice = sc.nextInt();
//
//                    switch (choice) {
//                        case 1 : System.out.println("1. Create new patient genetic profile");break;
//                        case 2 : System.out.println("2. View genetic profile");break;
//                        case 3 : System.out.println("3. Analyze DNA for risk markers");break;
//                        case 4 : System.out.println("4. Recommend treatment");break;
//                        case 5 : System.out.println("5. Add clinical notes");break;
//                        case 6 : System.out.println("6. Search profiles");break;
//                        case 7 : System.out.println("7. View analysis history");break;
//                        case 8 : System.out.println("🔒 Logged out from Doctor panel.");break;
//                        default : System.out.println("❌ Invalid choice.");break;
//                    }
//                } while (choice != 8);
//                break;
//            }
//
//            case "Student": {
//                do {
//                    StudentActions();
//                    System.out.print("Enter your choice: ");
//                    choice = sc.nextInt();
//
//                    switch (choice) {
//                        case 1 :System.out.println("1. Learn about DNA structure");break;
//                        case 2 : System.out.println("2. View sample sequences");break;
//                        case 3 : System.out.println("3. Take a quiz");break;
//                        case 4 : System.out.println("4. Fun facts about DNA");break;
//                        case 5 : System.out.println("🔒 Logged out from Student panel.");break;
//                        default : System.out.println("❌ Invalid choice.");break;
//
//                    }
//                } while (choice != 5);
//                break;
//            }
////
////            case "Logout":
////                System.out.println(" Exiting to  main menu...");
////                System.exit(0);
////                break;
//
//            default:
//                System.out.println("❌ Unknown role.");
//                break;
//        }
//    }
//
//
//    public static void DoctorActions() {
//        System.out.println("""
//            --- Doctor Menu ---
//          1. create new patient genetic profile
//          2. view genetic profile
//          3. analyze patient DNA for Risk markers
//          4. recommend treatment
//          5. add clinical notes
//          6. search profiles (By name , disease ,Id)
//          7. view analysis history
//          8-->  Logout
//        """);
//    }
//    public static void StudentActions() {
//        System.out.println("""
//            --- Student Practice Menu ---
//            1. learn about DNA structure
//            2. view sample sequences
//            3. play quiz
//            4. facts about DNA
//            5--> Logout
//        """);
//    }
//
//    public static void AdminActions() {
//        System.out.println("""
//            --- Admin Panel ---
//            1. view all users
//            2. add new user
//            3. delete user
//            4. view all profiles
//            5. delete genetic profile
//            6. view access logs
//            7. backup logs
//            8 --> Logout
//        """);
//    }
//}
//
//}
//
//
