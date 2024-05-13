package com.votingapp.server;

import com.votingapp.common.Message;
import com.votingapp.common.MessageType;
import com.votingapp.common.VoteBallot;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
public class VoteManager {
    private VoteBallot voteBallot;
    private boolean isVotingOpen = false;

    public VoteManager() {
        // Initially, there are no candidates or votes
        this.voteBallot = new VoteBallot(new ConcurrentHashMap<>());
    }

    public void addCandidate(Integer candidateId, String candidateName) {
        if (!isVotingOpen) {
            this.voteBallot.getCandidates().put(candidateId, candidateName);
            this.voteBallot.getVotes().put(candidateId, 0);
        } else {
            System.out.println("Cannot add candidates during an active voting session.");
        }
    }

    public void removeCandidate(Integer candidateId) {
        if (!isVotingOpen) {
            this.voteBallot.getCandidates().remove(candidateId);
            this.voteBallot.getVotes().remove(candidateId);
        } else {
            System.out.println("Cannot remove candidates during an active voting session.");
        }
    }

    public void startVoting() {
        if (this.voteBallot.getCandidates().isEmpty()) {
            System.out.println("No candidates available for voting.");
            return;
        }
        this.isVotingOpen = true;
        this.voteBallot.initVotes(); // Reset votes to zero before starting
        System.out.println("Voting has started.");
    }

    public void stopVoting() {
        if (isVotingOpen) {
            this.isVotingOpen = false;
            System.out.println("Voting has stopped.");
            reportResults();
        } else {
            System.out.println("No active voting session to stop.");
        }
    }

    public boolean castVote(Integer candidateId) {
        if (isVotingOpen && this.voteBallot.getCandidates().containsKey(candidateId)) {
            this.voteBallot.addVote(candidateId);
            return true;
        }
        System.out.println("Voting is either closed or candidate does not exist.");
        return false;
    }

    public void reportResults() {
        if (!isVotingOpen) {
            int maxVotes = 0;
            Integer winner = null;
            for (Map.Entry<Integer, Integer> entry : this.voteBallot.getVotes().entrySet()) {
                if (entry.getValue() > maxVotes) {
                    maxVotes = entry.getValue();
                    winner = entry.getKey();
                }
            }
            if (winner != null) {
                System.out.println("The winner is: " + this.voteBallot.getCandidates().get(winner) + " with " + maxVotes + " votes.");
            } else {
                System.out.println("No votes were cast.");
            }
        } else {
            System.out.println("Voting is still open, cannot report results yet.");
        }
    }

    public void updateCandidate(int i, String name) {
        if (!isVotingOpen) {
            this.voteBallot.getCandidates().put(i, name);
        } else {
            System.out.println("Cannot update candidates during an active voting session.");
        }
    }

    public void deleteCandidate(int i) {
        if (!isVotingOpen) {
            this.voteBallot.getCandidates().remove(i);
            this.voteBallot.getVotes().remove(i);
        } else {
            System.out.println("Cannot delete candidates during an active voting session.");
        }
    }
}
