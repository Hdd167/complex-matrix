import java.util.Arrays;

public class ComplexMatrix {

    private final int rows;
    private final int cols;
    private final Complex[][] data;

    public ComplexMatrix(int rows, int cols) {
        if (rows <= 0 || cols <= 0) {
            throw new IllegalArgumentException(
                    "Размеры матрицы должны быть положительными: " + rows + "x" + cols);
        }
        this.rows = rows;
        this.cols = cols;
        this.data = new Complex[rows][cols];
        for (int i = 0; i < rows; i++) {
            Arrays.fill(data[i], new Complex(0, 0));
        }
    }

    public ComplexMatrix(Complex[][] source) {
        if (source == null || source.length == 0 || source[0].length == 0) {
            throw new IllegalArgumentException("Исходный массив пуст или null");
        }
        int r = source.length;
        int c = source[0].length;
        for (int i = 0; i < r; i++) {
            if (source[i] == null || source[i].length != c) {
                throw new IllegalArgumentException("Матрица должна быть прямоугольной");
            }
        }
        this.rows = r;
        this.cols = c;
        this.data = new Complex[r][c];
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                if (source[i][j] == null) {
                    throw new IllegalArgumentException(
                            "Элемент [" + i + "][" + j + "] равен null");
                }
                this.data[i][j] = source[i][j];
            }
        }
    }

    public int getRows() { return rows; }
    public int getCols() { return cols; }

    public Complex get(int i, int j) {
        checkIndex(i, j);
        return data[i][j];
    }

    public void set(int i, int j, Complex value) {
        checkIndex(i, j);
        if (value == null) {
            throw new IllegalArgumentException(
                    "Нельзя присвоить null в позицию [" + i + "][" + j + "]");
        }
        data[i][j] = value;
    }

    private void checkIndex(int i, int j) {
        if (i < 0 || i >= rows || j < 0 || j >= cols) {
            throw new IllegalArgumentException(
                    "Индекс вне диапазона: [" + i + "][" + j + "] для " + rows + "x" + cols);
        }
    }

    public ComplexMatrix add(ComplexMatrix other) {
        requireSameSize(other, "Сложение");
        ComplexMatrix result = new ComplexMatrix(rows, cols);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result.data[i][j] = data[i][j].plus(other.data[i][j]);
            }
        }
        return result;
    }

    public ComplexMatrix multiply(ComplexMatrix other) {
        if (this.cols != other.rows) {
            throw new IllegalArgumentException(
                    "Умножение невозможно: столбцов у A (" + cols + ") != строк у B (" + other.rows + ")");
        }
        ComplexMatrix result = new ComplexMatrix(this.rows, other.cols);
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < other.cols; j++) {
                Complex sum = new Complex(0, 0);
                for (int k = 0; k < this.cols; k++) {
                    sum = sum.plus(data[i][k].multiply(other.data[k][j]));
                }
                result.data[i][j] = sum;
            }
        }
        return result;
    }

    public ComplexMatrix divide(ComplexMatrix other) {
        if (other.rows != other.cols) {
            throw new IllegalArgumentException(
                    "Деление возможно только на квадратную матрицу");
        }
        return this.multiply(other.inverse());
    }

    public ComplexMatrix transpose() {
        ComplexMatrix result = new ComplexMatrix(cols, rows);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result.data[j][i] = data[i][j];
            }
        }
        return result;
    }

    public Complex determinant() {
        if (rows != cols) {
            throw new IllegalArgumentException(
                    "Определитель определён только для квадратной матрицы, получено "
                            + rows + "x" + cols);
        }
        int n = rows;
        Complex[][] a = copyData();
        Complex det = new Complex(1, 0);

        for (int i = 0; i < n; i++) {
            int pivot = selectPivotRow(a, i);
            if (a[pivot][i].isZero()) {
                return new Complex(0, 0);
            }
            if (pivot != i) {
                swapRows(a, i, pivot);
                det = det.multiply(new Complex(-1, 0));
            }
            det = det.multiply(a[i][i]);

            for (int k = i + 1; k < n; k++) {
                Complex factor = a[k][i].divide(a[i][i]);
                for (int j = i; j < n; j++) {
                    a[k][j] = a[k][j].minus(factor.multiply(a[i][j]));
                }
            }
        }
        return det;
    }

    public ComplexMatrix inverse() {
        if (rows != cols) {
            throw new IllegalArgumentException(
                    "Обратная матрица существует только для квадратной, получено "
                            + rows + "x" + cols);
        }
        int n = rows;
        Complex[][] a = copyData();
        Complex[][] inv = identity(n);

        for (int i = 0; i < n; i++) {
            int pivot = selectPivotRow(a, i);
            if (a[pivot][i].isZero()) {
                throw new ArithmeticException(
                        "Матрица вырождена (определитель = 0), обратной не существует");
            }
            if (pivot != i) {
                swapRows(a, i, pivot);
                swapRows(inv, i, pivot);
            }

            Complex pivotValue = a[i][i];
            for (int j = 0; j < n; j++) {
                a[i][j] = a[i][j].divide(pivotValue);
                inv[i][j] = inv[i][j].divide(pivotValue);
            }

            for (int k = 0; k < n; k++) {
                if (k == i) continue;
                Complex factor = a[k][i];
                if (factor.isZero()) continue;
                for (int j = 0; j < n; j++) {
                    a[k][j] = a[k][j].minus(factor.multiply(a[i][j]));
                    inv[k][j] = inv[k][j].minus(factor.multiply(inv[i][j]));
                }
            }
        }
        return new ComplexMatrix(inv);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ComplexMatrix)) return false;
        ComplexMatrix other = (ComplexMatrix) o;
        if (rows != other.rows || cols != other.cols) return false;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (!data[i][j].equals(other.data[i][j])) return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + rows;
        result = 31 * result + cols;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result = 31 * result + data[i][j].hashCode();
            }
        }
        return result;
    }

    @Override
    public String toString() {
        String[][] cells = new String[rows][cols];
        int width = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                cells[i][j] = data[i][j].toString();
                width = Math.max(width, cells[i][j].length());
            }
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rows; i++) {
            sb.append("[ ");
            for (int j = 0; j < cols; j++) {
                sb.append(String.format("%" + width + "s", cells[i][j]));
                if (j < cols - 1) sb.append("  ");
            }
            sb.append(" ]");
            if (i < rows - 1) sb.append(System.lineSeparator());
        }
        return sb.toString();
    }

    private void requireSameSize(ComplexMatrix other, String operation) {
        if (this.rows != other.rows || this.cols != other.cols) {
            throw new IllegalArgumentException(
                    operation + " требует одинаковых размеров: "
                            + rows + "x" + cols + " и " + other.rows + "x" + other.cols);
        }
    }

    private Complex[][] copyData() {
        Complex[][] copy = new Complex[rows][cols];
        for (int i = 0; i < rows; i++) {
            copy[i] = Arrays.copyOf(data[i], cols);
        }
        return copy;
    }

    private static Complex[][] identity(int n) {
        Complex[][] e = new Complex[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                e[i][j] = (i == j) ? new Complex(1, 0) : new Complex(0, 0);
            }
        }
        return e;
    }

    private static int selectPivotRow(Complex[][] a, int col) {
        int pivot = col;
        double best = a[col][col].abs();
        for (int k = col + 1; k < a.length; k++) {
            double cur = a[k][col].abs();
            if (cur > best) {
                best = cur;
                pivot = k;
            }
        }
        return pivot;
    }

    private static void swapRows(Complex[][] a, int i, int j) {
        Complex[] tmp = a[i];
        a[i] = a[j];
        a[j] = tmp;
    }
}
