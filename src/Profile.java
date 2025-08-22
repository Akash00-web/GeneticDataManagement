import java.time.LocalDate;

public class Profile {
    private int profileID;
    private String patientName;
    private String dob;
    private String dnaSequence;
    private String diseaseMarkers;
    private int doctorID;

    public Profile(int profileID, String patientName, String dob, String dnaSequence, String diseaseMarkers, int doctorID) {
        this.profileID = profileID;
        this.patientName = patientName;
        this.dob = dob;
        this.dnaSequence = dnaSequence;
        this.diseaseMarkers = diseaseMarkers;
        this.doctorID = doctorID;
    }

    public int getProfileID() {
        return profileID;
    }
    public String getPatientName() {
        return patientName;
    }
    public String getDob() {
        return dob;
    }
    public String getDnaSequence() {
        return dnaSequence;
    }
    public String getDiseaseMarkers() {
        return diseaseMarkers;
    }
    public int getDoctorID() {
        return doctorID;
    }

}

