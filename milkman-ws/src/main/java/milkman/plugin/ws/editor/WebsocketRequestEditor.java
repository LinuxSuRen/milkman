package milkman.plugin.ws.editor;

import com.jfoenix.controls.JFXTextField;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import lombok.SneakyThrows;
import milkman.domain.RequestContainer;
import milkman.plugin.ws.domain.WebsocketRequestContainer;
import milkman.ui.components.AutoCompleter;
import milkman.ui.plugin.AutoCompletionAware;
import milkman.ui.plugin.RequestTypeEditor;
import milkman.utils.fxml.FxmlBuilder.HboxExt;
import milkman.utils.fxml.GenericBinding;

public class WebsocketRequestEditor implements RequestTypeEditor, AutoCompletionAware {

	 TextField requestUrl;
	
	private GenericBinding<WebsocketRequestContainer, String> urlBinding = GenericBinding.of(WebsocketRequestContainer::getUrl, WebsocketRequestContainer::setUrl);
	private AutoCompleter completer;
	
	
	@Override
	@SneakyThrows
	public Node getRoot() {
		Node root = new WebsocketRequestEditorFxml(this);
		return root;
	}

	@Override
	public void displayRequest(RequestContainer request) {
		if (!(request instanceof WebsocketRequestContainer))
			throw new IllegalArgumentException("Other request types not yet supported");
		
		WebsocketRequestContainer restRequest = (WebsocketRequestContainer)request;
		
		urlBinding.bindTo(requestUrl.textProperty(), restRequest);
		urlBinding.addListener(s -> request.setDirty(true));
		completer.attachVariableCompletionTo(requestUrl);
	}


	@Override
	public void setAutoCompleter(AutoCompleter completer) {
		this.completer = completer;
		
	}

	
	public static class WebsocketRequestEditorFxml extends HboxExt {
		private WebsocketRequestEditor controller; //avoid gc collection

		public WebsocketRequestEditorFxml(WebsocketRequestEditor controller) {
			this.controller = controller;
			HBox.setHgrow(this, Priority.ALWAYS);
			controller.requestUrl = add(new JFXTextField(), true);
			controller.requestUrl.setId("requestUrl");
			controller.requestUrl.setPromptText("ws(s)://host:port/url");
		}
	}
	
}
