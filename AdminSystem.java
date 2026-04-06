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

public class AdminSystem {
        public boolean addUser(String username, String password, String role) {
        String sql = "INSERT INTO Users(username, password, role) VALUES (?, ?, ?)";

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, role);

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Register error: " + e.getMessage());
            return false;
        }
        }
        
        public boolean addAdventurer(String username, String password, String adventurerRace, String adventurerClass) {
    String userSql = "INSERT INTO Users(username, password, role) VALUES (?, ?, ?)";
    String detailsSql = "INSERT INTO AdventurerDetails(username, race, class) VALUES (?, ?, ?)";

    try (Connection conn = Database.connect()) {
        conn.setAutoCommit(false);

        try (PreparedStatement userStmt = conn.prepareStatement(userSql);
             PreparedStatement detailsStmt = conn.prepareStatement(detailsSql)) {

            userStmt.setString(1, username);
            userStmt.setString(2, password);
            userStmt.setString(3, "adventurer");
            userStmt.executeUpdate();

            detailsStmt.setString(1, username);
            detailsStmt.setString(2, adventurerRace);
            detailsStmt.setString(3, adventurerClass);
            detailsStmt.executeUpdate();

            conn.commit();
            return true;

        } catch (SQLException e) {
            conn.rollback();
            System.out.println("Register adventurer error: " + e.getMessage());
            return false;
        }

    } catch (SQLException e) {
        System.out.println("Database error: " + e.getMessage());
        return false;
    }
}
        
        public boolean addParty(String partyname) {
    String sql = "INSERT INTO Parties(partyname) VALUES (?)";

    try (Connection conn = Database.connect();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, partyname);
        stmt.executeUpdate();
        return true;

    } catch (SQLException e) {
        System.out.println("Add party error: " + e.getMessage());
        return false;
    }
        }
        
        public boolean addPartyMember(String partyname, String username) {
    String getPartySql = "SELECT party_id FROM Parties WHERE partyname = ?";
    String checkUserSql = "SELECT username FROM Users WHERE username = ? AND role = ?";
    String insertMemberSql = "INSERT INTO PartyMembers(party_id, usernames) VALUES (?, ?)";

    try (Connection conn = Database.connect();
         PreparedStatement getPartyStmt = conn.prepareStatement(getPartySql);
         PreparedStatement checkUserStmt = conn.prepareStatement(checkUserSql)) {

        getPartyStmt.setString(1, partyname);
        ResultSet partyRs = getPartyStmt.executeQuery();

        if (!partyRs.next()) {
            System.out.println("Party not found.");
            return false;
        }

        int partyId = partyRs.getInt("party_id");

        checkUserStmt.setString(1, username);
        checkUserStmt.setString(2, "adventurer");
        ResultSet userRs = checkUserStmt.executeQuery();

        if (!userRs.next()) {
            System.out.println("User is not a registered adventurer.");
            return false;
        }

        try (PreparedStatement insertStmt = conn.prepareStatement(insertMemberSql)) {
            insertStmt.setInt(1, partyId);
            insertStmt.setString(2, username);
            insertStmt.executeUpdate();
            return true;
        }

    } catch (SQLException e) {
        System.out.println("Add party member error: " + e.getMessage());
        return false;
    }
}
            public boolean addQuest(String questName) {
        String sql = "INSERT INTO Quests(quest_name, reservation_status, reserved_by_type, reserved_by_name) VALUES (?, ?, ?, ?)";

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, questName);
            stmt.setString(2, "Available");
            stmt.setNull(3, Types.VARCHAR);
            stmt.setNull(4, Types.VARCHAR);

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Add quest error: " + e.getMessage());
            return false;
        }
    }
    public boolean removeReceptionist(String username) {
    String sql = "DELETE FROM Users WHERE username = ? AND role = ?";

    try (Connection conn = Database.connect();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, username);
        stmt.setString(2, "receptionist");

        int rowsDeleted = stmt.executeUpdate();

        if (rowsDeleted > 0) {
            return true;
        } else {
            System.out.println("Receptionist not found.");
            return false;
        }

    } catch (SQLException e) {
        System.out.println("Remove receptionist error: " + e.getMessage());
        return false;
    }
}
    public boolean removeAdventurer(String username) {
    String deleteFromPartyMembers = "DELETE FROM PartyMembers WHERE usernames = ?";
    String deleteFromAdventurerDetails = "DELETE FROM AdventurerDetails WHERE username = ?";
    String deleteFromUsers = "DELETE FROM Users WHERE username = ? AND role = ?";

    try (Connection conn = Database.connect()) {
        conn.setAutoCommit(false);

        try (PreparedStatement partyStmt = conn.prepareStatement(deleteFromPartyMembers);
             PreparedStatement detailsStmt = conn.prepareStatement(deleteFromAdventurerDetails);
             PreparedStatement userStmt = conn.prepareStatement(deleteFromUsers)) {

            partyStmt.setString(1, username);
            partyStmt.executeUpdate();

            detailsStmt.setString(1, username);
            detailsStmt.executeUpdate();

            userStmt.setString(1, username);
            userStmt.setString(2, "adventurer");
            int rowsDeleted = userStmt.executeUpdate();

            if (rowsDeleted > 0) {
                conn.commit();
                return true;
            } else {
                conn.rollback();
                System.out.println("Adventurer not found.");
                return false;
            }

        } catch (SQLException e) {
            conn.rollback();
            System.out.println("Remove adventurer error: " + e.getMessage());
            return false;
        }

    } catch (SQLException e) {
        System.out.println("Database error: " + e.getMessage());
        return false;
    }
}
    public boolean removeAdventurerFromParty(String username, String partyName) {
    String sql = """
        DELETE FROM PartyMembers
        WHERE usernames = ?
        AND party_id = (
            SELECT party_id FROM Parties WHERE partyname = ?
        )
        """;

    try (Connection conn = Database.connect();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, username);
        stmt.setString(2, partyName);

        int rowsDeleted = stmt.executeUpdate();

        if (rowsDeleted > 0) {
            return true;
        } else {
            System.out.println("Adventurer is not in that party, or party not found.");
            return false;
        }

    } catch (SQLException e) {
        System.out.println("Remove adventurer from party error: " + e.getMessage());
        return false;
    }
}
    public boolean removeParty(String partyName) {
    String getPartyIdSql = "SELECT party_id FROM Parties WHERE partyname = ?";
    String deleteMembersSql = "DELETE FROM PartyMembers WHERE party_id = ?";
    String deletePartySql = "DELETE FROM Parties WHERE party_id = ?";

    try (Connection conn = Database.connect()) {
        conn.setAutoCommit(false);

        try (PreparedStatement getPartyStmt = conn.prepareStatement(getPartyIdSql)) {
            getPartyStmt.setString(1, partyName);
            ResultSet rs = getPartyStmt.executeQuery();

            if (!rs.next()) {
                System.out.println("Party not found.");
                conn.rollback();
                return false;
            }

            int partyId = rs.getInt("party_id");

            try (PreparedStatement deleteMembersStmt = conn.prepareStatement(deleteMembersSql);
                 PreparedStatement deletePartyStmt = conn.prepareStatement(deletePartySql)) {

                deleteMembersStmt.setInt(1, partyId);
                deleteMembersStmt.executeUpdate();

                deletePartyStmt.setInt(1, partyId);
                int rowsDeleted = deletePartyStmt.executeUpdate();

                if (rowsDeleted > 0) {
                    conn.commit();
                    return true;
                } else {
                    conn.rollback();
                    System.out.println("Failed to remove party.");
                    return false;
                }
            }

        } catch (SQLException e) {
            conn.rollback();
            System.out.println("Remove party error: " + e.getMessage());
            return false;
        }

    } catch (SQLException e) {
        System.out.println("Database error: " + e.getMessage());
        return false;
    }
}
    public boolean removeQuest(String questName) {
    String sql = "DELETE FROM Quests WHERE quest_name = ?";

    try (Connection conn = Database.connect();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, questName);

        int rowsDeleted = stmt.executeUpdate();

        if (rowsDeleted > 0) {
            return true;
        } else {
            System.out.println("Quest not found.");
            return false;
        }

    } catch (SQLException e) {
        System.out.println("Remove quest error: " + e.getMessage());
        return false;
    }
}
    public boolean removeReservation(String questName) {
    String sql = """
        UPDATE Quests
        SET reservation_status = ?, reserved_by_type = NULL, reserved_by_name = NULL
        WHERE quest_name = ?
        """;

    try (Connection conn = Database.connect();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, "Available");
        stmt.setString(2, questName);

        int rowsUpdated = stmt.executeUpdate();

        if (rowsUpdated > 0) {
            return true;
        } else {
            System.out.println("Quest not found.");
            return false;
        }

    } catch (SQLException e) {
        System.out.println("Remove reservation error: " + e.getMessage());
        return false;
    }
}
}
