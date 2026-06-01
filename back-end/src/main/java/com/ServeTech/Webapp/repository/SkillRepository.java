package com.ServeTech.Webapp.repository;

import com.ServeTech.Webapp.entity.Skill;
import com.ServeTech.Webapp.entity.enums.SkillType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {

    Optional<Skill> findByName(SkillType name);

    boolean existsByName(SkillType name);
}