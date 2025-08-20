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

    public void setProfileID(int profileID) {
        this.profileID = profileID;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getDnaSequence() {
        return dnaSequence;
    }

    public void setDnaSequence(String dnaSequence) {
        this.dnaSequence = dnaSequence;
    }

    public String getDiseaseMarkers() {
        return diseaseMarkers;
    }

    public void setDiseaseMarkers(String diseaseMarkers) {
        this.diseaseMarkers = diseaseMarkers;
    }

    public int getDoctorID() {
        return doctorID;
    }

    public void setDoctorID(int doctorID) {
        this.doctorID = doctorID;
    }
}

