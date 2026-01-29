package com.ServeTech.Webapp.config;

import com.ServeTech.Webapp.entity.PincodeLocation;
import com.ServeTech.Webapp.entity.Role;
import com.ServeTech.Webapp.entity.Skill;
import com.ServeTech.Webapp.repository.PincodeLocationRepository;
import com.ServeTech.Webapp.repository.RoleRepository;
import com.ServeTech.Webapp.repository.SkillRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

// This will help with initializing data in the database on application startup
@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    private final SkillRepository skillRepository;

    private final PincodeLocationRepository pincodeLocationRepository;

    // Constructor injection for the repositories
    public DataInitializer(RoleRepository roleRepository, SkillRepository skillRepository,
                           PincodeLocationRepository pincodeLocationRepository) {
        this.roleRepository = roleRepository;
        this.skillRepository = skillRepository;
        this.pincodeLocationRepository = pincodeLocationRepository;
    }

    @Override
    public void run(String... args) {
        initializeRoles();
        initializeSkills();
        initializeSamplePincodes();
    }

    // Methods to initialize the user roles
    private void initializeRoles() {
        if (roleRepository.count() == 0) {
            Role adminRole = new Role("ROLE_ADMIN", "System Administrator with full access");
            Role clientRole = new Role("ROLE_CLIENT", "Client/Contractor who posts work requests");
            Role workerRole = new Role("ROLE_WORKER", "Skilled/Unskilled worker who applies for work");

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
            Skill labour = new Skill("LABOUR", "General labour work - lifting, carrying, assisting");
            Skill painter = new Skill("PAINTER", "Painting and coating work - interior and exterior");
            Skill mason = new Skill("MASON", "Masonry and bricklaying work");
            Skill carpenter = new Skill("CARPENTER", "Carpentry and woodwork");

            skillRepository.save(labour);
            skillRepository.save(painter);
            skillRepository.save(mason);
            skillRepository.save(carpenter);

            System.out.println("✓ Skills initialized successfully");
            System.out.println("  - 10 skills created (LABOUR, PAINTER, MASON, etc.)");
        } else {
            System.out.println("✓ Skills already exist in database");
        }
    }

    // Some sample pincode for testing purposes
    private void initializeSamplePincodes() {
        if (pincodeLocationRepository.count() == 0) {
            // Mumbai & Navi Mumbai
            pincodeLocationRepository.save(new PincodeLocation("400001", "Mumbai", "Maharashtra", "Mumbai City"));
            pincodeLocationRepository.save(new PincodeLocation("400051", "Mumbai", "Maharashtra", "Mumbai Suburban"));
            pincodeLocationRepository.save(new PincodeLocation("400708", "Navi Mumbai", "Maharashtra", "Thane"));
            pincodeLocationRepository.save(new PincodeLocation("400614", "Thane", "Maharashtra", "Thane"));

            // Delhi NCR
            pincodeLocationRepository.save(new PincodeLocation("110001", "New Delhi", "Delhi", "Central Delhi"));
            pincodeLocationRepository.save(new PincodeLocation("110016", "New Delhi", "Delhi", "South Delhi"));
            pincodeLocationRepository.save(new PincodeLocation("201301", "Noida", "Uttar Pradesh", "Gautam Buddha Nagar"));
            pincodeLocationRepository.save(new PincodeLocation("122001", "Gurgaon", "Haryana", "Gurgaon"));

            // Bangalore
            pincodeLocationRepository.save(new PincodeLocation("560001", "Bangalore", "Karnataka", "Bangalore Urban"));
            pincodeLocationRepository.save(new PincodeLocation("560066", "Bangalore", "Karnataka", "Bangalore Urban"));

            // Pune
            pincodeLocationRepository.save(new PincodeLocation("411001", "Pune", "Maharashtra", "Pune"));
            pincodeLocationRepository.save(new PincodeLocation("411014", "Pune", "Maharashtra", "Pune"));

            // Hyderabad
            pincodeLocationRepository.save(new PincodeLocation("500001", "Hyderabad", "Telangana", "Hyderabad"));
            pincodeLocationRepository.save(new PincodeLocation("500081", "Hyderabad", "Telangana", "Hyderabad"));

            // Chennai
            pincodeLocationRepository.save(new PincodeLocation("600001", "Chennai", "Tamil Nadu", "Chennai"));
            pincodeLocationRepository.save(new PincodeLocation("600028", "Chennai", "Tamil Nadu", "Chennai"));

            // Kolkata
            pincodeLocationRepository.save(new PincodeLocation("700001", "Kolkata", "West Bengal", "Kolkata"));
            pincodeLocationRepository.save(new PincodeLocation("700091", "Kolkata", "West Bengal", "Kolkata"));

            // Ahmedabad
            pincodeLocationRepository.save(new PincodeLocation("380001", "Ahmedabad", "Gujarat", "Ahmedabad"));
            pincodeLocationRepository.save(new PincodeLocation("380015", "Ahmedabad", "Gujarat", "Ahmedabad"));

            // Jaipur
            pincodeLocationRepository.save(new PincodeLocation("302001", "Jaipur", "Rajasthan", "Jaipur"));
            pincodeLocationRepository.save(new PincodeLocation("302017", "Jaipur", "Rajasthan", "Jaipur"));

            System.out.println("✓ Sample pincodes initialized successfully");
            System.out.println("  - 20 pincodes added for major cities");
            System.out.println("  - Cities: Mumbai, Delhi, Bangalore, Pune, Hyderabad, Chennai, Kolkata, Ahmedabad, Jaipur");
        } else {
            System.out.println("✓ Pincodes already exist in database");
        }
    }
}