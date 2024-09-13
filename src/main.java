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

                    // Generar el AFN desde el AST
                    NFA nfa = root.toNFA();

                    // Visualizar el AFN generado en un archivo .dot
                    NFAtoGraphvizVisitor nfaVisitor = new NFAtoGraphvizVisitor();
                    nfaVisitor.visit(nfa.start);
                    String nfaGraph = nfaVisitor.getGraph();
                    String nfaFilename = "nfa_" + count + ".dot";
                    try (FileWriter writer = new FileWriter(nfaFilename)) {
                        writer.write(nfaGraph);
                    }
                    Runtime.getRuntime().exec(new String[] { "dot", "-Tpng", nfaFilename, "-o", nfaFilename + ".png" });

                    // Simulación del AFN con una cadena de prueba y un límite de visitas
                    int visitLimit = 4; // Número de veces que un estado puede ser visitado
                    String cadena = "aab";
                    boolean result = nfa.simulate(cadena, visitLimit);
                    System.out.println(
                            "La cadena: " + cadena + " es " + (result ? "aceptada" : "rechazada") + " por el AFN.");

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
