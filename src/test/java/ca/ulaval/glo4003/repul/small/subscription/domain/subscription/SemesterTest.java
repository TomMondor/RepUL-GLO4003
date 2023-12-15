package ca.ulaval.glo4003.repul.small.subscription.domain.subscription;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.subscription.domain.subscription.semester.Semester;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.semester.SemesterCode;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SemesterTest {

    private static final LocalDate A_SEMESTER_START_DATE = LocalDate.of(2021, 1, 1);
    private static final LocalDate A_SEMESTER_END_DATE = LocalDate.of(2021, 4, 30);
    private static final SemesterCode A_SEMESTER_CODE = new SemesterCode("A21");

    @Test
    public void givenDateBeforeSemestersStartDate_whenCheckingIncludes_thenShouldReturnFalse() {
        LocalDate dateBeforeSemesterStartDate = A_SEMESTER_START_DATE.minusDays(1);
        Semester semester = new Semester(A_SEMESTER_CODE, A_SEMESTER_START_DATE, A_SEMESTER_END_DATE);

        boolean result = semester.includes(dateBeforeSemesterStartDate);

        assertFalse(result);
    }

    @Test
    public void givenDateAfterSemestersEndDate_whenCheckingIncludes_thenShouldReturnFalse() {
        LocalDate dateAfterSemesterEndDate = A_SEMESTER_END_DATE.plusDays(1);
        Semester semester = new Semester(A_SEMESTER_CODE, A_SEMESTER_START_DATE, A_SEMESTER_END_DATE);

        boolean result = semester.includes(dateAfterSemesterEndDate);

        assertFalse(result);
    }

    @Test
    public void givenDateEqualToSemestersStartDate_whenCheckingIncludes_thenShouldReturnTrue() {
        Semester semester = new Semester(A_SEMESTER_CODE, A_SEMESTER_START_DATE, A_SEMESTER_END_DATE);

        boolean result = semester.includes(A_SEMESTER_START_DATE);

        assertTrue(result);
    }

    @Test
    public void givenDateEqualToSemestersEndDate_whenCheckingIncludes_thenShouldReturnTrue() {
        Semester semester = new Semester(A_SEMESTER_CODE, A_SEMESTER_START_DATE, A_SEMESTER_END_DATE);

        boolean result = semester.includes(A_SEMESTER_END_DATE);

        assertTrue(result);
    }

    @Test
    public void givenDateBetweenSemestersStartDateAndEndDate_whenCheckingIncludes_thenShouldReturnTrue() {
        LocalDate dateBetweenSemesterStartDateAndEndDate = A_SEMESTER_START_DATE.plusDays(10);
        Semester semester = new Semester(A_SEMESTER_CODE, A_SEMESTER_START_DATE, A_SEMESTER_END_DATE);

        boolean result = semester.includes(dateBetweenSemesterStartDateAndEndDate);

        assertTrue(result);
    }
}
