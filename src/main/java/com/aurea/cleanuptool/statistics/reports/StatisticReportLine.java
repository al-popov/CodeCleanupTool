package com.aurea.cleanuptool.statistics.reports;

import java.util.ArrayList;
import java.util.List;

public class StatisticReportLine {
    public String name;
    public String link;
    public List<String> values = new ArrayList<>();

    public String getName() {
        if (name != null && !name.isEmpty()) {
            return name;
        } else {
            return null;
        }
    }

    public String getLink() {
        if (link != null && !link.isEmpty()) {
            return link;
        } else {
            return null;
        }
    }

    public List<String> getValues() {
        return values;
    }
}
