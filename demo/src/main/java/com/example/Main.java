package com.example;

import java.util.Scanner;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/** JavaFX application entry point for Tic-Tac-Toe. */
public class Main extends Application {
    private final Board board = new Board();
    private final HumanPlayer human = new HumanPlayer('X', new Scanner(""));
    private final CpuPlayer cpu = new CpuPlayer('O');
    private final Button[][] cells = new Button[3][3];
    private final Label status = new Label();
    private final Label score = new Label();
    private int humanWins;
    private int cpuWins;
    private int draws;

    /** Launches the JavaFX application. */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Label eyebrow = new Label("A CLASSIC GAME");
        eyebrow.getStyleClass().add("eyebrow");

        Label title = new Label("Tic-Tac-Toe");
        title.getStyleClass().add("title");

        status.getStyleClass().add("status");
        score.getStyleClass().add("score");

        VBox header = new VBox(8, eyebrow, title, status, score);
        header.setAlignment(Pos.CENTER);

        GridPane grid = new GridPane();
        grid.getStyleClass().add("board");
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setAlignment(Pos.CENTER);
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                int cellRow = row;
                int cellColumn = column;
                Button cell = new Button();
                cell.getStyleClass().add("cell");
                cell.setAccessibleText("Row " + (row + 1) + ", column " + (column + 1));
                cell.setOnAction(event -> playHumanMove(cellRow, cellColumn));
                cells[row][column] = cell;
                grid.add(cell, column, row);
            }
        }

        Button restart = new Button("New game");
        restart.getStyleClass().add("restart-button");
        restart.setOnAction(event -> resetGame());

        Label hint = new Label("Choose a square to make your move.");
        hint.getStyleClass().add("hint");

        VBox footer = new VBox(12, hint, restart);
        footer.setAlignment(Pos.CENTER);

        BorderPane content = new BorderPane();
        content.getStyleClass().add("content");
        content.setTop(header);
        content.setCenter(grid);
        content.setBottom(footer);
        BorderPane.setAlignment(header, Pos.CENTER);
        BorderPane.setAlignment(footer, Pos.CENTER);
        BorderPane.setMargin(header, new Insets(0, 0, 26, 0));
        BorderPane.setMargin(footer, new Insets(24, 0, 0, 0));

        StackPane root = new StackPane(content);
        root.getStyleClass().add("root");
        root.setPadding(new Insets(36));

        var imageResource = getClass().getResource("/congratulations.png");
        if (imageResource == null) {
            throw new IllegalStateException("Could not find congratulations.png on the classpath");
        }
        ImageView congratulationsImage = new ImageView(new Image(imageResource.toExternalForm()));
        congratulationsImage.setFitWidth(150);
        congratulationsImage.setFitHeight(150);
        congratulationsImage.setPreserveRatio(true);

        Label congratulationsText = new Label("Congratulations!!");
        congratulationsText.getStyleClass().add("congratulations-title");
        Label winnerText = new Label();
        winnerText.getStyleClass().add("congratulations-winner");
        Button backToBoard = new Button("Back to board");
        backToBoard.getStyleClass().add("restart-button");
        backToBoard.setOnAction(event -> resetGame());
        VBox congratulations = new VBox(
                16, congratulationsText, congratulationsImage, winnerText, backToBoard);
        congratulations.getStyleClass().add("congratulations");
        congratulations.setAlignment(Pos.CENTER);
        congratulations.setVisible(false);
        congratulations.setManaged(false);
        root.getChildren().add(congratulations);

        Scene scene = new Scene(root, 500, 650);
        var stylesheet = getClass().getResource("/app.css");
        if (stylesheet == null) {
            throw new IllegalStateException("Could not find app.css on the classpath");
        }
        scene.getStylesheets().add(stylesheet.toExternalForm());

        stage.setTitle("Tic-Tac-Toe");
        stage.setMinWidth(460);
        stage.setMinHeight(620);
        stage.setScene(scene);
        this.congratulations = congratulations;
        this.winnerText = winnerText;
        resetGame();
        stage.show();
    }

    private VBox congratulations;
    private Label winnerText;

    private void playHumanMove(int row, int column) {
        if (!human.makeMove(board, row, column)) {
            return;
        }
        updateCell(row, column);
        if (finishIfGameOver(human)) {
            return;
        }

        status.setText(cpu.getName() + "'s turn...");
        cpu.makeMove(board);
        updateBoard();
        if (!finishIfGameOver(cpu)) {
            status.setText(human.getName() + "'s turn (X)");
        }
    }

    private boolean finishIfGameOver(Player player) {
        if (board.checkWin()) {
            status.setText(player.getName() + " wins!");
            if (player == human) {
                humanWins++;
            } else {
                cpuWins++;
            }
            updateScore();
            disableEmptyCells();
            if (player == human) {
                winnerText.setText(player.getName() + " wins!");
                congratulations.setManaged(true);
                congratulations.setVisible(true);
            }
            return true;
        }
        if (board.isFull()) {
            status.setText("It's a draw!");
            draws++;
            updateScore();
            disableEmptyCells();
            return true;
        }
        return false;
    }

    private void updateBoard() {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                updateCell(row, column);
            }
        }
    }

    private void updateCell(int row, int column) {
        char marker = board.getCell(row, column);
        if (marker == ' ') {
            return;
        }
        Button cell = cells[row][column];
        if (cell.getText().isEmpty()) {
            cell.setText(String.valueOf(marker));
            cell.getStyleClass().add(marker == 'X' ? "x-cell" : "o-cell");
        }
        cell.setDisable(true);
    }

    private void disableEmptyCells() {
        for (Button[] row : cells) {
            for (Button cell : row) {
                cell.setDisable(true);
            }
        }
    }

    private void resetGame() {
        board.initialize();
        for (Button[] row : cells) {
            for (Button cell : row) {
                cell.setText("");
                cell.setDisable(false);
                cell.getStyleClass().removeAll("x-cell", "o-cell");
            }
        }
        status.setText(human.getName() + "'s turn (X)");
        updateScore();
        if (congratulations != null) {
            congratulations.setVisible(false);
            congratulations.setManaged(false);
            winnerText.setText("");
        }
    }

    private void updateScore() {
        score.setText("You: " + humanWins + "   CPU: " + cpuWins + "   Draws: " + draws);
    }
}
