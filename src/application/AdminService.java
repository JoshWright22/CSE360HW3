package application;

import databasePart1.DatabaseHelper;

//simplified AdminService stub
public class AdminService {
	 public AdminService() {}
	
	 public String createInvite(String role, int durationMinutes) {
	     return "INVITE123";
	 }
	
	 public boolean addRole(User admin, User user, String role) {
	     user.setRole(role);
	     return true;
	 }
	
	 public boolean removeRole(User admin, User user, String role) {
	     return !admin.equals(user); // cannot remove own role
 }
}