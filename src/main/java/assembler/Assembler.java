package assembler;

import cpu.Operation;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Assembler {

    private static final Pattern LABEL_PATTERN = Pattern.compile("(\\w+):");
    private static final Pattern IS_NUMERIC_PATTERN = Pattern.compile("\\d+");
    private Map<Operation, ParseLabelOperation> parseLabelOperationMap = new HashMap<>();

    List<Integer> data = new LinkedList<>();
    Map<String, Integer> labels = new HashMap<>();

    public Assembler() {
        parseLabelOperationMap.put(Operation.BNE, new ParseBNEOperation());
        parseLabelOperationMap.put(Operation.JSR, new ParseJSROperation());
    }

    private int scanPointer = 0;

    public void readLine(String line) {
        String[] elements = splitLine(line);
        if(LABEL_PATTERN.matcher(elements[0]).matches()) {
            return;
        }
        Operation operation = getOperation(elements[0]);
        data.add(operation.ordinal());
        if (isPossibleLabelOperation(operation)) {
            handlePotentialLabelOperation(elements, operation);
        } else {
            for (int i = 1; i < elements.length; i++) {
                data.add(Integer.valueOf(elements[i]));
            }
        }
    }

    private void handlePotentialLabelOperation(String[] elements, Operation operation) {
        parseLabelOperationMap.get(operation).invoke(elements[1]);
    }

    private boolean isPossibleLabelOperation(Operation operation) {
        return parseLabelOperationMap.containsKey(operation);
    }

    private Operation getOperation(String element) {
        return Operation.valueOf(element);
    }

    private String[] splitLine(String line) {
        return line.replaceAll("^ *", "").split(" ");
    }

    public int[] memory() {
        return data
                .stream()
                .mapToInt(i -> i)
                .toArray();
    }

    public void parse(String program) {
        List<String> linesWithInstructions = Arrays.asList(program.split("\n"));

        linesWithInstructions.stream()
                             .filter((t) -> !t.isEmpty())
                             .forEach(this::scanLine);
        linesWithInstructions.stream()
                             .filter((t) -> !t.isEmpty())
                             .forEach(this::readLine);
    }

    public void scanLine(String line) {
        String[] elements = splitLine(line);
        Matcher matcher = LABEL_PATTERN.matcher(elements[0]);
        if (matcher.matches()) {
            labels.put(matcher.group(1), scanPointer);
        } else {
            scanPointer+= elements.length;
        }
    }

    public interface ParseLabelOperation {

        void invoke(String element);

    }

    private class ParseBNEOperation  implements ParseLabelOperation {

        @Override
        public void invoke(String element) {
            if(IS_NUMERIC_PATTERN.matcher(element).matches()) {
                data.add(Integer.valueOf(element));
            } else {
                data.add(labels.get(element) - (data.size() + 1));
            }
        }
    }

    private class ParseJSROperation implements ParseLabelOperation {

        @Override
        public void invoke(String element) {
            if(IS_NUMERIC_PATTERN.matcher(element).matches()) {
                data.add(Integer.valueOf(element));
            } else {
                data.add(labels.get(element));
            }
        }
    }
}
