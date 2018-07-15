package domain;

import java.util.List;

import xpress.ann.*;

@Table(name="direccion")
public class Direccion
{
	@Id(strategy=Id.IDENTITY)
	@Column(name="id_direccion")
	private Integer idDireccion;

	@Column(name="calle")
	private String calle;

	@Column(name="numero")
	private Integer numero;
	
	@OneToMany(mappedBy="id_direccion")
	private List<PersonaDireccion> personas;

	public List<PersonaDireccion> getPersonas()
	{
		return personas;
	}

	public void setPersonas(List<PersonaDireccion> personas)
	{
		this.personas=personas;
	}

	public Integer getIdDireccion()
	{
		return idDireccion;
	}

	public void setIdDireccion(Integer idDireccion)
	{
		this.idDireccion=idDireccion;
	}

	public String getCalle()
	{
		return calle;
	}

	public void setCalle(String calle)
	{
		this.calle=calle;
	}

	public Integer getNumero()
	{
		return numero;
	}

	public void setNumero(Integer numero)
	{
		this.numero=numero;
	}

	@Override
	public String toString()
	{
		return getCalle()+" "+getNumero();
	}

	@Override
	public boolean equals(Object obj)
	{
		Direccion other=(Direccion)obj;
		return other.getIdDireccion().equals(getIdDireccion())
			&& other.getCalle().equals(getCalle())
			&& other.getNumero().equals(getNumero());
	}






}
