/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.jfoenix.validation.base;

import Jowil.FileConfigController;
import de.jensd.fx.glyphs.GlyphsBuilder;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.property.*;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Control;
import javafx.scene.control.Tooltip;

import java.util.function.Supplier;

/**
 * An abstract class that defines the basic validation functionality for a certain control.
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2016-03-09
 */
public abstract class ValidatorBase extends Parent {

    /**
     * this style class will be activated when a validation error occurs
     */
    public static final PseudoClass PSEUDO_CLASS_ERROR = PseudoClass.getPseudoClass("error");

    private Tooltip tooltip = null;
    protected Tooltip errorTooltip = null;
    public final static int ERROR=0,WARNING=1,SUCCESS=2;
    protected int messageType=ERROR;
    public static final PseudoClass PSEUDO_CLASS_SUCCESS = PseudoClass.getPseudoClass("success");
    protected String successMessage="";

    public ValidatorBase(String message) {
        this();
        this.setMessage(message);
    }

    public ValidatorBase() {
        parentProperty().addListener((o, oldVal, newVal) -> parentChanged());
        errorTooltip = new Tooltip();
        errorTooltip.getStyleClass().add("error-tooltip");
    }


    /***************************************************************************
     *                                                                         *
     * Methods                                                                 *
     *                                                                         *
     **************************************************************************/

    private void parentChanged() {
        updateSrcControl();
    }

    private void updateSrcControl() {
        Parent parent = getParent();
        if (parent != null) {
            Node control = parent.lookup(getSrc());
            srcControl.set(control);
        }
    }

    /**
     * will validate the source control
     */

    //joe add start
    public int getMessageType() {
        return messageType;
    }
    //joe add end

    public void validate() {
        eval();
        onEval();
    }

    /**
     * will evaluate the validation condition once calling validate method
     */
    protected abstract void eval();

    /**
     * this method will update the source control after evaluating the validation condition
     */
    protected void onEval() {
        Node control = getSrcControl();
        if (hasErrors.get()) {

            //joe edit start
            if(messageType==ERROR){
                control.pseudoClassStateChanged(PSEUDO_CLASS_SUCCESS, false);
                control.pseudoClassStateChanged(PSEUDO_CLASS_ERROR, true);
                //control.setStyle("-jfx-focus-color: #D34336;");
            }

            else if(messageType==SUCCESS) {
                control.pseudoClassStateChanged(PSEUDO_CLASS_ERROR, false);
//                control.pseudoClassStateChanged(PSEUDO_CLASS_SUCCESS, true);
                errorTooltip.setText(successMessage);
                ((Control) control).setTooltip(errorTooltip);

                //control.setStyle("-jfx-focus-color: #3CB371;");
                return;
            }
            else{
                control.pseudoClassStateChanged(PSEUDO_CLASS_SUCCESS, false);
                control.pseudoClassStateChanged(PSEUDO_CLASS_ERROR, false);
                //control.setStyle("-jfx-focus-color: #095c90");
            }

            //joe edit end


            if (control instanceof Control) {
                Tooltip controlTooltip = ((Control) control).getTooltip();
                if (controlTooltip != null && !controlTooltip.getStyleClass().contains("error-tooltip")) {
                    tooltip = ((Control) control).getTooltip();
                }
                errorTooltip.setText(getMessage());
                ((Control) control).setTooltip(errorTooltip);
            }
        } else {
            if (control instanceof Control) {
                Tooltip controlTooltip = ((Control) control).getTooltip();
                if ((controlTooltip != null && controlTooltip.getStyleClass().contains("error-tooltip"))
                    || (controlTooltip == null && tooltip != null)) {
                    ((Control) control).setTooltip(tooltip);
                }
                tooltip = null;
            }
            control.pseudoClassStateChanged(PSEUDO_CLASS_ERROR, false);
        }
    }

    /***************************************************************************
     *                                                                         *
     * Properties                                                              *
     *                                                                         *
     **************************************************************************/

    /***** srcControl *****/
    protected SimpleObjectProperty<Node> srcControl = new SimpleObjectProperty<>();

    public void setSrcControl(Node srcControl) {
        this.srcControl.set(srcControl);
    }

    public Node getSrcControl() {
        return this.srcControl.get();
    }

    public ObjectProperty<Node> srcControlProperty() {
        return this.srcControl;
    }


    /***** src *****/
    protected SimpleStringProperty src = new SimpleStringProperty() {
        @Override
        protected void invalidated() {
            updateSrcControl();
        }
    };

    public void setSrc(String src) {
        this.src.set(src);
    }

    public String getSrc() {
        return this.src.get();
    }

    public StringProperty srcProperty() {
        return this.src;
    }


    /***** hasErrors *****/
    protected ReadOnlyBooleanWrapper hasErrors = new ReadOnlyBooleanWrapper(false);

    public boolean getHasErrors() {
        return hasErrors.get();
    }

    public ReadOnlyBooleanProperty hasErrorsProperty() {
        return hasErrors.getReadOnlyProperty();
    }

    /***** Message *****/
    protected SimpleStringProperty message = new SimpleStringProperty() {
        @Override
        protected void invalidated() {
            updateSrcControl();
        }
    };

    public void setMessage(String msg) {
        this.message.set(msg);
    }

    public String getMessage() {
        return this.message.get();
    }

    public StringProperty messageProperty() {
        return this.message;
    }

    /***** Icon *****/
    protected SimpleObjectProperty<Supplier<Node>> iconSupplier = new SimpleObjectProperty<Supplier<Node>>() {
        @Override
        protected void invalidated() {
            updateSrcControl();
        }
    };

    public void setIconSupplier(Supplier<Node> icon) {
        this.iconSupplier.set(icon);
    }

    public SimpleObjectProperty<Supplier<Node>> iconSupplierProperty() {
        return this.iconSupplier;
    }

    public Supplier<Node> getIconSupplier() {
        return iconSupplier.get();
    }

    public void setIcon(Node icon) {
        iconSupplier.set(() -> icon);
    }

    //Joe edit start
    public Node getIcon() {

        if (iconSupplier.get() == null) {
            return null;
        }

        Node icon = iconSupplier.get().get();

        if(icon!=null) {
            switch (messageType) {
                case ERROR:
                    icon.getStyleClass().add("error-icon");
                    break;
                case WARNING:
                    icon.getStyleClass().add("warning-icon");
                    break;
                case SUCCESS:
                    icon.getStyleClass().add("success-icon");
                    break;

            }
        }

        return icon;
    }

    //Joe edit end
}
