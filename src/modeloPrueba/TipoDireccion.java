package modeloPrueba;

import xpressutn.annotations.*;

@Table(name="tipo_direccion")
public class TipoDireccion
{
	@Id(strategy=Id.IDENTITY)
	@Column(name="id_tipo_direccion")
	private Integer idTipoDireccion;
	
	@Column(name="descripcion")
	private String descripcion;

	public Integer getIdTipoDireccion()
	{
		return idTipoDireccion;
	}

	public void setIdTipoDireccion(Integer idTipoDireccion)
	{
		this.idTipoDireccion=idTipoDireccion;
	}

	public String getDescripcion()
	{
		return descripcion;
	}

	public void setDescripcion(String descripcion)
	{
		this.descripcion=descripcion;
	}

	public boolean equals(Object o)
	{
		TipoDireccion other = (TipoDireccion)o;
		return other.getIdTipoDireccion().equals(getIdTipoDireccion())
			&& other.getDescripcion().equals(getDescripcion());
	}
	
	public String toString()
	{
		return getDescripcion();
	}
}
