package com.example.demo.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@NamedQuery(name = "selectByTwoBirthdate",query = "from Student s where s.birthDate between ?1 and ?2")
@ToString(exclude = {"groups"})
@Table(name = "student") public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    private Integer age;

    private LocalDate birthDate;

    private String gender;
    @ManyToOne(cascade= CascadeType.ALL)
    @JoinColumn(name = "groups_id")
            @JsonBackReference
    Groups groups;
    public enum Gender { MEN, WOMEN }// other fields, getters and setters

}
