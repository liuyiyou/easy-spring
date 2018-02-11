package org.springframework.util;


public abstract class ClassLoaderUtils {


	public static String showClassLoaderHierarchy(Object obj, String role, String delim, String tabText) {
		String s = "object of " + obj.getClass() + ": role is " + role + delim;
		return s + showClassLoaderHierarchy(obj.getClass().getClassLoader(), delim, tabText, 0);
	}


	public static String showClassLoaderHierarchy(ClassLoader cl, String delim, String tabText, int indent) {
		if (cl == null) {
			String s = "null classloader " + delim;
			ClassLoader ctxcl = Thread.currentThread().getContextClassLoader();
			s += "Context class loader=" + ctxcl + " hc=" + ctxcl.hashCode();
			return s;
		}
		String s = "";
		for (int i = 0; i < indent; i++) {
			s += tabText;
		}
		s += cl + " hc=" + cl.hashCode() + delim;
		ClassLoader parent = cl.getParent();
		return s + showClassLoaderHierarchy(parent, delim, tabText, indent + 1);
	}

}
