package milkman.plugin.scripting;

import javafx.scene.control.Tab;
import lombok.SneakyThrows;
import lombok.val;
import milkman.domain.RequestContainer;
import milkman.plugin.scripting.conenttype.JavascriptContentType;
import milkman.ui.components.ContentEditor;
import milkman.ui.plugin.RequestAspectEditor;

import java.util.Collections;

public class ScriptingAspectEditor implements RequestAspectEditor {

	@Override
	@SneakyThrows
	public Tab getRoot(RequestContainer request) {
		val script = request.getAspect(ScriptingAspect.class).get();
		ContentEditor root = new ContentEditor();
		root.setEditable(true);
		root.setContent(script::getPostRequestScript, script::setPostRequestScript);
		root.setContentTypePlugins(Collections.singletonList(new JavascriptContentType()));
		root.setContentType("application/javascript");
		root.setHeaderVisibility(false);
		
		return new Tab("Scripting", root);
	}

	@Override
	public boolean canHandleAspect(RequestContainer request) {
		return request.getAspect(ScriptingAspect.class).isPresent();
	}

}
