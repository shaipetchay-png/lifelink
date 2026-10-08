package com.lifelink.lifelink.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "eligibility_assessments")
public class EligibilityAssessment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "donor_id", nullable = false) private User donor;
    @Column(nullable = false) private Double weightKg;
    private Boolean feelingWell; private Boolean meetsAgeRequirement; private Boolean meetsWeightRequirement;
    private Boolean waitingPeriodComplete; private String previousDonation; private LocalDate lastDonationDate;
    private Boolean feverOrIllness; private Boolean takingMedication; private Boolean recentSurgery;
    private Boolean tattooOrPiercingRecently; private Boolean medicalCondition; private Boolean advisedNotToDonate;
    private Boolean recentVaccination; private Boolean recentDentalProcedure; private Boolean recentTravel;
    private Boolean transfusionOrTransplant; private String pregnancyStatus;
    private Integer bloodReadinessScore = 0;
    @Column(nullable = false) private String result = "IN_PROCESS";
    @Column(nullable = false) private LocalDateTime assessedAt = LocalDateTime.now();

    public Long getId(){return id;} public User getDonor(){return donor;} public void setDonor(User v){donor=v;}
    public Double getWeightKg(){return weightKg;} public void setWeightKg(Double v){weightKg=v;}
    public Boolean getFeelingWell(){return feelingWell;} public void setFeelingWell(Boolean v){feelingWell=v;}
    public Boolean getMeetsAgeRequirement(){return meetsAgeRequirement;} public void setMeetsAgeRequirement(Boolean v){meetsAgeRequirement=v;}
    public Boolean getMeetsWeightRequirement(){return meetsWeightRequirement;} public void setMeetsWeightRequirement(Boolean v){meetsWeightRequirement=v;}
    public Boolean getWaitingPeriodComplete(){return waitingPeriodComplete;} public void setWaitingPeriodComplete(Boolean v){waitingPeriodComplete=v;}
    public String getPreviousDonation(){return previousDonation;} public void setPreviousDonation(String v){previousDonation=v;}
    public LocalDate getLastDonationDate(){return lastDonationDate;} public void setLastDonationDate(LocalDate v){lastDonationDate=v;}
    public Boolean getFeverOrIllness(){return feverOrIllness;} public void setFeverOrIllness(Boolean v){feverOrIllness=v;}
    public Boolean getTakingMedication(){return takingMedication;} public void setTakingMedication(Boolean v){takingMedication=v;}
    public Boolean getRecentSurgery(){return recentSurgery;} public void setRecentSurgery(Boolean v){recentSurgery=v;}
    public Boolean getTattooOrPiercingRecently(){return tattooOrPiercingRecently;} public void setTattooOrPiercingRecently(Boolean v){tattooOrPiercingRecently=v;}
    public Boolean getMedicalCondition(){return medicalCondition;} public void setMedicalCondition(Boolean v){medicalCondition=v;}
    public Boolean getAdvisedNotToDonate(){return advisedNotToDonate;} public void setAdvisedNotToDonate(Boolean v){advisedNotToDonate=v;}
    public Boolean getRecentVaccination(){return recentVaccination;} public void setRecentVaccination(Boolean v){recentVaccination=v;}
    public Boolean getRecentDentalProcedure(){return recentDentalProcedure;} public void setRecentDentalProcedure(Boolean v){recentDentalProcedure=v;}
    public Boolean getRecentTravel(){return recentTravel;} public void setRecentTravel(Boolean v){recentTravel=v;}
    public Boolean getTransfusionOrTransplant(){return transfusionOrTransplant;} public void setTransfusionOrTransplant(Boolean v){transfusionOrTransplant=v;}
    public String getPregnancyStatus(){return pregnancyStatus;} public void setPregnancyStatus(String v){pregnancyStatus=v;}
    public Integer getBloodReadinessScore(){return bloodReadinessScore;} public void setBloodReadinessScore(Integer v){bloodReadinessScore=v;}
    public String getResult(){return result;} public void setResult(String v){result=v;}
    public LocalDateTime getAssessedAt(){return assessedAt;} public void setAssessedAt(LocalDateTime v){assessedAt=v;}
}
