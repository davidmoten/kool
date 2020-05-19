package org.davidmoten.kool;

public final class Statistics {
    final double sum;
    final long count;
    final double sumSquared;
    final double m2;
    final double m3;
    final double m4;

    final double min;

    final double max;

    Statistics() {
        this(0, 0, 0, 0, 0, 0, Double.MAX_VALUE, Double.MIN_VALUE);
    }

    Statistics(double sum, long count, double sumSquared, double m2, double m3, double m4, double min, double max) {
        this.sum = sum;
        this.count = count;
        this.sumSquared = sumSquared;
        this.m2 = m2;
        this.m3 = m3;
        this.m4 = m4;
        this.min = min;
        this.max = max;
    }

    Statistics add(double x) {
        long n = count + 1;
        final double mean;
        if (count == 0) {
            mean = 0;
        } else {
            mean = mean();
        }
        double delta = x - mean;
        double delta_n = delta / n;
        double delta_n2 = delta_n * delta_n;
        double term1 = delta * delta_n * (n - 1);
        double m4 = this.m4 + term1 * delta_n2 * (n * n - 3 * n + 3) + 6 * delta_n2 * m2 - 4 * delta_n * m3;
        double m3 = this.m3 + term1 * delta_n * (n - 2) - 3 * delta_n * m2;
        double m2 = this.m2 + term1;
        double min = Math.min(x, this.min);
        double max = Math.max(x, this.max);
        return new Statistics(sum + x, count + 1, sumSquared + x * x, m2, m3, m4, min, max);
    }

    public long count() {
        return count;
    }

    public double standardDeviation() {
        return Math.sqrt(sumSquared / count - mean() * mean());
    }

    public double mean() {
        return sum / count;
    }

    public double min() {
        return min;
    }

    public double max() {
        return max;
    }

    /**
     * Returns the calculated kurtosis of the data. Kurtosis is a measure of whether
     * the data are heavy-tailed (profusion of outliers) or light-tailed (lack of
     * outliers) relative to a normal distribution. The higher the value the more
     * outliers. The smallest value is 1, maximum is infinity.
     * 
     * @return kurtosis
     */
    public double kurtosis() {
        // smallest value is 1, max is infinity
        return (count * m4) / (m2 * m2);
    }

    public double excessKurtosis() {
        return kurtosis() - 3;
    }

    public double skewness() {
        return Math.sqrt(count) * m3 / Math.sqrt(m2) / m2;
    }

    public double range() {
        return max - min;
    }

    public double variance() {
        double sd = standardDeviation();
        return sd * sd;
    }

    public String toString(String prefix, String delimiter) {
        StringBuilder b = new StringBuilder();
        b.append(prefix);
        b.append("count=");
        b.append(count);
        b.append(delimiter);

        b.append(prefix);
        b.append("mean=");
        b.append(mean());
        b.append(delimiter);

        b.append(prefix);
        b.append("standardDeviation=");
        b.append(standardDeviation());
        b.append(delimiter);

        b.append(prefix);
        b.append("variance=");
        b.append(variance());
        b.append(delimiter);

        b.append(prefix);
        b.append("kurtosis=");
        b.append(kurtosis());
        b.append(delimiter);

        b.append(prefix);
        b.append("skewness=");
        b.append(skewness());
        b.append(delimiter);

        b.append(prefix);
        b.append("min=");
        b.append(min());
        b.append(delimiter);

        b.append(prefix);
        b.append("max=");
        b.append(max());
        b.append(delimiter);

        b.append(prefix);
        b.append("range=");
        b.append(range());
        b.append(delimiter);

        return b.toString();
    }

    @Override
    public String toString() {
        return "Statistics [" + toString(" ", ",") + "]";
    }
}