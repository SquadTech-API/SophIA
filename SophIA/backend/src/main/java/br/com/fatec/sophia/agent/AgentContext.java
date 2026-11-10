package br.com.fatec.sophia.agent;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import br.com.fatec.sophia.model.Book;
import br.com.fatec.sophia.model.BookFilters;

/**
 * Estado compartilhado de UMA execução do agente.
 *
 * <p>É o "quadro branco" que passa por todos os estados da máquina: cada estado
 * e cada comando lê o que precisa daqui e escreve de volta o que produziu
 * (filtros, embedding, resultados, resposta...). Como é criado por requisição,
 * não há estado compartilhado entre usuários — cada chamada tem o seu contexto.</p>
 */
public class AgentContext {

    // --- Entrada ---------------------------------------------------------
    private final String originalQuestion;   // pergunta crua, como chegou
    private String userQuestion;             // pergunta normalizada (usada pelos comandos)
    private final String plannerName;        // qual Strategy de planejamento usar
    private final int maxIterations;         // trava anti-loop

    // --- Dados de trabalho (preenchidos pelos comandos) ------------------
    private BookFilters filters;             // filtros extraídos da pergunta
    private String queryEmbedding;           // literal do vetor da pergunta
    private List<Book> results;              // livros encontrados
    private String response;                 // resposta final ao usuário

    // --- Controle da máquina de estados ----------------------------------
    private final Deque<String> plan = new ArrayDeque<>();   // fila de comandos a executar
    private boolean planned = false;         // o plano completo já foi montado? (PlanThenExecute)
    private boolean awaitingUser = false;    // precisa de mais input do usuário? (HumanInTheLoop)
    private int iterations = 0;              // quantos ciclos de PLANNING já rodaram
    private String lastCommand;              // último comando executado (para OBSERVING e logs)
    private String errorMessage;             // mensagem de erro, se algo falhar
    private final List<String> trace = new ArrayList<>();    // histórico de comandos executados

    public AgentContext(String question, String plannerName, int maxIterations) {
        this.originalQuestion = question;
        this.userQuestion = question;
        this.plannerName = plannerName;
        this.maxIterations = maxIterations;
    }

    // --- Helpers do plano (fila de comandos) -----------------------------

    /** Adiciona um comando ao fim da fila do plano. */
    public void pushStep(String commandName) {
        plan.addLast(commandName);
    }

    /** Retira e retorna o próximo comando do plano (ou {@code null} se vazio). */
    public String pollStep() {
        return plan.pollFirst();
    }

    public boolean hasSteps() {
        return !plan.isEmpty();
    }

    public void clearSteps() {
        plan.clear();
    }

    public void addTrace(String commandName) {
        trace.add(commandName);
    }

    public void incIterations() {
        this.iterations++;
    }

    // --- Getters / setters ----------------------------------------------

    public String getOriginalQuestion() {
        return originalQuestion;
    }

    public String getUserQuestion() {
        return userQuestion;
    }

    public void setUserQuestion(String userQuestion) {
        this.userQuestion = userQuestion;
    }

    public String getPlannerName() {
        return plannerName;
    }

    public int getMaxIterations() {
        return maxIterations;
    }

    public BookFilters getFilters() {
        return filters;
    }

    public void setFilters(BookFilters filters) {
        this.filters = filters;
    }

    public String getQueryEmbedding() {
        return queryEmbedding;
    }

    public void setQueryEmbedding(String queryEmbedding) {
        this.queryEmbedding = queryEmbedding;
    }

    public List<Book> getResults() {
        return results;
    }

    public void setResults(List<Book> results) {
        this.results = results;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public boolean isPlanned() {
        return planned;
    }

    public void setPlanned(boolean planned) {
        this.planned = planned;
    }

    public boolean isAwaitingUser() {
        return awaitingUser;
    }

    public void setAwaitingUser(boolean awaitingUser) {
        this.awaitingUser = awaitingUser;
    }

    public int getIterations() {
        return iterations;
    }

    public String getLastCommand() {
        return lastCommand;
    }

    public void setLastCommand(String lastCommand) {
        this.lastCommand = lastCommand;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public List<String> getTrace() {
        return trace;
    }
}
