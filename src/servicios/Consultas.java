package servicios;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import anotaciones.Columna;
import anotaciones.Id;
import anotaciones.Tabla;
import utilidades.UBean;
import utilidades.UConexion;

public class Consultas {

	public static Object guardar(Object o){
		Class c = o.getClass();		
		if (c.getAnnotation(Tabla.class) != null) {
			Connection conexion = UConexion.getInstancia();
			Tabla t = (Tabla) c.getAnnotation(Tabla.class);
			String tabla = t.nombre();
			String idNombre = "";
			int id;
			String columnas = "";
			StringBuilder valores = new StringBuilder();
			
			for (Field f : UBean.obtenerAtributos(o)) {
				if (f.getAnnotation(Columna.class) != null) {
					String columna = f.getAnnotation(Columna.class).nombre();
					columnas = columnas.concat(", " + columna);					
					if (UBean.ejecutarGet(o, columna).getClass().equals(String.class)) {
						valores.append(", '");
						valores.append(UBean.ejecutarGet(o, columna));
						valores.append("'");
					}
					else {
						valores.append(", ");
						valores.append(UBean.ejecutarGet(o, columna));
					}
				}
				if (f.getAnnotation(Id.class) != null) {
					idNombre = f.getName();
				}
			}
			columnas = columnas.replaceFirst(", ", "");
			valores = valores.replace(0, 2, "");
			String query = "INSERT INTO " + tabla + " (" + columnas + ") VALUES (" + valores + ")";
			try {
					synchronized(conexion) {
						conexion.setAutoCommit(false);
						PreparedStatement ps = conexion.prepareStatement(query);
						ps.execute();
						PreparedStatement ps2 = conexion.prepareStatement("SELECT MAX(" + idNombre + ") AS id from " + tabla);
						ResultSet rs = ps2.executeQuery();
						conexion.commit();
						rs.next();
						id = rs.getInt("id");
						UBean.ejecutarSet(o, idNombre, id);
					}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return o;
	}
	
	public static void modificar(Object o){
		Class c = o.getClass();
		if (c.getAnnotation(Tabla.class) != null) {
			Connection conexion = UConexion.getInstancia();
			Tabla t = (Tabla) c.getAnnotation(Tabla.class);
			String tabla = t.nombre();
			String id = "";
			StringBuilder columnasValores = new StringBuilder();
			
			for (Field f : UBean.obtenerAtributos(o)) {
				if (f.getAnnotation(Columna.class) != null) {
					String columna = f.getAnnotation(Columna.class).nombre();
					columnasValores.append(", " + columna + "=");
					if (UBean.ejecutarGet(o, columna).getClass().equals(String.class)) {
						columnasValores.append("'" + UBean.ejecutarGet(o, columna) + "'");
					}
					else { columnasValores.append(UBean.ejecutarGet(o, columna)); }
				}
				if (f.getAnnotation(Id.class) != null) {
					id = f.getName();
				}
			}
			columnasValores = columnasValores.replace(0, 2, "");
			String query = "UPDATE " + tabla + " SET " + columnasValores + " WHERE " + id + "=" + UBean.ejecutarGet(o, id);
			try {
					PreparedStatement ps = conexion.prepareStatement(query);					
					ps.executeUpdate();
					
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void eliminar(Object o){
		Class c = o.getClass();
		if (c.getAnnotation(Tabla.class) != null) {
			Connection conexion = UConexion.getInstancia();
			Tabla t = (Tabla) c.getAnnotation(Tabla.class);
			String tabla = t.nombre();
			StringBuilder columnasValores = new StringBuilder();
			
			for (Field f : UBean.obtenerAtributos(o)) {
				if (f.getAnnotation(Columna.class) != null) {
					String columna = f.getAnnotation(Columna.class).nombre();
					columnasValores.append(" AND " + columna + "=");
					if (UBean.ejecutarGet(o, columna).getClass().equals(String.class)) {
						columnasValores.append("'" + UBean.ejecutarGet(o, columna) + "'");
					}
					else { columnasValores.append(UBean.ejecutarGet(o, columna)); }
				}
				if (f.getAnnotation(Id.class) != null) {
					String id = f.getName();
					columnasValores.append(" AND " + id + "=" + UBean.ejecutarGet(o, id));
				}
			}
			columnasValores = columnasValores.replace(0, 5, "");
			String query = "DELETE FROM " + tabla + " WHERE " + columnasValores;
			try {
					PreparedStatement ps = conexion.prepareStatement(query);					
					ps.execute();
					
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static Object obtenerPorId(Class c, Object id){
		Object obj = null;
		if (c.getAnnotation(Tabla.class) != null) {
			Connection conexion = UConexion.getInstancia();
			Tabla t = (Tabla) c.getAnnotation(Tabla.class);
			String tabla = t.nombre();
			String idNombre = "";
			ArrayList<String> columnas = new ArrayList();
			
			for (Field f : c.getDeclaredFields()) {
				if (f.getAnnotation(Id.class) != null) {
					idNombre = f.getName();
				}
				if (f.getAnnotation(Columna.class) != null) {
					columnas.add(f.getAnnotation(Columna.class).nombre());
				}
			}
			
			for (Constructor cons : c.getDeclaredConstructors()) {
				if (cons.getParameterTypes().length == 0) {
					try {
						obj = cons.newInstance();
						UBean.ejecutarSet(obj, idNombre, id);
						break;
					} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						e.printStackTrace();
					}
				}				
			}
			String query = "SELECT * FROM " + tabla + " WHERE " + idNombre + "=" + id;
			try {
					PreparedStatement ps = conexion.prepareStatement(query);					
					ResultSet rs = ps.executeQuery();
					while (rs.next()) {
						for (String columna : columnas) {
							UBean.ejecutarSet(obj, columna, rs.getObject(columna));
						}
					}					
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return obj;
	}
}
