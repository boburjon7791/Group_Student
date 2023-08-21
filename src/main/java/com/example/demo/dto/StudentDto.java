package com.example.demo.dto;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@ToString
public class StudentDto {
    public String group_name;
    public String name;
    public Integer age;
    public Long id;
    public String gender;
    public LocalDate birthdate;
}
