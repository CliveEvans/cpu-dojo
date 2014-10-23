package cpu;

public class CPU {

    public final int[] memory = new int[256];
    public int registerA;
    public int pointer = 0;
    public int registerX;
    public int registerY;
    public boolean flag;

    public CPU(int... input) {
        System.arraycopy(input, 0, this.memory, 0, input.length);
    }

    public void execute() {
        while(! currentOp().equals(Operation.BRK)) {
            switch (currentOp()) {
                case LDA:
                    advance();
                    registerA = current();
                    break;
                case ADC:
                    advance();
                    registerA += current();
                    break;
                case STA:
                    advance();
                    memory[current()] = registerA;
                    break;
                case STA_X:
                    memory[registerX] = registerA;
                    break;
                case LDX:
                    advance();
                    registerX = current();
                    break;
                case INX:
                    registerX++;
                    break;
                case LDY:
                    advance();
                    registerY = current();
                    break;
                case CMY:
                    advance();
                    flag = (current() == registerY);
                    break;
                case DEY:
                    registerY--;
                    break;
                case BNE:
                    advance();
                    if(!flag) {
                        pointer += current();
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Not implemented op code: " + currentOp());
            }
            advance();
        }

    }

    private Operation currentOp() {
        return Operation.values()[current()];
    }

    private int current() {
        return this.memory[pointer];
    }

    private void advance() {
        pointer++;
    }

    enum Operation {
        BRK, LDA, ADC, STA, LDX, INX, CMY, BNE, STA_X, DEY, LDY
    }
}
