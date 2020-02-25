package milkman.ui.main.dialogs;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.fxml.FXML;
import javafx.scene.control.Dialog;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import milkman.ui.main.Toaster;
import milkman.ui.plugin.Exporter;
import milkman.ui.plugin.Templater;
import milkman.utils.fxml.FxmlBuilder;
import milkman.utils.fxml.FxmlUtil;

import java.util.List;

import static milkman.utils.fxml.FxmlBuilder.*;

public class ExportDialog<T> {
	@FXML VBox exportArea;
	@FXML JFXComboBox<Exporter<T>> exportSelector;
	@FXML JFXButton exportBtn;
	
	
	private Dialog dialog;

	private Exporter<T> selectedExporter = null;
	private Toaster toaster;
	private T objToExport;
	private Templater templater;
	
	public void showAndWait(List<Exporter<T>> exporters, Templater templater, Toaster toaster, T objToExport) {
		this.templater = templater;
		this.toaster = toaster;
		this.objToExport = objToExport;
		JFXDialogLayout content = new ExportDialogFxml(this);
		exportSelector.setPromptText("Select Exporter");
		exporters.forEach(exportSelector.getItems()::add);
		exportSelector.setConverter(new StringConverter<Exporter<T>>() {
			
			@Override
			public String toString(Exporter<T> object) {
				return object.getName();
			}
			
			@Override
			public Exporter<T> fromString(String string) {
				return null;
			}
		});
		exportSelector.getSelectionModel().selectedItemProperty().addListener((obs, o, v) -> {
			if (o != v && v != null) {
				selectedExporter = v;
				exportArea.getChildren().clear();
				exportArea.getChildren().add(v.getRoot(objToExport, templater));
				exportBtn.setDisable(v.isAdhocExporter());
			} 
		});
		dialog = FxmlUtil.createDialog(content);
		dialog.showAndWait();
	}
	
	
	@FXML public void onClose() {
		dialog.close();
	}
	
	@FXML public void onExport() {
		if (selectedExporter != null) {
			if (selectedExporter.doExport(objToExport, templater, toaster)) {
				dialog.close();
			}
		}
	}

	public class ExportDialogFxml extends JFXDialogLayout {
		public ExportDialogFxml(ExportDialog controller){
			setHeading(label("Export"));

			var vbox = new FxmlBuilder.VboxExt();
			controller.exportSelector = vbox.add(new JFXComboBox());
			controller.exportArea = vbox.add(new VBox());
			setBody(vbox);

			setActions(submit(controller::onExport, "Export"),
					cancel(controller::onClose, "Close"));

		}
	}
}
