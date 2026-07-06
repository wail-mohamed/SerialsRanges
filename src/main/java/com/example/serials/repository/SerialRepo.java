package com.example.serials.repository;

import com.example.serials.dto.SerialRangeProjection;
import com.example.serials.entity.Serial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface SerialRepo extends JpaRepository<Serial, String> {

    @Query(value = """
            SELECT
                MIN(CAST(serial_num AS numeric)) AS "minSerial",
                MAX(CAST(serial_num AS numeric)) AS "maxSerial",
                COUNT(*)::numeric AS "count",
                MAX(CAST(serial_num AS numeric)) - MIN(CAST(serial_num AS numeric)) + 1 AS "diff"
            FROM serials
            WHERE 
                serial_num ~ '^[0-9]+$'
                AND CAST(serial_num AS numeric) BETWEEN :minSerial AND :maxSerial
            """, nativeQuery = true)
    SerialRangeProjection getMinMaxSerials(
            @Param("minSerial") BigDecimal minSerial,
            @Param("maxSerial") BigDecimal maxSerial
    );

    @Query(value = """
        SELECT MIN(gs.serial_num)
        FROM generate_series(
                CAST(:minSerial AS numeric),
                CAST(:maxSerial AS numeric),
                1
             ) AS gs(serial_num)
        LEFT JOIN serials s
            ON s.serial_num ~ '^[0-9]+$'
           AND s.serial_num::numeric = gs.serial_num
        WHERE s.serial_num IS NULL
        """, nativeQuery = true)
    BigDecimal findFirstMissingSerials(
            @Param("minSerial") BigDecimal minSerial,
            @Param("maxSerial") BigDecimal maxSerial
    );
}
