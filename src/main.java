import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

import xpressutn.modelo.Aplicacion;
import xpressutn.modelo.Persona;
import xpressutn.modelo.Rol;
import xpressutn.modelo.Usuario;
import xpressutn.refactor.XpressUTN;

public class main
{

	public static void main(String[] args) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
//		XpressUTN xPress = new XpressUTN();
//		String query = xPress.findAll(Persona.class);
//		System.out.println(query);
//		
//		// TODO: 1. conectar la BD para ejecutar la QUERY obtenida
//		try
//		{
//			xPress.executePlainQuery(query);
//		}
//		catch(SQLException e)
//		{
//			e.printStackTrace();
//		}
//		
//		try
//		{
//			Persona per = XpressUTN.find(Persona.class,new Integer(1));
//			System.out.println(per.toString());
//			Usuario usu = XpressUTN.find(Usuario.class,new Integer(1));
//			System.out.println(usu.toString());
//		}
//		catch(SQLException e1)
//		{
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		
//		// TODO: 2. nos va a devolver un array(supongo), hay que recorrer ese array e instanciar la clase correspondiente y setear los atributos
			XpressUTN.findAll(Usuario.class);
	}

}
