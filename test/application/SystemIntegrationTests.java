package application;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;

/**
 * SystemIntegrationTests contains simplified automated tests
 * for key user, admin, question, and review interactions.
 * <p>
 * These tests emulate the functionality described in the user stories:
 * <ul>
 *     <li>Create first user as Admin</li>
 *     <li>Invite users and assign roles</li>
 *     <li>Admin role management</li>
 *     <li>Student posting and resolving questions</li>
 *     <li>Reviewer creating, updating, and scoring reviews</li>
 * </ul>
 */
class SystemIntegrationTests {

    private UserService userService;
    private AdminService adminService;
    private QuestionService questionService;
    private ReviewService reviewService;

    /**
     * Sets up simple stub services before each test.
     */
    @BeforeEach
    void setUp() {
        userService = new UserService();
        adminService = new AdminService();
        questionService = new QuestionService();
        reviewService = new ReviewService();
    }

    /**
     * Tests that the first registered user is assigned the Admin role
     * and is able to log in successfully.
     */
    @Test
    void testCreateFirstUserAdmin() {
        User firstUser = userService.registerFirstUser("adminUser", "StrongPass123!");
        assertNotNull(firstUser, "First user should be created");
        assertEquals("Admin", firstUser.getRole(), "First user should have Admin role");

        boolean loginSuccess = userService.login("adminUser", "StrongPass123!");
        assertTrue(loginSuccess, "Admin should be able to login");
    }

    /**
     * Tests the process of an admin creating an invitation code,
     * and a new user registering using that invite code.
     */
    @Test
    void testInviteUserAndJoin() {
        User admin = userService.registerFirstUser("admin", "StrongPass123!");
        String inviteCode = adminService.createInvite("studentRole", 60);
        assertNotNull(inviteCode, "Invite code should be generated");

        User invited = userService.registerUsingInvite("student1", "Pass@123", inviteCode);
        assertNotNull(invited, "Invited user should register");
        assertEquals("studentRole", invited.getRole(), "User should get invited role");
    }

    /**
     * Tests admin's ability to add and remove roles for other users,
     * and ensures admin cannot remove their own admin privileges.
     */
    @Test
    void testRoleManagementByAdmin() {
        User admin = userService.registerFirstUser("admin", "StrongPass123!");
        User user = userService.createUser("bob", "B0b@123");

        assertTrue(adminService.addRole(admin, user, "Reviewer"), "Should be able to add role");
        assertTrue(adminService.removeRole(admin, user, "Reviewer"), "Should be able to remove role");
        assertFalse(adminService.removeRole(admin, admin, "Admin"), "Cannot remove own admin role");
    }

    /**
     * Tests a student posting a question, another user submitting an answer,
     * and marking the question as resolved.
     */
    @Test
    void testStudentQuestionAndResolve() {
        User student = userService.createUser("alice", "Alice123!");
        Question q = questionService.postQuestion(student, "How do I reset my password?");
        assertNotNull(q, "Student can post question");

        User responder = userService.createUser("bob", "Bob123!");
        Answer a = questionService.submitAnswer(responder, q, "Reset in settings");
        assertNotNull(a, "Responder can submit answer");

        boolean resolved = questionService.markAsResolved(student, q, a);
        assertTrue(resolved, "Question can be marked resolved");

        assertFalse(questionService.getUnresolvedQuestions().contains(q), "Resolved question removed from unresolved");
        assertTrue(questionService.getResolvedQuestions().contains(q), "Resolved question in resolved list");
    }

    /**
     * Tests a reviewer creating a review, updating it,
     * receiving feedback, and having their score updated.
     */
    @Test
    void testReviewerCreateUpdatesAndScore() {
        User instructor = userService.registerFirstUser("prof", "ProfPass@1");
        User reviewer = userService.createUser("charlie", "Charli3!");
        adminService.addRole(instructor, reviewer, "Reviewer");

        Answer answer = new Answer("Sample answer", reviewer);
        Review review = reviewService.createReview(reviewer, answer, "Good answer");
        assertNotNull(review, "Reviewer can create review");

        Review updated = reviewService.updateReview(reviewer, review, "Updated answer");
        assertEquals(review.getId(), updated.getPreviousVersionId(), "Updated review links to previous");

        reviewService.addFeedback("student1", reviewer, "Helpful feedback");
        reviewService.recalculateScorecard(instructor);
        assertTrue(reviewService.getReviewerScore(reviewer) > 0, "Score updated");
    }
}
