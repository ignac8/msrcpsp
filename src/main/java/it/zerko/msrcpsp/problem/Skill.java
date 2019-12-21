package it.zerko.msrcpsp.problem;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
public class Skill {

    private String name;
    private int level;

    public boolean isSufficient(Skill skill) {
        return name.equals(skill.getName()) && level <= skill.getLevel();
    }

}
