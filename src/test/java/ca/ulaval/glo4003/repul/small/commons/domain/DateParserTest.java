package ca.ulaval.glo4003.repul.small.commons.domain;

import java.time.DayOfWeek;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.commons.domain.DateParser;
import ca.ulaval.glo4003.repul.commons.domain.exception.InvalidDateException;
import ca.ulaval.glo4003.repul.commons.domain.exception.InvalidDayOfWeekException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DateParserTest {
    private static final String INVALID_DAY_OF_WEEK = "JOHNDEER";
    private static final String VALID_DAY_OF_WEEK = "MONDAY";
    private static final String INVALID_DATE = "2021-13-32";
    private static final String VALID_DATE = "2021-12-31";

    @Test
    public void givenInvalidDayOfWeek_whenDayOfWeekFrom_shouldThrowInvalidDayOfWeekException() {
        assertThrows(InvalidDayOfWeekException.class, () -> DateParser.dayOfWeekFrom(INVALID_DAY_OF_WEEK));
    }

    @Test
    public void givenValidDayOfWeek_whenDayOfWeekFrom_shouldReturnDayOfWeek() {
        DayOfWeek dayOfWeek = DateParser.dayOfWeekFrom(VALID_DAY_OF_WEEK);

        assertEquals(DayOfWeek.MONDAY, dayOfWeek);
    }

    @Test
    public void givenInvalidDate_whenLocalDateFrom_shouldThrowInvalidDateException() {
        assertThrows(InvalidDateException.class, () -> DateParser.localDateFrom(INVALID_DATE));
    }

    @Test
    public void givenValidDate_whenLocalDateFrom_shouldReturnLocalDate() {
        LocalDate date = DateParser.localDateFrom(VALID_DATE);

        assertEquals(LocalDate.of(2021, 12, 31), date);
    }
}
