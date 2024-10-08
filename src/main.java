import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class main {
    public static void main(String[] args) {
        String filename = "src/expresiones.txt";

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            int count = 1;
            while ((line = br.readLine()) != null) {
                // Convertir de infix a postfix
                String postfix = ShuntingYard.infixToPostfix(line);
                System.out.println("Infix: " + line + " -> Postfix: " + postfix);

                try {
                    // Construir el AST desde la expresión postfix
                    ASTNode root = PostfixToAST.constructAST(postfix);

                    // Visualizar el AST en un archivo .dot
                    String astFilename = "ast_" + count + ".dot";
                    DrawAST.draw(root, astFilename);

                    // Generar el AFN desde el AST
                    NFA nfa = root.toNFA();

                    // Visualizar el AFN generado en un archivo .dot
                    NFAtoGraphvizVisitor nfaVisitor = new NFAtoGraphvizVisitor();
                    nfaVisitor.visit(nfa.start); // Cambiado para que visite el estado inicial
                    String nfaGraph = nfaVisitor.getGraph();
                    String nfaFilename = "nfa_" + count + ".dot";
                    try (FileWriter writer = new FileWriter(nfaFilename)) {
                        writer.write(nfaGraph);
                    }
                    Runtime.getRuntime().exec(new String[] { "dot", "-Tpng", nfaFilename, "-o", nfaFilename + ".png" });

                    // Generar el AFD a partir del AFN usando la construcción de subconjuntos
                    SubsetConstruction subsetConstruction = new SubsetConstruction(nfa);
                    DFA dfa = subsetConstruction.toDFA(); // Cambiado a toDFA()

                    // Visualizar el AFD generado en un archivo .dot
                    DFAToGraphvizVisitor dfaVisitor = new DFAToGraphvizVisitor();
                    dfaVisitor.visit(dfa); // Cambiado para que visite el DFA
                    String dfaGraph = dfaVisitor.getGraph();
                    String dfaFilename = "dfa_" + count + ".dot";
                    try (FileWriter writer = new FileWriter(dfaFilename)) {
                        writer.write(dfaGraph);
                    }
                    Runtime.getRuntime().exec(new String[] { "dot", "-Tpng", dfaFilename, "-o", dfaFilename + ".png" });

                    dfa.minimize();

                    // DFA minimizado
                    DFAToGraphvizVisitor dfaVisitor1 = new DFAToGraphvizVisitor();
                    dfaVisitor1.visit(dfa); // Cambiado para que visite el DFA
                    String dfaGraph1 = dfaVisitor1.getGraph();
                    String dfaFilename1 = "dfa_Minimizado" + count + ".dot";
                    try (FileWriter writer = new FileWriter(dfaFilename1)) {
                        writer.write(dfaGraph1);
                    }
                    Runtime.getRuntime()
                            .exec(new String[] { "dot", "-Tpng", dfaFilename1, "-o", dfaFilename1 +
                                    ".png" });

                    // Simulación del AFN con una cadena de prueba y un límite de visitas
                    int visitLimit = 4; // Número de veces que un estado puede ser visitado
                    String cadena = "aab";
                    boolean result = nfa.simulate(cadena, visitLimit);
                    System.out.println(
                            "La cadena: " + cadena + " es " + (result ? "aceptada" : "rechazada") + " por el AFN.");

                    // Simulación del AFD con la misma cadena de prueba
                    boolean resultDFA = dfa.simulate(cadena);
                    System.out.println(
                            "La cadena: " + cadena + " es " + (resultDFA ? "aceptada" : "rechazada") + " por el AFD.");

                    count++;
                } catch (IllegalArgumentException e) {
                    System.err.println("Error procesando la expresión: " + line);
                    System.err.println(e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error leyendo el archivo: " + e.getMessage());
        }
    }
}