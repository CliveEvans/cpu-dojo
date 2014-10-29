package assembler;

import cpu.Operation;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AssemblerTest {

    @Test
    public void shouldReturn0ForBRK() {
        Assembler assembler = new Assembler();
        assembler.readLine("BRK");
        assertThat(assembler.data(), is(array(0)));
    }

    @Test
    public void shouldReturn5ForINX() {
        Assembler assembler = new Assembler();
        assembler.readLine("INX");
        assertThat(assembler.data(), is(array(5)));
    }

    @Test
    public void shouldReturnInstructionAndValuesForValueInstruction() {
        Assembler assembler = new Assembler();
        assembler.readLine("LDA 100");
        assertThat(assembler.data(), is(array(1, 100)));
    }

    @Test
    public void shouldReadMultipleLines(){
        Assembler assembler = new Assembler();
        assembler.parse("LDA 100\n" +
                        "ADC 7\n" +
                        "STA 5\n" +
                        "BRK");
        assertThat(assembler.data(), is(array(1, 100, 2, 7, 3, 5, 0)));
    }

    private Integer[] array(Integer... values) {
        return values;
    }

    public static class Assembler {

        List<Integer> data = new LinkedList<>();

        public void readLine(String line) {
            String[] elements = line.split(" ");
            data.add(Operation.valueOf(elements[0]).ordinal());
            for (int i = 1; i < elements.length; i++) {
                data.add(Integer.valueOf(elements[i]));
            }
        }

        public Integer[] data() {
            return data.toArray(new Integer[data.size()]);
        }

        public void parse(String program) {
            Arrays.stream(program.split("\n"))
                    .forEach(this::readLine);
        }
    }
}
