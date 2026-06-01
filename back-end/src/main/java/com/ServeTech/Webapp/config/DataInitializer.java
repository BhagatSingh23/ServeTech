package com.ServeTech.Webapp.config;

import com.ServeTech.Webapp.entity.*;
import com.ServeTech.Webapp.entity.enums.AccountStatus;
import com.ServeTech.Webapp.entity.enums.GenderType;
import com.ServeTech.Webapp.entity.enums.RoleType;
import com.ServeTech.Webapp.entity.enums.SkillType;
import com.ServeTech.Webapp.repository.*;
import com.ServeTech.Webapp.util.UniqueIdGenerator;
import com.ServeTech.Webapp.util.UsernameGenerator;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

// This will help with initializing data in the database on application startup
@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final WorkerProfileRepository workerProfileRepository;
    private final ClientProfileRepository clientProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final UniqueIdGenerator uniqueIdGenerator;
    private final UsernameGenerator usernameGenerator;

    // Constructor injection for the repositories
    public DataInitializer(RoleRepository roleRepository,
                           SkillRepository skillRepository,
                           UserRepository userRepository,
                           WorkerProfileRepository workerProfileRepository,
                           ClientProfileRepository clientProfileRepository,
                           PasswordEncoder passwordEncoder,
                           UniqueIdGenerator uniqueIdGenerator,
                           UsernameGenerator usernameGenerator) {
        this.roleRepository = roleRepository;
        this.skillRepository = skillRepository;
        this.userRepository = userRepository;
        this.workerProfileRepository = workerProfileRepository;
        this.clientProfileRepository = clientProfileRepository;
        this.passwordEncoder = passwordEncoder;
        this.uniqueIdGenerator = uniqueIdGenerator;
        this.usernameGenerator = usernameGenerator;
    }

    @Override
    public void run(String... args) {
        initializeRoles();
        initializeSkills();
        initializeTestUsers();
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

    // Initialize all skills from SkillType enum
    private void initializeSkills() {
        if (skillRepository.count() == 0) {
            for (SkillType skillType : SkillType.values()) {
                Skill skill = new Skill(skillType);
                skillRepository.save(skill);
            }
            System.out.println("✓ All " + SkillType.values().length + " skills initialized successfully");
        } else {
            // Check for new skills that might have been added to the enum
            for (SkillType skillType : SkillType.values()) {
                if (!skillRepository.existsByName(skillType)) {
                    Skill skill = new Skill(skillType);
                    skillRepository.save(skill);
                    System.out.println("  + Added new skill: " + skillType.name());
                }
            }
            System.out.println("✓ Skills already exist in database");
        }
    }

    // Initialize test users for development
    private void initializeTestUsers() {
        // Create Admin user
        if (!userRepository.existsByPhoneNumber("9999999999")) {
            User admin = new User();
            admin.setFirstName("Admin");
            admin.setLastName("ServeTech");
            admin.setPhoneNumber("9999999999");
            admin.setPassword(passwordEncoder.encode("Admin@123"));
            admin.setUniqueUserId(uniqueIdGenerator.generateUniqueUserId());
            admin.setUsername(usernameGenerator.generateUsername("Admin", "ServeTech", admin.getUniqueUserId()));
            admin.setPincode("110001");
            admin.setBlock("Connaught Place");
            admin.setDistrict("New Delhi");
            admin.setState("Delhi");
            admin.setAccountStatus(AccountStatus.ACTIVE);
            admin.setPhoneVerified(true);
            admin.setDateOfBirth(LocalDate.of(1990, 1, 1));
            admin.setGender(GenderType.MALE);

            Role adminRole = roleRepository.findByName(RoleType.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Admin role not found"));
            admin.addRole(adminRole);

            userRepository.save(admin);
            System.out.println("✓ Admin user created (phone: 9999999999, password: Admin@123)");
        }

        // Create test Worker
        if (!userRepository.existsByPhoneNumber("8888888888")) {
            User worker = new User();
            worker.setFirstName("Rajesh");
            worker.setLastName("Kumar");
            worker.setPhoneNumber("8888888888");
            worker.setPassword(passwordEncoder.encode("Worker@123"));
            worker.setUniqueUserId(uniqueIdGenerator.generateUniqueUserId());
            worker.setUsername(usernameGenerator.generateUsername("Rajesh", "Kumar", worker.getUniqueUserId()));
            worker.setPincode("110001");
            worker.setBlock("Karol Bagh");
            worker.setDistrict("New Delhi");
            worker.setState("Delhi");
            worker.setAccountStatus(AccountStatus.ACTIVE);
            worker.setPhoneVerified(true);
            worker.setDateOfBirth(LocalDate.of(1995, 5, 15));
            worker.setGender(GenderType.MALE);

            Role workerRole = roleRepository.findByName(RoleType.ROLE_WORKER)
                    .orElseThrow(() -> new RuntimeException("Worker role not found"));
            worker.addRole(workerRole);

            userRepository.save(worker);

            // Create WorkerProfile
            WorkerProfile profile = new WorkerProfile();
            profile.setUser(worker);
            profile.setBio("Experienced painter and mason with 5 years of professional experience.");
            profile.setExperienceYears(5);
            profile.setDailyWage(800.0);
            profile.setAvailableForWork(true);
            profile.setIsVerified(true);

            Set<Skill> skills = new HashSet<>();
            skillRepository.findByName(SkillType.PAINTER).ifPresent(skills::add);
            skillRepository.findByName(SkillType.MASON).ifPresent(skills::add);
            profile.setSkills(skills);

            workerProfileRepository.save(profile);
            System.out.println("✓ Test worker created (phone: 8888888888, password: Worker@123)");
        }

        // Create test Client
        if (!userRepository.existsByPhoneNumber("7777777777")) {
            User client = new User();
            client.setFirstName("Priya");
            client.setLastName("Sharma");
            client.setPhoneNumber("7777777777");
            client.setPassword(passwordEncoder.encode("Client@123"));
            client.setUniqueUserId(uniqueIdGenerator.generateUniqueUserId());
            client.setUsername(usernameGenerator.generateUsername("Priya", "Sharma", client.getUniqueUserId()));
            client.setPincode("110001");
            client.setBlock("Saket");
            client.setDistrict("New Delhi");
            client.setState("Delhi");
            client.setAccountStatus(AccountStatus.ACTIVE);
            client.setPhoneVerified(true);
            client.setDateOfBirth(LocalDate.of(1988, 8, 20));
            client.setGender(GenderType.FEMALE);

            Role clientRole = roleRepository.findByName(RoleType.ROLE_CLIENT)
                    .orElseThrow(() -> new RuntimeException("Client role not found"));
            client.addRole(clientRole);

            userRepository.save(client);

            // Create ClientProfile
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setUser(client);
            clientProfileRepository.save(clientProfile);

            System.out.println("✓ Test client created (phone: 7777777777, password: Client@123)");
        }
    }
}