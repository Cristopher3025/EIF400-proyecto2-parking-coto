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
 * Provides reusable JavaFX animations for user interface nodes.
 */
public final class UiAnimations {

    private static final Duration FADE_DURATION = Duration.millis(430);
    private static final Duration MOVE_DURATION = Duration.millis(430);

    private UiAnimations() {
    }

    /**
     * Fades in a node while moving it slightly upward.
     *
     * @param node node to animate; {@code null} is ignored
     */
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

    /**
     * Updates a label's text and fades the label into view.
     *
     * @param label label to update and animate; {@code null} is ignored
     * @param text text to display
     */
    public static void fadeText(Label label, String text) {
        if (label == null) {
            return;
        }

        label.setText(text == null ? "" : text);
        cinematicFadeIn(label);
    }

    /**
     * Installs a fade-in animation for rows added to a table.
     *
     * @param table table to configure; {@code null} is ignored
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

    /**
     * Fades a view into visibility.
     *
     * @param view view node to animate
     */
    public static void fadeView(Node view) {
        cinematicFadeIn(view);
    }
}
