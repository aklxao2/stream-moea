/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moa.subgroupdiscovery.qualitymeasures;

import java.util.logging.Level;
import java.util.logging.Logger;
import moa.core.ObjectRepository;
import moa.tasks.TaskMonitor;
import org.core.exceptions.InvalidMeasureComparisonException;
import org.core.exceptions.InvalidRangeInMeasureException;

/**
 *
 * @author Ángel Miguel García Vico (agvico@ujaen.es)
 * @since JDK 8.0
 */
public final class GrowthRate extends QualityMeasure {
    
    public GrowthRate(){
        this.name = "Growth Rate";
        this.short_name = "GR";
        this.value = 0.0;
    }

    @Override
    public double calculateValue(ContingencyTable t) {
        try {
            TPR tpr = new TPR();
            FPR fpr = new FPR();

            tpr.calculateValue(t);
            fpr.calculateValue(t);
            tpr.validate();
            fpr.validate();

            if (fpr.getValue() == 0 && tpr.getValue() == 0) {
                value = 0.0;
            } else if (tpr.getValue() != 0 && fpr.getValue() == 0) {
                value = Double.POSITIVE_INFINITY;
            } else {
                value = tpr.getValue() / fpr.getValue();
            }

        } catch (InvalidRangeInMeasureException ex) {
            ex.showAndExit(this);
        }
        return value;
    }

    @Override
    public void validate() throws InvalidRangeInMeasureException {
        if (value < 0) {
            throw new InvalidRangeInMeasureException(this);
        }
    }

    @Override
    public QualityMeasure clone() {
        GrowthRate a = new GrowthRate();
        a.name = this.name;
        a.value = this.value;

        return a;
    }

    @Override
    protected void prepareForUseImpl(TaskMonitor tm, ObjectRepository or) {
    }

    @Override
    public void getDescription(StringBuilder sb, int i) {
    }

    @Override
    public int compareTo(QualityMeasure o) {
        try {
            if (!(o instanceof GrowthRate)) {
                throw new InvalidMeasureComparisonException(this, o);
            }

            if (this.value < o.value) {
                return -1;
            } else if (this.value > o.value) {
                return 1;
            } else {
                return 0;
            }
        } catch (InvalidMeasureComparisonException ex) {
            ex.showAndExit(this);
        }
        return 0;
    }

}
