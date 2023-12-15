package ca.ulaval.glo4003.repul.subscription.domain.subscription.semester;

import java.time.LocalDate;
import java.util.List;

import ca.ulaval.glo4003.repul.subscription.domain.exception.SemesterNotFoundException;

public class Semesters {
    private final List<Semester> semesters;

    public Semesters(List<Semester> semesters) {
        this.semesters = semesters;
    }

    public Semester findSemesterByDate(LocalDate date) {
        return semesters.stream().filter(semester -> semester.includes(date)).findFirst()
            .orElseThrow(SemesterNotFoundException::new);
    }
}
