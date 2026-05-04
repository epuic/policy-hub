package com.endava.insurance.insurance_service.domain.model.geography;

import com.endava.insurance.insurance_service.domain.model.Building;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "county_id", nullable = false)
    private County county;

    @OneToMany(mappedBy = "city")
    private List<Building> buildings;
}