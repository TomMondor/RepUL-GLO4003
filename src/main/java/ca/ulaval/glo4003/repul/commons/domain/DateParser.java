package ca.ulaval.glo4003.repul.commons.domain;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import ca.ulaval.glo4003.repul.commons.domain.exception.InvalidDateException;
import ca.ulaval.glo4003.repul.commons.domain.exception.InvalidDayOfWeekException;

public class DateParser {
    public static DayOfWeek dayOfWeekFrom(String dayOfWeek) {
        try {
            return DayOfWeek.valueOf(dayOfWeek);
        } catch (IllegalArgumentException e) {
            throw new InvalidDayOfWeekException();
        }
    }

    public static LocalDate localDateFrom(String date) {
        try {
            return LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            throw new InvalidDateException();
        }
    }
}
