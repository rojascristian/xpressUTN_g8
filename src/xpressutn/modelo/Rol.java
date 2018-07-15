package xpressutn.modelo;

import java.util.List;

import xpressutn.annotations.Column;
import xpressutn.annotations.Id;
import xpressutn.annotations.OneToMany;
import xpressutn.annotations.Table;

@Table(name = "XPRESS.ROL")
public class Rol
{
	@Id(strategy = Id.IDENTITY)
	@Column(name = "id_rol")
	private int idRol;
	
	@Column
	private String descripcion;
	
	@OneToMany(mappedBy = "id_rol")
	private List<UsuarioRol> roles;
	
	public int getIdRol()
	{
		return idRol;
	}

	public void setIdRol(int idRol)
	{
		this.idRol=idRol;
	}

	public String getDescripcion()
	{
		return descripcion;
	}

	public void setDescripcion(String descripcion)
	{
		this.descripcion=descripcion;
	}
		
	public Rol(){}
}
