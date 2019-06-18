package utilidades;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class UBean {
	
	public static ArrayList<Field> obtenerAtributos(Object o){ 
		Class c = o.getClass();
		ArrayList<Field> atts = new ArrayList();
		for (Field f : c.getDeclaredFields()) {
			atts.add(f);
		}
		return atts;
	}
	
	public static void ejecutarSet(Object o, String att, Object valor){
		Class c = o.getClass();		
		Method[] methods = c.getDeclaredMethods();
		for (Method m : methods) {
			String metodo = m.getName().toLowerCase();
			if (metodo.startsWith("set")) {
				if (metodo.contains(att)) {
					try {
						m.invoke(o, valor);
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public static Object ejecutarGet(Object o, String att){
		Class c = o.getClass();
		Object res = null;
		Object[] valor = new Object[0];
		Method[] methods = c.getDeclaredMethods();
		for (Method m : methods) {
			String metodo = m.getName().toLowerCase();
			if (metodo.startsWith("get")) {
				if (metodo.contains(att)) {
					try {
						res = m.invoke(o, valor);
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return res;
	}
}
