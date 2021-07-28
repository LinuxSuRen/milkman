/*
 * Copyright (C) 2021 Parisi Alessandro
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX).
 *
 * MaterialFX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.materialfx.controls.legacy;

import io.github.palexdev.materialfx.MFXResourcesLoader;
import io.github.palexdev.materialfx.beans.MFXSnapshotWrapper;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.cell.MFXListCell;
import io.github.palexdev.materialfx.controls.enums.DialogType;
import io.github.palexdev.materialfx.skins.legacy.MFXLegacyComboBoxSkin;
import io.github.palexdev.materialfx.utils.NodeUtils;
import io.github.palexdev.materialfx.validation.MFXDialogValidator;
import io.github.palexdev.materialfx.validation.base.AbstractMFXValidator;
import io.github.palexdev.materialfx.validation.base.Validated;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.css.*;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Labeled;
import javafx.scene.control.ListCell;
import javafx.scene.control.Skin;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.StrokeLineCap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
/**
 * monkey-patched to use converter instead of toString method
 */

/**
 * This is a restyle of the JavaFX's combo box.
 * <p>
 * For a combo box which more closely follows the guidelines of material design see {@link MFXComboBox}.
 * <p>
 * Extends {@code ComboBox}, redefines the style class to "mfx-legacy-combo-box" for usage in CSS and
 * includes a {@link MFXDialogValidator}. Also, introduces a new PseudoClass ":invalid" to specify
 * the control's look when the validation fails.
 * <p></p>
 * A few notes on features and usage:
 * <p>
 * If you check {@link ComboBox} documentation you will see a big warning about using nodes as content
 * because the scenegraph only allows for Nodes to be in one place at a time.
 * I found a workaround to this issue using {@link #snapshot(SnapshotParameters, WritableImage)}.
 * Basically I make a "screenshot" of the graphic and then I use an {@code ImageView} to show it.
 * <p>
 * So let's say you have a combo box of labels with icons as graphic, when you select an item, it won't disappear anymore
 * from the list because what you are seeing it's not the real graphic but a screenshot of it.
 * <p>
 * I recommend to use only nodes which are instances of {@code Labeled} since the {@code toString()} method is overridden
 * to return the control's text.
 *
 * @see MFXSnapshotWrapper
 */
public class MFXLegacyComboBox<T> extends ComboBox<T> implements Validated<MFXDialogValidator> {
    //================================================================================
    // Properties
    //================================================================================
    private static final StyleablePropertyFactory<MFXLegacyComboBox<?>> FACTORY = new StyleablePropertyFactory<>(ComboBox.getClassCssMetaData());
    private final String STYLE_CLASS = "mfx-legacy-combo-box";
    private final String STYLESHEET = MFXResourcesLoader.load("css/legacy/MFXComboBox.css");

    private MFXDialogValidator validator;
    private final ObjectProperty<Paint> invalidLineColor = new SimpleObjectProperty<>(Color.web("#EF6E6B"));
    protected static final PseudoClass INVALID_PSEUDO_CLASS = PseudoClass.getPseudoClass("invalid");

    //================================================================================
    // Constructors
    //================================================================================
    public MFXLegacyComboBox() {
        initialize();
    }

    public MFXLegacyComboBox(ObservableList<T> observableList) {
        super(observableList);
        initialize();
    }

    //================================================================================
    // Validation
    //================================================================================

    /**
     * Configures the validator. The first time the error label can appear in two cases:
     * <p></p>
     * 1) The validator {@link AbstractMFXValidator#isInitControlValidation()} flag is true,
     * in this case as soon as the control is laid out in the scene the label visible property is
     * set accordingly to the validator state. (by default is false) <p>
     * 2) When the control lose the focus and the the validator's state is invalid.
     * <p></p>
     * Then the label visible property is automatically updated when the validator state changes.
     * <p></p>
     * The validator is also responsible for updating the ":invalid" PseudoClass.
     */
    private void setupValidator() {
        validator = new MFXDialogValidator("Error");
        validator.setDialogType(DialogType.ERROR);
        validator.validProperty().addListener(invalidated -> {
            if (isValidated()) {
                pseudoClassStateChanged(INVALID_PSEUDO_CLASS, !isValid());
            }
        });

        NodeUtils.waitForScene(this, () -> {
            if (isValidated()) {
                if (getValidator().isInitControlValidation()) {
                    pseudoClassStateChanged(INVALID_PSEUDO_CLASS, !isValid());
                } else {
                    pseudoClassStateChanged(INVALID_PSEUDO_CLASS, false);
                }
            }
        }, true, false);
    }

    @Override
    public MFXLegacyComboBox<T> installValidator(Supplier<MFXDialogValidator> validatorSupplier) {
        if (validatorSupplier == null) {
            throw new IllegalArgumentException("The supplier cannot be null!");
        }
        this.validator = validatorSupplier.get();
        return this;
    }

    @Override
    public MFXDialogValidator getValidator() {
        return validator;
    }

    /**
     * Delegate method to get the validator's title.
     */
    public String getValidatorTitle() {
        return validator.getTitle();
    }

    /**
     * Delegate method to get the validator's title property.
     */
    public StringProperty validatorTitleProperty() {
        return validator.titleProperty();
    }

    /**
     * Delegate method to set the validator's title.
     */
    public void setValidatorTitle(String title) {
        validator.setTitle(title);
    }

    public Paint getInvalidLineColor() {
        return invalidLineColor.get();
    }

    /**
     * Specifies the color of the focused line when the validator state is invalid.
     * <p></p>
     * This workaround is needed because I discovered a rather surprising/shocking bug.
     * If you set the line color in SceneBuilder (didn't test in Java code) and the validator state is invalid,
     * the line won't change color as specified in the CSS file, damn you JavaFX :)
     */
    public ObjectProperty<Paint> invalidLineColorProperty() {
        return invalidLineColor;
    }

    public void setInvalidLineColor(Paint invalidLineColor) {
        this.invalidLineColor.set(invalidLineColor);
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        getStyleClass().add(STYLE_CLASS);
        setCellFactory(listCell -> new MFXListCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);

                getChildren().remove(lookup(".mfx-ripple-generator"));
            }
        });

        setButtonCell(new ListCell<>() {
            {
                valueProperty().addListener(observable -> {
                    if (getValue() == null) {
                        updateItem(null, true);
                    }
                });
            }

            @Override
            protected void updateItem(T item, boolean empty) {
                updateComboItem(this, item, empty);
            }
        });

        setupValidator();
    }

    /**
     * Defines the behavior of the button cell.
     * <p>
     * If it's empty or the item is null, shows the prompt text.
     * <p>
     * If the item is instanceof {@code Labeled} makes a "screenshot" of the graphic if not null,
     * and gets item's text. Otherwise calls {@code toString()} on the item.
     */
    protected void updateComboItem(ListCell<T> cell, T item, boolean empty) {

        if (empty || item == null) {
            cell.setGraphic(null);
            cell.setText(getPromptText());
            return;
        }

        if (item instanceof Labeled) {
            Labeled nodeItem = (Labeled) item;
            if (nodeItem.getGraphic() != null) {
                cell.setGraphic(new MFXSnapshotWrapper(nodeItem.getGraphic()).getGraphic());
            }
            cell.setText(nodeItem.getText());
        } else {

            cell.setText(getConverter().toString(item));
        }
    }

    //================================================================================
    // Styleable Properties
    //================================================================================
    private final StyleableObjectProperty<Paint> lineColor = new SimpleStyleableObjectProperty<>(
            StyleableProperties.LINE_COLOR,
            this,
            "lineColor",
            Color.rgb(50, 120, 220)
    );

    private final StyleableObjectProperty<Paint> unfocusedLineColor = new SimpleStyleableObjectProperty<>(
            StyleableProperties.UNFOCUSED_LINE_COLOR,
            this,
            "unfocusedLineColor",
            Color.rgb(77, 77, 77)
    );

    private final StyleableDoubleProperty lineStrokeWidth = new SimpleStyleableDoubleProperty(
            StyleableProperties.LINE_STROKE_WIDTH,
            this,
            "lineStrokeWidth",
            2.0
    );

    private final StyleableObjectProperty<StrokeLineCap> lineStrokeCap = new SimpleStyleableObjectProperty<>(
            StyleableProperties.LINE_STROKE_CAP,
            this,
            "lineStrokeCap",
            StrokeLineCap.ROUND
    );

    private final StyleableBooleanProperty animateLines = new SimpleStyleableBooleanProperty(
            StyleableProperties.ANIMATE_LINES,
            this,
            "animateLines",
            true
    );

    private final StyleableBooleanProperty isValidated = new SimpleStyleableBooleanProperty(
            StyleableProperties.IS_VALIDATED,
            this,
            "isValidated",
            false
    );

    public Paint getLineColor() {
        return lineColor.get();
    }

    /**
     * Specifies the line's color when the control is focused.
     */
    public StyleableObjectProperty<Paint> lineColorProperty() {
        return lineColor;
    }

    public void setLineColor(Paint lineColor) {
        this.lineColor.set(lineColor);
    }

    public Paint getUnfocusedLineColor() {
        return unfocusedLineColor.get();
    }

    /**
     * Specifies the line's color when the control is not focused.
     */
    public StyleableObjectProperty<Paint> unfocusedLineColorProperty() {
        return unfocusedLineColor;
    }

    public void setUnfocusedLineColor(Paint unfocusedLineColor) {
        this.unfocusedLineColor.set(unfocusedLineColor);
    }

    public double getLineStrokeWidth() {
        return lineStrokeWidth.get();
    }

    /**
     * Specifies the lines' stroke width.
     */
    public StyleableDoubleProperty lineStrokeWidthProperty() {
        return lineStrokeWidth;
    }

    public void setLineStrokeWidth(double lineStrokeWidth) {
        this.lineStrokeWidth.set(lineStrokeWidth);
    }

    public StrokeLineCap getLineStrokeCap() {
        return lineStrokeCap.get();
    }

    /**
     * Specifies the lines' stroke cap.
     */
    public StyleableObjectProperty<StrokeLineCap> lineStrokeCapProperty() {
        return lineStrokeCap;
    }

    public void setLineStrokeCap(StrokeLineCap lineStrokeCap) {
        this.lineStrokeCap.set(lineStrokeCap);
    }

    public boolean isAnimateLines() {
        return animateLines.get();
    }

    /**
     * Specifies if the lines switch between focus/un-focus should be animated.
     */
    public StyleableBooleanProperty animateLinesProperty() {
        return animateLines;
    }

    public void setAnimateLines(boolean animateLines) {
        this.animateLines.set(animateLines);
    }

    public boolean isValidated() {
        return isValidated.get();
    }

    /**
     * Specifies if validation is required for the control.
     */
    public StyleableBooleanProperty isValidatedProperty() {
        return isValidated;
    }

    public void setValidated(boolean isValidated) {
        this.isValidated.set(isValidated);
    }

    //================================================================================
    // CssMetaData
    //================================================================================
    private static class StyleableProperties {
        private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

        private static final CssMetaData<MFXLegacyComboBox<?>, Paint> LINE_COLOR =
                FACTORY.createPaintCssMetaData(
                        "-mfx-line-color",
                        MFXLegacyComboBox::lineColorProperty,
                        Color.rgb(50, 120, 220)
                );

        private static final CssMetaData<MFXLegacyComboBox<?>, Paint> UNFOCUSED_LINE_COLOR =
                FACTORY.createPaintCssMetaData(
                        "-mfx-unfocused-line-color",
                        MFXLegacyComboBox::unfocusedLineColorProperty,
                        Color.rgb(77, 77, 77)
                );

        private static final CssMetaData<MFXLegacyComboBox<?>, Number> LINE_STROKE_WIDTH =
                FACTORY.createSizeCssMetaData(
                        "-mfx-line-stroke-width",
                        MFXLegacyComboBox::lineStrokeWidthProperty,
                        2.0
                );

        private static final CssMetaData<MFXLegacyComboBox<?>, StrokeLineCap> LINE_STROKE_CAP =
                FACTORY.createEnumCssMetaData(
                        StrokeLineCap.class,
                        "-mfx-line-stroke-cap",
                        MFXLegacyComboBox::lineStrokeCapProperty,
                        StrokeLineCap.ROUND
                );

        private static final CssMetaData<MFXLegacyComboBox<?>, Boolean> ANIMATE_LINES =
                FACTORY.createBooleanCssMetaData(
                        "-mfx-animate-lines",
                        MFXLegacyComboBox::animateLinesProperty,
                        true
                );

        private static final CssMetaData<MFXLegacyComboBox<?>, Boolean> IS_VALIDATED =
                FACTORY.createBooleanCssMetaData(
                        "-mfx-validate",
                        MFXLegacyComboBox::isValidatedProperty,
                        false
                );

        static {
            List<CssMetaData<? extends Styleable, ?>> lcbCssMetaData = new ArrayList<>(ComboBox.getClassCssMetaData());
            Collections.addAll(lcbCssMetaData, ANIMATE_LINES, LINE_COLOR, UNFOCUSED_LINE_COLOR, LINE_STROKE_WIDTH, LINE_STROKE_CAP, IS_VALIDATED);
            cssMetaDataList = Collections.unmodifiableList(lcbCssMetaData);
        }

    }

    public static List<CssMetaData<? extends Styleable, ?>> getControlCssMetaDataList() {
        return StyleableProperties.cssMetaDataList;
    }

    //================================================================================
    // Override Methods
    //================================================================================
    @Override
    protected Skin<?> createDefaultSkin() {
        return new MFXLegacyComboBoxSkin<>(this);
    }

    @Override
    public String getUserAgentStylesheet() {
        return STYLESHEET;
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return getControlCssMetaDataList();
    }
}
