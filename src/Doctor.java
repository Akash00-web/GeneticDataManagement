import java.io.File;
import java.io.FileInputStream;
import java.sql.SQLException;
import java.util.Scanner;

public class Doctor extends RoleHandler {

    public  void actions() {
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

    public boolean handleSession(User user, int userid , String username, Scanner sc) throws SQLException {
        int choice;

        do {
            System.out.println();
            actions();

            choice =getValidInt(sc, "Enter your choice: ");

            switch (choice) {
                case 1: //System.out.println("Action: Create new patient genetic profile\n");
                    System.out.println("<---   Creating New Patient Profile   --->");
                    try {
                        System.out.println("enter new patient name: ");
                        String name = sc.nextLine();

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

                        // Pass fis to DB
                        DBConnection.AddNewPatient(name, birthDate, dnaSequence, diseaseMarkers, doctorID, fis);

                        if (fis != null) fis.close(); // close stream after use

                    } catch (Exception e) {
                        System.out.println(User.RED+"⚠ An error happened.."+User.RESET);
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
                                System.out.println(User.RED+"❌ Invalid input! Please enter a valid numeric Patient ID."+User.RESET);
                                sc.nextLine(); // clear wrong input from buffer
                            }
                        }
                        while (true) {
                            try {
                                System.out.print("Enter doctor ID: ");
                                doctorID = sc.nextInt();
                                break;
                            } catch (Exception ex) {
                                System.out.println(User.RED+"❌ Invalid input! Please enter a valid numeric Doctor ID."+User.RESET);
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
                                System.out.println(User.RED+"❌ Note cannot be empty! Please enter again."+User.RESET);
                            }
                        }
                        DBConnection.addClinicalNote(patientID, note, doctorID);

                    } catch (Exception e) {
                        System.out.println(User.RED+"⚠ An error occurred while inserting note."+User.RESET);
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
                default: System.out.println(User.RED+"❌ Invalid choice. Please try again.\n"+User.RESET);
                    break;
            }
        } while (choice != 10);
return false;
    }
}
