package com.talan.adminmodule.repository;

import com.talan.adminmodule.entity.Attribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AttributeRepository extends JpaRepository<Attribute, Integer> {
    boolean existsByName(String name) ;

    @Query("SELECT a FROM Attribute a WHERE LOWER(a.name) = LOWER(:name)")
    Attribute findByNameIgnoreCase(@Param("name") String name);

    Attribute findByName(String name);

}
