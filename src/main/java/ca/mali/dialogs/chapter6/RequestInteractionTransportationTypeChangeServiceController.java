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

import ca.mali.fomparser.InteractionClassFDD;
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
public class RequestInteractionTransportationTypeChangeServiceController implements Initializable {

    //Logger
    private static final Logger logger = LogManager.getLogger();

    @FXML
    private ComboBox<InteractionClassFDD> InteractionClassName;

    @FXML
    private ComboBox<TransportationFDD> TransportationType;

    @FXML
    private Button OkButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger.entry();
        if (fddObjectModel != null) {
            InteractionClassName.getItems().addAll(fddObjectModel.getInteractionClasses().values());
            InteractionClassName.setValue(InteractionClassName.getItems().get(0));
            TransportationType.getItems().addAll(fddObjectModel.getTransportation().values());
            TransportationType.setValue(TransportationType.getItems().get(0));
            OkButton.setDisable(false);
        }
        logger.exit();
    }

    @FXML
    private void Cancel_click(ActionEvent event) {
        logger.entry();
        ((Stage) InteractionClassName.getScene().getWindow()).close();
        logger.exit();
    }

    @FXML
    private void OK_click(ActionEvent event) {
        logger.entry();
        LogEntry log = new LogEntry("6.27", "Request Interaction Transportation Type Change service");
        try {
            log.getSuppliedArguments().add(new ClassValuePair("Interaction<Handle>",
                    InteractionClassHandle.class, InteractionClassName.getValue().toString()
                    + '<' + InteractionClassName.getValue().getHandle().toString() + '>'));
            log.getSuppliedArguments().add(new ClassValuePair("Transportation<Handle>",
                    TransportationTypeHandle.class, TransportationType.getValue().toString()
                    + '<' + TransportationType.getValue().getHandle().toString() + '>'));
            rtiAmb.requestInteractionTransportationTypeChange(InteractionClassName.getValue().getHandle(), TransportationType.getValue().getHandle());
            log.setDescription("Interaction Transportation change requested successfully");
            log.setLogType(LogEntryType.REQUEST);
        } catch (InteractionClassAlreadyBeingChanged | InteractionClassNotPublished |
                InteractionClassNotDefined | InvalidTransportationType | SaveInProgress |
                RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.ERROR);
            logger.log(Level.ERROR, ex.getMessage(), ex);
        } catch (Exception ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.FATAL);
            logger.log(Level.FATAL, ex.getMessage(), ex);
        }
        logEntries.add(log);
        ((Stage) InteractionClassName.getScene().getWindow()).close();
        logger.exit();
    }
}
