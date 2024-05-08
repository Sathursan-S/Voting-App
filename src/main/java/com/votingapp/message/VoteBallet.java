package com.votingapp.message;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Builder
@Getter
@Setter
public class VoteBallet implements Serializable {
    private Map<Integer, Integer> votes;
    private Map<Integer, String> candidates;

    public synchronized void addVote(Integer candidate) {
        votes.put(candidate, votes.get(candidate) + 1);
    }

    public void initVotes() {
        for (Integer candidateId : this.candidates.keySet()) {
            votes.put(candidateId, 0);
        }
    }
}
