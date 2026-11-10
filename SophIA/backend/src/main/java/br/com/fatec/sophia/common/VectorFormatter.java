package br.com.fatec.sophia.common;

import org.springframework.stereotype.Component;

/**
 * Converte um vetor de embedding (array de floats) para o literal textual que o
 * pgvector entende, no formato {@code [0.12,0.98,...]}.
 *
 * <p>Antes essa lógica estava copiada em 4 lugares diferentes (BookIndexer,
 * EmbeddingState e nas strategies de busca). Centralizar aqui elimina a
 * duplicação: se um dia o formato mudar, muda só neste ponto.</p>
 */
@Component
public class VectorFormatter {

    /**
     * @param vector vetor de floats devolvido pelo modelo de embedding
     * @return string no formato aceito pelo pgvector, ex.: {@code [0.1,0.2,0.3]}
     */
    public String toLiteral(float[] vector) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < vector.length; i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(vector[i]);
        }
        return sb.append("]").toString();
    }
}
