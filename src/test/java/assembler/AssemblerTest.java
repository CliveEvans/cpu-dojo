package assembler;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AssemblerTest {

    @Test
    public void shouldReturn0ForBRK() {
        Assembler assembler = new Assembler();
        assembler.readLine("BRK");
        assertThat(assembler.memory(), is(array(0)));
    }

    @Test
    public void shouldReturn5ForINX() {
        Assembler assembler = new Assembler();
        assembler.readLine("INX");
        assertThat(assembler.memory(), is(array(5)));
    }

    @Test
    public void shouldReturnInstructionAndValuesForValueInstruction() {
        Assembler assembler = new Assembler();
        assembler.readLine("LDA 100");
        assertThat(assembler.memory(), is(array(1, 100)));
    }

    @Test
    public void shouldReadMultipleLines(){
        Assembler assembler = new Assembler();
        assembler.parse("LDA 100\n" +
                        "ADC 7\n" +
                        "STA 5\n" +
                        "BRK");
        assertThat(assembler.memory(), is(array(1, 100, 2, 7, 3, 5, 0)));
    }

    @Test
    public void shouldIgnoreBlankMultipleLines(){
        Assembler assembler = new Assembler();
        assembler.parse("LDA 100\n\n\n" +
                                "ADC 7\n\n" +
                                "STA 5\n" +
                                "BRK\n\n");
        assertThat(assembler.memory(), is(array(1, 100, 2, 7, 3, 5, 0)));
    }

    private int[] array(int... values) {
        return values;
    }

}
