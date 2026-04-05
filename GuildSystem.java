/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package studentdatabase.task3.milestoneone;

import java.sql.*;
/**
 *
 * @author kludy
 */

public class GuildSystem {
    
        private final Repository repo;

        public GuildSystem(Repository repo) {
            this.repo = repo;
        }

        public User loginUser(String username, String password) {
            return repo.findByCredentials(username, password);
        }

        public void saveConfig(String username, String password, String role) {
            String sql = """
                INSERT INTO CurrentUsers (id, username, password, role)
                VALUES (1, ?, ?, ?)
                ON CONFLICT(id)
                DO UPDATE SET
                    username = excluded.username,
                    password = excluded.password,
                    role = excluded.role
            """;

            try (Connection conn = Database.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, username);
                stmt.setString(2, password);
                stmt.setString(3, role);

                stmt.executeUpdate();

                System.out.println("Config saved!");

            } catch (SQLException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    
    public boolean logoutUser() {
    String sql = "DELETE FROM CurrentUsers WHERE id = 1";

    try (Connection conn = Database.connect();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        int rowsDeleted = stmt.executeUpdate();

        if (rowsDeleted > 0) {
            System.out.println("Logged out successfully.");
            return true;
        } else {
            System.out.println("No current user is logged in.");
            return false;
        }

    } catch (SQLException e) {
        System.out.println("Logout error: " + e.getMessage());
        return false;
    }
}
    
    public boolean reserveQuestByParty(String questName, String partyName) {
        String checkQuestSql = "SELECT reservation_status FROM Quests WHERE quest_name = ?";
        String updateQuestSql = "UPDATE Quests SET reservation_status = ?, reserved_by_type = ?, reserved_by_name = ? WHERE quest_name = ?";

        try (Connection conn = Database.connect();
             PreparedStatement checkStmt = conn.prepareStatement(checkQuestSql);
             PreparedStatement updateStmt = conn.prepareStatement(updateQuestSql)) {

            checkStmt.setString(1, questName);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                System.out.println("Quest not found.");
                return false;
            }

            String status = rs.getString("reservation_status");
            if ("Reserved".equalsIgnoreCase(status)) {
                System.out.println("Quest is already reserved.");
                return false;
            }

            updateStmt.setString(1, "Reserved");
            updateStmt.setString(2, "Party");
            updateStmt.setString(3, partyName);
            updateStmt.setString(4, questName);

            updateStmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Reserve quest error: " + e.getMessage());
            return false;
        }
    }

    public boolean reserveQuestByAdventurer(String questName, String username) {
        String checkQuestSql = "SELECT reservation_status FROM Quests WHERE quest_name = ?";
        String updateQuestSql = "UPDATE Quests SET reservation_status = ?, reserved_by_type = ?, reserved_by_name = ? WHERE quest_name = ?";

        try (Connection conn = Database.connect();
             PreparedStatement checkStmt = conn.prepareStatement(checkQuestSql);
             PreparedStatement updateStmt = conn.prepareStatement(updateQuestSql)) {

            checkStmt.setString(1, questName);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                System.out.println("Quest not found.");
                return false;
            }

            String status = rs.getString("reservation_status");
            if ("Reserved".equalsIgnoreCase(status)) {
                System.out.println("Quest is already reserved.");
                return false;
            }

            updateStmt.setString(1, "Reserved");
            updateStmt.setString(2, "Adventurer");
            updateStmt.setString(3, username);
            updateStmt.setString(4, questName);

            updateStmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Reserve quest error: " + e.getMessage());
            return false;
        }
    }
    public boolean completeQuest(String questName) {
    String sql = "UPDATE Quests SET reservation_status = ? WHERE quest_name = ?";

    try (Connection conn = Database.connect();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, "Completed");
        stmt.setString(2, questName);

        int rowsUpdated = stmt.executeUpdate();

        if (rowsUpdated > 0) {
            return true;
        } else {
            System.out.println("Quest not found.");
            return false;
        }

    } catch (SQLException e) {
        System.out.println("Complete quest error: " + e.getMessage());
        return false;
    }
}
    public boolean updateAdventurerRank(String username, String newRank) {
    String sql = "UPDATE AdventurerDetails SET rank = ? WHERE username = ?";

    try (Connection conn = Database.connect();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, newRank);
        stmt.setString(2, username);

        int rowsUpdated = stmt.executeUpdate();

        if (rowsUpdated > 0) {
            return true;
        } else {
            System.out.println("Adventurer not found.");
            return false;
        }

    } catch (SQLException e) {
        System.out.println("Update rank error: " + e.getMessage());
        return false;
    }
}
}