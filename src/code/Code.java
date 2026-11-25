package code;

import java.util.Map;

/**
 * Responsavel pela traducao das instrucoes
 * Assembly Hack para codigo de maquina binario.
 */
public final class Code {

    /**
     * Tabela de computacao: 7 bits.
     */
    private static final Map<String, String> COMP = Map.ofEntries(

        // Operacoes com a = 0
        Map.entry("0",   "0101010"),
        Map.entry("1",   "0111111"),
        Map.entry("-1",  "0111010"),
        Map.entry("D",   "0001100"),
        Map.entry("A",   "0110000"),
        Map.entry("!D",  "0001101"),
        Map.entry("!A",  "0110001"),
        Map.entry("-D",  "0001111"),
        Map.entry("-A",  "0110011"),
        Map.entry("D+1", "0011111"),
        Map.entry("A+1", "0110111"),
        Map.entry("D-1", "0001110"),
        Map.entry("A-1", "0110010"),
        Map.entry("D+A", "0000010"),
        Map.entry("D-A", "0010011"),
        Map.entry("A-D", "0000111"),
        Map.entry("D&A", "0000000"),
        Map.entry("D|A", "0010101"),

        // Operacoes com a = 1
        Map.entry("M",   "1110000"),
        Map.entry("!M",  "1110001"),
        Map.entry("-M",  "1110011"),
        Map.entry("M+1", "1110111"),
        Map.entry("M-1", "1110010"),
        Map.entry("D+M", "1000010"),
        Map.entry("D-M", "1010011"),
        Map.entry("M-D", "1000111"),
        Map.entry("D&M", "1000000"),
        Map.entry("D|M", "1010101")
    );

    /**
     * Tabela de destino: 3 bits.
     * Inclui os sinonimos DM e ADM.
     */
    private static final Map<String, String> DEST = Map.ofEntries(
        Map.entry("",    "000"),
        Map.entry("M",   "001"),
        Map.entry("D",   "010"),
        Map.entry("MD",  "011"),
        Map.entry("DM",  "011"),
        Map.entry("A",   "100"),
        Map.entry("AM",  "101"),
        Map.entry("AD",  "110"),
        Map.entry("AMD", "111"),
        Map.entry("ADM", "111")
    );

    /**
     * Tabela de salto: 3 bits.
     */
    private static final Map<String, String> JUMP = Map.of(
        "",    "000",
        "JGT", "001",
        "JEQ", "010",
        "JGE", "011",
        "JLT", "100",
        "JNE", "101",
        "JLE", "110",
        "JMP", "111"
    );

    /**
     * Impede a criacao de instancias da classe.
     */
    private Code() {
    }

    /**
     * Traduz o campo comp para 7 bits.
     */
    public static String comp(String mnemonic) {
        return lookup(COMP, mnemonic, "comp");
    }

    /**
     * Traduz o campo dest para 3 bits.
     */
    public static String dest(String mnemonic) {
        return lookup(DEST, mnemonic, "dest");
    }

    /**
     * Traduz o campo jump para 3 bits.
     */
    public static String jump(String mnemonic) {
        return lookup(JUMP, mnemonic, "jump");
    }

    /**
     * Codifica uma instrucao A em 16 bits.
     */
    public static String encodeA(int value) {

        if (value < 0 || value > 32767) {
            throw new IllegalArgumentException(
                "Valor fora do intervalo de 0 a 32767: "
                + value
            );
        }

        String binary = Integer.toBinaryString(value);

        return "0" + "0".repeat(15 - binary.length())
            + binary;
    }

    /**
     * Codifica uma instrucao C em 16 bits.
     */
    public static String encodeC(
        String dest,
        String comp,
        String jump
    ) {
        return "111"
            + Code.comp(comp)
            + Code.dest(dest)
            + Code.jump(jump);
    }

    /**
     * Consulta uma tabela e rejeita mnemonicos invalidos.
     */
    private static String lookup(
        Map<String, String> table,
        String mnemonic,
        String field
    ) {
        if (mnemonic == null || !table.containsKey(mnemonic)) {
            throw new IllegalArgumentException(
                "Mnemonico invalido para "
                + field + ": " + mnemonic
            );
        }

        return table.get(mnemonic);
    }
}
