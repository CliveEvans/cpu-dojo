package assembler;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AssemblerTest {

    private Assembler assembler = new Assembler();

    @Test
    public void shouldReturn0ForBRK() {
        assembler.readLine("BRK");
        assertThat(assembler.memory(), is(array(0)));
    }

    @Test
    public void shouldReturn5ForINX() {
        assembler.readLine("INX");
        assertThat(assembler.memory(), is(array(5)));
    }

    @Test
    public void shouldReturnInstructionAndValuesForValueInstruction() {
        assembler.readLine("LDA 100");
        assertThat(assembler.memory(), is(array(1, 100)));
    }

    @Test
    public void shouldReadMultipleLines(){
        assembler.parse("LDA 100\n" +
                        "ADC 7\n" +
                        "STA 5\n" +
                        "BRK");
        assertThat(assembler.memory(), is(array(1, 100, 2, 7, 3, 5, 0)));
    }

    @Test
    public void shouldIgnoreBlankMultipleLines(){
        assembler.parse("LDA 100\n\n\n" +
                                "ADC 7\n\n" +
                                "STA 5\n" +
                                "BRK\n\n");
        assertThat(assembler.memory(), is(array(1, 100, 2, 7, 3, 5, 0)));
    }

    @Test
    public void shouldIgnoreLeadingWhitespace() {
        assembler.parse("  DEY");
        assertThat(assembler.memory(), is(array(9)));
    }

    @Test
    public void readLineShouldIgnoreLabel() {
        assembler.readLine("LDA 10");
        assembler.readLine("loop:");
        assembler.readLine("  DEY");

        assertThat(assembler.memory(), is(array(1, 10, 9)));
    }

    @Test
    public void shouldAddOfTheNextLocationToTheLabelsMap(){

        assembler.scanLine("LDA 10");
        assembler.scanLine("loop:");
        assembler.scanLine("  DEY");

        assertThat(assembler.labels.get("loop"), is(2));

    }

    @Test
    public void shouldSetTheValueOf_BNE_ToTheRelativeLocationOfTheLoopAddress() {

        assembler.labels.put("loop", 10);
        assembler.readLine("BNE loop");

        assertThat(assembler.memory(), is(array(7, 8)));
    }

    @Test
    public void shouldCorrectlyUseASimpleLoop() {
        String program = "LDY 3\n" +
                "loop:\n" +
                "  DEY\n" +
                "  CMY 0\n" +
                "  BNE loop\n" +
                "BRK";
        assembler.parse(program);

        assertThat(assembler.memory(), is(array(10, 3, 9, 6, 0, 7, -5, 0)));
    }

    @Test
    public void shouldStoreAnAddressAsNeedingASubroutineReference() {

    }

    private int[] array(int... values) {
        return values;
    }

}
