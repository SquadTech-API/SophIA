package br.com.fatec.sophia;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import br.com.fatec.sophia.common.VectorFormatter;

/**
 * Testes unitários puros (não sobem o contexto do Spring, não dependem de banco
 * nem do Ollama) — assim {@code mvn package} roda mesmo sem a infra no ar.
 *
 * <p>O teste de integração que sobe o contexto inteiro ({@code @SpringBootTest})
 * foi deixado de fora de propósito, porque exigiria PostgreSQL e Ollama rodando.</p>
 */
class SophiaApplicationTests {

    @Test
    void vectorFormatterGeraLiteralDoPgvector() {
        VectorFormatter formatter = new VectorFormatter();
        String literal = formatter.toLiteral(new float[] {0.1f, 0.2f, 0.3f});
        assertEquals("[0.1,0.2,0.3]", literal);
    }

    @Test
    void vectorFormatterComVetorVazio() {
        VectorFormatter formatter = new VectorFormatter();
        assertEquals("[]", formatter.toLiteral(new float[] {}));
    }
}
