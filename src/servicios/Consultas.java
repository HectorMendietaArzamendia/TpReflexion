package servicios;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import anotaciones.Columna;
import anotaciones.Tabla;
import utilidades.UBean;
import utilidades.UConexion;

public class Consultas {

	public void guardar(Object o){
		Class c = o.getClass();
		Connection conexion = UConexion.getInstancia();
		String tabla;
		if (c.getAnnotation(Tabla.class) != null) {
			tabla = c.getName();
			ArrayList<String> columnas = new ArrayList();
			String colum = "";
			String valores = "";
			for (Field f : c.getDeclaredFields()) {
				if (f.getAnnotation(Columna.class) != null) {
					String columna = f.getName();
					columnas.add(columna);
					colum = colum.concat(", ".concat(columna));
					valores = valores.concat(", '".concat((String) UBean.ejecutarGet(o, columna)).concat("'"));
				}
			}
			colum = colum.replaceFirst(", ", "");
			valores = valores.replaceFirst(", ", "");
			String query = "INSERT INTO ".concat(tabla.concat(" (")).concat(colum.concat(") VALUES (")).concat(valores.concat(")"));
			try {
				PreparedStatement ps = conexion.prepareStatement(query);
				ps.execute();
			} catch (SQLException e) {
				e.printStackTrace();
			}			
		}
	}
	
	public void modificar(Object o){}
	
	public void eliminar(Object o){}
	
	public Object obtenerPorId(Class c, Object id){ return null; }
}
