package milkmancli;

import java.io.IOException;
import java.util.List;

import milkman.ctrl.RequestTypeManager;
import milkman.logback.LogbackConfiguration;
import milkman.persistence.PersistenceManager;
import milkman.ui.plugin.OptionPageProvider;
import milkman.ui.plugin.OptionsObject;
import milkman.ui.plugin.UiPluginManager;
import wrm.hardwire.Module;

@Module
public class MilkmanCli extends MilkmanCliBase {

	public static void main(String[] args) throws IOException {
		LogbackConfiguration.setMuteConsole(true);
		MilkmanCli cli = new MilkmanCli();
		cli.start();
		if (args.length > 1) {
			cli.getTerminalUi().executeCommand(args);
		} else {
			cli.getTerminalUi().runCommandLoop();
		}
	}

	@Override
	protected PersistenceManager createPersistenceManager() {
		return new PersistenceManager();
	}
	
	@Override
	protected void onPostConstruct() {
		getPersistenceManager().init();
	}

	@Override
	protected RequestTypeManager createRequestTypeManager() {
		return new RequestTypeManager(getUiPluginManager());
	}

	@Override
	protected UiPluginManager createUiPluginManager() {
		return new UiPluginManager(null);
	}
	
	@Override
	protected void onInitialized() {
		initOptions();
	}
	
	public void initOptions() {
		List<OptionPageProvider> optionProviders = getUiPluginManager().loadOptionPages();
		this.getPersistenceManager().loadOptions().forEach(e -> {
			optionProviders.stream().filter(p -> p.getClass().getName().equals(e.getOptionProviderClass()))
			.findAny().ifPresent(p -> {
				((OptionPageProvider<OptionsObject>)p).setOptions(e.getOptionsObject());
			});
		});

	}
}
