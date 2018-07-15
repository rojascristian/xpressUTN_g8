package test;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.junit.Assert;

import iceblock.IBlock;
import iceblock.connection.ConnectionManager;
import models.*;

public class Test {

	private Connection getConnection() throws ClassNotFoundException, SQLException {
		
		// Connect to DB
		if (ConnectionManager.idExists("hsqldb")){
			return ConnectionManager.getConnection();
		} else {
			ConnectionManager.create("org.hsqldb.jdbcDriver","jdbc:hsqldb:hsql://localhost/","sa","","hsqldb");
			ConnectionManager.changeConnection("hsqldb");
			return ConnectionManager.getConnection();
		}
		
	}
	
		@org.junit.Test
	public void testFind() throws IOException,SQLException,InvocationTargetException,NoSuchMethodException,InstantiationException,IllegalAccessException
	{
		Connection con=xpressConnectionFactory.getConnection();

		// verifico el find
		Persona p=xpress.find(con,Persona.class,12);
		Assert.assertEquals(p.getNombre(),"Pablo");

		// ocupacion es LAZY => debe permanecer NULL hasta que haga el get
		Assert.assertNull(p.ocupacion);

		// debe traer el objeto
		Ocupacion o=p.getOcupacion();
		Assert.assertNotNull(o);

		// verifico que lo haya traido bien
		Assert.assertEquals(o.getDescripcion(),"Ingeniero");
		Assert.assertEquals((Integer)p.getOcupacion().getIdOcupacion(),(Integer)4);

		// tipoOcupacion (por default) es EAGER => no debe ser null
		Assert.assertNotNull(o.getTipoOcupacion());
		TipoOcupacion to=o.getTipoOcupacion();

		// verifico que venga bien...
		Assert.assertEquals(to.getDescripcion(),"Profesional");

		// -- Relation --

		// las relaciones son LAZY si o si!
		Assert.assertNull(p.direcciones);

		List<PersonaDireccion> dirs=p.getDirecciones();
		Assert.assertNotNull(dirs);

		// debe tener 2 elementos
		Assert.assertEquals(dirs.size(),2);

		for(PersonaDireccion pd:dirs)
		{
			Persona p1=pd.getPersona();
			Direccion d=pd.getDireccion();

			Assert.assertNotNull(p1);
			Assert.assertNotNull(d);

			Assert.assertEquals(p1.getNombre(),p.getNombre());
		}

	}

	@org.junit.Test
	public void testFindAll() throws IllegalAccessException,InstantiationException,NoSuchMethodException,InvocationTargetException,SQLException,IOException,IllegalArgumentException,
			NoSuchFieldException,ClassNotFoundException
	{
		Connection con=xpressConnectionFactory.getConnection();
		 List<Persona> lst = xpress.findAll(con, Persona.class);
		 for(Persona p : lst) {
		 System.out.println(p);
		}
		Ocupacion o=new Ocupacion();
		o.setIdOcupacion(7);
		o.setDescripcion("Estudiante");

		Persona p=new Persona();
		p.setIdPersona(22);
		p.setNombre("PabloTest");
		p.setOcupacion(o);

		int i=xpress.insert(con,p);
		System.out.println("Se inserto "+i+" registros");
		
		p.setNombre("Julian");
		i=xpress.update(con,p);
		System.out.println("Se actualizo "+i+" registros");

		i=xpress.delete(con,Persona.class,22);
		System.out.println("Se elimino "+i+" registros");
	}
}
