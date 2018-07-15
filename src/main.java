import java.lang.reflect.Method;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import xpressutn.modelo.*;
import xpressutn.refactor.XpressUTN;

public class main
{

	public static void main(String[] args)
	{
		//TODO: mapear el resultset a sus respectivas clases
		//TODO: devolver un mensaje si la query no devuelve registros
//			XpressUTN.findAll(Persona.class);
//			Usuario u = XpressUTN.find(Usuario.class,3);
//			System.out.println(u.getIdUsuario());
			
		Usuario u = new Usuario();
		u.setIdUsuario(1);
		System.out.println(u.getIdUsuario());
		Enhancer enhancer=new Enhancer();
		enhancer.setSuperclass(Usuario.class);
		enhancer.setCallback(new MethodInterceptor() {
		    @Override
		    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy)
		        throws Throwable {
		    	System.out.println("método interceptado: "+proxy.getSignature());
		        return proxy.invokeSuper(obj, args);
		    }
		  });
		u = (Usuario)enhancer.create();
		System.out.println(u.getIdUsuario());
		
		//	u.getRoles();

		

			
			
			//		Usuario u = new Usuario();
//		Rol rol = new Rol();
//		UsuarioRol a = new UsuarioRol();
//		UsuarioRol b = new UsuarioRol();
//		UsuarioRol c = new UsuarioRol();
//		a.setRol(rol);
//		b.setRol(rol);
//		c.setRol(rol);
//		a.setUsuario(u);
//		b.setUsuario(u);
//		c.setUsuario(u);
//		
//		List<UsuarioRol> roles = Arrays.asList(a,b,c);
//
//		u.setFechaAlta(new Date(1996));
//		u.setIdUsuario(5);
//		u.setPassword("asd123");
//		u.setPersona(new Persona());
//		u.setRoles(roles);
//		u.setUsername("XxXPu$$y$laYer69XxX");
//		
//		XpressUTN.insert(u);
		
//		String xql = "SELECT * FROM Usuario x WHERE x.fechaAlta > ?";
//		String xql = "SELECT * FROM Usuario x WHERE x.persona.nombre LIKE ?";
//		XpressUTN.query(Usuario.class,xql,"'%s%'");
	}

}
