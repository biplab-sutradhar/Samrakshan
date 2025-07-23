package com.github.biplab.nic.repository;
import com.github.biplab.nic.entity.Person;
import com.github.biplab.nic.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PersonRepository extends JpaRepository<Person, UUID> {
    Optional<Person> findByEmail(String email);

    List<Person> findByRoleAndDistrict(Role role, String district);

    List<Person> findByDepartmentAndDistrictAndRoleAndRank(String department, String district, Role role, Integer rank);

    List<Person> findByRoleAndSubdivision(Role role, String subdivision);

    List<Person> findByDepartmentAndSubdivisionAndRoleAndRankGreaterThanEqual(String department, String subdivision, Role role, Integer rank);

    List<Person> findByDepartmentAndSubdivisionAndRoleAndRank(String department, String subdivision, Role role, Integer rank);

}
