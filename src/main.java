import java.lang.reflect.InvocationTargetException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import xpressutn.modelo.*;
import xpressutn.refactor.XpressUTN;

public class main
{

	public static void main(String[] args)
	{
		//TODO: mapear el resultset a sus respectivas clases
		//TODO: devolver un mensaje si la query no devuelve registros
//			XpressUTN.findAll(Persona.class);
		//	Usuario u = XpressUTN.find(Usuario.class,3);
		//	u.getRoles();
		Usuario u = new Usuario();
		Rol rol = new Rol();
		UsuarioRol a = new UsuarioRol();
		UsuarioRol b = new UsuarioRol();
		UsuarioRol c = new UsuarioRol();
		a.setRol(rol);
		b.setRol(rol);
		c.setRol(rol);
		a.setUsuario(u);
		b.setUsuario(u);
		c.setUsuario(u);
		
		List<UsuarioRol> roles = Arrays.asList(a,b,c);

		u.setFechaAlta(new Date(1996));
		u.setIdUsuario(5);
		u.setPassword("asd123");
		u.setPersona(new Persona());
		u.setRoles(roles);
		u.setUsername("XxXPu$$y$laYer69XxX");
		
		XpressUTN.insert(u);
		
//		String xql = "SELECT * FROM Usuario x WHERE x.fechaAlta > ?";
//		String xql = "SELECT * FROM Usuario x WHERE x.persona.nombre LIKE ?";
//		XpressUTN.query(Usuario.class,xql,"'%s%'");
	}

}
