import java.sql.Connection;
import java.util.List;

import org.junit.Assert;

import modeloPrueba.*;
import xpressutn.refactor.XpressUTN;


public class Test
{
	@org.junit.Test
	public void testFind()
	{
//		Connection con = XConnecti0onFactory.getConnection();
		
		// verifico el find
		Persona p = XpressUTN.find(Persona.class,12);
		Assert.assertEquals(p.getNombre(),"Pablo");
		Assert.assertNull(p.ocupacion);

		// ocupacion es LAZY => debe permanecer NULL hasta que haga el get
		Assert.assertEquals((Integer)p.getOcupacion().getIdOcupacion(),(Integer)4);

		// debe traer el objeto
		Ocupacion o = p.getOcupacion();
		Assert.assertNotNull(o);
	
		// verifico que lo haya traido bien
		Assert.assertEquals(o.getDescripcion(),"Ingeniero");
	
		// tipoOcupacion (por default) es EAGER => no debe ser null
		Assert.assertNotNull(o.getTipoOcupacion());
		TipoOcupacion to = o.getTipoOcupacion();
		
		// verifico que venga bien...
		Assert.assertEquals(to.getDescripcion(),"Profesional");
		
		// -- Relation --
		
		// las relaciones son LAZY si o si!
		Assert.assertNull(p.direcciones);
		
		List<PersonaDireccion> dirs = p.getDirecciones();
		Assert.assertNotNull(dirs);
		
		// debe tener 2 elementos
		Assert.assertEquals(dirs.size(),2);
		
		for(PersonaDireccion pd:dirs)
		{
			Persona p1 = pd.getPersona();
			Direccion d = pd.getDireccion();
			
			Assert.assertNotNull(p1);
			Assert.assertNotNull(d);
		
			Assert.assertEquals(p1.getNombre(),p.getNombre());
		}
		
	}
}
