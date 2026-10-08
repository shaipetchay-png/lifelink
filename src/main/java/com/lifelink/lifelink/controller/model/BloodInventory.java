package com.lifelink.lifelink.model;

import jakarta.persistence.*;

@Entity
@Table(name="blood_inventory", uniqueConstraints=@UniqueConstraint(columnNames="bloodType"))
public class BloodInventory {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false) private String bloodType;
 @Column(nullable=false) private Integer units=0;
 public Long getId(){return id;} public String getBloodType(){return bloodType;} public void setBloodType(String v){bloodType=v;}
 public Integer getUnits(){return units;} public void setUnits(Integer v){units=v;}
}
