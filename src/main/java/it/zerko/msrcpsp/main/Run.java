package it.zerko.msrcpsp.main;

import it.zerko.msrcpsp.solver.Solver;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class Run {
    private int counter;
    private Category category;
    private Solver solver;
    private LocalDateTime timeStart;

    @Override
    public String toString() {
        return String.format("%s_%s_%s_%d", timeStart.format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss")),
                category.getAlgorithm().getClass().getSimpleName(), category.getDataset(), counter);
    }
}
