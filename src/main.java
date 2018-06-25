import java.lang.reflect.InvocationTargetException;

import xpressutn.modelo.*;
import xpressutn.refactor.XpressUTN;

public class main
{

	public static void main(String[] args)
	{
		//TODO: mapear el resultset a sus respectivas clases
		//TODO: devolver un mensaje si la query no devuelve registros
//			XpressUTN.findAll(Persona.class);
			Usuario u = XpressUTN.find(Usuario.class,3);
			u.getRoles();
			
//		String xql = "SELECT * FROM Usuario x WHERE x.fechaAlta > ?";
//		String xql = "SELECT * FROM Usuario x WHERE x.persona.nombre LIKE ?";
//		XpressUTN.query(Usuario.class,xql,"'%s%'");
	}

}
