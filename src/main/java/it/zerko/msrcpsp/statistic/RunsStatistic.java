package it.zerko.msrcpsp.statistic;

import lombok.Getter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RunsStatistic {

    private String algorithmKey = "Alg";
    private String datasetKey = "Dat";
    private String minimumKey = "Min";
    private String averageKey = "Avg";
    private String standardDeviationKey = "Std";
    private String regex = "(?<=%s: )[A-za-z0-9._]*";

    @Getter
    private String algorithm;
    @Getter
    private String dataset;
    @Getter
    private double minimum;
    @Getter
    private double average;
    @Getter
    private double standardDeviation;

    public RunsStatistic(String algorithm, String dataset, double minimum, double average, double standardDeviation) {
        this.algorithm = algorithm;
        this.dataset = dataset;
        this.minimum = minimum;
        this.average = average;
        this.standardDeviation = standardDeviation;
    }

    public RunsStatistic(String string) {
        algorithm = find(string, String.format(regex, algorithmKey));
        dataset = find(string, String.format(regex, datasetKey));
        minimum = Double.parseDouble(find(string, String.format(regex, minimumKey)));
        average = Double.parseDouble(find(string, String.format(regex, averageKey)));
        standardDeviation = Double.parseDouble(find(string, String.format(regex, standardDeviationKey)));
    }


    private String find(String string, String regex) {
        Matcher matcher = Pattern.compile(regex).matcher(string);
        matcher.find();
        return matcher.group();
    }

    @Override
    public String toString() {
        return String.format("%s: %s, %s: %s, %s: %s, %s: %s, %s: %s", algorithmKey, algorithm, datasetKey, dataset,
                minimumKey, minimum, averageKey, average, standardDeviationKey, standardDeviation);
    }

    public String toMarkdownString() {
        return String.format("%s|%s|%s|%s|%s", algorithm, dataset, minimum, average, standardDeviation);
    }


}
