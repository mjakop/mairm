/***************************************************************************
 *   Copyright (C) 2010 by                                                 *
 *   	Matej Jakop <matej@jakop.si>                                       *
 *      Gregor Kališnik <gregor@unimatrix-one.org>                         *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License version 3        *
 *   as published by the Free Software Foundation.                         *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 ***************************************************************************/

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import lib.MAIRKeyboard;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class GestureDetectedActions {

	private static TreeMap<String,GestureDetectedAction> actions=new TreeMap<String, GestureDetectedAction>();
	
	public static GestureDetectedAction getActionForGesture(String gestureName){
		gestureName=gestureName.toUpperCase();
		return actions.get(gestureName);
	}
	
	public static void createNewActionForGestureIfNotExist(String gestureName){
		gestureName=gestureName.toUpperCase();
		GestureDetectedAction action=actions.get(gestureName);
		if (action==null){
			action=new GestureDetectedAction();
			actions.put(gestureName, action);
		}
	}
	
	private static String getAtributeValue(Node n, String attrName){
		NamedNodeMap map = n.getAttributes();
		for(int i=0;i<map.getLength();i++){
			Node item=map.item(i);
			if (item.getNodeName().equalsIgnoreCase(attrName)){
				return item.getTextContent();
			}
		}
		return "";
	}
	
	private static void parseActions(Node n){
		NodeList act=n.getChildNodes();
		for(int i=0;i<act.getLength();i++){
			Node a=act.item(i);
			if (a.getNodeName().equalsIgnoreCase("action")){
				String gestureName=getAtributeValue(a,"gesture");
				GestureDetectedAction action=new GestureDetectedAction();
				actions.put(gestureName, action);
				//now parse other things for this action
				NodeList aList=a.getChildNodes();
				for(int j=0;j<aList.getLength();j++){
					Node al=aList.item(j);
					if (al.getNodeName().equalsIgnoreCase("exec")){
						action.setExec(al.getTextContent());
					}else if (al.getNodeName().equalsIgnoreCase("keys")) {
		        		parseKeys(al,action);
		        	}
				}
			}
		}
	}
	
	private static void parseKeys(Node n, GestureDetectedAction action){
		NodeList l=n.getChildNodes();
		for(int i=0;i<l.getLength();i++){
			Node l1=l.item(i);
			if (l1.getNodeName().equalsIgnoreCase("key")){
				GestureDetectedActionKey key=new GestureDetectedActionKey();
				String delayRelease=getAtributeValue(l1, "delayRelease");
				key.setImediatellyRelease(delayRelease.equalsIgnoreCase("false"));
				key.setKeyCode(MAIRKeyboard.stringToKeyCode(l1.getTextContent()));
				action.addKey(key);
			}
		}
	}
	
	public static boolean loadFromFile(String fileName) throws Exception{
		actions.clear();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        factory.setNamespaceAware(false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(fileName));
        NodeList l=doc.getFirstChild().getChildNodes();
        for(int i=0;i<l.getLength();i++){
        	Node n=l.item(i);
        	if (n.getNodeName().equalsIgnoreCase("actions")){
        		parseActions(n);
        	}
        }
		return true;
	}
	
	public static boolean saveChangesToFile(String fileName) throws Exception{
		StringBuffer buff=new StringBuffer();
		buff.append("<MAIRM>\n");
			buff.append("\t<actions>\n");
				for (String key : actions.keySet()) {
					GestureDetectedAction item=actions.get(key);
					buff.append("\t\t<action gesture=\""+key+"\">\n");
					//other parameters
					buff.append("\t\t\t<exec>");
					buff.append(item.getExec());
					buff.append("</exec>\n");
					buff.append("\t\t\t<keys>\n");
					for(int j=0;j<item.getKeys().size();j++){
						GestureDetectedActionKey k=item.getKeys().get(j);
						String delayRelease="FALSE";
						if (k.isImediatellyRelease()==false){
							delayRelease="TRUE";
						}
						buff.append("\t\t\t\t<key delayRelease=\""+delayRelease+"\">");
						buff.append(MAIRKeyboard.keyCodeToString(k.getKeyCode()));
						buff.append("</key>\n");
					}
					buff.append("\t\t\t</keys>\n");
					buff.append("\t\t</action>\n");
				}
			buff.append("\t</actions>\n");
		buff.append("</MAIRM>");
		BufferedWriter writer=new BufferedWriter(new FileWriter(fileName));
		writer.write(buff.toString());
		writer.flush();
		writer.close();
		return true;
	}
}
