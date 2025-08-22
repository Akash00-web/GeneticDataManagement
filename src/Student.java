import java.sql.SQLException;
import java.util.Scanner;

public class Student extends RoleHandler{


    public  void actions() {
        System.out.println("""
            --- 🎓 Student Practice Menu ---
        
           1. DNA Sequence Viewer(Helix Style)
           2. GC Content Calculator
           3. Find a Pattern in DNA
           4. Reverse & Complement Generator
           5. DNA Fun Facts
           6. Logout (Return to Main Menu)
        
        """);
    }

    public boolean handleSession(User user, int userid , String username, Scanner sc) throws SQLException {

       int choice;
        do {
            System.out.println();
            actions();

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
                default: System.out.println(User.RED+"❌ Invalid choice. Please try again.\n"+User.RESET);
                    break;
            }
        } while (choice != 6);
        return false;
    }


}
