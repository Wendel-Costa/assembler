package integration;

import main.Main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Testes de integracao do montador sem simbolos.
 */
public class MainTest {

    private static int total = 0;

    private static void check(Object expected, Object actual) {

        if (!Objects.equals(expected, actual)) {
            throw new AssertionError(
                "Esperado: " + expected
                + " | Obtido: " + actual
            );
        }

        total++;
    }

    /**
     * Testa a traducao do programa Add.
     */
    private static void testAdd(Path dir) throws IOException {

        Path input = dir.resolve("Add.asm");

        Files.writeString(input, """
            // RAM[0] = 2 + 3
            @2
            D=A
            @3
            D=D+A
            @0
            M=D
            """);

        Path output = Main.assemble(input);

        check(dir.resolve("Add.hack"), output);

        check(
            List.of(
                "0000000000000010",
                "1110110000010000",
                "0000000000000011",
                "1110000010010000",
                "0000000000000000",
                "1110001100001000"
            ),
            Files.readAllLines(output)
        );
    }

    /**
     * Testa diferentes formatos de instrucoes.
     */
    private static void testMixed(Path dir) throws IOException {

        Path input = dir.resolve("Mixed.asm");

        Files.writeString(input, """
            // Comentario
            @32767 // Limite
            DM = M - 1
            ADM = M + 1
            D = A ; JNE
            0 ; JMP
            """);

        Path output = Main.assemble(input);

        List<String> lines = Files.readAllLines(output);

        check(
            List.of(
                "0111111111111111",
                "1111110010011000",
                "1111110111111000",
                "1110110000010101",
                "1110101010000111"
            ),
            lines
        );

        // Toda instrucao deve possuir exatamente 16 bits.
        for (String line : lines) {
            check(true, line.matches("[01]{16}"));
        }
    }

    /**
     * Testa um arquivo sem instrucoes.
     */
    private static void testEmpty(Path dir) throws IOException {

        Path input = dir.resolve("Empty.asm");

        Files.writeString(
            input,
            "// Apenas comentario\n\n"
        );

        check(
            List.of(),
            Files.readAllLines(Main.assemble(input))
        );
    }

    /**
     * Verifica se uma entrada invalida e rejeitada.
     */
    private static void expectInvalid(
        Path dir,
        String name,
        String source
    ) throws IOException {

        Path input = dir.resolve(name + ".asm");

        Files.writeString(input, source);

        try {
            Main.assemble(input);

            throw new AssertionError(
                "Entrada invalida aceita: " + name
            );

        } catch (IllegalArgumentException e) {
            total++;
        }

        // Nenhum arquivo novo deve ser gerado.
        check(
            false,
            Files.exists(dir.resolve(name + ".hack"))
        );
    }

    public static void main(String[] args) throws IOException {

        Path dir = Files.createTempDirectory(
            "assembler test "
        );

        try {

            testAdd(dir);
            testMixed(dir);
            testEmpty(dir);

            // Variavel nao permitida na Etapa 1.
            expectInvalid(
                dir,
                "Symbol",
                "@2\n@variable\n"
            );

            // Rotulo nao permitido na Etapa 1.
            expectInvalid(
                dir,
                "Label",
                "(LOOP)\n@0\n"
            );

            // Valor superior ao limite de 15 bits.
            expectInvalid(
                dir,
                "Overflow",
                "@32768\n"
            );

            // Operacao computacional inexistente.
            expectInvalid(
                dir,
                "InvalidComp",
                "D=D*2\n"
            );

            System.out.println(
                "MainTest: " + total
                + " verificacoes passaram."
            );

        } finally {

            // Remove os arquivos temporarios dos testes.
            try (var paths = Files.walk(dir)) {

                for (
                    Path path :
                    paths.sorted(
                        Comparator.reverseOrder()
                    ).toList()
                ) {
                    Files.deleteIfExists(path);
                }
            }
        }
    }
}
