package xpressutn.modelo;

import java.util.Date;

import xpressutn.annotations.Column;
import xpressutn.annotations.Id;
import xpressutn.annotations.Table;

@Table(name="PERSONA")
public class Persona
{
	@Id(strategy=Id.IDENTITY) 
	@Column(name="id_persona")
	private int idPersona;
	
	@Column(name="nombre") 
	private String nombre;
	
	@Column(name="direccion")
	private String direccion;
	
	@Column(name="fecha_alta")
	private Date fechaAlta;
	
	public Persona(){}
	
}
