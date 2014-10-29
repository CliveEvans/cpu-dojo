package cpu;

import assembler.Assembler;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Runner {

    public static void main(String[] arguments) throws Exception {
        String content = new String(Files.readAllBytes(Paths.get("src/main/resources/", arguments[0])));

        Assembler assembler = new Assembler();
        assembler.parse(content);
        CPU cpu = new CPU(assembler.memory());
        cpu.execute();
        cpu.printAsciiMemory();
    }

}
