package com.pascs.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "counters")
@Data
public class Counter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String location;

    private boolean active = true;

    @ManyToOne
    @JoinColumn(name = "current_staff_id")
    private User currentStaff;

    @Column(name = "current_queue_number")
    private String currentQueueNumber;
}