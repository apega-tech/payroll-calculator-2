import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class PayrollCalculator {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the name of the file to process: ");
        String inputFilePath = scanner.nextLine().trim();

        System.out.print("Enter the name of the payroll file to create: ");
        String outputFilePath = scanner.nextLine().trim();

        scanner.close();

        boolean isJson = outputFilePath.toLowerCase().endsWith(".json");

        try (BufferedReader br = new BufferedReader(new FileReader(inputFilePath));
             BufferedWriter bw = new BufferedWriter(new FileWriter(outputFilePath))) {

            String line;
            boolean firstJsonEntry = true;

            if (isJson) {
                bw.write("[\n");
            } else {
                bw.write("id|name|gross pay\n");
            }

            while ((line = br.readLine()) != null) {
                String[] tokens = line.split("\\|");

                if (tokens.length == 4) {
                    String employeeId = tokens[0];
                    String name = tokens[1];
                    double hoursWorked = Double.parseDouble(tokens[2]);
                    double payRate = Double.parseDouble(tokens[3]);

                    Employee employee = new Employee(employeeId, name, hoursWorked, payRate);

                    if (isJson) {
                        if (!firstJsonEntry) {
                            bw.write(",\n");
                        }
                        String jsonEntry = String.format(
                                "  {\n    \"id\": %s,\n    \"name\": \"%s\",\n    \"grossPay\": %.2f\n  }",
                                employee.getEmployeeId(),
                                employee.getName(),
                                employee.getGrossPay()
                        );
                        bw.write(jsonEntry);
                        firstJsonEntry = false;
                    } else {
                        String csvRow = String.format("%s|%s|%.2f\n",
                                employee.getEmployeeId(),
                                employee.getName(),
                                employee.getGrossPay()
                        );
                        bw.write(csvRow);
                    }
                }
            }

            if (isJson) {
                bw.write("\n]");
            }

            System.out.println("Payroll report successfully created: " + outputFilePath);

        } catch (IOException e) {
            System.out.println("An error occurred while handling files: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Error parsing numeric data from file: " + e.getMessage());
        }
    }
}