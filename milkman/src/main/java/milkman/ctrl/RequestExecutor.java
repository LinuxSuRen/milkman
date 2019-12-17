package milkman.ctrl;

import java.util.Optional;

import org.apache.commons.lang3.exception.ExceptionUtils;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import milkman.domain.RequestContainer;
import milkman.domain.ResponseContainer;
import milkman.ui.plugin.CustomCommand;
import milkman.ui.plugin.RequestTypePlugin;
import milkman.ui.plugin.Templater;
import milkman.utils.AsyncResponseControl;

@RequiredArgsConstructor
@Slf4j
public class RequestExecutor extends Service<AsyncResponseControl> {

	private final RequestContainer request; 
	private final RequestTypePlugin plugin;
	private final Templater templater;
	private final Optional<CustomCommand> customCommand;
	
	@Override
	protected Task<AsyncResponseControl> createTask() {
		return new Task<AsyncResponseControl>() {
			
			@Override
			protected AsyncResponseControl call() {
				try {
					var asyncCtrl = new AsyncResponseControl();
					if (customCommand.isPresent()) {
						String commandId = customCommand.get().getCommandId();
						log.info("Execute custom command: " + commandId);
						var response =  plugin.executeCustomCommandAsync(commandId, request, templater, asyncCtrl.getCancellationControl());
						asyncCtrl.setResponse(response);
						return asyncCtrl;
					} else {
						log.info("Execute request");
						var response = plugin.executeRequestAsync(request, templater, asyncCtrl.getCancellationControl());
						asyncCtrl.setResponse(response);
						return asyncCtrl;
					}
				} catch (Throwable e) {
					log.error("Execution of request failed", e);
					String rootCauseMessage = ExceptionUtils.getRootCauseMessage(e);
					updateMessage(rootCauseMessage);
					throw e;
				}
			}
		};
	}

}
