package prueba;

import anotaciones.Columna;
import anotaciones.Id;
import anotaciones.Tabla;

@Tabla(nombre="empleados")
public class Empleado {
	@Id private int idEmpleado;
	@Columna(nombre="nombre") private String nombre;
	@Columna(nombre="apellido") private String apellido;
	@Columna(nombre="dni") private int dni;
	@Columna(nombre="sueldo") private Double sueldo;
	public int getIdEmpleado() {
		return idEmpleado;
	}
	public void setIdEmpleado(int idEmpleado) {
		this.idEmpleado = idEmpleado;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getApellido() {
		return apellido;
	}
	public void setApellido(String apellido) {
		this.apellido = apellido;
	}
	public int getDni() {
		return dni;
	}
	public void setDni(int dni) {
		this.dni = dni;
	}
	public Double getSueldo() {
		return sueldo;
	}
	public void setSueldo(Double sueldo) {
		this.sueldo = sueldo;
	}
	@Override
	public String toString() {
		return "Empleado [idEmpleado=" + idEmpleado + ", nombre=" + nombre + ", apellido=" + apellido + ", dni=" + dni
				+ ", sueldo=" + sueldo + "]";
	}
}
