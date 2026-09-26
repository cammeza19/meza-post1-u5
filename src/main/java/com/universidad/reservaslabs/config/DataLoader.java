package com.universidad.reservaslabs.config;

import com.universidad.reservaslabs.model.Laboratorio;
import com.universidad.reservaslabs.repository.LaboratorioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final LaboratorioRepository laboratorioRepository;

    public DataLoader(LaboratorioRepository laboratorioRepository) {
        this.laboratorioRepository = laboratorioRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (laboratorioRepository.count() == 0) {
            Laboratorio lab1 = new Laboratorio();
            lab1.setNombre("Laboratorio de Software");
            lab1.setUbicacion("Edificio A - Aula 201");
            lab1.setCapacidad(30);
            lab1.setTipo("Software");

            Laboratorio lab2 = new Laboratorio();
            lab2.setNombre("Laboratorio de Hardware y Redes");
            lab2.setUbicacion("Edificio B - Aula 105");
            lab2.setCapacidad(25);
            lab2.setTipo("Hardware");

            laboratorioRepository.save(lab1);
            laboratorioRepository.save(lab2);

            System.out.println(">>> [DataLoader] Laboratorios de prueba cargados exitosamente.");
        }
    }
}