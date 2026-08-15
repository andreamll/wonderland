package com.wonderland.immi.dto;

public class ApplicationResponse {

    private Long id;
    private String decision;
    private String decisionMessage;

    public ApplicationResponse(Long id, String decision, String decisionMessage) {
        this.id = id;
        this.decision = decision;
        this.decisionMessage = decisionMessage;
    }

    public Long getId() {
        return id;
    }

    public String getDecision() {
        return decision;
    }

    public String getDecisionMessage() {
        return decisionMessage;
    }
}