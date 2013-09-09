/** 
   Copyright 2013 Technische Universitat Wien (TUW), Distributed SystemsGroup E184.               
   
   This work was partially supported by the European Commission in terms of the CELAR FP7 project (FP7-ICT-2011-8 #317790).
 
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

/**
 *  Author : Georgiana Copil - e.copil@dsg.tuwien.ac.at
 */

package at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.abstractModelXML;

import java.util.List;

import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.BinaryRestriction;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.Condition;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.Constraint;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.Monitor;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.Monitoring;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.Priority;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.ReferenceTo;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.SYBLAnnotation;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.SYBLSpecification;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.Strategy;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.ToEnforce;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.UnaryRestriction;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.BinaryRestriction.LeftHandSide;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.BinaryRestriction.RightHandSide;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.SYBLAnnotation.AnnotationType;








public class SYBLDirectiveMappingFromXML {
public static SYBLAnnotation mapFromXMLRepresentation(SYBLSpecification syblSpecification){
	SYBLAnnotation syblAnnotation = new SYBLAnnotation();
	
	switch (syblSpecification.getType().toUpperCase()){
	case "SERVICE_UNIT": syblAnnotation.setAnnotationType(AnnotationType.SERVICE_UNIT); 
		break;
	case "SERVICE_TOPOLOGY": syblAnnotation.setAnnotationType(AnnotationType.SERVICE_TOPOLOGY); 
	break;
	case "CODE_REGION": syblAnnotation.setAnnotationType(AnnotationType.CODE_REGION); 
	break;
	case "CLOUD_SERVICE": syblAnnotation.setAnnotationType(AnnotationType.CLOUD_SERVICE); 
	break;
	case "RELATIONSHIP": syblAnnotation.setAnnotationType(AnnotationType.RELATIONSHIP);
	break;
	default: syblAnnotation.setAnnotationType(AnnotationType.SERVICE_UNIT);
	}
	syblAnnotation.setEntityID(syblSpecification.getComponentId());
	String constraints ="";
	List<Constraint> constr = syblSpecification.getConstraint();
	for (Constraint constraint:constr){
		constraints+=mapXMLConstraintToSYBLAnnotation(constraint);
	}
	String monitoring = "";
	List<Monitoring> monitorings = syblSpecification.getMonitoring();
	for (Monitoring m:monitorings){
		monitoring+=mapFromXMLMonitoringToSYBLAnnotation(m);
	}

	String strategies = "";
	List<Strategy> str = syblSpecification.getStrategy();
	for (Strategy strategy:str){
		strategies+=mapFromXMLStrategyToSYBLAnnotation(strategy);
	}
	
	String priorities = "";
	List<Priority> pr = syblSpecification.getPriority();
	for (Priority priority:pr){
		priorities+=priority;
	}
	
	syblAnnotation.setConstraints(constraints);
	syblAnnotation.setMonitoring(monitoring);
	syblAnnotation.setStrategies(strategies);
	syblAnnotation.setPriorities(priorities);
	return syblAnnotation;
}

public static String mapXMLConstraintToSYBLAnnotation(Constraint constraint){
	String constraints=constraint.getId()+":CONSTRAINT ";
	if (constraint.getToEnforce().getBinaryRestriction().size()>0){
		BinaryRestriction binaryRestriction = constraint.getToEnforce().getBinaryRestriction().get(0);
		String leftHandSide="";
		String rightHandSide="";
		if (binaryRestriction.getLeftHandSide().getMetric()!=null){
			leftHandSide=binaryRestriction.getLeftHandSide().getMetric();
			rightHandSide=binaryRestriction.getRightHandSide().getNumber();
		}else{
			leftHandSide=binaryRestriction.getLeftHandSide().getNumber();
			rightHandSide=binaryRestriction.getRightHandSide().getMetric();
		}
		constraints+=leftHandSide;
		switch(binaryRestriction.getType()){
		case "lessThan":constraints+=" < ";
		break;
		case "greaterThan":constraints+=" > ";
		break;
		case "lessThanOrEqual":constraints+=" <= ";
		break;
		case "greaterThanOrEqual":constraints+=" >= ";
		break;
		case "differentThan":constraints+=" != ";
		break;
		case "equals":constraints+=" = ";
		break;
		default:
			constraints+=" < ";
			break;
		}
		constraints+=rightHandSide;
	}
	
	
	//TODO:Add UnaryRestriction
	if (constraint.getToEnforce().getUnaryRestriction().size()>0){
		
	}
	
	if (constraint.getCondition()!=null){
		constraints += " WHEN ";
		if (constraint.getCondition().getBinaryRestriction().size()>0){
		BinaryRestriction binaryRestriction = constraint.getCondition().getBinaryRestriction().get(0);
		String leftHandSide="";
		String rightHandSide="";
		if (binaryRestriction.getLeftHandSide().getMetric()!=null){
			leftHandSide=binaryRestriction.getLeftHandSide().getMetric();
			rightHandSide=binaryRestriction.getRightHandSide().getNumber();
		}else{
			leftHandSide=binaryRestriction.getLeftHandSide().getNumber();
			rightHandSide=binaryRestriction.getRightHandSide().getMetric();
		}
		
		constraints+=leftHandSide;
		switch(binaryRestriction.getType()){
		case "lessThan":constraints+=" < ";
		break;
		case "greaterThan":constraints+=" > ";
		break;
		case "lessThanOrEqual":constraints+=" <= ";
		break;
		case "greaterThanOrEqual":constraints+=" >= ";
		break;
		case "differentThan":constraints+=" != ";
		break;
		case "equals":constraints+=" = ";
		break;
		default:
			constraints+=" < ";
			break;
		}
		constraints+=rightHandSide;
		
	}
	
	
	//TODO:Add UnaryRestriction
	if (constraint.getCondition().getUnaryRestriction().size()>0){
		
	}
	
	}
	constraints+=";";
	return constraints;
}

public static String mapFromXMLStrategyToSYBLAnnotation(Strategy strategy){
	if (strategy.getCondition()!=null){
	String strategies = strategy.getId()+":STRATEGY CASE ";
	int nb = 0;
	if (strategy.getCondition()!=null){
		if (strategy.getCondition().getBinaryRestriction().size()>0){
			BinaryRestriction binaryRestriction = strategy.getCondition().getBinaryRestriction().get(0);
			String leftHandSide="";
			String rightHandSide="";
			if (binaryRestriction.getLeftHandSide().getMetric()!=null){
				leftHandSide=binaryRestriction.getLeftHandSide().getMetric();
				rightHandSide=binaryRestriction.getRightHandSide().getNumber();
			}else{
				leftHandSide=binaryRestriction.getLeftHandSide().getNumber();
				rightHandSide=binaryRestriction.getRightHandSide().getMetric();
			}
			strategies+=leftHandSide;
			switch(binaryRestriction.getType()){
			case "lessThan":strategies+=" < ";
			break;
			case "greaterThan":strategies+=" > ";
			break;
			case "lessThanOrEqual":strategies+=" <= ";
			break;
			case "greaterThanOrEqual":strategies+=" >= ";
			break;
			case "differentThan":strategies+=" != ";
			break;
			case "equals":strategies+=" = ";
			break;
			default:
				strategies+=" < ";
				break;
			}
			strategies+=rightHandSide;
			if (strategy.getCondition().getBinaryRestriction().size()>1){
				strategies +=" AND ";
				binaryRestriction = strategy.getCondition().getBinaryRestriction().get(1);
			    leftHandSide="";
			    rightHandSide="";
			if (binaryRestriction.getLeftHandSide().getMetric()!=null){
				
				leftHandSide=binaryRestriction.getLeftHandSide().getMetric();
				rightHandSide=binaryRestriction.getRightHandSide().getNumber();
			}else{
				leftHandSide=binaryRestriction.getLeftHandSide().getNumber();
				rightHandSide=binaryRestriction.getRightHandSide().getMetric();
			}
			strategies+=leftHandSide;
			switch(binaryRestriction.getType()){
			case "lessThan":strategies+=" < ";
			break;
			case "greaterThan":strategies+=" > ";
			break;
			case "lessThanOrEqual":strategies+=" <= ";
			break;
			case "greaterThanOrEqual":strategies+=" >= ";
			break;
			case "differentThan":strategies+=" != ";
			break;
			case "equals":strategies+=" = ";
			break;
			default:
				strategies+=" < ";
				break;
			}
			strategies+=rightHandSide;

			}
			
			//nb+=1;
		}

	 if (strategy.getCondition().getUnaryRestriction()!=null && strategy.getCondition().getUnaryRestriction().size()>0){
		 
		 UnaryRestriction unaryRestriction=strategy.getCondition().getUnaryRestriction().get(0);
		 if (nb>0) strategies += " AND ";
		 if (unaryRestriction.getReferenceTo().getValue()!="") strategies += unaryRestriction.getReferenceTo().getValue();
		 else
		 if (unaryRestriction.getReferenceTo().getName()!="") strategies += unaryRestriction.getReferenceTo().getFunction()+"("+unaryRestriction.getReferenceTo().getName()+")";
		
		 //System.err.println("EEEEEEEEEEEEEEEEEEEEEEEEEEEE  "+ strategies+strategy.getCondition().getUnaryRestriction());

	 }
	 strategies +=" : ";
	}
	//System.err.println("EEEEEEEEEEEEEEEEEEEEEEEEEEEE  "+ strategies);

	if (strategy.getToEnforce().getParameter()!=null && strategy.getToEnforce().getParameter()!=""){
		strategies += strategy.getToEnforce().getActionName()+"("+strategy.getToEnforce().getParameter()+");";
	}else
	strategies += strategy.getToEnforce().getActionName()+";";
	//System.err.println("EEEEEEEEEEEEEEEEEEEEEEEEEEEE Returning "+ strategies);

	return strategies;
	}else{
		String strategies = strategy.getId()+":STRATEGY ";
		if (strategy.getToEnforce().getParameter()!=null && strategy.getToEnforce().getParameter()!=""){
			strategies += strategy.getToEnforce().getActionName()+"("+strategy.getToEnforce().getParameter()+");";
		}else
		strategies += strategy.getToEnforce().getActionName()+";";
		return strategies;
	}
}
public static String mapFromXMLMonitoringToSYBLAnnotation(Monitoring m){
	String monitoring= m.getId()+":MONITORING ";
	monitoring += m.getMonitor().getEnvVar() +" = ";
	monitoring +=m.getMonitor().getMetric();
	if (m.getCondition()!=null){
		monitoring += " WHEN";
		if (m.getCondition().getBinaryRestriction().size()>0){
		BinaryRestriction binaryRestriction = m.getCondition().getBinaryRestriction().get(0);
		String leftHandSide="";
		String rightHandSide="";
		if (binaryRestriction.getLeftHandSide().getMetric()!=null){
			leftHandSide=binaryRestriction.getLeftHandSide().getMetric();
			rightHandSide=binaryRestriction.getRightHandSide().getNumber();
		}else{
			leftHandSide=binaryRestriction.getLeftHandSide().getNumber();
			rightHandSide=binaryRestriction.getRightHandSide().getMetric();
		}
		
		monitoring+=leftHandSide;
		switch(binaryRestriction.getType()){
		case "lessThan":monitoring+=" < ";
		break;
		case "greaterThan":monitoring+=" > ";
		break;
		case "lessThanOrEqual":monitoring+=" <= ";
		break;
		case "greaterThanOrEqual":monitoring+=" >= ";
		break;
		case "differentThan":monitoring+=" != ";
		break;
		case "equals":monitoring+=" = ";
		break;
		default:
			monitoring+=" < ";
			break;
		}
		monitoring+=rightHandSide;
	}
		
}
	monitoring+=";";
	return monitoring;
	

	
}
public static String mapFromXMLPriorityToSYBLAnnotation(Priority priority){
	String priorities ="";
	if (priority.getCondition()!=null){
		BinaryRestriction binaryRestriction = priority.getCondition().getBinaryRestriction().get(0);
		priorities += "Priority("+binaryRestriction.getLeftHandSide().getMetric()+")";
		
		switch(binaryRestriction.getType()){
		case "lessThan":priorities+=" < ";
		break;
		case "greaterThan":priorities+=" > ";
		break;
		case "lessThanOrEqual":priorities+=" <= ";
		break;
		case "greaterThanOrEqual":priorities+=" >= ";
		break;
		case "differentThan":priorities+=" != ";
		break;
		case "equals":priorities+=" = ";
		break;
		default:
			priorities+=" < ";
			break;
		}

		priorities+="Priority("+binaryRestriction.getRightHandSide().getMetric()+") ;";
		
	}
	return priorities;
}
public static SYBLSpecification mapFromSYBLAnnotation(SYBLAnnotation syblAnnotation){
	SYBLSpecification syblSpecification = new SYBLSpecification();
	
	switch (syblAnnotation.getAnnotationType()){
	case SERVICE_UNIT: syblSpecification.setType("SERVICE_UNIT"); 
	break;
	case SERVICE_TOPOLOGY:syblSpecification.setType("SERVICE_TOPOLOGY"); 
	break;
	case CODE_REGION:syblSpecification.setType("CODE_REGION"); 
	break;
	case CLOUD_SERVICE:syblSpecification.setType("CLOUD_SERVICE"); 
	break;
	case RELATIONSHIP:syblSpecification.setType("RELATIONSHIP");;
	break;
	default: syblSpecification.setType("SERVICE_UNIT");
	}
	
	syblSpecification.setComponentId(syblAnnotation.getEntityID());
	
	if (syblAnnotation.getConstraints()!="")	{
	String[] constraints=syblAnnotation.getConstraints().split(";");
	for (String constraint:constraints){
		syblSpecification.addConstraint(mapSYBLAnnotationToXMLConstraint(constraint));
	}
	}
	
	if (syblAnnotation.getStrategies()!=""){
	String[] strategies=syblAnnotation.getStrategies().split(";");
	for (String strategy:strategies){
			syblSpecification.addStrategy(mapFromSYBLAnnotationToXMLStrategy(strategy));
	}
}
	/*
	 @SYBL_ComponentDirective(annotatedEntityID="test",
			  monitoring ="Mo1:MONITORING cost = cost.instant;" +
			  		"Mo2: MONITORING cpuUsage = cpu.usage;",
			  strategies="St1:STRATEGY CASE Violated(Co2): scaleIn; " +
			  		"St2:STRATEGY CASE Enabled(Co1) AND Violated(Co1): scaleOut;" +
			  		"St3:STRATEGY CASE Enabled(Co3) AND Violated(Co3): scaleOut;",
			  priorities="Priority(Co3) > Priority(Co1)")
*/
	if (syblAnnotation.getMonitoring()!="")	
	{
	String[] monitoring=syblAnnotation.getMonitoring().split(";");
	for (String monitor:monitoring){
		syblSpecification.addMonitoring(mapFromSYBLAnnotationToXMLMonitoring(monitor));
	}
	}
	/*
	 @SYBL_ComponentDirective(annotatedEntityID="test",
			  monitoring ="Mo1:MONITORING cost = cost.instant;" +
			  		"Mo2: MONITORING cpuUsage = cpu.usage;",
			  strategies="St1:STRATEGY CASE Violated(Co2): scaleIn; " +
			  		"St2:STRATEGY CASE Enabled(Co1) AND Violated(Co1): scaleOut;" +
			  		"St3:STRATEGY CASE Enabled(Co3) AND Violated(Co3): scaleOut;",
			  priorities="Priority(Co3) > Priority(Co1)")
*/
	if (syblAnnotation.getPriorities()!="")	
	{
	String[] pr=syblAnnotation.getPriorities().split(";");
	for (String p:pr){
		syblSpecification.addPriority(mapFromSYBLAnnotationToXMLPriority(p));
	}}
	//System.err.println("AAAAAAAAAAAAAEEEEEEEEEEEEEEEEEEE"+syblSpecification.toString());
	return syblSpecification;
}
public static Constraint mapSYBLAnnotationToXMLConstraint(String constraint ){
	
	String [] s = constraint.split("[: ]");
	Constraint c = new Constraint();
	Condition toEnforce = new Condition();
	Condition cond = new Condition();
	c.setId(s[0]);
	BinaryRestriction binaryRestr = new BinaryRestriction();
	LeftHandSide leftHandSide = new LeftHandSide();
	if ((s[2].charAt(0)>='a' && s[2].charAt(0)<='z') ||(s[2].charAt(0)>='A' && s[2].charAt(0)<='Z')) leftHandSide.setMetric(s[2]);
	else leftHandSide.setMetric(s[2]);
	switch (s[3]){
	case "<":binaryRestr.setType("lessThan");
	break;
	case ">":binaryRestr.setType("greaterThan");
	break;
	case "<=":binaryRestr.setType("lessThanOrEqual");
	break;
	case ">=":binaryRestr.setType("greaterThanOrEqual");
	break;
	case "&lt;":binaryRestr.setType("lessThan");
	break;
	
	default:binaryRestr.setType("lessThan");
		break;
	}
	RightHandSide rightHandSide = new RightHandSide();
	if ((s[4].charAt(0)>='a' && s[4].charAt(0)<='z') ||(s[4].charAt(0)>='A' && s[4].charAt(0)<='Z')) rightHandSide.setMetric(s[4]);
	else
	rightHandSide.setNumber(s[4]);
	binaryRestr.setLeftHandSide(leftHandSide);
	binaryRestr.setRightHandSide(rightHandSide);
	toEnforce.addBinaryRestriction(binaryRestr);
	c.setToEnforce(toEnforce);

	if (constraint.contains("WHEN")){
		
	String [] x = constraint.split("WHEN ");
	//for (String y:x) System.err.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAA"+y);
	String [] when = x[1].split(" ");
		if (when.length>2){
			BinaryRestriction binaryRestriction = new BinaryRestriction();
			LeftHandSide leftHandSide2 = new LeftHandSide();
			RightHandSide rightHandSide2 = new RightHandSide();
			if ((when[0].charAt(0)>='a' && when[0].charAt(0)<='z') ||(when[0].charAt(0)>='A' && when[0].charAt(0)<='Z')) leftHandSide2.setMetric(when[0]);
			else
			leftHandSide2.setNumber(when[0]);
			if ((when[2].charAt(0)>='a' && when[2].charAt(0)<='z') ||(when[2].charAt(0)>='A' && when[2].charAt(0)<='Z')) rightHandSide2.setMetric(when[2]);
			else
			rightHandSide2.setNumber(when[2]);
			switch(when[1]){
			case "<":binaryRestriction.setType("lessThan");
			break;
			case ">":binaryRestriction.setType("greaterThan");
			break;
			case "<=":binaryRestriction.setType("lessThanOrEqual");
			break;
			case ">=":binaryRestriction.setType("greaterThanOrEqual");
			break;
			case "&lt;":binaryRestriction.setType("lessThan");
			break;
			
			default:binaryRestriction.setType("lessThan");
				break;
			}
			binaryRestriction.setLeftHandSide(leftHandSide2);
			binaryRestriction.setRightHandSide(rightHandSide2);
			cond.addBinaryRestriction(binaryRestriction);
			c.setCondition(cond);
		}else{
			if (when.length>1){
				UnaryRestriction unaryRestriction = new UnaryRestriction();
				ReferenceTo referenceTo = new ReferenceTo();
				referenceTo.setValue(when[1]);
				unaryRestriction.setReferenceTo(referenceTo);
				cond.addUnaryRestriction(unaryRestriction);
				
				c.setCondition(cond);
			}
		}
	}
	
	return c;
}
public static Strategy mapFromSYBLAnnotationToXMLStrategy(String strategy){
	String [] s = strategy.split("[: ]");
	Strategy c = new Strategy();
	ToEnforce toEnforce = new ToEnforce();

	if (! s[s.length-1].contains("("))
		toEnforce.setActionName(s[s.length-1]);
	else
	{
		String []a = s[s.length-1].split("[ ( ) ]");
		//System.err.println("a[0]="+a[0]);
		toEnforce.setActionName(a[0]);
		toEnforce.setParameter(a[1]);
	}
	c.setToEnforce(toEnforce);
	c.setId(s[0]);
	if (strategy.toLowerCase().contains("case")){
	Condition cond = new Condition();

	

	if (s.length>5){
	
	if (s[7].equalsIgnoreCase("AND") || s[6].equalsIgnoreCase("AND")){
		
		if (s.length==8){
			UnaryRestriction unaryRestriction = new UnaryRestriction();
			ReferenceTo referenceTo = new ReferenceTo();
			referenceTo.setValue(s[3]);
			unaryRestriction.setReferenceTo(referenceTo);
			cond.addUnaryRestriction(unaryRestriction);
			unaryRestriction=new UnaryRestriction();
			referenceTo = new ReferenceTo();
			referenceTo.setValue(s[6]);
			unaryRestriction.setReferenceTo(referenceTo);
			cond.addUnaryRestriction(unaryRestriction);
			
		}else{
			BinaryRestriction binaryRestriction = new BinaryRestriction();
			LeftHandSide leftHandSide2 = new LeftHandSide();
			RightHandSide rightHandSide2 = new RightHandSide();
			
			if ((s[3].charAt(0)>='a' && s[3].charAt(0)<='z') ||(s[3].charAt(0)>='A' && s[3].charAt(0)<='Z')) leftHandSide2.setMetric(s[3]);
			else
			leftHandSide2.setNumber(s[3]);
			if ((s[5].charAt(0)>='a' && s[5].charAt(0)<='z') ||(s[5].charAt(0)>='A' && s[5].charAt(0)<='Z')) rightHandSide2.setMetric(s[5]);
			else
				rightHandSide2.setNumber(s[5]);
			switch(s[4]){
			case "<":binaryRestriction.setType("lessThan");
			break;
			case ">":binaryRestriction.setType("greaterThan");
			break;
			case "<=":binaryRestriction.setType("lessThanOrEqual");
			break;
			case ">=":binaryRestriction.setType("greaterThanOrEqual");
			break;
			case "&lt;":binaryRestriction.setType("lessThan");
			break;
			default:binaryRestriction.setType("lessThan");
				break;
			}
			binaryRestriction.setLeftHandSide(leftHandSide2);
			binaryRestriction.setRightHandSide(rightHandSide2);
			cond.addBinaryRestriction(binaryRestriction);
			int index = 0;
			if (s[7].equalsIgnoreCase("AND")) index= 7;
			else index = 6;
			binaryRestriction = new BinaryRestriction();
			 leftHandSide2 = new LeftHandSide();
			 rightHandSide2 = new RightHandSide();
			
			if ((s[index+1].charAt(0)>='a' && s[index+1].charAt(0)<='z') ||(s[index+1].charAt(0)>='A' && s[index+1].charAt(0)<='Z')) leftHandSide2.setMetric(s[index+1]);
			else
			leftHandSide2.setNumber(s[3]);
			if ((s[index+3].charAt(0)>='a' && s[index+3].charAt(0)<='z') ||(s[index+3].charAt(0)>='A' && s[index+3].charAt(0)<='Z')) rightHandSide2.setMetric(s[index+3]);
			else
				rightHandSide2.setNumber(s[index+3]);
			switch(s[index+2]){
			case "<":binaryRestriction.setType("lessThan");
			break;
			case ">":binaryRestriction.setType("greaterThan");
			break;
			case "<=":binaryRestriction.setType("lessThanOrEqual");
			break;
			case ">=":binaryRestriction.setType("greaterThanOrEqual");
			break;
			case "&lt;":binaryRestriction.setType("lessThan");
			break;
			default:binaryRestriction.setType("lessThan");
				break;
			}
			binaryRestriction.setLeftHandSide(leftHandSide2);
			binaryRestriction.setRightHandSide(rightHandSide2);
			cond.addBinaryRestriction(binaryRestriction);
		}
	}else{
			
			BinaryRestriction binaryRestriction = new BinaryRestriction();
			LeftHandSide leftHandSide2 = new LeftHandSide();
			RightHandSide rightHandSide2 = new RightHandSide();
			if ((s[3].charAt(0)>='a' && s[3].charAt(0)<='z') ||(s[3].charAt(0)>='A' && s[3].charAt(0)<='Z')) leftHandSide2.setMetric(s[4]);
			else
			leftHandSide2.setNumber(s[3]);
			if ((s[5].charAt(0)>='a' && s[5].charAt(0)<='z') ||(s[5].charAt(0)>='A' && s[5].charAt(0)<='Z')) rightHandSide2.setMetric(s[6]);
			else
			rightHandSide2.setMetric(s[5]);
			switch(s[4]){
			case "<":binaryRestriction.setType("lessThan");
			break;
			case ">":binaryRestriction.setType("greaterThan");
			break;
			case "<=":binaryRestriction.setType("lessThanOrEqual");
			break;
			case ">=":binaryRestriction.setType("greaterThanOrEqual");
			break;
			case "&lt;":binaryRestriction.setType("lessThan");
			break;
			default:binaryRestriction.setType("lessThan");
				break;
			}
			binaryRestriction.setLeftHandSide(leftHandSide2);
			binaryRestriction.setRightHandSide(rightHandSide2);
			cond.addBinaryRestriction(binaryRestriction);
	}
	}else{
		UnaryRestriction unaryRestriction = new UnaryRestriction();
		ReferenceTo referenceTo = new ReferenceTo();
		referenceTo.setValue(s[3]);
		unaryRestriction.setReferenceTo(referenceTo);
		cond.addUnaryRestriction(unaryRestriction);
	}
	c.setCondition(cond);
	}
	return c;

}
public static Monitoring mapFromSYBLAnnotationToXMLMonitoring(String monitor){
	String [] s = monitor.split("[: ]");
	Monitoring monitoring1 = new Monitoring();
	monitoring1.setId(s[0]);
	Monitor m = new Monitor();
	m.setEnvVar(s[1]);
	m.setMetric(s[2]);
	Condition c = new Condition();
	if (s[3].equalsIgnoreCase("when")){
		BinaryRestriction binaryRestriction = new BinaryRestriction();
		LeftHandSide leftHandSide2 = new LeftHandSide();
		RightHandSide rightHandSide2 = new RightHandSide();
		if ((s[4].charAt(0)>='a' && s[4].charAt(0)<='z') ||(s[4].charAt(0)>='A' && s[4].charAt(0)<='Z')) leftHandSide2.setMetric(s[4]);
		else
		leftHandSide2.setNumber(s[4]);
		if ((s[6].charAt(0)>='a' && s[6].charAt(0)<='z') ||(s[6].charAt(0)>='A' && s[6].charAt(0)<='Z')) rightHandSide2.setMetric(s[6]);
		else
		rightHandSide2.setNumber(s[6]);
		switch(s[5]){
		case "<":binaryRestriction.setType("lessThan");
		break;
		case ">":binaryRestriction.setType("greaterThan");
		break;
		case "<=":binaryRestriction.setType("lessThanOrEqual");
		break;
		case ">=":binaryRestriction.setType("greaterThanOrEqual");
		break;
		case "&lt;":binaryRestriction.setType("lessThan");
		break;
		default:binaryRestriction.setType("lessThan");
			break;
		}
		binaryRestriction.setLeftHandSide(leftHandSide2);
		binaryRestriction.setRightHandSide(rightHandSide2);
		c.addBinaryRestriction(binaryRestriction);
	}
	monitoring1.setCondition(c);
	monitoring1.setMonitor(m);
	return monitoring1;
}
public static Priority mapFromSYBLAnnotationToXMLPriority(String p){
	Priority priority = new Priority();
	String[] s = p.split("[: ]");
	priority.setId(s[0]);
	Condition cond = new Condition();
	BinaryRestriction binaryRestriction =  new BinaryRestriction();
	LeftHandSide leftHandSide2 = new LeftHandSide();
	RightHandSide rightHandSide2 = new RightHandSide();
	if ((s[1].charAt(0)>='a' && s[1].charAt(0)<='z') ||(s[1].charAt(0)>='A' && s[1].charAt(0)<='Z')) leftHandSide2.setMetric(s[1]);
	else
	leftHandSide2.setNumber(s[1]);
	
	if ((s[3].charAt(0)>='a' && s[3].charAt(0)<='z') ||(s[3].charAt(0)>='A' && s[3].charAt(0)<='Z')) rightHandSide2.setMetric(s[3]);
	else
	rightHandSide2.setNumber(s[3]);
	switch(s[2]){
	case "<":binaryRestriction.setType("lessThan");
	break;
	case ">":binaryRestriction.setType("greaterThan");
	break;
	case "<=":binaryRestriction.setType("lessThanOrEqual");
	break;
	case ">=":binaryRestriction.setType("greaterThanOrEqual");
	break;
	case "&lt;":binaryRestriction.setType("lessThan");
	break;
	default:binaryRestriction.setType("lessThan");
		break;
	}
	binaryRestriction.setLeftHandSide(leftHandSide2);
	binaryRestriction.setRightHandSide(rightHandSide2);
	priority.setCondition(cond);
	return priority;
}
}