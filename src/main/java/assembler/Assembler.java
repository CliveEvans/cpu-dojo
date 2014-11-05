package assembler;

import cpu.Operation;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Assembler {

    private static final Pattern LABEL_PATTERN = Pattern.compile("(\\w+):");
    private static final Pattern IS_NUMERIC_PATTERN = Pattern.compile("\\d+");

    List<Integer> data = new LinkedList<>();
    Map<String, Integer> labels = new HashMap<>();

    private int scanPointer = 0;

    public void readLine(String line) {
        String[] elements = splitLine(line);
        if(LABEL_PATTERN.matcher(elements[0]).matches()) {
            return;
        }
        Operation operation = getOperation(elements[0]);
        data.add(operation.ordinal());
        if(operation.equals(Operation.BNE)) {
            String next = elements[1];
            if(IS_NUMERIC_PATTERN.matcher(next).matches()) {
                data.add(Integer.valueOf(next));
            } else {
                data.add(labels.get(next) - (data.size() + 1));
            }
        } else {
            for (int i = 1; i < elements.length; i++) {
                data.add(Integer.valueOf(elements[i]));
            }
        }
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
        List<String> linesWithInstructions = Arrays.stream(program.split("\n"))
                                     .filter((t) -> !t.isEmpty())
                                     .collect(Collectors.toList());
        linesWithInstructions.forEach(this::scanLine);
        linesWithInstructions.forEach(this::readLine);
    }

    public void scanLine(String line) {
        String[] elements = splitLine(line);
        Matcher matcher = LABEL_PATTERN.matcher(elements[0]);
        if (matcher.matches()) {
            labels.put(matcher.group(1), scanPointer + 1);
        } else {
            for (int i = 1; i < elements.length; i++) {
                scanPointer++;
            }
        }
    }
}
