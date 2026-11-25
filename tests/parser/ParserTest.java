package parser;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Testes unitarios do modulo Parser.
 */
public class ParserTest {

    private static void check(Object expected, Object actual) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError(
                "Esperado: " + expected
                + " | Obtido: " + actual
            );
        }
    }

    public static void main(String[] args) throws Exception {

        Path file = Files.createTempFile(
            "parser-test", ".asm"
        );

        try {
            Files.writeString(file, """
                // Comentario de linha inteira

                @21 // Comentario ao final
                MD = M + 1
                D ; JGT
                D = A ; JNE
                (LOOP)
                @LOOP
                0;JMP
                """);

            Parser p = new Parser(file);

            // Teste 1: existencia de instrucoes
            check(true, p.hasMoreInstructions());

            // Teste 2: instrucao A numerica
            p.advance();

            check(
                InstructionType.A_INSTRUCTION,
                p.instructionType()
            );

            check("21", p.symbol());

            // Teste 3: instrucao C com destino
            p.advance();

            check(
                InstructionType.C_INSTRUCTION,
                p.instructionType()
            );

            check("MD", p.dest());
            check("M+1", p.comp());
            check("", p.jump());

            // Teste 4: instrucao C com salto
            p.advance();

            check("", p.dest());
            check("D", p.comp());
            check("JGT", p.jump());

            // Teste 5: instrucao C completa
            p.advance();

            check("D", p.dest());
            check("A", p.comp());
            check("JNE", p.jump());

            // Teste 6: declaracao de rotulo
            p.advance();

            check(
                InstructionType.L_INSTRUCTION,
                p.instructionType()
            );

            check("LOOP", p.symbol());

            // Teste 7: instrucao A simbolica
            p.advance();

            check(
                InstructionType.A_INSTRUCTION,
                p.instructionType()
            );

            check("LOOP", p.symbol());

            // Teste 8: salto incondicional
            p.advance();

            check("", p.dest());
            check("0", p.comp());
            check("JMP", p.jump());

            // Teste 9: fim das instrucoes
            check(false, p.hasMoreInstructions());

            System.out.println(
                "ParserTest: todos os testes passaram."
            );

        } finally {
            Files.deleteIfExists(file);
        }
    }
}
