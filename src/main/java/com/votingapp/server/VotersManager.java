package com.votingapp.server;

import com.votingapp.client.User;

import java.util.ArrayList;
import java.util.List;

public class VotersManager {
    private  final List<User> voters = new ArrayList<>();

    public VotersManager() {
        addVoter(new User("Sathu", "4196"));
        addVoter(new User("Himosh", "3967"));
        addVoter(new User("Shathu", "4216"));
        addVoter(new User("Voter4", "4567"));
        addVoter(new User("Voter5", "5678"));
    }

    public void addVoter(User user) {
        voters.add(user);
        System.out.println("Voter added: " + voters);
    }

    public boolean authenticateVoter(User user) {
        if (voters.isEmpty()) {
            return false;
        }
        User user1 = voters.stream()
                .filter(voter -> voter.getVoterID().equals(user.getVoterID()))
                .findFirst()
                .orElse(null);
        if (user1 != null) {
            System.out.println("Voter authenticated: " + user1);
            user1.setLogin(user1.getPassword().equals(user.getPassword()));
            return user1.isLogin();
        }
        return false;
    }

    public List<User> getVoters() {
        return voters;
    }

    public void setVoters(List<User> voters) {
        this.voters.clear();
        this.voters.addAll(voters);
    }

    public void clearVoters() {
        voters.clear();
    }

    public void updateVoter(User user) {
        User user1 = voters.stream()
                .filter(voter -> voter.getVoterID() == user.getVoterID())
                .findFirst()
                .orElse(null);
        if (user1 != null) {
            user1.setPassword(user.getPassword());
        }
    }

    public void deleteVoter(String voterId) {
        voters.removeIf(voter -> voter.getVoterID().equals(voterId));
    }

    public void removeClient(String voterId) {
        voters.stream()
                .filter(voter -> voter.getVoterID().equals(voterId))
                .findFirst()
                .ifPresent(voter -> voter.setLogin(false));
    }
}
