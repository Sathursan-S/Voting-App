package com.votingapp.common;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

public class VoteBallot implements Serializable {
    private static final long serialVersionUID = 1L;
    private ConcurrentHashMap<Integer, String> candidates;
    private ConcurrentHashMap<Integer, Integer> votes;

    // Constructor that initializes the maps
    public VoteBallot(ConcurrentHashMap<Integer, String> candidates) {
        this.candidates = new ConcurrentHashMap<>(candidates);
        this.votes = new ConcurrentHashMap<>();
        for (Integer id : this.candidates.keySet()) {
            this.votes.put(id, 0);  // Initialize vote counts to 0 for each candidate
        }
    }

    // Add vote to a candidate
    public void addVote(Integer candidateId) {
        votes.merge(candidateId, 1, Integer::sum); // Safely increment vote count in a thread-safe manner
    }

    // Getters for both maps
    public ConcurrentHashMap<Integer, String> getCandidates() {
        return candidates;
    }

    public ConcurrentHashMap<Integer, Integer> getVotes() {
        return votes;
    }

    // Setters for both maps
    public void setCandidates(ConcurrentHashMap<Integer, String> candidates) {
        this.candidates = candidates;
    }

    public void setVotes(ConcurrentHashMap<Integer, Integer> votes) {
        this.votes = votes;
    }

    // Method to reset votes
    public void initVotes() {
        for (Integer candidateId : candidates.keySet()) {
            votes.put(candidateId, 0);
        }
    }
}
