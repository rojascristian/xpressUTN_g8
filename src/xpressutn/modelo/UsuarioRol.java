package xpressutn.modelo;

import xpressutn.annotations.Column;
import xpressutn.annotations.Id;
import xpressutn.annotations.ManyToOne;
import xpressutn.annotations.Table;
@Table(name = "XPRESS.USUARIO_ROL")
public class UsuarioRol
{
	@Id(strategy = Id.IDENTITY)
	@Column(name = "id_usuario_rol")
	private int idUsuarioRol;
	
	@ManyToOne
	private Usuario usuario;
	
	@ManyToOne
	private Rol rol;
	
	public UsuarioRol(){}
	
	public int getIdUsuarioRol()
	{
		return idUsuarioRol;
	}

	public void setIdUsuarioRol(int idUsuarioRol)
	{
		this.idUsuarioRol=idUsuarioRol;
	}

	public Usuario getUsuario()
	{
		return usuario;
	}

	public void setUsuario(Usuario usuario)
	{
		this.usuario=usuario;
	}

	public Rol getRol()
	{
		return rol;
	}

	public void setRol(Rol rol)
	{
		this.rol=rol;
	}
}
