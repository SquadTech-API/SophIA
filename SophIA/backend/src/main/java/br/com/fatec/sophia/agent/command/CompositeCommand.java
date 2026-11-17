package br.com.fatec.sophia.agent.command;

import java.util.List;

import org.springframework.stereotype.Component;

import br.com.fatec.sophia.agent.AgentContext;

/**
 * Padrão <b>Composite</b>: um {@link Command} que é composto por OUTROS
 * {@link Command}s e é tratado exatamente como um comando único.
 *
 * <p>Esta é a aplicação clássica do Composite sobre o padrão Command (conhecida
 * como "Macro Command"): o objeto-folha (um comando simples, como
 * {@code search_books}) e o objeto-composto (este, que agrupa vários) expõem a
 * MESMA interface {@link Command}. Assim, quem executa não precisa saber se está
 * rodando um comando simples ou um grupo inteiro — ele chama {@code execute} do
 * mesmo jeito.</p>
 *
 * <p>Aqui o composto se chama {@code "retrieve"} e agrupa a etapa de recuperação
 * de livros: extrair filtros → gerar embedding → buscar. O planner
 * {@code CompositePlanner} usa este comando como um único passo do plano.</p>
 */
@Component
public class CompositeCommand implements Command {

    /** Os comandos "filhos" que este composto executa em sequência. */
    private final List<Command> children;

    public CompositeCommand(ExtractFiltersCommand extractFilters,
                            EmbedQueryCommand embedQuery,
                            SearchBooksCommand searchBooks) {
        // A árvore do Composite: um comando que contém outros comandos.
        this.children = List.of(extractFilters, embedQuery, searchBooks);
    }

    @Override
    public String name() {
        return "retrieve";
    }

    @Override
    public void execute(AgentContext ctx) {
        // Trata o grupo como uma peça única: executa cada filho na ordem.
        for (Command child : children) {
            child.execute(ctx);
        }
    }
}
