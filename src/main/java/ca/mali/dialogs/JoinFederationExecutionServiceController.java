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
package ca.mali.dialogs;

import ca.mali.customcontrol.*;
import static ca.mali.hlalistener.PublicVariables.*;
import hla.rti1516e.exceptions.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javafx.beans.binding.*;
import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.stage.*;
import org.apache.logging.log4j.*;

/**
 * FXML Controller class
 *
 * @author Mostafa Ali <engabdomostafa@gmail.com>
 */
public class JoinFederationExecutionServiceController implements Initializable {

    //Logger
    private static final Logger logger = LogManager.getLogger();

    @FXML
    private TextField FederateName;
    @FXML
    private TextField FederateType;
    @FXML
    private TextField FederationExecutionName;
    @FXML
    private FilesList FomModuleDesignators;
    @FXML
    private Button OkButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        OkButton.disableProperty().bind(
                Bindings.isEmpty(FederationExecutionName.textProperty())
                .or(Bindings.isEmpty(FederateType.textProperty())));
    }

    @FXML
    private void Cancel_click(ActionEvent event) {
        ((Stage) FederationExecutionName.getScene().getWindow()).close();
    }

    @FXML
    private void OK_click(ActionEvent event) {
        try {
            List<URL> foms = new ArrayList<>();
            for (File file : FomModuleDesignators.getFiles()) {
                foms.add(file.toURI().toURL());
            }
            if (FederateName.getText().isEmpty() && FomModuleDesignators.getFileNames().isEmpty()) {
                rtiAmb.joinFederationExecution(FederateType.getText(), FederationExecutionName.getText());
            } else if (FomModuleDesignators.getFileNames().isEmpty()) {
                rtiAmb.joinFederationExecution(FederateName.getText(), FederateType.getText(), FederationExecutionName.getText());
            } else if (FederateName.getText().isEmpty()) {
                rtiAmb.joinFederationExecution(FederateType.getText(), FederationExecutionName.getText(), foms.toArray(new URL[foms.size()]));
            } else {
                rtiAmb.joinFederationExecution(FederateName.getText(), FederateType.getText(), FederationExecutionName.getText(), foms.toArray(new URL[foms.size()]));
            }
            logicalTimeFactory = rtiAmb.getTimeFactory();
        } catch (CouldNotCreateLogicalTimeFactory ex) {
            logger.log(Level.ERROR, "Couldn't create logical time factory", ex);
        } catch (CallNotAllowedFromWithinCallback ex) {
            logger.log(Level.ERROR, "Call not allowed from within callback", ex);
        } catch (CouldNotOpenFDD ex) {
            logger.log(Level.ERROR, "Couldn't open FDD", ex);
        } catch (ErrorReadingFDD ex) {
            logger.log(Level.ERROR, "Error reading FDD", ex);
        } catch (InconsistentFDD ex) {
            logger.log(Level.ERROR, "Inconsistend FDD", ex);
        } catch (FederateNameAlreadyInUse ex) {
            logger.log(Level.ERROR, "Federate name is already in use", ex);
        } catch (FederateAlreadyExecutionMember ex) {
            logger.log(Level.ERROR, "Federate is already Execution Member", ex);
        } catch (FederationExecutionDoesNotExist ex) {
            logger.log(Level.ERROR, "Federation Exeuction doesn't Exist", ex);
        } catch (SaveInProgress ex) {
            logger.log(Level.ERROR, "Save is in Progress", ex);
        } catch (RestoreInProgress ex) {
            logger.log(Level.ERROR, "Restore is in Progress", ex);
        } catch (NotConnected ex) {
            logger.log(Level.ERROR, "Not connected to RTI", ex);
        } catch (RTIinternalError ex) {
            logger.log(Level.ERROR, "Internal error in RTI", ex);
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error in joining Federation Execution", ex);
        }
        ((Stage) FederationExecutionName.getScene().getWindow()).close();
    }
}