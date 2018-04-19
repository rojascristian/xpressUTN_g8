package xpressutn.modelo;

import java.util.Date;
import java.util.List;

import xpressutn.annotations.Column;
import xpressutn.annotations.Id;
import xpressutn.annotations.OneToMany;
import xpressutn.annotations.Table;

@Table(name="XPRESS.PERSONA")
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
	
	@OneToMany(mappedBy = "id_persona")
	private List<Usuario> usuarios;
	
	public Persona(){}

	public int getIdPersona()
	{
		return idPersona;
	}

	public void setIdPersona(int idPersona)
	{
		this.idPersona=idPersona;
	}

	public String getNombre()
	{
		return nombre;
	}

	public void setNombre(String nombre)
	{
		this.nombre=nombre;
	}

	public String getDireccion()
	{
		return direccion;
	}

	public void setDireccion(String direccion)
	{
		this.direccion=direccion;
	}

	public Date getFechaAlta()
	{
		return fechaAlta;
	}

	public void setFechaAlta(Date fechaAlta)
	{
		this.fechaAlta=fechaAlta;
	}

	@Override
	public String toString()
	{
		return "Persona [idPersona="+idPersona+", nombre="+nombre+", direccion="+direccion+", fechaAlta="+fechaAlta+"]";
	}


}
