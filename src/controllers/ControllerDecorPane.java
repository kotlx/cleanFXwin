package controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import kotlx.ru.RuTWinFX.RuTWinFX;
import kotlx.ru.RuTWinFX.SlidePane;

import java.net.URL;
import java.util.ResourceBundle;

public class ControllerDecorPane implements Initializable {
	@FXML
	private Pane minimizePane;

	@FXML
	private Pane maximizePane;

	@FXML
	private Pane closeBtnPane;

	@FXML
	private Pane decorationPane;

	private final AnchorPane frame = RuTWinFX.getFrame();
	private double xOffset = 0;
	private double yOffset = 0;

	@Override
	public void initialize(URL location, ResourceBundle resources) {


		// Перемещение окна если нажать на декорационной панели
		decorationPane.addEventFilter(MouseEvent.ANY, event -> {
			if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
				xOffset = frame.getLayoutX() - event.getScreenX();
				yOffset = frame.getLayoutY() - event.getScreenY();
			}
			if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
				frame.setLayoutX(event.getScreenX() + xOffset);
				frame.setLayoutY(event.getScreenY() + yOffset);
			}
		});

		// Инициализируем Scene и Stage для контроллера в слушателях соответсвующих property
		// И регистрируем обработку событий использующих Scene, Stage и их узлы.
		decorationPane.sceneProperty().addListener((observable, oldValue, newValue) -> {
			final Scene scene = newValue;
			scene.windowProperty().addListener((observableVal, oldVal, window) -> {
				final Stage stage = (Stage) window;
				final SlidePane slidePane = (SlidePane) scene.lookup("SlidePane");

				// Закрываем приложение
				closeBtnPane.setOnMouseClicked(event -> {
					slidePane.setStop();
					Platform.exit();
				});

				// Свернуть окно
				minimizePane.setOnMouseClicked(event -> stage.setIconified(true));

				// ***Это костыль что бы после восстановления свернутого окна убиралась slidePane
				stage.iconifiedProperty().addListener((observable1, oldValue1, newValue1) -> {
					if (!newValue1) {
						slidePane.setSlideOut();
						try {
							Thread.sleep(1);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						slidePane.setSlideOff();
					}
				});

				// Развернуь / свернуть окно в максимум / к прежним размерам
				maximizePane.setOnMouseClicked(event -> {
					RuTWinFX.setFrameMaximized();
				});
			});
		});
	}
}
