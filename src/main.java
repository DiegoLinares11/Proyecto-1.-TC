import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class main {
    public static void main(String[] args) {
        // Nombre del archivo que contiene las expresiones regulares
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

                    // Generar el NFA desde el AST
                    NFA nfa = root.toNFA();
                    System.out.println("Estado de aceptación en el NFA: " + nfa.accept.isAccept);
                    // Convertir el NFA a DFA
                    DFA dfa = SubsetConstruction.constructDFA(nfa);
                    if (dfa == null) {
                        System.out.println("Error: El DFA es null. No se puede continuar.");
                        return;
                    }
                    dfa.removeUnreachableStates();  // Elimina estados no alcanzables

                    // Minimizar el DFA
                    DFA minimizedDFA = DFAMinimization.minimizeDFA(dfa);
                    if (minimizedDFA == null) {
                        System.out.println("Error: El DFA minimizado es null.");
                        return;
                    }

                    // Visualizar el DFA minimizado en un archivo .dot
                    DFAToGraphvizVisitor dfaVisitor = new DFAToGraphvizVisitor();
                    dfaVisitor.visit(minimizedDFA);
                    String dfaGraph = dfaVisitor.getGraph();
                    String dfaFilename = "dfa_" + count + ".dot";
                    try (FileWriter writer = new FileWriter(dfaFilename)) {
                        writer.write(dfaGraph);
                    }
                    Runtime.getRuntime().exec(new String[] { "dot", "-Tpng", dfaFilename, "-o", dfaFilename + ".png" });

                    // Simulación del DFA con una cadena de prueba
                    String cadena = "aab";
                    boolean result = minimizedDFA.simulate(cadena);
                    System.out.println(
                            "La cadena: " + cadena + " es " + (result ? "aceptada" : "rechazada") + " por el DFA.");

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
