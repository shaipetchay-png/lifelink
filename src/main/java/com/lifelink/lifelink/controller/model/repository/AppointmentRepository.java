package com.lifelink.lifelink.repository;

import com.lifelink.lifelink.model.Appointment;
import com.lifelink.lifelink.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByDonorAndAppointmentDateGreaterThanEqualAndStatusInOrderByAppointmentDateAscAppointmentTimeAsc(
            User donor, LocalDate date, List<String> statuses);

    List<Appointment> findByDonorOrderByAppointmentDateDescAppointmentTimeDesc(User donor);

    List<Appointment> findByAppointmentDateGreaterThanEqualAndStatusInOrderByAppointmentDateAscAppointmentTimeAsc(
            LocalDate date, List<String> statuses);
}
