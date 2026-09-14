package br.com.fatec.sophia.command;

public interface AgentCommand<T> {
    T execute();
}