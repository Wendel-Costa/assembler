# Relatório de validação — Etapa 1

## 1. Objetivo

Validar o montador Hack sem símbolos, verificando se os
arquivos binários produzidos pela implementação em Java
são idênticos aos gerados pelo Assembler oficial do
Nand2Tetris.

## 2. Ambiente

- Linguagem: Java
- Compatibilidade: Java 17 ou superior
- Sistema operacional: Fedora Linux
- Material de teste: Nand2Tetris Project 06
- Ferramenta de referência: Assembler oficial
- Ferramenta de execução: CPU Emulator

## 3. Metodologia

A validação foi organizada nas seguintes etapas:

1. Compilação do código-fonte.
2. Execução dos testes unitários e de integração.
3. Tradução dos programas Assembly sem símbolos.
4. Geração independente dos arquivos binários de referência.
5. Comparação dos arquivos utilizando o comando diff.
6. Verificação do comportamento dos programas no CPU Emulator.

## 4. Testes automatizados

| Módulo | Resultado |
| --- | --- |
| ParserTest | Aprovado |
| CodeTest | 66 verificações aprovadas |
| MainTest | 17 verificações aprovadas |

Todos os testes automatizados foram executados com sucesso.

## 5. Comparação com o Assembler oficial

| Programa | Instruções geradas | Comparação | Resultado |
| --- | ---: | --- | --- |
| Add.asm | 6 | Arquivos idênticos | Aprovado |
| MaxL.asm | 16 | Arquivos idênticos | Aprovado |
| RectL.asm | 25 | Arquivos idênticos | Aprovado |
| PongL.asm | 27483 | Arquivos idênticos | Aprovado |
| Total | 27530 | 4 de 4 programas | Aprovado |

Resumo:

- Programas testados: 4
- Programas aprovados: 4
- Programas reprovados: 0
- Taxa de aprovação: 100%

Todos os arquivos binários produzidos foram idênticos
aos arquivos gerados pelo Assembler oficial.

## 6. Validação no CPU Emulator

| Programa | Comportamento esperado | Resultado |
| --- | --- | --- |
| Add | RAM[0] = 5 | Pendente |
| MaxL | RAM[2] contém o maior valor | Pendente |
| RectL | Retângulo desenhado na tela | Pendente |
| PongL | Jogo executado e controle pelo teclado | Pendente |

## 7. Reprodução dos testes

### Testes automatizados

Executar na raiz do repositório:

    ./scripts/test.sh

### Comparação com o Assembler oficial

    ./scripts/validate_stage1.sh /caminho/nand2tetris

Caso o diretório do Project 06 esteja em outro caminho:

    ./scripts/validate_stage1.sh /caminho/nand2tetris /caminho/project06

Os resultados são armazenados em:

    build/etapa1/our/
    build/etapa1/reference/

### Comparação manual

Exemplo:

    diff build/etapa1/our/Add.hack build/etapa1/reference/Add.hack

A ausência de diferenças indica que os arquivos são
idênticos.

## 8. Conclusão

A implementação do montador sem símbolos foi aprovada
em todos os testes automatizados.

Os quatro programas do Project 06 foram traduzidos
corretamente, totalizando 27530 instruções binárias,
sem divergências em relação ao Assembler oficial.

A validação funcional no CPU Emulator complementa
a comparação binária.
