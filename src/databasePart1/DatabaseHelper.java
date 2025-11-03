package databasePart1;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import application.User;

public class DatabaseHelper {

    static final String JDBC_DRIVER = "org.h2.Driver";
    static final String DB_URL = "jdbc:h2:~/FoundationDatabase";

    static final String USER = "sa";
    static final String PASS = "";

    private Connection connection = null;
    private Statement statement = null;
    
    public void reset() {
        // stub: do nothing
    }

    public void connectToDatabase() throws SQLException {
        try {
            Class.forName(JDBC_DRIVER);
            System.out.println("Connecting to database...");
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            statement = connection.createStatement();
            createTables();
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found: " + e.getMessage());
        }
    }

    private void createTables() throws SQLException {
        // Original user table
        String userTable = "CREATE TABLE IF NOT EXISTS cse360users ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "userName VARCHAR(255) UNIQUE, "
                + "password VARCHAR(255), "
                + "role VARCHAR(20))";
        statement.execute(userTable);

        // Original invitation codes table
        String invitationCodesTable = "CREATE TABLE IF NOT EXISTS InvitationCodes ("
                + "code VARCHAR(10) PRIMARY KEY, "
                + "isUsed BOOLEAN DEFAULT FALSE)";
        statement.execute(invitationCodesTable);

        // New Question table
        String questionTable = "CREATE TABLE IF NOT EXISTS Questions ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "studentName VARCHAR(255), "
                + "title VARCHAR(255), "
                + "description VARCHAR(1024))";
        statement.execute(questionTable);

        // New Answer table
        String answerTable = "CREATE TABLE IF NOT EXISTS Answers ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "questionId INT, "
                + "content VARCHAR(1024), "
                + "FOREIGN KEY (questionId) REFERENCES Questions(id) ON DELETE CASCADE)";
        statement.execute(answerTable);
    }

    // ------------------- Original User Methods -------------------
    public boolean isDatabaseEmpty() throws SQLException {
        String query = "SELECT COUNT(*) AS count FROM cse360users";
        ResultSet resultSet = statement.executeQuery(query);
        if (resultSet.next()) {
            return resultSet.getInt("count") == 0;
        }
        return true;
    }

    public void register(User user) throws SQLException {
        String insertUser = "INSERT INTO cse360users (userName, password, role) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
            pstmt.setString(1, user.getUserName());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getRole());
            pstmt.executeUpdate();
        }
    }

    public boolean doesUserExist(String userName) {
        String query = "SELECT COUNT(*) FROM cse360users WHERE userName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, userName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean login(User user) throws SQLException {
        String query = "SELECT * FROM cse360users WHERE userName = ? AND password = ? AND role = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, user.getUserName());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getRole());
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public String getUserRole(String userName) {
        String query = "SELECT role FROM cse360users WHERE userName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, userName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("role");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String generateInvitationCode() {
        String code = UUID.randomUUID().toString().substring(0, 4);
        String query = "INSERT INTO InvitationCodes (code) VALUES (?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, code);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return code;
    }

    public boolean validateInvitationCode(String code) {
        String query = "SELECT * FROM InvitationCodes WHERE code = ? AND isUsed = FALSE";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, code);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                markInvitationCodeAsUsed(code);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void markInvitationCodeAsUsed(String code) {
        String query = "UPDATE InvitationCodes SET isUsed = TRUE WHERE code = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, code);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ------------------- Question CRUD -------------------
    public int createQuestion(String studentName, String title, String description) {
        if (title == null || title.isBlank() || description == null || description.isBlank()) {
            throw new IllegalArgumentException("Title and description cannot be empty");
        }
        String query = "INSERT INTO Questions (studentName, title, description) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, studentName);
            pstmt.setString(2, title);
            pstmt.setString(3, description);
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public List<Question> getAllQuestions() {
        List<Question> list = new ArrayList<>();
        String query = "SELECT * FROM Questions";
        try (ResultSet rs = statement.executeQuery(query)) {
            while (rs.next()) {
                list.add(new Question(rs.getInt("id"), rs.getString("studentName"), rs.getString("title"), rs.getString("description")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void updateQuestion(int questionId, String newTitle, String newDesc) {
        if (newTitle == null || newTitle.isBlank() || newDesc == null || newDesc.isBlank()) {
            throw new IllegalArgumentException("Title and description cannot be empty");
        }
        String query = "UPDATE Questions SET title = ?, description = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, newTitle);
            pstmt.setString(2, newDesc);
            pstmt.setInt(3, questionId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteQuestion(int questionId) {
        String query = "DELETE FROM Questions WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, questionId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ------------------- Answer CRUD -------------------
    public int createAnswer(int questionId, String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Answer content cannot be empty");
        }
        String query = "INSERT INTO Answers (questionId, content) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, questionId);
            pstmt.setString(2, content);
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public List<Answer> getAnswersForQuestion(int questionId) {
        List<Answer> list = new ArrayList<>();
        String query = "SELECT * FROM Answers WHERE questionId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, questionId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new Answer(rs.getInt("id"), rs.getInt("questionId"), rs.getString("content")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void updateAnswer(int answerId, String newContent) {
        if (newContent == null || newContent.isBlank()) {
            throw new IllegalArgumentException("Answer content cannot be empty");
        }
        String query = "UPDATE Answers SET content = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, newContent);
            pstmt.setInt(2, answerId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteAnswer(int answerId) {
        String query = "DELETE FROM Answers WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, answerId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ------------------- Close Connection -------------------
    public void closeConnection() {
        try {
            if (statement != null) statement.close();
            if (connection != null) connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ------------------- Inner Classes -------------------
    public static class Question {
        private final int id;
        private final String studentName;
        private final String title;
        private final String description;

        public Question(int id, String studentName, String title, String description) {
            this.id = id;
            this.studentName = studentName;
            this.title = title;
            this.description = description;
        }

        public int getId() { return id; }
        public String getStudentName() { return studentName; }
        public String getTitle() { return title; }
        public String getDescription() { return description; }
    }

    public static class Answer {
        private final int id;
        private final int questionId;
        private final String content;

        public Answer(int id, int questionId, String content) {
            this.id = id;
            this.questionId = questionId;
            this.content = content;
        }

        public int getId() { return id; }
        public int getQuestionId() { return questionId; }
        public String getContent() { return content; }
    }
}
