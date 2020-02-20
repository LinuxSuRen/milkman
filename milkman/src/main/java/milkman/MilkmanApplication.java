package milkman;



import java.io.IOException;
import java.lang.management.ManagementFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import milkman.logback.LogbackConfiguration;
import milkman.ui.main.options.CoreApplicationOptionsProvider;

@Slf4j
public class MilkmanApplication extends Application {
	
	private MainModule module;

	private static long startInitializationTime;
	private boolean hasError = false;
	
	@Override
	public void init() throws Exception {
		super.init();
		module = new MainModule();
		try {
			module.start();
		} catch (Throwable t) {
			hasError = true;
			Platform.runLater(() -> showFatalError(t));
		}
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		if (!hasError) {
			try {
				module.getMainWindow().start(primaryStage); // 1 sec
				module.getApplicationController().initOptions();
				module.getThemeSwitcher().setTheme(CoreApplicationOptionsProvider.options().getTheme());
				module.getApplicationController().initApplication(); // 1 sec
				module.getAppCdsGenerator().initializeCds(false);
				log.info("App Initialization time: {} ms", System.currentTimeMillis() - startInitializationTime);

				
				primaryStage.setOnCloseRequest(e -> {
					module.getApplicationController().persistState();
					module.getApplicationController().closeApplication();
				});
			} catch (Throwable t) {
				hasError = true;
				showFatalError(t);
			}
		}
	}

	
	
	public static void main(String[] args) throws IOException, InterruptedException {
		log.info("VM Startup time: {} ms", ManagementFactory.getRuntimeMXBean().getUptime());
		startInitializationTime = System.currentTimeMillis();
		com.sun.javafx.application.LauncherImpl.launchApplication(
				MilkmanApplication.class, MilkmanPreloader.class, args);
	}

	@SneakyThrows
	private static void showFatalError(Throwable t) {
		log.error("Failed to start application", t);
		Alert alert = new ExceptionDialog(t);
		alert.showAndWait();
		Platform.exit();
	}

}
