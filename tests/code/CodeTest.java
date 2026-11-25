package code;

import java.util.Objects;

/**
 * Testes unitarios do modulo Code.
 */
public class CodeTest {

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

    private static void expectError(Runnable action) {
        try {
            action.run();
        } catch (IllegalArgumentException e) {
            total++;
            return;
        }

        throw new AssertionError(
            "Era esperada uma IllegalArgumentException."
        );
    }

    private static void testComp() {

        String[][] cases = {
            {"0",   "0101010"},
            {"1",   "0111111"},
            {"-1",  "0111010"},
            {"D",   "0001100"},
            {"A",   "0110000"},
            {"!D",  "0001101"},
            {"!A",  "0110001"},
            {"-D",  "0001111"},
            {"-A",  "0110011"},
            {"D+1", "0011111"},
            {"A+1", "0110111"},
            {"D-1", "0001110"},
            {"A-1", "0110010"},
            {"D+A", "0000010"},
            {"D-A", "0010011"},
            {"A-D", "0000111"},
            {"D&A", "0000000"},
            {"D|A", "0010101"},
            {"M",   "1110000"},
            {"!M",  "1110001"},
            {"-M",  "1110011"},
            {"M+1", "1110111"},
            {"M-1", "1110010"},
            {"D+M", "1000010"},
            {"D-M", "1010011"},
            {"M-D", "1000111"},
            {"D&M", "1000000"},
            {"D|M", "1010101"}
        };

        for (String[] item : cases) {
            check(item[1], Code.comp(item[0]));
        }
    }

    private static void testDest() {

        String[] mnemonics = {
            "", "M", "D", "MD", "A", "AM", "AD", "AMD"
        };

        String[] bits = {
            "000", "001", "010", "011",
            "100", "101", "110", "111"
        };

        for (int i = 0; i < mnemonics.length; i++) {
            check(bits[i], Code.dest(mnemonics[i]));
        }

        // Sinonimos exigidos pelo professor
        check("011", Code.dest("DM"));
        check("111", Code.dest("ADM"));
    }

    private static void testJump() {

        String[] mnemonics = {
            "", "JGT", "JEQ", "JGE",
            "JLT", "JNE", "JLE", "JMP"
        };

        String[] bits = {
            "000", "001", "010", "011",
            "100", "101", "110", "111"
        };

        for (int i = 0; i < mnemonics.length; i++) {
            check(bits[i], Code.jump(mnemonics[i]));
        }
    }

    private static void testEncodeA() {

        check("0000000000000000", Code.encodeA(0));
        check("0000000000000001", Code.encodeA(1));
        check("0000000000010101", Code.encodeA(21));
        check("0100000000000000", Code.encodeA(16384));
        check("0111111111111111", Code.encodeA(32767));

        // Valores fora do intervalo permitido
        expectError(() -> Code.encodeA(-1));
        expectError(() -> Code.encodeA(32768));
    }

    private static void testEncodeC() {

        // MD=M+1
        check(
            "1111110111011000",
            Code.encodeC("MD", "M+1", "")
        );

        // DM=M+1
        check(
            "1111110111011000",
            Code.encodeC("DM", "M+1", "")
        );

        // DM=M-1
        check(
            "1111110010011000",
            Code.encodeC("DM", "M-1", "")
        );

        // ADM=M+1
        check(
            "1111110111111000",
            Code.encodeC("ADM", "M+1", "")
        );

        // 0;JMP
        check(
            "1110101010000111",
            Code.encodeC("", "0", "JMP")
        );

        // D;JGT
        check(
            "1110001100000001",
            Code.encodeC("", "D", "JGT")
        );

        // D=A
        check(
            "1110110000010000",
            Code.encodeC("D", "A", "")
        );

        // D=A;JNE
        check(
            "1110110000010101",
            Code.encodeC("D", "A", "JNE")
        );
    }

    private static void testInvalidMnemonics() {

        expectError(() -> Code.comp("D*2"));
        expectError(() -> Code.comp(""));
        expectError(() -> Code.dest("X"));
        expectError(() -> Code.jump("JINVALID"));
        expectError(() -> Code.comp(null));
    }

    public static void main(String[] args) {

        testComp();
        testDest();
        testJump();
        testEncodeA();
        testEncodeC();
        testInvalidMnemonics();

        System.out.println(
            "CodeTest: " + total
            + " verificacoes passaram."
        );
    }
}
