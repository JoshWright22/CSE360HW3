package application;

import java.util.ArrayList;
import java.util.List;

//simplified QuestionService stub
public class QuestionService {
 private List<Question> unresolved = new ArrayList<>();
 private List<Question> resolved = new ArrayList<>();

 public QuestionService() {}

 public Question postQuestion(User user, String content) {
     Question q = new Question(1, user.getUserName(), null, "Title", content, new ArrayList<>(), new ArrayList<>());
     unresolved.add(q);
     return q;
 }

 public Answer submitAnswer(User user, Question question, String content) {
     Answer a = new Answer(content, user);
     return a;
 }

 public boolean markAsResolved(User user, Question question, Answer answer) {
     unresolved.remove(question);
     resolved.add(question);
     return true;
 }

 public List<Question> getUnresolvedQuestions() {
     return unresolved;
 }

 public List<Question> getResolvedQuestions() {
     return resolved;
 }
}

