package com.github.mikhailerofeev.nlp.hw1;

/**
 * @author Mikhail Erofeev https://github.com/MikhailErofeev
 * @since 15.04.14
 */
public class StatisticsResult {
    private final double precision;
    private final double recall;
    private final double accuracy;
    private final double F1Measure;
    private final int tp;
    private final int fp;
    private final int fn;
    private final int tn;

    public StatisticsResult(Builder builder) {
        this.precision = builder.getPrecision();
        this.recall = builder.getRecall();
        this.accuracy = builder.getAccuracy();
        this.F1Measure = builder.getF1Measure();
        this.tp = builder.tp;
        this.fp = builder.fp;
        this.fn = builder.fn;
        this.tn = builder.tn;
    }

    public double getPrecision() {
        return precision;
    }

    public double getRecall() {
        return recall;
    }

    public double getF1Measure() {
        return F1Measure;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public int getTp() {
        return tp;
    }

    public int getFp() {
        return fp;
    }

    public int getFn() {
        return fn;
    }

    public int getTn() {
        return tn;
    }

    @Override
    public String toString() {
        return "StatisticsResult{" +
                "F1=" + round(F1Measure) +
                ", precision=" + round(precision) +
                ", recall=" + round(recall) +
                ", accuracy=" + round(accuracy) +
                ", tp=" + tp +
                ", tn=" + tn +
                ", fp=" + fp +
                ", fn=" + fn +
                '}';
    }

    private double round(double src) {
        return ((int)(src * 1000)) / 1000.;
    }

    public static class Builder {
        private int tp;
        private int fp;
        private int fn;
        private int tn;

        public Double getF1Measure() {
            if (getPrecision() + getPrecision() == 0) {
                return 0.0;
            } else {
                return 2 * getRecall() * getPrecision() / (getRecall() + getPrecision());
            }
        }

        public Double getAccuracy() {
            return (double) (tp + tn) / (tp + fp + fn + tn);
        }

        public Double getRecall() {
            if (tp + fn == 0) {
                return 0.0;
            } else {
                return (double) tp / (tp + fn);
            }
        }

        public Double getPrecision() {
            if (tp + fp == 0) {
                return 0.0;
            } else {
                return (double) tp / (tp + fp);
            }
        }

        public Builder setTp(int tp) {
            this.tp = tp;
            return this;
        }

        public Builder setFp(int fp) {
            this.fp = fp;
            return this;
        }


        public Builder setFn(int fn) {
            this.fn = fn;
            return this;
        }

        public Builder setTn(int tn) {
            this.tn = tn;
            return this;
        }

        public Builder addTp(int tp) {
            this.tp += tp;
            return this;
        }

        public Builder addFp(int fp) {
            this.fp += fp;
            return this;
        }

        public Builder addFn(int fn) {
            this.fn += fn;
            return this;
        }

        public Builder addTn(int tn) {
            this.tn += tn;
            return this;
        }

        public StatisticsResult build() {
            return new StatisticsResult(this);
        }

        public Builder addResult(StatisticsResult ret) {
            addFn(ret.getFn())
                    .addFp(ret.getFp())
                    .addTn(ret.getTn())
                    .addTp(ret.getTp());
            return this;
        }
    }
}
