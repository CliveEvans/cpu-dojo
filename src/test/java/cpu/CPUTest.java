package cpu;

import org.junit.Test;

import static cpu.Operation.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CPUTest {


    @Test(timeout = 100)
    public void shouldStopOnBRK() throws Exception {
        new CPU(0).execute();
    }

    @Test
    public void shouldWriteTheValueToAonLDA() {
        CPU cpu = new CPU(1, 100);
        cpu.execute();

        assertThat(cpu.registerA, is(100));
        assertThat(cpu.pointer, is(2));
    }

    @Test
    public void shouldMoveForwardsAndAddTheNextLDA() {
        CPU cpu = new CPU(1, 100, 1, 150);
        cpu.execute();

        assertThat(cpu.registerA, is(150));
        assertThat(cpu.pointer, is(4));
    }


    @Test
    public void shouldAddTheNextMemoryRegisterIfADC() {
        CPU cpu = new CPU(1, 100, 2, 150);
        cpu.execute();

        assertThat(cpu.registerA, is(250));
        assertThat(cpu.pointer, is(4));
    }

    @Test
    public void shouldPlaceTheRegisterInAMemoryLocation() {
        CPU cpu = new CPU(1, 100, 3, 15);
        cpu.execute();

        assertThat(cpu.memory[15], is(100));
    }

    @Test
    public void shouldRunTimsProgram() {
        CPU cpu = new CPU(1, 100, 2, 7, 3, 15);
        cpu.execute();

        assertThat(cpu.memory[15], is(107));
    }

    @Test
    public void shouldLoadTheValueInTheNextMemoryAddressToRegisterX(){
        CPU cpu = new CPU(4, 20);
        cpu.execute();

        assertThat(cpu.registerX, is(20));
    }

    @Test
    public void shouldIncrementTheRegisterInXByOne(){
        CPU cpu = new CPU(4, 20, 5);
        cpu.execute();

        assertThat(cpu.registerX, is(21));
    }


    @Test
    public void shouldLoadTheValueInTheNextMemoryAddressToRegisterY(){
        CPU cpu = new CPU(10, 20);
        cpu.execute();

        assertThat(cpu.registerY, is(20));
    }

    @Test
    public void shouldCompareYToTheNextAddressWhenTheyAreTheSame(){
        CPU cpu = new CPU(10, 20, 6, 20);
        cpu.execute();

        assertThat(cpu.flag, is(true));
    }

    @Test
    public void shouldCompareYToTheNextAddressWhenTheyAreTheDifferent(){
        CPU cpu = new CPU(10, 20, 6, 30);
        cpu.execute();

        assertThat(cpu.flag, is(false));
    }

    @Test
    public void shouldExecuteTheNextInstructionIfTheValuesAreEqual(){
        CPU cpu = new CPU(10, 20, 6, 20, 7, 2, 5);
        cpu.execute();

        assertThat(cpu.flag, is(true));
        assertThat(cpu.registerX, is(1));
    }


    @Test
    public void shouldJumpIfTheValuesAreNotEqual(){
        CPU cpu = new CPU(LDY.ordinal(), 20, CMY.ordinal(), 21, BNE.ordinal(), 2, 5);
        cpu.execute();

        assertThat(cpu.flag, is(false));
        assertThat(cpu.registerX, is(0));
    }

    @Test
    public void shouldStoreTheValueInTheARegisterAtTheAddressInTheXRegister() {
        CPU cpu = new CPU(LDA.ordinal(), 27, LDX.ordinal(), 101, 8);
        cpu.execute();

        assertThat(cpu.memory[101], is(27));
    }

    @Test
    public void shouldDecrementTheValueInTheYRegister(){
        CPU cpu = new CPU(LDY.ordinal(), 20, 9);
        cpu.execute();

        assertThat(cpu.registerY, is(19));
    }

    @Test
    public void shouldWriteSomeStuffIn128To255() {
        int[] application = {
                4, 128, 1, 0x77, 8, 5, 1, 0x68, 8, 5, 1, 0x6F, 8, 5, 1, 0x20, 8, 5, 1, 0x6c, 8, 5, 1, 0x65, 8, 5, 1, 0x74, 8, 5, 1, 0x20, 8, 5, 1, 0x74, 8, 5, 1, 0x68, 8, 5, 1, 0x65, 8, 5, 1, 0x20, 8, 5, 1, 0x64, 8, 5, 1, 0x6F, 8, 5, 1, 0x67, 8, 5, 1, 0x73, 8, 5, 1, 0x20, 8, 5, 1, 0x6F, 8, 5, 1, 0x75, 8, 5, 1, 0x74, 8, 5, 1, 0x20, 8, 5, 10, 3, 1, 0x77, 8, 5, 1, 0x68, 8, 5, 1, 0x6F, 8, 5, 1, 0x20, 8, 5, 9, 6, 0, 7, -21, 0
        };
        CPU cpu = new CPU(application);

        cpu.execute();

        printAsciiMemory(cpu);

    }

    @Test
    public void theStackPointerShouldStartAtTheLastAddress() {
        CPU cpu = new CPU();
        assertThat(cpu.stackPointer, is(CPU.MEMORY_SIZE - 1));
    }

    @Test
    public void shouldSetTheCurrentPointerToTheLocationOfNextOnJSR() {
        CPU cpu = new CPU(11,12);
        cpu.doNextOp();
        assertThat(cpu.pointer, is(12));
    }

    @Test
    public void shouldSetTheCurrentStackToThePointerOnJSR() {
        CPU cpu = new CPU(5,11,12);
        cpu.pointer = 1;
        cpu.doNextOp();
        assertThat(cpu.memory[CPU.MEMORY_SIZE - 1], is(1));
    }

    @Test
    public void shouldDecrementTheStackPointerOnJSR() {
        CPU cpu = new CPU(11,12);
        cpu.doNextOp();
        assertThat(cpu.stackPointer, is(CPU.MEMORY_SIZE - 2));
    }

    @Test
    public void shouldContinueExecutionAfterJSR() {
        CPU cpu = new CPU(5,11,4,0,5,0);
        cpu.execute();
        assertThat(cpu.stackPointer, is(CPU.MEMORY_SIZE - 2));
        assertThat(cpu.registerX, is(2));
        assertThat(cpu.memory[cpu.stackPointer + 1], is(1));
    }

    @Test
    public void shouldAddOneToTheStackPointerOnRTS() {
        CPU cpu = new CPU(12,0);
        cpu.stackPointer = 10;
        cpu.doNextOp();
        assertThat(cpu.stackPointer, is(11));
    }

    @Test
    public void shouldSetTheProgramCounterToTheValueStoredAtStackPlus1AfterRTS() {
        CPU cpu = new CPU(12);
        cpu.stackPointer = 10;
        cpu.memory[11] = 7;
        cpu.doNextOp();
        assertThat(cpu.pointer, is(8));
    }

    private void printAsciiMemory(CPU cpu) {
        for (int i = 128; i < 256; i++) {

            char c = (char) cpu.memory[i];
            if (c == 0) {
                break;
            }
            System.out.print(Character.toString(c));
        }
    }
}
