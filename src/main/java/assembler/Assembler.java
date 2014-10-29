package assembler;

import cpu.Operation;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Assembler {

    List<Integer> data = new LinkedList<>();

    public void readLine(String line) {
        String[] elements = line.split(" ");
        data.add(Operation.valueOf(elements[0]).ordinal());
        for (int i = 1; i < elements.length; i++) {
            data.add(Integer.valueOf(elements[i]));
        }
    }

    public int[] memory() {
        return data
                .stream()
                .mapToInt(i -> i)
                .toArray();
    }

    public void parse(String program) {
        Arrays.stream(program.split("\n"))
                .filter((t) -> !t.isEmpty())
                .forEach(this::readLine);
    }
}
