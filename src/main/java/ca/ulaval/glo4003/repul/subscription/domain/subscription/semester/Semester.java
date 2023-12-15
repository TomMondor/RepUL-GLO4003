package ca.ulaval.glo4003.repul.subscription.domain.subscription.semester;

import java.time.LocalDate;

public record Semester(
    SemesterCode semesterCode,
    LocalDate startDate,
    LocalDate endDate
) {
    public boolean includes(LocalDate date) {
        return (date.isAfter(startDate) || date.isEqual(startDate)) && (date.isBefore(endDate) || date.isEqual(endDate));
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s", semesterCode.value(), startDate.toString(), endDate.toString());
    }
}
