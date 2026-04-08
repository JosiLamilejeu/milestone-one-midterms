package studentdatabase.task3.milestoneone;

import java.sql.*;

public class Repository {
    private final String dbURL;
    
    private Repository(String dbURL) {
        this.dbURL = dbURL;
    }
    
    private Connection connect() throws SQLException {
        return DriverManager.getConnection(dbURL);
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

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, role);

            stmt.executeUpdate();
            System.out.println("Current user saved.");

        } catch (SQLException e) {
            System.out.println("Save config error: " + e.getMessage());
        }
    }

    public boolean logoutUser() {
        String sql = "DELETE FROM CurrentUsers WHERE id = 1";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            int rowsDeleted = stmt.executeUpdate();
            return rowsDeleted > 0;

        } catch (SQLException e) {
            System.out.println("Logout error: " + e.getMessage());
            return false;
        }
    }

    public void clearCurrentUser() {
        String sql = "DELETE FROM CurrentUsers";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Clear current user error: " + e.getMessage());
        }
    }

    public User findByCredentials(String username, String password) {
        String sql = "SELECT username, password, role FROM Users WHERE username = ? AND password = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("role")
                );
            }

        } catch (SQLException e) {
            System.out.println("Login error: " + e.getMessage());
        }

        return null;
    }

    public boolean addUser(String username, String password, String role) {
        String sql = "INSERT INTO Users(username, password, role) VALUES (?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, role);

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Add user error: " + e.getMessage());
            return false;
        }
    }

    public boolean addAdventurer(String username, String password, String race, String adventurerClass) {
        String userSql = "INSERT INTO Users(username, password, role) VALUES (?, ?, ?)";
        String detailsSql = "INSERT INTO AdventurerDetails(username, race, class) VALUES (?, ?, ?)";

        try (Connection conn = connect()) {
            conn.setAutoCommit(false);

            try (PreparedStatement userStmt = conn.prepareStatement(userSql);
                 PreparedStatement detailsStmt = conn.prepareStatement(detailsSql)) {

                userStmt.setString(1, username);
                userStmt.setString(2, password);
                userStmt.setString(3, "adventurer");
                userStmt.executeUpdate();

                detailsStmt.setString(1, username);
                detailsStmt.setString(2, race);
                detailsStmt.setString(3, adventurerClass);
                detailsStmt.executeUpdate();

                conn.commit();
                return true;

            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Add adventurer error: " + e.getMessage());
                return false;
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            return false;
        }
    }

    public boolean updateAdventurerRank(String username, String newRank) {
        String sql = "UPDATE AdventurerDetails SET rank = ? WHERE username = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newRank);
            stmt.setString(2, username);

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            System.out.println("Update rank error: " + e.getMessage());
            return false;
        }
    }

    public boolean addParty(String partyname) {
        String sql = "INSERT INTO Parties(partyname) VALUES (?)";

        try (Connection conn = connect();
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

        try (Connection conn = connect();
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

    public String getPartyNameByUsername(String usernames) {
        String sql = """
            SELECT p.partyname
            FROM Parties p
            JOIN PartyMembers pm ON p.party_id = pm.party_id
            WHERE pm.usernames = ?
            """;

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usernames);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("partyname");
            }

        } catch (SQLException e) {
            System.out.println("Get party error: " + e.getMessage());
        }

        return null;
    }

    public boolean viewCurrentUserPartyList(String usernames) {
        String sql = """
            SELECT p.partyname, pm.usernames
            FROM Parties p
            JOIN PartyMembers pm ON p.party_id = pm.party_id
            WHERE p.party_id = (
                SELECT party_id
                FROM PartyMembers
                WHERE usernames = ?
            )
            """;

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usernames);
            ResultSet rs = stmt.executeQuery();

            boolean hasParty = false;
            System.out.println("\n=== CURRENT USER PARTY LIST ===");

            while (rs.next()) {
                if (!hasParty) {
                    System.out.println("Party Name: " + rs.getString("partyname"));
                    System.out.println("Members:");
                    hasParty = true;
                }

                System.out.println("- " + rs.getString("usernames"));
            }

            return hasParty;

        } catch (SQLException e) {
            System.out.println("View current user party list error: " + e.getMessage());
            return false;
        }
    }

    public boolean addQuest(String questName) {
        String sql = "INSERT INTO Quests(quest_name, reservation_status, reserved_by_type, reserved_by_name) VALUES (?, ?, ?, ?)";

        try (Connection conn = connect();
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

    public boolean viewQuestList() {
        String sql = "SELECT quest_name, reservation_status, reserved_by_type, reserved_by_name FROM Quests";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("\n=== QUEST LIST ===");

            boolean found = false;
            int count = 1;

            while (rs.next()) {
                System.out.println(count + ".");
                System.out.println("Quest Name: " + rs.getString("quest_name"));
                System.out.println("Reservation Status: " + rs.getString("reservation_status"));

                String reservedByType = rs.getString("reserved_by_type");
                String reservedByName = rs.getString("reserved_by_name");

                if (reservedByType != null && reservedByName != null) {
                    System.out.println("Reserved By: " + reservedByType + " - " + reservedByName);
                } else {
                    System.out.println("Reserved By: None");
                }

                System.out.println();
                count++;
                found = true;
            }

            return found;

        } catch (SQLException e) {
            System.out.println("View quest list error: " + e.getMessage());
            return false;
        }
    }

    public boolean reserveQuestByAdventurer(String questName, String username) {
        String checkQuestSql = "SELECT reservation_status FROM Quests WHERE quest_name = ?";
        String updateQuestSql = "UPDATE Quests SET reservation_status = ?, reserved_by_type = ?, reserved_by_name = ? WHERE quest_name = ?";

        try (Connection conn = connect();
             PreparedStatement checkStmt = conn.prepareStatement(checkQuestSql);
             PreparedStatement updateStmt = conn.prepareStatement(updateQuestSql)) {

            checkStmt.setString(1, questName);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                System.out.println("Quest not found.");
                return false;
            }

            if ("Reserved".equalsIgnoreCase(rs.getString("reservation_status"))) {
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

    public boolean reserveQuestByParty(String questName, String partyName) {
        String checkQuestSql = "SELECT reservation_status FROM Quests WHERE quest_name = ?";
        String updateQuestSql = "UPDATE Quests SET reservation_status = ?, reserved_by_type = ?, reserved_by_name = ? WHERE quest_name = ?";

        try (Connection conn = connect();
             PreparedStatement checkStmt = conn.prepareStatement(checkQuestSql);
             PreparedStatement updateStmt = conn.prepareStatement(updateQuestSql)) {

            checkStmt.setString(1, questName);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                System.out.println("Quest not found.");
                return false;
            }

            if ("Reserved".equalsIgnoreCase(rs.getString("reservation_status"))) {
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

    public boolean completeQuest(String questName) {
        String sql = """
            UPDATE Quests
            SET reservation_status = ?, reserved_by_type = NULL, reserved_by_name = NULL
            WHERE quest_name = ?
            """;

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "Completed");
            stmt.setString(2, questName);

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            System.out.println("Complete quest error: " + e.getMessage());
            return false;
        }
    }

    public boolean viewCurrentAdventurerDetails(String username) {
        String sql = """
            SELECT u.username, a.race, a.class, a.rank
            FROM Users u
            JOIN AdventurerDetails a ON u.username = a.username
            WHERE u.username = ? AND u.role = 'adventurer'
            """;

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                System.out.println("\n=== ADVENTURER DETAILS ===");
                System.out.println("Username: " + rs.getString("username"));
                System.out.println("Race: " + rs.getString("race"));
                System.out.println("Class: " + rs.getString("class"));
                System.out.println("Rank: " + rs.getString("rank"));
                return true;
            }

            return false;

        } catch (SQLException e) {
            System.out.println("View adventurer details error: " + e.getMessage());
            return false;
        }
    }

    public boolean viewRegisteredAdventurers() {
        String sql = """
            SELECT u.username, a.race, a.class, a.rank
            FROM Users u
            JOIN AdventurerDetails a ON u.username = a.username
            WHERE u.role = ?
            """;

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "adventurer");
            ResultSet rs = stmt.executeQuery();

            boolean found = false;
            System.out.println("\n=== REGISTERED ADVENTURERS ===");

            while (rs.next()) {
                System.out.println("Username: " + rs.getString("username"));
                System.out.println("Race: " + rs.getString("race"));
                System.out.println("Class: " + rs.getString("class"));
                System.out.println("Rank: " + rs.getString("rank"));
                System.out.println();
                found = true;
            }

            return found;

        } catch (SQLException e) {
            System.out.println("View adventurers error: " + e.getMessage());
            return false;
        }
    }

    public boolean viewRegisteredReceptionists() {
        String sql = "SELECT username FROM Users WHERE role = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "receptionist");
            ResultSet rs = stmt.executeQuery();

            boolean found = false;
            System.out.println("\n=== REGISTERED RECEPTIONISTS ===");

            while (rs.next()) {
                System.out.println("- " + rs.getString("username"));
                found = true;
            }

            return found;

        } catch (SQLException e) {
            System.out.println("View receptionists error: " + e.getMessage());
            return false;
        }
    }

    public boolean removeReceptionist(String username) {
        String sql = "DELETE FROM Users WHERE username = ? AND role = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, "receptionist");

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Remove receptionist error: " + e.getMessage());
            return false;
        }
    }

    public boolean removeAdventurer(String username) {
        String deleteFromPartyMembers = "DELETE FROM PartyMembers WHERE usernames = ?";
        String deleteFromAdventurerDetails = "DELETE FROM AdventurerDetails WHERE username = ?";
        String deleteFromUsers = "DELETE FROM Users WHERE username = ? AND role = ?";

        try (Connection conn = connect()) {
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

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, partyName);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Remove adventurer from party error: " + e.getMessage());
            return false;
        }
    }

    public boolean removeParty(String partyName) {
        String getPartyIdSql = "SELECT party_id FROM Parties WHERE partyname = ?";
        String deleteMembersSql = "DELETE FROM PartyMembers WHERE party_id = ?";
        String deletePartySql = "DELETE FROM Parties WHERE party_id = ?";

        try (Connection conn = connect()) {
            conn.setAutoCommit(false);

            try (PreparedStatement getPartyStmt = conn.prepareStatement(getPartyIdSql)) {
                getPartyStmt.setString(1, partyName);
                ResultSet rs = getPartyStmt.executeQuery();

                if (!rs.next()) {
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

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, questName);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Remove quest error: " + e.getMessage());
            return false;
        }
    }

    public boolean removeReservation(String questName) {
        String sql = """
            UPDATE Quests
            SET reservation_status = ?, reserved_by_type = NULL, reserved_by_name = NULL
            WHERE quest_name = ? AND reservation_status = 'Reserved'
            """;

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "Available");
            stmt.setString(2, questName);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Remove reservation error: " + e.getMessage());
            return false;
        }
    }
    
    public static class RepositoryBuilder {
        private String path;
        
        public RepositoryBuilder setDatabasePath() {
            this.path = "jdbc:sqlite:adventurersguild.db";
            return this;
        }
        public Repository build() {
            if (path == null) throw new IllegalStateException("Database path not set!");
            return new Repository(path);
        }
    }
}

