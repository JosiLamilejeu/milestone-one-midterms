package studentdatabase.task3.milestoneone;

import java.util.*;

public class MilestoneOne {
    public static void main(String[] args) {

        Database db = new Database();
        db.connect();

        Scanner s = new Scanner(System.in);

        Repository repo = new Repository();
        GuildSystem gldsys = new GuildSystem(repo);
        AdminSystem admnsys = new AdminSystem();
        gldsys.clearCurrentUser();

        boolean mainMenu = true;

        while (mainMenu) {
            System.out.println("\n=== ADVENTURER'S GUILD MENU ===");
            System.out.println("1. Log in");
            System.out.println("2. Exit");
            System.out.print("Choice: ");

            int choice = s.nextInt();
            s.nextLine();

            switch (choice) {
                case 1:
                    System.out.println("\n=== LOGIN ===");

                    System.out.print("Username: ");
                    String username = s.nextLine();

                    System.out.print("Password: ");
                    String password = s.nextLine();

                    User user = gldsys.loginUser(username, password);

                    if (user != null) {
                        System.out.println("\nLogin successful!");
                        System.out.println("Welcome, " + user.getUsername());
                        System.out.println("Role detected: " + user.getRole());

                        gldsys.saveConfig(username, password, user.getRole());

                        String option;
                        String questName;
                        String partyName;

                        switch (user.getRole()) {
                            case "guildmaster":
                                System.out.println("Redirecting to Guildmaster menu...");
                                boolean guildmasterMenu = true;

                                while (guildmasterMenu) {
                                    System.out.println("\n=== GUILDMASTER MENU ===");
                                    System.out.println("1. Register Receptionist");
                                    System.out.println("2. View Registered Receptionists");
                                    System.out.println("3. Remove Receptionist");
                                    System.out.println("4. Remove Adventurer");
                                    System.out.println("5. Remove Adventurer from a Party");
                                    System.out.println("6. Remove Party");
                                    System.out.println("7. Remove Quest");
                                    System.out.println("8. Remove Quest Reservation");
                                    System.out.println("9. Log out");
                                    System.out.print("Choice: ");
                                    choice = s.nextInt();
                                    s.nextLine();

                                    switch (choice) {
                                        case 1:
                                            System.out.print("Username of the Receptionist: ");
                                            String receptionistUsername = s.nextLine();

                                            System.out.print("Password of the Receptionist: ");
                                            String receptionistPassword = s.nextLine();

                                            if (admnsys.addUser(receptionistUsername, receptionistPassword, "receptionist")) {
                                                System.out.println("Receptionist registered successfully!");
                                            } else {
                                                System.out.println("Failed to register receptionist.");
                                            }
                                            break;
                                        case 2:
                                            repo.viewRegisteredReceptionists();
                                            break;

                                        case 3:
                                            System.out.print("Enter receptionist username to remove: ");
                                            username = s.nextLine();

                                            if (admnsys.removeReceptionist(username)) {
                                                System.out.println("Receptionist removed successfully.");
                                            } else {
                                                System.out.println("Failed to remove receptionist.");
                                            }
                                            break;

                                        case 4:
                                            System.out.print("Enter adventurer username to remove: ");
                                            username = s.nextLine();

                                            if (admnsys.removeAdventurer(username)) {
                                                System.out.println("Adventurer removed successfully.");
                                            } else {
                                                System.out.println("Failed to remove adventurer.");
                                            }
                                            break;

                                        case 5:
                                            System.out.print("Enter party name: ");
                                            partyName = s.nextLine();

                                            System.out.print("Enter adventurer username to remove from party: ");
                                            username = s.nextLine();

                                            if (admnsys.removeAdventurerFromParty(username, partyName)) {
                                                System.out.println("Adventurer removed from party successfully.");
                                            } else {
                                                System.out.println("Failed to remove adventurer from party.");
                                            }
                                            break;

                                        case 6:
                                            System.out.print("Enter party name to remove: ");
                                            partyName = s.nextLine();

                                            if (admnsys.removeParty(partyName)) {
                                                System.out.println("Party removed successfully.");
                                            } else {
                                                System.out.println("Failed to remove party.");
                                            }
                                            break;

                                        case 7:
                                            System.out.print("Enter quest name to remove: ");
                                            questName = s.nextLine();

                                            if (admnsys.removeQuest(questName)) {
                                                System.out.println("Quest removed successfully.");
                                            } else {
                                                System.out.println("Failed to remove quest.");
                                            }
                                            break;

                                        case 8:
                                            System.out.print("Enter quest name to remove reservation: ");
                                            questName = s.nextLine();

                                            if (admnsys.removeReservation(questName)) {
                                                System.out.println("Reservation removed successfully.");
                                            } else {
                                                System.out.println("Failed to remove reservation.");
                                            }
                                            break;

                                        case 9:
                                            if (gldsys.logoutUser()) {
                                                guildmasterMenu = false;
                                                System.out.println("Logged out successfully.");
                                            } else {
                                                System.out.println("Logout failed.");
                                            }
                                            break;

                                        default:
                                            System.out.println("Invalid choice.");
                                            break;
                                    }
                                }
                                break;

                            case "receptionist":
                                System.out.println("Redirecting to Receptionist menu...");
                                boolean receptionistMenu = true;

                                while (receptionistMenu) {
                                    System.out.println("\n=== RECEPTIONIST MENU ===");
                                    System.out.println("1. Register Adventurer");
                                    System.out.println("2. View Registered Adventurers");
                                    System.out.println("3. Update Adventurer");
                                    System.out.println("4. Create Party");
                                    System.out.println("5. Add Party Member");
                                    System.out.println("6. Create Quest");
                                    System.out.println("7. Update Quests");
                                    System.out.println("8. Log out");
                                    System.out.print("Choice: ");
                                    choice = s.nextInt();
                                    s.nextLine();

                                    switch (choice) {
                                        case 1:
                                            System.out.print("How old are you? ");
                                            int age = s.nextInt();
                                            s.nextLine();

                                            if (age > 17) {
                                                System.out.println("Age is applicable!");

                                                System.out.print("Username of the Adventurer: ");
                                                String adventurerUsername = s.nextLine();

                                                System.out.print("Password of the Adventurer: ");
                                                String adventurerPassword = s.nextLine();

                                                System.out.print("Race of the Adventurer: ");
                                                String adventurerRace = s.nextLine();

                                                System.out.print("Class of the Adventurer: ");
                                                String adventurerClass = s.nextLine();

                                                if (admnsys.addAdventurer(adventurerUsername, adventurerPassword, adventurerRace, adventurerClass)) {
                                                    System.out.println("Adventurer registered successfully!");
                                                } else {
                                                    System.out.println("Failed to register adventurer.");
                                                }
                                            } else {
                                                System.out.println("Age is not appropriate.");
                                            }
                                            break;
                                        case 2:
                                            repo.viewRegisteredAdventurers();
                                            break;

                                        case 3:
                                            System.out.print("Enter adventurer username: ");
                                            username = s.nextLine();

                                            System.out.print("Enter new rank: ");
                                            String newRank = s.nextLine();

                                            if (gldsys.updateAdventurerRank(username, newRank)) {
                                                System.out.println("Adventurer rank updated successfully!");
                                            } else {
                                                System.out.println("Failed to update adventurer rank.");
                                            }
                                            break;

                                        case 4:
                                            System.out.print("Name of the party: ");
                                            partyName = s.nextLine();

                                            if (admnsys.addParty(partyName)) {
                                                System.out.println("Party created successfully!");

                                                do {
                                                    System.out.print("Enter member username: ");
                                                    username = s.nextLine();

                                                    if (admnsys.addPartyMember(partyName, username)) {
                                                        System.out.println("Member added successfully!");
                                                    } else {
                                                        System.out.println("Failed to add member.");
                                                    }

                                                    System.out.print("Add another member? (yes/no): ");
                                                    option = s.nextLine();

                                                } while (option.equalsIgnoreCase("yes"));
                                            } else {
                                                System.out.println("Failed to create party.");
                                            }
                                            break;

                                        case 5:
                                            System.out.print("Enter existing party name: ");
                                            partyName = s.nextLine();

                                            do {
                                                System.out.print("Enter username to add: ");
                                                username = s.nextLine();

                                                if (admnsys.addPartyMember(partyName, username)) {
                                                    System.out.println("Member added successfully!");
                                                } else {
                                                    System.out.println("Failed to add member.");
                                                }

                                                System.out.print("Add another member? (yes/no): ");
                                                option = s.nextLine();

                                            } while (option.equalsIgnoreCase("yes"));
                                            break;

                                        case 6:
                                            do {
                                                System.out.print("Enter quest name: ");
                                                questName = s.nextLine();

                                                if (admnsys.addQuest(questName)) {
                                                    System.out.println("Quest added successfully!");
                                                } else {
                                                    System.out.println("Failed to add quest.");
                                                }

                                                System.out.print("Add another quest? (yes/no): ");
                                                option = s.nextLine();

                                            } while (option.equalsIgnoreCase("yes"));
                                            break;

                                        case 7:
                                            System.out.print("Enter quest name to mark as completed: ");
                                            questName = s.nextLine();

                                            if (gldsys.completeQuest(questName)) {
                                                System.out.println("Quest marked as completed!");
                                            } else {
                                                System.out.println("Failed to complete quest.");
                                            }
                                            break;

                                        case 8:
                                            if (gldsys.logoutUser()) {
                                                receptionistMenu = false;
                                                System.out.println("Logged out successfully.");
                                            } else {
                                                System.out.println("Logout failed.");
                                            }
                                            break;

                                        default:
                                            System.out.println("Invalid choice.");
                                            break;
                                    }
                                }
                                break;

                            case "adventurer":
                                System.out.println("Redirecting to Adventurer menu...");
                                boolean adventurerMenu = true;

                                while (adventurerMenu) {
                                    System.out.println("\n=== ADVENTURER MENU ===");
                                    System.out.println("1. View Quests");
                                    System.out.println("2. Reserve Quests");
                                    System.out.println("3. View Party");
                                    System.out.println("4. View Profile");
                                    System.out.println("5. Log out");
                                    System.out.print("Choice: ");
                                    choice = s.nextInt();
                                    s.nextLine();

                                    switch (choice) {
                                        case 1:
                                            System.out.println("\nFetching quest list...");
                                            repo.viewQuestList();
                                            break;

                                        case 2:
                                            repo.viewQuestList();
                                            System.out.print("Enter quest name: ");
                                            questName = s.nextLine();

                                            partyName = repo.getPartyNameByUsername(user.getUsername());

                                            if (partyName == null) {
                                                System.out.println("You are not in a party.");
                                                System.out.println("Reserving quest as adventurer...");

                                                if (gldsys.reserveQuestByAdventurer(questName, user.getUsername())) {
                                                    System.out.println("Quest reserved successfully!");
                                                } else {
                                                    System.out.println("Failed to reserve quest.");
                                                }

                                            } else {
                                                System.out.println("You are in party: " + partyName);
                                                System.out.println("Reserve as:");
                                                System.out.println("1. Myself");
                                                System.out.println("2. My Party");
                                                System.out.print("Choice: ");
                                                int reserveChoice = Integer.parseInt(s.nextLine());

                                                switch (reserveChoice) {
                                                    case 1:
                                                        if (gldsys.reserveQuestByAdventurer(questName, user.getUsername())) {
                                                            System.out.println("Quest reserved successfully for yourself!");
                                                        } else {
                                                            System.out.println("Failed to reserve quest for yourself.");
                                                        }
                                                        break;

                                                    case 2:
                                                        if (gldsys.reserveQuestByParty(questName, partyName)) {
                                                            System.out.println("Quest reserved successfully for party!");
                                                        } else {
                                                            System.out.println("Failed to reserve quest for party.");
                                                        }
                                                        break;

                                                    default:
                                                        System.out.println("Invalid choice.");
                                                        break;
                                                }
                                            }
                                            break;

                                        case 3:
                                            System.out.println("\nFetching your party details...");
                                            repo.viewCurrentUserPartyList(user.getUsername());
                                            break;

                                        case 4:
                                            if (user != null && user.getRole().equalsIgnoreCase("adventurer")) {
                                                System.out.println("\nFetching your profile...");
                                                repo.viewCurrentAdventurerDetails(user.getUsername());
                                            } else {
                                                System.out.println("Only adventurers can view adventurer details.");
                                            }
                                            break;

                                        case 5:
                                            if (gldsys.logoutUser()) {
                                                adventurerMenu = false;
                                                System.out.println("Logged out successfully.");
                                            } else {
                                                System.out.println("Logout failed.");
                                            }
                                            break;

                                        default:
                                            System.out.println("Invalid choice.");
                                            break;
                                    }
                                }
                                break;

                            default:
                                System.out.println("Unknown role.");
                                break;
                        }
                    } else {
                        System.out.println("Invalid username or password.");
                    }
                    break;

                case 2:
                    System.out.println("Exiting program...");
                    mainMenu = false;
                    break;

                default:
                    System.out.println("Invalid choice.");
                    break;
            }
        }

        s.close();
    }
}
