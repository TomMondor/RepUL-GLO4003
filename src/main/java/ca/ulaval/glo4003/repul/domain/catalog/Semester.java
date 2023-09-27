package ca.ulaval.glo4003.repul.domain.catalog;

import java.time.LocalDate;

public record Semester(SemesterCode semesterCode, LocalDate startDate, LocalDate endDate) {
}
