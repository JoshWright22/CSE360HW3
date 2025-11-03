package application;



//simplified ReviewService stub
public class ReviewService {
 public ReviewService() {}

 public Review createReview(User reviewer, Answer answer, String content) {
     return new Review(1, content);
 }

 public Review updateReview(User reviewer, Review review, String newContent) {
     return new Review(review.getId(), newContent, review.getId());
 }

 public void addFeedback(String student, User reviewer, String feedback) {}

 public void recalculateScorecard(User instructor) {}

 public double getReviewerScore(User reviewer) {
     return 1.0;
 }
}