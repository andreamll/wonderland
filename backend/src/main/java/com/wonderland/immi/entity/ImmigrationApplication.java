package com.wonderland.immi.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "applications")
public class ImmigrationApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String dob;
    private String nationality;
    private String passport;
    private String visaType;

    private String decision;
    private String decisionMessage;

    private LocalDateTime createdAt;

    public ImmigrationApplication() {}

    public Long getId() { return id; }

    public String getFullName() { return fullName; }

    public String getDob() { return dob; }

    public String getNationality() { return nationality; }

    public String getPassport() { return passport; }

    public String getVisaType() { return visaType; }

    public String getDecision() { return decision; }

    public String getDecisionMessage() { return decisionMessage; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }

    public void setFullName(String fullName) { this.fullName = fullName; }

    public void setDob(String dob) { this.dob = dob; }

    public void setNationality(String nationality) { this.nationality = nationality; }

    public void setPassport(String passport) { this.passport = passport; }

    public void setVisaType(String visaType) { this.visaType = visaType; }

    public void setDecision(String decision) { this.decision = decision; }

    public void setDecisionMessage(String decisionMessage) { this.decisionMessage = decisionMessage; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}