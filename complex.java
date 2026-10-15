package src;

public class Complex {

    private static final double EPS = 1e-9;

    private final double re;
    private final double im;

    public Complex(double re, double im) {
        this.re = re;
        this.im = im;
    }

    public Complex(double re) {
        this(re, 0.0);
    }

    public double getRe() { return re; }
    public double getIm() { return im; }

    public Complex plus(Complex other) {
        return new Complex(re + other.re, im + other.im);
    }

    public Complex minus(Complex other) {
        return new Complex(re - other.re, im - other.im);
    }

    public Complex multiply(Complex other) {
        return new Complex(
                re * other.re - im * other.im,
                re * other.im + im * other.re);
    }

    public Complex divide(Complex other) {
        double denominator = other.re * other.re + other.im * other.im;
        if (Math.abs(denominator) < EPS) {
            throw new ArithmeticException("Деление на ноль (нулевой комплексный делитель)");
        }
        return new Complex(
                (re * other.re + im * other.im) / denominator,
                (im * other.re - re * other.im) / denominator);
    }

    public boolean isZero() {
        return Math.abs(re) < EPS && Math.abs(im) < EPS;
    }

    public double abs() {
        return Math.hypot(re, im);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Complex)) return false;
        Complex other = (Complex) o;
        return Math.abs(re - other.re) < EPS
                && Math.abs(im - other.im) < EPS;
    }

    @Override
    public int hashCode() {
        long reBits = Math.round(re / EPS);
        long imBits = Math.round(im / EPS);
        int result = 17;
        result = 31 * result + Long.hashCode(reBits);
        result = 31 * result + Long.hashCode(imBits);
        return result;
    }

    private static double round(double x) {
        return Math.round(x * 10000.0) / 10000.0;
    }

    @Override
    public String toString() {
        double shownRe = round(re);
        double shownIm = round(im);

        boolean realPartExists = Math.abs(shownRe) > EPS;
        boolean imagPartExists = Math.abs(shownIm) > EPS;

        if (!realPartExists && !imagPartExists) {
            return "0";
        }
        StringBuilder sb = new StringBuilder();
        if (realPartExists) {
            sb.append(format(shownRe));
        }
        if (imagPartExists) {
            if (realPartExists) {
                sb.append(shownIm >= 0 ? "+" : "-");
            } else if (shownIm < 0) {
                sb.append("-");
            }
            sb.append(format(Math.abs(shownIm))).append("i");
        }
        return sb.toString();
    }

    private static String format(double value) {
        if (Math.abs(value - Math.round(value)) < EPS) {
            return String.valueOf(Math.round(value));
        }
        return String.format("%.4f", value)
                .replaceAll("0+$", "")
                .replaceAll("\\.$", "");
    }
}