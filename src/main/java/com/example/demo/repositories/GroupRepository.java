package com.example.demo.repositories;

import com.example.demo.entities.Groups;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface GroupRepository extends JpaRepository<Groups,Integer> {
    @Query(value = "from Groups g where g.name=?1")
    Groups findByName(String name);
    @Modifying
    @Query(value = "update Groups g set g.name=?1")
    Groups editGroup(String name);

    @Query(value = "from Groups g where g.id=?1")
    Groups getGroupBy(Long id);


    @Modifying
    @Query(value = "delete from Groups g where g.name=?1")
    void deleteByName(String name);
}
