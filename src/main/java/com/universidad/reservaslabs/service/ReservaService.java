package com.universidad.reservaslabs.service;

import com.universidad.reservaslabs.exception.RecursoNoEncontradoException;
import com.universidad.reservaslabs.exception.ReservaConflictException;
import com.universidad.reservaslabs.exception.ReservaInvalidaException;
import com.universidad.reservaslabs.model.EstadoReserva;
import com.universidad.reservaslabs.model.Laboratorio;
import com.universidad.reservaslabs.model.Reserva;
import com.universidad.reservaslabs.repository.LaboratorioRepository;
import com.universidad.reservaslabs.repository.ReservaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ReservaService {

    private static final LocalTime APERTURA = LocalTime.of(7, 0);
    private static final LocalTime CIERRE = LocalTime.of(21, 0);
    private static final Duration DURACION_MINIMA = Duration.ofMinutes(30);
    private static final Duration DURACION_MAXIMA = Duration.ofHours(3);

    private final ReservaRepository reservaRepo;
    private final LaboratorioRepository laboratorioRepo;

    public ReservaService(ReservaRepository reservaRepo, LaboratorioRepository laboratorioRepo) {
        this.reservaRepo = reservaRepo;
        this.laboratorioRepo = laboratorioRepo;
    }

    public List<Reserva> findAll() { 
        return reservaRepo.findAll(); 
    }

    public Optional<Reserva> findById(Long id) { 
        return reservaRepo.findById(id); 
    }

    public List<Reserva> findByLaboratorio(Long laboratorioId) {
        return reservaRepo.findByLaboratorioId(laboratorioId);
    }

    public Reserva crear(Reserva reserva) {
        
        validarHorarioYDuracion(reserva.getInicio(), reserva.getFin());

        
        if (reserva.getLaboratorio() == null || reserva.getLaboratorio().getId() == null) {
            throw new ReservaInvalidaException("Debe especificar el ID del laboratorio");
        }

        Laboratorio laboratorio = laboratorioRepo.findById(reserva.getLaboratorio().getId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Laboratorio no encontrado: " + reserva.getLaboratorio().getId()));

        reserva.setLaboratorio(laboratorio);

        
        List<Reserva> solapamientos = reservaRepo.buscarSolapamientos(
                laboratorio.getId(), reserva.getInicio(), reserva.getFin());

        if (!solapamientos.isEmpty()) {
            throw new ReservaConflictException(
                    "El laboratorio " + laboratorio.getNombre() + " ya tiene una reserva en ese horario");
        }

        reserva.setEstado(EstadoReserva.CONFIRMADA);
        return reservaRepo.save(reserva);
    }

    public void cancelar(Long id) {
        Reserva reserva = reservaRepo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reserva no encontrada: " + id));

        // Regla 3: No cancelar reservas pasadas
        if (reserva.getInicio().isBefore(LocalDateTime.now())) {
            throw new ReservaConflictException(
                    "No se puede cancelar una reserva cuyo horario de inicio ya pasó");
        }

        reserva.setEstado(EstadoReserva.CANCELADA);
        reservaRepo.save(reserva);
    }

    public void validarHorarioYDuracion(LocalDateTime inicio, LocalDateTime fin) {
    LocalTime horaInicio = inicio.toLocalTime();
    LocalTime horaFin = fin.toLocalTime();

    if (horaInicio.isBefore(APERTURA) || horaFin.isAfter(CIERRE)) {
        throw new ReservaInvalidaException("El horario debe estar entre las 07:00 y las 21:00");
    }

    Duration duracion = Duration.between(inicio, fin);
    if (duracion.compareTo(DURACION_MINIMA) < 0 || duracion.compareTo(DURACION_MAXIMA) > 0) {
        throw new ReservaInvalidaException("La duración debe ser entre 30 minutos y 3 horas");
    }
}
}