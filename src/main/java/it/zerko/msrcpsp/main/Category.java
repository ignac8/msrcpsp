package it.zerko.msrcpsp.main;

import it.zerko.msrcpsp.algorithm.Algorithm;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class Category {
    private Algorithm algorithm;
    private String dataset;
}
