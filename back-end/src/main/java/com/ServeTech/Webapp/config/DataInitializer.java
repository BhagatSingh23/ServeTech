package com.ServeTech.Webapp.config;

import com.ServeTech.Webapp.entity.Role;
import com.ServeTech.Webapp.entity.Skill;
import com.ServeTech.Webapp.entity.enums.RoleType;
import com.ServeTech.Webapp.entity.enums.SkillType;
import com.ServeTech.Webapp.repository.RoleRepository;
import com.ServeTech.Webapp.repository.SkillRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

// This will help with initializing data in the database on application startup
@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    private final SkillRepository skillRepository;

    // Constructor injection for the repositories
    public DataInitializer(RoleRepository roleRepository, SkillRepository skillRepository) {
        this.roleRepository = roleRepository;
        this.skillRepository = skillRepository;
    }

    @Override
    public void run(String... args) {
        initializeRoles();
        initializeSkills();
    }

    // Methods to initialize the user roles
    private void initializeRoles() {
        if (roleRepository.count() == 0) {
            Role adminRole = new Role(RoleType.ROLE_ADMIN);
            Role clientRole = new Role(RoleType.ROLE_CLIENT);
            Role workerRole = new Role(RoleType.ROLE_WORKER);

            roleRepository.save(adminRole);
            roleRepository.save(clientRole);
            roleRepository.save(workerRole);

            System.out.println("✓ Roles initialized successfully");
            System.out.println("  - ROLE_ADMIN created");
            System.out.println("  - ROLE_CLIENT created");
            System.out.println("  - ROLE_WORKER created");
        } else {
            System.out.println("✓ Roles already exist in database");
        }
    }

    // Initialize sample skills
    // Extra skills can be added here
    private void initializeSkills() {
        if (skillRepository.count() == 0) {
            // Create skills
            Skill labour = new Skill(SkillType.LABOUR);
            Skill painter = new Skill(SkillType.PAINTER);
            Skill mason = new Skill(SkillType.MASON);
            Skill carpenter = new Skill(SkillType.CARPENTER);

            skillRepository.save(labour);
            skillRepository.save(painter);
            skillRepository.save(mason);
            skillRepository.save(carpenter);

            System.out.println("✓ Skills initialized successfully");
            System.out.println("  - skills created (LABOUR, PAINTER, MASON, CARPENTER.)");
        } else {
            System.out.println("✓ Skills already exist in database");
        }
    }
}