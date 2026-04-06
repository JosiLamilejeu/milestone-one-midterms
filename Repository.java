//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import java.sql.*;

/**
 *
 * @author kludy
 */

public class Repository {

    public User findByCredentials(String username, String password) {
        String sql = "SELECT username, password, role FROM users WHERE username = ? AND password = ?";

        try (Connection conn = Database.connect();
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

    public boolean viewQuestList() {
        String sql = "SELECT quest_name, reservation_status, reserved_by_type, reserved_by_name FROM Quests";

        try (Connection conn = Database.connect();
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

    public boolean viewCurrentAdventurerDetails(String username) {
        String sql = """
            SELECT u.username, a.race, a.class, a.rank
            FROM Users u
            JOIN AdventurerDetails a ON u.username = a.username
            WHERE u.username = ? AND u.role = 'adventurer'
            """;

        try (Connection conn = Database.connect();
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

    public String getPartyNameByUsername(String usernames) {
        String sql = """
            SELECT p.partyname
            FROM Parties p
            JOIN PartyMembers pm ON p.party_id = pm.party_id
            WHERE pm.usernames = ?
            """;

        try (Connection conn = Database.connect();
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

        try (Connection conn = Database.connect();
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

    public boolean viewRegisteredAdventurers() {
        String sql = """
            SELECT u.username, a.race, a.class, a.rank
            FROM Users u
            JOIN AdventurerDetails a ON u.username = a.username
            WHERE u.role = ?
            """;

        try (Connection conn = Database.connect();
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

        try (Connection conn = Database.connect();
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
}
