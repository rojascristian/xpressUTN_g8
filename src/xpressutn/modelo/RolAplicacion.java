package xpressutn.modelo;

import xpressutn.annotations.Column;
import xpressutn.annotations.Id;
import xpressutn.annotations.OneToMany;
import xpressutn.annotations.Table;

@Table(name = "XPRESS.ROL_APLICACION")
public class RolAplicacion
{
	@Id(strategy = Id.IDENTITY)
	@Column(name = "id_rol_aplicacion")
	private int idRolAplicacion;

	@OneToMany(mappedBy = "id_rol")
	private Rol rol;
	
	@OneToMany(mappedBy = "id_aplicacion")
	private Aplicacion aplicacion;

	public int getIdRolAplicacion()
	{
		return idRolAplicacion;
	}

	public void setIdRolAplicacion(int idRolAplicacion)
	{
		this.idRolAplicacion=idRolAplicacion;
	}

	public Rol getRol()
	{
		return rol;
	}

	public void setRol(Rol rol)
	{
		this.rol=rol;
	}

	public Aplicacion getAplicacion()
	{
		return aplicacion;
	}

	public void setAplicacion(Aplicacion aplicacion)
	{
		this.aplicacion=aplicacion;
	}
}
