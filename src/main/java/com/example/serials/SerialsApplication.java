package com.example.serials;

import com.example.serials.dto.SerialRange;
import com.example.serials.dto.SerialRangeProjection;
import com.example.serials.entity.Serial;
import com.example.serials.repository.SerialRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class SerialsApplication implements CommandLineRunner {

    private BigDecimal superMinSerial = BigDecimal.ZERO;
    private BigDecimal superMaxSerial = new BigDecimal("9".repeat(30));

    @Autowired
    private SerialRepo serialRepo;

    private Integer[] testSerials = new Integer[] {
            100, 101, 102, 103,
            200, 201, 202, 203, 204,
            300, 301, 302, 303, 304, 305
    };

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(SerialsApplication.class);
        app.setWebApplicationType(WebApplicationType.NONE);
        app.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        // Try to insert for first time. This will fail if already inserted because it's primary key
        insertTestSerials();

        List<SerialRange> serialStats = new ArrayList<>();
        getSerializedRanges(serialStats, null, null);
        for (SerialRange serialStat : serialStats) {
            System.out.println(serialStat.toString());
        }
    }

    private void getSerializedRanges(List<SerialRange> serialStats, BigDecimal minSerial, BigDecimal maxSerial) {
        minSerial = minSerial == null ? superMinSerial : minSerial;
        maxSerial = maxSerial == null ? superMaxSerial : maxSerial;

        SerialRangeProjection projection = serialRepo.getMinMaxSerials(minSerial, maxSerial);
        SerialRange stats = new SerialRange(
                projection.getMinSerial(),
                projection.getMaxSerial(),
                projection.getCount(),
                projection.getDiff()
        );

        if (stats.getCount().compareTo(BigDecimal.ZERO) == 0)
            return;

        if (stats.getCount().compareTo(stats.getDiff()) == 0) {
            serialStats.add(stats);
            return;
        }

        BigDecimal firstMissing = serialRepo.findFirstMissingSerials(stats.getMinSerial(), stats.getMaxSerial());
        BigDecimal diff = firstMissing.subtract(stats.getMinSerial());
        serialStats.add(
                new SerialRange(
                        stats.getMinSerial(),   //min
                        firstMissing.subtract(BigDecimal.ONE),  //max
                        diff,
                        diff)
        );
        getSerializedRanges(serialStats, firstMissing, stats.getMaxSerial());
    }


    private void insertTestSerials() {
        try {
            for (int ts : testSerials) {
                Serial serial = new Serial(String.valueOf(ts));
                serialRepo.save(serial);
            }
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
