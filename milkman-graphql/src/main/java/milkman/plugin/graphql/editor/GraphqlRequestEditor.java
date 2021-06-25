package milkman.plugin.graphql.editor;

import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import lombok.SneakyThrows;
import milkman.domain.RequestContainer;
import milkman.plugin.graphql.domain.GraphqlRequestContainer;
import milkman.ui.components.AutoCompleter;
import milkman.ui.plugin.AutoCompletionAware;
import milkman.ui.plugin.RequestTypeEditor;
import milkman.utils.fxml.GenericBinding;
import milkman.utils.fxml.facade.FxmlBuilder;
import milkman.utils.fxml.facade.FxmlBuilder.HboxExt;

public class GraphqlRequestEditor implements RequestTypeEditor, AutoCompletionAware {

	 TextField requestUrl;
	
	private final GenericBinding<GraphqlRequestContainer, String> urlBinding = GenericBinding.of(GraphqlRequestContainer::getUrl, GraphqlRequestContainer::setUrl);
	private AutoCompleter completer;
	
	
	@Override
	@SneakyThrows
	public Node getRoot() {
		Node root = new GraphqlRequestEditorFxml(this);
		return root;
	}

	@Override
	public void displayRequest(RequestContainer request) {
		if (!(request instanceof GraphqlRequestContainer))
			throw new IllegalArgumentException("Other request types not yet supported");
		
		GraphqlRequestContainer restRequest = (GraphqlRequestContainer)request;
		
		urlBinding.bindTo(requestUrl.textProperty(), restRequest);
		urlBinding.addListener(s -> request.setDirty(true));
		completer.attachVariableCompletionTo(requestUrl);
	}


	@Override
	public void setAutoCompleter(AutoCompleter completer) {
		this.completer = completer;
		
	}

	
	public static class GraphqlRequestEditorFxml extends HboxExt {
		private final GraphqlRequestEditor controller; //avoid gc collection

		public GraphqlRequestEditorFxml(GraphqlRequestEditor controller) {
			this.controller = controller;
			HBox.setHgrow(this, Priority.ALWAYS);
			controller.requestUrl = add(FxmlBuilder.text(), true);
			controller.requestUrl.setId("requestUrl");
		}
	}
	
}
