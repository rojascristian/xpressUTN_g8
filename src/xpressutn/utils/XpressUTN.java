package xpressutn.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;

import xpressutn.annotations.Column;
import xpressutn.annotations.Id;
import xpressutn.annotations.ManyToOne;
import xpressutn.annotations.OneToMany;
import xpressutn.annotations.Table;

public class XpressUTN
{
	public static Connection abrirConexion() throws SQLException{
		Properties prop = new Properties();
	    InputStream input = null;
	    Connection con = null;
		try{
			input = new FileInputStream("db.properties");
	        prop.load(input);
	        
	        String conexion_url = prop.getProperty("jdbc.connection.url").trim();
	        String usuario_conexion = prop.getProperty("jdbc.connection.user").trim();
	        String usuario_password = prop.getProperty("jdbc.connection.password").trim();
	
	        con = DriverManager.getConnection(conexion_url,usuario_conexion,usuario_password);
		}
		catch (IOException ex) {
	        ex.printStackTrace();
	    } finally {
	        if (input != null) {
	            try {
	                input.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	    }
		return con;
	}
	public static ResultSet executeQuery(String query) throws SQLException{
		return abrirConexion().createStatement().executeQuery(query); 
	}	
	
	public static <T> List<T> findAll(Class<T> clase){
//		List<String> nombresColumnas = new ArrayList<String>();
		HashMap<String, String> propiedadesPrimitivas_HM = new HashMap<String, String>();
		String column;
		
		// OBTENGO TODOS LOS ATRIBUTOS (publicos/privados)
		final Field[] variables = clase.getDeclaredFields();
		List<Field> listaAtributosEager = new ArrayList<Field>();
		// RECORRO TODOS LOS ATRIBUTOS
		for (final Field variable : variables) {
			// APARTO LOS ATRIBUTOS CON LA ANNOTATION COLUMN
			if (variable.isAnnotationPresent(Column.class)) {	
				Annotation anotacionObtenida = variable.getAnnotation(Column.class);
				column = (((Column)anotacionObtenida).name().equals("")) ? variable.getName() :((Column)anotacionObtenida).name(); 
				
				try {
					// ALMACENO LOS CAMPOS COLUMN
//					nombresColumnas.add(column);
					propiedadesPrimitivas_HM.put(variable.getName(), "=");
				} catch (IllegalArgumentException | SecurityException e) {
					e.printStackTrace();
				}
			}
			if (variable.isAnnotationPresent(Id.class)) {
				// TODO: hacer lo correspondiente con los atributos @ID
				listaAtributosEager.add(variable);
			}
//			ManyToOne -> default Eager (se puede especificar fetchType=ManyToOne.LAZY por si trae problemas)
//			ManyToOne -> se traducen en un inner join
			if (variable.isAnnotationPresent(ManyToOne.class)) {
				// TODO: hacer lo correspondiente con los atributos @ManyToOne
				
			}
//			OneToMany -> son exclusivamente LAZY
			if (variable.isAnnotationPresent(OneToMany.class)) {
				// TODO: hacer lo correspondiente con los atrib utos @ManyToOne
			}
		}
		
		final Annotation annotationTabla = clase.getAnnotation(Table.class);
		
		String xql = generarQuery(clase, propiedadesPrimitivas_HM);
		List<T> listaResultado = query(clase, xql, null);
//		List<T> listaResultado = queryFindAll(propiedadesPrimitivas_HM, ((Table)annotationTabla).name());
/*
		SETEAR LA LISTA<T> donde cada T tiene seteado los atributos primitivos
		
		//ManyToOne -> default Eager
		if(!listaAtributosEager.isEmpty()){
			for(registro: lista){
				for(atributoEager: atributosEager){
				SELECT * FROM Usuario x WHERE x.idUsuario = ?
					String xql = generarXQL(atributoEager.class, propiedadesFiltro[atributoEager.mappedBy])
					registro.setAtributoEager(xpress.queryForSingleResult(registro.class, xql, registro.PK.value))
				}
			}
		}
*/
		return null;
	}

	
	private static <T> String generarQuery(Class<T> clase, HashMap<String, String> propiedadesPrimitivas_HM) {
		String query_string = "SELECT * FROM "+clase.getName();
		if(propiedadesPrimitivas_HM!=null && !propiedadesPrimitivas_HM.isEmpty()){
			query_string += " WHERE ";
			for (Map.Entry<String, String> propiedadPrimitiva : propiedadesPrimitivas_HM.entrySet()){
				query_string += " "+propiedadPrimitiva.getKey()+" "+propiedadPrimitiva.getValue()+" ? ";
			}
		}
		return query_string;
	}
	
	private static <T> List<T> query(Class<T> clase, String xql, Object args) {
		return null;
	}
	private String queryFindAll(HashMap<String, String> propiedadesPrimitivas_HM, String claseNombre)
	{
		
		return null;
	}

	public static <T> T find(Class<T> dtoClass, Object id) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, SQLException{
		Constructor<T> constructorDtoClass = dtoClass.getConstructor();
		T objetoDtoClass = constructorDtoClass.newInstance();
		
		Field[] atributos = objetoDtoClass.getClass().getDeclaredFields();
		Method[] metodos = dtoClass.getMethods();
		String query = "SELECT ";
		Map<String, String> column = new HashMap<String, String>();
		String pk = "";
		
		Annotation anotacionObtenida;
		for (final Field atributo : atributos) {
			if (atributo.isAnnotationPresent(Column.class)){
				anotacionObtenida = atributo.getAnnotation(Column.class);
				column.put(atributo.getName(),(((Column)anotacionObtenida).name().equals("")) ? atributo.getName() :((Column)anotacionObtenida).name());
				query = query + column.get(atributo.getName()) + ", ";	
				if(atributo.isAnnotationPresent(Id.class)){
					pk = column.get(atributo.getName());
				}
			}
		}
		query = setSelectQuery(query, dtoClass.getAnnotation(Table.class).name(), pk, id);
		
		//System.out.println(query);
		ResultSet rs = executeQuery(query);
		rs.next();
		for(Field atributo : atributos){
			if(atributo.isAnnotationPresent(Column.class)){
				for(Method metodo : metodos){	
					if(metodo.getName().equals("set" + atributo.getName().substring(0,1).toUpperCase() + atributo.getName().substring(1)))
						metodo.invoke(objetoDtoClass,rs.getObject(column.get(atributo.getName())));										
				}
			}
		}
		return objetoDtoClass;
	}
	
	private static String setSelectQuery(String st, String tabla, String pk, Object id){
		return st.substring(0, st.length() - 2) + " FROM " + tabla + " WHERE " + pk + "=" + id.toString() + ";";
	}
	
	public void executePlainQuery(String query) throws SQLException
	{
	    Properties prop = new Properties();
	    InputStream input = null;

	    try {

	        input = new FileInputStream("db.properties");

	        // load a properties file
	        prop.load(input);
	        
	        String conexion_url = prop.getProperty("jdbc.connection.url").trim();
	        String usuario_conexion = prop.getProperty("jdbc.connection.user").trim();
	        String usuario_password = prop.getProperty("jdbc.connection.password").trim();

	        // get the property value and print it out
	        Connection con = DriverManager.getConnection(conexion_url,usuario_conexion,usuario_password);
	        ResultSet rs = con.createStatement().executeQuery(query);
	        while (rs.next()) {
//	            int idPersona = rs.getInt("id_persona");
//	            String nombre = rs.getString("nombre");
//	            String direccion = rs.getString("direccion");
//	            Date fechaAlta = rs.getDate("fecha_alta");
//	            System.out.println(idPersona + "\t" + nombre + "\t" + direccion + "\t" + fechaAlta);
	        	int idUsuario = rs.getInt("id_usuario");
	        	String username = rs.getString("username");
	        	String password = rs.getString("password");
	        	int idPersona = rs.getInt("id_persona");
	        	System.out.println(idUsuario + "\t" + username + "\t" + password + "\t" + idPersona);
	        }

	    } catch (IOException ex) {
	        ex.printStackTrace();
	    } finally {
	        if (input != null) {
	            try {
	                input.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	    }
	}

	public int insert(Object obj) 
	{
		Class<?> clase = obj.getClass();
		Field[] atributos = clase.getDeclaredFields();
		Method[] metodos = clase.getMethods();
		String query = "INSERT INTO" + clase.getAnnotation(Table.class).name() + " ";
		Queue<String> valores = new LinkedList<>();
		try
		{
		for(Field atributo : atributos)
		{
			if(atributo.isAnnotationPresent(Column.class))
			{
				String nombre = atributo.getAnnotation(Column.class).name();
				for(Method getter : metodos)
				{
					//BUSCO LOS GETTERS PARA TRAER LA INFORMACION
					if(getter.getName().equals("get" + atributo.getName().substring(0,1).toUpperCase() + atributo.getName().substring(1)))
					{
						//AGREGO LOS DATOS A UNA COLA PARA PODER PONERLOS EN EL MISMO ORDEN QUE LEO LAS COLUMNAS
						valores.add(getter.invoke(obj).toString());
						query += (nombre.equals("") ? atributo.getName() : nombre) + ", ";
					}
				}
			}
		}
		} 
		catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
		{
			return 0;
		}
		//ELIMINO LA ULTIMA COMA
		query = query.substring(0 , query.length() -2) + " VALUES ";
		while(valores.size()>0)
			query += valores.remove() + ", ";
		query = query.substring(0 , query.length() -2) + ";";
		
		try
		{
			executeQuery(query);
		}
		catch(SQLException e)
		{
			return 0;
		}
		
		return 1;
	}
	
	
	
	public static <T> T queryForSingleRow(Class<T> dtoClass,String xql,Object args){
		return null;
	}
}
