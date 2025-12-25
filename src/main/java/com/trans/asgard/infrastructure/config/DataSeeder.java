package com.trans.asgard.infrastructure.config;

import com.trans.asgard.domain.Entrepot.model.Entrepot;
import com.trans.asgard.domain.Entrepot.repository.EntrepotRepository;
import com.trans.asgard.domain.historiquevente.model.HistoriqueVente;
import com.trans.asgard.domain.historiquevente.repository.HistoriqueVenteRepository;
import com.trans.asgard.domain.iam.enums.Role;
import com.trans.asgard.domain.iam.model.User;
import com.trans.asgard.domain.iam.repository.UserRepository;
import com.trans.asgard.domain.product.model.Product;
import com.trans.asgard.domain.product.repository.ProductRepository;
import com.trans.asgard.domain.stock.model.Stock;
import com.trans.asgard.domain.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Random;

@Configuration
@RequiredArgsConstructor
public class DataSeeder {

    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initDatabase(
            UserRepository userRepository,
            EntrepotRepository entrepotRepository,
            ProductRepository productRepository,
            StockRepository stockRepository,
            HistoriqueVenteRepository historyRepository) {

        return args -> {
            if (userRepository.count() > 0) return;

            System.out.println("⏳ Starting Data Seeding...");

            Entrepot casa = Entrepot.builder().nom("Entrepot Casa").ville("Casablanca").adresse("Sidi Maarouf").build();
            Entrepot tanger = Entrepot.builder().nom("Entrepot Tanger").ville("Tanger").adresse("Zone Franche").build();
            entrepotRepository.saveAll(Arrays.asList(casa, tanger));

            User admin = User.builder()
                    .login("admin")
                    .nom("Admin")
                    .prenom("Super")
                    .email("admin@asgard.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ROLE_ADMIN)
                    .build();

            User managerCasa = User.builder()
                    .login("manager_casa")
                    .nom("Achraf")
                    .prenom("Manager")
                    .email("casa@asgard.com")
                    .password(passwordEncoder.encode("123456"))
                    .role(Role.ROLE_GESTIONNAIRE)
                    .entrepotAssigne(casa)
                    .build();

            userRepository.saveAll(Arrays.asList(admin, managerCasa));

            Product p1 = Product.builder().nom("iPhone 15").prixVente(12000.0).prixAchat(10000.0).categorie("Electronics").build();
            Product p2 = Product.builder().nom("Samsung S24").prixVente(11000.0).prixAchat(9000.0).categorie("Electronics").build();
            Product p3 = Product.builder().nom("MacBook Pro").prixVente(25000.0).prixAchat(20000.0).categorie("IT").build();
            productRepository.saveAll(Arrays.asList(p1, p2, p3));

            Stock s1 = Stock.builder().quantity(50).alertThreshold(10).product(p1).entrepot(casa).build();
            Stock s2 = Stock.builder().quantity(30).alertThreshold(5).product(p2).entrepot(casa).build();
            Stock s3 = Stock.builder().quantity(100).alertThreshold(20).product(p1).entrepot(tanger).build();
            stockRepository.saveAll(Arrays.asList(s1, s2, s3));

            Random random = new Random();
            for (int i = 0; i < 100; i++) {
                LocalDate date = LocalDate.now().minusDays(random.nextInt(90));

                HistoriqueVente sale = HistoriqueVente.builder()
                        .dateVente(date)
                        .quantiteVendue(random.nextInt(5) + 1)
                        .product(p1)
                        .entrepot(casa)
                        .jourSemaine(date.getDayOfWeek().toString())
                        .mois(date.getMonthValue())
                        .annee(date.getYear())
                        .build();

                historyRepository.save(sale);
            }

            System.out.println("✅ Data Seeding Completed!");
        };
    }
}