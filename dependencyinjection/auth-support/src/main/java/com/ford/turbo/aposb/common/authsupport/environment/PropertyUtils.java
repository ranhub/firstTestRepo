package com.ford.turbo.aposb.common.authsupport.environment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PropertyUtils {

	public static Map<String, Object> getProperties(ConfigPropertyService configPropertyService, String name) {
		PropertyNamespace propertyNamespace = new PropertyNamespace(configPropertyService, name);
		return propertyNamespace.getChildPropertyNames().isEmpty() ? Collections.emptyMap() : (Map) getValue(propertyNamespace, name);
	}

	static Object getValue(PropertyNamespace propertyNamespace, String name) {
		if (propertyNamespace.isObject(name)) {
			return propertyNamespace.getChildPropertyNames(name).stream().collect(Collectors.toMap(Function.identity(), childName -> getValue(propertyNamespace, name + "." + childName)));
		} else if (propertyNamespace.isArray(name)) {
			Object value;
			int i = 0;
			List<Object> elements = new ArrayList<Object>();
			while ((value = getValue(propertyNamespace, String.format("%s[%s]", name, i++))) != null) elements.add(value);
			return elements;
		} else {
			return propertyNamespace.getProperty(name);
		}
	}

	public static class PropertyNamespace {
		ConfigPropertyService configPropertyService;
		
		Map<String, Collection<String>> childPropertyNames = new HashMap<>(); //i.e.  given propeties a.b.d, a.c, x.y[0].i  ==>  a={b,c}, a.b={d}, x={y}, x.y[0]={i}
		Set<String> arrayPropertyNames = new HashSet<>();  //i.e.  given propeties a.b.e[0] x.y[0].i  ==> {a.b.e, x.y}

		public PropertyNamespace(ConfigPropertyService configPropertyService, String namespace) {
			this.configPropertyService = configPropertyService;
			collectChildPropertyNames(namespace);
		}

		public boolean isObject(String parentName) {
			return childPropertyNames.containsKey(parentName);
		}

		public boolean isArray(String name) {
			return arrayPropertyNames.contains(name);
		}
		
		public Map<String, Collection<String>> getChildPropertyNames() {
			return childPropertyNames;
		}
		
		public Collection<String> getChildPropertyNames(String parentName) {
			return childPropertyNames.get(parentName);
		}

		public String getProperty(String name) {
			return this.configPropertyService.getProperty(name);
		}

		void collectChildPropertyNames(String namespace) {
			int pos;
			for (String propertyName : this.configPropertyService.getPropertyNamesStartingWith(namespace + ".")) {
				String currentName = propertyName;
				while ((pos = currentName.lastIndexOf('.')) >= 0) { //split on last .
					currentName = registerChildProperty(currentName.substring(0, pos) /*before .*/, currentName.substring(pos + 1) /*after .*/);
				}
			}
		}

		String registerChildProperty(String parentName, String childName) {
			if (!childPropertyNames.containsKey(parentName))
				childPropertyNames.put(parentName, new HashSet<>());
			int pos = childName.lastIndexOf('[');
			if (pos >= 0) { // child is array
				childName = childName.substring(0, pos);  //drop index portion [*] for child only  (not parent)
				arrayPropertyNames.add(parentName + "." + childName);
			}
			childPropertyNames.get(parentName).add(childName);
			return parentName;
		}
	}

}
