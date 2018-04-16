import xpressutn.modelo.Persona;
import xpressutn.utils.XpressUTN;

public class main
{

	public static void main(String[] args)
	{
		XpressUTN xPress = new XpressUTN();
		String query = xPress.findAll(Persona.class);
		System.out.println(query);
		// TODO: 1. conectar la BD para ejecutar la QUERY obtenida
		// TODO: 2. nos va a devolver un array(supongo), hay que recorrer ese array e instanciar la clase correspondiente y setear los atributos
	}

}
