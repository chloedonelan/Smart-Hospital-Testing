package com.hms.dao;

import com.hms.entity.Doctor;
import org.junit.jupiter.api.*;

import java.nio.file.*;
import java.sql.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

// Note: this test class does not check for null parameters, as I don't have the original sql files which would contain the necessary constraints
// e.g. name not null, phone number/email follow a specific format

// other things i could test - duplicate emails, null fields, foreign key violations (but i don't know the database sql so... just making it up???)

public class DoctorDAOTest {

    private static Connection conn;
    private DoctorDAO doctorDAO;

    @BeforeAll
    public static void setupDB() throws Exception {
        conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/?allowMultiQueries=true",
                "root", "rootuser"
        );

        String sql = new String(Files.readAllBytes(Paths.get("src/test/resources/setup.sql")));

        // Execute setup
        Statement stmt = conn.createStatement();
        stmt.execute(sql);

        // Now switch connection to hospital_db
        conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/hospital_db",
                "root", "rootuser"
        );
    }

    @BeforeEach
    public void setup() throws SQLException {
        conn.setAutoCommit(false);
        doctorDAO = new DoctorDAO(conn);

        Statement stmt = conn.createStatement();
        // ignore fk constraints
        stmt.execute("SET FOREIGN_KEY_CHECKS=0");

        // reset auto-increment and clear data in tables
        stmt.execute("TRUNCATE TABLE appointment");
        stmt.execute("TRUNCATE TABLE user_details");
        stmt.execute("TRUNCATE TABLE specialist");
        stmt.execute("TRUNCATE TABLE doctor");

        stmt.execute("SET FOREIGN_KEY_CHECKS=1");
    }

    @AfterEach
    public void rollback() throws SQLException {
        conn.rollback(); // undo db changes
    }

    @AfterAll
    public static void cleanup() throws SQLException {
        if (conn != null) conn.close();
    }

//    private Doctor createDoctor(String name, String email) {
//        Doctor doc = new Doctor();
//
//        doc.setFullName(name);
//        doc.setDateOfBirth("1990-01-01");
//        doc.setEmail(email);
//        doc.setPhone("1234567890");
//        doc.setPassword("pass");
//        doc.setQualification("idk");
//        doc.setSpecialist("specialist");
//        return doc;
//    }

    private List<Doctor> insertMultipleDoctors(int count) {
        List<Doctor> doctors = new ArrayList<>();

        for (int i = 1; i <= count; i++) {
            Doctor doc = new Doctor();
            doc.setFullName("Test Doc " + i);
            doc.setDateOfBirth("1990-01-0" + i);
            doc.setQualification("qual " + i);
            doc.setSpecialist("specialist " + i);
            doc.setEmail("test" + i + "@gmail.com");
            doc.setPhone("123456789" + i);
            doc.setPassword("pw" + i);

            doctorDAO.registerDoctor(doc);
            doctors.add(doc);
        }

        return doctors;
    }


    @Test
    public void testRegisterDoctor() throws SQLException {
        Doctor doc = new Doctor();
        doc.setFullName("register doctor test name");
        doc.setDateOfBirth("1990-01-01");
        doc.setEmail("registerdoctest@gmail.com");
        doc.setPhone("1234567890");
        doc.setPassword("pass");
        doc.setQualification("idk");
        doc.setSpecialist("specialist");

        boolean b = doctorDAO.registerDoctor(doc);
        assertTrue(b);

        // maybe bad bc its a dependent test? uses getAllDoctor to check so it assumes getAllDoctor is right
//        List<Doctor> allDocs = doctorDAO.getAllDoctor();
//        assertEquals(1, allDocs.size());
    }

    @Test
    public void testGetAllDoctor() throws SQLException {
        insertMultipleDoctors(3);
        List<Doctor> docs = doctorDAO.getAllDoctor();
        assertEquals(3, docs.size());
    }

    @Test
    public void testGetDoctorByID() throws SQLException {
        List<Doctor> docs = insertMultipleDoctors(3);
        Doctor doc1 = docs.get(0);

        String sql = "SELECT id FROM doctor WHERE email = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, doc1.getEmail());
        ResultSet rs = stmt.executeQuery();

        assertTrue(rs.next());
        int id = rs.getInt("id");

        Doctor fetched = doctorDAO.getDoctorById(id);
        assertNotNull(fetched);
        assertEquals(doc1.getFullName(), fetched.getFullName());
    }

    @Test
    public void testUpdateDoctor() throws SQLException {
        List<Doctor> docs = insertMultipleDoctors(3); // Test Doc 1, Test Doc 2, Test Doc 3
        Doctor originalDoc = docs.get(0);

        // get id
        String sql = "SELECT id FROM doctor WHERE email = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, originalDoc.getEmail());
        ResultSet rs = stmt.executeQuery();
        assertTrue(rs.next());
        int id = rs.getInt("id");

        // update doctor information
        Doctor updatedDoc = new Doctor();
        updatedDoc.setId(id);
        updatedDoc.setFullName("update doctor test name");
        updatedDoc.setDateOfBirth("1990-01-01");
        updatedDoc.setQualification("newQualification");
        updatedDoc.setSpecialist("newSpecialist");
        updatedDoc.setEmail("updatedoctest@gmail.com");
        updatedDoc.setPhone("0987654321");
        updatedDoc.setPassword("updatedocpass");

        boolean b = doctorDAO.updateDoctor(updatedDoc);
        assertTrue(b);

        Doctor fetched = doctorDAO.getDoctorById(id);
        assertNotNull(fetched);
        assertEquals("update doctor test name", fetched.getFullName());
        assertEquals("1990-01-01", fetched.getDateOfBirth());
        assertEquals("newQualification", fetched.getQualification());
        assertEquals("newSpecialist", fetched.getSpecialist());
        assertEquals("updatedoctest@gmail.com", fetched.getEmail());
        assertEquals("0987654321", fetched.getPhone());
        assertEquals("updatedocpass", fetched.getPassword());
    }

    @Test
    public void testDeleteDoctorByID() throws SQLException {
        List<Doctor> docs = insertMultipleDoctors(2);

        Doctor doc1 = docs.get(0);

        String sql = "SELECT id FROM doctor WHERE email = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, doc1.getEmail());
        ResultSet rs = stmt.executeQuery();
        assertTrue(rs.next());
        int id = rs.getInt("id");

        boolean deleted = doctorDAO.deleteDoctorById(id);
        assertTrue(deleted);

        Doctor fetched = doctorDAO.getDoctorById(id);
        assertNull(fetched); // should be nothing
    }

    @Test
    public void testLoginDoctorSuccess() throws SQLException {
        Doctor doc = new Doctor();

        doc.setFullName("login success test name");
        doc.setDateOfBirth("1990-01-01");
        doc.setEmail("logintestsuccess@gmail.com");
        doc.setPhone("1234567890");
        doc.setPassword("loginsuccesspass");
        doc.setQualification("idk");
        doc.setSpecialist("specialist");
        doctorDAO.registerDoctor(doc);

        Doctor loggedInDocSuccess = doctorDAO.loginDoctor("logintestsuccess@gmail.com", "loginsuccesspass");
        assertEquals("login success test name", loggedInDocSuccess.getFullName());
    }

    @Test
    public void testLoginDoctorFail() throws SQLException {
        Doctor doc = new Doctor();

        doc.setFullName("login fail test name");
        doc.setDateOfBirth("1990-01-01");
        doc.setEmail("logintestfail@gmail.com");
        doc.setPhone("1234567890");
        doc.setPassword("loginfailedpass");
        doc.setQualification("idk");
        doc.setSpecialist("specialist");
        doctorDAO.registerDoctor(doc);

        Doctor loggedInDocFail = doctorDAO.loginDoctor("logintestfail@gmail.com", "wrongpassword");
        assertNull(loggedInDocFail);
    }

    @Test
    public void testCountTotalDoctor() throws SQLException {
        insertMultipleDoctors(9);
        int total = doctorDAO.countTotalDoctor();
        assertEquals(9, total);
    }

    @Test
    public void testCountTotalAppointment() throws SQLException {
        Statement stmt = conn.createStatement();

        stmt.executeUpdate("INSERT INTO user_details(fullName, email, password) " +
                "VALUES ('User 1','user1@gmail.com','pass')," +
                "('User 2','user2@gmail.com','pass')," +
                "('User 3','user3@gmail.com','pass')");

        stmt.executeUpdate("INSERT INTO doctor(fullName, dateOfBirth, qualification, specialist, email, phone, password) " +
                "VALUES ('Doctor 1', '1990-01-01', 'Qual1', 'Spec1', 'doc1@gmail.com', '1234567890', 'pass1')," +
                "('Doctor 2', '1990-02-02', 'Qual2', 'Spec2', 'doc2@gmail.com', '0987654321', 'pass2')");

        stmt.executeUpdate("INSERT INTO appointment(doctorId, userId, appointmentDate, status) " +
                "VALUES (1, 1, '2025-04-10', 'pending')," +
                "(1, 2, '2025-04-11', 'pending')," +
                "(2, 3, '2025-04-11', 'done')");
        int total = doctorDAO.countTotalAppointment();
        assertEquals(3, total);
    }

    @Test
    public void testCountTotalAppointmentByDoctorID() throws SQLException {
        Statement stmt = conn.createStatement();

        stmt.executeUpdate("INSERT INTO doctor(fullName, dateOfBirth, qualification, specialist, email, phone, password) " +
                "VALUES ('doc one', '1990-01-01', 'qual1', 'spec1', 'doc1@gmail.com', '9999999999', 'pass1')," +
                "('doc two', '1990-02-02', 'qual2', 'spec2', 'doc2@gmail.com', '111111111', 'pass2')");

        stmt.executeUpdate("INSERT INTO user_details(fullName, email, password) " +
                "VALUES ('test user', 'testuser@gmail.com', 'testuserpass')");

        PreparedStatement userStmt = conn.prepareStatement("SELECT id FROM user_details WHERE email='testuser@gmail.com'");
        ResultSet userRs = userStmt.executeQuery();
        assertTrue(userRs.next());
        int userId = userRs.getInt("id");

        stmt.executeUpdate("INSERT INTO appointment(doctorId, userId, appointmentDate, status) " +
                "VALUES (1, " + userId + ", '2025-04-10', 'pending')," +
                "(1, " + userId + ", '2025-04-11', 'pending')," +
                "(2, " + userId + ", '2025-04-11', 'done')");

        int total = doctorDAO.countTotalAppointment();
        assertEquals(3, total);
    }

    @Test
    public void testCountTotalUser() throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.executeUpdate("INSERT INTO user_details(fullName, email, password) " +
                "VALUES ('User A','usera@gmail.com', 'userapass')," +
                "('User B','userb@gmail.com', 'userbpass')," +
                "('User C','userc@gmail.com', 'usercpass')");

        int total = doctorDAO.countTotalUser();
        assertEquals(3, total);
    }

    @Test
    public void testCountTotalSpecialist() {
        SpecialistDAO specialistDAO = new SpecialistDAO(conn);
        specialistDAO.addSpecialist("cardiology");
        specialistDAO.addSpecialist("oncology");

        int total = doctorDAO.countTotalSpecialist();
        assertEquals(2, total);
    }

    @Test
    public void testCheckOldPassword() throws SQLException {
        Doctor doc = new Doctor();

        doc.setFullName("check old password test name");
        doc.setDateOfBirth("1990-01-01");
        doc.setEmail("oldpwtest@gmail.com");
        doc.setPhone("1234567890");
        doc.setPassword("oldpwpass");
        doc.setQualification("idk");
        doc.setSpecialist("specialist");
        doctorDAO.registerDoctor(doc);

        PreparedStatement stmt = conn.prepareStatement("SELECT id FROM doctor WHERE email = ?");
        stmt.setString(1, doc.getEmail());
        ResultSet rs = stmt.executeQuery();
        assertTrue(rs.next());
        int docId = rs.getInt("id");

        boolean correct = doctorDAO.checkOldPassword(docId, "oldpwpass");
        assertTrue(correct);

        boolean wrong = doctorDAO.checkOldPassword(docId, "oldpwpassBad");
        assertFalse(wrong);
    }

    @Test
    public void testChangePassword() throws SQLException {
        Doctor doc = new Doctor();

        doc.setFullName("change password test name");
        doc.setDateOfBirth("1990-01-01");
        doc.setEmail("changepwtest@gmail.com");
        doc.setPhone("1234567890");
        doc.setPassword("changepwpass");
        doc.setQualification("idk");
        doc.setSpecialist("specialist");
        doctorDAO.registerDoctor(doc);

        PreparedStatement stmt = conn.prepareStatement("SELECT id FROM doctor WHERE email = ?");
        stmt.setString(1, doc.getEmail());
        ResultSet rs = stmt.executeQuery();
        assertTrue(rs.next());
        int docId = rs.getInt("id");

        doctorDAO.changePassword(docId, "changepwpassNew");

        Doctor updatedDoc = doctorDAO.getDoctorById(docId);
        assertEquals("changepwpassNew", updatedDoc.getPassword());
    }

    @Test
    public void testEditDocProfile() throws SQLException {
        Doctor doc = new Doctor();

        doc.setFullName("edit doctor test name");
        doc.setDateOfBirth("1990-01-01");
        doc.setEmail("editdoctest@gmail.com");
        doc.setPhone("1234567890");
        doc.setPassword("editdocpass");
        doc.setQualification("idk");
        doc.setSpecialist("specialist");
        doctorDAO.registerDoctor(doc);

        PreparedStatement stmt = conn.prepareStatement("SELECT id FROM doctor WHERE email = ?");
        stmt.setString(1, doc.getEmail());
        ResultSet rs = stmt.executeQuery();
        assertTrue(rs.next());
        int docId = rs.getInt("id");

        Doctor updatedDoc = new Doctor();
        updatedDoc.setId(docId);
        updatedDoc.setFullName("edited doctor test name");
        updatedDoc.setDateOfBirth("1990-02-02");
        updatedDoc.setQualification("edited qualification");
        updatedDoc.setSpecialist("edited specification");
        updatedDoc.setEmail("editeddoctest@gmail.com");
        updatedDoc.setPhone("111111111");

        boolean updated = doctorDAO.editDoctorProfile(updatedDoc);
        assertTrue(updated);

        Doctor fetched = doctorDAO.getDoctorById(docId);
        assertNotNull(fetched);
        assertEquals("edited doctor test name", fetched.getFullName()); // diff
        assertEquals("1990-02-02", fetched.getDateOfBirth()); // diff
        assertEquals("edited qualification", fetched.getQualification()); // diff
        assertEquals("edited specification", fetched.getSpecialist()); // diff
        assertEquals("editeddoctest@gmail.com", fetched.getEmail()); // diff
        assertEquals("111111111", fetched.getPhone()); // diff
        assertEquals("editdocpass", fetched.getPassword()); // same
    }
}
