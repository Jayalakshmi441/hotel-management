package com.hotel.management.repository;

import com.hotel.management.entity.ReservationReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationReportRepository extends JpaRepository<ReservationReport, Long> {
}
