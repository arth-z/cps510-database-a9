import java.sql.*;

/*
 * This class automatically initializes the database:
 *  Creates tables if they do NOT already exist
 *  Populates sample data only if tables are empty
 *  Safe to run on every login (no duplicates, no crashes)
 * NOTE: This class is only ran for Company, Applicant, or Recruiter roles, NOT FOR Database Admin roles
 */
public class DatabaseInitializer {

    private final DBConnection dbConnection;
    private final Connection connection;

    public DatabaseInitializer(String username, String password) throws SQLException {
        this.dbConnection = DBConnection.getInstance(username, password);
        this.connection = dbConnection.getConnection();
    }

    /* Run this method once immediately after login */
    public void initialize() throws SQLException {
        connection.setAutoCommit(false);

        try {
            createTablesIfMissing();
            populateTablesIfEmpty();
            connection.commit();
            System.out.println("Database successfully initialized.");
        } catch (SQLException ex) {
            connection.rollback();
            System.err.println("Database initialization failed: " + ex.getMessage());
            throw ex;
        }
    }

    /* Create tables only if they do not already exist */
    private void createTablesIfMissing() throws SQLException {
        createTable("COMPANY", 
            "CREATE TABLE Company (" +
            "companyID INTEGER PRIMARY KEY, " +
            "name VARCHAR(30) NOT NULL UNIQUE, " +
            "industry VARCHAR(100), " +
            "location VARCHAR(200), " +
            "email VARCHAR(100), " +
            "phone VARCHAR(20))"
        );

        createTable("RECRUITER", 
            "CREATE TABLE Recruiter (" +
            "recruiterID INTEGER PRIMARY KEY, " +
            "companyID INTEGER NOT NULL, " +
            "first_name VARCHAR(30), " +
            "last_name VARCHAR(30), " +
            "email VARCHAR(100), " +
            "phone VARCHAR(20), " +
            "FOREIGN KEY (companyID) REFERENCES Company(companyID))"
        );

        createTable("JOB", 
            "CREATE TABLE Job (" +
            "jobID INTEGER PRIMARY KEY, " +
            "companyID INTEGER NOT NULL, " +
            "recruiterID INTEGER NOT NULL, " +
            "salary FLOAT, " +
            "workingHours FLOAT, " +
            "datePosted DATE, " +
            "location VARCHAR(200), " +
            "title VARCHAR(100) NOT NULL, " +
            "description VARCHAR(500) NOT NULL, " +
            "FOREIGN KEY (companyID) REFERENCES Company(companyID), " +
            "FOREIGN KEY (recruiterID) REFERENCES Recruiter(recruiterID))"
        );

        createTable("JOBAPPLICANT", 
            "CREATE TABLE JobApplicant (" +
            "applicantID INTEGER PRIMARY KEY, " +
            "first_name VARCHAR(30) NOT NULL, " +
            "last_name VARCHAR(30), " +
            "industry VARCHAR(100), " +
            "birthdate DATE, " +
            "address VARCHAR(200), " +
            "email VARCHAR(100), " +
            "phone VARCHAR(20))"
        );

        createTable("JOBAPPLICATION", 
            "CREATE TABLE JobApplication (" +
            "jobAppID INTEGER PRIMARY KEY, " +
            "jobID INTEGER NOT NULL, " +
            "applicantID INTEGER NOT NULL, " +
            "dateTime DATE, " +
            "status VARCHAR(20), " +
            "FOREIGN KEY (jobID) REFERENCES Job(jobID), " +
            "FOREIGN KEY (applicantID) REFERENCES JobApplicant(applicantID))"
        );

        createTable("RESUME", 
            "CREATE TABLE Resume (" +
            "resumeID INTEGER PRIMARY KEY, " +
            "applicantID INTEGER NOT NULL, " +
            "uploadFile BLOB NOT NULL, " +
            "uploadDate DATE DEFAULT SYSDATE NOT NULL, " +
            "FOREIGN KEY (applicantID) REFERENCES JobApplicant(applicantID))"
        );

        createTable("INTERVIEW", 
            "CREATE TABLE Interview (" +
            "interviewID INTEGER PRIMARY KEY, " +
            "jobAppID INTEGER NOT NULL, " +
            "dateTime DATE DEFAULT SYSDATE NOT NULL, " +
            "location VARCHAR(100) NOT NULL, " +
            "FOREIGN KEY (jobAppID) REFERENCES JobApplication(jobAppID))"
        );
    }

    private void createTable(String tableName, String createSQL) throws SQLException {
        if (!tableExists(tableName)) {
            dbConnection.executeUpdate(createSQL);
            System.out.println("Created table: " + tableName);
        } else {
            System.out.println("Table already exists: " + tableName + " (skipped)");
        }
    }

    private boolean tableExists(String tableName) throws SQLException {
        String sql = 
            "SELECT COUNT(*) FROM user_tables WHERE table_name = '" + tableName.toUpperCase() + "'";
        ResultSet rs = dbConnection.executeQuery(sql);
        rs.next();
        return rs.getInt(1) > 0;
    }

    /* Populate tables only if they are empty */
    private void populateTablesIfEmpty() throws SQLException {
        /* Insert statements */
        if (isTableEmpty("Company")) { 
            dbConnection.executeUpdate("INSERT INTO Company (companyID, name, industry, location, email, phone) VALUES (1, 'Apple Canada', 'Software', '120 Bremner Boulevard Suite 1600, Toronto, Ontario, M5J 0A8', '', '647-943-4400')"); 
            dbConnection.executeUpdate("INSERT INTO Company (companyID, name, industry, location, email, phone) VALUES (2, 'Royal Bank of Canada (RBC)', 'Banking', 'Toronto, Ontario, Canada', 'recruitment@rbc.com', '1-800-769-2511')"); 
            dbConnection.executeUpdate("INSERT INTO Company (companyID, name, industry, location, email, phone) VALUES (3, 'AMD', 'Technology', 'Markham, Ontario, Canada', '', '905-882-2600')"); 
            dbConnection.executeUpdate("INSERT INTO Company (companyID, name, industry, location, email, phone) VALUES (4, 'SAMSUNG', 'Hardware', 'Vancouver, British Columbia', 'recruitment@samsung.com', '416-230-8121')");
            dbConnection.executeUpdate("UPDATE Company SET location = 'Toronto, Ontario, Canada' WHERE companyID = 1");
            dbConnection.executeUpdate("UPDATE Company SET industry = 'Technology' WHERE companyID = 1");
        }

        if (isTableEmpty("Recruiter")) { 
            dbConnection.executeUpdate("INSERT INTO Recruiter (recruiterID, companyID, first_name, last_name, email, phone) VALUES (1, 1, 'Jane', 'Doe', 'jane.doe@apple.com', '647-222-3333')");
            dbConnection.executeUpdate("INSERT INTO Recruiter (recruiterID, companyID, first_name, last_name, email, phone) VALUES (2, 2, 'Bob', 'William', 'bob.william@rbc.com', '416-111-1111')"); 
            dbConnection.executeUpdate("INSERT INTO Recruiter (recruiterID, companyID, first_name, last_name, email, phone) VALUES (3, 3, 'John', 'Daniels', 'john.daniels@amd.com', '905-423-5678')"); 
            dbConnection.executeUpdate("INSERT INTO Recruiter (recruiterID, companyID, first_name, last_name, email, phone) VALUES (4, 4, 'Jack', 'Jones', 'jack.jones@samsung.com', '647-333-4444')");         
        }

        if (isTableEmpty("Job")) { 
            dbConnection.executeUpdate("INSERT INTO Job (jobID, companyID, recruiterID, salary, workingHours, datePosted, location, title, description) VALUES (1, 1, 1, 31.50, 36.25, TO_DATE('2025-09-22', 'YYYY-MM-DD'), 'Toronto, Ontario, Canada', 'Software Engineer', 'Develop and maintain Apple software products.')");
            dbConnection.executeUpdate("INSERT INTO Job (jobID, companyID, recruiterID, salary, workingHours, datePosted, location, title, description) VALUES (2, 2, 2, 24.50, 35.00, TO_DATE('2025-09-24', 'YYYY-MM-DD'), 'Toronto, Ontario, Canada', 'Financial Analyst', 'Analyze financial data and provide insights for RBC clients.')");
            dbConnection.executeUpdate("INSERT INTO Job (jobID, companyID, recruiterID, salary, workingHours, datePosted, location, title, description) VALUES (3, 3, 3, 28.50, 40.00, TO_DATE('2025-09-26', 'YYYY-MM-DD'), 'Markham, Ontario, Canada', 'Systems Design Engineer', 'Responsible for designing, integrating, and validating complex hardware and software systems.')");
            dbConnection.executeUpdate("INSERT INTO Job (jobID, companyID, recruiterID, salary, workingHours, datePosted, location, title, description) VALUES (4, 4, 4, 25, 42, DATE '2025-09-28', 'Vancouver, British Columbia, Canada', 'Software Developer', 'Develop and maintain applications for Samsung.')");
            dbConnection.executeUpdate("INSERT INTO Job (jobID, companyID, recruiterID, salary, workingHours, datePosted, location, title, description) VALUES (5, 1, 1, 31.50, 36.25, TO_DATE('2025-09-22', 'YYYY-MM-DD'), 'Toronto, Ontario, Canada', 'Mobile Developer', 'Develop and maintain Apple mobile products and services.')");
            dbConnection.executeUpdate("INSERT INTO Job (jobID, companyID, recruiterID, salary, workingHours, datePosted, location, title, description) VALUES (6, 1, 1, 31.50, 36.25, TO_DATE('2025-10-04', 'YYYY-MM-DD'), 'Toronto, Ontario, Canada', 'Janitor', 'Mop the floors and clean the bathrooms.')");
            dbConnection.executeUpdate("INSERT INTO Job (jobID, companyID, recruiterID, salary, workingHours, datePosted, location, title, description) VALUES (7, 1, 1, 40.75, 40.00, TO_DATE('2025-10-10', 'YYYY-MM-DD'), 'Toronto, Ontario, Canada', 'Data Engineer', 'Design and maintain Apple’s data pipelines.')");
            dbConnection.executeUpdate("INSERT INTO Job (jobID, companyID, recruiterID, salary, workingHours, datePosted, location, title, description) VALUES (8, 1, 1, 29.00, 38.00, TO_DATE('2025-10-11', 'YYYY-MM-DD'), 'Toronto, Ontario, Canada', 'IT Support Specialist', 'Provide internal tech support for Apple employees.')");
            dbConnection.executeUpdate("INSERT INTO Job (jobID, companyID, recruiterID, salary, workingHours, datePosted, location, title, description) VALUES (9, 2, 2, 22.00, 35.00, TO_DATE('2025-10-08', 'YYYY-MM-DD'), 'Toronto, Ontario, Canada', 'Bank Teller', 'Assist clients with daily transactions.')");
            dbConnection.executeUpdate("INSERT INTO Job (jobID, companyID, recruiterID, salary, workingHours, datePosted, location, title, description) VALUES (10, 2, 2, 31.50, 37.50, TO_DATE('2025-10-09', 'YYYY-MM-DD'), 'Toronto, Ontario, Canada', 'Data Analyst', 'Analyze customer trends to improve banking performance.')");
            dbConnection.executeUpdate("INSERT INTO Job (jobID, companyID, recruiterID, salary, workingHours, datePosted, location, title, description) VALUES (11, 3, 3, 45.00, 40.00, TO_DATE('2025-10-06', 'YYYY-MM-DD'), 'Markham, Ontario, Canada', 'Hardware Engineer', 'Develop and test AMD hardware components.')");
            dbConnection.executeUpdate("INSERT INTO Job (jobID, companyID, recruiterID, salary, workingHours, datePosted, location, title, description) VALUES (12, 3, 3, 35.00, 40.00, TO_DATE('2025-10-07', 'YYYY-MM-DD'), 'Markham, Ontario, Canada', 'Software Engineer', 'Develop internal tools and automation frameworks for AMD.')");
            dbConnection.executeUpdate("INSERT INTO Job (jobID, companyID, recruiterID, salary, workingHours, datePosted, location, title, description) VALUES (13, 4, 4, 26.50, 40.00, TO_DATE('2025-10-05', 'YYYY-MM-DD'), 'Vancouver, British Columbia, Canada', 'QA Tester', 'Perform software and hardware quality assurance tests.')");
            dbConnection.executeUpdate("INSERT INTO Job (jobID, companyID, recruiterID, salary, workingHours, datePosted, location, title, description) VALUES (14, 4, 4, 30.00, 40.00, TO_DATE('2025-10-06', 'YYYY-MM-DD'), 'Vancouver, British Columbia, Canada', 'UI/UX Designer', 'Design user interfaces for Samsung applications.')");
        }
        
        if (isTableEmpty("JobApplicant")) { 
            dbConnection.executeUpdate("INSERT INTO JobApplicant (applicantID, first_name, last_name, industry, birthdate, address, email, phone) VALUES (1, 'Alice', 'Bob', 'Technology', TO_DATE('2002-04-13', 'YYYY-MM-DD'), '123 Bay Street, Toronto, Ontario, Canada, M1B 2K3', 'alice.bob@gmail.com', '416-123-455')");
            dbConnection.executeUpdate("INSERT INTO JobApplicant (applicantID, first_name, last_name, industry, birthdate, address, email, phone) VALUES (2, 'Jake', 'Blake', 'Technology', TO_DATE('2004-05-06', 'YYYY-MM-DD'), '456 Main Street, Markham, Ontario, Canada, L6H 1F3', 'jake.blake@hotmail.com', '647-444-1947')");
            dbConnection.executeUpdate("INSERT INTO JobApplicant (applicantID, first_name, last_name, industry, birthdate, address, email, phone) VALUES (3, 'Griffin', 'Walker', 'Banking', TO_DATE('2003-12-25', 'YYYY-MM-DD'), '789 Bond Avenue, Ajax, Ontario, Canada, L0H 1H9', 'griffin.walker@outlook.com', '905-289-9876')");
            dbConnection.executeUpdate("INSERT INTO JobApplicant (applicantID, first_name, last_name, industry, birthdate, address, email, phone) VALUES (4, 'Ed', 'Stephens', 'Software', TO_DATE('2004-01-25', 'YYYY-MM-DD'), '145 Bloor Avenue, Toronto, Ontario, Canada, M5B 3K9', 'ed.stephens@outlook.com', '905-444-2121')");
            dbConnection.executeUpdate("INSERT INTO JobApplicant (applicantID, first_name, last_name, industry, birthdate, address, email, phone) VALUES (5, 'Joe', 'Random', 'Technology', TO_DATE('2001-07-29', 'YYYY-MM-DD'), '7622 Markham Road, Markham, Ontario, Canada, L6H 9A3', 'joe.random@gmail.com', '647-543-2211')");
            dbConnection.executeUpdate("INSERT INTO JobApplicant (applicantID, first_name, last_name, industry, birthdate, address, email, phone) VALUES (6, 'Michael', 'Jordan', 'Hardware', TO_DATE('2002-11-09', 'YYYY-MM-DD'), '116 Bond Street, Hamilton, Ontario, Canada, LOP 1B9', 'michael.jordan@gmail.com', '416-989-7777')");
            dbConnection.executeUpdate("INSERT INTO JobApplicant (applicantID, first_name, last_name, industry, birthdate, address, email, phone) VALUES (7, 'Sam', 'Inactive', 'Technology', TO_DATE('2002-04-13', 'YYYY-MM-DD'), '123 Bay Street, Toronto, Ontario, Canada, M1B 2K3', 'sam.inactive@gmail.com', '416-123-8293')");
        }

        if (isTableEmpty("JobApplication")) { 
            dbConnection.executeUpdate("INSERT INTO JobApplication (jobAppID, jobID, applicantID, dateTime, status) VALUES (1, 1, 1, TO_DATE('2025-09-28', 'YYYY-MM-DD'), 'Rejected')");
            dbConnection.executeUpdate("INSERT INTO JobApplication (jobAppID, jobID, applicantID, dateTime, status) VALUES (2, 2, 2, TO_DATE('2025-09-29', 'YYYY-MM-DD'), 'Under Review')");
            dbConnection.executeUpdate("INSERT INTO JobApplication (jobAppID, jobID, applicantID, dateTime, status) VALUES (3, 3, 3, TO_DATE('2025-09-30', 'YYYY-MM-DD'), 'Submitted')");
            dbConnection.executeUpdate("INSERT INTO JobApplication (jobAppID, jobID, applicantID, dateTime, status) VALUES (4, 1, 4, TO_DATE('2025-09-30', 'YYYY-MM-DD'), 'Interview Pending')");
            dbConnection.executeUpdate("INSERT INTO JobApplication (jobAppID, jobID, applicantID, dateTime, status) VALUES (5, 3, 5, TO_DATE('2025-09-30', 'YYYY-MM-DD'), 'Interview Pending')");
            dbConnection.executeUpdate("INSERT INTO JobApplication (jobAppID, jobID, applicantID, dateTime, status) VALUES (6, 4, 6, TO_DATE('2025-09-30', 'YYYY-MM-DD'), 'Interview Pending')");
            dbConnection.executeUpdate("INSERT INTO JobApplication (jobAppID, jobID, applicantID, dateTime, status) VALUES (7, 4, 5, TO_DATE('2025-10-04', 'YYYY-MM-DD'), 'Interview Pending')");
        }

        if (isTableEmpty("Resume")) { 
            dbConnection.executeUpdate("INSERT INTO Resume (resumeID, applicantID, uploadFile, uploadDate) VALUES (1, 1, UTL_RAW.CAST_TO_RAW('Alice Bob'), TO_DATE('2002-04-13', 'YYYY-MM-DD'))");
            dbConnection.executeUpdate("INSERT INTO Resume (resumeID, applicantID, uploadFile, uploadDate) VALUES (2, 2, UTL_RAW.CAST_TO_RAW('Jake Blake Resume'), TO_DATE('2025-09-28', 'YYYY-MM-DD'))");
            dbConnection.executeUpdate("INSERT INTO Resume (resumeID, applicantID, uploadFile, uploadDate) VALUES (3, 3, UTL_RAW.CAST_TO_RAW('Griffin Walker Resume'), TO_DATE('2025-09-29', 'YYYY-MM-DD'))");
            dbConnection.executeUpdate("INSERT INTO Resume (resumeID, applicantID, uploadFile, uploadDate) VALUES (4, 4, UTL_RAW.CAST_TO_RAW('Ed Stephens Resume'), TO_DATE('2025-09-28', 'YYYY-MM-DD'))");
            dbConnection.executeUpdate("INSERT INTO Resume (resumeID, applicantID, uploadFile, uploadDate) VALUES (5, 5, UTL_RAW.CAST_TO_RAW('Joe Random Resume'), TO_DATE('2025-09-29', 'YYYY-MM-DD'))");
            dbConnection.executeUpdate("INSERT INTO Resume (resumeID, applicantID, uploadFile, uploadDate) VALUES (6, 6, UTL_RAW.CAST_TO_RAW('Michael Jordan Resume'), TO_DATE('2025-09-30', 'YYYY-MM-DD'))");
            dbConnection.executeUpdate("INSERT INTO Resume (resumeID, applicantID, uploadFile, uploadDate) VALUES (7, 6, UTL_RAW.CAST_TO_RAW('Michael Jordan Resume 2'), TO_DATE('2025-10-04', 'YYYY-MM-DD'))");
            dbConnection.executeUpdate("UPDATE Resume SET uploadDate = TO_DATE('2025-09-27', 'YYYY-MM-DD') WHERE resumeID = 1");
            dbConnection.executeUpdate("UPDATE Resume SET uploadFile = UTL_RAW.CAST_TO_RAW('Alice Bob Resume') WHERE resumeID = 1");
        }
        
        if (isTableEmpty("Interview")) { 
            dbConnection.executeUpdate("INSERT INTO Interview (interviewID, jobAppID, dateTime, location) VALUES (1, 4, TO_DATE('2025-10-1 10:00 AM', 'YYYY-MM-DD HH:MI AM'), 'Toronto, Ontario, Canada')");
            dbConnection.executeUpdate("INSERT INTO Interview (interviewID, jobAppID, dateTime, location) VALUES (2, 5, TO_DATE('2025-10-2 11:00 AM', 'YYYY-MM-DD HH:MI AM'), 'Markham, Ontario, Canada')");
            dbConnection.executeUpdate("INSERT INTO Interview (interviewID, jobAppID, dateTime, location) VALUES (3, 6, TO_DATE('2025-10-3 1:00 PM', 'YYYY-MM-DD HH:MI PM'), 'Vancouver, British Columbia, Canada')");
        }
    }

    private boolean isTableEmpty(String tableName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM " + tableName;
        ResultSet rs = dbConnection.executeQuery(sql);
        rs.next();
        return rs.getInt(1) == 0;
    }

}
