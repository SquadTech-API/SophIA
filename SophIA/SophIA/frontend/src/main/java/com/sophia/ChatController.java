package com.sophia;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Controller da tela de chat. Agora conectado de verdade ao backend: cada
 * mensagem do usuário é enviada à API REST da SophIA, e a resposta volta para a
 * conversa. A chamada roda em uma thread separada para não travar a interface.
 */
public class ChatController {

    @FXML
    private VBox messagesBox;

    @FXML
    private TextField messageField;

    @FXML
    private ScrollPane chatScroll;

    @FXML
    private ComboBox<String> plannerCombo;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
    private final SophiaApiClient apiClient = new SophiaApiClient();

    /** Bolha de "digitando..." exibida enquanto esperamos o backend. */
    private HBox typingRow;

    @FXML
    public void initialize() {
        // Estratégias (planners) disponíveis — demonstra o padrão Strategy ao vivo.
        plannerCombo.getItems().setAll("Plan-then-Execute", "ReAct", "Human-in-the-Loop");
        plannerCombo.setValue("Plan-then-Execute");

        Platform.runLater(messageField::requestFocus);
    }

    @FXML
    private void sendMessage() {
        String message = messageField.getText().trim();
        if (message.isEmpty()) {
            return;
        }

        adicionarMensagemUsuario(message);
        messageField.clear();

        String planner = plannerCombo.getValue() == null
                ? null
                : plannerCombo.getValue().toLowerCase(); // "ReAct" -> "react", etc.

        setCarregando(true);

        // Chamada ao backend em uma thread virtual, fora da thread de UI.
        Thread.ofVirtual().name("sophia-call").start(() -> {
            try {
                SophiaApiClient.ChatReply reply = apiClient.send(message, planner);
                Platform.runLater(() -> {
                    setCarregando(false);
                    adicionarResposta(reply.answer());
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    setCarregando(false);
                    adicionarResposta("Não consegui falar com o servidor da SophIA em "
                            + apiClient.getBaseUrl() + ".\n"
                            + "Verifique se o backend está rodando e tente novamente.");
                });
            }
        });
    }

    // ---------------------------------------------------------------------
    // Montagem das bolhas de mensagem
    // ---------------------------------------------------------------------

    private void adicionarMensagemUsuario(String texto) {
        Label mensagem = new Label(texto);
        mensagem.setWrapText(true);
        mensagem.setMaxWidth(500);
        mensagem.getStyleClass().add("user-message");

        VBox conteudo = new VBox(8, mensagem, criarHorario());
        conteudo.setAlignment(Pos.CENTER_RIGHT);

        Label icone = new Label("●");
        icone.getStyleClass().add("user-icon");

        HBox linha = new HBox(12, conteudo, icone);
        linha.setAlignment(Pos.CENTER_RIGHT);
        linha.getStyleClass().add("message-row");

        messagesBox.getChildren().add(linha);
        rolarParaFinal();
    }

    public void adicionarResposta(String texto) {
        Runnable adicionar = () -> {
            Label mensagem = new Label(texto);
            mensagem.setWrapText(true);
            mensagem.setMaxWidth(500);
            mensagem.getStyleClass().add("assistant-message");

            Label icone = new Label("●");
            icone.getStyleClass().add("assistant-icon");

            VBox conteudo = new VBox(8, mensagem, criarHorario());

            HBox linha = new HBox(14, icone, conteudo);
            linha.setAlignment(Pos.CENTER_LEFT);
            linha.getStyleClass().add("message-row");

            messagesBox.getChildren().add(linha);
            rolarParaFinal();
        };

        if (Platform.isFxApplicationThread()) {
            adicionar.run();
        } else {
            Platform.runLater(adicionar);
        }
    }

    // ---------------------------------------------------------------------
    // Estado de carregamento ("digitando...") e utilitários
    // ---------------------------------------------------------------------

    private void setCarregando(boolean carregando) {
        messageField.setDisable(carregando);
        plannerCombo.setDisable(carregando);

        if (carregando) {
            Label mensagem = new Label("SophIA está digitando…");
            mensagem.getStyleClass().add("assistant-message");

            Label icone = new Label("●");
            icone.getStyleClass().add("assistant-icon");

            typingRow = new HBox(14, icone, new VBox(8, mensagem));
            typingRow.setAlignment(Pos.CENTER_LEFT);
            typingRow.getStyleClass().add("message-row");

            messagesBox.getChildren().add(typingRow);
            rolarParaFinal();
        } else {
            if (typingRow != null) {
                messagesBox.getChildren().remove(typingRow);
                typingRow = null;
            }
            messageField.requestFocus();
        }
    }

    private Label criarHorario() {
        Label horario = new Label(LocalTime.now().format(formatter));
        horario.getStyleClass().add("message-time");
        return horario;
    }

    private void rolarParaFinal() {
        Platform.runLater(() -> chatScroll.setVvalue(1.0));
    }
}
