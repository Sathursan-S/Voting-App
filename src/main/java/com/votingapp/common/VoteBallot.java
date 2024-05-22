package com.votingapp.common;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class VoteBallot implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final ConcurrentHashMap<Integer, String> candidates;
    private final ConcurrentHashMap<Integer, Integer> votes;

    public static VoteBallot instance;

    public static VoteBallot getInstance() {
        if (instance == null) {
            instance = new VoteBallot();
        }
        return instance;
    }

    private VoteBallot() {
        this.candidates = new ConcurrentHashMap<>();
        this.votes = new ConcurrentHashMap<>();
    }

    private VoteBallot(ConcurrentHashMap<Integer, String> candidates) {
        this.candidates = new ConcurrentHashMap<>(candidates);
        this.votes = new ConcurrentHashMap<>();
        for (Integer id : this.candidates.keySet()) {
            this.votes.put(id, 0);
        }
    }

    public synchronized void addCandidate(Integer candidateId, String candidateName) throws IllegalArgumentException {
        if (candidateId == null || candidateName == null) {
            throw new IllegalArgumentException("Candidate ID and name must not be null");
        }
        VoteBallot instance = getInstance();
        instance.candidates.put(candidateId, candidateName);
        instance.votes.put(candidateId, 0);
    }

    public void removeCandidate(Integer candidateId) {
        instance.candidates.remove(candidateId);
        instance.votes.remove(candidateId);
    }

    public void addVote(Integer candidateId) {
        instance.votes.merge(candidateId, 1, Integer::sum);
    }

    public void initVotes() {
        for (Integer candidateId : instance.candidates.keySet()) {
            instance.votes.put(candidateId, 0);
        }
    }
}
