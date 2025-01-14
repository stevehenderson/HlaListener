/*
 * Copyright (c) 2015, Mostafa Ali
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package ca.mali.dialogs.chapter10;

import static ca.mali.hlalistener.PublicVariables.*;
import ca.mali.hlalistener.*;
import hla.rti1516e.*;
import hla.rti1516e.exceptions.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.*;
import javafx.beans.binding.Bindings;
import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.stage.*;
import org.apache.logging.log4j.*;

/**
 * FXML Controller class
 *
 * @author Mostafa
 */
public class GetAttributeNameServiceController implements Initializable {

    //Logger
    private static final Logger logger = LogManager.getLogger();

    @FXML
    private TextField ObjectClassHandleTextBox;

    @FXML
    private TextField AttributeHandleTextBox;

    @FXML
    private Button OkButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger.entry();
        OkButton.disableProperty().bind(Bindings.isEmpty(ObjectClassHandleTextBox.textProperty())
                .or(Bindings.isEmpty(AttributeHandleTextBox.textProperty())));
        logger.exit();
    }

    @FXML
    private void Cancel_click(ActionEvent event) {
        logger.entry();
        ((Stage) OkButton.getScene().getWindow()).close();
        logger.exit();
    }

    @FXML
    private void OK_click(ActionEvent event) {
        logger.entry();
        LogEntry log = new LogEntry("10.12", "Get Attribute Name service");
        try {
            log.getSuppliedArguments().add(new ClassValuePair("Class handle",
                    ObjectClassHandle.class,ObjectClassHandleTextBox.getText()));
            ObjectClassHandle objectHandle = rtiAmb.getObjectClassHandleFactory()
                    .decode(ByteBuffer.allocate(4).putInt(Integer.parseInt(ObjectClassHandleTextBox.getText())).array(), 0);
            log.getSuppliedArguments().add(new ClassValuePair("Attribute handle", AttributeHandle.class, AttributeHandleTextBox.getText()));
            AttributeHandle attributeHandle = rtiAmb.getAttributeHandleFactory()
                    .decode(ByteBuffer.allocate(4).putInt(Integer.parseInt(AttributeHandleTextBox.getText())).array(), 0);
            String attributeName = rtiAmb.getAttributeName(objectHandle, attributeHandle);
            log.getReturnedArguments().add(new ClassValuePair("Attribute name", String.class, attributeName));
            log.setDescription("Attribute name retrieved successfully");
            log.setLogType(LogEntryType.REQUEST);
        } catch (AttributeNotDefined | InvalidAttributeHandle | InvalidObjectClassHandle |
                FederateNotExecutionMember | NotConnected | RTIinternalError ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.ERROR);
            logger.log(Level.ERROR, ex.getMessage(), ex);
        } catch (Exception ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.FATAL);
            logger.log(Level.FATAL, ex.getMessage(), ex);
        }
        logEntries.add(log);
        ((Stage) OkButton.getScene().getWindow()).close();
        logger.exit();
    }
}
