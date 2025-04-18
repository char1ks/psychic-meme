package com.example.LLMCodingChallenge2025.Model.Info;

import jakarta.persistence.*;

@Entity
@Table(name = "info")
public class Info {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "original_text")
    private String original_text;

    @Column(name = "date")
    private String date;

    @Column(name = "subdivision")
    private String subdivision;

    @Column(name = "operation")
    private String operation;

    @Column(name = "plant_culture")
    private String plant_culture;

    @Column(name = "per_day_ga")
    private String per_day_ga;

    @Column(name = "from_start_ga")
    private String from_start_ga;

    @Column(name = "val_day_ga")
    private String val_day_ga;

    @Column(name = "val_start_ga")
    private String val_start_ga;

    public Info() {
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getOriginal_text() {
        return original_text;
    }

    public void setOriginal_text(String original_text) {
        this.original_text = original_text;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSubdivision() {
        return subdivision;
    }

    public void setSubdivision(String subdivision) {
        this.subdivision = subdivision;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getPlant_culture() {
        return plant_culture;
    }

    public void setPlant_culture(String plant_culture) {
        this.plant_culture = plant_culture;
    }

    public String getPer_day_ga() {
        return per_day_ga;
    }

    public void setPer_day_ga(String per_day_ga) {
        this.per_day_ga = per_day_ga;
    }

    public String getFrom_start_ga() {
        return from_start_ga;
    }

    public void setFrom_start_ga(String from_start_ga) {
        this.from_start_ga = from_start_ga;
    }

    public String getVal_day_ga() {
        return val_day_ga;
    }

    public void setVal_day_ga(String val_day_ga) {
        this.val_day_ga = val_day_ga;
    }

    public String getVal_start_ga() {
        return val_start_ga;
    }

    public void setVal_start_ga(String val_start_ga) {
        this.val_start_ga = val_start_ga;
    }
}
