package main;

import parser.Parser;
import code.Code;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Ponto de entrada do montador Hack.
 *
 * Etapa 1: traduz programas Assembly Hack
 * que nao possuem simbolos.
 */
public final class Main {

    private Main() {
    }

    /**
     * Executa o montador pela linha de comando.
     */
    public static void main(String[] args) {

        if (args.length != 1) {
            System.err.println(
                "Uso: java -cp build main.Main <arquivo.asm>"
            );

            System.exit(1);
            return;
        }

        try {
            Path input = Path.of(args[0]);
            Path output = assemble(input);

            System.out.println(
                "Arquivo gerado: " + output
            );

        } catch (IOException | IllegalArgumentException e) {

            System.err.println(
                "Erro: " + e.getMessage()
            );

            System.exit(1);
        }
    }

    /**
     * Traduz um arquivo .asm e gera um arquivo .hack.
     *
     * Retorna o caminho do arquivo produzido.
     */
    public static Path assemble(Path input) throws IOException {

        Path output = outputPath(input);

        Parser parser = new Parser(input);

        List<String> binary = new ArrayList<>();

        while (parser.hasMoreInstructions()) {

            parser.advance();

            switch (parser.instructionType()) {

                case A_INSTRUCTION -> {

                    String symbol = parser.symbol();

                    if (!symbol.matches("[0-9]+")) {
                        throw new IllegalArgumentException(
                            "Simbolos nao sao permitidos "
                            + "na Etapa 1: @" + symbol
                        );
                    }

                    int value = Integer.parseInt(symbol);

                    binary.add(Code.encodeA(value));
                }

                case C_INSTRUCTION -> {

                    String instruction = Code.encodeC(
                        parser.dest(),
                        parser.comp(),
                        parser.jump()
                    );

                    binary.add(instruction);
                }

                case L_INSTRUCTION -> {

                    throw new IllegalArgumentException(
                        "Rotulos nao sao permitidos "
                        + "na Etapa 1: "
                        + parser.symbol()
                    );
                }
            }
        }

        Files.write(output, binary);

        return output;
    }

    /**
     * Determina o caminho do arquivo de saida.
     */
    private static Path outputPath(Path input) {

        String filename = input.getFileName().toString();

        if (!filename.endsWith(".asm")) {
            throw new IllegalArgumentException(
                "O arquivo de entrada deve possuir "
                + "a extensao .asm."
            );
        }

        String outputName = filename.substring(
            0, filename.length() - 4
        ) + ".hack";

        return input.resolveSibling(outputName);
    }
}
