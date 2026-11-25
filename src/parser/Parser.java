package parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsavel pela leitura e decomposicao
 * das instrucoes Assembly Hack.
 */
public class Parser {

    private final List<String> instructions = new ArrayList<>();
    private int index = -1;

    /**
     * Le o arquivo e remove comentarios,
     * espacos e linhas vazias.
     */
    public Parser(Path file) throws IOException {
        for (String line : Files.readAllLines(file)) {

            int comment = line.indexOf("//");

            if (comment >= 0) {
                line = line.substring(0, comment);
            }

            line = line.replaceAll("\\s+", "");

            if (!line.isEmpty()) {
                instructions.add(line);
            }
        }
    }

    /**
     * Verifica se ainda existem instrucoes.
     */
    public boolean hasMoreInstructions() {
        return index + 1 < instructions.size();
    }

    /**
     * Avanca para a proxima instrucao.
     */
    public void advance() {
        if (!hasMoreInstructions()) {
            throw new IllegalStateException(
                "Nao ha mais instrucoes."
            );
        }

        index++;
    }

    /**
     * Retorna a instrucao atual.
     */
    private String current() {
        if (index < 0) {
            throw new IllegalStateException(
                "Chame advance() primeiro."
            );
        }

        return instructions.get(index);
    }

    /**
     * Identifica o tipo da instrucao atual.
     */
    public InstructionType instructionType() {
        String instruction = current();

        if (instruction.startsWith("@")) {
            return InstructionType.A_INSTRUCTION;
        }

        if (instruction.startsWith("(")) {
            return InstructionType.L_INSTRUCTION;
        }

        return InstructionType.C_INSTRUCTION;
    }

    /**
     * Retorna o simbolo de uma instrucao A ou L.
     */
    public String symbol() {
        String instruction = current();

        return switch (instructionType()) {

            case A_INSTRUCTION ->
                instruction.substring(1);

            case L_INSTRUCTION -> {
                if (!instruction.endsWith(")")) {
                    throw new IllegalArgumentException(
                        "Rotulo invalido: " + instruction
                    );
                }

                yield instruction.substring(
                    1, instruction.length() - 1
                );
            }

            case C_INSTRUCTION ->
                throw new IllegalStateException(
                    "Instrucao C nao possui simbolo."
                );
        };
    }

    /**
     * Retorna o campo dest de uma instrucao C.
     */
    public String dest() {
        String instruction = currentC();
        int equals = instruction.indexOf('=');

        return equals < 0
            ? ""
            : instruction.substring(0, equals);
    }

    /**
     * Retorna o campo comp de uma instrucao C.
     */
    public String comp() {
        String instruction = currentC();

        int start = instruction.indexOf('=') + 1;
        int end = instruction.indexOf(';');

        return instruction.substring(
            start,
            end < 0 ? instruction.length() : end
        );
    }

    /**
     * Retorna o campo jump de uma instrucao C.
     */
    public String jump() {
        String instruction = currentC();
        int semicolon = instruction.indexOf(';');

        return semicolon < 0
            ? ""
            : instruction.substring(semicolon + 1);
    }

    /**
     * Garante que a instrucao atual seja do tipo C.
     */
    private String currentC() {
        if (instructionType() != InstructionType.C_INSTRUCTION) {
            throw new IllegalStateException(
                "Instrucao atual nao e do tipo C."
            );
        }

        return current();
    }
}
