import java.util.Scanner;

public class Main {

    private static final Scanner SCANNER = new Scanner(System.in);

    private static ComplexMatrix lastResult = null;

    public static void main(String[] args) {
        System.out.println("=== КАЛЬКУЛЯТОР МАТРИЦ КОМПЛЕКСНЫХ ЧИСЕЛ ===");
        System.out.println("Элемент матрицы: два числа через пробел (действительная и мнимая части).");
        System.out.println("Если мнимой части нет, можно ввести одно число: 5  ->  5 + 0i.");
        System.out.println();

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Ваш выбор: ", 0, 6);
            System.out.println();
            try {
                switch (choice) {
                    case 1 -> {
                        ComplexMatrix a = getMatrix("A");
                        ComplexMatrix b = getMatrix("B");
                        saveAndPrint(a.add(b));
                    }
                    case 2 -> {
                        ComplexMatrix a = getMatrix("A");
                        ComplexMatrix b = getMatrix("B");
                        saveAndPrint(a.multiply(b));
                    }
                    case 3 -> {
                        ComplexMatrix a = getMatrix("A");
                        ComplexMatrix b = getMatrix("B");
                        saveAndPrint(a.divide(b));
                    }
                    case 4 -> {
                        ComplexMatrix a = getMatrix("A");
                        saveAndPrint(a.transpose());
                    }
                    case 5 -> {
                        ComplexMatrix a = getMatrix("A");
                        System.out.println("det A = " + a.determinant());
                    }
                    case 6 -> showLastResult();
                    case 0 -> running = false;
                }
            } catch (IllegalArgumentException | ArithmeticException e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
            System.out.println();
        }
        System.out.println("До встречи!");
    }

    private static void printMenu() {
        System.out.println("---------------------------------------------");
        System.out.println("1. Сложить две матрицы        A + B");
        System.out.println("2. Перемножить две матрицы    A * B");
        System.out.println("3. Разделить две матрицы      A / B = A * B^(-1)");
        System.out.println("4. Транспонировать матрицу    A^T");
        System.out.println("5. Вычислить определитель     det A");
        System.out.println("6. Показать сохранённый результат");
        System.out.println("0. Выход");
        System.out.println("---------------------------------------------");
    }

    private static void saveAndPrint(ComplexMatrix result) {
        lastResult = result;
        System.out.println("Результат (сохранён для следующих операций):");
        System.out.println(result);
    }

    private static void showLastResult() {
        if (lastResult == null) {
            System.out.println("Сохранённого результата пока нет.");
        } else {
            System.out.println("Сохранённый результат:");
            System.out.println(lastResult);
        }
    }

    private static ComplexMatrix getMatrix(String name) {
        if (lastResult != null) {
            System.out.println("Есть сохранённый результат прошлой операции:");
            System.out.println(lastResult);
            int answer = readInt("Взять его как матрицу " + name + "? (1 — да, 2 — ввести новую): ", 1, 2);
            if (answer == 1) {
                System.out.println("Матрица " + name + " = сохранённый результат");
                return lastResult;
            }
            System.out.println();
        }
        return readMatrix(name);
    }

    private static ComplexMatrix readMatrix(String name) {
        System.out.println("--- Ввод матрицы " + name + " ---");
        int rows = readInt("  Строк (1..10): ", 1, 10);
        int cols = readInt("  Столбцов (1..10): ", 1, 10);
        ComplexMatrix matrix = new ComplexMatrix(rows, cols);
        System.out.println("  Формат элемента: <re> <im>  (например: 3 -2  или  5)");
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                matrix.set(i, j, readComplex("  [" + i + "][" + j + "] = "));
            }
        }
        return matrix;
    }

    private static int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String line = SCANNER.nextLine().trim();
            try {
                int value = Integer.parseInt(line);
                if (value >= min && value <= max) return value;
                System.out.println("Число должно быть от " + min + " до " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("Ожидалось целое число. Попробуйте ещё раз.");
            }
        }
    }

    private static Complex readComplex(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = SCANNER.nextLine().trim();
            if (line.isEmpty()) continue;
            String[] parts = line.replace(',', '.').split("\\s+");
            try {
                if (parts.length == 1) {
                    return new Complex(Double.parseDouble(parts[0]), 0.0);
                }
                if (parts.length == 2) {
                    return new Complex(
                            Double.parseDouble(parts[0]),
                            Double.parseDouble(parts[1]));
                }
            } catch (NumberFormatException ignored) {
            }
            System.out.println("Неверный формат. Пример: 3 -2  или  5");
        }
    }
}
