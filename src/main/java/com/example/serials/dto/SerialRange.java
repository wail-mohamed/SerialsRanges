package com.example.serials.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class SerialRange {

    private BigDecimal minSerial;
    private BigDecimal maxSerial;
    private BigDecimal count;
    private BigDecimal diff;

    @Override
    public String toString() {
        return "SerialStats{" +
                "minSerial=" + minSerial +
                ", maxSerial=" + maxSerial +
                ", count=" + count +
                ", diff=" + diff +
                '}';
    }
}
