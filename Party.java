import java.util.*;

public class Party {
    private final String partyName;
    
    public Party(String partyName) {
        this.partyName = partyName;
    }
    
    public String getPartyName() {
        return partyName;
    }   
    
    private List<User> members = new ArrayList<>();

    public void addMember(User user) {
        members.add(user);
    }

    public void showMembers() {
        for (User user : members) {
            System.out.println(user.getUsername());
        }
    }