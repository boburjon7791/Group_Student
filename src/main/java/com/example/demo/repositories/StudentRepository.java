package com.example.demo.repositories;

import com.example.demo.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public interface StudentRepository extends JpaRepository<Student,Integer> {
    @Query("from Student s where s.name=?1")
    Student findByName(String name);
    @Modifying
    @Query(nativeQuery = true,value = "delete from student s where s.name=?1")
    void deleteStudentByName(String name);
    @Query(name = "selectByTwoBirthdate")
    List<Student> findAllByBirthDates(LocalDate date1, LocalDate date2);
    @Query(nativeQuery = true,value = "select * from student where groups_id=?1")
    List<Student> findAllByGroupId(Integer group_id);
    @Modifying
    @Transactional
    @Query(nativeQuery = true,value = "delete from student where id=?1")
    void deleteWithId(Long id);
}
