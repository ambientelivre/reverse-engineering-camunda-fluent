package br.com.ambientelivre.simplebpmn.service;

import java.util.logging.Logger;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class ArchiveSimpleBPMNService implements JavaDelegate {

  private final Logger LOGGER = Logger.getLogger(ArchiveSimpleBPMNService.class.getName());

  public static boolean wasExecuted=false;

  public void execute(DelegateExecution execution) throws Exception {

    wasExecuted = true;
    LOGGER.info("\n\n  Archiving SimpleBPMN "+execution.getVariable("SimpleBPMNName")+" \n\n");

  }

}
