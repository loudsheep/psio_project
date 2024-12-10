package org.loudsheep.psio_project.frontend.util;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class Epoch {
    public static long toEpochSeconds(int year, int month, int day) {
        LocalDateTime dateTime = LocalDateTime.of(year, month, day, 0, 0);
        return dateTime.toEpochSecond(ZoneOffset.UTC);
    }
}
