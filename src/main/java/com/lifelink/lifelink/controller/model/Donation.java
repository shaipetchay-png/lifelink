package com.lifelink.lifelink.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "donations")
public class Donation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "donor_id", nullable = false)
    private User donor;

    @Column(nullable = false)
    private LocalDate donationDate;

    @Column(nullable = false)
    private String bloodType;

    @Column(nullable = false)
    private Integer units = 1;

    @Column(nullable = false)
    private String status = "COMPLETED";

    public Long getId() { return id; }
    public User getDonor() { return donor; }
    public void setDonor(User donor) { this.donor = donor; }
    public LocalDate getDonationDate() { return donationDate; }
    public void setDonationDate(LocalDate donationDate) { this.donationDate = donationDate; }
    public String getBloodType() { return bloodType; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }
    public Integer getUnits() { return units; }
    public void setUnits(Integer units) { this.units = units; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
