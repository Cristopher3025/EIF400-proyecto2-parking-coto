package util;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.util.Duration;

/**
 * Animaciones visuales reutilizables para Parking Coto.
 * No modifica la lógica del dominio; únicamente anima nodos JavaFX.
 */
public final class UiAnimations {

    private static final Duration FADE_DURATION = Duration.millis(430);
    private static final Duration MOVE_DURATION = Duration.millis(430);

    private UiAnimations() {
    }

    /** Aparición suave estilo película: opacidad + pequeño desplazamiento vertical. */
    public static void cinematicFadeIn(Node node) {
        if (node == null) {
            return;
        }

        node.setOpacity(0.0);
        node.setTranslateY(8.0);

        FadeTransition fade = new FadeTransition(FADE_DURATION, node);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);

        TranslateTransition move = new TranslateTransition(MOVE_DURATION, node);
        move.setFromY(8.0);
        move.setToY(0.0);

        new ParallelTransition(fade, move).play();
    }

    /** Ideal para labels cuyo número/texto acaba de actualizarse. */
    public static void fadeText(Label label, String text) {
        if (label == null) {
            return;
        }

        label.setText(text == null ? "" : text);
        cinematicFadeIn(label);
    }

    /**
     * Hace que cada fila nueva de un TableView aparezca suavemente.
     * Se configura una sola vez en initialize().
     */
    public static <T> void installTableFade(TableView<T> table) {
        if (table == null) {
            return;
        }

        table.setRowFactory(tv -> {
            TableRow<T> row = new TableRow<>();

            row.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (newItem != null && oldItem == null) {
                    cinematicFadeIn(row);
                }
            });

            return row;
        });

        table.getItems().addListener((ListChangeListener<T>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    table.requestLayout();
                }
            }
        });
    }

    /** Aparición suave de una vista completa al navegar. */
    public static void fadeView(Node view) {
        cinematicFadeIn(view);
    }
}
