package xpressutn.modelo;

import java.util.Date;
import java.util.List;

import xpressutn.annotations.Column;
import xpressutn.annotations.Id;
import xpressutn.annotations.ManyToOne;
import xpressutn.annotations.OneToMany;
import xpressutn.annotations.Table;

@Table(name = "USUARIO")
public class Usuario
{
	@Id(strategy = Id.IDENTITY)
	@Column(name = "id_usuario")
	private int idUsuario;
	
	@Column
	private String username;
	
	@Column
	private String password;
	
	// En el der no se muestra en USUARIO, pero en la definición de las clases del mismo documento sí.
	@Column(name="fecha_alta")
	private Date fechaAlta;
	
	@ManyToOne(columnName = "id_persona", fetchType = ManyToOne.EAGER)
	private Persona persona;
	
	@OneToMany(mappedBy = "usuario")
	private List<UsuarioRol> roles;
	
	public Usuario (){}

	public int getIdUsuario()
	{
		return idUsuario;
	}

	public void setIdUsuario(int idUsuario)
	{
		this.idUsuario=idUsuario;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username=username;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password=password;
	}

	public Persona getPersona()
	{
		return persona;
	}

	public void setPersona(Persona persona)
	{
		this.persona=persona;
	}

	public List<UsuarioRol> getRoles()
	{
		return roles;
	}

	public void setRoles(List<UsuarioRol> roles)
	{
		this.roles=roles;
	}

	@Override
	public String toString()
	{
		return "Usuario [idUsuario="+idUsuario+", username="+username+", password="+password+"]";
	}
	
}
