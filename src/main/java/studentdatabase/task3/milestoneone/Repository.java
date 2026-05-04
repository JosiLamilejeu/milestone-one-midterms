package studentdatabase.task3.milestoneone;

import java.sql.*;

public class Repository {
    private static Repository instance;
    private final String DB_URL;
    
    private Repository(String dbURL) {
        this.DB_URL = dbURL;
    }
    
    public static Repository getInstance() {
        if (instance == null) {
            instance = new Repository("jdbc:sqlite:C:\\\\MilestoneOne\\\\Milestone1.db");
        }
        return instance;
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
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
                return new User.UserBuilder()
                    .setUsername(rs.getString("username"))
                    .setPassword(rs.getString("password"))
                    .setRole(rs.getString("role"))
                    .build();
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

    public boolean addQuest(String questName, double questFee) {
        String sql = "INSERT INTO Quests(quest_name, reservation_status, reserved_by_type, reserved_by_name, reservation_fee) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, questName);
            stmt.setString(2, "Available");
            stmt.setNull(3, Types.VARCHAR);
            stmt.setNull(4, Types.VARCHAR);
            stmt.setDouble(5, questFee);

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

        if (!rs.next()) {
            System.out.println("No quests available.");
            return false;
        }

        int count = 1;

        do {
            System.out.println(count++ + ".");
            System.out.println("Quest Name: " + rs.getString("quest_name"));
            System.out.println("Reservation Status: " + rs.getString("reservation_status"));

            String type = rs.getString("reserved_by_type");
            String name = rs.getString("reserved_by_name");

            if (type != null && name != null) {
                System.out.println("Reserved By: " + type + " - " + name);
            } else {
                System.out.println("Reserved By: None");
            }

            System.out.println();

        } while (rs.next());

        return true;

    } catch (SQLException e) {
        System.out.println("Get quests error: " + e.getMessage());
        return false;
    }
}

    public boolean reserveQuestByAdventurer(String questName, String username) {

    String checkQuestSql = "SELECT reservation_status, reservation_fee FROM Quests WHERE quest_name = ?";
    String getUserSql = "SELECT balance FROM AdventurerDetails WHERE username = ?";
    String updateQuestSql = "UPDATE Quests SET reservation_status = ?, reserved_by_type = ?, reserved_by_name = ? WHERE quest_name = ?";

    try (Connection conn = connect()) {

        conn.setAutoCommit(false);

        try (PreparedStatement checkStmt = conn.prepareStatement(checkQuestSql);
             PreparedStatement userStmt = conn.prepareStatement(getUserSql);
             PreparedStatement updateStmt = conn.prepareStatement(updateQuestSql)) {

            checkStmt.setString(1, questName);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next() || "Reserved".equalsIgnoreCase(rs.getString("reservation_status"))) {
                conn.rollback();
                return false;
            }

            double questCost = rs.getDouble("reservation_fee");

            userStmt.setString(1, username);
            ResultSet userRs = userStmt.executeQuery();

            if (!userRs.next()) {
                conn.rollback();
                return false;
            }

            double balance = userRs.getDouble("balance");

            AdventurerPayment payment = new AdventurerPayment(balance);
            double finalAmount = payment.processInvoice(questCost, 0);

            if (finalAmount <= 0) {
                conn.rollback();
                return false;
            }

            updateStmt.setString(1, "Reserved");
            updateStmt.setString(2, "Adventurer");
            updateStmt.setString(3, username);
            updateStmt.setString(4, questName);
            updateStmt.executeUpdate();

            try (PreparedStatement updateGold = conn.prepareStatement(
                    "UPDATE AdventurerDetails SET balance = ? WHERE username = ?")) {

                updateGold.setDouble(1, payment.balance);
                updateGold.setString(2, username);
                updateGold.executeUpdate();
            }

            recordQuestTransaction(conn, "Adventurer", username, questName, questCost, 0, finalAmount);

            conn.commit();
            return true;

        } catch (Exception e) {
            conn.rollback();
            System.out.println("Error: " + e.getMessage());
            return false;
        }

    } catch (SQLException e) {
        System.out.println("DB error: " + e.getMessage());
        return false;
    }
}

    public boolean reserveQuestByParty(String questName, String partyName) {

    String checkQuestSql = "SELECT reservation_status, reservation_fee FROM Quests WHERE quest_name = ?";
    String getPartySql = "SELECT party_id, balance FROM Parties WHERE partyname = ?";
    String countMembersSql = "SELECT COUNT(*) AS member_count FROM PartyMembers WHERE party_id = ?";
    String updateQuestSql = "UPDATE Quests SET reservation_status = ?, reserved_by_type = ?, reserved_by_name = ? WHERE quest_name = ?";

    try (Connection conn = connect()) {

        conn.setAutoCommit(false);

        try (PreparedStatement checkStmt = conn.prepareStatement(checkQuestSql);
             PreparedStatement partyStmt = conn.prepareStatement(getPartySql);
             PreparedStatement countStmt = conn.prepareStatement(countMembersSql);
             PreparedStatement updateStmt = conn.prepareStatement(updateQuestSql)) {

            checkStmt.setString(1, questName);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next() || "Reserved".equalsIgnoreCase(rs.getString("reservation_status"))) {
                conn.rollback();
                return false;
            }

            double questCost = rs.getDouble("reservation_fee");

            partyStmt.setString(1, partyName);
            ResultSet partyRs = partyStmt.executeQuery();

            if (!partyRs.next()) {
                conn.rollback();
                return false;
            }

            int partyId = partyRs.getInt("party_id");
            double balance = partyRs.getDouble("balance");

            countStmt.setInt(1, partyId);
            ResultSet countRs = countStmt.executeQuery();

            if (!countRs.next()) {
                conn.rollback();
                return false;
            }

            int memberCount = countRs.getInt("member_count");

            double discount = 0;
            if (memberCount >= 4) discount = questCost * 0.20;
            else if (memberCount >= 2) discount = questCost * 0.10;

            System.out.println("Party members: " + memberCount);
            System.out.println("Discount applied: " + discount);

            AdventurerPayment payment = new AdventurerPayment(balance);
            double finalAmount = payment.processInvoice(questCost, discount);

            if (finalAmount <= 0) {
                conn.rollback();
                return false;
            }

            updateStmt.setString(1, "Reserved");
            updateStmt.setString(2, "Party");
            updateStmt.setString(3, partyName);
            updateStmt.setString(4, questName);
            updateStmt.executeUpdate();

            try (PreparedStatement updateGold = conn.prepareStatement(
                    "UPDATE Parties SET balance = ? WHERE party_id = ?")) {

                updateGold.setDouble(1, payment.balance);
                updateGold.setInt(2, partyId);
                updateGold.executeUpdate();
            }

            recordQuestTransaction(conn, "Party", partyName, questName, questCost, discount, finalAmount);

            conn.commit();
            return true;

        } catch (Exception e) {
            conn.rollback();
            System.out.println("Error: " + e.getMessage());
            return false;
        }

    } catch (SQLException e) {
        System.out.println("DB error: " + e.getMessage());
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
            SELECT u.username, a.race, a.class, a.rank, a.balance
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
                System.out.println("Balance: " + rs.getInt("balance"));
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
                System.out.println("Balance: " + rs.getInt("balance"));
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
    
    public boolean updateBalance(String username,
                                String sourceName,
                                double cashAmount,
                                double fee) {

    String getUserSql = "SELECT balance FROM AdventurerDetails WHERE username = ?";
    String updateBalanceSql = "UPDATE AdventurerDetails SET balance = ? WHERE username = ?";
    String insertTransactionSql = """
        INSERT INTO CreditsTransaction
        (source_type, source_name, amount, fee, net_ecash_amount, transaction_type)
        VALUES (?, ?, ?, ?, ?, 'CASH_TO_ECASH')
    """;
    
    String sourceType = "Adventurer";

    try (Connection conn = connect()) {

        conn.setAutoCommit(false);

        try (PreparedStatement userStmt = conn.prepareStatement(getUserSql);
             PreparedStatement updateStmt = conn.prepareStatement(updateBalanceSql);
             PreparedStatement logStmt = conn.prepareStatement(insertTransactionSql)) {

            userStmt.setString(1, username);
            ResultSet rs = userStmt.executeQuery();

            if (!rs.next()) {
                conn.rollback();
                return false;
            }

            double balance = rs.getDouble("balance");

            double netAmount = cashAmount - fee;

            if (netAmount <= 0) {
                conn.rollback();
                return false;
            }

            double updatedBalance = balance + netAmount;

            updateStmt.setDouble(1, updatedBalance);
            updateStmt.setString(2, username);
            updateStmt.executeUpdate();

            logStmt.setString(1, sourceType);
            logStmt.setString(2, sourceName);
            logStmt.setDouble(3, cashAmount);
            logStmt.setDouble(4, fee);
            logStmt.setDouble(5, netAmount);
            logStmt.executeUpdate();

            conn.commit();

            System.out.println("\n================ CASH IN RECEIPT ================");
            System.out.println("User: " + username);
            System.out.println("Type: Adventurer Cash In");
            System.out.println("Source: " + sourceType + " - " + sourceName);
            System.out.println("Cash: " + cashAmount);
            System.out.println("Fee: " + fee);
            System.out.println("Net Added: " + netAmount);
            System.out.println("New Balance: " + updatedBalance);
            System.out.println("=================================================\n");

            return true;

        } catch (Exception e) {
            conn.rollback();
            System.out.println("Adventurer cash-in error: " + e.getMessage());
            return false;
        }

    } catch (SQLException e) {
        System.out.println("DB error: " + e.getMessage());
        return false;
    }
}
    public boolean updateBalanceOfParty(String partyName,
                           String sourceName,
                           double cashAmount,
                           double fee) {

    String getPartySql = "SELECT party_id, balance FROM Parties WHERE partyname = ?";
    String updatePartySql = "UPDATE Parties SET balance = ? WHERE party_id = ?";
    String insertTransactionSql = """
        INSERT INTO CreditsTransaction
        (source_type, source_name, amount, fee, net_ecash_amount, transaction_type)
        VALUES (?, ?, ?, ?, ?, 'CASH_TO_ECASH')
    """;
    
    String sourceType = "Adventurer";
    
    try (Connection conn = connect()) {

        conn.setAutoCommit(false);

        try (PreparedStatement partyStmt = conn.prepareStatement(getPartySql);
             PreparedStatement updateStmt = conn.prepareStatement(updatePartySql);
             PreparedStatement logStmt = conn.prepareStatement(insertTransactionSql)) {

            partyStmt.setString(1, partyName);
            ResultSet rs = partyStmt.executeQuery();

            if (!rs.next()) {
                conn.rollback();
                return false;
            }

            int partyId = rs.getInt("party_id");
            double balance = rs.getDouble("balance");

            double netAmount = cashAmount - fee;

            if (netAmount <= 0) {
                conn.rollback();
                return false;
            }

            double updatedBalance = balance + netAmount;

            updateStmt.setDouble(1, updatedBalance);
            updateStmt.setInt(2, partyId);
            updateStmt.executeUpdate();

            logStmt.setString(1, sourceType);
            logStmt.setString(2, sourceName);
            logStmt.setDouble(3, cashAmount);
            logStmt.setDouble(4, fee);
            logStmt.setDouble(5, netAmount);
            logStmt.executeUpdate();

            conn.commit();

            System.out.println("\n================ CASH IN RECEIPT ================");
            System.out.println("Party: " + partyName);
            System.out.println("Type: Party Cash In");
            System.out.println("Source: " + sourceType + " - " + sourceName);
            System.out.println("Cash: " + cashAmount);
            System.out.println("Fee: " + fee);
            System.out.println("Net Added: " + netAmount);
            System.out.println("New Party Balance: " + updatedBalance);
            System.out.println("=================================================\n");

            return true;

        } catch (Exception e) {
            conn.rollback();
            System.out.println("Party cash-in error: " + e.getMessage());
            return false;
        }

    } catch (SQLException e) {
        System.out.println("DB error: " + e.getMessage());
        return false;
    }
}
    
    public void recordQuestTransaction(Connection conn,
                                    String sourceType,
                                    String sourceName,
                                    String questName,
                                    double amount,
                                    double discount,
                                    double netAmount) throws SQLException {

          String sql = """
              INSERT INTO QuestTransactions
              (source_type, source_name, quest_name, amount, discount, net_amount, transaction_type)
              VALUES (?, ?, ?, ?, ?, ?, 'INCOME')
          """;

          try (PreparedStatement stmt = conn.prepareStatement(sql)) {
              stmt.setString(1, sourceType);
              stmt.setString(2, sourceName);
              stmt.setString(3, questName);
              stmt.setDouble(4, amount);
              stmt.setDouble(5, discount);
              stmt.setDouble(6, netAmount);
              stmt.executeUpdate();
          }
      }
    
    public void recordCashToECash(Connection conn,
                              String sourceType,
                              String sourceName,
                              double cashAmount,
                              double fee,
                              double netEcashAmount) throws SQLException {

    String sql = """
        INSERT INTO CreditsTransactions
        (source_type, source_name, amount, fee, net_ecash_amount, transaction_type)
        VALUES (?, ?, ?, ?, ?, 'CASH_TO_ECASH')
    """;

    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, sourceType);
        stmt.setString(2, sourceName);
        stmt.setDouble(3, cashAmount);
        stmt.setDouble(4, fee);
        stmt.setDouble(5, netEcashAmount);

        stmt.executeUpdate();
    }
}
        public boolean viewQuestTransactionHistory() {
    String sql = """
        SELECT transaction_id,
               source_type,
               source_name,
               quest_name,
               amount,
               discount,
               net_amount,
               transaction_type
        FROM QuestTransactions
        ORDER BY transaction_id DESC
    """;

    try (Connection conn = connect();
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

        System.out.println("\n=== TRANSACTION HISTORY ===");

        boolean hasData = false;

        while (rs.next()) {
            hasData = true;

            System.out.println("-----------------------------------");
            System.out.println("Transaction ID: " + rs.getInt("transaction_id"));
            System.out.println("Type: " + rs.getString("transaction_type"));
            System.out.println("Source: " + rs.getString("source_type")
                    + " - " + rs.getString("source_name"));
            System.out.println("Quest: " + rs.getString("quest_name"));
            System.out.println("Original Amount: " + rs.getDouble("amount"));
            System.out.println("Discount: " + rs.getDouble("discount"));
            System.out.println("Net Amount Paid: " + rs.getDouble("net_amount"));
        }

        if (!hasData) {
            System.out.println("No transactions found.");
            return false;
        }

        System.out.println("-----------------------------------");
        return true;

    } catch (SQLException e) {
        System.out.println("View transaction error: " + e.getMessage());
        return false;
    }
}
        
public boolean viewCashToECashTransactionHistory() {

    String sql = """
        SELECT transaction_id,
               source_type,
               source_name,
               amount,
               fee,
               net_ecash_amount,
               transaction_type
        FROM CreditsTransaction
        WHERE transaction_type = 'CASH_TO_ECASH'
        ORDER BY transaction_id DESC
    """;

    try (Connection conn = connect();
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

        System.out.println("\n=== CASH TO E-CASH TRANSACTION HISTORY ===");

        boolean hasData = false;

        while (rs.next()) {
            hasData = true;

            System.out.println("-----------------------------------");
            System.out.println("Transaction ID: " + rs.getInt("transaction_id"));
            System.out.println("Type: " + rs.getString("transaction_type"));
            System.out.println("Source: " + rs.getString("source_type")
                    + " - " + rs.getString("source_name"));
            System.out.println("Cash Amount: " + rs.getDouble("amount"));
            System.out.println("Fee: " + rs.getDouble("fee"));
            System.out.println("Net E-Cash Credited: " + rs.getDouble("net_ecash_amount"));
        }

        if (!hasData) {
            System.out.println("No cash-to-e-cash transactions found.");
            return false;
        }

        System.out.println("-----------------------------------");
        return true;

    } catch (SQLException e) {
        System.out.println("View cash-to-e-cash transaction error: " + e.getMessage());
        return false;
    }
}
        public void generateIncomeStatement(double salaries, double ads, double supplies, double expenseOthers) {

            String questSql = "SELECT SUM(amount) AS total FROM QuestTransactions WHERE transaction_type = 'INCOME'";
            String ecashSql = "SELECT SUM(amount) AS total FROM CreditsTransaction WHERE transaction_type = 'CASH_TO_ECASH'";

            double questReservations = 0;
            double cashToEcash = 0;

            double totalIncome = 0;
            double totalExpenses = 0;

            try (Connection conn = connect()) {

                try (PreparedStatement stmt = conn.prepareStatement(questSql);
                     ResultSet rs = stmt.executeQuery()) {

                    if (rs.next()) {
                        questReservations = rs.getDouble("total");
                    }
                }

                try (PreparedStatement stmt = conn.prepareStatement(ecashSql);
                     ResultSet rs = stmt.executeQuery()) {

                    if (rs.next()) {
                        cashToEcash = rs.getDouble("total");
                    }
                }

            } catch (SQLException e) {
                System.out.println("Error: " + e.getMessage());
            }

            totalIncome = questReservations + cashToEcash;
            totalExpenses = salaries + ads + supplies + expenseOthers;

            double netIncome = totalIncome - totalExpenses;

            System.out.println("===== INCOME STATEMENT =====\n");

            System.out.println("INCOME:");
            System.out.println("Quest Reservations: " + questReservations);
            System.out.println("Cash to eCash: " + cashToEcash);
            System.out.println("Total Income: " + totalIncome + "\n");

            System.out.println("EXPENSES:");
            System.out.println("Receptionists' Salaries: " + salaries);
            System.out.println("Advertisements: " + ads);
            System.out.println("Supplies: " + supplies);
            System.out.println("Others: " + expenseOthers);
            System.out.println("Total Expenses: " + totalExpenses + "\n");

            System.out.println("NET INCOME:");
            System.out.println(netIncome);
        }
}