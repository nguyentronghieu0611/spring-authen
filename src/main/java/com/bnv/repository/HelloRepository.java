package com.bnv.repository;


import com.bnv.model.DAOUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HelloRepository extends JpaRepository<DAOUser, Long> {
    @Query(value = "SELECT PKG_TEST1.select_test(:input) from dual", nativeQuery = true)
    String findCarsAfterYear(@Param("input") String input);

    @Query(value = "SELECT * FROM TEMP_ADM_USER",nativeQuery = true)
    List<DAOUser> getAll();

}
