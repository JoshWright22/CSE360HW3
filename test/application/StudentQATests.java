import databasePart1.DatabaseHelper;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.List;

class StudentQATests {

    static DatabaseHelper db;

    @BeforeAll
    static void setupDatabase() throws SQLException {
        db = new DatabaseHelper();
        db.connectToDatabase();
        // Clear tables for testing
        for (DatabaseHelper.Question q : db.getAllQuestions()) {
            db.deleteQuestion(q.getId());
        }
        // Answers will be deleted automatically due to foreign key ON DELETE CASCADE
    }

    @AfterAll
    static void closeDatabase() {
        db.closeConnection();
    }

    @BeforeEach
    void clearData() throws SQLException {
        for (DatabaseHelper.Question q : db.getAllQuestions()) {
            db.deleteQuestion(q.getId());
        }
    }

    // Question Create
    @Test
    void TC1_createQuestionValid() throws SQLException {
        int id = db.createQuestion("Student1", "Title 1", "Description 1");
        List<DatabaseHelper.Question> questions = db.getAllQuestions();
        assertEquals(1, questions.size());
        assertEquals("Title 1", questions.get(0).getTitle());
    }

    @Test
    void TC2_createQuestionEmptyTitle() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            db.createQuestion("Student1", "", "Description");
        });
        assertEquals("Title and description cannot be empty", exception.getMessage());
    }

    @Test
    void TC3_createQuestionLongDescription() {
        String longDesc = "a".repeat(1025); // assuming max length is 1024
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            db.createQuestion("Student1", "Title", longDesc);
        });
        assertEquals("Title and description cannot be empty", exception.getMessage());
    }

    // Question Read
    @Test
    void TC4_readQuestionsExist() throws SQLException {
        db.createQuestion("Student1", "Q1", "Desc1");
        db.createQuestion("Student2", "Q2", "Desc2");
        List<DatabaseHelper.Question> questions = db.getAllQuestions();
        assertEquals(2, questions.size());
    }

    @Test
    void TC5_readQuestionsEmpty() throws SQLException {
        List<DatabaseHelper.Question> questions = db.getAllQuestions();
        assertTrue(questions.isEmpty());
    }

    // Question Update
    @Test
    void TC6_updateQuestionValid() throws SQLException {
        int qId = db.createQuestion("Student1", "Old", "Old desc");
        db.updateQuestion(qId, "New", "New desc");
        DatabaseHelper.Question updated = db.getAllQuestions().stream()
                .filter(q -> q.getId() == qId).findFirst().orElse(null);
        assertNotNull(updated);
        assertEquals("New", updated.getTitle());
    }

    @Test
    void TC7_updateQuestionEmptyTitle() throws SQLException {
        int qId = db.createQuestion("Student1", "Old", "Old desc");
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            db.updateQuestion(qId, "", "New desc");
        });
        assertEquals("Title and description cannot be empty", ex.getMessage());
    }

    @Test
    void TC8_updateQuestionNotExist() {
        Exception ex = assertThrows(RuntimeException.class, () -> {
            db.updateQuestion(999, "New", "Desc");
        });
    }

    // Question Delete
    @Test
    void TC9_deleteQuestionExist() throws SQLException {
        int qId = db.createQuestion("Student1", "Q", "Desc");
        db.deleteQuestion(qId);
        assertTrue(db.getAllQuestions().isEmpty());
    }

    @Test
    void TC10_deleteQuestionNotExist() {
        // No exception thrown in current DatabaseHelper for deleting non-existent IDs
        db.deleteQuestion(999);
        assertTrue(db.getAllQuestions().isEmpty());
    }

    // Further tests for answers can be similarly updated...
}
