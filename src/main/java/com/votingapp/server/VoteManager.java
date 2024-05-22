package com.votingapp.server;

import com.votingapp.common.Message;
import com.votingapp.common.MessageType;
import com.votingapp.common.VoteBallot;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
public class VoteManager {
    private final VoteBallot voteBallot;
    private boolean isVotingOpen = false;

    public static VoteManager voteManagerInstance;

    public static VoteManager getInstance() {
        if (voteManagerInstance == null) {
            voteManagerInstance = new VoteManager();
        }
        return voteManagerInstance;
    }

    private VoteManager() {
        this.voteBallot = VoteBallot.getInstance();
    }

    public void addCandidate(Integer candidateId, String candidateName) {
        if (!isVotingOpen) {
            try {
                this.voteBallot.addCandidate(candidateId, candidateName);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("Cannot add candidates during an active voting session.");
        }
    }

    public void removeCandidate(Integer candidateId) {
        if (!isVotingOpen) {
            this.voteBallot.removeCandidate(candidateId);
        } else {
            System.out.println("Cannot remove candidates during an active voting session.");
        }
    }

    public void startVoting() {
        if (this.voteBallot.getCandidates().isEmpty()) {
            System.out.println("No candidates available for voting.");
            throw new RuntimeException("No candidates available for voting.");
        }
        this.isVotingOpen = true;
        this.voteBallot.initVotes();

        Message message = new Message(MessageType.VOTING_REQUEST, new TreeMap<>(this.voteBallot.getCandidates()));
        try {
            Server.startVoting(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    public String reportResults() {
        StringBuilder results = new StringBuilder();
        if (!isVotingOpen) {
            ConcurrentHashMap<Integer, Integer> votes = this.voteBallot.getVotes();
            int maxVotes = 0;
            List<Integer> winners = new ArrayList<>();

            for (ConcurrentHashMap.Entry<Integer, Integer> entry : votes.entrySet()) {
                if (entry.getValue() > maxVotes) {
                    maxVotes = entry.getValue();
                    winners.clear();
                    winners.add(entry.getKey());
                } else if (entry.getValue() == maxVotes) {
                    winners.add(entry.getKey());
                }
            }

            results.append("\nElection Results:\n");
            results.append("----------------\n");
            for (ConcurrentHashMap.Entry<Integer, String> entry : this.voteBallot.getCandidates().entrySet()) {
                results.append(String.format("%s: %s %s\n", entry.getKey(), entry.getValue(), getBar(votes.getOrDefault(entry.getKey(), 0), maxVotes)));
            }

            if (!winners.isEmpty()) {
                if (winners.size() == 1) {
                    Integer winner = winners.get(0);
                    results.append(String.format("\nThe winner is: %s with %d votes.\n", this.voteBallot.getCandidates().get(winner), maxVotes));
                } else {
                    results.append("There is a tie between the following candidates:\n");
                    for (Integer winner : winners) {
                        results.append(String.format("%s (ID: %d) with %d votes\n", this.voteBallot.getCandidates().get(winner), winner, maxVotes));
                    }
                }
            } else {
                results.append("No votes were cast.\n");
            }
        } else {
            results.append("Voting is still open, cannot report results yet.\n");
        }

        Message electionResults = new Message(MessageType.VOTING_RESULTS, results.toString());
        try {
            Server.broadcastMessage(electionResults);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return results.toString();
    }

    private String getBar(int count, int max) {
        StringBuilder bar = new StringBuilder();
        int scale = Math.max(1, 10 / Math.max(max, 1));
        for (int i = 0; i < count * scale; i++) {
            bar.append("█");
        }
        return bar.toString() + " " + count;
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

    public ConcurrentHashMap<Integer, Integer> getResults() {
        return this.voteBallot.getVotes();
    }

    public void startCounting() {
        if (isVotingOpen) {
            System.out.println("Voting is still open, cannot count results yet.");
        }
    }
}
