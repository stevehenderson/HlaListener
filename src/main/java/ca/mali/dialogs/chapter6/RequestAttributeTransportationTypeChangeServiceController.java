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
package ca.mali.dialogs.chapter6;

import ca.mali.customcontrol.ObjectInstanceAttributesController;
import ca.mali.fomparser.ObjectInstanceFDD;
import ca.mali.fomparser.TransportationFDD;
import static ca.mali.hlalistener.PublicVariables.*;
import ca.mali.hlalistener.*;
import hla.rti1516e.*;
import hla.rti1516e.exceptions.*;
import java.net.*;
import java.util.*;
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
public class RequestAttributeTransportationTypeChangeServiceController implements Initializable {

    //Logger
    private static final Logger logger = LogManager.getLogger();

    @FXML
    private ComboBox<ObjectInstanceFDD> ObjectInstanceName;

    @FXML
    private ComboBox<TransportationFDD> TransportationType;

    @FXML
    private ObjectInstanceAttributesController ObjectInstancesAttributes;

    @FXML
    private Button OkButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger.entry();
        if (fddObjectModel != null) {
            if (!objectInstances.isEmpty() && !fddObjectModel.getTransportation().isEmpty()) {
                ObjectInstanceName.getItems().addAll(objectInstances.values());
                ObjectInstanceName.setValue(ObjectInstanceName.getItems().get(0));
                ObjectInstancesAttributes.setObjectInstance(ObjectInstanceName.getValue());
                TransportationType.getItems().addAll(fddObjectModel.getTransportation().values());
                TransportationType.setValue(TransportationType.getItems().get(0));
                OkButton.setDisable(false);
                ObjectInstanceName.valueProperty().addListener((observable, oldValue, newValue) -> {
                    ObjectInstancesAttributes.setObjectInstance(newValue);
                });
            }
        }
        logger.exit();
    }

    @FXML
    private void Cancel_click(ActionEvent event) {
        logger.entry();
        ((Stage) ObjectInstanceName.getScene().getWindow()).close();
        logger.exit();
    }

    @FXML
    private void OK_click(ActionEvent event) {
        logger.entry();
        LogEntry log = new LogEntry("6.23", "Request Attribute Transportation Type Change service");
        try {
            log.getSuppliedArguments().add(new ClassValuePair("Object Instance<Handle>",
                    ObjectInstanceHandle.class, ObjectInstanceName.getValue().toString()
                    + '<' + ObjectInstanceName.getValue().getHandle().toString() + '>'));
            AttributeHandleSet set = rtiAmb.getAttributeHandleSetFactory().create();
            ObjectInstancesAttributes.getAttributes().forEach((item) -> {
                set.add(item.getHandle());
                log.getSuppliedArguments().add(new ClassValuePair("Attribute<handle>",
                        AttributeHandle.class, item.getName() + '<' + item.getHandle().toString() + '>'));
            });
            log.getSuppliedArguments().add(new ClassValuePair("Transportation<Handle>",
                    TransportationTypeHandle.class, TransportationType.getValue().toString()
                    + '<' + TransportationType.getValue().getHandle().toString() + '>'));
            rtiAmb.requestAttributeTransportationTypeChange(ObjectInstanceName.getValue().getHandle(), set, TransportationType.getValue().getHandle());
            log.setDescription("Attribute Transportation change requested successfully");
            log.setLogType(LogEntryType.REQUEST);
        } catch (FederateNotExecutionMember | NotConnected | AttributeAlreadyBeingChanged |
                AttributeNotOwned | AttributeNotDefined | ObjectInstanceNotKnown |
                InvalidTransportationType | SaveInProgress | RestoreInProgress |
                RTIinternalError ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.ERROR);
            logger.log(Level.ERROR, ex.getMessage(), ex);
        } catch (Exception ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.FATAL);
            logger.log(Level.FATAL, ex.getMessage(), ex);
        }
        logEntries.add(log);
        ((Stage) ObjectInstanceName.getScene().getWindow()).close();
        logger.exit();
    }
}
