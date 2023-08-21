package com.example.demo.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "groups")
@ToString(exclude = {"students"})
public class Groups {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", unique = true,length = 50, nullable = false)
    private String name;

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "groups",cascade = CascadeType.ALL)
    @Builder.Default
            @JsonManagedReference
    Set<Student> students=new HashSet<>();

    public void addStudent(Student student){
        this.students.add(student);
    }
    public void removeStudent(Student student){
        this.students.remove(student);
    }
}