package br.com.ambientelivre.generatesimplebpmn;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.task.TaskQuery;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.GatewayDirection;
import org.camunda.bpm.model.bpmn.instance.Documentation;
import org.camunda.bpm.model.bpmn.instance.EndEvent;

import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.ParallelGateway;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.bpm.model.xml.type.ModelElementType;

import br.com.ambientelivre.simplebpmn.service.ArchiveSimpleBPMNService;

import org.junit.Rule;
import org.junit.Test;

public class CreateSimpleBPMNProcessTest {

	@Rule
	public ProcessEngineRule processEngine = new ProcessEngineRule();

	@Test
	public void testCreateInvoiceProcess() throws Exception {

		BpmnModelInstance modelInstance = Bpmn.readModelFromFile(new File("BPMNModelForFluentRead.bpmn"));

		ParallelGateway gateway = (ParallelGateway) modelInstance.getModelElementById("gateway");
		gateway.builder().userTask().name("Recovering student").id("recoveringStudentTask").camundaAssignee("demo")
				.documentation("Students did not get enough grade to pass").connectTo("EndEvent");

		UserTask approvedStudentTask = (UserTask) modelInstance.getModelElementById("approvedStudentTask");
		approvedStudentTask.setName("Recovering Student Task");

		// View BPMN in System.out
		//Bpmn.writeModelToStream(System.out, modelInstance);

		File arqXML = new File("BPMNModelForFluentWrite.bpmn");
		Bpmn.writeModelToFile(arqXML, modelInstance);

		// ---------------------- Complex to Simple BPMN ----------------------------------------
		BpmnModelInstance complexModel = Bpmn.readModelFromFile(new File("BPMNModelForFluentComplexRead.bpmn"));
		BpmnModelInstance simpleModel =  Bpmn.readModelFromFile(new File("BPMNModelForFluentSimpleModelRead.bpmn")); 
		
		StartEvent start = complexModel.getModelElementById("StartEvent");
		Collection<Documentation> docStart = start.getDocumentations();
		String showStart = docStart.iterator().next().getTextContent();

		UserTask taskTeacher = (UserTask) complexModel.getModelElementById("TaskTeacher");
		Collection<Documentation> doctaskTeacher = taskTeacher.getDocumentations();
		String showStartTeacher = doctaskTeacher.iterator().next().getTextContent();
		
		// Init new Simple Process		
		StartEvent newStart = (StartEvent) simpleModel.getModelElementById("StartEvent");
		
		if (showStartTeacher.equalsIgnoreCase("show")) {
			newStart.builder().userTask().name("Teacher informs student's grade").id("TaskTeacher").camundaAssignee("demo")
			.documentation("Teacher informs student's grade");

			UserTask newtaskTeacher = (UserTask) simpleModel.getModelElementById("TaskTeacher");			

			newtaskTeacher.builder().userTask().name("Approved student").id("approvedStudentTask").camundaAssignee("demo")
			.documentation("Approved student");

			UserTask newApproved = (UserTask) simpleModel.getModelElementById("approvedStudentTask");			

			newApproved.builder().userTask().name("Confirm Graduation Date").id("ConfirmGraduationDate").camundaAssignee("demo")
			.documentation("Approved student").connectTo("EndEvent");		
		}

		File arqXMLSimple = new File("BPMNModelForFluentSimpleWrite.bpmn");
		Bpmn.writeModelToFile(arqXMLSimple, simpleModel);

		System.out.println(" Propriedade showSimple = " + docStart.iterator().next().getTextContent() );

		// deploy process model
		processEngine.getRepositoryService().createDeployment().addModelInstance("BPMNModelForFluentSimpleWrite.bpmn",
		simpleModel).deploy();
		
		// start process model
		//processEngine.getRuntimeService().startProcessInstanceByKey("BPMNModelForFluentSimpleModelRead");

		// check if process instance is ended
		// org.junit.Assert.assertEquals(0,
		// processEngine.getRuntimeService().createProcessInstanceQuery().count());
		
		
		/*
		 * 
		 * BpmnModelInstance modelInstance = Bpmn.createExecutableProcess("invoice")
		 * .name("BPMN API Invoice Process") .startEvent() .name("Invoice received")
		 * .userTask() .name("Assign Approver") .camundaAssignee("demo") .userTask()
		 * .id("approveInvoice") .name("Approve Invoice") .exclusiveGateway()
		 * .name("Invoice approved?") .gatewayDirection(GatewayDirection.Diverging)
		 * .condition("yes", "${approved}") .userTask() .name("Prepare Bank Transfer")
		 * .camundaFormKey("embedded:app:forms/prepare-bank-transfer.html")
		 * .camundaCandidateGroups("accounting") .serviceTask() .name("Archive Invoice")
		 * .camundaClass(
		 * "org.camunda.bpm.example.invoice.service.ArchiveInvoiceService") .endEvent()
		 * .name("Invoice processed") .moveToLastGateway() .condition("no",
		 * "${!approved}") .userTask() .name("Review Invoice") .camundaAssignee("demo")
		 * .exclusiveGateway() .name("Review successful?")
		 * .gatewayDirection(GatewayDirection.Diverging) .condition("no",
		 * "${!clarified}") .endEvent() .name("Invoice not processed")
		 * .moveToLastGateway() .condition("yes", "${clarified}")
		 * .connectTo("approveInvoice") .done();
		 * 
		 */
	}
}
