package com.example.serials.dto;

import java.math.BigDecimal;

public interface SerialRangeProjection {
    BigDecimal getMinSerial();
    BigDecimal getMaxSerial();
    BigDecimal getCount();
    BigDecimal getDiff();
}
