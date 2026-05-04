package  com.mycompany.milestoneone;

import java.util.*;

public class MilestoneProject {
    public static void main(String[] args) {

        Scanner s = new Scanner(System.in);
        Repository repo = Repository.getInstance();

        repo.clearCurrentUser();

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

                    User user = repo.findByCredentials(username, password);

                    if (user != null) {
                        System.out.println("\nLogin successful!");
                        System.out.println("Welcome, " + user.getUsername());
                        System.out.println("Role detected: " + user.getRole());

                        repo.saveConfig(username, password, user.getRole());

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
                                    System.out.println("4. View Adventurers");
                                    System.out.println("5. Remove Adventurer");
                                    System.out.println("6. Remove Adventurer from a Party");
                                    System.out.println("7. Remove Party");
                                    System.out.println("8. View Quests");
                                    System.out.println("9. Remove Quest");
                                    System.out.println("10. Remove Quest Reservation");
                                    System.out.println("11. View Transaction History");
                                    System.out.println("12. Show Income Statement");
                                    System.out.println("13. Log out");
                                    System.out.print("Choice: ");
                                    choice = s.nextInt();
                                    s.nextLine();

                                    switch (choice) {
                                        case 1:
                                            System.out.print("Username of the Receptionist: ");
                                            String receptionistUsername = s.nextLine();

                                            System.out.print("Password of the Receptionist: ");
                                            String receptionistPassword = s.nextLine();

                                            if (repo.addUser(receptionistUsername, receptionistPassword, "receptionist")) {
                                                System.out.println("Receptionist registered successfully!");
                                            } else {
                                                System.out.println("Failed to register receptionist.");
                                            }
                                            break;

                                        case 2:
                                            if (repo.viewRegisteredReceptionists()) {
                                                System.out.println("Receptionist list loaded successfully.");
                                            } else {
                                                System.out.println("No registered receptionists found.");
                                            }
                                            break;

                                        case 3:
                                            System.out.print("Enter receptionist username to remove: ");
                                            username = s.nextLine();

                                            if (repo.removeReceptionist(username)) {
                                                System.out.println("Receptionist removed successfully.");
                                            } else {
                                                System.out.println("Failed to remove receptionist.");
                                            }
                                            break;
                                        case 4:
                                            if (repo.viewRegisteredAdventurers()) {
                                                System.out.println("Adventurer list loaded successfully.");
                                            } else {
                                                System.out.println("No registered adventurers found.");
                                            }
                                            break;
                                        case 5:
                                            System.out.print("Enter adventurer username to remove: ");
                                            username = s.nextLine();

                                            if (repo.removeAdventurer(username)) {
                                                System.out.println("Adventurer removed successfully.");
                                            } else {
                                                System.out.println("Failed to remove adventurer.");
                                            }
                                            break;

                                        case 6:
                                            System.out.print("Enter party name: ");
                                            partyName = s.nextLine();

                                            System.out.print("Enter adventurer username to remove from party: ");
                                            username = s.nextLine();

                                            if (repo.removeAdventurerFromParty(username, partyName)) {
                                                System.out.println("Adventurer removed from party successfully.");
                                            } else {
                                                System.out.println("Failed to remove adventurer from party.");
                                            }
                                            break;

                                        case 7:
                                            System.out.print("Enter party name to remove: ");
                                            partyName = s.nextLine();

                                            if (repo.removeParty(partyName)) {
                                                System.out.println("Party removed successfully.");
                                            } else {
                                                System.out.println("Failed to remove party.");
                                            }
                                            break;
                                        case 8:
                                            System.out.println("\nFetching quest list...");
                                            if (repo.viewQuestList()) {
                                                System.out.println("Quest list loaded successfully.");
                                            } else {
                                                System.out.println("No quests found.");
                                            }
                                            break;
                                        case 9:
                                            System.out.print("Enter quest name to remove: ");
                                            questName = s.nextLine();

                                            if (repo.removeQuest(questName)) {
                                                System.out.println("Quest removed successfully.");
                                            } else {
                                                System.out.println("Failed to remove quest.");
                                            }
                                            break;

                                        case 10:
                                            System.out.print("Enter quest name to remove reservation: ");
                                            questName = s.nextLine();

                                            if (repo.removeReservation(questName)) {
                                                System.out.println("Reservation removed successfully.");
                                            } else {
                                                System.out.println("Failed to remove reservation.");
                                            }
                                            break;
                                        case 11:
                                            repo.viewQuestTransactionHistory();
                                            repo.viewCashToECashTransactionHistory();
                                            break;
                                        case 12:
                                            System.out.print("Salary for the Receptionists: ");
                                            double salaries = s.nextDouble();
                                            System.out.print("Payment for ads: ");
                                            double ads = s.nextDouble();
                                            System.out.print("Payment for Supplies: ");
                                            double supplies = s.nextDouble();
                                            System.out.print("Payment for other expenses: ");
                                            double expenseOthers = s.nextDouble();
                                            repo.generateIncomeStatement(salaries, ads, supplies, expenseOthers);
                                            break;
                                        case 13:
                                            if (repo.logoutUser()) {
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
                                    System.out.println("7. View Quests");
                                    System.out.println("8. Update Quests");
                                    System.out.println("9. Change Balance");
                                    System.out.println("10. View Transaction History");
                                    System.out.println("11. Log out");
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

                                                if (repo.addAdventurer(adventurerUsername, adventurerPassword, adventurerRace, adventurerClass)) {
                                                    System.out.println("Adventurer registered successfully!");
                                                } else {
                                                    System.out.println("Failed to register adventurer.");
                                                }
                                            } else {
                                                System.out.println("Age is not appropriate.");
                                            }
                                            break;

                                        case 2:
                                            if (repo.viewRegisteredAdventurers()) {
                                                System.out.println("Adventurer list loaded successfully.");
                                            } else {
                                                System.out.println("No registered adventurers found.");
                                            }
                                            break;

                                        case 3:
                                            System.out.print("Enter adventurer username: ");
                                            username = s.nextLine();

                                            System.out.print("Enter new rank: ");
                                            String newRank = s.nextLine();

                                            if (repo.updateAdventurerRank(username, newRank)) {
                                                System.out.println("Adventurer rank updated successfully!");
                                            } else {
                                                System.out.println("Failed to update adventurer rank.");
                                            }
                                            break;

                                        case 4:
                                            System.out.print("Name of the party: ");
                                            partyName = s.nextLine();

                                            if (repo.addParty(partyName)) {
                                                System.out.println("Party created successfully!");
                                                    System.out.print("Enter member username: ");
                                                    username = s.nextLine();

                                                    if (repo.addPartyMember(partyName, username)) {
                                                        System.out.println("Member added successfully!");
                                                    } else {
                                                        System.out.println("Failed to add member.");
                                                    }
                                            } else {
                                                System.out.println("Failed to create party.");
                                            }
                                            break;

                                        case 5:
                                            System.out.print("Enter existing party name: ");
                                            partyName = s.nextLine();

                                            System.out.print("Enter username to add: ");
                                            username = s.nextLine();

                                                if (repo.addPartyMember(partyName, username)) {
                                                    System.out.println("Member added successfully!");
                                                } else {
                                                    System.out.println("Failed to add member.");
                                                }
                                            break;

                                        case 6:
                                                System.out.print("Enter quest name: ");
                                                questName = s.nextLine();
                                                
                                                System.out.print("Fee for Reservation: ");
                                                double questFee = s.nextDouble();

                                                if (repo.addQuest(questName, questFee)) {
                                                    System.out.println("Quest added successfully!");
                                                } else {
                                                    System.out.println("Failed to add quest.");
                                                }
                                            break;
                                        case 7:
                                            System.out.println("\nFetching quest list...");
                                            if (repo.viewQuestList()) {
                                                System.out.println("Quest list loaded successfully.");
                                            } else {
                                                System.out.println("No quests found.");
                                            }
                                            break;
                                        case 8:
                                            System.out.print("Enter quest name to mark as completed: ");
                                            questName = s.nextLine();

                                            if (repo.completeQuest(questName)) {
                                                System.out.println("Quest marked as completed!");
                                            } else {
                                                System.out.println("Failed to complete quest.");
                                            }
                                            break;
                                        case 9:
                                            System.out.println("Change balance of?");
                                            System.out.println("1. Adventurer");
                                            System.out.println("2. Party");
                                            System.out.print("Choice: ");
                                            choice = s.nextInt();
                                            
                                            switch (choice) {
                                                case 1:
                                                    System.out.print("Enter adventurer username: ");
                                                    username = s.next();
                                                    
                                                    System.out.print("Enter Adventurer's username: ");
                                                    String sourceName = s.next();
                                                        
                                                    System.out.print("Enter balance: ");
                                                    double cashAmount = s.nextDouble();
                                                    
                                                    System.out.print("Enter fee: ");
                                                    double fee = s.nextDouble();

                                                    if (repo.updateBalance(username, sourceName, cashAmount,fee)) {
                                                        System.out.println("Adventurer's balance changed successfully!");
                                                    } else {
                                                        System.out.println("Failed to change adventurer's balance.");
                                                    }
                                                    break;
                                                case 2:
                                                    System.out.print("Enter party name: ");
                                                    partyName = s.next();
                                                    
                                                    System.out.print("Enter Adventurer's username: ");
                                                    sourceName = s.next();
                                                    
                                                    System.out.print("Enter balance: ");
                                                    cashAmount = s.nextDouble();
                                                    
                                                    System.out.print("Enter Fee: ");
                                                    fee = s.nextDouble();
                                                    
                                                    if (repo.updateBalanceOfParty(partyName, sourceName, cashAmount, fee)) {
                                                        System.out.println("Party's balance changed successfully!");
                                                    } else {
                                                        System.out.println("Failed to change adventurer's balance.");
                                                    }
                                            }
                                            break;
                                        case 10:
                                            repo.viewQuestTransactionHistory();
                                            repo.viewCashToECashTransactionHistory();
                                            break;
                                        case 11:
                                            if (repo.logoutUser()) {
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
                                            if (repo.viewQuestList()) {
                                                System.out.println("Quest list loaded successfully.");
                                            } else {
                                                System.out.println("No quests found.");
                                            }
                                            break;

                                        case 2:
                                            if (!repo.viewQuestList()) {
                                                System.out.println("No quests available.");
                                                break;
                                            }

                                            System.out.print("Enter quest name: ");
                                            questName = s.nextLine();

                                            partyName = repo.getPartyNameByUsername(user.getUsername());

                                            if (partyName == null) {
                                                System.out.println("You are not in a party.");
                                                System.out.println("Reserving quest as adventurer...");

                                                if (repo.reserveQuestByAdventurer(questName, user.getUsername())) {
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
                                                        if (repo.reserveQuestByAdventurer(questName, user.getUsername())) {
                                                            System.out.println("Quest reserved successfully for yourself!");
                                                        } else {
                                                            System.out.println("Failed to reserve quest for yourself.");
                                                        }
                                                        break;

                                                    case 2:
                                                        if (repo.reserveQuestByParty(questName, partyName)) {
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
                                            if (repo.viewCurrentUserPartyList(user.getUsername())) {
                                                System.out.println("Party details loaded successfully.");
                                            } else {
                                                System.out.println("You are not in any party.");
                                            }
                                            break;

                                        case 4:
                                            if (user != null && user.getRole().equalsIgnoreCase("adventurer")) {
                                                System.out.println("\nFetching your profile...");
                                                if (repo.viewCurrentAdventurerDetails(user.getUsername())) {
                                                    System.out.println("Profile loaded successfully.");
                                                } else {
                                                    System.out.println("No adventurer details found.");
                                                }
                                            } else {
                                                System.out.println("Only adventurers can view adventurer details.");
                                            }
                                            break;

                                        case 5:
                                            if (repo.logoutUser()) {
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