package servicios;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
			Object id;
			String columnas = "";
			String valores = "";			
			List<Field> atributos = new ArrayList<>();
			
			for (Field f : UBean.obtenerAtributos(o)) {
				if (f.getAnnotation(Columna.class) != null) {
					if (f.getAnnotation(Id.class) != null) {
						idNombre = f.getName();
					}
					else {
						atributos.add(f);
						columnas = columnas.concat(", " + f.getAnnotation(Columna.class).nombre());					
						valores = valores.concat(", ?");
					}
				}
			}
			columnas = columnas.replaceFirst(", ", "");
			valores = valores.replaceFirst(", ", "");
			String query = "INSERT INTO " + tabla + " (" + columnas + ") VALUES (" + valores + ")";
			try {
					synchronized(conexion) {
						conexion.setAutoCommit(false);
						PreparedStatement ps = conexion.prepareStatement(query);
						for (int i = 0; i < atributos.size(); i++) {
							ps.setObject(i+1, UBean.ejecutarGet(o, atributos.get(i).getName()));
						}
						ps.execute();
						PreparedStatement ps2 = conexion.prepareStatement("SELECT MAX(" + idNombre + ") AS id from " + tabla);
						ResultSet rs = ps2.executeQuery();
						conexion.commit();
						rs.next();
						id = rs.getObject("id");
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
			List<Field> atributos = new ArrayList<>();
			
			for (Field f : UBean.obtenerAtributos(o)) {
				if (f.getAnnotation(Columna.class) != null) {
					if (f.getAnnotation(Id.class) != null) {
						id = f.getAnnotation(Columna.class).nombre();
					}
					else {
						atributos.add(f);
						columnasValores.append(", " + f.getAnnotation(Columna.class).nombre() + "=?");
					}
				}
			}
			columnasValores = columnasValores.replace(0, 2, "");
			String query = "UPDATE " + tabla + " SET " + columnasValores + " WHERE " + id + "=" + UBean.ejecutarGet(o, id);
			try {
					PreparedStatement ps = conexion.prepareStatement(query);
					for (int i = 0; i < atributos.size(); i++) {
						ps.setObject(i+1, UBean.ejecutarGet(o, atributos.get(i).getName()));
					}
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
			List<Field> atributos = new ArrayList<>();
			
			for (Field f : UBean.obtenerAtributos(o)) {
				if (f.getAnnotation(Columna.class) != null) {
					atributos.add(f);
					columnasValores.append(" AND " + f.getAnnotation(Columna.class).nombre() + "=?");
				}
			}
			columnasValores = columnasValores.replace(0, 5, "");
			String query = "DELETE FROM " + tabla + " WHERE " + columnasValores;
			try {
					PreparedStatement ps = conexion.prepareStatement(query);
					for (int i = 0; i < atributos.size(); i++) {
						ps.setObject(i+1, UBean.ejecutarGet(o, atributos.get(i).getName()));
					}
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
			ArrayList<String> atributos = new ArrayList<>();
			
			for (Field f : c.getDeclaredFields()) {				
				if (f.getAnnotation(Columna.class) != null) {
					if (f.getAnnotation(Id.class) != null) {
						idNombre = f.getAnnotation(Columna.class).nombre();
					}
					else {
						atributos.add(f.getName());
					}
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
						for (String atr : atributos) {
							UBean.ejecutarSet(obj, atr, rs.getObject(atr));
						}
					}					
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return obj;
	}
}
