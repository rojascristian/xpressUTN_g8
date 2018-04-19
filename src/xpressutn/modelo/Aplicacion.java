package xpressutn.modelo;

import xpressutn.annotations.Column;
import xpressutn.annotations.Id;
import xpressutn.annotations.Table;

@Table(name = "XPRESS.APLICACION")
public class Aplicacion
{
	@Id(strategy = Id.IDENTITY)
	@Column(name = "id_aplicacion")
	private int idAplicacion;
	
	public int getIdAplicacion()
	{
		return idAplicacion;
	}

	public void setIdAplicacion(int idAplicacion)
	{
		this.idAplicacion=idAplicacion;
	}

	public String getDescripcion()
	{
		return descripcion;
	}

	public void setDescripcion(String descripcion)
	{
		this.descripcion=descripcion;
	}

	@Column
	private String descripcion;
}
